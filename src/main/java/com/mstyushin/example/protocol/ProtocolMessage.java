package com.mstyushin.example.protocol;

import com.mstyushin.example.rest.ApiMapping;
import com.mstyushin.example.rest.ApiRoute;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Field;

@Slf4j
public class ProtocolMessage {

    private int                       build      = 10;
    private String                    version    = "1.0";
    private String                    geo        = null;
    private String                    clientIP   = null;
    private String                    serverIP   = null;
    private String                    api        = null;
    private String                    endpoint   = null;
    private String                    auth       = null;
    private int                       offset     = 0;
    private int                       limit      = 10;
    private HttpMethod                method     = HttpMethod.GET;
    private Map<String, List<String>> parameters = new HashMap<String, List<String>>();
    private String                    postBody   = null;

    public int getBuild() {
        return build;
    }

    public String getVersion() {
        return version;
    }

    public String getGeo() {
        return geo;
    }

    public String getClientIP() {
        return clientIP;
    }

    public String getServerIP() {
        return serverIP;
    }

    public String getApi() {
        return api;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAuth() {
        return auth;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    private void addParameter(String key, String param) {
        List<String> params = new ArrayList<>();
        params.add(param);
        this.parameters.put(key, params);
    }

    public String getPostBody() {
        return postBody;
    }

    public ProtocolMessage(ChannelHandlerContext ctx, Object msg) {
        HttpRequest req = (HttpRequest) msg;

        String uri = req.uri();
        if (uri.length() <= 0) {
            return;
        }

        log.info(uri);
        this.method = req.method();

        parseEndpoint(uri);
        setIp(ctx, req);
        queryStringHandler(uri);
        requestParametersHandler(req);
        requestBodyHandler(msg);

        if (this.parameters.size() > 0) {
            setFields();
        }
    }

    private void parseEndpoint(String uri) {
        String endpoint = uri.split("\\?")[0];
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length());
        }
        this.endpoint = endpoint;

        Set<Map.Entry<String, ApiMapping>> set = ApiRoute.apiMap.entrySet();

        for (Map.Entry<String, ApiMapping> entry : set) {
            ApiMapping apiMapping = entry.getValue();
            Pattern pattern = Pattern.compile("^" + apiMapping.getRegex() + "$");
            Matcher matcher = pattern.matcher(endpoint);
            if (matcher.find()) {
                this.api = apiMapping.getName();
                if (matcher.groupCount() > 0) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        addParameter(apiMapping.getParameterNames().get(i), matcher.group(i + 1));
                    }
                }
                break;
            }
        }

    }

    private void setIp(ChannelHandlerContext ctx, HttpRequest req) {
        String clientIP = (String) req.headers().get("X-Forwarded-For");
        if (clientIP == null) {
            InetSocketAddress remoteSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIP = remoteSocket.getAddress().getHostAddress();
        }
        this.clientIP = clientIP;

        InetSocketAddress serverSocket = (InetSocketAddress) ctx.channel().localAddress();
        this.serverIP = serverSocket.getAddress().getHostAddress();
    }

    private void setFields() {
        Field[] fields = this.getClass().getDeclaredFields();

        for (int i = 0, length = fields.length; i < length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();

            if (fieldName.equals("logger")
                    || fieldName.equals("method")
                    || fieldName.equals("parameters")
                    || fieldName.equals("postBody")) {
                continue;
            }

            if (!this.parameters.containsKey(fieldName)) {
                continue;
            }

            Class fieldType = field.getType();
            field.setAccessible(true);
            try {
                if (fieldType == int.class) {
                    field.set(this, Integer.parseInt(this.parameters.get(fieldName).get(0)));
                } else {
                    field.set(this, this.parameters.get(fieldName).get(0));
                }
            } catch (NumberFormatException | IllegalAccessException e) {
                log.error("field set error", e);
            }

            this.parameters.remove(fieldName);
        }
    }

    private void queryStringHandler(String uri) {

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        if (queryStringDecoder.parameters().size() > 0) {
            this.parameters.putAll(queryStringDecoder.parameters());
        }
    }

    private void requestParametersHandler(HttpRequest req) {
        if (req.method().equals(HttpMethod.POST)) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
            try {
                List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
                for (InterfaceHttpData data : postList) {
                    List<String> values = new ArrayList<String>();
                    MixedAttribute value = (MixedAttribute) data;
                    value.setCharset(CharsetUtil.UTF_8);
                    values.add(value.getValue());
                    this.parameters.put(data.getName(), values);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void requestBodyHandler(Object msg) {
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            StringBuilder buf = new StringBuilder();
            buf.append(content.toString(CharsetUtil.UTF_8));
            this.postBody = buf.toString();
        }
    }
}

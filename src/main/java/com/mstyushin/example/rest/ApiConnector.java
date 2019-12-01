package com.mstyushin.example.rest;

import com.mstyushin.example.netty.ErrorHandler;
import com.mstyushin.example.protocol.ProtocolMessage;
import com.mstyushin.example.protocol.StatusCode;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ApiConnector {

    public static byte[] transfer(ChannelHandlerContext ctx, Object msg) {
        ProtocolMessage protocolMessage = new ProtocolMessage(ctx, msg);

        if (protocolMessage.getEndpoint() == null) {
            return encode(ErrorHandler.error(StatusCode.API_CAN_NOT_BE_NULL));
        }

        if (protocolMessage.getApi() == null) {
            return encode(ErrorHandler.error(StatusCode.API_NOT_FOUND));
        }

        Object result = invoke(protocolMessage.getApi(), protocolMessage);
        if (result == null) {
            return encode(ErrorHandler.error(StatusCode.UNKNOWN_ERROR));
        }

        return encode(result);
    }

    public static Object invoke(String apiName, ProtocolMessage protocolMessage) {
        Class<?> classname;
        Object   classObject;
        Constructor constructor;
        Method method;
        Object   result = null;

        ApiMapping apiMapping = ApiRoute.apiMap.get(apiName);
        if (apiMapping == null) {
            return ErrorHandler.error(StatusCode.API_NOT_FOUND);
        }

        if (protocolMessage.getBuild() < apiMapping.getBuild()){
            return ErrorHandler.error(StatusCode.VERSION_IS_TOO_LOW);
        }

        if(apiMapping.getHttpMethod() != null && !apiMapping.getHttpMethod().contains(protocolMessage.getMethod().toString().toLowerCase())){
            return ErrorHandler.error(StatusCode.REQUEST_MODE_ERROR);
        }

        try {
            classname = Class.forName("com.mstyushin.example.resource" + "." + apiMapping.getResource());
            constructor = classname.getConstructor(ProtocolMessage.class);
            classObject = constructor.newInstance(protocolMessage);
        } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage());
            return ErrorHandler.error(StatusCode.API_SERVER_ERROR);
        }

        try {
            method = classname.getMethod(protocolMessage.getMethod().toString().toLowerCase());
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage());
            return ErrorHandler.error(StatusCode.API_SERVER_ERROR);
        }

        try {
            result = method.invoke(classObject);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.toString());
        }

        return result;
    }

    public static byte[] encode(Object object) {
        String data = new JSONObject(object).toString();
        data = filter(data);
        return data.getBytes();
    }

    public static String filter(String data){
        return data;
    }


}

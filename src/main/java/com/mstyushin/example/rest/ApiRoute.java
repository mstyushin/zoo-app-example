package com.mstyushin.example.rest;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ApiRoute {

    private static final String routeName = "/routeMap.xml";

    private static final String apiNode       = "api";
    private static final String apiName       = "name";
    private static final String apiHttpMethod = "method";
    private static final String apiResource   = "resource";
    private static final String apiBuild      = "build";

    public static final Map<String, ApiMapping> apiMap = new HashMap<>();

    static {
        init();
    }

    public static void init() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(ApiRoute.class.getResourceAsStream(routeName));

            NodeList apiList = doc.getElementsByTagName(apiNode);
            for (int i = 0, apiLength = apiList.getLength(); i < apiLength; i++) {
                Element element = (Element) apiList.item(i);

                ApiMapping apiMapping = new ApiMapping();

                for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        String name = node.getNodeName();
                        String value = node.getFirstChild().getNodeValue();

                        switch (name) {
                            case apiName:
                                apiMapping.setName(value);
                                break;
                            case apiHttpMethod:
                                apiMapping.addHttpMethod(value);
                                break;
                            case apiResource:
                                apiMapping.setResource(value);
                                break;
                            case apiBuild:
                                try {
                                    apiMapping.setBuild(Integer.parseInt(value));
                                } catch (NumberFormatException e) {
                                    log.error(e.getMessage());
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }

                apiMap.put(apiMapping.getName(), apiMapping);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

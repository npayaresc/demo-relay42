package com.sas.agent;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.Bean;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AgentToTargetRoutes extends RouteBuilder {


    private final static ObjectMapper mapper = new ObjectMapper();

    public static final String UTF_8 = "UTF-8";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCEPT = "Accept";
    //public static final String URL = "https4://api.relay42.com/v1/site-1252/profiles/42/facts?throwExceptionOnFailure=false";
    public static final String URL = "https4://api.relay42.com/v1/site-1252/profiles/42/facts";

    @Override
    public void configure() {
        /*
        restConfiguration()
                //.contextPath("/rest")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "CI360 Capture Event API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                //.port(59191)
                .enableCORS(true);

        rest("/events").description("CI360 Capture Event service")
                .consumes("application/json").produces("application/json")
                .post("/all").description("capturing ci360 json object")
                //.responseMessage().code(200).message("Event successfully uploaded").endResponseMessage()
                .to("direct:route1");

        rest("/say")
                .get("/hello").to("direct:hello")
                .get("/bye").consumes("application/json").to("direct:bye")
                .post("/bye").to("mock:update");

        from("direct:hello")
                .transform().constant("Hello World");
        from("direct:bye")
                .transform().constant("Bye World");
        from("direct:route1").to("mock:update");

        */

        //from("quartz2://myTimer?trigger.repeatInterval=2000&trigger.repeatCount=-1").to("exec:{{exec.curlscript}}").to("file:/{{output.dir}}/?fileName=execDone").log("Body: ${body}").routeId("CALLING RELAY42");
        //from("quartz2://myTimer?trigger.repeatInterval=5000&trigger.repeatCount=-1").to("exec:C:/Development/cygwin64/bin/curl?args= --user nicolas.payares@sas.com:Orion123 -o C:/temp/test/nicotest.txt https://api.relay42.com:443/v1/site-1252/profiles/interactions/stream -G --data-urlencode \"query=interaction.interactionType==\"engagement\"\" ").log("Body: ${body}");
        //from("quartz2://nicotimers/timerForRelay42?cron=0/2+*+*+*+*+?").to("exec:sh?args -c \"{{exec.curlscript}}\"").log("Body: ${body}").to("file:/{{output.dir}}/?fileName=execDone").routeId("CALLING RELAY42");
        //from("quartz2://nicotimers/timerForRelay42?cron=0/2+*+*+*+*+?").to("exec:C:/Development/Cygwin64/bin/bash?args=C:/Users/eurnpa/Dropbox/demo-relay42/exec_curl.sh").log("Body: ${body}").setBody(simple("exec executed")).to("file:/{{output.dir}}/?fileName=execDone").routeId("CALLING RELAY42");
        //from("quartz2://nicotimers/timerForRelay42?cron=0/2+*+*+*+*+?").to("exec:bash?args=/home/eurnpa/exec_curl.sh").log("Body: ${body}").setBody(simple("exec executed")).to("file:/{{output.dir}}/?fileName=execDone").log("Body after execution: ${body}").routeId("CALLING RELAY42");

        //from("file:{{output.dir}}?move=.done&delay=4000&doneFileName=execDone").log("File content: ${body}").to("mock:end").routeId("SENDING TO RTDM");
        from("file:{{output.dir}}?noop=true&idempotentKey=${file:name}-${file:modified}?idempotent=true&fileName=nicotest4.txt&readLock=changed").tracing().log("File content: ${body}").convertBodyTo(String.class).process(new Processor() {
            public void process(Exchange msg) {
                String fileContent = msg.getIn().getBody(String.class);
                log.info("Body Content parsing: " + "START"+  fileContent + "END");
                if (!fileContent.isEmpty()) {

                    //String lines[] = fileContent.split("\\r?\\n");
                    String lines[] = fileContent.split("(\r\n|\r|\n)", -1);
                    if(lines.length != 0 && lines.length >=2) {
                        String lastLine = lines[lines.length-1] + lines[lines.length-2];
                        log.info("Processing last line : " + "START" + lastLine + "END") ;
                        if(!lastLine.isEmpty()) {
                            msg.getIn().setBody(lastLine);
                        }else{
                            msg.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                        }

                    }else{
                        msg.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                    }
                } else {
                    msg.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                    //msg.setException(new InterruptedException("Cancel Exchange"));
                    //msg.getIn().setBody("");
                }


            }
        }).log("JSON File content: ${body}")
                .process(new Processor() {
                    public void process(Exchange msg) {
                        String jsonString = msg.getIn().getBody(String.class);
                        log.info("jsonString: " + jsonString);
                        if(!jsonString.isEmpty()) {
                            Engagement engagement = null;
                            try {
                                engagement = mapper.readValue(jsonString, Engagement.class);
                            } catch (IOException e) {
                                log.info("Invalid message {}", jsonString);
                                log.info("-----------");
                                //e.printStackTrace();
                                //return null;
                            }


                            String trackID = engagement.getTrackId();
                            int categorieId = engagement.getVariables().getCategoryId();

                            ArrayNode arrayNode = mapper.createArrayNode();
                            ObjectNode objectNode1 = mapper.createObjectNode();
                            objectNode1.put("factName", "SAS_108");
                            objectNode1.put("factTtl", "1604800");


                            ObjectNode objectNode2 = mapper.createObjectNode();
                            if (categorieId == 249) {
                                objectNode2.put("CampaignId", "1633");
                            } else if (categorieId == 250) {
                                objectNode2.put("CampaignId", "1640");
                            } else if (categorieId == 250) {
                                objectNode2.put("CampaignId", "1646");
                            }


                            objectNode1.putPOJO("properties", objectNode2);

                            arrayNode.add(objectNode1);

                            msg.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                            msg.getIn().setHeader(HttpHeaders.CONTENT_TYPE, constant(APPLICATION_JSON));
                            msg.getIn().setHeader(HttpHeaders.ACCEPT, constant("*/*"));

                            //msg.getIn().setHeader(HttpHeaders.AUTHORIZATION, simple("Basic " + getBinaryEncodeUsrPass(user, pass) ));
                            msg.getIn().setHeader("authorization", constant("Basic bmljb2xhcy5wYXlhcmVzQHNhcy5jb206T3Jpb24xMjM="));
                            msg.getIn().setHeader("url", URL + trackID + "&forceInsert=true");
                            //msg.getIn().setHeader(Exchange.HTTP_QUERY, "forceInsert=true");
                            msg.getIn().setHeader(Exchange.HTTP_QUERY, "partnerId=" + trackID + "&forceInsert=true");
                            msg.getIn().setHeader("categoryId", categorieId);


                            msg.getIn().setBody(arrayNode.toString());
                        }else{
                            msg.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                            //msg.setException(new InterruptedException("Cancel Exchange"));
                        }



                    }
                })


                .process(new Processor() {
                    public void process(Exchange msg) {
                        OkHttpClient client = new OkHttpClient();
                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = null;
                        log.info("CategorieID:  " + msg.getIn().getHeader("categoryId", Integer.class));

                        if (msg.getIn().getHeader("categoryId", Integer.class) == 249) {

                            body = RequestBody.create(mediaType, "[\r\n  {\r\n    \"factName\": \"SAS_108\",\r\n    \"factTtl\": \"1604800\",\r\n    \"properties\": {\r\n\"CampaignId\":\"1633\"\r\n}\r\n  }\r\n]");

                        } else if (msg.getIn().getHeader("categoryId", Integer.class) == 250) {
                            body = RequestBody.create(mediaType, "[\r\n  {\r\n    \"factName\": \"SAS_108\",\r\n    \"factTtl\": \"1604800\",\r\n    \"properties\": {\r\n\"CampaignId\":\"1640\"\r\n}\r\n  }\r\n]");

                        } else if (msg.getIn().getHeader("categoryId", Integer.class) == 251) {
                            body = RequestBody.create(mediaType, "[\r\n  {\r\n    \"factName\": \"SAS_108\",\r\n    \"factTtl\": \"1604800\",\r\n    \"properties\": {\r\n\"CampaignId\":\"1646\"\r\n}\r\n  }\r\n]");

                        }


                        //RequestBody body = RequestBody.create(mediaType, "[\r\n  {\r\n    \"factName\": \"SAS_108\",\r\n    \"factTtl\": \"1604800\",\r\n    \"properties\": {\r\n\"CampaignId\":\"1640\"\r\n}\r\n  }\r\n]");
                        Request request = new Request.Builder()
                                .url("https://api.relay42.com/v1/site-1252/profiles/42/facts?partnerId=2c19ae19-d138-41b1-89c7-f99f5e965247&forceInsert=true")
                                .post(body)
                                .addHeader("content-type", "application/json")
                                .addHeader("accept", "*/*")
                                .addHeader("authorization", "Basic bmljb2xhcy5wYXlhcmVzQHNhcy5jb206T3Jpb24xMjM=")
                                .addHeader("cache-control", "no-cache")
                                .addHeader("postman-token", "c1c3d20b-a3ea-da7d-fc1a-5ad1bb35910c")
                                .build();

                        try {
                            Response response = client.newCall(request).execute();
                            log.info("RESPONSE FROM RELAY42: " + response.body().source().readUtf8());
                            msg.getIn().setBody(response.message());
                        } catch (IOException e) {
                            log.error("Exception: ", e);
                            e.printStackTrace();
                        }


                    }
                })





                /*.to(URL).process(exchange -> log.info("The response code is: {}", exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE)))*/.log("Response from server: ${body}").routeId("SENDING TO RTDM");

        //from("direct:route1").log("CI Event message: ${body}").transform(method("transformer", "transformJson(${body})")).log("CI Map: ${body}").to("dfESP://192.168.99.100:55555/CaptureDigitalBehavior4/digital_stream/digital_event_src?producerDefaultOpcode=eo_INSERT&bodyType=event").routeId("Converting CI360 Events");

    }


}

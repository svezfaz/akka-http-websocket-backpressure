package eu.svez.backpressuredemo;

import java.net.URI;

public class RemoteDemoClientJavaX {

    public static void main(String[] args) throws Exception {
        final WebSocketClientEndpoint clientEndPoint = new WebSocketClientEndpoint(new URI("ws://localhost:8080/prices"));
        clientEndPoint.addMessageHandler(message -> {
            // PAUSE
            try {
                Thread.sleep(100);
            } catch(Exception e) {
                e.printStackTrace();
            }

            System.out.println(message);
        });


        while(true){}
    }

}
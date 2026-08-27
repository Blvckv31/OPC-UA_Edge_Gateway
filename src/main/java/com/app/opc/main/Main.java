package com.app.opc.main;

import org.eclipse.milo.opcua.stack.core.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.opc.client.Client;
import com.app.opc.registry.ClientRegistry;
import com.app.opc.subscription.Subscriptions;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws Exception {
        Client clientWrapper = new Client();
        clientWrapper.configure();
        clientWrapper.connect();
        
        ClientRegistry.client = clientWrapper.getClient();
        logger.info("Client Registered, Ready to use!");
       
        //Run connectivity tests
        //Test test = new Test();
        //test.run();
        
        //Run Subscription
        Subscriptions subscription = new Subscriptions();
        subscription.run();

        //Terminate Connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received. Disconnecting client...");
            try {
                if (clientWrapper.getClient() != null) {
                	clientWrapper.getClient().disconnect();
                }
         
                Stack.releaseSharedResources();
       
                logger.info("OPC UA resources released cleanly.");
            } catch (Exception e) {
                logger.error("Error during graceful shutdown", e);
            }
        }));

        Thread.currentThread().join(); 
    }
}

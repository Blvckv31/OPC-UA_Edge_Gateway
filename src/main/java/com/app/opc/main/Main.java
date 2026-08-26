package com.app.opc.main;

import java.util.concurrent.TimeUnit;

import org.eclipse.milo.opcua.stack.core.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.opc.client.Client;
import com.app.opc.registry.ClientRegistry;
import com.app.opc.test.Test;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) throws Exception {
        Client wrapper = new Client();

        wrapper.configure();
        wrapper.connect();
        
        ClientRegistry.client = wrapper.getClient();
        logger.info("Client Registered, Ready to use!");
       
        //run basic connectivity tests
        Test test = new Test();
        test.run();
        

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received. Disconnecting client...");
            try {
                if (wrapper.getClient() != null) {
                    wrapper.getClient().disconnect().get(5, TimeUnit.SECONDS);
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

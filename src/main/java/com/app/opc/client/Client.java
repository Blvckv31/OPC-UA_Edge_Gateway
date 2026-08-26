package com.app.opc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;

public class Client {
	
	private final static Logger logger = LoggerFactory.getLogger(Client.class);

    private final String serverUrl = "opc.tcp://E-5CG220342Q:53530/OPCUA/SimulationServer";
    
    private static OpcUaClient client;

    public void configure() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        SelfSignedCertificateBuilder certBuilder = new SelfSignedCertificateBuilder(keyPair)
            .setCommonName("OPC UA Client")
            .setApplicationUri("urn:opc:client");
        X509Certificate certificate = certBuilder.build();

        client = OpcUaClient.create(
            serverUrl,
            endpoints -> endpoints.stream()
                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                .findFirst(),
            configBuilder -> configBuilder
                .setApplicationName(org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText.english("OPC UA Client"))
                .setApplicationUri("urn:opc:client")
                .setKeyPair(keyPair)
                .setCertificate(certificate)
                .setIdentityProvider(new AnonymousProvider()) 
                .build()
        );
    }
    
    public OpcUaClient getClient() {
    	if(client == null) {
    		logger.error("Could not fetch client!");
    	}
		return client;
    }

    public void connect() throws InterruptedException, ExecutionException {
        if (client != null) {
            logger.info("Connecting with Anonymous User Identity...");
            client.connect().get(); 
            
            logger.info("Connected successfully as Anonymous!");
        }
    }
    
    public void disconnect() throws InterruptedException, ExecutionException {
    	if(client != null) {
    		logger.info("Disconnecting Client");
    		client.disconnect().get();
    		
    		logger.info("Client Disconnected!");
    	}
    }
}

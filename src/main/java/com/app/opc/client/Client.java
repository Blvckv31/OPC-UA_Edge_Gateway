package com.app.opc.client;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.DiscoveryClient;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.structured.ApplicationDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
	
	private final static Logger logger = LoggerFactory.getLogger(Client.class);

    private final String serverUrl = "opc.tcp://E-5CG220342Q:53530/OPCUA/SimulationServer";
    
    private static OpcUaClient client;
    
    
    //Find the endpoint to connect to the OPC server
    public EndpointDescription findEndpoint() throws Exception {
        URI uri = new URI(serverUrl);
        String extractedHost = uri.getHost(); 
        
        CompletableFuture<List<ApplicationDescription>> serverResp = DiscoveryClient.findServers(serverUrl);
        List<ApplicationDescription> apps = serverResp.get();
        
        if (apps.isEmpty() || apps.get(0).getDiscoveryUrls() == null || apps.get(0).getDiscoveryUrls().length == 0) {
            throw new RuntimeException("No discovery URLs found for the target server.");
        }
        
        String discoveryUrl = apps.get(0).getDiscoveryUrls()[0];
        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(discoveryUrl).get();
        
        for (EndpointDescription e : endpoints) {
            if (SecurityPolicy.None.getUri().equals(e.getSecurityPolicyUri())) {
                return EndpointUtil.updateUrl(e, extractedHost); 
            }
        }
        throw new RuntimeException("No unsecured (SecurityPolicy.None) endpoint profile found on this server.");
    }

    
    //Server connection configurations
    public void configure() throws Exception {
        OpcUaClientConfigBuilder configBuilder = new OpcUaClientConfigBuilder();
        configBuilder.setApplicationName(org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText.english("OPC UA Client"));
        configBuilder.setApplicationUri("urn:opc:client");
        configBuilder.setEndpoint(findEndpoint());
        configBuilder.setIdentityProvider(new org.eclipse.milo.opcua.sdk.client.identity.AnonymousProvider());
        
        OpcUaClientConfig clientConfig = configBuilder.build();
        
        client = OpcUaClient.create(clientConfig);
    }
    
    
    //Return OPC UA Client
    public OpcUaClient getClient() {
    	if(client == null) {
    		logger.error("Could not fetch client!");
    	}
		return client;
    }

    
    //Initiate a connection to the OPC UA server via configured client
    public void connect() throws InterruptedException, ExecutionException, UaException {
        if (client != null) {
            logger.info("Connecting with Anonymous User Identity...");
            client.connect(); 
            
            logger.info("Connected successfully as Anonymous!");
        }
    }
    
    
    //Disconnect the client from the OPC UA server
    public void disconnect() throws InterruptedException, ExecutionException, UaException {
    	if(client != null) {
    		logger.info("Disconnecting Client");
    		client.disconnect();
    		
    		logger.info("Client Disconnected!");
    	}
    }
}

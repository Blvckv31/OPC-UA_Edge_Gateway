package com.app.opc.subscription;

import static com.app.opc.registry.ClientRegistry.client;

import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaSubscription;

public class Subscriptions {
	
	public void run() {
		OpcUaSubscription subscription = new OpcUaSubscription(client);
		
	}
}
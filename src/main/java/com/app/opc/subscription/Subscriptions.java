package com.app.opc.subscription;

import static com.app.opc.registry.ClientRegistry.client;

import java.util.List;

import org.eclipse.milo.opcua.sdk.client.subscriptions.MonitoredItemSynchronizationException;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaSubscription;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.opc.utility.ClientUtility;

public class Subscriptions {
	
	private static final Logger logger = LoggerFactory.getLogger(Subscriptions.class);
	
	private static final ClientUtility utility = new ClientUtility();
	
	private static final NodeId tempNode = new NodeId(3, "Temperature");
	
	public void run() throws UaException, InterruptedException {
		//client.connect();
		
		OpcUaSubscription subscription = new OpcUaSubscription(client);
		
		//Set a listener to monitor data changes at subscription level
		subscription.setSubscriptionListener(
				new OpcUaSubscription.SubscriptionListener() {
					public void onDataReceived(OpcUaSubscription subscription, List<OpcUaMonitoredItem> items, List<DataValue> values) {
						for(int i = 0; i < items.size(); i++) {
							try {
								logger.info("data received: {} : {}", utility.discover(
														items.get(i).getReadValueId().getNodeId())
														.getBrowseName().getName(), values.get(i).value().getValue());
							} catch (UaException e) {
								e.printStackTrace();
							}
						}
					}
				});
		
		//Create the subscription on server
		subscription.create();
		
		OpcUaMonitoredItem monitoredItem = OpcUaMonitoredItem.newDataItem(tempNode);
		
		//Set a listener to monitor data changes at monitored level
		//monitoredItem.setDataValueListener((item,value) -> logger.info("monitoredItem data received: {} : {}", item.getReadValueId().getNodeId(), value.value()));
		
		subscription.addMonitoredItem(monitoredItem);
		
		//Sync the monitored items with server
		try {
			subscription.synchronizeMonitoredItems();
		} catch(MonitoredItemSynchronizationException e) {
			e.getCreateResults().forEach(result -> logger.warn("failed to create item {}", result.monitoredItem().getReadValueId().getNodeId()));
		}
	}
}
package com.app.opc.subscription;

import static com.app.opc.registry.ClientRegistry.client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.milo.opcua.sdk.client.subscriptions.MonitoredItemSynchronizationException;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.OpcUaSubscription;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.opc.pojo.CNC;
import com.app.opc.utility.ClientUtility;

public class Subscriptions {

	private static final Logger logger = LoggerFactory.getLogger(Subscriptions.class);

	private static final ClientUtility utility = new ClientUtility();

	private static Map<NodeId, String> nmap = new ConcurrentHashMap<>();

	private CNC cnc = new CNC();

	// Variable Nodes
	private static final NodeId tempNode = new NodeId(3, "Temperature");
	private static final NodeId statusNode = new NodeId(3, "Status");
	private static final NodeId spindleNode = new NodeId(3, "SpindleSpeed");
	private static final NodeId partNode = new NodeId(3, "PartCount");
	private static final NodeId cycleNode = new NodeId(3, "CycleTime");

	// Run a subscription on OPC UA server
	public void run() throws UaException, InterruptedException {
		OpcUaSubscription subscription = new OpcUaSubscription(client);

		List<NodeId> nodes = new ArrayList<>(List.of(tempNode, statusNode, spindleNode, partNode, cycleNode));
		this.populateQualifedName(nodes);

		// Set a listener to monitor data changes at subscription level
		subscription.setSubscriptionListener(new OpcUaSubscription.SubscriptionListener() {
			public void onDataReceived(OpcUaSubscription subscription, List<OpcUaMonitoredItem> items,
					List<DataValue> values) {
				for (int i = 0; i < items.size(); i++) {
					NodeId nodeId = items.get(i).getReadValueId().getNodeId();
					Object value = values.get(i).value().getValue();

					if (nodeId.equals(tempNode)) {
						cnc.setTemperature(((Number) value).floatValue());
					} else if (nodeId.equals(statusNode)) {
						cnc.setStatus(((String) value).toString());
					} else if (nodeId.equals(spindleNode)) {
						cnc.setSpindleSpeed(((Number) value).intValue());
					} else if (nodeId.equals(partNode)) {
						cnc.setPartCount(((Number) value).intValue());
					} else if (nodeId.equals(cycleNode)) {
						cnc.setCycleTime(((Number) value).floatValue());
					} else {
						new RuntimeException("Unexpected Node detected!");
					}
					cnc.setId("CNC-01");
					cnc.setTimestamp(LocalDateTime.now());

					logger.info(
							"\nEquip ID\t:\t{}\nTemperature\t:\t{}\nEquip Status\t:\t{}\nSpindle Speed\t:\t{} RPM\nPart Count\t:\t{}\nCycle Time\t:\t{} seconds\nTimestamp\t:\t{}",
							cnc.getId(), cnc.getTemperature(), cnc.getStatus(), cnc.getSpindleSpeed(), cnc.getPartCount(),
							cnc.getCycleTime(), cnc.getTimestamp());
				}
			}
		});

		// Add nodes to be monitored
		List<OpcUaMonitoredItem> monitoredItems = new ArrayList<>();
		for (NodeId node : nodes) {
			OpcUaMonitoredItem monitoredItem = OpcUaMonitoredItem.newDataItem(node);
			monitoredItems.add(monitoredItem);
		}
		subscription.addMonitoredItems(monitoredItems);

		// Create subscription on server
		subscription.create();

		// Sync the monitored items with server
		try {
			subscription.synchronizeMonitoredItems();
		} catch (MonitoredItemSynchronizationException e) {
			e.getCreateResults().forEach(result -> logger.warn("failed to create item {}",
					result.monitoredItem().getReadValueId().getNodeId()));
		}
	}

	public void populateQualifedName(List<NodeId> nodeIds) throws UaException {
		for (NodeId nodeId : nodeIds) {
			nmap.put(nodeId, utility.discover(nodeId).getBrowseName().getName());
		}
	}
}
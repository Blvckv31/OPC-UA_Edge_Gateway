package com.app.opc.browse;

import static com.app.opc.registry.ClientRegistry.client;

import java.util.List;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Browse {
	private final static Logger logger = LoggerFactory.getLogger(Browse.class);
	
	public List<? extends UaNode> browseServer() throws UaException {
		
		NodeId factoryNodeId = new NodeId(3, "factory");
		
		AddressSpace addressSpace = client.getAddressSpace();
		UaNode factoryNode = addressSpace.getNode(factoryNodeId);
		
		logger.info("Browsing Nodes");
		List<? extends UaNode> nodes = addressSpace.browseNodes(factoryNode);
		
		for(UaNode node: nodes) {
			System.out.println(node.getBrowseName());
		}
		return nodes;
	}
}
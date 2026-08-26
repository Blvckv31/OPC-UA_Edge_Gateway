package com.app.opc.utility;

import static com.app.opc.registry.ClientRegistry.client;

import java.util.List;

import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.AddressSpace.BrowseOptions;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientUtility {
	private static final Logger logger = LoggerFactory.getLogger(ClientUtility.class);

	private static final AddressSpace addressSpace = client.getAddressSpace();
	
	private static final BrowseOptions componentBrowseOptions = BrowseOptions.builder()
		    .setReferenceType(Identifiers.HasComponent)
		    .setIncludeSubtypes(true)
		    .build();
	
	//Check if a node exists
	public UaNode discover(NodeId nodeId) throws UaException {
		UaNode node = addressSpace.getNode(nodeId);
		if(node != null) {
			logger.info("{} found in address space!", node.getBrowseName().getName());	
		} else {
			logger.error("could not find node in address space!");
		}
		return node;
	}
	
	//Print the variable components of a node
	public void getProperties(NodeId nodeId) throws UaException {
		UaNode node = discover(nodeId);
		
		List<? extends UaNode> components = node.browseNodes(componentBrowseOptions);
		
		logger.info(node.getBrowseName().getName());
		for(UaNode component: components) {
			if(component.getNodeClass() == NodeClass.Variable) {
				UaVariableNode variableNode = (UaVariableNode)component;
				
				Object value = variableNode.getValue().getValue().getValue();
				String name = variableNode.getBrowseName().getName();
				
				logger.info("{}\t: {}", name, value);
			}
		}
	}

}
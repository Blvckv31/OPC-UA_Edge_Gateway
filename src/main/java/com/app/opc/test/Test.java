package com.app.opc.test;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import com.app.opc.utility.ClientUtility;

public class Test {
	
	private final static ClientUtility utility = new ClientUtility();
	private final static NodeId cncNodeId = new NodeId(3, "cnc1");
	
	public void run() throws UaException {
		
		//Test functionality
		utility.getProperties(cncNodeId);
	}
}
package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.interfaces.ConnectCoordinateAVMI;

public class ConnectCoordinateAVMOutboundPort extends AbstractOutboundPort implements ConnectCoordinateAVMI {

	public ConnectCoordinateAVMOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,ConnectCoordinateAVMI.class, owner);
	}

	@Override
	public void connectAVMwithSubmissioner(String uriDispatcher) throws Exception {
		((ConnectCoordinateAVMI)this.connector).connectAVMwithSubmissioner(uriDispatcher);
	}

	@Override
	public void disconnectAVMFromSubmissioner() throws Exception {
		((ConnectCoordinateAVMI)this.connector).disconnectAVMFromSubmissioner();
		
	}

}

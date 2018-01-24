package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.interfaces.ConnectCoordinateAVMI;

public class ConnectCoordinateAVMOutboundPort extends AbstractOutboundPort implements ConnectCoordinateAVMI {

	public ConnectCoordinateAVMOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,ConnectCoordinateAVMI.class, owner);
	}

	@Override
	public void connectAVMwithSubmissioner(String uriDispatcher) throws Exception {
		((ConnectCoordinateAVMI)this.connector).connectAVMwithSubmissioner(uriDispatcher);
	}

}

package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.interfaces.ConnectCoordinateAVMI;

public class ConnectCoordinateAVMConnector extends AbstractConnector implements ConnectCoordinateAVMI {

	@Override
	public void connectAVMwithSubmissioner(String uriDispatcher) throws Exception {
		((ConnectCoordinateAVMI)this.offering).connectAVMwithSubmissioner(uriDispatcher);
	}

}

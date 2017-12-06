package fr.upmc.datacenter.software.requestdispatcher.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementConnector extends AbstractConnector implements RequestDispatcherManagementI{
	
	public void connectAVM(String avmUri, String avmRequestSubmissionInboundPortURI) throws Exception {
		((RequestDispatcherManagementI) this.offering).connectAVM(avmUri, avmRequestSubmissionInboundPortURI);
	}

	@Override
	public void connectNotificationOutboundPort(String notficationInboundPort) throws Exception {
		((RequestDispatcherManagementI) this.offering).connectNotificationOutboundPort(notficationInboundPort);
	}

}

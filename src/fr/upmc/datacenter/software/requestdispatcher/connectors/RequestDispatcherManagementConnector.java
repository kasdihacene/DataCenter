package fr.upmc.datacenter.software.requestdispatcher.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementConnector extends AbstractConnector implements RequestDispatcherManagementI{
	
	public void connectAVM(String avmUri, String avmRequestSubmissionInboundPortURI,
			String avmRequestNotificationOutboundPortUri) throws Exception {
		((RequestDispatcherManagementI) this.offering).connectAVM(avmUri, avmRequestSubmissionInboundPortURI, avmRequestNotificationOutboundPortUri);
	}

}

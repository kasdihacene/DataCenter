package fr.upmc.datacenter.software.requestdispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementOutboundPort extends AbstractOutboundPort
		implements RequestDispatcherManagementI {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
		assert owner != null;
	}

	public RequestDispatcherManagementOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
		assert uri != null;
		assert owner != null;
	}

	@Override
	public void connectAVM(String avmUri, String avmRequestSubmissionInboundPortURI) throws Exception {
		((RequestDispatcherManagementI) this.connector).connectAVM(avmUri, avmRequestSubmissionInboundPortURI);
	}
	
	@Override
	public void connectNotificationOutboundPort(String notficationInboundPort) throws Exception{
		((RequestDispatcherManagementI) this.connector).connectNotificationOutboundPort(notficationInboundPort);
	}
}

package fr.upmc.datacenter.software.requestdispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementInboundPort extends AbstractInboundPort
		implements RequestDispatcherManagementI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementInboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
		assert owner != null;
	}
	
	public RequestDispatcherManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestDispatcherManagementI.class, owner);
		assert uri != null;
		assert owner != null;
	}

	@Override
	public void connectAVM(String avmUri, String avmRequestSubmissionInboundPortURI,
			String avmRequestNotificationOutboundPortUri) throws Exception {
		RequestDispatcherManagementI rd = (RequestDispatcherManagementI) this.owner;
		this.owner.handleRequestSync(new ComponentI.ComponentService<Void>() {
			@Override
			public Void call() throws Exception {
				rd.connectAVM(avmUri, avmRequestSubmissionInboundPortURI, avmRequestNotificationOutboundPortUri);
				return null;
			}
		});
	}
}

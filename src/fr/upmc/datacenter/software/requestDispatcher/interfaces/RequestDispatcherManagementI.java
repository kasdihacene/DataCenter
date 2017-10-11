package fr.upmc.datacenter.software.requestDispatcher.interfaces;

import fr.upmc.datacenter.software.interfaces.RequestI;

public interface RequestDispatcherManagementI {
	/**
	 * COUNT NUMBER OF REQUESTS
	 */
	public int getNumberRequest();
	
	/**
	 * GET URI OF THE REQUEST
	 */
	public String getRequestURI(RequestI ri);
	
	public void connectAVM(
			String avmUri, 
			String avmRequestSubmissionInboundPortURI, 
			String avmRequestNotificationOutboundPortUri) throws Exception;
}

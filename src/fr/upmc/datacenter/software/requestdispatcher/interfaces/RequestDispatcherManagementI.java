package fr.upmc.datacenter.software.requestdispatcher.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface RequestDispatcherManagementI extends OfferedI, RequiredI {

	public void connectAVM(String avmUri, String avmRequestSubmissionInboundPortURI,
			String avmRequestNotificationOutboundPortUri) throws Exception;

}

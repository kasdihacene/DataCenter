package fr.upmc.datacenter.software.applicationcontainer.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;

public class AdmissionNotificationOutboundPort 
		extends AbstractOutboundPort 
		implements AdmissionNotificationI {

	public AdmissionNotificationOutboundPort(
	 ComponentI owner) throws Exception {
		super(AdmissionNotificationI.class, owner);

	 }
	 
	public AdmissionNotificationOutboundPort
			(String uri, ComponentI owner) throws Exception {
		super(uri,AdmissionNotificationI.class, owner);
		assert uri != null;
	}
	

	@Override
	public void notifyAdmissionNotification(String uri) throws Exception {
			
		((AdmissionNotificationI)this.connector).notifyAdmissionNotification(uri);
	}

}

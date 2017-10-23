package fr.upmc.datacenter.software.applicationcontainer.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;

public class AdmissionNotificationConnector 
		extends AbstractConnector 
		implements AdmissionNotificationI {

	@Override
	public void notifyAdmissionNotification(String uri) throws Exception {
		((AdmissionNotificationI)this.offering).notifyAdmissionNotification(uri);

	}

}

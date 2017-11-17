package fr.upmc.datacenter.software.applicationcontainer.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;

public class AdmissionNotificationConnector 
		extends AbstractConnector 
		implements AdmissionNotificationI {

	@Override
	public void notifyAdmissionNotification(AdmissionI admission) throws Exception {
		((AdmissionNotificationI)this.offering).notifyAdmissionNotification(admission);

	}

}

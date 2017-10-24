package fr.upmc.datacenter.software.admissionController.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;

public class AdmissionRequestConnector 
		extends AbstractConnector 
		implements AdmissionRequestI {

	@Override
	public void askForHost(AdmissionI admission) throws Exception {
		((AdmissionRequestI)this.offering).askForHost(admission);

	}

	@Override
	public void askForHostAndWaitResponse(AdmissionI admission) throws Exception {
		((AdmissionRequestI)this.offering).askForHostAndWaitResponse(admission);

	}

}

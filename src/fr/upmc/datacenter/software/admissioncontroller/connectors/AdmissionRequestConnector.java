package fr.upmc.datacenter.software.admissioncontroller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestI;

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

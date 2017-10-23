package fr.upmc.datacenter.software.admissionController.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;

public class AdmissionRequestConnector 
		extends AbstractConnector 
		implements AdmissionRequestI {

	@Override
	public void askForHost(String uri) throws Exception {
		((AdmissionRequestI)this.offering).askForHost(uri);

	}

	@Override
	public void askForHostAndWaitResponse(String uri) throws Exception {
		((AdmissionRequestI)this.offering).askForHostAndWaitResponse(uri);

	}

}

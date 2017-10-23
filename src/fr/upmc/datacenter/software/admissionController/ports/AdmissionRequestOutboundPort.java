package fr.upmc.datacenter.software.admissionController.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;

public class AdmissionRequestOutboundPort
		extends AbstractOutboundPort
		implements AdmissionRequestI {

	//----------------------------------------------------//
	//------------------CONSTRUCTORS----------------------//
	//----------------------------------------------------//
	
	public AdmissionRequestOutboundPort(ComponentI owner) throws Exception {
		super(AdmissionRequestI.class, owner);
	}
	
	public AdmissionRequestOutboundPort(String uri, ComponentI owner)throws Exception {
		super(uri, AdmissionRequestI.class, owner);
		assert uri != null;
	}
	
	//----------------------------------------------------//
	//-----------------------METHODS----------------------//
	//----------------------------------------------------//
		

	@Override
	public void askForHost(String uri) throws Exception {
	
		((AdmissionRequestI)this.connector).askForHost(uri);
		
	}

	@Override
	public void askForHostAndWaitResponse(String uri) throws Exception {
		((AdmissionRequestI)this.connector).askForHostAndWaitResponse(uri);

	}

}

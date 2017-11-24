package fr.upmc.datacenter.software.admissioncontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestI;

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
	public void askForHost(AdmissionI admission) throws Exception {
	
		((AdmissionRequestI)this.connector).askForHost(admission);
		
	}

	@Override
	public void askForHostAndWaitResponse(AdmissionI admission) throws Exception {
		((AdmissionRequestI)this.connector).askForHostAndWaitResponse(admission);

	}

}

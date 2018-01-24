package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.admissioncontrollercoordination;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.step2.AdmissionController;

public class AdmissionControllerCoordination extends AdmissionController {

	private static Integer AVMCREATOR = 0;
	public AdmissionControllerCoordination(String acURI, AbstractCVM acvm) throws Exception {
		super(acURI, acvm);
		
	}
	
	public void inspectResources(AdmissionI admission) throws Exception {
		System.out.println("REQUEST RECEIVED BY AdmissionControllerCoordination "+admission.getApplicationURI());
		createAVMsAndDeploy(admission);
		
	}
	
	protected void createAVMsAndDeploy(AdmissionI admissionI ) {
		synchronized (AVMCREATOR) {
			if(AVMCREATOR == 0) {
			for (int i = 0; i < 4; i++) {
				System.err.println(" TAKEN BY : "+admissionI.getApplicationURI());
			}
			AVMCREATOR = AVMCREATOR + 1;
		}
		}
	}

}

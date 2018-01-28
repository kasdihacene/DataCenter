package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.admissioncontrollercoordination;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.step2.AdmissionController;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.CoordinationLargeScaleI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleInboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleOutboundPort;

public class AdmissionControllerCoordination 
											extends 		AdmissionController 
											implements 		CoordinationLargeScaleI{

	/** port to receive {@link TransitTokenI} from the network */
	private CoordinationLargeScaleInboundPort coordinationLargeScaleInboundPort;
	
	/** port to send to other autonomic controller a {@link TransitTokenI}*/
	private CoordinationLargeScaleOutboundPort coordinationLargeScaleOutboundPort;
	
	private static Integer AVMCREATOR = 0;
	
	public AdmissionControllerCoordination(String acURI, AbstractCVM acvm) throws Exception {
		super(acURI, acvm);

		/** publish the port to receive tokens */
		this.coordinationLargeScaleInboundPort = new CoordinationLargeScaleInboundPort(acURI+"_COOR_CLSIP", this);
		this.addPort(coordinationLargeScaleInboundPort);
		coordinationLargeScaleInboundPort.publishPort();
		
		
		// 1-
		// Set an offered interface to receive the request of creation of the AVMs 
		// we have to implement an Interface which offers possibilities of AVM creation
	}
	
	public void inspectResources(AdmissionI admission) throws Exception {
		System.out.println("REQUEST RECEIVED BY AdmissionControllerCoordination "+admission.getApplicationURI());
		
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

	@Override
	public void submitChip(TransitTokenI tokenI) throws Exception {
		
	}
	
	// 2-  We have to create an sufficient number of ApplicationVM 
	// and deploy it
	// 3- We have to add every AVM created to the DataProvider
	// 4- be careful about the inbound port of the AVMs we have to make link with the DataProvider

}

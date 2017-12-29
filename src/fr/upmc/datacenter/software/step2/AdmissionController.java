package fr.upmc.datacenter.software.step2;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissioncontroller.ResourceInspector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionRequestInboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationOutboundPort;

public class AdmissionController 	extends ResourceInspector 
									implements AdmissionRequestHandlerI {

	private AbstractCVM acvm;
	private String acURI;
	
	/** OUTBOUND PORT SENDING THE NOTIFICATIONS     */
	protected AdmissionNotificationOutboundPort anop;
	
	/** INBOUND PORT OFFERING THE ADMISSION SERVICE */
	protected AdmissionRequestInboundPort arip;
	
	/** Class to store information about ports URIs */
	protected AdmissionI admission;
	
	/**
	 * 
	 * @param riURI : Resource Inspector URI
	 * @param acvm
	 * @throws Exception
	 */
	public AdmissionController(
			String acURI,AbstractCVM acvm) throws Exception {
		super(acURI);
		this.acURI=acURI;
		this.acvm=acvm;
		
				//CREATE OFFRED AND REQUIRED INTERFACES
				this.addOfferedInterface(AdmissionRequestI.class);
				this.addRequiredInterface(AdmissionNotificationI.class);
				

				//CREATE THE INBOUND AND OUTBOUN PORT
				this.anop = new AdmissionNotificationOutboundPort(acURI+"_ANOP",this);
				this.arip = new AdmissionRequestInboundPort(acURI+"_ACIP",this);
				
				this.addPort(anop);
				this.addPort(arip);
			
				this.anop.publishPort();
				this.arip.publishPort();
	
	}

	@Override
	public void inspectResources(AdmissionI admission) throws Exception {
		System.out.println("REQUEST ARRIVED FROM APPLICATION - "+admission.getApplicationURI());
		inspectResourcesAndNotifiy(admission);

	}

	@Override
	public void inspectResourcesAndNotifiy(AdmissionI admission) throws Exception {
				String availableComputerURI = getAvailableResource();
	}

}

package fr.upmc.datacenter.software.step2;

import java.util.LinkedList;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissioncontroller.ResourceInspector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionRequestInboundPort;
import fr.upmc.datacenter.software.applicationcontainer.connectors.AdmissionNotificationConnector;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherComponent;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step2.requestresourcevm.RequestVM;
import fr.upmc.datacenter.software.step2.requestresourcevm.connector.RequestResourceVMConnector;

/**
 * 
 * 
 * @author Hacene KASDI
 * @version 2017.12.10.HK
 *
 */
public class AdmissionController 	extends ResourceInspector 
									implements AdmissionRequestHandlerI {
	
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
		// Get available resources for 2 AVM
				LinkedList<String> availableComputerURI = getAvailableResource(admission);
		if(availableComputerURI!=null) {
				System.out.println("computer available for : "+admission.getApplicationURI()+" == "+availableComputerURI);
				// Allow hosting Application
				allowHostingApplication(admission, availableComputerURI);
				// Send a response to the ApplicationContainer
				admission.setAllowed(true);
				System.out.println(admission.getAdmissionNotificationInboundPortURI());
				anop.doConnection(
						admission.getAdmissionNotificationInboundPortURI(), 
						AdmissionNotificationConnector.class.getCanonicalName());
				anop.notifyAdmissionNotification(admission);
				anop.doDisconnection();
		}else {
				System.out.println("No available resource for : "+admission.getApplicationURI());	
		}
	}
	/**
	 * Allow reserving resources for the given application and create AVM, RequestDispatcher
	 * @param admissionI
	 * @param computerURI
	 * @throws Exception 
	 */
	protected void allowHostingApplication(AdmissionI admissionI, LinkedList<String> computerURIs) throws Exception {
		
		// Create the RequestDispatcher
		RequestDispatcherComponent RD = createRequestDispatcher(admissionI);
		
		for(String computerURI : computerURIs) {
		// Create an ApplicationVM
		ApplicationVM avm = createApplicationVM(admissionI.getApplicationURI(), computerURI);
		
		// Get the ApplicationVM URI
		RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort.getApplicationInfos(admissionI.getApplicationURI());
		String avmURIrecentlyAdded = dispatcherInfo.getAVMRecentlyAdded().getVmURI();
		
		// Connect the AVM to Request Dispatcher for sending Notifications
		avm.doPortConnection(
							avmURIrecentlyAdded+"_RNOP",
							admissionI.getApplicationURI()+"RD_RNIP", 
							RequestNotificationConnector.class.getCanonicalName());
		
		// Add AVM URI on the list of AVMs of the RequestDispatcher
		requestResourceVMOutboundPort.doConnection(
							admissionI.getApplicationURI()+"RD_RVMIP",
							RequestResourceVMConnector.class.getCanonicalName());
		RequestVM requestVMI = new RequestVM(avmURIrecentlyAdded, admissionI.getApplicationURI());
		requestResourceVMOutboundPort.requestAddVM(requestVMI);
		
		// Update AdmissionI informations
		RequestDispatcherInfo rdInfos= dataProviderOutboundPort.getApplicationInfos(admissionI.getApplicationURI());
		int nbCreated	 = rdInfos.getNbVMCreated();
		System.out.println("Nb VM created for "+admissionI.getApplicationURI()+" : "+nbCreated);
		
		// Connect the current RequestDispatcher with the ApplicationVM (APPx_AMV_x_RSIP)
		    RD.doPortConnection(admissionI.getApplicationURI()+"RD_RSOP", 
		    					avmURIrecentlyAdded+"_RSIP", 
		    					RequestSubmissionConnector.class.getCanonicalName());
		    
		 
		}
		// set the RD URI on the AdmissionI response to send to the ApplicationContainer
		admissionI.setRequestSubmissionInboundPortRD(admissionI.getApplicationURI()+"RD_RSIP");
		
	}
	
}

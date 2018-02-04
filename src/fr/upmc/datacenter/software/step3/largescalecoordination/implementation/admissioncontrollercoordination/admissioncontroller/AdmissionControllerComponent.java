package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.admissioncontrollercoordination.admissioncontroller;

import java.util.LinkedList;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenter.software.admissioncontroller.ResourceInspector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionRequestInboundPort;
import fr.upmc.datacenter.software.applicationcontainer.connectors.AdmissionNotificationConnector;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;
import fr.upmc.datacenter.software.step2.requestresourcevm.RequestVM;
import fr.upmc.datacenter.software.step2.requestresourcevm.connector.RequestResourceVMConnector;
import fr.upmc.datacenter.software.step2.tools.DelployTools;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.Coordinator;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.adapterlargescale.AdapterComponent;

/**
 * 
 * Controller used in step 3 large scale cooperation
 * @see a main component on step 1 of the project {@link AdmissionController }
 * and and step 2 {@link fr.upmc.datacenter.software.step2.AdmissionController}
 * @author Hacene KASDI
 * @version 2017.12.10.HK
 *
 */
public class AdmissionControllerComponent 	extends ResourceInspector 
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
	public AdmissionControllerComponent(
			String acURI,AbstractCVM acvm) throws Exception {
		super(acURI);
		
			//CREATE OFFRED AND REQUIRED INTERFACES
			this.addOfferedInterface(AdmissionRequestI.class);
			this.addRequiredInterface(AdmissionNotificationI.class);
			
			//CREATE THE INBOUND AND OUTBOUN PORT
			this.anop = new AdmissionNotificationOutboundPort	(acURI+"_ANOP",this);
			this.arip = new AdmissionRequestInboundPort			(acURI+"_ACIP",this);
			
			this.addPort(anop);
			this.addPort(arip);
		
			this.anop.publishPort();
			this.arip.publishPort();
	
	}

	@Override
	public void inspectResources(AdmissionI admission) throws Exception {
		System.out.println("Request hosting of - "+admission.getApplicationURI());
		inspectResourcesAndNotifiy(admission);
	}

	@Override
	public void inspectResourcesAndNotifiy(AdmissionI admission) throws Exception {
		// Get available resources for 2 AVM
				LinkedList<String> availableComputerURI = getAvailableResource(admission.getApplicationURI());
		
		if(availableComputerURI!=null) {
				
				System.err.println("Computers available for : "+admission.getApplicationURI()+" == "+availableComputerURI);
				// Allow hosting Application
				allowHostingApplication(admission, availableComputerURI);
				// Send a response to the ApplicationContainer
				admission.setAllowed(true);
				anop.doConnection(
						admission.getAdmissionNotificationInboundPortURI(), 
						AdmissionNotificationConnector.class.getCanonicalName());
				anop.notifyAdmissionNotification(admission);
				anop.doDisconnection();
		}else {
			admission.setAllowed(false);
			anop.doConnection(
			admission.getAdmissionNotificationInboundPortURI(), 
			AdmissionNotificationConnector.class.getCanonicalName());
			anop.notifyAdmissionNotification(admission);
			anop.doDisconnection();	
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
		createRequestDispatcher(admissionI);
		
		for(String computerURI : computerURIs) {

		// Create an ApplicationVM
		ApplicationVMAdaptable avm = createApplicationVM(admissionI.getApplicationURI(), computerURI);
		
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
	
		}
		
		// set the RD URI on the AdmissionI response to send to the ApplicationContainer
		admissionI.setRequestSubmissionInboundPortRD(admissionI.getApplicationURI()+"RD_RSIP");
		
		// Create the Adapter Component and launch it
		AdapterComponent adapterRequestDispatcher = new AdapterComponent(
																			admissionI.getApplicationURI()+"RD",
																			admissionI.getApplicationURI());
		DelployTools.deployComponent(adapterRequestDispatcher);

		// connect to DataProvider to get available resources
		adapterRequestDispatcher.connectWithDataProvider(providerURI);
		adapterRequestDispatcher.connectAdapterWithProvider(providerURI);
		
		// Create a SensorDispatcherOutboundPort and launch pushing
		adapterRequestDispatcher.connectWithRequestDispatcher(admissionI.getApplicationURI());
		adapterRequestDispatcher.launchAdaptionEveryInterval();
				
	}
	
	/**
	 * Method which launch cooperation with other controllers
	 * @param admissionI
	 * @return serializable information after setting some ports 
	 * @throws Exception
	 */
	protected AdmissionI launchCooperation(AdmissionI admissionI) throws Exception {
		
		// set the RD URI on the AdmissionI response to send to the ApplicationContainer
		admissionI.setRequestSubmissionInboundPortRD(admissionI.getApplicationURI()+"RD_RSIP");
		
		// Create the Adapter Component and launch it
		Coordinator coordinator = new Coordinator(
												admissionI.getApplicationURI()+"RD",
												admissionI.getApplicationURI());
		DelployTools.deployComponent(coordinator);

		// subscribe to Ring network to exchange tokens ( list of AVM URIs )
		dataProviderOutboundPort.subscribeToRingNetwork(admissionI.getApplicationURI());
		
		// connect to DataProvider to get available resources
		coordinator.connectWithDataProvider(providerURI);
		coordinator.connectAdapterWithProvider(providerURI);
		// Create a SensorDispatcherOutboundPort and launch pushing
		coordinator.connectWithRequestDispatcher(admissionI.getApplicationURI());
		coordinator.launchAdaptionEveryInterval();
		
		// try to send token by checking first if I'm leader of the topology
		// means the first to initialize the cooperation
		coordinator.tryToSubmitToken();
		
		return admissionI;
	}

	@Override
	public void shutdownServices(String URI) throws Exception {
		System.out.println();
		System.err.println("========================STOPPING APPLICATION : "+URI);
		System.out.println();
		
	}
}

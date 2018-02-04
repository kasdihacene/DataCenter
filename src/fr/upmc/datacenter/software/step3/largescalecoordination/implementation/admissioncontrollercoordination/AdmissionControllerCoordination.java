package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.admissioncontrollercoordination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.applicationcontainer.connectors.AdmissionNotificationConnector;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo.State_change;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherComponent;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step2.AdmissionController;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;
import fr.upmc.datacenter.software.step2.adaptableproperty.ComputerAdaptable;
import fr.upmc.datacenter.software.step2.adaptableproperty.connector.AdapterComputerConnector;
import fr.upmc.datacenter.software.step2.adaptableproperty.connector.AdapterVMConnector;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterComputerI;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterVMI;
import fr.upmc.datacenter.software.step2.adaptableproperty.ports.AdapterComputerOutboundPort;
import fr.upmc.datacenter.software.step2.adaptableproperty.ports.AdapterVMOutboundPort;
import fr.upmc.datacenter.software.step2.requestresourcevm.RequestVM;
import fr.upmc.datacenter.software.step2.requestresourcevm.connector.RequestResourceVMConnector;
import fr.upmc.datacenter.software.step2.tools.DelployTools;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.Coordinator;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.TransitToken;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.ApplicationVMcoordinate;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.connectors.ConnectCoordinateAVMConnector;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.connectors.CoordinationLargeScaleConnector;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.CoordinationLargeScaleI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleInboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleOutboundPort;

/**
 * <h2>Descriptor class</h2>
 * 
 * This class represents the controller of the topology and the arbiter of the DataCenter
 * in the large scale, this class supervise the passage of {@link ApplicationVMcoordinate} 
 * URIs on the network and decide when should release <code>ApplicationVMcoordinate</code>
 * 
 * This component create at the beginning using all available resources <code>ComputerAdaptable</code>
 * a set of <code>ApplicationVMcoordinate</code> and deploy it, and push their URIs on the network in 
 * order to be used by the Controller and to do cooperation between Controllers <code>{@link Coordinator}</code>
 * 
 * @author Hacene KASDI
 *
 */
public class AdmissionControllerCoordination 
											extends 		AdmissionController 
											implements 		CoordinationLargeScaleI{


	/** port to receive {@link TransitTokenI} from the network */
	private 			CoordinationLargeScaleInboundPort coordinationLargeScaleInboundPort;
	
	/** port to send to other autonomic controller a {@link TransitTokenI}*/
	private 			CoordinationLargeScaleOutboundPort coordinationLargeScaleOutboundPort;
	
	/** Port used to ask {@link ApplicationVMAdaptable} for releasing AVM cores  */
	protected 			AdapterVMOutboundPort 				avmiop;
	
	/** Port used to ask {@link ComputerAdaptable for releasing cores in this context (Large scale)} */
	protected 			AdapterComputerOutboundPort 		acop;

	/** subscribe the controller to the network topology */
	private static 		Integer timeToSubscribe = 0;
	
	private static 		Integer AVMCORES = 2;
	private 			String acURI;
	private 			HashMap<String, Long> mapAVMfrequencyPassage;
	private 			LinkedList<ApplicationVMInfo> appVMtoDeleteDefinitely;
	private 			ArrayList<ApplicationVMInfo> listAPPvmNetRefreched;
	
	
	public AdmissionControllerCoordination(String acURI, AbstractCVM acvm) throws Exception {
		
		super(acURI, acvm);
		this.acURI=acURI;
		mapAVMfrequencyPassage		=new HashMap<>();
		appVMtoDeleteDefinitely		=new LinkedList<>();
		listAPPvmNetRefreched		=new ArrayList<>();
		
		
		/** interfaces used to exchange information through the topology */ 
		this.addOfferedInterface(CoordinationLargeScaleI.class);
		this.addRequiredInterface(CoordinationLargeScaleI.class);
		
		/** publish the port to receive tokens */
		this.coordinationLargeScaleInboundPort = new CoordinationLargeScaleInboundPort("AC_"+"COOR_CLSIP", this);
		this.addPort(coordinationLargeScaleInboundPort);
		coordinationLargeScaleInboundPort.publishPort();
		
		/** publish the port to send token in network */
		this.coordinationLargeScaleOutboundPort = new CoordinationLargeScaleOutboundPort("AC_"+"COOR_CLSOP", this);
		this.addPort(coordinationLargeScaleOutboundPort);
		coordinationLargeScaleOutboundPort.publishPort();
		
		//=====================================================
		//		Interfaces used for Adaption
		//=====================================================
		
		this.addRequiredInterface(AdapterComputerI.class);
		this.addRequiredInterface(AdapterVMI.class);
		
		this.acop 		= new AdapterComputerOutboundPort(riURI+"ADAPTER_ACOP", this);
		this.avmiop		= new AdapterVMOutboundPort(riURI+"ADAPTER_AVMIOP", this);
		
		this.addPort(acop);
		this.addPort(avmiop);
		
		this.acop.publishPort();
		this.avmiop.publishPort();
		
	}
	
	public void inspectResources(AdmissionI admission) throws Exception {
		synchronized (timeToSubscribe) {
			if(timeToSubscribe == 0) {
				timeToSubscribe = 1;
				// at the beginning we have to add our AdmissionController 
				// to the network topology
				dataProviderOutboundPort.subscribeToRingNetwork("AC_");
			}
		}
		inspectResourcesAndNotifiy(admission);
	}
	
	/**
	 * @see {@link AdmissionRequestHandlerI#inspectResourcesAndNotifiy(AdmissionI)}
	 */
	public void inspectResourcesAndNotifiy(AdmissionI admission) throws Exception {
		// GET 2 ApplicationVMcoordinate for the current ApplicationContainer
			
		ArrayList<ApplicationVMInfo> computers = getMapAVMs();
		if( computers.size() >= 2) {
			
			hostApplicationAndLAunchCoordination(computers, admission);
			AdmissionI admissionI = launchCooperation(admission);
			
			// Send a response to the ApplicationContainer
				admission.setAllowed(true);
				anop.doConnection(
						admission.getAdmissionNotificationInboundPortURI(), 
						AdmissionNotificationConnector.class.getCanonicalName());
				anop.notifyAdmissionNotification(admissionI);
				anop.doDisconnection();
		}else {
			
			// The application who got the resources 
			// should set back all ApplicationsVMInfos extracted
			
			admission.setAllowed(false);
			anop.doConnection(
			admission.getAdmissionNotificationInboundPortURI(), 
			AdmissionNotificationConnector.class.getCanonicalName());
			anop.notifyAdmissionNotification(admission);
			anop.doDisconnection();	
		}
		}
	
	/**
	 * In our case if there are resources we get 2 ApplicationVM or less
	 * than we check in {@link AdmissionControllerCoordination#inspectResourcesAndNotifiy(AdmissionI)}
	 * if the list is greater than 2 else we refuse the Hosting for this ( ApplicationContainer )
	 * 
	 * @return LIST of informations of available ApplicationVMcoordiante 
	 * @throws Exception
	 */
	private ArrayList<ApplicationVMInfo> getMapAVMs() throws Exception{
		ArrayList<ApplicationVMInfo> computerURIs =new ArrayList<ApplicationVMInfo>();

	for (int i = 0; i < 2; i++) {
			ApplicationVMInfo applicationVMInfo = dataProviderOutboundPort.removeApplicationVM();
		if(applicationVMInfo == null ) {return computerURIs;}else {
				computerURIs.add(applicationVMInfo);
			}	
		}	
			return computerURIs;
	}
	/**
	 * This method allow the creation of ApplicationVMcoordinate
	 * @throws Exception
	 */
	public void createAVMsAndDeploy() throws Exception {
		
		// Get all resources and create ApplicationVM for coordination
		LinkedList<String> computerURIs = askForAvailableComputers();
		for (String uri : computerURIs) {
			createAVMcoordinate(uri);
		}
	}
	
	/**
	 * 
	 * @return list of available computers
	 * @throws Exception
	 */
	private LinkedList<String> askForAvailableComputers() throws Exception{
		// Get all computer URIs
		LinkedList<String> computerListURI = new LinkedList<String>();
		computerListURI = dataProviderOutboundPort.getComputerListURIs();
		
		// List of computer to return to the Admission controller to create AVMs
		LinkedList<String> computerURIsTOreturn = new LinkedList<>();
		for (String uri : computerListURI) {
			ComputerInfo computerInfo=dataProviderOutboundPort.getComputerInfos(uri);
			int nbAvailableCores=0;
			Integer sharedResource = computerInfo.getSharedResource();
			boolean[][] allocatedCores;
			// look at the synchronization barrier if there is an available 
			// information about this computer, else wait()
			synchronized (sharedResource) {
				if(sharedResource==0) {
					try {
					System.err.println("APPLICATION WAITING FOR : "+uri);
					sharedResource.wait();
					} catch (InterruptedException e) {
						System.err.println("ERROR : WHEN WAITING FOR RESOURCE");
						e.printStackTrace();
					}
				}
			}
			
			synchronized (computerInfo) {
				// get number of available core of this computer
				allocatedCores=computerInfo.getCoreState();
				for (int i = 0; i < allocatedCores.length; i++) {
					for (int j = 0; j < allocatedCores[i].length; j++) {
						if(!allocatedCores[i][j]) nbAvailableCores++;
					}
				}
				// check if nbCoresAvailable >= NBCORES than set these cores as allocated
				if(nbAvailableCores >= AVMCORES) {
					if( nbAvailableCores % AVMCORES == 0) {
						int nbAVM =  (int)nbAvailableCores/AVMCORES;
						for (int i = 0; i < nbAVM; i++) {
							computerInfo.updateCoresState(allocatedCores, AVMCORES,State_change.ADD);
							computerURIsTOreturn.add(computerInfo.getComputerURI());
						}
					}
				}
			}
		}
		
		return computerURIsTOreturn;
	}
	
	/**
	 * Allocating resources for the {@link ApplicationVMcoordinate}
	 * @param computerURI
	 * @return ApplicationVM for coordination
	 * @throws Exception
	 */
	public ApplicationVMcoordinate createAVMcoordinate(String computerURI) throws Exception {
		String avmURI=null;
		// Allocate cores for the ApplicationVM
		this.computerServicesOutboundPort.doConnection(computerURI+"_CSIP",
						 ComputerServicesConnector.class.getCanonicalName());
		AllocatedCore[] cores = computerServicesOutboundPort.allocateCores(AVMCORES);
		this.computerServicesOutboundPort.doDisconnection();

		// create an URI for the ApplicationVMcoordinate
		int indexAVM = dataProviderOutboundPort.getNBAVMcreated(); 
		avmURI = "AVM_"+indexAVM;
		
		// Create and deploy the AppplicationVM
		String RSIP_URI=avmURI+"_RSIP";
		String RNOP_URI=avmURI+"_RNOP";
		ApplicationVMcoordinate aVMcoordinate = new ApplicationVMcoordinate(avmURI, 
																			avmURI+"_AVMMIP", 
																			RSIP_URI, 
																			RNOP_URI);
		// Create an ApplicationVMMangement
		ApplicationVMManagementOutboundPort avmMop= new ApplicationVMManagementOutboundPort(riURI+"_AVMMOP", new AbstractComponent(1,1){});
		avmMop.publishPort();
		avmMop.doConnection(avmURI+"_AVMMIP", ApplicationVMManagementConnector.class.getCanonicalName());

		// allocate cores for the ApplicationVM
		avmMop.allocateCores(cores);

		// store information related to this ApplicationVMcoordinate in DataProvider
		ApplicationVMInfo applicationVMInfo = new ApplicationVMInfo(avmURI, computerURI);
		applicationVMInfo.addManyCores(cores);
		
		// add the AVM to map in order to increment frequency passage on the network
		mapAVMfrequencyPassage.put(applicationVMInfo.getVmURI(), 0L);
		
		dataProviderOutboundPort.addApplicationVM(applicationVMInfo);
		System.err.println(applicationVMInfo.printCores());
		
		// Deploying the component
		DelployTools.deployComponent(aVMcoordinate);
		return aVMcoordinate;
	}

	/**
	 * @see {@link CoordinationLargeScaleI#submitChip(TransitTokenI)}
	 */
	@Override
	public void submitChip(TransitTokenI tokenI) throws Exception {
		synchronized (mapAVMfrequencyPassage) {

			checkPassageFrequency(tokenI.getListURIs());
			if(!appVMtoDeleteDefinitely.isEmpty()) {
				deleteAVMAfterAlotPassage();
			}
				listAPPvmNetRefreched=tokenI.getListURIs();
					
				String nextNode = dataProviderOutboundPort.getNextNode();
					// Ignore the case where the next node is Us
					if(acURI.equals(nextNode))return;
					// we have to connect this outbound port with the next inbound port component 
					TransitToken token = new TransitToken(acURI, nextNode, listAPPvmNetRefreched);
					coordinationLargeScaleOutboundPort		.doConnection(
																nextNode+"COOR_CLSIP", 
																CoordinationLargeScaleConnector.class.getCanonicalName());
					this.coordinationLargeScaleOutboundPort	.submitChip(token);
					this.coordinationLargeScaleOutboundPort	.doDisconnection();	
		}
	}
	/**
	 * Check the frequency of passage of the AVM URIs
	 * and the controller will decide if necessary to add or deallocate
	 * {@link ApplicationVMcoordinate}
	 *  
	 * As a choice we deallocate 50% of resources not used.
	 * 
	 * @param applicationVMInfos
	 * @throws Exception 
	 */
	public void checkPassageFrequency(ArrayList<ApplicationVMInfo> applicationVMInfos) throws Exception {
		
		HashMap<String, Long> mapPassageRefreshed=new HashMap<>();
		int switchOffHalfResources=0;
		for (ApplicationVMInfo appVM : applicationVMInfos) {
			if(mapAVMfrequencyPassage.containsKey(appVM.getVmURI())) {
				
				Long incr = mapAVMfrequencyPassage.get(appVM.getVmURI());
				Long newVar = incr+1;
				mapPassageRefreshed.put(appVM.getVmURI(), newVar);
				if(mapPassageRefreshed.get(appVM.getVmURI()) ==  6000000) {
					
					System.err.println("====================================== INACTIVITY DETECTED [AVM EXPIRED] : "+appVM.getVmURI());
					if(switchOffHalfResources%2==0) {
					appVMtoDeleteDefinitely.add(appVM);
					}
					switchOffHalfResources++;
					mapPassageRefreshed.put(appVM.getVmURI(), 0L);
					
					}
			}
		}
		mapAVMfrequencyPassage=mapPassageRefreshed;
	}
	/**
	 * Release AVM from the DataCenter and switch off the resources.
	 * @param avmURI
	 * @throws Exception
	 */
	protected void releaseAVMresources(String avmURI) throws Exception {
		ApplicationVMInfo applicationVMInfo = dataProviderOutboundPort.getApplicationVMCoordinate(avmURI);
		
		System.err.println("-------------- RELEASE AVM FROM DATACENTER ---------");
		System.err.println("AVM TO RELEASE		===> "+applicationVMInfo.getVmURI());
		System.err.println("COMPUTER TO RELEASE	===> "+applicationVMInfo.getComputerURI());
		
		
		ComputerInfo computerInfo = dataProviderOutboundPort.getComputerInfos(applicationVMInfo.getComputerURI());
		
		// resources allocated by the ApplicationVM
		LinkedList<AllocatedCore> allocatedCores = applicationVMInfo.getAllocatedCores();
		int nbCoresUsed = allocatedCores.size();
		synchronized (computerInfo) {
			computerInfo.updateCoresState(computerInfo.getCoreState(), nbCoresUsed, State_change.REMOVE);
		}
		
		for (int i = 0; i < nbCoresUsed; i++) {
			
			avmiop.doConnection(avmURI+"_AVMIP",AdapterVMConnector.class.getCanonicalName());
						
			// get the last core of the ApplicationVM
			AllocatedCore core=applicationVMInfo.getLastCore();
			
			// Update ApllicationVm Informations
			applicationVMInfo.removeCore(core);
			
			// remove concretely the AllocatedCore in the <code>ApplicationVM</code> Component
			avmiop.releaseCore(core);
			avmiop.doDisconnection();
		
			//  remove Processor Core concretely on <code>Processor</code>
			acop.doConnection(applicationVMInfo.getComputerURI()+"_ACIP",AdapterComputerConnector.class.getCanonicalName());
			acop.releaseCore(core);
			acop.doDisconnection();
		}
		System.err.println("----------------------------------------------------");
		}	

	/**
	 * Release {@link ApplicationVMcoordinate} after inactivity on the network
	 * controller will switch off the resources related to this AVM.
	 * 
	 * @throws Exception
	 */
	private void deleteAVMAfterAlotPassage() throws Exception {
		appVMtoDeleteDefinitely.forEach(element -> {
			try {
				listAPPvmNetRefreched.remove(element);
				releaseAVMresources(element.getVmURI());
				mapAVMfrequencyPassage.remove(element.getVmURI());
				dataProviderOutboundPort.DeleteDefinitelyAVM(element);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		appVMtoDeleteDefinitely.clear();
	}
	/**
	 * Ask for hosting the application and start coordination between controllers
	 * @param 	applicationVMInfos
	 * @param 	admissionI
	 * @throws 	Exception
	 */
	protected void hostApplicationAndLAunchCoordination(ArrayList<ApplicationVMInfo> applicationVMInfos, AdmissionI admissionI) throws Exception {
		RequestDispatcherComponent requestDispatcherComponent = createRequestDispatcher(admissionI);
		
		// Ask for connecting the RequestDispatcher with the AVM for receiving Notifications
		for (ApplicationVMInfo applicationVMInfo : applicationVMInfos) {
			connectCoordinateAVMOutboundPort.doConnection(
					applicationVMInfo.getVmURI()+"_CCIP", 
					ConnectCoordinateAVMConnector.class.getCanonicalName());
			connectCoordinateAVMOutboundPort.connectAVMwithSubmissioner(requestDispatcherComponent.getApplicationContainerURI());
		
			// Add AVM URI on the list of AVMs of the RequestDispatcher
			requestResourceVMOutboundPort.doConnection(
								admissionI.getApplicationURI()+"RD_RVMIP",
								RequestResourceVMConnector.class.getCanonicalName());
			RequestVM requestVMI = new RequestVM(applicationVMInfo.getVmURI(), admissionI.getApplicationURI());
			requestResourceVMOutboundPort.requestAddVM(requestVMI);
			requestResourceVMOutboundPort.doDisconnection();
		
			// Store informations about the RequestDispatcher
			RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort.getApplicationInfos(admissionI.getApplicationURI());
			// Add the ApplicationVM information to the RequestDispatcherInformation
			synchronized (dispatcherInfo) {
				
				dispatcherInfo.addApplicationVM(applicationVMInfo.getVmURI(), applicationVMInfo.getComputerURI(), applicationVMInfo.getAllCoresCoordiantion());
			}
			System.err.println("#################################### AVM created == "+dispatcherInfo.getNbVMCreated());

			
		}
		
		
	}
}
package fr.upmc.datacenter.software.admissioncontroller;

import java.util.LinkedList;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreator;
import fr.upmc.datacenter.dataprovider.connectors.DataProviderConnector;
import fr.upmc.datacenter.dataprovider.connectors.DataProviderDispatcherConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderDispatcherI;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.dataprovider.ports.DataDispatcherOutboundPort;
import fr.upmc.datacenter.dataprovider.ports.DataProviderOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo.State_change;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherComponent;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.ports.RequestResourceVMOutboundPort;
import fr.upmc.datacenter.software.step2.tools.DelployTools;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.interfaces.ConnectCoordinateAVMI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.ports.ConnectCoordinateAVMOutboundPort;

/**
 * <p><strong>Description</string></p>
 * 
 * this component <code>ResourceInspector</code> interacts with the 
 * <code>DataProvider</code> to request availability of resources in 
 * the DataCenter, he can inspects the abstract values of performances stored 
 * 
 * @author Hacene KASDI
 * @version 2017.12.10.HK
 *
 */
public class ResourceInspector extends AbstractComponent {

	/** port used to connect AVM with RequestDispatcherComponent for Notifications */
	protected ConnectCoordinateAVMOutboundPort connectCoordinateAVMOutboundPort;
	
	protected String riURI;
	protected String providerURI;
	protected static final int NBCORES = 2;
	
	public DynamicComponentCreator dynamicComponentCreator;
	
	protected DataProviderOutboundPort 			dataProviderOutboundPort;
	protected ComputerServicesOutboundPort 		computerServicesOutboundPort;
	protected DataDispatcherOutboundPort 		dataDispatcherOutboundPort;
	protected RequestResourceVMOutboundPort		requestResourceVMOutboundPort;
	protected ApplicationVMManagementOutboundPort avmMop;
	
	public ResourceInspector(String riURI) throws Exception {
		super(1, 1);
		this.riURI=riURI;
		//Used to ask for informations about Computer, processor and Core
		this.addRequiredInterface(DataProviderI.class);
		//Used to ask for informations about Computers of the DataCenter
		this.addRequiredInterface(ComputerServicesI.class);
		//Used to ask for informations about ApplicationContainer and RequestDispatcher
		this.addRequiredInterface(DataProviderDispatcherI.class);
		//Used to ask RequestDispatcher for adding or removing AVM
		this.addRequiredInterface(RequestResourceVMI.class);
		//Used to allocate cores
		this.addRequiredInterface(ApplicationVMManagementI.class);
		
		// CREATE AND PUBLISH PORT
		this.dataProviderOutboundPort 		= new DataProviderOutboundPort			(riURI+"_DPOP", this);
		this.computerServicesOutboundPort	= new ComputerServicesOutboundPort		(riURI+"_CSOP", this);
		this.dataDispatcherOutboundPort		= new DataDispatcherOutboundPort		(riURI+"_DDOP", this);
		this.requestResourceVMOutboundPort	= new RequestResourceVMOutboundPort		(riURI+"_RVMOP", this);
		this.avmMop 						= new ApplicationVMManagementOutboundPort(riURI+"_AVMMOP", this);
		
		this.addPort(dataProviderOutboundPort);
		this.addPort(computerServicesOutboundPort);
		this.addPort(dataDispatcherOutboundPort);
		this.addPort(requestResourceVMOutboundPort);
		this.addPort(avmMop);
		
		this.dataProviderOutboundPort.publishPort();
		this.computerServicesOutboundPort.publishPort();
		this.dataDispatcherOutboundPort.publishPort();
		this.requestResourceVMOutboundPort.publishPort();
		this.avmMop.publishPort();
		
		this.addRequiredInterface(ConnectCoordinateAVMI.class);
		this.connectCoordinateAVMOutboundPort = new ConnectCoordinateAVMOutboundPort(riURI+"_CCOP",this);
		this.addPort(connectCoordinateAVMOutboundPort);
		this.connectCoordinateAVMOutboundPort.publishPort();
	}
	
	/**
	 * Connect with DataProvider
	 * @param providerURI
	 * @throws Exception
	 */
	public void connectWithDataProvider(String providerURI) throws Exception {
		this.providerURI=providerURI;
		// DataProviderOutboundPort (resourceInspector) --C O-- DataProviderInboundPort (DataProvider)
		this.dataProviderOutboundPort	.doConnection(providerURI+"_DPIP", DataProviderConnector.class.getCanonicalName());
		this.dataDispatcherOutboundPort	.doConnection(providerURI+"_DDIP", DataProviderDispatcherConnector.class.getCanonicalName());
	}
	
	/**
	 * Check for resources availability if there is any available Processor 
	 * (Core) in the list of Computers
	 * 
	 * @return List URIs of the available Computers for 2 AVM
	 * @throws Exception
	 */
	public LinkedList<String> getAvailableResource(String applicationURI) throws Exception {
		// TODO we have to receive number of AVM to create on the request provided from the Client
		// int NBAVM_TO_CREATE = admissionI.getNBAVMToCreate();
		final int NBAVM_TO_CREATE = 2;

		// Get all computer URIs
		LinkedList<String> computerListURI = new LinkedList<String>();
		computerListURI = dataProviderOutboundPort.getComputerListURIs();
		
		for (String uri : computerListURI) {
			ComputerInfo computerInfo=dataProviderOutboundPort.getComputerInfos(uri);
			System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO "+computerInfo.getComputerURI()+" : "+computerInfo.getNbCoreAvailable());
		}
		
		
		// List of 2 URIs of available computers
		LinkedList<String> computerURIfor2AVM=new LinkedList<String>();
		for (String uri : computerListURI) {
			ComputerInfo computerInfo=dataProviderOutboundPort.getComputerInfos(uri);
			int nbAvailableCores=0;
			Integer sharedResource = computerInfo.getSharedResource();
			boolean[][] allocatedCores;
			
			// look at the synchronization barrier if there is an available 
			// information about this computer, else wait()
			synchronized (sharedResource) {
				if(sharedResource==0) {
					System.out.println(applicationURI+" waiting for : "+uri);
					wait();
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
				if(nbAvailableCores >= NBCORES) {
					computerInfo.updateCoresState(allocatedCores, NBCORES,State_change.ADD);
					computerURIfor2AVM.add(uri);
					if(computerURIfor2AVM.size()==NBAVM_TO_CREATE) {
					return computerURIfor2AVM;
					}
				}
				System.out.println("cores available in "+computerInfo.getComputerURI()+" == "+nbAvailableCores+" for : "+applicationURI);
			}
		}
		return null;
	}
	/**
	 * allocate resources for an ApplicationVM
	 * @throws Exception 
	 */
	public ApplicationVMAdaptable createApplicationVM(String applicationContainerURI, String computerURI) throws Exception {
		
		String avmURI=null;
		// Allocate cores for the ApplicationVM
		this.computerServicesOutboundPort.doConnection(computerURI+"_CSIP",
						 ComputerServicesConnector.class.getCanonicalName());
		AllocatedCore[] cores = computerServicesOutboundPort.allocateCores(NBCORES);
		this.computerServicesOutboundPort.doDisconnection();
		
		// Store informations about the RequestDispatcher
		RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort.getApplicationInfos(applicationContainerURI);
		
		// Add the ApplicationVM information to the RequestDispatcherInformation
		synchronized (dispatcherInfo) {
			avmURI=applicationContainerURI+"AVM_"+dispatcherInfo.getNbVMCreated();
			dispatcherInfo.addApplicationVM(avmURI, computerURI, cores);
		}

		// Create and deploy the AppplicationVM
		String RSIP_URI=avmURI+"_RSIP";
		String RNOP_URI=avmURI+"_RNOP";
		
		System.out.println(avmURI + " " + avmURI+"_AVMMIP" + " " + RSIP_URI + " " + RNOP_URI);
		ApplicationVMAdaptable avm= new ApplicationVMAdaptable(	avmURI, 
																avmURI+"_AVMMIP", 
																RSIP_URI, 
																RNOP_URI);

		// Create an ApplicationVMMangement
		/*
		ApplicationVMManagementOutboundPort avmMop= new ApplicationVMManagementOutboundPort(riURI+"_AVMMOP", new AbstractComponent(1,1){});
		avmMop.publishPort();
		*/
		avmMop.doConnection(avmURI+"_AVMMIP", ApplicationVMManagementConnector.class.getCanonicalName());

		// allocate cores for the ApplicationVM
		avmMop.allocateCores(cores);
		avmMop.doDisconnection();
		// deploy the component
		DelployTools.deployComponent(avm);
		return avm;
	}
	/**
	 * Create a Request dispatcher
	 * @param applicationContainerURI
	 * @param computerURI
	 * @return a RequestDispatcher Component
	 * @throws Exception 
	 */
	public RequestDispatcherComponent createRequestDispatcher(AdmissionI admissionI) throws Exception {
		// Get the URI of the ApplicationContainer
		String applicationContainerURI = admissionI.getApplicationURI();
		
		// Create and Deploy the Request Dispatcher 
		RequestDispatcherComponent RDC = new RequestDispatcherComponent(applicationContainerURI+"RD");
		DelployTools.deployComponent(RDC);
		
		// Store the informations related to the Request Dispatcher in the (DataProvider)
		dataDispatcherOutboundPort.addApplicationContainer(applicationContainerURI, applicationContainerURI+"RD");
		return RDC;
	} 
}

package fr.upmc.datacenter.software.admissioncontroller;

import java.util.LinkedList;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.dataprovider.connectors.DataProviderConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderDispatcherI;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.dataprovider.ports.DataDispatcherOutboundPort;
import fr.upmc.datacenter.dataprovider.ports.DataProviderOutboundPort;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.ports.RequestResourceVMOutboundPort;

/**
 * <p><strong>Description</string></p>
 * 
 * this component <code>ResourceInspector</code> interacts with the 
 * <code>DataProvider</code> to request availability of resources in 
 * the DataCenter, he can inspects the abstract values of performences stored 
 * 
 * @author Hacene KASDI
 * @version 2017.12.10.HK
 *
 */
public class ResourceInspector extends AbstractComponent {

	protected String riURI;
	protected String providerURI;
	private static final int NBCORES = 4;
	
	protected DataProviderOutboundPort 			dataProviderOutboundPort;
	protected ComputerServicesOutboundPort 		computerServicesOutboundPort;
	protected DataDispatcherOutboundPort 		dataDispatcherOutboundPort;
	protected RequestResourceVMOutboundPort		requestResourceVMOutboundPort;
	
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
		
		
		// CREATE AND PUBLISH PORT
		this.dataProviderOutboundPort 		= new DataProviderOutboundPort			(riURI+"_DPOP", this);
		this.computerServicesOutboundPort	= new ComputerServicesOutboundPort		(riURI+"_CSOP", this);
		this.dataDispatcherOutboundPort		= new DataDispatcherOutboundPort		(riURI+"_DDOP", this);
		this.requestResourceVMOutboundPort	= new RequestResourceVMOutboundPort		(riURI+"_RVMI", this);
		
		this.addPort(dataProviderOutboundPort);
		this.addPort(computerServicesOutboundPort);
		this.addPort(dataDispatcherOutboundPort);
		this.addPort(requestResourceVMOutboundPort);
		
		this.dataProviderOutboundPort.publishPort();
		this.computerServicesOutboundPort.publishPort();
		this.dataDispatcherOutboundPort.publishPort();
		this.requestResourceVMOutboundPort.publishPort();
	}
	
	/**
	 * Connect with DataProvider
	 * @param providerURI
	 * @throws Exception
	 */
	public void connectWithDataProvider(String providerURI) throws Exception {
		System.out.println("RessourceInspector try to connect with "+providerURI);
		this.providerURI=providerURI;
		// DataProviderOutboundPort (resourceInspector) --C O-- DataProviderInboundPort (DataProvider)
		this.dataProviderOutboundPort.doConnection(providerURI+"_DPIP", DataProviderConnector.class.getCanonicalName());
		System.out.println("RessourceInspector connected with "+providerURI);
	}
	
	/**
	 * Check for resources availability if there is any available Processor 
	 * (Core) in the list of Computers
	 * 
	 * @return URI of the available Computer
	 * @throws Exception
	 */
	public String getAvailableResource(AdmissionI admissionI) throws Exception {
		LinkedList<String> computerListURI = dataProviderOutboundPort.getComputerListURIs();

		for (String uri : computerListURI) {
			ComputerInfo computerInfo=dataProviderOutboundPort.getComputerInfos(uri);
			Integer sharedResource = computerInfo.getSharedResource();
			int nbCoresAvailable;
			boolean[][] allocatedCores;
			
			
			// look at the synchronisation barriere if there is ay available 
			// information about this computer, else wait()
			synchronized (sharedResource) {
				if(sharedResource==0) {
					System.out.println(admissionI.getApplicationURI()+" waiting for : "+uri);
					wait();
				}
			}
			
			synchronized (computerInfo) {
				// get number of available core of this computer
				allocatedCores=computerInfo.getCoreState();
				nbCoresAvailable = computerInfo.getNbCoreAvailable();
				// check if nbCoresAvailable >= NBCORES than set these cores as allocated
				if(nbCoresAvailable>= NBCORES) {
					computerInfo.updateCoresState(allocatedCores, NBCORES);
					return uri;
				}
				System.out.println("cores available in "+computerInfo.getComputerURI()+" == "+nbCoresAvailable+" for : "+admissionI.getApplicationURI());
				
			}
		}

		System.out.println("No core found by ResourceInspector : "+riURI+" for : "+admissionI.getApplicationURI());
		return null;
	}
	/**
	 * Reserve resources for an ApplicationVM
	 */
	
}

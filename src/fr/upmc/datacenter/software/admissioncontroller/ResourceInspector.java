package fr.upmc.datacenter.software.admissioncontroller;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.dataprovider.connectors.DataProviderConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderDispatcherI;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.dataprovider.ports.DataDispatcherOutboundPort;
import fr.upmc.datacenter.dataprovider.ports.DataProviderOutboundPort;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMI;

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
	
	protected DataProviderOutboundPort 			dataProviderOutboundPort;
	protected ComputerServicesOutboundPort 		computerServicesOutboundPort;
	protected DataDispatcherOutboundPort 		dataDispatcherOutboundPort;
	
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
		
		this.addPort(dataProviderOutboundPort);
		this.addPort(computerServicesOutboundPort);
		
		this.dataProviderOutboundPort.publishPort();
		this.computerServicesOutboundPort.publishPort();
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
	 * Check for resources availability if there is any available Processor (Core) in the Computers
	 */
	
	/**
	 * Reserve resources for an ApplicationVM
	 */
	
}

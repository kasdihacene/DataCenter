package fr.upmc.datacenter.dataprovider;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderDispatcherI;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.dataprovider.ports.DataDispatcherInboundPort;
import fr.upmc.datacenter.dataprovider.ports.DataProviderInboundPort;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
/**
 * 
 * @author Hacene KASDI
 * @version 24.12.17.HK
 *
 * <code>ComputerStateDataConsumerI</code> defines the consumer side methods used to 
 * receive state data pushed by a computer, both static and dynamic.
 */
public class DataProvider extends 	AbstractComponent 
						implements 	ComputerStateDataConsumerI,
									DataProviderI,DataProviderDispatcherI {

	protected String providerURI;
	protected HashMap<String, ComputerDynamicStateDataOutboundPort> mapComputerDynamicSOP;
	protected HashMap<String, ComputerInfo> mapComputerInfo;
	protected HashMap<String, RequestDispatcherInfo> mapApplicationDispatcher;
	
	protected DataProviderInboundPort 		dataProviderInboundPort;
	protected DataDispatcherInboundPort 	dataDispatcherInboundPort;
	
	@SuppressWarnings("deprecation")
	public DataProvider(String providerURI) throws Exception {
		
		this.providerURI=providerURI;
		mapComputerInfo			=new HashMap<String,ComputerInfo>();
		mapComputerDynamicSOP	=new HashMap<String,ComputerDynamicStateDataOutboundPort>();
		mapApplicationDispatcher=new HashMap<String,RequestDispatcherInfo>();
		
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.addOfferedInterface(DataRequiredI.PushI.class) ;
		this.addRequiredInterface(DataRequiredI.PullI.class) ;
		this.addOfferedInterface(DataProviderI.class);
		this.addOfferedInterface(DataProviderDispatcherI.class);
		
		// PUBLISH THE DataProviderInboundPort    --O
		this.dataProviderInboundPort= new DataProviderInboundPort(providerURI+"_DPIP", this);
		this.addPort(dataProviderInboundPort);
		dataProviderInboundPort.publishPort();
		
		// PUBLISH THE DataDispatcherInboundPort  --O
		this.dataDispatcherInboundPort= new DataDispatcherInboundPort(providerURI+"_DDIP", this);
		this.addPort(dataDispatcherInboundPort);
		dataDispatcherInboundPort.publishPort();
	}
	
	@Override
	public LinkedList<String> getComputerListURIs() throws Exception {
		LinkedList<String> computerListURIs=new LinkedList<String>();
		for (String computerURI : mapComputerInfo.keySet())
			computerListURIs.addLast(computerURI);
		return computerListURIs;
	}

	@Override
	public ComputerInfo getComputerInfos(String computerURI) throws Exception {
		return mapComputerInfo.get(computerURI);
	}
	
	public void storeComputerData(String computerURI,
			Set<Integer> possibleFrequencies,
			Map<Integer, Integer> processingPower,
			int defaultFrequency,
			int maxFrequencyGap,
			int numberOfProcessors,
			int numberOfCores) throws Exception
	{
		//Create a new port for receiving data about the new computer
		ComputerDynamicStateDataOutboundPort cdsPort = new ComputerDynamicStateDataOutboundPort(
				providerURI+"_CDSDOP",
				this,
				computerURI);
		
		//Connection with the computer to ask him for pushing informations 
		this.addPort(cdsPort) ;
		cdsPort.publishPort() ;	
		cdsPort.
		doConnection(
				computerURI+"_CDSDIP",
				ControlledDataConnector.class.getCanonicalName()) ;
		
		//Start pushing and receiving answers from a Computer #acceptComputerDynamicData
		cdsPort.startLimitedPushing(2,2);
		cdsPort.startUnlimitedPushing(10000);
		
		mapComputerDynamicSOP.put(computerURI, cdsPort);

		//Associate new Computer informations with the computer
		mapComputerInfo.put(computerURI, new ComputerInfo(computerURI, possibleFrequencies, processingPower, defaultFrequency, maxFrequencyGap, numberOfProcessors, numberOfCores));
	}

	/**
	 * 
	 * @param computerURI
	 * @param staticState
	 * @throws Exception
	 */
	@Override
	public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
	}
	
	/**
	 * 
	 * @param computerURI
	 * @param currentDynamicState
	 * @throws Exception
	 */
	@Override
	public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState)
			throws Exception {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < currentDynamicState.getCurrentCoreReservations().length; i++) {
			for (int j = 0; j < currentDynamicState.getCurrentCoreReservations()[i].length; j++) {
				if(currentDynamicState.getCurrentCoreReservations()[i][j]) {
					stringBuffer.append("|T");
				}else {
					stringBuffer.append("|F");
				}
			}
		}
		
		System.out.println("DYNAMIC DATA PUSHED FROM COMPUTER "+computerURI+" RESERVATION CORES : "+stringBuffer+"|");

		// get the state of the bariere
		Integer sharedResource = mapComputerInfo.get(computerURI).getSharedResource();
		synchronized (sharedResource) {
			ComputerInfo computerInfo = mapComputerInfo.get(computerURI);
			// get the cores information pushed by the Computer and update the ComputerInfo Cores
			boolean [][] allocatedCores = currentDynamicState.getCurrentCoreReservations();
			computerInfo.setCoreState(allocatedCores);
			// set free the resource shared and put it to 1
			if(sharedResource==0) {
			sharedResource.notifyAll();
			// set the resource as available
			sharedResource=1;
			mapComputerInfo.get(computerURI).setSharedResource(sharedResource);
			}
		}
		
	}

	@Override
	public void addApplicationContainer(String applicationURI, String dispatcherURI) throws Exception {
		if(mapApplicationDispatcher.containsKey(applicationURI)) {
			throw new Exception("Application has already a Request dispatcher");
		}
		mapApplicationDispatcher.put(applicationURI, new RequestDispatcherInfo(dispatcherURI));
	}

	@Override
	public void removeApplicationContainer(String applicationURI) throws Exception {
		if(!mapApplicationDispatcher.containsKey(applicationURI)) {
			throw new Exception("Application not found to remove !");
		}
		mapApplicationDispatcher.remove(applicationURI);
	}

/**
 * Get the Request Dispatcher Informations
 */
	@Override
	public RequestDispatcherInfo getApplicationInfos(String appURI) throws Exception {
		return mapApplicationDispatcher.get(appURI);
	}

	@Override
	public LinkedList<String> getApplicationInfosList() throws Exception {
		return null;
	}

}

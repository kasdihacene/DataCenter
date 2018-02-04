package fr.upmc.datacenter.dataprovider;

import java.util.ArrayList;
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
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherComponent;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;
/**
 * <h2>Class descriptor</h2>
 * <code>ComputerStateDataConsumerI</code> defines the consumer side methods used to 
 * receive state data pushed by a computer, both static and dynamic.
 * 
 * This component will store data ( Object java ) related to request dispatcher used on 
 * the data center provide data to controllers for cooperation, adaptation and informations
 * about the network leader ( initiator ) and the whole topology hierarchy. 
 * 
 * @author Hacene KASDI
 * @version 24.12.17.HK
 *
 */
public class DataProvider extends 	AbstractComponent 
						implements 	ComputerStateDataConsumerI,
									DataProviderI,DataProviderDispatcherI {
	
	protected String providerURI;
	protected HashMap<String, ComputerDynamicStateDataOutboundPort> mapComputerDynamicSOP;
	protected HashMap<String, ComputerInfo> mapComputerInfo;
	protected HashMap<String, RequestDispatcherInfo> mapApplicationDispatcher;
	
	protected HashMap<String, ApplicationVMInfo> mapInformationAVMcoordinate;
	
	/** list of ApplicationVM information used in cooperation to store informations about every AVM allocated */
	protected ArrayList<ApplicationVMInfo> listApplicationVMInfo;
	/** Number of available AplicationVM */
	protected static int NBVMAVAILABLE 		= 0;
	/** the leader of the network only can change this token */
	protected static Integer setNetLeader 	= 0;
	/** list of nodes of the RingNetwork*/
	protected LinkedList<String> nodesNetwork;
	
	protected DataProviderInboundPort 		dataProviderInboundPort;
	protected DataDispatcherInboundPort 	dataDispatcherInboundPort;
	
	@SuppressWarnings("deprecation")
	public DataProvider(String providerURI) throws Exception {

		this.providerURI=providerURI;
		mapComputerInfo						=new HashMap<String,ComputerInfo>();
		mapComputerDynamicSOP				=new HashMap<String,ComputerDynamicStateDataOutboundPort>();
		mapApplicationDispatcher			=new HashMap<String,RequestDispatcherInfo>();
		listApplicationVMInfo				=new ArrayList<>();
		nodesNetwork						=new LinkedList<>();
		mapInformationAVMcoordinate			=new HashMap<String,ApplicationVMInfo>();
		
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
		
		//Connection with the computer to ask him for pushing dynamic|static informations 
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
		mapComputerInfo.put(computerURI, new ComputerInfo(
				computerURI, 
				possibleFrequencies, 
				processingPower, 
				defaultFrequency, 
				maxFrequencyGap, 
				numberOfProcessors, 
				numberOfCores));
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
	 * @see {@link ComputerStateDataConsumerI#acceptComputerDynamicData(String, ComputerDynamicStateI)}
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
					stringBuffer.append("|BUSY");
				}else {
					stringBuffer.append("|IDLE");
				}
			}
		}
		
		
		System.out.println("DYNAMIC DATA PUSHED FROM "+computerURI+" RESERVATION CORES : "+stringBuffer+"|");

		// get the state of the barrier
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

	/**
	 * Add informations related to the <code>{@link RequestDispatcherComponent}</code>
	 */
	@Override
	public void addApplicationContainer(String applicationURI, String dispatcherURI) throws Exception {
		if(mapApplicationDispatcher.containsKey(applicationURI)) {
			throw new Exception("Application has already a Request dispatcher");
		}
		mapApplicationDispatcher.put(applicationURI, new RequestDispatcherInfo(dispatcherURI));
	}

	/**
	 * remove ApplicationContainer
	 */
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

	//========================================================================
	//		 THIS SECTION USED FOR COORDINATION AND COOPERATION IN LARGE SCALE
	//========================================================================
	@Override
	public int getNBAVMcreated() throws Exception {
		return NBVMAVAILABLE;
	}

	@Override
	public void addApplicationVM(ApplicationVMInfo applicationVMInfo) throws Exception {
		listApplicationVMInfo.add(applicationVMInfo);
		mapInformationAVMcoordinate.put(applicationVMInfo.getVmURI(), applicationVMInfo);
		NBVMAVAILABLE++;
		
	}

	@Override
	public void DeleteDefinitelyAVM(ApplicationVMInfo avmINFO) throws Exception {
		
		NBVMAVAILABLE--;
	listApplicationVMInfo.remove(avmINFO);
	mapInformationAVMcoordinate.remove(avmINFO.getVmURI());
	}
	
	@Override
	public ApplicationVMInfo removeApplicationVM() throws Exception {
		synchronized (listApplicationVMInfo) {
			if(NBVMAVAILABLE == 0)return null;
			NBVMAVAILABLE--;
			String uriAVM=listApplicationVMInfo.get(listApplicationVMInfo.size()-1).getVmURI();
			mapInformationAVMcoordinate.remove(uriAVM);
			return listApplicationVMInfo.remove(listApplicationVMInfo.size()-1);
		}				
	}
	@Override
	public ApplicationVMInfo getApplicationVMCoordinate(String avmURI) throws Exception {
			return mapInformationAVMcoordinate.get(avmURI);
	}

	@Override
	public ArrayList<ApplicationVMInfo> getCoordinateAVMs() throws Exception {
		return listApplicationVMInfo;
	}

	@Override
	public void subscribeToRingNetwork(String user) {
		synchronized (setNetLeader) {
			if(setNetLeader==0) {
				setNetLeader=1;
			}else {
				// The second arrival will be the Leader
				if(setNetLeader==1) {
					LeaderRingNetwork.setLeaderNetwork(user);
					setNetLeader=2;
				}
			}
		}
		nodesNetwork.addLast(user);
	}
	
	@Override
	public String whoIsNetworkLeader() {
		return LeaderRingNetwork.whoIsLeader();
	}
	
	@Override
	public String getNextNode() {
		String nextNode = nodesNetwork.getFirst();
		nodesNetwork.addLast(nodesNetwork.removeFirst());
		return nextNode;
		
	}
	/**
	 * <h2>Inner Class descriptor</h2>
	 * 
	 * This inner class represents information related to the 
	 * leader of the network topology, which initialize the first 
	 * transaction by sending the token {@link TransitTokenI}
	 *  
	 * @author Hacene KASDI
	 *
	 */
	private static class LeaderRingNetwork{
		
		private static String leaderURI = "NULL";
		public static void setLeaderNetwork(String leader) {
			leaderURI = leader;
		}
		
		public static String whoIsLeader() {
			return leaderURI;
		}
	}


}
package fr.upmc.datacenter.dataprovider;

import java.util.HashMap;
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
	
	protected DataProviderInboundPort 		dataProviderInboundPort;
	protected DataDispatcherInboundPort 	DataDispatcherInboundPort;
	
	@SuppressWarnings("deprecation")
	public DataProvider(String providerURI) throws Exception {
		this.providerURI=providerURI;
		mapComputerInfo=new HashMap<String,ComputerInfo>();
		mapComputerDynamicSOP=new HashMap<String,ComputerDynamicStateDataOutboundPort>();
	
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.addOfferedInterface(DataRequiredI.PushI.class) ;
		this.addRequiredInterface(DataRequiredI.PullI.class) ;
		this.addOfferedInterface(DataProviderI.class);
		
		// PUBLISH THE DataProviderInboundPort    --O
		this.dataProviderInboundPort= new DataProviderInboundPort(providerURI+"_DPIP", this);
		this.addPort(dataProviderInboundPort);
		dataProviderInboundPort.publishPort();
		
		// PUBLISH THE DataDispatcherInboundPort  --O
		
		
	}
	
	@Override
	public Set<String> getComputerListURIs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ComputerInfo getComputerInfos(String computerURI) throws Exception {
		// TODO Auto-generated method stub
		return null;
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
		cdsPort.startLimitedPushing(1,1);
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

		System.out.println("PUSHED FROM COMPUTER DYNAMIC"+computerURI+"  "+currentDynamicState.getCurrentCoreReservations()[0][0]);
	}

	@Override
	public void addApplicationContainer(String applicationURI, String dispatcherURI) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeApplicationContainer(String applicationURI) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

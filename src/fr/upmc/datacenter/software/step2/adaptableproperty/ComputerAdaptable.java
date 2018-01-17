package fr.upmc.datacenter.software.step2.adaptableproperty;

import java.util.Map;
import java.util.Set;

import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterComputerI;
import fr.upmc.datacenter.software.step2.adaptableproperty.ports.AdapterComputerInboundPort;
/**
 * 
 * @author Hacene KASDI
 * @version 08.01.2018.HKBIRTHDAY
 */
public class ComputerAdaptable 	extends Computer 
								implements AdapterComputerI {

	private ProcessorManagementOutboundPort 	processorManagementOutboundPort;
	private AdapterComputerInboundPort 			adapterComputerInboundPort;
	
	
	
	public ComputerAdaptable(
			String computerURI, 
			Set<Integer> possibleFrequencies,
			Map<Integer, Integer> processingPower, 
			int defaultFrequency, 
			int maxFrequencyGap, 
			int numberOfProcessors,
			int numberOfCores, 
			String computerServicesInboundPortURI,
			String computerStaticStateDataInboundPortURI,
			String computerDynamicStateDataInboundPortURI) throws Exception {
		
		super(computerURI, 
				possibleFrequencies, 
				processingPower, 
				defaultFrequency, 
				maxFrequencyGap, 
				numberOfProcessors,
				numberOfCores,
				computerServicesInboundPortURI, 
				computerStaticStateDataInboundPortURI,
				computerDynamicStateDataInboundPortURI);
		
		/**
		 *  To invoke updateCoreFrequency service used by Processor of a current Computer
		 *  we have to add a required interface <code>ProcessorManagementI</code>, here we use
		 *  the OutboundPort ProcessorManagementOutboundPort --C to set changes of frequency concretely
		 *  on the Processor of the current Computer
		 */
		this.addRequiredInterface(ProcessorManagementI.class) ;
		this.processorManagementOutboundPort =
				new ProcessorManagementOutboundPort(computerURI+"_PMOP",
												   this) ;
		this.addPort(this.processorManagementOutboundPort) ;
		this.processorManagementOutboundPort.publishPort() ;
		
		/**
		 * This interface will be used to make adaption on Computer Cores
		 * and managing Cores on Processors.
		 */
		this.addRequiredInterface(AdapterComputerI.class);
		this.adapterComputerInboundPort =
				new AdapterComputerInboundPort(computerURI+"_ACIP", this);
		this.addPort(this.adapterComputerInboundPort);
		this.adapterComputerInboundPort.publishPort();
	}

	/**
	 * set a new frequency for a given core on this processor; exceptions are
	 * raised if the required frequency is not admissible for this processor
	 * or not currently possible for the given core.
	 * 
	 * used on <code>Processor</code> component, it requires a <code>ProcessorManagementOutboundPort</code>
	 * to invoke this service.
	 * 
	 * @param coreNo
	 * @param frequency
	 * @throws UnavailableFrequencyException
	 * @throws UnacceptableFrequencyException
	 * @throws Exception
	 */
	@Override
	public void updateCoreFrequency(AllocatedCore allocatedCore, int frequency)
			throws 	UnavailableFrequencyException, 
					UnacceptableFrequencyException, 
					Exception {
		//
		// Computers has a map from processor URI to their different inbound ports URI
		// Map<String, Map<Processor.ProcessorPortTypes, String>>
		// processorsInboundPortURI	map form processors' URI to URI of the processors' inbound ports.
		
		String PMIP_URI = allocatedCore.processorInboundPortURI.get(Processor.ProcessorPortTypes.MANAGEMENT);
		processorManagementOutboundPort.doConnection(PMIP_URI, ProcessorManagementConnector.class.getCanonicalName());
		processorManagementOutboundPort.setCoreFrequency(allocatedCore.coreNo, frequency);
		processorManagementOutboundPort.doDisconnection();
	}

}

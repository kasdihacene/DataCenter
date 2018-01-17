package fr.upmc.datacenter.software.step2.adaptableproperty.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;

public interface AdapterComputerI {
	
	/**
	 * used on <code>Computer</code> component.
	 * 
	 * @return an instance of <code>AllocatedCore</code> with the data about the allocated core.
	 * @throws Exception
	 */
	public AllocatedCore	allocateCore() throws Exception;
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
	public void			updateCoreFrequency(AllocatedCore allocatedCore, int frequency)
			throws	UnavailableFrequencyException,
					UnacceptableFrequencyException,
					Exception;
	/**
	 * releases a priorly reserved core.
	 * 
	 * used on <code>Computer</code> component.
	 * 
	 * @param ac			priorly allocated core data.
	 * @throws Exception
	 */
	public void				releaseCore(AllocatedCore ac) throws Exception;

}

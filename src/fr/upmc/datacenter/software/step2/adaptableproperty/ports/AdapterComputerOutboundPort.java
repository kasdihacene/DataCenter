package fr.upmc.datacenter.software.step2.adaptableproperty.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterComputerI;
/**
 * 
 * @author Hacene KASDI
 * @version 08.01.2018.HKBIRTHDAY
 */
public class AdapterComputerOutboundPort 	extends AbstractOutboundPort 
											implements AdapterComputerI {

	public AdapterComputerOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, AdapterComputerI.class, owner);
	}

	@Override
	public AllocatedCore allocateCore() throws Exception {
		return ((AdapterComputerI)this.connector).allocateCore();
	}

	@Override
	public void updateCoreFrequency(AllocatedCore allocatedCore, int frequency)
			throws UnavailableFrequencyException, UnacceptableFrequencyException, Exception {
		((AdapterComputerI)this.connector).updateCoreFrequency(allocatedCore, frequency);
	}

	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		((AdapterComputerI)this.connector).releaseCore(ac);
	}

}

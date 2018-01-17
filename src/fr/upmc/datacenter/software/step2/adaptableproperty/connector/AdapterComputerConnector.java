package fr.upmc.datacenter.software.step2.adaptableproperty.connector;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterComputerI;
/**
 * 
 * @author Hacene KASDI
 * @version 08.01.2018.HKBIRTHDAY
 */
public class AdapterComputerConnector extends AbstractConnector implements AdapterComputerI {

	@Override
	public AllocatedCore allocateCore() throws Exception {
		return ((AdapterComputerI)this.offering).allocateCore();
	}

	@Override
	public void updateCoreFrequency(AllocatedCore allocatedCore, int frequency)
			throws UnavailableFrequencyException, UnacceptableFrequencyException, Exception {
		((AdapterComputerI)this.offering).updateCoreFrequency(allocatedCore, frequency);
	}

	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		((AdapterComputerI)this.offering).releaseCore(ac);
	}

}

package fr.upmc.datacenter.software.step2.adaptableproperty.connector;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterVMI;

/**
 * 
 * @author Hacene KASDI
 * @version 06.01.2018
 * 
 *
 */
public class AdapterVMConnector extends AbstractConnector implements AdapterVMI {

	@Override
	public void allocateCore(AllocatedCore ac) throws Exception {
		((AdapterVMI)this.offering).allocateCore(ac);
	}

	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		((AdapterVMI)this.offering).releaseCore(ac);
	}

	@Override
	public void releaseAllCores() throws Exception {
		((AdapterVMI)this.offering).releaseAllCores();
	}

}

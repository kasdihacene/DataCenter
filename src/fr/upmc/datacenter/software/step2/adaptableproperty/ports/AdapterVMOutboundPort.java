package fr.upmc.datacenter.software.step2.adaptableproperty.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterVMI;

/**
 * 
 * @author Hacene KASDI
 * @version 06.01.2018
 * 
 *
 */
public class AdapterVMOutboundPort extends AbstractOutboundPort implements AdapterVMI {

	public AdapterVMOutboundPort(	String uri, 
									ComponentI owner) throws Exception {
		super(uri, AdapterVMI.class , owner);
	}

	@Override
	public void allocateCore(AllocatedCore ac) throws Exception {
		((AdapterVMI)this.connector).allocateCore(ac);

	}

	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		((AdapterVMI)this.connector).releaseCore(ac);

	}

	@Override
	public void releaseAllCores() throws Exception {
		((AdapterVMI)this.connector).releaseAllCores();

	}

	@Override
	public Integer sizeTaskQueue() throws Exception {
		return 	((AdapterVMI)this.connector).sizeTaskQueue();

	}

}

package fr.upmc.datacenter.software.step2.adaptableproperty.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterVMI;

/**
 * 
 * @author Hacene KASDI
 * @version 06.01.2018
 * 
 *
 */
public class AdapterVMInboundPort extends AbstractInboundPort implements AdapterVMI {

	public AdapterVMInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, AdapterVMI.class, owner);
	}

	private static final long serialVersionUID = 1062018L;

	@Override
	public void allocateCore(AllocatedCore ac) throws Exception {
		final AdapterVMI adapterVMI = (AdapterVMI) this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() 
				{
					@Override
					public Void call() throws Exception {
						adapterVMI.allocateCore(ac);
						return null ;
					}
				}) ;

	}

	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		final AdapterVMI adapterVMI = (AdapterVMI) this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() 
				{
					@Override
					public Void call() throws Exception {
						adapterVMI.releaseCore(ac);
						return null ;
					}
				}) ;

	}

	@Override
	public void releaseAllCores() throws Exception {
		final AdapterVMI adapterVMI = (AdapterVMI) this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() 
				{
					@Override
					public Void call() throws Exception {
						adapterVMI.releaseAllCores();
						return null ;
					}
				}) ;

	}

	@Override
	public Integer sizeTaskQueue() throws Exception {
		final AdapterVMI adapterVMI = (AdapterVMI) this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return adapterVMI.sizeTaskQueue();
					}
				});
	}


}

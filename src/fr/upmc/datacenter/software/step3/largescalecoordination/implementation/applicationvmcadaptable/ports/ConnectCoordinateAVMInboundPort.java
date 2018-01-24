package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.interfaces.ConnectCoordinateAVMI;

public class ConnectCoordinateAVMInboundPort extends AbstractInboundPort implements ConnectCoordinateAVMI {

	public ConnectCoordinateAVMInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,ConnectCoordinateAVMI.class, owner);
	}

	private static final long serialVersionUID = 188877L;

	@Override
	public void connectAVMwithSubmissioner(String uriDispatcher) throws Exception {
		final ConnectCoordinateAVMI connectCoordinateAVMI = (ConnectCoordinateAVMI)this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {
			@Override
			public Void call() throws Exception {
				connectCoordinateAVMI.connectAVMwithSubmissioner(uriDispatcher);
				return null;
			}
		});
	}

}

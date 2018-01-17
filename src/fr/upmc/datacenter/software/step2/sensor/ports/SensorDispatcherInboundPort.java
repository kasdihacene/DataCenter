package fr.upmc.datacenter.software.step2.sensor.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataOfferedI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherComponent;

public class SensorDispatcherInboundPort extends AbstractControlledDataInboundPort {

	private static final long serialVersionUID = 1201805L;

	public SensorDispatcherInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
	
	public SensorDispatcherInboundPort(ComponentI owner) throws Exception {
		super(owner);
	}

	@Override
	public DataI get() throws Exception {
		final RequestDispatcherComponent requestDispatcherComponent = (RequestDispatcherComponent) this.owner;
		return requestDispatcherComponent.handleRequestSync(new ComponentI.ComponentService<DataOfferedI.DataI>() {
			@Override
			public DataI call() throws Exception {
				return requestDispatcherComponent.prepareCollectedData();
			}
		});
	}

	

}

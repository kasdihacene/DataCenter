package fr.upmc.datacenter.software.step2.sensor.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;
import fr.upmc.datacenter.software.step2.adapter.interfaces.DataPushDispatcherReceiverI;
import fr.upmc.datacenter.software.step2.sensor.DataPushDispatcherI;

public class SensorDispatcherOutboundPort extends AbstractControlledDataOutboundPort{

	private static final long serialVersionUID = 1052018L;
	protected String URI;
	
	public SensorDispatcherOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}
	
	public SensorDispatcherOutboundPort(
			String uri,
			ComponentI owner,
			String URI
			) throws Exception
		{
			super(uri, owner);
			this.URI = URI ;
		}
	@Override
	public void receive(DataI receivedData) throws Exception {
		((DataPushDispatcherReceiverI)this.owner).receivePushedData((DataPushDispatcherI)receivedData);
	}

}

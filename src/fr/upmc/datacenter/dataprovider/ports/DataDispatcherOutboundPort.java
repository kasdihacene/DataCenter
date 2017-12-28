package fr.upmc.datacenter.dataprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderDispatcherI;

/**
 * 
 * @author Hacene KASDI
 * @version 28.12.17.HK
 * 
 * 
 *
 */
public class DataDispatcherOutboundPort extends AbstractOutboundPort implements DataProviderDispatcherI {

	public DataDispatcherOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataProviderDispatcherI.class, owner);
	}

	@Override
	public void addApplicationContainer(String applicationURI, String dispatcherURI) throws Exception {
		((DataProviderDispatcherI)this.connector).addApplicationContainer(applicationURI, dispatcherURI);
	}

	@Override
	public void removeApplicationContainer(String applicationURI) throws Exception {
		((DataProviderDispatcherI)this.connector).removeApplicationContainer(applicationURI);
	}

}

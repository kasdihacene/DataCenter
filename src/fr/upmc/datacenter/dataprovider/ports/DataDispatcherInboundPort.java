package fr.upmc.datacenter.dataprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderDispatcherI;
/**
 * 
 * @author Hacene KASDI
 * @version 28.12.17.HK
 * 
 * This class <code>DataDispatcherInboundPort</code> receives requests for adding
 * or removing <code>ApplicationContainer</code>, offers services of addApplicationContainer
 * and removeApplicationContainer
 *
 */
public class DataDispatcherInboundPort extends AbstractInboundPort implements DataProviderDispatcherI {

	public DataDispatcherInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,DataProviderDispatcherI.class, owner);
	}
	private static final long serialVersionUID = 1L;

	@Override
	public void addApplicationContainer(String applicationURI, String dispatcherURI) throws Exception {
		final DataProviderDispatcherI dataProviderDispatcherI = (DataProviderDispatcherI)this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				dataProviderDispatcherI.addApplicationContainer(applicationURI, dispatcherURI);
				return null;
			}
		});

	}

	@Override
	public void removeApplicationContainer(String applicationURI) throws Exception {
		final DataProviderDispatcherI dataProviderDispatcherI = (DataProviderDispatcherI)this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				dataProviderDispatcherI.removeApplicationContainer(applicationURI);
				return null;
			}
		});
	}

}

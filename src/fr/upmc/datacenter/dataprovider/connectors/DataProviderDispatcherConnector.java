package fr.upmc.datacenter.dataprovider.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderDispatcherI;
/**
 * 
 * @author Hacene KASDI
 * @version 28.12.17.HK
 *
 */
public class DataProviderDispatcherConnector extends AbstractConnector implements DataProviderDispatcherI {

	@Override
	public void addApplicationContainer(String applicationURI, String dispatcherURI) throws Exception {
		((DataProviderDispatcherI)this.offering).addApplicationContainer(applicationURI, dispatcherURI);
	}

	@Override
	public void removeApplicationContainer(String applicationURI) throws Exception {
		((DataProviderDispatcherI)this.offering).removeApplicationContainer(applicationURI);
	}

}

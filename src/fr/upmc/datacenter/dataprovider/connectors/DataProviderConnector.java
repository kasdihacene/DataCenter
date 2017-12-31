package fr.upmc.datacenter.dataprovider.connectors;

import java.util.LinkedList;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;

public class DataProviderConnector extends AbstractConnector implements DataProviderI {

	@Override
	public LinkedList<String> getComputerListURIs() throws Exception {
		return ((DataProviderI)this.offering).getComputerListURIs();
	}

	@Override
	public ComputerInfo getComputerInfos(String computerURI) throws Exception {
		return ((DataProviderI)this.offering).getComputerInfos(computerURI);
	}

	@Override
	public RequestDispatcherInfo getApplicationInfos(String appURI) throws Exception {
		return ((DataProviderI)this.offering).getApplicationInfos(appURI);
	}

	@Override
	public LinkedList<String> getApplicationInfosList() throws Exception {
		return ((DataProviderI)this.offering).getApplicationInfosList();
	}

}

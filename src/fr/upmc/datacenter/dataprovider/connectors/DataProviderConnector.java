package fr.upmc.datacenter.dataprovider.connectors;

import java.util.LinkedList;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;

public class DataProviderConnector extends AbstractConnector implements DataProviderI {

	@Override
	public LinkedList<String> getComputerListURIs() throws Exception {
		return ((DataProviderI)this.offering).getComputerListURIs();
	}

	@Override
	public ComputerInfo getComputerInfos(String computerURI) throws Exception {
		return ((DataProviderI)this.offering).getComputerInfos(computerURI);
	}

}

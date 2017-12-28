package fr.upmc.datacenter.dataprovider.ports;

import java.util.Set;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
/**
 * @author Hacene KASDI
 * @version 28.12.17.H
 *
 */
public class DataProviderOutboundPort extends AbstractOutboundPort implements DataProviderI {

	public DataProviderOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, DataProviderI.class, owner);
	}

	@Override
	public Set<String> getComputerListURIs() throws Exception {
		return ((DataProviderI)this.connector).getComputerListURIs();
	}

	@Override
	public ComputerInfo getComputerInfos(String computerURI) throws Exception {
		return ((DataProviderI)this.connector).getComputerInfos(computerURI);
	}

}

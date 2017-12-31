package fr.upmc.datacenter.dataprovider.ports;

import java.util.LinkedList;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
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
	public LinkedList<String> getComputerListURIs() throws Exception {
		return ((DataProviderI)this.connector).getComputerListURIs();
	}

	@Override
	public ComputerInfo getComputerInfos(String computerURI) throws Exception {
		return ((DataProviderI)this.connector).getComputerInfos(computerURI);
	}

	@Override
	public RequestDispatcherInfo getApplicationInfos(String appURI) throws Exception {
		return ((DataProviderI)this.connector).getApplicationInfos(appURI);
	}

	@Override
	public LinkedList<String> getApplicationInfosList() throws Exception {
		return ((DataProviderI)this.connector).getApplicationInfosList();
	}

}

package fr.upmc.datacenter.dataprovider.ports;

import java.util.LinkedList;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;

public class DataProviderInboundPort extends AbstractInboundPort implements DataProviderI {

	public DataProviderInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,DataProviderI.class, owner);
	}
	private static final long serialVersionUID = 1L;

	@Override
	public LinkedList<String> getComputerListURIs() throws Exception {
	
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<LinkedList<String>>() {
					@Override
					public LinkedList<String> call() throws Exception {
						return providerI.getComputerListURIs();
					}
				});
	}

	@Override
	public ComputerInfo getComputerInfos(String computerURI) throws Exception {
	
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<ComputerInfo>() {
					@Override
					public ComputerInfo call() throws Exception {
						return providerI.getComputerInfos(computerURI);
					}
				});
	}

	@Override
	public RequestDispatcherInfo getApplicationInfos(String appURI) throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<RequestDispatcherInfo>() {
					@Override
					public RequestDispatcherInfo call() throws Exception {
						return providerI.getApplicationInfos(appURI);
					}
				});
	}

	@Override
	public LinkedList<String> getApplicationInfosList() throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<LinkedList<String>>() {
					@Override
					public LinkedList<String> call() throws Exception {
						return providerI.getApplicationInfosList();
					}
				});
	}

}

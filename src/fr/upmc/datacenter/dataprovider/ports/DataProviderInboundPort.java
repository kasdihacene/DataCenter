package fr.upmc.datacenter.dataprovider.ports;

import java.util.Set;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;

public class DataProviderInboundPort extends AbstractInboundPort implements DataProviderI {

	public DataProviderInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,DataProviderI.class, owner);
	}
	private static final long serialVersionUID = 1L;

	@Override
	public Set<String> getComputerListURIs() throws Exception {
	
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<Set<String>>() {
					@Override
					public Set<String> call() throws Exception {
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

}

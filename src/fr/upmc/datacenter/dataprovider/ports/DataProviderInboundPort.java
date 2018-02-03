package fr.upmc.datacenter.dataprovider.ports;

import java.util.ArrayList;
import java.util.LinkedList;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
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

	@Override
	public int getNBAVMcreated() throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return providerI.getNBAVMcreated();
					}
				});
	}

	@Override
	public void addApplicationVM(ApplicationVMInfo applicationVMInfo) throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						providerI.addApplicationVM( applicationVMInfo);
						return null;
					}
				});
	}

	@Override
	public ApplicationVMInfo removeApplicationVM() throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<ApplicationVMInfo>() {
					@Override
					public ApplicationVMInfo call() throws Exception {
						return providerI.removeApplicationVM();
					}
				});
	}

	@Override
	public ArrayList<ApplicationVMInfo> getCoordinateAVMs() throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<ArrayList<ApplicationVMInfo>>() {
					@Override
					public ArrayList<ApplicationVMInfo> call() throws Exception {
						return providerI.getCoordinateAVMs();
					}
				});
	}

	@Override
	public void subscribeToRingNetwork(String user) throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						providerI.subscribeToRingNetwork(user);
						return null;
					}
				});
		
	}

	@Override
	public String getNextNode() throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<String>() {
					@Override
					public String call() throws Exception {
						return providerI.getNextNode();
					}
				});
	}

	@Override
	public String whoIsNetworkLeader() throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<String>() {
					@Override
					public String call() throws Exception {
						return providerI.whoIsNetworkLeader();
					}
				});
	}

	@Override
	public ApplicationVMInfo getApplicationVMCoordinate(String avmURI) throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		return owner.handleRequestSync(
				new ComponentI.ComponentService<ApplicationVMInfo>() {
					@Override
					public ApplicationVMInfo call() throws Exception {
						return providerI.getApplicationVMCoordinate(avmURI);
					}
				});
	}

	@Override
	public void DeleteDefinitelyAVM(ApplicationVMInfo avmURI) throws Exception {
		final DataProviderI providerI= (DataProviderI)this.owner;
		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						providerI.DeleteDefinitelyAVM(avmURI);
						return null;
					}
				});		
	}

}

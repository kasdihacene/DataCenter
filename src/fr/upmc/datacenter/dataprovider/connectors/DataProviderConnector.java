package fr.upmc.datacenter.dataprovider.connectors;

import java.util.ArrayList;
import java.util.LinkedList;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
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

	@Override
	public int getNBAVMcreated() throws Exception {
		// TODO Auto-generated method stub
		return ((DataProviderI)this.offering).getNBAVMcreated();
	}

	@Override
	public void addApplicationVM(ApplicationVMInfo applicationVMInfo) throws Exception {
		((DataProviderI)this.offering).addApplicationVM(applicationVMInfo);
		
	}

	@Override
	public ApplicationVMInfo removeApplicationVM() throws Exception {
		return ((DataProviderI)this.offering).removeApplicationVM();
		
	}

	@Override
	public ArrayList<ApplicationVMInfo> getCoordinateAVMs() throws Exception {
		return ((DataProviderI)this.offering).getCoordinateAVMs();
	}

	@Override
	public void subscribeToRingNetwork(String user) throws Exception {
		((DataProviderI)this.offering).subscribeToRingNetwork(user);
		
	}

	@Override
	public String getNextNode() throws Exception {
		return ((DataProviderI)this.offering).getNextNode();
	}

	@Override
	public String whoIsNetworkLeader() throws Exception {
		return ((DataProviderI)this.offering).whoIsNetworkLeader();
	}

	@Override
	public ApplicationVMInfo getApplicationVMCoordinate(String avmURI) throws Exception {
		return ((DataProviderI)this.offering).getApplicationVMCoordinate(avmURI);
	}

	@Override
	public void DeleteDefinitelyAVM(ApplicationVMInfo avmURI) throws Exception {
		((DataProviderI)this.offering).DeleteDefinitelyAVM(avmURI);		
	}

}

package fr.upmc.datacenter.dataprovider.interfaces;

import java.util.ArrayList;
import java.util.LinkedList;

import fr.upmc.datacenter.dataprovider.DataProvider;
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.admissioncontrollercoordination.AdmissionControllerCoordination;

/**
 * This interface <code>DataProviderI</code> list the signature of 
 * methods to be implemented in the Class <code>DateProvider</code> 
 * which collects the infos about the Computers and Applications on the DataCenter
 * 
 * @author Hacene KASDI
 *
 */
public interface DataProviderI {
	/**
	 * 
	 * @return a set of Computer URIs
	 * @throws Exception
	 */
	public LinkedList<String> getComputerListURIs() throws Exception;
	
	/**
	 * 
	 * @param computerURI
	 * @return the informations about a Computer
	 * @throws Exception
	 */
	public ComputerInfo getComputerInfos(String computerURI) throws Exception;
	/**
	 * 
	 * @param appURI
	 * @return informations about the applicationConatiner and the RequestDispatcher associated
	 * @throws Exception
	 */
	public RequestDispatcherInfo getApplicationInfos(String appURI) throws Exception;
	/**
	 * 
	 * @return list of ApplicationContainer
	 * @throws Exception
	 */
	public LinkedList<String> getApplicationInfosList() throws Exception;
	/**
	 * 
	 * @return number of ApplicationVMcoordiante created by the {@link AdmissionControllerCoordination} 
	 * @throws Exception
	 */
	public int getNBAVMcreated() throws Exception;
	/**
	 * Add AVM informations to {@link DataProvider}
	 * @param applicationVMInfo
	 * @throws Exception
	 */
	public void addApplicationVM(ApplicationVMInfo applicationVMInfo) throws Exception;
	/**
	 * get the AVM informations entity and remove it from the list 
	 * it will be set as in use
	 * 
	 * @return an ApplicationVM informations 
	 * @throws Exception
	 */
	public ApplicationVMInfo removeApplicationVM() throws Exception;
	/**
	 * get the list of AVMs cooridinate
	 * @return array list of ApplicationVM information
	 * @throws Exception
	 */
	public ArrayList<ApplicationVMInfo> getCoordinateAVMs() throws Exception;
	
	public void subscribeToRingNetwork(String user) throws Exception;
	
	public String getNextNode() throws Exception;
	
	public String whoIsNetworkLeader() throws Exception;
	
	public ApplicationVMInfo getApplicationVMCoordinate(String avmURI) throws Exception;
	
	public void DeleteDefinitelyAVM(ApplicationVMInfo avmINFO) throws Exception;
}
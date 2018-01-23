package fr.upmc.datacenter.dataprovider.interfaces;

import java.util.LinkedList;

import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;

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
}

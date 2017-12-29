package fr.upmc.datacenter.dataprovider.interfaces;

import java.util.LinkedList;

import fr.upmc.datacenter.software.informations.computers.ComputerInfo;

/**
 * This interface <code>DataProviderI</code> list the signiture of methods to be implemented in the Class 
 * <code>DateProvider</code> which collects the infos about the Computers and Applications on the Datacenter
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

}

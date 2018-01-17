package fr.upmc.datacenter.software.step2.adaptableproperty.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * 
 * @author Hacene KASDI
 * @version 06.01.2018
 * 
 * This interface <code>AdapterVMI</code> allows allocating cores to the AVM
 *
 */
public interface AdapterVMI {

	/**
	 * 
	 * @param ac
	 * @throws Exception
	 */
	public void allocateCore(AllocatedCore ac) throws Exception;
	/**
	 * 
	 * @param ac
	 * @throws Exception
	 */
	public void releaseCore(AllocatedCore ac) throws Exception;
	/**
	 * 
	 * @throws Exception
	 */
	public void releaseAllCores() throws Exception;
	
}

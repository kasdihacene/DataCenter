package fr.upmc.datacenter.software.step2.adaptableproperty.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * 
 * @author Hacene KASDI
 * @version 06.01.2018
 * 
 * This interface <code>AdapterVMI</code> allows allocating cores to the AVM
 *
 */
public interface AdapterVMI extends RequiredI, OfferedI{

	/**
	 * Allocate Core for the AVM
	 * @param ac
	 * @throws Exception
	 */
	public void allocateCore(AllocatedCore ac) throws Exception;
	/**
	 * release Core from the AVM
	 * @param ac 
	 * @throws Exception
	 */
	public void releaseCore(AllocatedCore ac) throws Exception;
	/**
	 * remove all cores
	 * @throws Exception
	 */
	public void releaseAllCores() throws Exception;
	/**
	 * get the Queue size in order to allow sending the avmURI on the network. 
	 * @throws Exception
	 */
	public Integer sizeTaskQueue() throws Exception;
	
}

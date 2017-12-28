package fr.upmc.datacenter.software.informations.applicationvm;

import java.util.LinkedList;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.informations.computers.CoreInfo;

/**
 * 
 * @author Hacene KASDI
 * @version 28.12.2017.HK
 * 
 * This class <code>ApplicationVMInfo</code> represents the abstraction of an <code>ApplicationVM</code>
 * its stores informations about <code>AllocatedCore</code> and can inspect cores resources, availability
 * this reference can Add, Remove <code>Core</code> on the <code>Processor</code>
 */
public class ApplicationVMInfo {
	
	private String vmURI;
	private String computerURI;
	private LinkedList<AllocatedCore> allocatedCores;
	
	
	public ApplicationVMInfo(String vmURI, String computerURI) {
		super();
		this.vmURI = vmURI;
		this.computerURI = computerURI;
		this.allocatedCores = new LinkedList<AllocatedCore>();
	}

	//================================================
	//         		Manipulating Cores
	//================================================
	/**
	 * 
	 * @return all Cores
	 */
	public LinkedList<AllocatedCore> getAllocatedCores() {
		return allocatedCores;
	}
	/**
	 * 
	 * @param allocatedCores
	 */
	public void setAllocatedCores(LinkedList<AllocatedCore> allocatedCores) {
		this.allocatedCores = allocatedCores;
	}
	/**
	 * 
	 * @return the last Core
	 */
	public AllocatedCore getLastCore() {
		return allocatedCores.getLast();
	}
	/**
	 * 
	 * @param coreInfo
	 * @param processorURI
	 * @return true if a Core is user by a Processor according to informations stocked about resources.
	 */
	public boolean isCoreUsed(CoreInfo coreInfo, String processorURI) {
		for (AllocatedCore allocatedCore : allocatedCores)
			if(allocatedCore.processorURI.equals(processorURI) && coreInfo.getCoreURI()==allocatedCore.coreNo) 
				return true; 
			return false;
	}
	/**
	 * 
	 * @param coreInfo
	 * @param processorURI
	 * @return Core if it's used by the processor else null
	 */
	public AllocatedCore getCore(CoreInfo coreInfo, String processorURI) {
		for(AllocatedCore allocatedCore : allocatedCores)
			if(coreInfo.getCoreURI()==allocatedCore.coreNo && allocatedCore.processorURI.equals(processorURI))
				return allocatedCore;
		return null;
	}
	/**
	 * add a Core to the list
	 * @param allocatedCore
	 */
	public void addCore(AllocatedCore allocatedCore) {
		this.allocatedCores.add(allocatedCore);
	}
	/**
	 * Add list of Cores
	 * @param cores
	 */
	public void addManyCores(AllocatedCore[] cores) {
		for(AllocatedCore core: cores)
			this.allocatedCores.add(core);
	}
	/**
	 * Remove a Core
	 * @param core
	 */
	public void removeCore(AllocatedCore core) {
				allocatedCores.remove(core);
	}
	//================================================
	
	public String getVmURI() {
		return vmURI;
	}


	public void setVmURI(String vmURI) {
		this.vmURI = vmURI;
	}


	public String getComputerURI() {
		return computerURI;
	}


	public void setComputerURI(String computerURI) {
		this.computerURI = computerURI;
	}
	
	

}

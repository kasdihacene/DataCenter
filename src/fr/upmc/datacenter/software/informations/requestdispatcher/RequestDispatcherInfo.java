package fr.upmc.datacenter.software.informations.requestdispatcher;

import java.util.LinkedHashMap;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
import fr.upmc.datacenter.software.informations.computers.CoreInfo;

/**
 * 
 * @author Hacene KASDI
 * @version 28.12.2017.HK
 * 
 * This class <code>RequestDispatcherInfo</code> represents the abstraction of 
 * a <code>RequestDispatcher</code> its stores informations about <code>ApplicationVMInfo</code>
 * and can inspect cores resources, availability this reference can ask for Adding, 
 * Removing <code>Core</code> on the <code>Processor</code> of an <code>ApplicationVM</code>
 */
public class RequestDispatcherInfo {
	/** the URI of the RequestDispatcher   		*/
	private String requestDispatcherURI;
	/** MAP of vmURI and ApplicationVMInfo 		*/
	private LinkedHashMap<String, ApplicationVMInfo>  vmInformations;
	/** Number of ApplicationVMInfo created 	*/
	private int nbVM;
	
	public RequestDispatcherInfo(String requestDispatcherURI) {
		super();
		this.requestDispatcherURI = requestDispatcherURI;
		this.vmInformations = new LinkedHashMap<String, ApplicationVMInfo>();
		this.nbVM = 0;
	}

	//=====================================================
	//					Manipulating ApplicationVM
	//=====================================================
	/**
	 * Adding an ApplicationVM and allocate Cores
	 * @param vmURI
	 * @param computerURI
	 * @param allocatedCores
	 */
	public void addApplicationVM(String vmURI, String computerURI, AllocatedCore[] allocatedCores) {
				ApplicationVMInfo applicationVMInfo = new ApplicationVMInfo(vmURI, computerURI);
				applicationVMInfo.addManyCores(allocatedCores);
				vmInformations.put(vmURI, applicationVMInfo);
				nbVM++;
	}
	/**
	 * remove an ApplicationVM
	 * @param vmURI
	 */
	public void removeApplicationVM(String vmURI) {
		vmInformations.remove(vmURI);
	}
	/**
	 * 
	 * @param vmURI
	 * @return an ApplicationVM Informations
	 */
	public ApplicationVMInfo getApplicationVMInformation(String vmURI) {
		return vmInformations.get(vmURI);
	}
	/**
	 * Delegate the addition of a core to the <code>ApplicationVMInfo</code>
	 * @param core
	 * @param vmURI
	 */
	public void addCore(AllocatedCore core, String vmURI) {
		ApplicationVMInfo applicationVMInfo=vmInformations.get(vmURI);
		applicationVMInfo.addCore(core);
	}
	/**
	 * delegate the removing of the core to the <code>ApplicationVMInfo</code>
	 * @param core
	 * @param vmURI
	 */
	public void removeCore(AllocatedCore core,String vmURI) {
		ApplicationVMInfo applicationVMInfo=vmInformations.get(vmURI);
		applicationVMInfo.removeCore(core);
	}
	/**
	 * 
	 * @param coreInfo
	 * @param processorURI
	 * @return the URI of the ApplicationVM who uses this core
	 */
	public String whoUsesCore(CoreInfo coreInfo, String processorURI) {
		for(String vmURI : vmInformations.keySet()) {
			if(vmInformations.get(vmURI).isCoreUsed(coreInfo, processorURI))
				return vmURI;
		}
		return null;
	}
	/**
	 * 
	 * @param coreInfo
	 * @param processorURI
	 * @return table of allocatedCores
	 */
	public AllocatedCore[] getAllocatedCore(CoreInfo coreInfo,String processorURI) {
		AllocatedCore[] allocatedCores=new AllocatedCore[0];
		int i=0;
		for(String vmURI : vmInformations.keySet()) {
			if (vmInformations.get(vmURI).isCoreUsed(coreInfo, processorURI)) {
				allocatedCores[i]=vmInformations.get(vmURI).getCore(coreInfo, processorURI);
			}
		}
		return allocatedCores;
	}
	//=====================================================
	
	
	public String getRequestDispatcherURI() {
		return requestDispatcherURI;
	}

	public void setRequestDispatcherURI(String requestDispatcherURI) {
		this.requestDispatcherURI = requestDispatcherURI;
	}

	public LinkedHashMap<String, ApplicationVMInfo> getAllVmInformation() {
		return vmInformations;
	}

	public void setVmInformations(LinkedHashMap<String, ApplicationVMInfo> vmInformations) {
		this.vmInformations = vmInformations;
	}

	public int getNbVMCreated() {
		return nbVM;
	}

	public void setNbVM(int nbVM) {
		this.nbVM = nbVM;
	}
	
	
	
	
	

}

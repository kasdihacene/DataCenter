package fr.upmc.datacenter.software.step2.requestresourcevm.interfaces;

/**
 * 
 * @author Hacene KASDI
 * @version 26.12.17
 * 
 * The implementation of this interface <code>RequestResourceVMHandlerI</code> allows 
 * to handle a <code>RequestVMI</code>
 *
 */
public interface RequestResourceVMHandlerI {
	/**
	 * 
	 * @param requestVMI : request to add an ApplicationVM
	 * @throws Exception
	 */
	public void addVMApplication(RequestVMI requestVMI) throws Exception;
	/**
	 * 
	 * @param requestVMI : request to remove an Applcation VM
	 * @throws Exception
	 */
	public void removeVMAppication(RequestVMI requestVMI) throws Exception;
	/**
	 * 
	 * @param requestVMI
	 * @throws Exception
	 */
	public void removeAVMWhenEnds(RequestVMI requestVMI) throws Exception;

}

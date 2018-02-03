package fr.upmc.datacenter.software.step2.requestresourcevm.interfaces;
/**
 * @author Hacene KASDI
 * @version 26.12.17
 *
 * This interface allows asking to Add or Remove an ApplicationVM {@link RequestVMI}}
 */
public interface RequestResourceVMI {
	/**
	 * 
	 * @param requestVMI : request to add an ApplicationVM
	 * @throws Exception
	 */
	public void requestAddVM(RequestVMI requestVMI) throws Exception;
	/**
	 * 
	 * @param requestVMI : request to remove an Applcation VM
	 * @throws Exception
	 */
	public void requestRemoveVM(RequestVMI requestVMI) throws Exception;
	
	public void requestRemoveAVMEnded(RequestVMI requestVMI) throws Exception;

}

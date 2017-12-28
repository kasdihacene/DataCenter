package fr.upmc.datacenter.software.step2.requestresourcevm.interfaces;

import java.io.Serializable;

/**
 * 
 * @author Hacene KASDI
 * @version 26.12.17
 * 
 * The implementation of this class used to transport information
 * between the component <code>ResourceInspector</code> and the component
 * <code>RequestDispatcher</code>, used to ask for adding or removing <code>AppplicationVM</code>
 *
 */
public interface RequestVMI extends Serializable {

	/**
	 * 
	 * @return URI of an ApplicationVM
	 */
	public String getURIVM();
	/**
	 * 
	 * @return URI of an ApplicationContainer
	 */
	public String getURIApplication();
	/**
	 * set URI for an ApplicationVM
	 * @param URI
	 */
	public void setURIVM(String URI);
	/**
	 * set URI for an ApplicationContainer
	 * @param URI
	 */
	public void setURIApplication(String URI);
}

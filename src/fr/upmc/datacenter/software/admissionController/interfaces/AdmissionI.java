package fr.upmc.datacenter.software.admissionController.interfaces;

import java.io.Serializable;

import fr.upmc.components.cvm.AbstractCVM;

public interface AdmissionI extends Serializable {

	/**
	 * @return True if the execution of the application is allowed false otherwise
	 */
	boolean isAllowed() throws Exception;
	
	/**
	 * Allow the application execution 
	 * @param allowed
	 */
	void setAllowed(boolean allowed) throws Exception;
	
	public String getApplicationURI() throws Exception;
	public void setApplicationURI(String uriApp) throws Exception;
	
	public String getAdmissionNotificationInboundPortURI() throws Exception;
	public void setAdmissionNotificationInboundPortURI(String anipURI) throws Exception;
	
	public String getAdmissionControllerInboundPortURI() throws Exception;
	public void setAdmissionControllerInboundPortURI(String acipURI) throws Exception;
	
	public AbstractCVM getAbstractCVM() throws Exception;
	public void setAbstractCVM(AbstractCVM abstractCVM) throws Exception;
	
	public void setRequestSubmissionInboundPortRD(String rsip) throws Exception;
	public String getRequestSubmissionInboundPortRD() throws Exception;

}

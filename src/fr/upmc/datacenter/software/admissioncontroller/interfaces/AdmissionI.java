package fr.upmc.datacenter.software.admissioncontroller.interfaces;

import java.io.Serializable;

public interface AdmissionI extends Serializable {

	/**
	 * @return True if the execution of the application is allowed false otherwise
	 */
	boolean isAllowed();
	
	/**
	 * Allow the application execution 
	 * @param allowed
	 */
	void setAllowed(boolean allowed);
	
	public String getApplicationURI();
	public void setApplicationURI(String uriApp);
	
	public int getAVMNumber();
	public void setAVMNumber(int avmNumber);
	
	public String getAdmissionNotificationInboundPortURI();
	public void setAdmissionNotificationInboundPortURI(String anipURI);
	
	public String getAdmissionControllerInboundPortURI();
	public void setAdmissionControllerInboundPortURI(String acipURI);
	
	public void setRequestSubmissionInboundPortRD(String rsip);
	public String getRequestSubmissionInboundPortRD();

}

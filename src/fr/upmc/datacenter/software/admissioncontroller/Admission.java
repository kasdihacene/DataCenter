package fr.upmc.datacenter.software.admissioncontroller;

import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;

/**
 * This class <code>Admission</code> implements the interface <code>AdmissionI</code> witch offers the services that we transit 
 * between the components <code>ApplicationContainer</code> and the <code>AdmissionController</code>, this interface allows us to update the state 
 * of the components using the information collected in the transaction.  
 * 
 * @author Hacene KASDI & Marc REN
 * @version 2012.10.20.HK
 */
public class Admission implements AdmissionI{

	
	private static final long serialVersionUID = 1L;
	// A boolean variable to update if there is any available resource 
	private boolean isAllow;
	// URI of the ApplicationContainer
	private String applicationURI;
	// Number of AVM requested
	private int avmNumber = 2;
	// URI of the admission notification port 
	private String admissionNotificationInboundPortURI;
	// URI of the admission controller port
	private String admissionControllerInboundPortURI;
	
	// URI of the Request Dispatcher
	private String RequestSubmissionInboundPortRD;

	/**
	 * Constructor of the Admission
	 * @param abstractCVM
	 * @param admissionNotificationInboundPortURI
	 * @param admissionControllerInboundPortURI
	 */
	public Admission(
			String admissionNotificationInboundPortURI,
			String admissionControllerInboundPortURI) {
		super();
		this.admissionNotificationInboundPortURI = admissionNotificationInboundPortURI;
		this.admissionControllerInboundPortURI = admissionControllerInboundPortURI;
		
	}

/**
 * Getters and Setters
 */

	@Override
	public boolean isAllowed(){
		return isAllow;
	}

	@Override
	public void setAllowed(boolean allowed){
		this.isAllow=allowed;
		
	}

	@Override
	public String getApplicationURI(){
		return applicationURI;
	}

	@Override
	public void setApplicationURI(String uriApp){
		this.applicationURI=uriApp;
	}
	
	@Override
	public int getAVMNumber() {
		return avmNumber;
	}

	@Override
	public void setAVMNumber(int avmNumber) {
		this.avmNumber = avmNumber;
	}
	
	@Override
	public String getAdmissionNotificationInboundPortURI(){
		return admissionNotificationInboundPortURI;
	}

	@Override
	public void setAdmissionNotificationInboundPortURI(String anipURI){
		this.admissionNotificationInboundPortURI=anipURI;
		
	}

	@Override
	public String getAdmissionControllerInboundPortURI(){
		return admissionControllerInboundPortURI;
	}

	@Override
	public void setAdmissionControllerInboundPortURI(String acipURI){
		this.admissionControllerInboundPortURI=acipURI;
	}

	@Override
	public void setRequestSubmissionInboundPortRD(String rsip){
		RequestSubmissionInboundPortRD = rsip;
		
	}

	@Override
	public String getRequestSubmissionInboundPortRD(){
		return RequestSubmissionInboundPortRD;
	}

}

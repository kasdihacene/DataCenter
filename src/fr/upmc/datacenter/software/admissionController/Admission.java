package fr.upmc.datacenter.software.admissionController;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;

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
	// URI of the admission notification port 
	private String admissionNotificationInboundPortURI;
	// URI of the admission controller port
	private String admissionControllerInboundPortURI;
	// the main component to deploy the created components 
	private AbstractCVM abstractCVM;
	
	// URI of the Request Dispatcher
	private String RequestSubmissionInboundPortRD;

	/**
	 * Constructor of the Admission
	 * @param abstractCVM
	 * @param admissionNotificationInboundPortURI
	 * @param admissionControllerInboundPortURI
	 */
	public Admission(
			AbstractCVM abstractCVM,
			String admissionNotificationInboundPortURI,
			String admissionControllerInboundPortURI) {
		super();
		this.admissionNotificationInboundPortURI = admissionNotificationInboundPortURI;
		this.admissionControllerInboundPortURI = admissionControllerInboundPortURI;
		this.abstractCVM=abstractCVM;
		
	}

/**
 * Getters and Setters
 */

	@Override
	public boolean isAllowed() throws Exception {
		return isAllow;
	}

	@Override
	public void setAllowed(boolean allowed) throws Exception {
		this.isAllow=allowed;
		
	}

	@Override
	public String getApplicationURI() throws Exception {
		return applicationURI;
	}

	@Override
	public void setApplicationURI(String uriApp) throws Exception {
		this.applicationURI=uriApp;
	}

	@Override
	public String getAdmissionNotificationInboundPortURI() throws Exception {
		return admissionNotificationInboundPortURI;
	}

	@Override
	public void setAdmissionNotificationInboundPortURI(String anipURI) throws Exception {
		this.admissionNotificationInboundPortURI=anipURI;
		
	}

	@Override
	public String getAdmissionControllerInboundPortURI() throws Exception {
		return admissionControllerInboundPortURI;
	}

	@Override
	public void setAdmissionControllerInboundPortURI(String acipURI) throws Exception {
		this.admissionControllerInboundPortURI=acipURI;
	}



	@Override
	public AbstractCVM getAbstractCVM() throws Exception {
		return abstractCVM;
	}



	@Override
	public void setAbstractCVM(AbstractCVM abstractCVM) throws Exception {
		this.abstractCVM=abstractCVM;
	}



	@Override
	public void setRequestSubmissionInboundPortRD(String rsip) throws Exception {
		RequestSubmissionInboundPortRD = rsip;
		
	}



	@Override
	public String getRequestSubmissionInboundPortRD() throws Exception {
		return RequestSubmissionInboundPortRD;
	}



}

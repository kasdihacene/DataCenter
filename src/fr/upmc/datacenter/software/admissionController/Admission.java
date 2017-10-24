package fr.upmc.datacenter.software.admissionController;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;

public class Admission implements AdmissionI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isAllow;
	private String applicationURI;
	private String admissionNotificationInboundPortURI;
	private String admissionControllerInboundPortURI;
	private AbstractCVM abstractCVM;
	
	private String RequestSubmissionInboundPortRD;

	public Admission(
			AbstractCVM abstractCVM,
			String applicationURI, 
			String admissionNotificationInboundPortURI,
			String admissionControllerInboundPortURI) {
		super();
		this.applicationURI = applicationURI;
		this.admissionNotificationInboundPortURI = admissionNotificationInboundPortURI;
		this.admissionControllerInboundPortURI = admissionControllerInboundPortURI;
		
		this.abstractCVM=abstractCVM;
		
	}



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

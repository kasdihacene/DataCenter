package fr.upmc.datacenter.software.step2.requestresourcevm;

import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestVMI;

/**
 * 
 * @author Hacene KASDI
 * @version 26.12.17
 *
 */
public class RequestVM implements RequestVMI {

	private static final long serialVersionUID = 1122617L;

	private String vmURI;
	private String appContainerURI;
	
	public RequestVM(String vmURI, String appContainerURI) {
		super();
		this.vmURI = vmURI;
		this.appContainerURI = appContainerURI;
	}

	@Override
	public String getURIVM() {
		return vmURI;
	}

	@Override
	public String getURIApplication() {
		return appContainerURI;
	}

	@Override
	public void setURIVM(String URI) {
		this.vmURI=URI;
	}

	@Override
	public void setURIApplication(String URI) {
		this.appContainerURI=URI;
	}

}

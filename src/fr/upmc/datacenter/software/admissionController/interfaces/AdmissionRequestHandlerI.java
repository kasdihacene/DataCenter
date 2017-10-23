package fr.upmc.datacenter.software.admissionController.interfaces;

/**
 * <p><strong>Description</string></p>
 * 
 * this interface <code>AdmissionRequestHandlerI</code> define the component internal services 
 * to receive the requests from costumers and accept or refuse the hosting according the 
 * available resources ( Computers )
 * 
 * @author Hacene Kasdi & Marc REN
 * @version 2012.10.20.HK
 */
public interface AdmissionRequestHandlerI {

	/**
	 * 
	 * @param uri
	 * @throws Exception
	 */
	public void inspectResources(String uri) throws Exception;
	
	/**
	 * 
	 * @param uri
	 * @throws Exception
	 */
	public void inspectResourcesAndNotifiy(String uri) throws Exception;
}

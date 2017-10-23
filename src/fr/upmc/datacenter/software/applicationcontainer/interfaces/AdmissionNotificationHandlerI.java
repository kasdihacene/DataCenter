package fr.upmc.datacenter.software.applicationcontainer.interfaces;

public interface AdmissionNotificationHandlerI {

	/**
	 * 
	 * @param uri
	 * @throws Exception
	 */
	public void allowOrRefuseAdmissionNotification(String uri) throws Exception;
	
}

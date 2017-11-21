package fr.upmc.datacenter.software.applicationcontainer.interfaces;

import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;
/**
 * 
 * @author Hacene
 *
 */
public interface AdmissionNotificationHandlerI {

	/**
	 * 
	 * @param uri
	 * @throws Exception
	 */
	public void allowOrRefuseAdmissionNotification(AdmissionI admission) throws Exception;
	
}

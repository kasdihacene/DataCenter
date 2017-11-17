package fr.upmc.datacenter.software.applicationcontainer.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;

/**
 * 
 * @author Hacene KASDI & Marc REN
 * @version 2017.10.20.HK
 *
 *
 *THE INTERFACE <code>AdmissionNotificationI</code> RECEIVE THE NOTIFICATION TO START EXECUTION OF APPLICATION
 */
public interface AdmissionNotificationI 
		extends OfferedI, 
				RequiredI {
	/**
	 * 
	 * @param uri
	 * @throws Exception
	 */
	public void notifyAdmissionNotification(AdmissionI admission) throws Exception;
	

}


package fr.upmc.datacenter.software.admissioncontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
/**
 * 
 * @author Hacene KASDI & Marc REN
 * @version 2017.10.20.HK
 *
 *
 *THE INTREFACE <code>AdmissionRequestI</code> RECEIVE THE REQUEST TO HOST THE APPLICATIONS
 */
public interface AdmissionRequestI 
		extends OfferedI, 
				RequiredI {
	/**
	 * 
	 * @param uri
	 * @throws Exception
	 */
	public void askForHost( AdmissionI admission) throws Exception;
	
	/**
	 * 
	 * @param uri
	 * @throws Exception
	 */
	public void askForHostAndWaitResponse( AdmissionI admission) throws Exception;

	
}

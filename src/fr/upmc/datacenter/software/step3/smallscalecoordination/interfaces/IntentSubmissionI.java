package fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>IntentSubmissionI</code> defines the component service to submit Intent
 * 
 * <p><strong>Description</strong></p>
 * 
 * @author jaunecitron
 *
 */

public interface IntentSubmissionI extends OfferedI, RequiredI{
	
	/**
	 * Submit an intent
	 * @param intent
	 * @throws Exception 
	 */
	public void submitIntent(IntentI intent) throws Exception;
}

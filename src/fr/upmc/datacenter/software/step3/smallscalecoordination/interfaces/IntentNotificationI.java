package fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>IntentNotificationI</code> defines the component service to notify that intent received
 * and what 
 * 
 * <p><strong>Description</strong></p>
 * 
 * @author jaunecitron
 *
 */
public interface IntentNotificationI extends OfferedI, RequiredI{
	
	public void submitIntentNotification(IntentI previousIntent, IntentI intent) throws Exception;
	
}

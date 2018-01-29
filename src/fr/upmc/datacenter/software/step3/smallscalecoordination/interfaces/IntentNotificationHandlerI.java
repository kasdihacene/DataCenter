package fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces;

public interface IntentNotificationHandlerI {

	public void acceptIntentNotification(IntentI previousIntent, IntentI intent) throws Exception;
	
}

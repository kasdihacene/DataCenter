package fr.upmc.datacenter.software.step3.smallscalecoordination.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentNotificationI;

public class IntentNotificationConnector extends AbstractConnector implements IntentNotificationI{

	@Override
	public void submitIntentNotification(IntentI previousIntent, IntentI intent) throws Exception {
		((IntentNotificationI) this.offering).submitIntentNotification(previousIntent, intent);
	}
	
}

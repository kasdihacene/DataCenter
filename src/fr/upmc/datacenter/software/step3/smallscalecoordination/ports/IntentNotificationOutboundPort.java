package fr.upmc.datacenter.software.step3.smallscalecoordination.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentNotificationI;

public class IntentNotificationOutboundPort extends AbstractOutboundPort implements IntentNotificationI{
	
	public static String SUFFIX = "-inop";
	
	public IntentNotificationOutboundPort(ComponentI owner) throws Exception{
		super(IntentNotificationI.class, owner);
	}
	
	public IntentNotificationOutboundPort(String uri, ComponentI owner) throws Exception{
		super(uri, IntentNotificationI.class, owner);
		
		assert uri != null;
	}

	@Override
	public void submitIntentNotification(IntentI previousIntent, IntentI intent) throws Exception{
		((IntentNotificationI) this.connector).submitIntentNotification(previousIntent, intent);
	}
	
}

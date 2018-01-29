package fr.upmc.datacenter.software.step3.smallscalecoordination.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentNotificationHandlerI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentNotificationI;

public class IntentNotificationInboundPort extends AbstractInboundPort implements IntentNotificationI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String SUFFIX = "-inip";

	public IntentNotificationInboundPort(ComponentI owner) throws Exception {
		super(IntentNotificationI.class, owner);
		
		assert owner instanceof IntentNotificationHandlerI;
	}

	public IntentNotificationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, IntentNotificationI.class, owner);
		
		assert owner instanceof IntentNotificationHandlerI;
		assert uri != null;
	}

	@Override
	public void submitIntentNotification(IntentI previousIntent, IntentI intent) throws Exception {
		final IntentNotificationHandlerI inh = (IntentNotificationHandlerI) this.owner;
		
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						inh.acceptIntentNotification(previousIntent, intent);
						return null;
					}
				});
	}

}

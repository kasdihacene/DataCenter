package fr.upmc.datacenter.software.step3.smallscalecoordination.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentSubmissionHandlerI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentSubmissionI;

public class IntentSubmissionInboundPort extends AbstractInboundPort implements IntentSubmissionI{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String SUFFIX = "-isip";
	
	public IntentSubmissionInboundPort(ComponentI owner) throws Exception {
		super(IntentSubmissionI.class, owner);
		
		assert owner instanceof IntentSubmissionHandlerI;
	}
	
	public IntentSubmissionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, IntentSubmissionI.class, owner);
		
		assert owner instanceof IntentSubmissionHandlerI;
		assert uri != null;
	}

	@Override
	public void submitIntent(IntentI intent) throws Exception{
		final IntentSubmissionHandlerI ish = (IntentSubmissionHandlerI) this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						ish.acceptIntent(intent);
						return null;
					}
				});
	}
}

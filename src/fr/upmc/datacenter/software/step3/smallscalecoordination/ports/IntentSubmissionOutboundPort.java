package fr.upmc.datacenter.software.step3.smallscalecoordination.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.step3.smallscalecoordination.connectors.IntentSubmissionConnector;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentSubmissionI;

public class IntentSubmissionOutboundPort extends AbstractOutboundPort implements IntentSubmissionI {

	public static String SUFFIX = "-isop";

	public IntentSubmissionOutboundPort(ComponentI owner) throws Exception {
		super(IntentSubmissionI.class, owner);
	}

	public IntentSubmissionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, IntentSubmissionI.class, owner);

		assert uri != null;
	}
	
	@Override
	public void submitIntent(IntentI intent) throws Exception {
		((IntentSubmissionI) this.connector).submitIntent(intent);
	}
}

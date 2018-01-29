package fr.upmc.datacenter.software.step3.smallscalecoordination.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentSubmissionI;

public class IntentSubmissionConnector extends AbstractConnector implements IntentSubmissionI{
	
	@Override
	public void submitIntent(IntentI intent) throws Exception {
		((IntentSubmissionI) this.offering).submitIntent(intent);
	}

}

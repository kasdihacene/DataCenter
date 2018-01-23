package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.CoordinationLargeScaleI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;

/**
 * Coordination for Large scale connector
 * 
 * @author	<a href="mailto:hacene.kasdi.p6">Hacene KASDI</a>
 * @version 21.01.2018
 * <p>Created on : January 21, 2018</p>
 *
 */

public class CoordinationLargeScaleConnector 	extends AbstractConnector 
												implements CoordinationLargeScaleI {

	@Override
	public void submitChip(TransitTokenI tokenI) throws Exception {
		((CoordinationLargeScaleI)this.offering).submitChip(tokenI);
	}

}

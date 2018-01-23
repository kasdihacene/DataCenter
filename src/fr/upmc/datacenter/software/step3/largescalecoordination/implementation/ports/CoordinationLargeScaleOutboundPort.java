package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.CoordinationLargeScaleI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;

/**
 * 
 * @author	<a href="mailto:hacene.kasdi.p6">Hacene KASDI</a>
 * @version 21.01.2018
 * <p>Created on : January 21, 2018</p>
 */
public class CoordinationLargeScaleOutboundPort extends AbstractOutboundPort 
												implements CoordinationLargeScaleI {

	public CoordinationLargeScaleOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,CoordinationLargeScaleI.class, owner);
	}

	@Override
	public void submitChip(TransitTokenI tokenI) throws Exception {
		((CoordinationLargeScaleI)this.connector).submitChip(tokenI);
	}

}

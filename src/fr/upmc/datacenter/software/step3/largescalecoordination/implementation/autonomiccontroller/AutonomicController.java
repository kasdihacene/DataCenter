package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.autonomiccontroller;

import fr.upmc.datacenter.software.step2.adapter.AdapterRequestDispatcher;

/**
 * 
 * @author	<a href="mailto:hacene.kasdi.p6">Hacene KASDI</a>
 * @version 25.01.2018
 * <p>Created on : January 25, 2018</p>
 * @see {@link AdapterRequestDispatcher}
 *
 */
public class AutonomicController extends AdapterRequestDispatcher {

	public AutonomicController	(String riURI, 
								String applicationURI) throws Exception {
		
		super(riURI, applicationURI);
	}

}

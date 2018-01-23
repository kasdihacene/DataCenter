package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces;

/**
 * 
 * @author	<a href="mailto:hacene.kasdi.p6">Hacene KASDI</a>
 * @version 21.01.2018
 * <p>Created on : January 21, 2018</p>
 */
public interface CoordinationLargeScaleI {
	
	/**
	 * This method allows the sending of the Available URIs in 
	 * the network topology using simple connected ports.
	 * 
	 * @param tokenI
	 * @throws Exception
	 */
	public void submitChip(TransitTokenI tokenI) throws Exception;

}

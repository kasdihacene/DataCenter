package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author	<a href="mailto:hacene.kasdi.p6">Hacene KASDI</a>
 * @version 21.01.2018
 * <p>Created on : January 21, 2018</p>
 * 
 *
 */
public interface TransitTokenI extends Serializable {

	/**
	 * 
	 * @return the transmitter of the chip
	 * @throws Exception
	 */
	public String getSender() throws Exception;
	/**
	 * 
	 * @return the receiver of the chip
	 * @throws Exception
	 */
	public String getReceiver() throws Exception;
	/**
	 * 
	 * @return the list of available AVM URIs {@code fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable}
	 * @throws Exception
	 */
	public LinkedList<String> getListURIs() throws Exception;
}

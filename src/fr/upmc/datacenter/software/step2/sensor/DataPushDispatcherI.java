package fr.upmc.datacenter.software.step2.sensor;

import java.util.LinkedList;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;
import fr.upmc.datacenter.software.step2.adapter.InfoRequestResponse;
/**
 * 
 * @author Hacene KASDI
 * @version 01.01.2018
 * 
 * The implementation of this interface <code>DataPushDispatcherI</code> allows to get 
 * the execution average time of the amount of requests received by <code>RequestDispatcherComponent</code>
 * and data will be pushed to the adapter <code>AdapterComponent</code> every interval of time.
 *
 */
public interface DataPushDispatcherI 
					extends DataOfferedI.DataI, 
							DataRequiredI.DataI, 
							TimeStampingI {
	public String getApplicationURI() throws Exception;
	/**
	 * 
	 * @param avmURI
	 * @return the execution average time of quantity of requests on the time intervall
	 * @throws Exception
	 */
	public double getExecutionAverage() throws Exception;
	/**
	 * 
	 * @return list of AVM information used by the Request Disaptcher
	 * @throws Exception
	 */
	public LinkedList<InfoRequestResponse> getListStatsAVMs() throws Exception;

}

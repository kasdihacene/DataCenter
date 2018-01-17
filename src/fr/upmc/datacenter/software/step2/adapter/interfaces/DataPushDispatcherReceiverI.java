package fr.upmc.datacenter.software.step2.adapter.interfaces;

import fr.upmc.datacenter.software.step2.sensor.DataPushDispatcherI;

/**
 * 
 * @author Hacene KASDI
 * @version 01.01.2018.HK
 * 
 *
 */
public interface DataPushDispatcherReceiverI {

	/**
	 * Receives data pushed by Request Dispatcher
	 * @param dataPushDispatcherI
	 * @throws Exception
	 */
	public void receivePushedData(DataPushDispatcherI dataPushDispatcherI) throws Exception;
}

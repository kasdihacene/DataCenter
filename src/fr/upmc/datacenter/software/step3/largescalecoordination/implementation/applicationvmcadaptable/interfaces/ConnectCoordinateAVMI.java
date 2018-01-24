package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.interfaces;

import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherComponent;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;

public interface ConnectCoordinateAVMI {
	
	/**
	 * Connect the {@link ApplicationVMAdaptable} with the {@link RequestDispatcherComponent}
	 * after creating the AVM in order to use dynamically the AVMs after creating all AVMs 
	 * on the beginning of the process.
	 * 
	 * @param uriDispatcher
	 * @throws Exception
	 */
	public void connectAVMwithSubmissioner(String uriDispatcher) throws Exception;

}

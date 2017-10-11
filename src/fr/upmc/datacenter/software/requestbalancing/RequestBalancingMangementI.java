package fr.upmc.datacenter.software.requestbalancing;

import fr.upmc.datacenter.software.interfaces.RequestI;

public interface RequestBalancingMangementI {

	/**
	 * COUNT NUMBER OF REQUESTS
	 */
	public int getNumberRequest();
	
	/**
	 * GET URI OF THE REQUEST
	 */
	public String getRequestURI(RequestI ri);
}

package fr.upmc.datacenter.software.step2.adapter;

import java.util.HashMap;

public class InfoRequestResponse {
	
	private double average;
	private String avmURI;
	private int nbRequestProcessed=0;
	private long execTimeAccum=0;
	private HashMap<String, Long> listArrivedRequests;
	
	
	public InfoRequestResponse(String avmURI) {
		super();
		this.avmURI = avmURI;
		listArrivedRequests=new HashMap<String,Long>();
	}
	
	
	public String getAvmURI() {
		return avmURI;
	}


	public int getNbRequestProcessed() {
		return nbRequestProcessed;
	}


	/**
	 * Add an arrival request treated by the current AVM
	 * @param requestURI
	 */
	public void addArrivedRequest(String requestURI) {
		listArrivedRequests.put(requestURI,System.currentTimeMillis());
	}
	/**
	 * 
	 * @param requestURI
	 * @throws Exception
	 */
	public void addTerminationRequest(String requestURI) throws Exception {
		if(!listArrivedRequests.containsKey(requestURI)) {
			throw new Exception("Request : "+requestURI+" not found on arrivals requests !");
		}
		long requestExecTime = System.currentTimeMillis() - listArrivedRequests.get(requestURI);
		execTimeAccum=execTimeAccum+requestExecTime;
		nbRequestProcessed++;
	}
	/**
	 * 
	 * @return the average of processed requests
	 */
	public double calculateAverage() {
		this.average = execTimeAccum/nbRequestProcessed;
		return average;
	}
	/**
	 * reset all variable to prepare new calculations for push
	 */
	public void resetMechanism() {
		nbRequestProcessed	= 0;
		execTimeAccum		= 0;
		average				= 0;
		listArrivedRequests.clear();
	}
	
	
}

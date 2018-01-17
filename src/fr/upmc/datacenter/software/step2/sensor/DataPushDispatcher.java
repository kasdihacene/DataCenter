package fr.upmc.datacenter.software.step2.sensor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import fr.upmc.datacenter.software.step2.adapter.InfoRequestResponse;

/**
 * 
 * @author Hacene KASDI
 * @version 05.01.2018.HK
 * 
 * This class <code>InfoRequestResponse</code> stores the information about the received
 * request from the <code>RequestGenerator</code> and the <code>ApplicationVM</code>
 *
 */
public class DataPushDispatcher implements DataPushDispatcherI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 101012018L;
	private String URI;
	private long arrivalTime;
	private double average ;
	private LinkedList<InfoRequestResponse> avmList;
	
	
	
	
	public DataPushDispatcher(String URI, double average, LinkedList<InfoRequestResponse> listStatsAVMs) {
		super();
		this.URI 		= URI;
		this.average 	= average;
		avmList			= listStatsAVMs;
		setArrivalTime(System.currentTimeMillis());
	}
	
	public void addAvmInfo(InfoRequestResponse applicationVMInfo) {
		avmList.add(applicationVMInfo);
	}
	
	public String getURI() {
		return URI;
	}
	public void setURI(String requestFromRG) {
		this.URI = requestFromRG;
	}
	
	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	

	/**
	 * return the time at which the state has been gathered in local system
	 * time (currentTimeMillis). in milliseconds.
	 * @return	the time at which the state has been gathered.
	 */
	@Override
	public long getTimeStamp() {
		return arrivalTime;
	}

	/**
	 * return the string representation of the IP address of the host on
	 * which the timestamp has been taken.
	 * @return	the IP address of the host.
	 * @throws UnknownHostException 
	 */
	@Override
	public String getTimeStamperId() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
		}

	/**
	 * return the execution time average of an amount of requests
	 */
	@Override
	public double getExecutionAverage() throws Exception {
		return average;
	}

	/**
	 * return a list of ApplicationVM information used by the RequestDispatcherComponent
	 */
	@Override
	public LinkedList<InfoRequestResponse> getListStatsAVMs() throws Exception {
		return avmList;
	}

	@Override
	public String getApplicationURI() throws Exception {
		return URI;
	}
	
	
	
}

package fr.upmc.datacenter.software.step3.largescalecoordination.implementation;

import java.util.LinkedList;

import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;

/**
 * The implementation of the interface {@link TransitTokenI} this object will
 * be transited on the topology used on the Large scale coordination.
 * 
 * @author	<a href="mailto:hacene.kasdi.p6">Hacene KASDI</a>
 * @version 21.01.2018
 * <p>Created on : January 21, 2018</p>
 * 
 * 
 */
public class TransitToken implements TransitTokenI {
	private static final long serialVersionUID = 1L;
	
	private String sender;
	private String receiver;
	private LinkedList<String> avmURIs;
	
	

	public TransitToken(String sender, String receiver, LinkedList<String> avmURIs) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.avmURIs = avmURIs;
	}

	@Override
	public String getSender() throws Exception {
		return sender;
	}

	@Override
	public String getReceiver() throws Exception {
		return receiver;
	}

	@Override
	public LinkedList<String> getListURIs() throws Exception {
		return avmURIs;
	}

	@Override
	public void addAVM(String uri) throws Exception {
		avmURIs.add(uri);
	}

	@Override
	public void removeAVM(String uri) throws Exception {
		avmURIs.remove(uri);
	}

}

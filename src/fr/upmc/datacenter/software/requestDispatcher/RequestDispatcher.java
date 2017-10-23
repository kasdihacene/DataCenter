package fr.upmc.datacenter.software.requestDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenter.software.requestDispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcher extends AbstractComponent
		implements 
		RequestSubmissionHandlerI,
		RequestNotificationHandlerI, 
		RequestDispatcherManagementI {
	
	public static enum RequestDispatcherPortTypes{
		REQUEST_SUBMISSION, MANAGEMENT
	}
	
	// THE URI OF THE REQUEST DISPATCHER
	protected String 							rdURI;
	
	// A PORT TO RECEIVE REQUESTS PROVIDING FROM REQUEST GENERATOR
	protected RequestSubmissionInboundPort		requestSubmissionInboundPort ;	
	
	// A PORT TO NOTIFY THE REQUEST GENERATOR AT THE END OF THE TASK 
	protected RequestNotificationOutboundPort	requestNotificationOutboundPort ;
	
	// properties
	// Map of avmURI to his request submission outbound port
	protected Map<String, RequestSubmissionOutboundPort> submissionPorts;
	// Coin like fair threads
	protected int coin = 0;
	
	public RequestDispatcher(
			String rdURI, 
			String rsip, 
			String rnop) throws Exception {
		super(1, 1);
		
		/**
		 * PRECONDITIONS 
		 */
		
		assert rdURI != null;
		assert rsip != null;
		assert rnop != null;
		
		
		this.submissionPorts = new HashMap<String, RequestSubmissionOutboundPort>();
		
		// CREATE PORT TO THE COMPONENT
		/**
		 * A PORT TO RECEIVE REQUESTS FROM THE RequestGenerator   O--
		 */
		this.addOfferedInterface(RequestSubmissionI.class);
		this.requestSubmissionInboundPort =
				new RequestSubmissionInboundPort(rsip, this);
		this.addPort(this.requestSubmissionInboundPort);
		this.requestSubmissionInboundPort.publishPort();
		
		/**
		 * A PORT TO NETIFY THE RequestGenerator 				  --C
		 */
		this.addRequiredInterface(RequestNotificationI.class);
		this.requestNotificationOutboundPort =
				new RequestNotificationOutboundPort(rnop, this);
		this.addPort(this.requestNotificationOutboundPort);
		this.requestNotificationOutboundPort.publishPort();
		
		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		

	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		
		System.out.println("-----------------------------");
		
		// submissionPorts : associate avm uri with request dispatcher request submission outbound port
		ArrayList<RequestSubmissionOutboundPort> vmRequestSubmissionPorts = 
				new ArrayList<RequestSubmissionOutboundPort>(this.submissionPorts.values());

		// GET ONE OF THE AVM PORTS (RequestSumissionOutboundPort)
		RequestSubmissionOutboundPort rsopAVM = vmRequestSubmissionPorts.get(coin);
		rsopAVM.submitRequest(r);
		
		// SWITCH THE PORT
		coin = (coin + 1) % vmRequestSubmissionPorts.size();
		System.out.println("------------ coin = "+coin);
		
		System.out.println("-----------------------------");
	}


	@Override
	public int getNumberRequest() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public String getRequestURI(RequestI ri) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void connectAVM(String avmURI, String vmRequestSubmissionInboundPortURI,
			String vmRequestNotificationOutboundPortUri) throws Exception {
		
		// SEND REQUESTS TO THE AVM USING THE PORT OF RequestDispatcher (RequestSubmissionOutboundPort)
		RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(this);
		this.addPort(rsop);
		rsop.publishPort();
		
		// CONNECT THE RequestDispatcher Port (RequestSubmissionOutboundPort) to the AVM Port (RequestSubmissionInboundPort)
		rsop.doConnection(
				vmRequestSubmissionInboundPortURI, 
				RequestSubmissionConnector.class.getCanonicalName());
		
		// ADD THE <KEY VALUE> TO THE COLLECTION OF PORTS <avmURI, RequestSubmissionOutboundPort>
		this.submissionPorts.put(avmURI, rsop);
		
		
		/**
		 * TODO
		 */
		// Create Request Dispatcher request notification inbound port
		RequestNotificationInboundPort rnip = new RequestNotificationInboundPort(this);
		this.addPort(rnip);
		rnip.publishPort();
		// To connect with the avm request notification outbound port
//		 rnip.doConnection(vmRequestNotificationOutboundPortUri, RequestNotificationConnector.class.getCanonicalName());
	}

}

package fr.upmc.datacenter.software.requestDispatcher;

import java.util.LinkedList;
import java.util.Queue;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.interfaces.PushModeControllingI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;

public class RequestDispatcher extends AbstractComponent
		implements RequestSubmissionHandlerI, RequestNotificationHandlerI, PushModeControllingI {
	
	public static enum RequestDispatcherPortTypes{
		REQUEST_SUBMISSION, MANAGEMENT
	}
	
	// THE URI OF THE REQUEST DISPATCHER
	protected String 							rdURI;
	
	// THE QUEUE FOR THE REQUESTS
	protected Queue<TaskI>						taskQueue ;
	
	// A PORT TO RECEIVE REQUESTS PROVIDING FROM REQUEST GENERATOR
	protected RequestSubmissionInboundPort		requestSubmissionInboundPort ;	
	
	// A PORT TO NOTIFY THE REQUEST GENERATOR AT THE END OF THE TASK 
	protected RequestNotificationOutboundPort	requestNotificationOutboundPort ;
	
	// AN OUTBOUND PORT TO SEND REQUESTS TO THE AVM (R_D_OUTBOUNDPORT)
	/**
	 * TODO ADD THE PORT LATER
	 */
	
	
	public RequestDispatcher(String rdURI, String rsip, String rnop) throws Exception {
		super(2, 2);
		
		/**
		 * PRECONDITIONS 
		 */
		
		assert rdURI != null;
		assert rsip != null;
		assert rnop != null;
		
		
		this.taskQueue = new LinkedList<TaskI>() ;
		
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
		 * A PORT TO NETIFY THE RequestGenerator 
		 */
		this.addRequiredInterface(RequestNotificationI.class);
		this.requestNotificationOutboundPort =
				new RequestNotificationOutboundPort(rnop, this);
		this.addPort(this.requestNotificationOutboundPort);
		this.requestNotificationOutboundPort.publishPort();
		
		
	}
	
	
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		

	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopPushing() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
System.out.println("acceptRequestSub");

	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
System.out.println("accept RequestSubAndNotify");
System.out.println(r.getRequestURI());
System.out.println("-----------------------------");

	}

}

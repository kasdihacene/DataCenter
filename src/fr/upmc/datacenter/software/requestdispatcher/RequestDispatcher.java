package fr.upmc.datacenter.software.requestdispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherComponent;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementInboundPort;
/**
 * LAST VERSION OF THE COMPONENT USED ON STEP 1 OF THE PROJECT 
 * @see {@link RequestDispatcherComponent} the new version used for step 2 and step 3
 * 
 * @author Hacene KASDI 
 *
 */
public class RequestDispatcher extends AbstractComponent
		implements RequestSubmissionHandlerI, RequestNotificationHandlerI, RequestDispatcherManagementI {

	public static enum RequestDispatcherPortTypes {
		REQUEST_SUBMISSION, MANAGEMENT
	}

	// THE URI OF THE REQUEST DISPATCHER
	protected String rdURI;

	protected RequestDispatcherManagementInboundPort requestDispatcherManagementInboundPort;

	// A PORT TO RECEIVE REQUESTS PROVIDING FROM REQUEST GENERATOR
	protected RequestSubmissionInboundPort requestSubmissionInboundPort;

	// A PORT TO RECEIVE NOTIFICATION OF ENDING REQUEST
	protected RequestNotificationInboundPort requestNotificationInboundPort;

	// A PORT TO NOTIFY THE REQUEST GENERATOR AT THE END OF THE TASK
	protected RequestNotificationOutboundPort requestNotificationOutboundPort;

	// properties
	// Map of avmURI to his request submission outbound port
	protected Map<String, RequestSubmissionOutboundPort> submissionPorts;
	// Coin like fair threads
	protected int coin = 0;

	protected boolean verbose;

	public RequestDispatcher(String rdURI, String rdmip, String rsip, String rnip, String rnop) throws Exception {
		this(rdURI, rdmip, rsip, rnip, rnop, true);
	}

	public RequestDispatcher(String rdURI, String rdmip, String rsip, String rnip, String rnop, boolean verbose)
			throws Exception {
		super(1, 1);

		/**
		 * PRECONDITIONS
		 */

		assert rdURI != null;
		assert rdmip != null;
		assert rsip != null;
		assert rnip != null;
		assert rnop != null;

		this.verbose = verbose;
		this.rdURI = rdURI;

		this.submissionPorts = new HashMap<String, RequestSubmissionOutboundPort>();

		// CREATE PORT TO THE COMPONENT
		/**
		 * A PORT TO MANGE REQUEST DISPATCHER
		 */
		this.addOfferedInterface(RequestDispatcherManagementI.class);
		this.requestDispatcherManagementInboundPort = new RequestDispatcherManagementInboundPort(rdmip, this);
		this.addPort(this.requestDispatcherManagementInboundPort);
		this.requestDispatcherManagementInboundPort.publishPort();

		/**
		 * A PORT TO RECEIVE REQUESTS FROM THE RequestGenerator O--
		 */
		this.addOfferedInterface(RequestSubmissionI.class);
		this.requestSubmissionInboundPort = new RequestSubmissionInboundPort(rsip, this);
		this.addPort(this.requestSubmissionInboundPort);
		this.requestSubmissionInboundPort.publishPort();

		/**
		 * A PORT TO RECEIVE NOTIFICATION FROM THE RequestGenerator --C
		 */
		this.addOfferedInterface(RequestNotificationI.class);
		this.requestNotificationInboundPort = new RequestNotificationInboundPort(rnip, this);
		this.addPort(requestNotificationInboundPort);
		this.requestNotificationInboundPort.publishPort();

		/**
		 * A PORT TO NOTIFY THE RequestGenerator --C
		 */
		this.addRequiredInterface(RequestNotificationI.class);
		this.requestNotificationOutboundPort = new RequestNotificationOutboundPort(rnop, this);
		this.addPort(this.requestNotificationOutboundPort);
		this.requestNotificationOutboundPort.publishPort();
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		System.out.println("RD :== TERMINATION REQUEST " + r.getRequestURI());
		requestNotificationOutboundPort.notifyRequestTermination(r);
		System.out.println("RD connected : " + requestNotificationOutboundPort.connected());
		System.out.println("RD connected to " + requestNotificationOutboundPort.getClientPortURI());
		System.out.println("RD port uri " + requestNotificationOutboundPort.getPortURI());
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {

	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		acceptRequestSubmissionAndNotify(r, verbose);
	}

	public void acceptRequestSubmissionAndNotify(RequestI r, boolean verbose) throws Exception {

		// submissionPorts : associate avm uri with request dispatcher request
		// submission outbound port
		ArrayList<RequestSubmissionOutboundPort> vmRequestSubmissionPorts = new ArrayList<RequestSubmissionOutboundPort>(
				this.submissionPorts.values());
		ArrayList<String> vmRequestSubmissionURIs = new ArrayList<String>(this.submissionPorts.keySet());

		// GET ONE OF THE AVM PORTS (RequestSumissionOutboundPort)
		RequestSubmissionOutboundPort rsopAVM = vmRequestSubmissionPorts.get(coin);
		rsopAVM.submitRequestAndNotify(r);
		System.out.println(String.format("DISPATCHER <%s>: Request <%s> sent to AVM no.%d <%s>", rdURI,
				r.getRequestURI(), coin, vmRequestSubmissionURIs.get(coin)));

		// SWITCH THE PORT
		coin = (coin + 1) % vmRequestSubmissionPorts.size();

	}

	@Override
	public void connectAVM(String avmURI, String vmRequestSubmissionInboundPortURI) throws Exception {

		// SEND REQUESTS TO THE AVM USING THE PORT OF RequestDispatcher
		// (RequestSubmissionOutboundPort)
		RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(this);
		this.addPort(rsop);
		rsop.publishPort();

		// CONNECT THE RequestDispatcher Port (RequestSubmissionOutboundPort) to the AVM
		// Port (RequestSubmissionInboundPort)
		rsop.doConnection(vmRequestSubmissionInboundPortURI, RequestSubmissionConnector.class.getCanonicalName());

		// ADD THE <KEY VALUE> TO THE COLLECTION OF PORTS <avmURI,
		// RequestSubmissionOutboundPort>
		this.submissionPorts.put(avmURI, rsop);

	}

	@Override
	public void connectNotificationOutboundPort(String notficationInboundPort) throws Exception {
		System.out.println("try to connect notification outbound port with port " + notficationInboundPort);
		this.requestNotificationOutboundPort.doConnection(notficationInboundPort,
				RequestNotificationConnector.class.getCanonicalName());
	}

}

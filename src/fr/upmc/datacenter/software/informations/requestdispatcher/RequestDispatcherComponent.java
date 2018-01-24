package fr.upmc.datacenter.software.informations.requestdispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.interfaces.PushModeControllingI;
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
import fr.upmc.datacenter.software.step2.adapter.InfoRequestResponse;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMHandlerI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.ports.RequestResourceVMInboundPort;
import fr.upmc.datacenter.software.step2.sensor.DataPushDispatcher;
import fr.upmc.datacenter.software.step2.sensor.ports.SensorDispatcherInboundPort;

/**
 * 
 * @author Hacene KASDI
 * @version 28.12.17.HK
 * 
 * The <code>RequestDispatcherComponent</code> references of a new AVM reference affected 
 * to him, receives request providing from <code>RequestGenerator</code> and dispatch 
 * the request using a round-robin policy to current list of <code>ApplicationVMAdaptable</code>
 * 
 * This component is subscribed to a Push service witch allows to send <code>DataI</code>
 * to all clients, in our case the data will be received by <code>AdapterRequestDispatcher</code>
 * start the pushing of data and force the pushing to be done each <code>interval</code> period of time.
 *
 */


public class RequestDispatcherComponent 	extends 	AbstractComponent
								implements 	RequestSubmissionHandlerI,
											RequestNotificationHandlerI,
											RequestResourceVMHandlerI,
											PushModeControllingI{

	private String applicationContainerURI;
	private ArrayList<String> applicationVMList;
	private RequestSubmissionOutboundPort rsop;
	private RequestNotificationInboundPort rnip;
	private RequestResourceVMInboundPort rrvmip;
	protected SensorDispatcherInboundPort sdip;
	
	// statistics about the current Sensor 
	private HashMap<String, InfoRequestResponse> infoStatsAVM;
	private int nbRequests;
	private long execTimeAccum;
	private HashMap<String, String> listRequests;
	private HashMap<String, Long> listExecRequests;
	
	/** future of the task scheduled to push dynamic data.					*/
	protected ScheduledFuture<?>			pushingFuture ;
	
	public RequestDispatcherComponent(String applicationContainerURI) throws Exception {
		super(1,1);
		this.setApplicationContainerURI(applicationContainerURI);
		this.applicationVMList=new ArrayList<String>();
		
		nbRequests		=	0;
		execTimeAccum	=	0L;
		infoStatsAVM					= new HashMap<String,InfoRequestResponse>();
		listRequests 					= new HashMap<String,String>();
		listExecRequests				= new HashMap<String,Long>();
		
		this.addOfferedInterface(RequestResourceVMI.class);
		this.addOfferedInterface(RequestNotificationI.class);
		this.addRequiredInterface(RequestNotificationI.class);
		this.addRequiredInterface(RequestSubmissionI.class);
		this.addOfferedInterface(RequestSubmissionI.class);
		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class);
												
												// APP1-RD_RNOP
		RequestNotificationOutboundPort rnop 	= new RequestNotificationOutboundPort(applicationContainerURI+"_RNOP",this);
		RequestSubmissionInboundPort 	rsip 	= new RequestSubmissionInboundPort(applicationContainerURI+"_RSIP",this);
										rrvmip	= new RequestResourceVMInboundPort(applicationContainerURI+"_RVMIP",this);
										rsop	= new RequestSubmissionOutboundPort(applicationContainerURI+"_RSOP",this);
										rnip	= new RequestNotificationInboundPort(applicationContainerURI+"_RNIP",this);
										sdip	= new SensorDispatcherInboundPort(applicationContainerURI+"_SDIP", this);
										
		this.addPort(rnop);
		this.addPort(rsip);
		this.addPort(rrvmip);
		this.addPort(rsop);
		this.addPort(rnip);
		this.addPort(sdip);
		
		this.sdip.publishPort();
		this.rnip.publishPort();
		this.rrvmip.publishPort();
		this.rsop.publishPort();
		rsip.publishPort();
		rnop.publishPort();
		
	}

	@Override
	public void addVMApplication(RequestVMI requestVMI) throws Exception {
		synchronized (applicationVMList) {
		// Add the AVM URI to the list
		applicationVMList.add(requestVMI.getURIVM());
		}
		// We have to create a new object InfoRequestResponse to store all stats
		// about the request submitted to the given ApplicationVM and to calculate
		// the average execution of queries and the number queries, these informations
		// will be pushed in each time interval
		
		infoStatsAVM.put(requestVMI.getURIVM(), new InfoRequestResponse(requestVMI.getURIVM()));
		System.out.println("+++++++++++++++++++++ NEW AVM CREATED : "+requestVMI.getURIVM());
		
		
		// Connect the current RequestDispatcher with the ApplicationVM (APPx_AMV_x_RSIP)
	    rsop.doConnection(	requestVMI.getURIVM()+"_RSIP", 
	    					RequestSubmissionConnector.class.getCanonicalName());
		    
	}

	@Override
	public void removeVMAppication(RequestVMI requestVMI) throws Exception {
		synchronized (applicationVMList) {
			System.err.println("SIZE LIST AVM BEFORE == "+applicationVMList.size()+"  "+applicationVMList);
			applicationVMList.remove(requestVMI.getURIVM());
			System.err.println("SIZE LIST AVM AFTER == "+applicationVMList.size()+"  "+applicationVMList);
		}
	}

	/**
	 * Received from the AVM when the execution of the RequestI was finished
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		// Get the AVM URI of the arrival notification and store the the arrival time
		String avmURI = listRequests.get(r.getRequestURI());
		System.out.println("TERMINATION : "+r.getRequestURI()+" : "+avmURI);
		infoStatsAVM.get(avmURI).addTerminationRequest(r.getRequestURI());
		execTimeAccum = execTimeAccum + (System.currentTimeMillis() - listExecRequests.get(r.getRequestURI()));
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		
	}
	/**
	 * RoundRobin policy for dispatching request to the ApplicationVM
	 * @param AVMlistURIs
	 * @return the next AVM uri that we should execute the request
	 */
	public String roundRobinAVM(ArrayList<String> AVMlistURIs) {
		AVMlistURIs.add(AVMlistURIs.remove(0));
		return AVMlistURIs.get(0);
	}
	/**
	 * Received from the RequestGenerator, asking the execution of the RequestI
	 */
	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {

			// Connect the ApplicationVM added to the RequestDispatcher using RoundRobin policy
			String avmNextURI = roundRobinAVM(applicationVMList);
			System.out.println("******************** REQUEST : "+r.getRequestURI()+" SUBMITED TO ******* "+avmNextURI);
			
			// Store the arrived request from the Request Generator
			nbRequests++;
			infoStatsAVM.get(avmNextURI).addArrivedRequest(r.getRequestURI());
			listRequests.put(r.getRequestURI(), avmNextURI);
			listExecRequests.put(r.getRequestURI(), System.currentTimeMillis());
			
			rsop.doConnection(avmNextURI+"_RSIP",RequestSubmissionConnector.class.getCanonicalName());
			rsop.submitRequestAndNotify(r);
			rsop.doDisconnection();
	
	}

	public DataPushDispatcher prepareCollectedData() {
			LinkedList<InfoRequestResponse> avmStats = new LinkedList<InfoRequestResponse>();
			for(InfoRequestResponse infoRequestResponse : infoStatsAVM.values()) {
				avmStats.add(infoRequestResponse);
			}
			DataPushDispatcher dataPushDispatcher =  new DataPushDispatcher(	getApplicationContainerURI(),
													(execTimeAccum/nbRequests),
													avmStats);
			return dataPushDispatcher;
	}
	
	/**
	 * push the dynamic state of the Request Dispatcher through 
	 * its notification data inbound port.
	 * @throws Exception
	 */
	public void	sendDynamicState() throws Exception
	{
		if (this.sdip.connected()) {
			DataPushDispatcher dataToPush = this.prepareCollectedData() ;
			this.sdip.send(dataToPush);
		}
	}
	
	/**
	 * push the dynamic state of the Request Dispatcher through its 
	 * notification data inbound port at a specified time interval 
	 * in ms and for a specified number of times.
	 * 
	 * @param interval
	 * @param numberOfRemainingPushes
	 * @throws Exception
	 */
	public void	sendDynamicState(
			final int interval,
			int numberOfRemainingPushes
			) throws Exception
		{
			this.sendDynamicState() ;
			final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1 ;
			if (fNumberOfRemainingPushes > 0) {
				final RequestDispatcherComponent RDC = this ;
				this.pushingFuture =
						this.scheduleTask(
								new ComponentI.ComponentTask() {
									@Override
									public void run() {
										try {
											RDC.sendDynamicState(
													interval,
													fNumberOfRemainingPushes) ;
										} catch (Exception e) {
											throw new RuntimeException(e) ;
										}
									}
								},
								TimeManagement.acceleratedDelay(interval),
								TimeUnit.MILLISECONDS) ;
			}
		}
	
	/**
	 * start the pushing of data and force the pushing to be done each
	 * <code>interval</code> period of time. 
	 * @param interval		delay between pushes (in milliseconds).
	 * @throws Exception
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		// first, send the state if the corresponding port is connected
				final RequestDispatcherComponent c = this ;
				this.pushingFuture =
					this.scheduleTaskAtFixedRate(
							new ComponentI.ComponentTask() {
								@Override
								public void run() {
									try {
										c.sendDynamicState() ;
									} catch (Exception e) {
										throw new RuntimeException(e) ;
									}
								}
							},
							TimeManagement.acceleratedDelay(interval),
							TimeManagement.acceleratedDelay(interval),
							TimeUnit.MILLISECONDS) ;
		
	}

	/**
	 * start <code>n>/code> pushing of data and force the pushing to be done
	 * each <code>interval</code> period of time. 
	 * @param interval		delay between pushes (in milliseconds).
	 * @param n				total number of pushes to be done, unless stopped.
	 * @throws Exception
	 */
	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		assert	n > 0 ;

		this.logMessage(this.applicationContainerURI+"RD" + " startLimitedPushing with interval "
									+ interval + " ms for " + n + " times.") ;

		// first, send the static state if the corresponding port is connected

		final RequestDispatcherComponent c = this ;
		this.pushingFuture =
			this.scheduleTask(
					new ComponentI.ComponentTask() {
						@Override
						public void run() {
							try {
								c.sendDynamicState(interval, n) ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;		
	}


	/**
	 * stop the pushing of data.
	 * @throws Exception
	 */
	@Override
	public void stopPushing() throws Exception {
		if (this.pushingFuture != null &&
				!(this.pushingFuture.isCancelled() ||
									this.pushingFuture.isDone())) {
											this.pushingFuture.cancel(false) ;
										}
	}

	/**
	 * 
	 * @return the URI of the Application container 
	 * used by the Request Dispatcher
	 */
	public String getApplicationContainerURI() {
		return applicationContainerURI;
	}

	/**
	 * setting the URI of the ApplicationContainer
	 * @param applicationContainerURI
	 */
	public void setApplicationContainerURI(String applicationContainerURI) {
		this.applicationContainerURI = applicationContainerURI;
	}
}

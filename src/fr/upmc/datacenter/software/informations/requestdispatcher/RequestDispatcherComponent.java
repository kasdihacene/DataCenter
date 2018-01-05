package fr.upmc.datacenter.software.informations.requestdispatcher;

import java.util.ArrayList;

import fr.upmc.components.AbstractComponent;
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
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMHandlerI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.ports.RequestResourceVMInboundPort;

/**
 * 
 * @author Hacene KASDI
 * @version 28.12.17.HK
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
	
	public RequestDispatcherComponent(String applicationContainerURI) throws Exception {
		super(1,1);
		this.setApplicationContainerURI(applicationContainerURI);
		this.applicationVMList=new ArrayList<String>();
		
		this.addOfferedInterface(RequestResourceVMI.class);
		this.addOfferedInterface(RequestNotificationI.class);
		this.addRequiredInterface(RequestNotificationI.class);
		this.addRequiredInterface(RequestSubmissionI.class);
		this.addOfferedInterface(RequestSubmissionI.class);
		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class);
												
												// APP-1_RD_RNOP
		RequestNotificationOutboundPort rnop 	= new RequestNotificationOutboundPort(applicationContainerURI+"_RNOP",this);
		RequestSubmissionInboundPort 	rsip 	= new RequestSubmissionInboundPort(applicationContainerURI+"_RSIP",this);
										rrvmip	= new RequestResourceVMInboundPort(applicationContainerURI+"_RVMIP",this);
										rsop	= new RequestSubmissionOutboundPort(applicationContainerURI+"_RSOP",this);
										rnip	= new RequestNotificationInboundPort(applicationContainerURI+"_RNIP",this);
		
										
		this.addPort(rnop);
		this.addPort(rsip);
		this.addPort(rrvmip);
		this.addPort(rsop);
		this.addPort(rnip);
		
		this.rnip.publishPort();
		this.rrvmip.publishPort();
		this.rsop.publishPort();
		rsip.publishPort();
		rnop.publishPort();
		
		
	}

	@Override
	public void addVMApplication(RequestVMI requestVMI) throws Exception {
		// Add the AVM URI to the list
		applicationVMList.add(requestVMI.getURIVM());
		
		
	}

	@Override
	public void removeVMAppication(RequestVMI requestVMI) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Received from the AVM when the execution of the RequestI was finished
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		System.out.println("REQUEST TERMINATION : "+r.getRequestURI());
		
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		
	}
	/**
	 * RoundRobin policy for dispatching request to the ApplicationVM
	 * @param AVMlistURIs
	 * @return the next AVM uri that weh should execute the request
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
		System.out.println("********* REQUEST : "+r.getRequestURI()+" ARRIVED AND SUBMITED TO ******* "+applicationVMList.get(0));
		
		// Connect the ApplicationVM added to the RequestDispatcher using RoundRobin policy
		String avmNextURI = roundRobinAVM(applicationVMList);
		rsop.doConnection(avmNextURI+"_RSIP",RequestSubmissionConnector.class.getCanonicalName());
		rsop.submitRequestAndNotify(r);
		rsop.doDisconnection();
	}

	/**
	 * start the pushing of data and force the pushing to be done each
	 * <code>interval</code> period of time. 
	 * @param interval		delay between pushes (in milliseconds).
	 * @throws Exception
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		System.out.println("=== START UNLIMITED PUSHING");
		
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
		System.out.println("=== START LIMITED PUSHING ");
		
	}


	/**
	 * stop the pushing of data.
	 * @throws Exception
	 */
	@Override
	public void stopPushing() throws Exception {
		System.out.println("=== STOP PUSHING ");
		
	}

	public String getApplicationContainerURI() {
		return applicationContainerURI;
	}

	public void setApplicationContainerURI(String applicationContainerURI) {
		this.applicationContainerURI = applicationContainerURI;
	}
}

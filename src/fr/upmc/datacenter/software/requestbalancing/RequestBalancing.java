package fr.upmc.datacenter.software.requestbalancing;

import java.util.ArrayList;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.interfaces.PushModeControllingI;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class RequestBalancing 	extends AbstractComponent 
								implements 	RequestSubmissionHandlerI, 
											PushModeControllingI, 
											RequestBalancingMangementI{

	public static int	DEBUG_LEVEL = 1 ;

	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------
	
	/**	the URI of the Request Balancing						*/
	protected String 									rbURI;
	/**	THE INITIALIZATION PORTS */
	protected RequestGeneratorManagementOutboundPort 	rgmop;
	protected RequestSubmissionOutboundPort				rsop;
	protected RequestNotificationInboundPort			rnip;
	protected RequestSubmissionInboundPort				rsip;
	
	public ArrayList<RequestI> numberRequest;
	
	// ------------------------------------------------------------------------
	// Component constructor
	// ------------------------------------------------------------------------
	
	public RequestBalancing(
			String rbURI,
			String rgmopURI,
			String rsopURI,
			String rnipURI,
			String rsipURI
			) throws Exception {
		// The normal thread pool is used to process component services, while
		// the scheduled one is used to schedule the pushes of dynamic state
		// when requested.
				super(1, 1) ;
				
		// PRECONDITIONS
			assert rbURI	!=null;
			assert rgmopURI	!=null;
			assert rsopURI		!=null;
			assert rnipURI		!=null;
			assert rsipURI		!=null;
			
			this.rbURI=rbURI;
			
			//ADD HERE ACCORDING TO THE AVM BEHAVIOR ( INTERFACES AND PORTS )
			
			//THE OFFERING INTERFACES
			
			this.addOfferedInterface(RequestNotificationHandlerI.class);
			this.rnip = 
					new RequestNotificationInboundPort(
						rnipURI,
						this
							);
			
			this.addPort(this.rnip);
			
			//===============
			this.addOfferedInterface(RequestSubmissionI.class) ;
			this.rsip =
							new RequestSubmissionInboundPort(
									rsipURI, this) ;
			this.addPort(this.rsip) ;
			this.rsip.publishPort() ;
			
			//==============
			/**
			 * publish the port RequestNotification
			 */
			
			//THE REQUIRED INTERFACES
			this.addRequiredInterface(RequestGeneratorManagementI.class);
		
			
			
		numberRequest = new ArrayList<RequestI>();
	}
	
	

	/**
	 * REQUEST SUBMISSION METHODS INTERFACE
	 */

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		this.numberRequest.add(r);
		this.getRequestURI(r);
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopPushing() throws Exception {
		// TODO Auto-generated method stub
		
	}


	/**
	 * REQUEST BALANCING METHODS INTERFACE
	 */
	@Override
	public int getNumberRequest() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getRequestURI(RequestI ri) {
		String uri;
		uri = ri.getRequestURI();
		return uri;
	}
	
	
	

}

package fr.upmc.datacenter.software.requestbalancing.test;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.requestbalancing.RequestBalancing;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class TestRequestBalancing extends AbstractCVM{

	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;
	
	
	protected RequestGenerator rg;
	protected RequestBalancing rb;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;
	
	public TestRequestBalancing() throws Exception {
		super();
	}

	@Override
	public void deploy()throws Exception {
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;

		
		// --------------------------------------------------------------------
				// Creating the request generator component.
				// --------------------------------------------------------------------
				this.rg = new RequestGenerator(
							"rg",			// generator component URI
							500.0,			// mean time between two requests
							6000000000L,	// mean number of instructions in requests
							RequestGeneratorManagementInboundPortURI,
							RequestSubmissionOutboundPortURI,
							RequestNotificationInboundPortURI) ;
				this.addDeployedComponent(rg) ;

				// Toggle on tracing and logging in the request generator to
				// follow the submission and end of execution notification of
				// individual requests.
				this.rg.toggleTracing() ;
				this.rg.toggleLogging() ;
				
				
				
				this.rb = new RequestBalancing("RequstBalancing", 
						RequestGeneratorManagementInboundPortURI, 
						RequestSubmissionOutboundPortURI, 
						RequestNotificationInboundPortURI, 
						RequestSubmissionInboundPortURI);
				
				
				this.rg.doPortConnection(RequestSubmissionOutboundPortURI, 
										RequestSubmissionInboundPortURI, 
										RequestSubmissionConnector.class.getCanonicalName());
		
				
				// Create a mock up port to manage to request generator component
				// (starting and stopping the generation).
				this.rgmop = new RequestGeneratorManagementOutboundPort(
									RequestGeneratorManagementOutboundPortURI,
									new AbstractComponent(0, 0) {}) ;
				this.rgmop.publishPort() ;
				this.rgmop.doConnection(
						RequestGeneratorManagementInboundPortURI,
						RequestGeneratorManagementConnector.class.getCanonicalName()) ;
				
				//COMPLETE THE DEPLOYEMENT AT THE COMPONENT VIRTUAL MACHINE
				super.deploy();
	}
	
	@Override
	public void start()throws Exception {
		super.start();
	}
	
	public void testRB() throws Exception {
		this.rg.startGeneration();
		Thread.sleep(100000);
		this.rg.stopGeneration();
	}
	
	public static void main(String[] args) {
		try {
			final TestRequestBalancing trb = new TestRequestBalancing();
			System.out.println("PREPARING...");
			trb.deploy();
			System.out.println("STARTING PROCESS ...");
			trb.start();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						trb.testRB();
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			
			Thread.sleep(10000);
			
			//SHUTDOW THE APPLOCATION
			
			System.out.println("END...");
			
			System.exit(0);
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

}

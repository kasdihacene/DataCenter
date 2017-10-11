package fr.upmc.datacenter.software.requestDispatcher.test;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.requestDispatcher.RequestDispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class TestRequestDispatcher extends  fr.upmc.components.cvm.AbstractCVM {

	public TestRequestDispatcher() throws Exception {
		super();
		
	}
	// PREDIFINED URI OF PORTS 
	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;

	/** 	Request Dispatcher component.							*/
	protected RequestDispatcher							rd ;
	/** 	Request generator component.										*/
	protected RequestGenerator							rg;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;
	
	@Override
	public void deploy() throws Exception{

		// --------------------------------------------------------------------
		// Creating the request Dispatcher component.
		// --------------------------------------------------------------------
		this.rd = new RequestDispatcher("RDispatcher", 
				RequestSubmissionInboundPortURI, 
				RequestNotificationOutboundPortURI);
		this.addDeployedComponent(rd);

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
		
				
				this.rg.doPortConnection(
						RequestSubmissionOutboundPortURI,
						RequestSubmissionInboundPortURI,
						RequestSubmissionConnector.class.getCanonicalName()) ;

			this.rd.doPortConnection(
						RequestNotificationOutboundPortURI,
						RequestNotificationInboundPortURI,
						RequestNotificationConnector.class.getCanonicalName()) ;

			// Create a mock up port to manage to request generator component
			// (starting and stopping the generation).
			this.rgmop = new RequestGeneratorManagementOutboundPort(
								RequestGeneratorManagementOutboundPortURI,
								new AbstractComponent(0, 0) {}) ;
			this.rgmop.publishPort() ;
			this.rgmop.doConnection(
					RequestGeneratorManagementInboundPortURI,
					RequestGeneratorManagementConnector.class.getCanonicalName()) ;
			// --------------------------------------------------------------------

			// complete the deployment at the component virtual machine level.
			super.deploy();
	}
	
	@Override
	public void			start() throws Exception
	{
		super.start() ;

		// Allocate the 4 cores of the computer to the application virtual
		// machine.
//		AllocatedCore[] ac = this.csPort.allocateCores(4) ;
//		this.avmPort.allocateCores(ac) ;
	}
	
	public void			testScenario() throws Exception
	{
		// start the request generation in the request generator.
		this.rgmop.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(20000L) ;
		// then stop the generation.
		this.rgmop.stopGeneration() ;
		
	}
	
	
	public static void main(String[] args) {
		try {
			TestRequestDispatcher trd = new TestRequestDispatcher();
			// DEPLY THE COMPONENTS
			System.out.println("DEPLOYING COMPONENTS...");
			trd.deploy();
			System.out.println("STARTING...");
			trd.start();
			
			// Execute the chosen request generation test scenario in a
						// separate thread.
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									trd.testScenario() ;
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						}).start() ;
						// Sleep to let the test scenario execute to completion.
						Thread.sleep(10000) ; //10000 to try 90000L
		} catch (Exception e) {
			System.out.println("THE ERROR : "+e.toString());
		}

	}

}

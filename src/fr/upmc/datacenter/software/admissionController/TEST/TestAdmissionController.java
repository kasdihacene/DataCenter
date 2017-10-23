package fr.upmc.datacenter.software.admissionController.TEST;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.ports.PortI;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissionController.AdmissionController;
import fr.upmc.datacenter.software.admissionController.connectors.AdmissionRequestConnector;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissionController.ports.AdmissionRequestInboundPort;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class TestAdmissionController extends fr.upmc.components.cvm.AbstractCVM{

	public TestAdmissionController() throws Exception {
		super();
	}
	
	/**
	* All URIs and ports for first computer
	*/
	// URIs
	public static final String ComputerServicesInboundPortURI0 = "cs1-ibp";
	public static final String ComputerServicesOutboundPortURI0 = "cs1-obp";
	public static final String ComputerStaticStateDataInboundPortURI0 = "css1-dip";
	public static final String ComputerStaticStateDataOutboundPortURI0 = "css1-dop";
	public static final String ComputerDynamicStateDataInboundPortURI0 = "cds1-dip";
	public static final String ComputerDynamicStateDataOutboundPortURI0 = "cds1-dop";
	// Ports
	protected ComputerServicesOutboundPort csPort0;

	/**
	* All URIs and ports for second computer
	*/
	// URIs
	public static final String ComputerServicesInboundPortURI1 = "cs1-ibp";
	public static final String ComputerServicesOutboundPortURI1 = "cs1-obp";
	public static final String ComputerStaticStateDataInboundPortURI1 = "css1-dip";
	public static final String ComputerStaticStateDataOutboundPortURI1 = "css1-dop";
	public static final String ComputerDynamicStateDataInboundPortURI1 = "cds1-dip";
	public static final String ComputerDynamicStateDataOutboundPortURI1 = "cds1-dop";
	// Ports
	protected ComputerServicesOutboundPort csPort1;
	

	// PREDIFINED URIs OF PORTS OF INTAKE CONTROLLER
	/**
	 * ADMISSION CONTROLLER PORTS
	 */
	public static final String	AdmissionControllerInboundPortURI = "admissionControllerIN" ;
	
	// PREDEFINED URIs OF PORTS OF REQUEST GENERATORS
	/**
	 * REQUEST GEENRATOR 1
	 */
	public static final String	RequestSubmissionOutboundPortURI = "rsobp-generator1" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp-generator1" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip-generator1" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop-generator1" ;
	public static final String	AdmissionControllerOutboundPortURI = "admissionControllerOUT" ;
	/**
	 * REQUEST GENERATOR 2
	 */
	public static final String	RequestSubmissionOutboundPortURI2 = "rsobp2-generator2" ;
	public static final String	RequestNotificationInboundPortURI2 = "rnibp2-generator2" ;
	public static final String	RequestGeneratorManagementInboundPortURI2 = "rgmip2-generator2" ;
	public static final String	RequestGeneratorManagementOutboundPortURI2 = "rgmop2-generator2" ;
	
	
	//--------------------------------------------------------------------------
	// ADD THE COMPONENTS
	//--------------------------------------------------------------------------
	/**		Intake admission component										*/
	protected AdmissionController						admissionController;
	/** 	Request generator component.										*/
	protected RequestGenerator							rg1,rg2;
	
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop,rgmop2 ;
	
	protected Computer c0, c1;
	protected ComputerMonitor cm0, cm1;

	//--------------------------------------------------------------------------

	@Override
	public void deploy() throws Exception{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;
		
		/**
		 * characteristics of Computers 
		 */
		int numberOfProcessors = 2;
		int numberOfCores = 2;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>();
		admissibleFrequencies.add(1500); // Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000); // and at 3 GHz
		Map<Integer, Integer> processingPower = new HashMap<Integer, Integer>();
		processingPower.put(1500, 1500000); // 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000); // 3 GHz executes 3 Mips

		
		// --------------------------------------------------------------------
		// Create and deploy first computer with his monitor
		// --------------------------------------------------------------------
		/** First computer component */
		String computerURI0 = "computer0";
		this.c0 = new Computer(
				computerURI0, 
				admissibleFrequencies, 
				processingPower, 
				1500, // Test scenario 1, frequency = 1,5 GHz
				1500, // max frequency gap within a processor
				numberOfProcessors,
				numberOfCores, 
				ComputerServicesInboundPortURI0,
				ComputerStaticStateDataInboundPortURI0, 
				ComputerDynamicStateDataInboundPortURI0);
		this.addDeployedComponent(c0);

		// Create a mock-up computer services port to later allocate its cores
		// to the application virtual machine.
		this.csPort0 = new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI0, new AbstractComponent(0, 0) {});
		this.csPort0.publishPort();
		this.csPort0.doConnection(
				ComputerServicesInboundPortURI0, 
				ComputerServicesConnector.class.getCanonicalName());
		/** Monitor component */
		this.cm0 = new ComputerMonitor(
				computerURI0, 
				true, 
				ComputerStaticStateDataOutboundPortURI0,
				ComputerDynamicStateDataOutboundPortURI0);
		this.addDeployedComponent(this.cm0);
		this.cm0.doPortConnection(
				ComputerStaticStateDataOutboundPortURI0, 
				ComputerStaticStateDataInboundPortURI0,
				DataConnector.class.getCanonicalName());

		this.cm0.doPortConnection(
				ComputerDynamicStateDataOutboundPortURI0, 
				ComputerDynamicStateDataInboundPortURI0,
				ControlledDataConnector.class.getCanonicalName());
		
		// --------------------------------------------------------------------
		// Create and deploy second computer with his monitor
		// -------------------------------------------------------------------
		/** Second computer component */
		String computerURI = "computer1";
		this.c1 = new Computer(
				computerURI, 
				admissibleFrequencies, 
				processingPower, 
				1500, // Test scenario 1, frequency = 1,5 GHz
				1500, // max frequency gap within a processor
				numberOfProcessors,
				numberOfCores, 
				ComputerServicesInboundPortURI1,
				ComputerStaticStateDataInboundPortURI1, 
				ComputerDynamicStateDataInboundPortURI1);
		this.addDeployedComponent(c1);
		
		// Create a mock-up computer services port to later allocate its cores
		// to the application virtual machine.
		this.csPort1 = new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI1, new AbstractComponent(0, 0) {});
		this.csPort1.publishPort();
		this.csPort1.doConnection(
				ComputerServicesInboundPortURI1, 
				ComputerServicesConnector.class.getCanonicalName());
		/** Monitor component */
		this.cm1 = new ComputerMonitor(
				computerURI, 
				true, 
				ComputerStaticStateDataOutboundPortURI1,
				ComputerDynamicStateDataOutboundPortURI1);
		this.addDeployedComponent(this.cm1);
		this.cm1.doPortConnection(
				ComputerStaticStateDataOutboundPortURI1, 
				ComputerStaticStateDataInboundPortURI1,
				DataConnector.class.getCanonicalName());

		this.cm1.doPortConnection(
				ComputerDynamicStateDataOutboundPortURI0, 
				ComputerDynamicStateDataInboundPortURI0,
				ControlledDataConnector.class.getCanonicalName());

		// --------------------------------------------------------------------
		// Creating the request generators component.
		// --------------------------------------------------------------------
		
		/**
		 * CREATE THE ADMISSION CONTROLLER
		 */
	
		this.admissionController = new AdmissionController(
				"Controller1",
				AdmissionControllerInboundPortURI);
		this.addDeployedComponent(admissionController);
		
		
		/**
		 * CREATE THE FIRST REQUEST GENERATOR				
		 */
		this.rg1 = new RequestGenerator(
									"rg1",			// generator component URI
									500.0,			// mean time between two requests
									6000000000L,	// mean number of instructions in requests
									RequestGeneratorManagementInboundPortURI,
									RequestSubmissionOutboundPortURI,
									RequestNotificationInboundPortURI,
									AdmissionControllerOutboundPortURI) ;
						this.addDeployedComponent(rg1) ;

						// Toggle on tracing and logging in the request generator to
						// follow the submission and end of execution notification of
						// individual requests.
						this.rg1.toggleTracing() ;
						this.rg1.toggleLogging() ;
				
		/**
		 * CREATE THE SECOND REQUEST GENERATOR				
		 */
//		this.rg2 = new RequestGenerator(
//									"rg2",			// generator component URI
//									500.0,			// mean time between two requests
//									6000000000L,	// mean number of instructions in requests
//									RequestGeneratorManagementInboundPortURI2,
//									RequestSubmissionOutboundPortURI2,
//									RequestNotificationInboundPortURI2) ;
//						this.addDeployedComponent(rg2) ;
//
//						// Toggle on tracing and logging in the request generator to
//						// follow the submission and end of execution notification of
//						// individual requests.
//						this.rg2.toggleTracing() ;
//						this.rg2.toggleLogging() ;
				
						
					
		/**
		 * CREATE THE SECOND REQUEST GENERATOR MANAGER
		 */
//				this.rgmop2 = new RequestGeneratorManagementOutboundPort(
//						RequestGeneratorManagementOutboundPortURI2,
//						new AbstractComponent(0, 0) {}) ;
//					this.rgmop2.publishPort() ;
//					this.rgmop2.doConnection(
//								RequestGeneratorManagementInboundPortURI2,
//								RequestGeneratorManagementConnector.class.getCanonicalName()) ;
//			

	/**
	 * PORT CONNECTIONS BETWEEN COMPONENTS 
	 */						
			this.rg1.doPortConnection(
					AdmissionControllerOutboundPortURI, 
					AdmissionControllerInboundPortURI,
					AdmissionRequestConnector.class.getCanonicalName());
			
	/**
	 * CREATE THE FIRST REQUEST GENERATOR MANAGER
	 */
			this.rgmop = new RequestGeneratorManagementOutboundPort(
					RequestGeneratorManagementOutboundPortURI,
					new AbstractComponent(0, 0) {}) ;
				this.rgmop.publishPort() ;
				this.rgmop.doConnection(
							RequestGeneratorManagementInboundPortURI,
							RequestGeneratorManagementConnector.class.getCanonicalName()) ;
					
	
							

		
		super.deploy();

	}
	
	@Override
	public void			start() throws Exception
	{
		super.start() ;
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
	public void			testScenario2() throws Exception
	{
		// start the request generation in the request generator.
		this.rgmop2.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(20000L) ;
		// then stop the generation.
		this.rgmop2.stopGeneration() ;
		
	}
	
	
	public static void main(String[] args) {
		try {
			TestAdmissionController testAdmissionControler = new TestAdmissionController();
			// DEPLY THE COMPONENTS
			System.out.println("DEPLOYING COMPONENTS...");
			testAdmissionControler.deploy();
			System.out.println("STARTING...");
			testAdmissionControler.start();
			
			// Execute the chosen request generation test scenario in a
						// separate thread.
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									testAdmissionControler.testScenario();
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

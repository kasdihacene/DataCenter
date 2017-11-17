package fr.upmc.datacenter.software.admissionController.TEST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissionController.Admission;
import fr.upmc.datacenter.software.admissionController.AdmissionController;
import fr.upmc.datacenter.software.admissionController.connectors.AdmissionRequestConnector;
import fr.upmc.datacenter.software.applicationcontainer.ApplicationContainer;
import fr.upmc.datacenter.software.applicationcontainer.connectors.AdmissionNotificationConnector;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class TestAdmissionController extends fr.upmc.components.cvm.AbstractCVM{

	public TestAdmissionController() throws Exception {
		super();
	}
	
	/**
	* All URIs and ports for first computer
	*/
	protected final static int COMPUTER_NUMBER = 10;
	protected final static String COMPUTER_URI = "computer";
	protected final static String COMPUTER_MONITOR_URI = "monitor";
	protected final static String COMPUTER_SERVICE_INBOUND_PORT_SUFFIX = "csip";
	protected final static String COMPUTER_SERVICE_OUTBOUND_PORT_SUFFIX = "csop";
	protected final static String COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX = "csdip";
	protected final static String COMPUTER_STATIC_DATA_OUTBOUND_PORT_SUFFIX = "csdop";
	protected final static String COMPUTER_DYNAMIC_DATA_INBOUND_PORT_SUFFIX = "cddip";
	protected final static String COMPUTER_DYNAMIC_DATA_OUTBOUND_PORT_SUFFIX = "cddop";
	protected Computer[] computers = new Computer[COMPUTER_NUMBER];
	

	// PREDIFINED URIs OF PORTS OF INTAKE CONTROLLER
	
	
	// PREDEFINED URIs OF PORTS OF REQUEST GENERATORS
	/**
	 * REQUEST GEENRATOR 1
	 */
	public static final String	RequestSubmissionOutboundPortURI = "rsobp-generator1" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp-generator1" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip-generator1" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop-generator1" ;
	/**
	 * REQUEST GENERATOR 2
	 */
	public static final String	RequestSubmissionOutboundPortURI2 = "rsobp2-generator2" ;
	public static final String	RequestNotificationInboundPortURI2 = "rnibp2-generator2" ;
	public static final String	RequestGeneratorManagementInboundPortURI2 = "rgmip2-generator2" ;
	public static final String	RequestGeneratorManagementOutboundPortURI2 = "rgmop2-generator2" ;
	
	
	/**
	 * APPLICATION CONTAINER
	 */
	public static final String	AdmissionNotificationInboundPortURI = "admissionNotifyIN-APPLICATION" ;
	public static final String	AdmissionControllerOutboundPortURI = "admissionControllerIN-APPLICATION" ;
	
	public static final String	AdmissionNotificationInboundPortURI2 = "admissionNotifyIN-APPLICATION2" ;
	public static final String	AdmissionControllerOutboundPortURI2 = "admissionControllerIN-APPLICATION2" ;
	
	/**
	 * ADMISSION CONTROLLER PORTS
	 */
	public static final String	AdmissionControllerInboundPortURI = "admissionControllerIN-CONTROLLER" ;
	public static final String	AdmissionNotificationOutboundPortURI = "admissionNofifyOUT-CONTROLLER" ;
	
	//--------------------------------------------------------------------------
	// ADD THE COMPONENTS
	//--------------------------------------------------------------------------
	/**		Intake admission component										*/
	protected AdmissionController						admissionController;
	/**		Admission Notification component								*/
	protected ApplicationContainer applicationContainer;
	
	/** 	Request generator component.										*/
	protected RequestGenerator							rg1,rg2;
	
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop,rgmop2 ;
	
	protected Computer c0, c1;
	protected ComputerMonitor cm0, cm1;
	
	protected ArrayList<Computer> listComputers;
	
	protected Admission admission;
	//--------------------------------------------------------------------------

	@Override
	public void deploy() throws Exception{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;
		
		listComputers = new ArrayList<Computer>();
		
		/**
		 * DYNAMYC CREATION OF COMPUTERS
		 */
		
		int numberOfProcessors = 2;
		int numberOfCores = 2;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>();
		admissibleFrequencies.add(1500); // Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000); // and at 3 GHz
		Map<Integer, Integer> processingPower = new HashMap<Integer, Integer>();
		processingPower.put(1500, 1500000); // 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000); // 3 GHz executes 3 Mips

		for (int i = 0; i < COMPUTER_NUMBER; i++) {
			String computerURI = COMPUTER_URI + i;
			System.out.println(computerURI);
			String csipURI = computerURI + COMPUTER_SERVICE_INBOUND_PORT_SUFFIX;
			String csopURI = computerURI + COMPUTER_SERVICE_OUTBOUND_PORT_SUFFIX;
			String csdipURI = computerURI + COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX;
			String csdopURI = computerURI + COMPUTER_STATIC_DATA_OUTBOUND_PORT_SUFFIX;
			String cddipURI = computerURI + COMPUTER_DYNAMIC_DATA_INBOUND_PORT_SUFFIX;
			String cddopURI = computerURI + COMPUTER_DYNAMIC_DATA_OUTBOUND_PORT_SUFFIX;
			Computer computer = new Computer(computerURI, admissibleFrequencies, processingPower, 1500, 1500,
					numberOfProcessors, numberOfCores, csipURI, csdipURI, cddipURI);

			ComputerServicesOutboundPort csPort = new ComputerServicesOutboundPort(csopURI,
					new AbstractComponent(0, 0) {
					});
			csPort.publishPort();
			csPort.doConnection(csipURI, ComputerServicesConnector.class.getCanonicalName());

			ComputerMonitor cm = new ComputerMonitor(COMPUTER_MONITOR_URI + i, true, csdopURI, cddopURI);
			this.addDeployedComponent(cm);
			cm.doPortConnection(csdopURI, csdipURI, DataConnector.class.getCanonicalName());
			cm.doPortConnection(cddopURI, cddipURI, ControlledDataConnector.class.getCanonicalName());
			System.out.println(csdopURI + csdipURI + cddopURI + cddipURI);
			
			listComputers.add(computer);
			System.out.println(String.format("DEPLOYING : %d-th computer deployed", i + 1));
		}

		// --------------------------------------------------------------------
		// Creating the request generators component.
		// --------------------------------------------------------------------
		
		Admission admission = new Admission(
				this, 
				AdmissionNotificationInboundPortURI, 
				AdmissionControllerInboundPortURI);
	
		/**
		 * CREATE THE APPLICATION
		 */
		this.applicationContainer =
				new ApplicationContainer(
						"APP1-", 
						admission,
						AdmissionNotificationInboundPortURI,
						AdmissionControllerOutboundPortURI);
		this.addDeployedComponent(applicationContainer);
		
		
		
		
		/**
		 * CREATE THE ADMISSION CONTROLLER
		 */
	
		this.admissionController = new AdmissionController(
				"Controller1",
				AdmissionControllerInboundPortURI,
				AdmissionNotificationOutboundPortURI,
				listComputers);
		this.addDeployedComponent(admissionController);
		
		/**
		 * CONNEXION OF THE COMPONENT
		 */
		this.applicationContainer.doPortConnection(
				AdmissionControllerOutboundPortURI, 
				AdmissionControllerInboundPortURI, 
				AdmissionRequestConnector.class.getCanonicalName());
		
		this.admissionController.doPortConnection(
				AdmissionNotificationOutboundPortURI, 
				AdmissionNotificationInboundPortURI, 
				AdmissionNotificationConnector.class.getCanonicalName());
		/**
		 * CREATE THE FIRST REQUEST GENERATOR				
		 */
//		this.rg1 = new RequestGenerator(
//									"rg1",			// generator component URI
//									500.0,			// mean time between two requests
//									6000000000L,	// mean number of instructions in requests
//									RequestGeneratorManagementInboundPortURI,
//									RequestSubmissionOutboundPortURI,
//									RequestNotificationInboundPortURI,
//									AdmissionControllerOutboundPortURI) ;
//						this.addDeployedComponent(rg1) ;
//
//						// Toggle on tracing and logging in the request generator to
//						// follow the submission and end of execution notification of
//						// individual requests.
//						this.rg1.toggleTracing() ;
//						this.rg1.toggleLogging() ;
				
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

//	/**
//	 * PORT CONNECTIONS BETWEEN COMPONENTS 
//	 */						
//			this.rg1.doPortConnection(
//					AdmissionControllerOutboundPortURI, 
//					AdmissionControllerInboundPortURI,
//					AdmissionRequestConnector.class.getCanonicalName());
//			
//	/**
//	 * CREATE THE FIRST REQUEST GENERATOR MANAGER
//	 */
//			this.rgmop = new RequestGeneratorManagementOutboundPort(
//					RequestGeneratorManagementOutboundPortURI,
//					new AbstractComponent(0, 0) {}) ;
//				this.rgmop.publishPort() ;
//				this.rgmop.doConnection(
//							RequestGeneratorManagementInboundPortURI,
//							RequestGeneratorManagementConnector.class.getCanonicalName()) ;
					
	
							

		
		super.deploy();

	}
	
	@Override
	public void			start() throws Exception
	{
		super.start() ;
		
	}
	
	
	public void			testScenario() throws Exception
	{
	
		applicationContainer.startAsync();

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

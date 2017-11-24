package fr.upmc.datacenter.software.requestdispatcher.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class TestRequestDispatcher extends AbstractCVM {
	public TestRequestDispatcher() throws Exception {
		super();

	}

	/**
	 * All URIs and ports for first computer
	 */
	// URIs
	public static final String ComputerServicesInboundPortURI0 = "cs0-ibp";
	public static final String ComputerServicesOutboundPortURI0 = "cs0-obp";
	public static final String ComputerStaticStateDataInboundPortURI0 = "css0-dip";
	public static final String ComputerStaticStateDataOutboundPortURI0 = "css0-dop";
	public static final String ComputerDynamicStateDataInboundPortURI0 = "cds0-dip";
	public static final String ComputerDynamicStateDataOutboundPortURI0 = "cds0-dop";
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

	/**
	 * All URIs and ports for first AVM
	 */
	// URIs
	public static final String avmURI0 = "avm0";
	public static final String ApplicationVMManagementInboundPortURI0 = "avm0-ibp";
	public static final String ApplicationVMManagementOutboundPortURI0 = "avm0-obp";
	public static final String RequestSubmissionInboundPortURI0 = "rsibp0";
	public static final String RequestNotificationOutboundPortURI0 = "rnobp0";
	// Ports
	protected ApplicationVMManagementOutboundPort avmPort0;

	/**
	 * All URIs and ports for second AVM
	 */
	// URIs
	public static final String avmURI1 = "avm1";
	public static final String ApplicationVMManagementInboundPortURI1 = "avm1-ibp";
	public static final String ApplicationVMManagementOutboundPortURI1 = "avm1-obp";
	public static final String RequestSubmissionInboundPortURI1 = "rsibp1";
	public static final String RequestNotificationOutboundPortURI1 = "rnobp1";
	// Ports
	protected ApplicationVMManagementOutboundPort avmPort1;

	/**
	 * All URIs and ports for Request Dispatcher
	 */
	public static final String RDRequestManagementInboundPortURI = "rd-rdmibp";
	public static final String RDRequestSubmissionInboundPortURI = "rd-rsibp";
	public static final String RDRequestNotificationInboundPortURI = "rd-rnibp";
	public static final String RDRequestNotificationOutboundPortURI = "rd-rnobp";

	/**
	 * All URIs and ports for Request Generator
	 */
	public static final String RGRequestSubmissionOutboundPortURI = "rg-rsobp";
	public static final String RGRequestNotificationInboundPortURI = "rg-rnibp";
	public static final String RequestGeneratorManagementInboundPortURI = "rg-rgmip";
	public static final String RequestGeneratorManagementOutboundPortURI = "rg-rgmop";

	/** Request Dispatcher component. */
	protected RequestDispatcher rd;
	/** Request generator component. */
	protected RequestGenerator rg;
	/**
	 * Port connected to the request generator component to manage its execution
	 * (starting and stopping the request generation).
	 */
	protected RequestGeneratorManagementOutboundPort rgmop;

	protected Computer c0, c1;
	protected ComputerMonitor cm0, cm1;
	protected ApplicationVM avm0, avm1;

	@Override
	public void deploy() throws Exception {

		// --------------------------------------------------------------------
		// Create and deploy 2 computer components with theirs 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		// Caracteristics for computers
		// --------------------------------------------------------------------
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
		this.c0 = new Computer(computerURI0, admissibleFrequencies, processingPower, 1500, // Test scenario 1, frequency
																							// = 1,5 GHz
				1500, // max frequency gap within a processor
				numberOfProcessors, numberOfCores, ComputerServicesInboundPortURI0,
				ComputerStaticStateDataInboundPortURI0, ComputerDynamicStateDataInboundPortURI0);
		this.addDeployedComponent(c0);

		// Create a mock-up computer services port to later allocate its cores
		// to the application virtual machine.
		this.csPort0 = new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI0, new AbstractComponent(0, 0) {
		});
		this.csPort0.publishPort();
		this.csPort0.doConnection(ComputerServicesInboundPortURI0, ComputerServicesConnector.class.getCanonicalName());
		/** Monitor component */
		this.cm0 = new ComputerMonitor(computerURI0, true, ComputerStaticStateDataOutboundPortURI0,
				ComputerDynamicStateDataOutboundPortURI0);
		this.addDeployedComponent(this.cm0);
		this.cm0.doPortConnection(ComputerStaticStateDataOutboundPortURI0, ComputerStaticStateDataInboundPortURI0,
				DataConnector.class.getCanonicalName());

		this.cm0.doPortConnection(ComputerDynamicStateDataOutboundPortURI0, ComputerDynamicStateDataInboundPortURI0,
				ControlledDataConnector.class.getCanonicalName());
		System.out.println("DEPLOYING : First computer deployed");

		// --------------------------------------------------------------------
		// Create and deploy second computer with his monitor
		// -------------------------------------------------------------------
		/** Second computer component */
		String computerURI = "computer1";
		this.c1 = new Computer(computerURI, admissibleFrequencies, processingPower, 1500, // Test scenario 1, frequency
																							// = 1,5 GHz
				1500, // max frequency gap within a processor
				numberOfProcessors, numberOfCores, ComputerServicesInboundPortURI1,
				ComputerStaticStateDataInboundPortURI1, ComputerDynamicStateDataInboundPortURI1);
		this.addDeployedComponent(c1);

		// Create a mock-up computer services port to later allocate its cores
		// to the application virtual machine.
		this.csPort1 = new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI1, new AbstractComponent(0, 0) {
		});
		this.csPort1.publishPort();
		this.csPort1.doConnection(ComputerServicesInboundPortURI1, ComputerServicesConnector.class.getCanonicalName());
		/** Monitor component */
		this.cm1 = new ComputerMonitor(computerURI, true, ComputerStaticStateDataOutboundPortURI1,
				ComputerDynamicStateDataOutboundPortURI1);
		this.addDeployedComponent(this.cm1);
		this.cm1.doPortConnection(ComputerStaticStateDataOutboundPortURI1, ComputerStaticStateDataInboundPortURI1,
				DataConnector.class.getCanonicalName());

		this.cm1.doPortConnection(ComputerDynamicStateDataOutboundPortURI1, ComputerDynamicStateDataInboundPortURI1,
				ControlledDataConnector.class.getCanonicalName());
		System.out.println("DEPLOYING : Second computer deployed");

		// --------------------------------------------------------------------
		// Create and deploy first avm component
		// --------------------------------------------------------------------
		this.avm0 = new ApplicationVM(avmURI0, // application vm component URI
				ApplicationVMManagementInboundPortURI0, RequestSubmissionInboundPortURI0,
				RequestNotificationOutboundPortURI0);
		this.addDeployedComponent(this.avm0);

		// Create a mock up port to manage the AVM component (allocate cores).
		this.avmPort0 = new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI0,
				new AbstractComponent(0, 0) {
				});
		this.avmPort0.publishPort();
		this.avmPort0.doConnection(ApplicationVMManagementInboundPortURI0,
				ApplicationVMManagementConnector.class.getCanonicalName());

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.avm0.toggleTracing();
		this.avm0.toggleLogging();
		System.out.println("DEPLOYING : First AVM deployed");

		// --------------------------------------------------------------------
		// Create and deploy second avm component
		// --------------------------------------------------------------------
		this.avm1 = new ApplicationVM(avmURI1, // application vm component URI
				ApplicationVMManagementInboundPortURI1, RequestSubmissionInboundPortURI1,
				RequestNotificationOutboundPortURI1);
		this.addDeployedComponent(this.avm1);

		// Create a mock up port to manage the AVM component (allocate cores).
		this.avmPort1 = new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI1,
				new AbstractComponent(0, 0) {
				});
		this.avmPort1.publishPort();
		this.avmPort1.doConnection(ApplicationVMManagementInboundPortURI1,
				ApplicationVMManagementConnector.class.getCanonicalName());

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.avm1.toggleTracing();
		this.avm1.toggleLogging();
		System.out.println("DEPLOYING : Second AVM deployed");

		// --------------------------------------------------------------------
		// Creating the request Dispatcher component.
		// --------------------------------------------------------------------
		this.rd = new RequestDispatcher("RDispatcher", RDRequestManagementInboundPortURI,
				RDRequestSubmissionInboundPortURI, RDRequestNotificationInboundPortURI,
				RDRequestNotificationOutboundPortURI);
		this.addDeployedComponent(rd);
		System.out.println("DEPLOYING : Request Dispatcher deployed");

		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		this.rg = new RequestGenerator("rg", // generator component URI
				500.0, // mean time between two requests
				6000000000L, // mean number of instructions in requests
				RequestGeneratorManagementInboundPortURI, RGRequestSubmissionOutboundPortURI,
				RGRequestNotificationInboundPortURI);
		this.addDeployedComponent(rg);

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		this.rg.toggleTracing();
		this.rg.toggleLogging();
		System.out.println("DEPLOYING : Request Generator deployed");

		// Create a mock up port to manage to request generator component
		// (starting and stopping the generation).
		this.rgmop = new RequestGeneratorManagementOutboundPort(RequestGeneratorManagementOutboundPortURI,
				new AbstractComponent(0, 0) {
				});
		this.rgmop.publishPort();
		this.rgmop.doConnection(RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName());
		// --------------------------------------------------------------------

		// complete the deployment at the component virtual machine level.
		super.deploy();
	}

	@Override
	public void start() throws Exception {
		super.start();

		// Allocate the 4 cores of the computer to the application virtual
		// machine.
		AllocatedCore[] ac0 = this.csPort0.allocateCores(4);
		this.avmPort0.allocateCores(ac0);

		AllocatedCore[] ac1 = this.csPort1.allocateCores(3);
		this.avmPort1.allocateCores(ac1);

		ComputerStaticStateI c0StaticState = this.c0.getStaticState();
		ComputerStaticStateI c1StaticState = this.c1.getStaticState();
		for (int i = 0; i < c0StaticState.getNumberOfProcessors(); i++)
			for (int j = 0; j < c0StaticState.getNumberOfCoresPerProcessor(); j++)
				System.out.println(String.format("Computer 0 processor %d core %d enable : %b", i, j,
						this.c0.getDynamicState().getCurrentCoreReservations()[i][j]));
		for (int i = 0; i < c1StaticState.getNumberOfProcessors(); i++)
			for (int j = 0; j < c1StaticState.getNumberOfCoresPerProcessor(); j++)
				System.out.println(String.format("Computer 1 processor %d core %d enable : %b", i, j,
						this.c1.getDynamicState().getCurrentCoreReservations()[i][j]));

		doConnections();
	}

	public void doConnections() throws Exception {
		/**
		 * COMPONENT CONNECTIONS -----------------------------------------
		 */
		this.rg.doPortConnection(RGRequestSubmissionOutboundPortURI, RDRequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName());
		System.out.println(
				"CONNECTION : Request Generator submission outbound port connected to Request Dispatcher submission inbound port");

		this.rd.doPortConnection(RDRequestNotificationOutboundPortURI, RGRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
		System.out.println(
				"CONNECTION : Request Dispatcher notification outbound port connected to Request Generator notification inbound port");

		this.avm0.doPortConnection(RequestNotificationOutboundPortURI0, RDRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
		System.out.println(
				"CONNECTION : AVM 0 notification outbound port connected to Request Dispatcher notification inbound port");

		this.avm1.doPortConnection(RequestNotificationOutboundPortURI1, RDRequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
		System.out.println(
				"CONNECTION : AVM 1 notification outbound port connected to Request Dispatcher notification inbound port");

		this.rd.connectAVM(avmURI0, RequestSubmissionInboundPortURI0, RequestNotificationOutboundPortURI0);
		System.out.println("CONNECTION : AVM0 connected to Request Dispatcher");

		this.rd.connectAVM(avmURI1, RequestSubmissionInboundPortURI1, RequestNotificationOutboundPortURI1);
		System.out.println("CONNECTION : AVM1 connected to Request Dispatcher");
		/**
		 * -----------------------------------------------------------------
		 */
	}

	public void testScenario() throws Exception {
		// start the request generation in the request generator.
		this.rgmop.startGeneration();
		// wait 20 seconds
		Thread.sleep(20000L);
		// then stop the generation.
		this.rgmop.stopGeneration();

	}

	public static void main(String[] args) {
		try {
			TestRequestDispatcher trd = new TestRequestDispatcher();
			// DEPLY THE COMPONENTS
			System.out.println("***********************");
			System.out.println("DEPLOYING COMPONENTS...");
			trd.deploy();
			System.out.println("ALL DEPLOYED...........");
			System.out.println("***********************\n");

			System.out.println("***********");
			System.out.println("STARTING...");
			trd.start();
			System.out.println("ALL STARTED");
			System.out.println("***********\n");

			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						trd.testScenario();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}).start();
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(10000); // 10000 to try 90000L
		} catch (Exception e) {
			System.out.println("THE ERROR : " + e.toString());
		}

	}
}

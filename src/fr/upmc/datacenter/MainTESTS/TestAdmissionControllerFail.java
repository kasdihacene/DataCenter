package fr.upmc.datacenter.MainTESTS;

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
import fr.upmc.datacenter.software.applicationcontainer.ApplicationContainer;

/**
 * This class shows a test of one failure example when the Application who asked for hosting got a negative answer
 * 
 * In this case we try to put just one <code>Computer</code> in our data center means that we can allocate 2 <code>Processor</code>
 * with 2 <code>Cores</code> or we can say that the ApplicationVM reserve 4 cores to execute the received requests.
 * 
 *  Test :  1 Computer 
 *  		2 Processors
 *  		4 Cores ( 4 cores for each ApplicationVM )
 *  		2 ApplicationContainer
 * 
 * @author Hacene & Marc
 *
 */
public class TestAdmissionControllerFail extends fr.upmc.components.cvm.AbstractCVM{

	public TestAdmissionControllerFail() throws Exception {
		super();
	}
	
	/**
	* All URIs and ports for first computer
	*/
	protected final static int COMPUTER_NUMBER = 1;
	protected final static String COMPUTER_URI = "computer";
	protected final static String COMPUTER_MONITOR_URI = "monitor";
	protected final static String COMPUTER_SERVICE_INBOUND_PORT_SUFFIX = "csip";
	protected final static String COMPUTER_SERVICE_OUTBOUND_PORT_SUFFIX = "csop";
	protected final static String COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX = "csdip";
	protected final static String COMPUTER_STATIC_DATA_OUTBOUND_PORT_SUFFIX = "csdop";
	protected final static String COMPUTER_DYNAMIC_DATA_INBOUND_PORT_SUFFIX = "cddip";
	protected final static String COMPUTER_DYNAMIC_DATA_OUTBOUND_PORT_SUFFIX = "cddop";
	protected Computer[] computers = new Computer[COMPUTER_NUMBER];
	
	/**
	 * APPLICATION CONTAINER
	 */
	// APPLICATION CONTAINER 1
	public static final String	AdmissionNotificationInboundPortURI = "admissionNotifyIN-APPLICATION" ;
	public static final String	AdmissionControllerOutboundPortURI = "admissionControllerIN-APPLICATION" ;
	// APPLICATION CONTAINER 2
	public static final String	AdmissionNotificationInboundPortURI2 = "admissionNotifyIN-APPLICATION2" ;
	public static final String	AdmissionControllerOutboundPortURI2 = "admissionControllerIN-APPLICATION2" ;
	
	/**
	 * ADMISSION CONTROLLER PORTS
	 */
	//INTERFACE ADMISSION 1
	public static final String	AdmissionControllerInboundPortURI = "admissionControllerIN-CONTROLLER" ;
	public static final String	AdmissionNotificationOutboundPortURI = "admissionNofifyOUT-CONTROLLER" ;
	//INTERFACE ADMISSION 2
	public static final String	AdmissionControllerInboundPortURI2 = "admissionControllerIN-CONTROLLER2" ;
	public static final String	AdmissionNotificationOutboundPortURI2 = "admissionNofifyOUT-CONTROLLER2" ;
	
	//--------------------------------------------------------------------------
	// ADD THE COMPONENTS
	//--------------------------------------------------------------------------
	/**		 Admission Controller component										*/
	protected AdmissionController						admissionController;
	/**		Application Container component								*/
	protected ApplicationContainer applicationContainer, applicationContainer2;
	
	/**
	 * LIst of computers
	 */
	protected ArrayList<Computer> listComputers;
	
	protected Admission admission, admission2;
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
		
		Admission admission2 = new Admission(
				this, 
				AdmissionNotificationInboundPortURI2, 
				AdmissionControllerInboundPortURI2);
	
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
		
		
		this.applicationContainer2 =
				new ApplicationContainer(
						"APP2-", 
						admission2,
						AdmissionNotificationInboundPortURI2,
						AdmissionControllerOutboundPortURI2);
		this.addDeployedComponent(applicationContainer2);
		
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
		 * CONNEXION OF THE COMPONENTS
		 */
		
		// ApplicationContainer1 and AdmissionController connections 
		this.applicationContainer.connectWithAdmissionController(AdmissionControllerInboundPortURI);
		
		this.applicationContainer2.connectWithAdmissionController(AdmissionControllerInboundPortURI);		

		
		super.deploy();

	}
	
	@Override
	public void			start() throws Exception
	{
		super.start() ;	
	}
	
	public void			testScenario() throws Exception
	{
	
		applicationContainer.startSync();
		applicationContainer2.startSync();

	}
	
	
	public static void main(String[] args) {
		try {
			TestAdmissionControllerFail testAdmissionControler = new TestAdmissionControllerFail();
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
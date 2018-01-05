package fr.upmc.datacenter.software.step2.teststep2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.dataprovider.DataProvider;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissioncontroller.Admission;
import fr.upmc.datacenter.software.step2.AdmissionController;
import fr.upmc.datacenter.software.step2.ApplicationContainer;
import fr.upmc.datacenter.software.step2.tools.DelployTools;

/**
 * This class shows a test of one hosting two <code>ApplicationContainer</code>
 * 
 * In this case we use 2 <code>Computer</code> in our data center means that we can allocate 4 <code>Processor</code>
 * with 2 <code>Cores</code> or we can say that the ApplicationVM reserve 4 cores to execute the received requests.
 * The test will succeed because there are available resources for 2 <code>ApplicationConatiner</code>
 * 
 *  Test :  2 Computer 
 *  		2 * 2 Processors
 *  		8 Cores ( 4 cores for each ApplicationVM )
 *  		2 ApplicationContainer
 * 
 * @author Hacene & Marc
 *
 */
public class TestAdmissionControllerSuccess extends fr.upmc.components.cvm.AbstractCVM{

	public TestAdmissionControllerSuccess() throws Exception {
		super();
		// Set the AbstractCVM to deploy components
		DelployTools.setAcvm(this);
	}
	
	/**
	* All URIs and ports for first computer
	*/
	protected final static int COMPUTER_NUMBER = 5;
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
	protected AdmissionController admissionController;
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

		
		/**
		 * CREATE A DataProvider to store Computers informations
		 */
		DataProvider dataProvider = new DataProvider("DATA_PROVIDER");
		for (int i = 0; i < COMPUTER_NUMBER; i++) {
			String computerURI = COMPUTER_URI + i;
			System.out.println(computerURI);
			String csipURI = computerURI + "_CSIP";
			String csopURI = computerURI + "_CSOP";
			String csdipURI = computerURI + COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX;
			String csdopURI = computerURI + COMPUTER_STATIC_DATA_OUTBOUND_PORT_SUFFIX;
			String cddipURI = computerURI + "_CDSDIP";
			String cddopURI = computerURI + "_CDSDOP";
			Computer computer = new Computer(computerURI, admissibleFrequencies, processingPower, 1500, 1500,
					numberOfProcessors, numberOfCores, csipURI, csdipURI, cddipURI);
			this.addDeployedComponent(computer);
			
			ComputerServicesOutboundPort csPort = new ComputerServicesOutboundPort(csopURI,
					new AbstractComponent(0, 0) {
					});
			csPort.publishPort();
			csPort.doConnection(csipURI, ComputerServicesConnector.class.getCanonicalName());

			ComputerMonitor cm = new ComputerMonitor(COMPUTER_MONITOR_URI + i, true, csdopURI, cddopURI);
			this.addDeployedComponent(cm);
			cm.doPortConnection(csdopURI, csdipURI, DataConnector.class.getCanonicalName());
			cm.doPortConnection(cddopURI, cddipURI, ControlledDataConnector.class.getCanonicalName());
			
			System.out.println(String.format("DEPLOYING : %d-th computer deployed", i + 1));
	
			dataProvider.storeComputerData(
					computerURI, 
					admissibleFrequencies, 
					processingPower, 
					1500, 
					1500, 
					numberOfProcessors,
					numberOfCores);
		}
		// Deploy the DataProvider Component
		this.addDeployedComponent(dataProvider);
		
		

		// --------------------------------------------------------------------
		// Creating the request generators component.
		// --------------------------------------------------------------------
		
		Admission admission = new Admission(
				"_ANIP1", 
				"_ACIP1");
		
		Admission admission2 = new Admission(
				"_ANIP2", 
				"_ACIP2");
	
		/**
		 * CREATE THE APPLICATION
		 */
		this.applicationContainer =
				new ApplicationContainer(
						"APP1-",
						this,
						admission,
						"_ANIP1",
						"_ACOP1");
		this.addDeployedComponent(applicationContainer);
		
		
		this.applicationContainer2 =
				new ApplicationContainer(
						"APP2-",
						this,
						admission2,
						"ANIP2",
						"ACOP2");
		this.addDeployedComponent(applicationContainer2);
		
		/**
		 * CREATE THE ADMISSION CONTROLLER AND CONNECT IT TO DATA PROVIDER
		 */
	
		this.admissionController = new AdmissionController("ADM_CONT", this);
		this.addDeployedComponent(admissionController);
		this.admissionController.connectWithDataProvider("DATA_PROVIDER");
		
		/**
		 * CONNEXION OF THE COMPONENTS
		 */
		
		// ApplicationContainer1 and AdmissionController connections 
		this.applicationContainer.connectWithAdmissionController("ADM_CONT_ACIP");
		
		this.applicationContainer2.connectWithAdmissionController("ADM_CONT_ACIP");		

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
			TestAdmissionControllerSuccess testAdmissionControler = new TestAdmissionControllerSuccess();
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

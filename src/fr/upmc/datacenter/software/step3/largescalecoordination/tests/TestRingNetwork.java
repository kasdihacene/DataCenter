package fr.upmc.datacenter.software.step3.largescalecoordination.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.dataprovider.DataProvider;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.software.admissioncontroller.Admission;
import fr.upmc.datacenter.software.step2.ApplicationContainer;
import fr.upmc.datacenter.software.step2.adaptableproperty.ComputerAdaptable;
import fr.upmc.datacenter.software.step2.tools.DelployTools;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.admissioncontrollercoordination.AdmissionControllerCoordination;

/**
 * This class shows a test of hosting two <code>ApplicationContainer</code>
 * 
 * In this case we use 2 <code>Computer</code> in our data center means 
 * that we can allocate 4 <code>Processor</code> with 2 <code>Cores</code> 
 * or we can say that the ApplicationVM reserve 4 cores to execute the 
 * received requests. The test will succeed because there are available 
 * resources for 2 <code>ApplicationConatiner</code>
 * 
 *  Test :  2 Computer 
 *  		2 * 2 Processors
 *  		8 Cores ( 2 cores for each ApplicationVM )
 *  		2 ApplicationContainer
 * 
 * @author Hacene KASDI
 *
 */
public class TestRingNetwork extends fr.upmc.components.cvm.AbstractCVM{

	public TestRingNetwork() throws Exception {
		super();
		// Set the AbstractCVM to deploy components
		DelployTools.setAcvm(this);
	}
	
	/**
	* All URIs and ports for first computer
	*/
	protected final static int COMPUTER_NUMBER = 4;
	protected final static String COMPUTER_URI = "computer";
	protected final static String COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX = "csdip";
	
	//--------------------------------------------------------------------------
	// ADD THE COMPONENTS
	//--------------------------------------------------------------------------
	/**		 Admission Controller coordinator component										*/
	protected AdmissionControllerCoordination admissionController;
	/**		Application Container component								*/
	protected ApplicationContainer applicationContainer, applicationContainer2;
	
	protected Admission admission, admission2;
	//--------------------------------------------------------------------------

	@Override
	public void deploy() throws Exception{
		AbstractComponent.configureLogging("", "", 0, '|') ;
		Processor.DEBUG = true ;
		
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
		// Deploy the DataProvider Component
		this.addDeployedComponent(dataProvider);
		
		for (int i = 0; i < COMPUTER_NUMBER; i++) {
			String computerURI = COMPUTER_URI + i;
			System.out.println(computerURI);
			String csipURI = computerURI + "_CSIP";
			String csdipURI = computerURI + COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX;
			String cddipURI = computerURI + "_CDSDIP";
			
			ComputerAdaptable computer = new ComputerAdaptable(computerURI, admissibleFrequencies, processingPower, 1500, 1500,
					numberOfProcessors, numberOfCores, csipURI, csdipURI, cddipURI);
			this.addDeployedComponent(computer);
			
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
	
		this.admissionController = new AdmissionControllerCoordination("ADM_CONT", this);
		this.addDeployedComponent(admissionController);
		this.admissionController.connectWithDataProvider("DATA_PROVIDER");
		
		// Here we have to create the ApplicationVMAdaptable using the 
		// available resources in Computers
		
		
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
	
		applicationContainer.startAsync();
		applicationContainer2.startAsync();

	}
	
	
	public static void main(String[] args) {
		try {
			TestRingNetwork testAdmissionControler = new TestRingNetwork();
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

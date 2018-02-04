package fr.upmc.datacenter.software.step3.largescalecoordination.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.dataprovider.DataProvider;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.software.admissioncontroller.Admission;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.admissioncontrollercoordination.*;
import fr.upmc.datacenter.software.step2.adaptableproperty.ComputerAdaptable;
import fr.upmc.datacenter.software.step2.tools.DelployTools;

/**
 * This class shows a test of hosting two <code>ApplicationContainer</code>
 * 
 * In this case we use 3 <code>Computer</code> in our data center means 
 * that we can allocate 2 <code>Processor</code> with 3 <code>Cores</code> 
 * and the ApplicationVM allocate 4 cores to execute the 
 * received requests. The test will succeed because there are available 
 * resources for 2 <code>ApplicationConatiner</code>
 * 
 * 
 * 
 *  Test :  2 Computer 
 *  		2 * 2 Processors
 *  		8 Cores ( 2 cores for each ApplicationVM )
 *  		2 ApplicationContainer
 * 
 * @author Hacene KASDI
 *
 */
public class TestCooperationLargeScale extends fr.upmc.components.cvm.AbstractCVM{

	public TestCooperationLargeScale() throws Exception {
		super();
		// Set the AbstractCVM to deploy components
		DelployTools.setAcvm(this);
	}
	
	/**
	* All URIs and ports for first computer
	*/
	protected final static String COMPUTER_URI = "computer";
	protected final static String COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX = "csdip";
	
	//--------------------------------------------------------------------------
	// ADD THE COMPONENTS
	//--------------------------------------------------------------------------
	/**		 Admission Controller coordinator component										*/
	protected AdmissionControllerCoordination admissionController;
	/**		Application Container component								*/
	protected ApplicationContainerCooperation applicationContainer, applicationContainer2;
	
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
		int numberOfCores = 3;
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
		
		String computerURI 	= COMPUTER_URI;
		String csipURI 		= "_CSIP";
		String csdipURI 	= COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX;
		String cddipURI 	= "_CDSDIP";
		
		/** COMPUTER 1	*/
		ComputerAdaptable computer1 = new ComputerAdaptable(	computerURI+"1", admissibleFrequencies, processingPower, 1500, 1500,
															numberOfProcessors, numberOfCores, computerURI+"1"+csipURI, 
															computerURI+"1"+csdipURI, computerURI+"1"+cddipURI);
		this.addDeployedComponent(computer1);
		dataProvider.storeComputerData(computerURI+"1",admissibleFrequencies,processingPower, 1500,1500, numberOfProcessors,numberOfCores);

		/** COMPUTER 2	*/
		ComputerAdaptable computer2 = new ComputerAdaptable(	computerURI+"2", admissibleFrequencies, processingPower, 1500, 1500,
															numberOfProcessors, numberOfCores, computerURI+"2"+csipURI, 
															computerURI+"2"+csdipURI, computerURI+"2"+cddipURI);
		this.addDeployedComponent(computer2);
		dataProvider.storeComputerData(computerURI+"2",admissibleFrequencies,processingPower, 1500,1500, numberOfProcessors,numberOfCores);

		/** COMPUTER 3	*/
		ComputerAdaptable computer3 = new ComputerAdaptable(	computerURI+"3", admissibleFrequencies, processingPower, 1500, 1500,
															numberOfProcessors, numberOfCores, computerURI+"3"+csipURI, 
															computerURI+"3"+csdipURI, computerURI+"3"+cddipURI);
		this.addDeployedComponent(computer3);
		dataProvider.storeComputerData(computerURI+"3",admissibleFrequencies,processingPower, 1500,1500, numberOfProcessors,numberOfCores);
		
//		/** COMPUTER 4	*/
//		ComputerAdaptable computer4 = new ComputerAdaptable(	computerURI+"4", admissibleFrequencies, processingPower, 1500, 1500,
//															numberOfProcessors, numberOfCores, computerURI+"4"+csipURI, 
//															computerURI+"4"+csdipURI, computerURI+"4"+cddipURI);
//		this.addDeployedComponent(computer4);
//		dataProvider.storeComputerData(computerURI+"4",admissibleFrequencies,processingPower, 1500,1500, numberOfProcessors,numberOfCores);

//		ComputerAdaptable computer5 = new ComputerAdaptable(	computerURI+"5", admissibleFrequencies, processingPower, 1500, 1500,
//															numberOfProcessors, numberOfCores, computerURI+"5"+csipURI, 
//															computerURI+"5"+csdipURI, computerURI+"5"+cddipURI);
//		this.addDeployedComponent(computer5);
		
		

//		dataProvider.storeComputerData(computerURI+"5",admissibleFrequencies,processingPower, 1500,1500, numberOfProcessors,numberOfCores);
		
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
				new ApplicationContainerCooperation(
						"APP1-",
						this,
						admission,
						"_ANIP1",
						"_ACOP1");
		this.addDeployedComponent(applicationContainer);
		
		
		this.applicationContainer2 =
				new ApplicationContainerCooperation(
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
		this.admissionController.createAVMsAndDeploy();
		
		// Here we have to create the ApplicationVMAdaptable using the 
		// available resources in Computers
		
		
		/**
		 * CONNEXION OF THE COMPONENTS
		 */
		
		// ApplicationContainer and AdmissionController connections 
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
			TestCooperationLargeScale testAdmissionControler = new TestCooperationLargeScale();
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

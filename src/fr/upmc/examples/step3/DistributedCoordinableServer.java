package fr.upmc.examples.step3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.dataprovider.DataProvider;
import fr.upmc.datacenter.software.step2.adaptableproperty.ComputerAdaptable;
import fr.upmc.datacenter.software.step3.smallscalecoordination.coordinable.AdmissionControllerCoordinable;
import fr.upmc.datacenter.software.step3.smallscalecoordination.coordinator.ComputerCoordinator;

public class DistributedCoordinableServer extends AbstractDistributedCVM{
	
	public static String DATA_PROVIDER_URI = "DATA_PROVIDER";
	
	private int COMPUTER_NUMBER = 4;
	private final static String COMPUTER_URI = "computer";
	private final static String ADMISSION_CONTROLLER_URI = "ADM_CONT";
	private final static String COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX = "-csdip";
	
	public DistributedCoordinableServer(String[] args) throws Exception {
		super(args);
		
		String jvmURI = args[0];
		assert jvmURI != null && jvmURI.length() > 0;
		thisJVMURI = jvmURI;
		
		if(args.length > 2) {
			int computerNumber = Integer.parseInt(args[1]);
			assert computerNumber > 0;
			COMPUTER_NUMBER = computerNumber;
		}
	}
	
	@Override
	public void initialise() throws Exception {
		super.initialise();
	}
	
	@Override
	public void instantiateAndPublish() throws Exception {
		super.instantiateAndPublish();
		
		// Deploy the data provider
		DataProvider dataProvider = new DataProvider(DATA_PROVIDER_URI);
		this.addDeployedComponent(dataProvider);
		
		// Deploy all computers and coordinators
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
			String csipURI = computerURI + "_CSIP";
			String csdipURI = computerURI + COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX;
			String cddipURI = computerURI + "_CDSDIP";

			ComputerAdaptable computer = new ComputerAdaptable(computerURI, admissibleFrequencies, processingPower,
					1500, 1500, numberOfProcessors, numberOfCores, csipURI, csdipURI, cddipURI);
			this.addDeployedComponent(computer);

			ComputerCoordinator computerCoordinator = new ComputerCoordinator(computerURI, 500);
			this.addDeployedComponent(computerCoordinator);
			computerCoordinator.connectWithProvider("DATA_PROVIDER");
			
			System.out.println(String.format("DEPLOYING : %d-th computer deployed", i + 1));

			dataProvider.storeComputerData(computerURI, admissibleFrequencies, processingPower, 1500, 1500,
					numberOfProcessors, numberOfCores);
		}
		
		// Deploy the admission controller
		AdmissionControllerCoordinable admissionController = new AdmissionControllerCoordinable(ADMISSION_CONTROLLER_URI, this);
		this.addDeployedComponent(admissionController);
		admissionController.connectWithDataProvider("DATA_PROVIDER");
	}
	
	@Override
	public void interconnect() throws Exception {
		
	}
	
	@Override
	public void start() throws Exception {
		
	}
	
	public static void main(String[] args) {
		System.out.println("Beginning distribued application container " + args[0]);
		DistributedCoordinableServer dcs = null;
		try {
			dcs = new DistributedCoordinableServer(args);
			dcs.deploy();
			System.out.println("All component deployed");
			System.out.println("Start\n");
			dcs.start();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}

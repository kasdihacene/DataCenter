package fr.upmc.examples.admissioncontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissionController.AdmissionController;

public class DistribuedApplicationController extends AbstractDistributedCVM {

	protected AdmissionController ac;
	protected int computerNumber = 0;

	protected final static String COMPUTER_URI = "computer";
	protected final static String COMPUTER_MONITOR_URI = "monitor";
	protected final static String COMPUTER_SERVICE_INBOUND_PORT_SUFFIX = "csip";
	protected final static String COMPUTER_SERVICE_OUTBOUND_PORT_SUFFIX = "csop";
	protected final static String COMPUTER_STATIC_DATA_INBOUND_PORT_SUFFIX = "csdip";
	protected final static String COMPUTER_STATIC_DATA_OUTBOUND_PORT_SUFFIX = "csdop";
	protected final static String COMPUTER_DYNAMIC_DATA_INBOUND_PORT_SUFFIX = "cddip";
	protected final static String COMPUTER_DYNAMIC_DATA_OUTBOUND_PORT_SUFFIX = "cddop";
	protected ArrayList<Computer> computers = new ArrayList<Computer>();

	public DistribuedApplicationController(String[] args) throws Exception {
		
		super(args);
		
		String jvmURI = args[0];
		int computerNumber = Integer.parseInt(args[2]);
		
		assert jvmURI.equals(StaticData.ADMISSION_CONTROLLER_JVM_URI);
		assert computerNumber > 0;
		
		this.computerNumber = computerNumber;
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		
		super.instantiateAndPublish();
		
		/**
		 * Computer default configuration
		 */
		int numberOfProcessors = 2;
		int numberOfCores = 2;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>();
		admissibleFrequencies.add(1500); // Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000); // and at 3 GHz
		Map<Integer, Integer> processingPower = new HashMap<Integer, Integer>();
		processingPower.put(1500, 1500000); // 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000); // 3 GHz executes 3 Mips

		for (int i = 0; i < computerNumber; i++) {
			String computerURI = COMPUTER_URI + i;
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
			
			this.computers.add(computer);
			System.out.println("START computer " + i);
			cm.start();
			computer.start();
			System.out.println(String.format("DEPLOYING : %d-th computer deployed", i + 1));
		}

		this.ac = new AdmissionController(this, StaticData.ADMISSION_CONTROLLER_JVM_URI,
				StaticData.ADMISSION_REQUEST_INBOUND_PORT_URI, StaticData.ADMISSION_NOTIFICATION_OUTBOUND_PORT_URI,
				computers);
		this.ac.start();
	
	}

	@Override
	public void interconnect() throws Exception{
		super.interconnect();
	}
	
	@Override
	public void start() throws Exception{
		super.start();
	}
	
	public static void main(String[] args) {
		System.out.println("Beginning distribued Admission Controller");
		try {
			DistribuedApplicationController dac = new DistribuedApplicationController(args);
			dac.deploy();
			System.out.println("All component deployed");
			System.out.println("Start\n");
			dac.start();
			while(true) {}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}

package fr.upmc.examples.step3;

import java.util.Scanner;

import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.software.admissioncontroller.Admission;
import fr.upmc.datacenter.software.step2.ApplicationContainer;
import fr.upmc.datacenter.software.step2.tools.DelployTools;

public class DistributedCoordinableClient extends AbstractDistributedCVM{
	
	public static String DATA_PROVIDER_URI = "DATA_PROVIDER";
	
	private static int CORES_BY_AVM = 4;
	private final static String ADMISSION_CONTROLLER_URI = "ADM_CONT";
	private final static String ADMISSION_NOTIFICATION_INBOUND_PORT_SUFFIX = "_ANIP";
	private final static String ADMISSION_CONTROLLER_INBOUND_PORT_SUFFIX = "_ACIP";
	private final static String ADMISSION_CONTROLLER_OUTBOUND_PORT_SUFFIX = "_ACOP";
	private final static String DATA_PROVIDER_INBOUND_PORT_SUFFIX = "_DPIP";
	
	private ApplicationContainer applicationContainer = null;
	
	public DistributedCoordinableClient(String[] args) throws Exception {
		super(args);
		
		String jvmURI = args[0];
		assert jvmURI != null && jvmURI.length() > 0;
		thisJVMURI = jvmURI;
		
		if(args.length > 2) {
			int coresByAVM = Integer.parseInt(args[1]);
			assert coresByAVM > 0;
			CORES_BY_AVM = coresByAVM;
		}
	}
	
	@Override
	public void initialise() throws Exception {
		super.initialise();
		DelployTools.setAcvm(this);
	}
	
	@Override
	public void instantiateAndPublish() throws Exception {
		super.instantiateAndPublish();
		Admission admission = new Admission(ADMISSION_NOTIFICATION_INBOUND_PORT_SUFFIX, ADMISSION_CONTROLLER_INBOUND_PORT_SUFFIX);
		applicationContainer = new ApplicationContainer(thisJVMURI, this, admission, ADMISSION_NOTIFICATION_INBOUND_PORT_SUFFIX, ADMISSION_CONTROLLER_OUTBOUND_PORT_SUFFIX);
		this.addDeployedComponent(applicationContainer);
	}
	
	@Override
	public void interconnect() throws Exception {
		super.interconnect();
		applicationContainer.connectWithAdmissionController(ADMISSION_CONTROLLER_URI + ADMISSION_CONTROLLER_INBOUND_PORT_SUFFIX);
	}
	
	@Override
	public void start() throws Exception {
		super.start();
		System.out.println("before ask for hosting");
		applicationContainer.askForHostingApllication();	
	}
	
	public static void main(String[] args) {
		System.out.println("Beginning distribued application container " + args[0]);
		DistributedCoordinableClient dcc = null;
		try {
			dcc = new DistributedCoordinableClient(args);
			dcc.deploy();
			System.out.println("All component deployed");
			System.out.println("Start\n");
			dcc.start();
			System.out.println(">>>> TYPE ENTER TO CLOSE APPLICATION <<<<");
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			sc.close();
		}catch(Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
		System.out.println(String.format("CLIENT <%s> CLOSED", thisJVMURI));
	}
}

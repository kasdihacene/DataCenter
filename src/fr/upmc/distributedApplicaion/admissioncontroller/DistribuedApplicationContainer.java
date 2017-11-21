package fr.upmc.distributedApplicaion.admissioncontroller;

import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.software.admissionController.Admission;
import fr.upmc.datacenter.software.applicationcontainer.ApplicationContainer;

public class DistribuedApplicationContainer extends AbstractDistributedCVM{

	protected ApplicationContainer ac;

	public DistribuedApplicationContainer(String[] args) throws Exception {
		super(args);

		String jvmURI = args[0];
		
		assert jvmURI.equals(StaticData.APPLICATION_CONTAINER_JVM_URI0)
				|| jvmURI.equals(StaticData.APPLICATION_CONTAINER_JVM_URI1);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		super.instantiateAndPublish();
		Admission admission;
		if (this.thisJVMURI.equals(StaticData.APPLICATION_CONTAINER_JVM_URI0)) {
			admission = new Admission(this, StaticData.AC_ADMISSION_NOTIFICATION_INBOUND_PORT0,
					StaticData.AC_REQUEST_SUBMISSION_OUTBOUND_PORT0);
			this.ac = new ApplicationContainer(StaticData.APPLICATION_CONTAINER_JVM_URI0, admission,
					StaticData.AC_ADMISSION_NOTIFICATION_INBOUND_PORT0,
					StaticData.AC_REQUEST_SUBMISSION_OUTBOUND_PORT0);
		}
		if (this.thisJVMURI.equals(StaticData.APPLICATION_CONTAINER_JVM_URI1)) {
			admission = new Admission(this, StaticData.AC_ADMISSION_NOTIFICATION_INBOUND_PORT1,
					StaticData.AC_REQUEST_SUBMISSION_OUTBOUND_PORT1);
			this.ac = new ApplicationContainer(StaticData.APPLICATION_CONTAINER_JVM_URI1, admission,
					StaticData.AC_ADMISSION_NOTIFICATION_INBOUND_PORT1,
					StaticData.AC_REQUEST_SUBMISSION_OUTBOUND_PORT1);
		}
	}
	
	@Override
	public void interconnect() throws Exception{
		super.interconnect();
		this.ac.connectWithAdmissionController(StaticData.ADMISSION_REQUEST_INBOUND_PORT_URI);
	}
	
	@Override
	public void start() throws Exception{
		super.start();
		this.ac.askForHostingApllication();
	}
	
	public static void main(String[] args) {
		System.out.println("Beginning distribued application container " + args[0]);
		try {
			DistribuedApplicationContainer dac = new DistribuedApplicationContainer(args);
			dac.deploy();
			System.out.println("All component deployed");
			System.out.println("Start\n");
			dac.start();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Admission Controller ending");
		System.exit(0);
	}
}

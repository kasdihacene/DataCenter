package fr.upmc.examples.admissioncontroller;

import java.util.Scanner;

import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.software.admissioncontroller.Admission;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.applicationcontainer.ApplicationContainer;

/**
 * This class create an <code>AbstractDistributedCVM</code> and add on this
 * <code>AbstractDistributedCVM</code> an <code>ApplicationContainer</code> component
 * Must launch with 1 argument, the JVM name
 * 
 * @author Hacene Kasdi & Marc Ren
 *
 */

public class DistribuedApplicationContainer extends AbstractDistributedCVM {

	protected String jvmURI;
	protected ApplicationContainer ac;

	public DistribuedApplicationContainer(String[] args) throws Exception {
		super(args);

		String jvmURI = args[0];

		assert jvmURI != null;

		this.jvmURI = jvmURI;
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		super.instantiateAndPublish();
		String anip = this.jvmURI + StaticData.AC_ADMISSION_NOTIFICATION_INBOUND_SUFFIX;
		String rsop = this.jvmURI + StaticData.AC_REQUEST_SUBMISSION_OUTBOUND_SUFFIX;
		AdmissionI admission = new Admission(anip, rsop);
		this.ac = new ApplicationContainer(this.jvmURI, this, admission, anip, rsop);
	}

	@Override
	public void interconnect() throws Exception {
		super.interconnect();
		this.ac.connectWithAdmissionController(StaticData.ADMISSION_REQUEST_INBOUND_PORT_URI);
	}

	@Override
	public void start() throws Exception {
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
			System.out.println(">>>> TYPE ENTER TO CLOSE APPLICATION <<<<");
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			sc.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("APPLICATION CLOSED");
		System.exit(0);
	}
}

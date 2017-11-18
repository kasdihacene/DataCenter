package fr.upmc.datacenter.software.javassist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.requestDispatcher.RequestDispatcher;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class JavassistUtility {

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
	protected static ApplicationVMManagementOutboundPort avmPort0;

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
	protected static ApplicationVMManagementOutboundPort avmPort1;

	protected Computer c0, c1;
	protected ComputerMonitor cm0, cm1;
	protected static ApplicationVM avm0, avm1;

	// PREDIFINED URI OF PORTS
	public static final String RequestSubmissionInboundPortURI = "rsibp";
	public static final String RequestNotificationInboundPortURI = "rnibp";
	public static final String RequestNotificationOutboundPortURI = "rnobp";

	/** Request Dispatcher component. */
	protected RequestDispatcher rd;

	public JavassistUtility() {
		super();
	}

	public static void createRequestDispatcher(

			AdmissionI admission, ArrayList<Computer> listComputers) throws Exception {

		// --------------------------------------------------------------------
		// Create and deploy first avm component
		// --------------------------------------------------------------------
		System.out.println("JAVASSIST");
		AbstractCVM absCVM = admission.getAbstractCVM();
		avm0 = new ApplicationVM(avmURI0, // application vm component URI
				ApplicationVMManagementInboundPortURI0, RequestSubmissionInboundPortURI0,
				RequestNotificationOutboundPortURI0);
		absCVM.addDeployedComponent(avm0);

		// Create a mock up port to manage the AVM component (allocate cores).
		avmPort0 = new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI0,
				new AbstractComponent(0, 0) {
				});
		avmPort0.publishPort();
		avmPort0.doConnection(ApplicationVMManagementInboundPortURI0,
				ApplicationVMManagementConnector.class.getCanonicalName());

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		avm0.toggleTracing();
		avm0.toggleLogging();

		// --------------------------------------------------------------------
		// Create and deploy second avm component
		// --------------------------------------------------------------------
		avm1 = new ApplicationVM(avmURI1, // application vm component URI
				ApplicationVMManagementInboundPortURI1, RequestSubmissionInboundPortURI1,
				RequestNotificationOutboundPortURI1);
		absCVM.addDeployedComponent(avm1);

		// Create a mock up port to manage the AVM component (allocate cores).
		avmPort1 = new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI1,
				new AbstractComponent(0, 0) {
				});
		avmPort1.publishPort();
		avmPort1.doConnection(ApplicationVMManagementInboundPortURI1,
				ApplicationVMManagementConnector.class.getCanonicalName());

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		avm1.toggleTracing();
		avm1.toggleLogging();

		// --------------------------------------------------------------------
		// Creating the request Dispatcher component.
		// --------------------------------------------------------------------
		RequestDispatcher rd = new RequestDispatcher("RDispatcher",
				RequestSubmissionInboundPortURI + admission.getApplicationURI(),
				RequestNotificationOutboundPortURI + admission.getApplicationURI());
		absCVM.addDeployedComponent(rd);

		// Allocate the 4 cores of the computer to the application virtual machine
		AllocatedCore[] ac0 = listComputers.get(0).allocateCores(4);
		avmPort0.allocateCores(ac0);

		AllocatedCore[] ac1 = listComputers.get(1).allocateCores(4);
		avmPort1.allocateCores(ac1);

		rd.connectAVM(avmURI0, RequestSubmissionInboundPortURI0, RequestNotificationOutboundPortURI0);
		rd.connectAVM(avmURI1, RequestSubmissionInboundPortURI1, RequestNotificationOutboundPortURI1);

		String RSIP = RequestSubmissionInboundPortURI + admission.getApplicationURI();
		admission.setRequestSubmissionInboundPortRD(RSIP);
		System.out.println("============ REQUEST DISPATCHER CREATED ============ ");
	}

	public static Class<?> makeConnectorClassJavassist(String connectorCanonicalClassName, Class<?> connectorSuperclass,
			Class<?> connectorImplementedInterface, Class<?> offeredInterface, HashMap<String, String> methodNamesMap)
			throws Exception {

		ClassPool pool = ClassPool.getDefault();
		CtClass cs = pool.get(connectorSuperclass.getCanonicalName());
		CtClass cii = pool.get(connectorImplementedInterface.getCanonicalName());
		CtClass oi = pool.get(offeredInterface.getCanonicalName());
		CtClass connectorCtClass = pool.makeClass(connectorCanonicalClassName);

		connectorCtClass.setSuperclass(cs);
		Method[] methodsToImplement = connectorImplementedInterface.getDeclaredMethods();
		for (int i = 0; i < methodsToImplement.length; i++) {
			String source = "public ";
			source += methodsToImplement[i].getReturnType().getName() + " ";
			source += methodsToImplement[i].getName() + "(";
			Class<?>[] pt = methodsToImplement[i].getParameterTypes();
			String callParam = "";
			for (int j = 0; j < pt.length; j++) {
				String pName = "aaa" + j;
				source += pt[j].getCanonicalName() + " " + pName;
				callParam += pName;
				if (j < pt.length - 1) {
					source += ", ";
					callParam += ", ";
				}
			}
			source += ")";
			Class<?>[] et = methodsToImplement[i].getExceptionTypes();
			if (et != null && et.length > 0) {
				source += " throws ";

				for (int z = 0; z < et.length; z++) {
					source += et[z].getCanonicalName();
					if (z < et.length - 1) {
						source += ",";
					}
				}
			}
			source += "\n{ return ((";
			source += offeredInterface.getCanonicalName() + ")this.offering).";
			source += methodNamesMap.get(methodsToImplement[i].getName());
			source += "(" + callParam + ") ;\n}";
			CtMethod theCtMethod = CtMethod.make(source, connectorCtClass);
			connectorCtClass.addMethod(theCtMethod);
		}
		connectorCtClass.setInterfaces(new CtClass[] { cii });
		cii.detach();
		cs.detach();
		if (!connectorImplementedInterface.equals(offeredInterface))
			oi.detach();
		Class<?> ret = connectorCtClass.toClass();
		connectorCtClass.detach();

		return ret;
	}

}

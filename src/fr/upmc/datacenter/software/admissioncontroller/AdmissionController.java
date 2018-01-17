package fr.upmc.datacenter.software.admissioncontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreator;
import fr.upmc.components.pre.reflection.connectors.ReflectionConnector;
import fr.upmc.components.pre.reflection.ports.ReflectionOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionRequestInboundPort;
import fr.upmc.datacenter.software.applicationcontainer.connectors.AdmissionNotificationConnector;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.upmc.datacenter.software.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

/**
 * <p>
 * <strong>Description</string>
 * </p>
 * 
 * This component <code>AdmissionController</code> receives the requests from
 * the Costumers in this context we can consider Costumers as
 * <code>ApplicationContainer</code> who want to host their applications
 * <code>RequestI</code> of the <code>RequestGenerator</code>. the controller
 * can accept if there is some available resources or refuse if all computers
 * are busy on handling the requests of other Costumers.
 * 
 * This Class knows the state of all computers available, Many methods can check
 * the state of Cores of Processors.
 * 
 * 
 * @author Hacene Kasdi & Marc REN
 * @version 2017.10.20.HK
 */

public class AdmissionController extends AbstractComponent implements AdmissionRequestHandlerI {

	private int CORES_BY_AVM = 2;
	private int AVMS_BY_APP = 2;

	private static final String LOCAL_AVM_URI = "CONTROLLER-avm";
	private static final String LOCAL_REQUEST_DISPATCHER_URI = "CONTROLLER-dispatcher";
	private static final String LOCAL_AVM_MANAGEMENT_INPORT_SUFFIX = "mibp";
	private static final String LOCAL_AVM_MANAGEMENT_OUTPORT_SUFFIX = "mobp";
	private static final String LOCAL_REQUEST_SUB_INPORT_PORT_SUFFIX = "rsibp";
	private static final String LOCAL_REQUEST_NOT_INPORT_PORT_SUFFIX = "rsnibp";
	private static final String LOCAL_REQUEST_NOT_OUTPORT_PORT_SUFFIX = "rnobp";
	private static final String LOCAL_RD_MANAGEMENT_INBOUND_PORT_SUFFIX = "rdmip";
	private static final String LOCAL_RD_MANAGEMENT_OUTBOUND_PORT_SUFFIX = "rdmop";
	
	/*
	 * COMPONENT TO CREATE DYNAMICALLY COMPONENTS
	 * TODO : CALL THE COMPONENT BY AN DYNAMIC COMPONENT CREATOR OUTBOUND PORT
	 */
	protected DynamicComponentCreator dynamicComponentCreator;
	protected final String LOCAL_DYNAMIC_COMPONENT_CREATOR_URI = "creator";
	protected final String DCC_INBOUND_PORT_URI = "creator-ip";
	
	protected ReflectionOutboundPort rop;
	
	protected String acURI;
	protected AbstractCVM cvm = null;
	protected String anopURI;

	/**
	 * All ports needed to handle application submission and notify
	 */
	protected AdmissionRequestInboundPort admissionRequestInboundPort;
	protected AdmissionNotificationOutboundPort admissionNotificationOutboundPort;

	protected List<String> applications = new ArrayList<String>();
	protected List<Computer> computers = new ArrayList<Computer>();
	protected Map<String, ApplicationVMManagementOutboundPort> avms = new HashMap<String, ApplicationVMManagementOutboundPort>();
	protected Map<String, RequestDispatcherManagementOutboundPort> dispatchers = new HashMap<String, RequestDispatcherManagementOutboundPort>();
	protected Map<String, String> applicationDispatcher = new HashMap<String, String>();

	public AdmissionController(
			String acURI, 
			AbstractCVM cvm, 
			String asipURI, 
			String anopURI) throws Exception {

		super(1, 1);

		/**
		 * PRECONDITIONS
		 */

		assert acURI != null;
		assert asipURI != null;
		assert anopURI != null;
		assert cvm != null;

		this.acURI = acURI;

		this.cvm = cvm;

		this.anopURI = anopURI;

		/**
		 * A PORT TO RECEIVE APPLICATION REQUEST
		 */
		this.addOfferedInterface(AdmissionRequestI.class);
		this.admissionRequestInboundPort = new AdmissionRequestInboundPort(asipURI, this);
		this.addPort(this.admissionRequestInboundPort);
		this.admissionRequestInboundPort.publishPort();

		/**
		 * A PORT TO NOTIFY APPLICATION ADMISSION
		 */
		this.addRequiredInterface(AdmissionNotificationI.class);
		this.admissionNotificationOutboundPort = new AdmissionNotificationOutboundPort(anopURI, this);
		this.addPort(this.admissionNotificationOutboundPort);
		this.admissionNotificationOutboundPort.publishPort();
		
		
		/**
		 * CREATING DCC
		 */
		this.dynamicComponentCreator = new DynamicComponentCreator(DCC_INBOUND_PORT_URI);
		this.cvm.addDeployedComponent(this.dynamicComponentCreator);
		
		/**
		 * CREATING REFLECTION OUTBOUND PORT
		 */
		this.rop = new ReflectionOutboundPort(this);
		this.addPort(this.rop);
		this.rop.localPublishPort();
	}

	public AdmissionController(String acURI, AbstractCVM cvm, int cores, String asipURI, String anopURI)
			throws Exception {
		this(acURI, cvm, asipURI, anopURI);
		assert cores > 0;
		this.CORES_BY_AVM = cores;
	}

	public AdmissionController(String acURI, AbstractCVM cvm, String asipURI, String anopURI, List<Computer> computers)
			throws Exception {
		this(acURI, cvm, asipURI, anopURI);
		assert computers.size() > 0;
		this.computers = computers;
	}

	public AdmissionController(String acURI, AbstractCVM cvm, int cores, String asipURI, String anopURI,
			List<Computer> computers) throws Exception {
		this(acURI, cvm, cores, asipURI, anopURI);
		assert computers.size() > 0;
		this.computers = computers;
	}

	/*
	 * ALL FUNCTIONS TO GET COMPUTER CORES STATE
	 */
	/**
	 * 
	 * @return number of Cores (idle and allocated)
	 * @throws Exception
	 */
	public int getCoreNumber() throws Exception {
		return getCoreNumber(0);
	}

	/**
	 * 
	 * @return number of Idle cores
	 * @throws Exception
	 */
	public int getIdleCoreNumber() throws Exception {
		return getCoreNumber(1);
	}

	/**
	 * 
	 * @param state
	 *            : 0 for all cores, 1 for just idle ones, -1 for just allocated
	 *            ones
	 * @return Number of Cores according to the chosen state
	 * @throws Exception
	 */
	public int getCoreNumber(int state) throws Exception {
		int res = 0;
		for (Computer computer : computers)
			res += getCoreNumber(computer, state);
		return res;
	}

	/**
	 * 
	 * @return true if got idle core unless false
	 * @throws Exception
	 */
	public boolean gotIdleCore() throws Exception {
		for (Computer c : computers) {
			ComputerStaticStateI css = c.getStaticState();
			ComputerDynamicStateI cds = c.getDynamicState();
			boolean[][] processorsCoresState = cds.getCurrentCoreReservations();
			int nbProcessors = css.getNumberOfProcessors();
			int nbCoresByProcessor = css.getNumberOfCoresPerProcessor();

			for (int i = 0; i < nbProcessors; i++)
				for (int j = 0; j < nbCoresByProcessor; j++)
					if (!processorsCoresState[i][j])
						return true;
		}

		return false;
	}

	/**
	 * 
	 * @param c
	 *            : <code>Computer</code>
	 * @param state
	 *            : 0 for all cores, 1 for just idle ones, -1 for just allocated
	 *            ones
	 * @return Number of Cores of Computer c according to the state
	 * @throws Exception
	 */
	public int getCoreNumber(Computer c, int state) throws Exception {
		int res = 0;
		ComputerStaticStateI css = c.getStaticState();
		ComputerDynamicStateI cds = c.getDynamicState();
		boolean[][] processorsCoresState = cds.getCurrentCoreReservations();
		int nbProcessors = css.getNumberOfProcessors();
		int nbCoresByProcessor = css.getNumberOfCoresPerProcessor();

		for (int i = 0; i < nbProcessors; i++) {
			for (int j = 0; j < nbCoresByProcessor; j++) {
				if (state == 0)
					res++;
				if (state == 1 && !processorsCoresState[i][j])
					res++;
				if (state == -1 && processorsCoresState[i][j])
					res++;
			}
		}
		return res;
	}

	/**
	 * 
	 * @param c
	 *            <code>Computer</code>
	 * @return Number of Idle cores of a specific <code>Computer</code>
	 * @throws Exception
	 */
	public int getIdleCoreNumber(Computer c) throws Exception {
		return getCoreNumber(c, 1);
	}

	/*
	 * ALLOCATING FUNCTIONS
	 */
	/**
	 * 
	 * @param number
	 *            : Number of core to allocate
	 * @return <code>AllocatedCore</code> array with length <= number
	 * @throws Exception
	 */
	public AllocatedCore[] allocateCores(int number) throws Exception {
		assert number > 0;

		System.out.println(String.format("CONTROLLER<%s>: try to allocate %d cores", this.acURI, number));
		int allocatedCoreNumber = 0;
		ArrayList<AllocatedCore> allocatedCore = new ArrayList<AllocatedCore>();
		for (Computer c : computers) {
			int computerIdleCoresNumber = getIdleCoreNumber(c);
			int allocateCoreNumber = computerIdleCoresNumber > (number - allocatedCoreNumber)
					? number - allocatedCoreNumber
					: computerIdleCoresNumber;
			if (allocateCoreNumber > 0) {
				allocatedCore.addAll(Arrays.asList((c.allocateCores(allocateCoreNumber))));
				allocatedCoreNumber += allocateCoreNumber;
			}
			if (allocatedCoreNumber >= number)
				break;
		}

		AllocatedCore[] cores = new AllocatedCore[allocatedCore.size()];
		cores = allocatedCore.toArray(cores);

		System.out.println(String.format("CONTROLLER<%s>: %d cores allocated", this.acURI, cores.length));
		return cores;
	}

	/**
	 * 
	 * @param coreNumber
	 *            : Number of core wanted
	 * @param notificationInboundPortURI
	 *            : URI of the port to get notified
	 * @return URI of the allocated <code>ApplicationVM</code> with number of cores
	 *         less or equal than coreNumber
	 * @throws Exception
	 */
	public String allocateAVM(int coreNumber, String notificationInboundPortURI) throws Exception {
		AllocatedCore[] cores = this.allocateCores(coreNumber);
		return allocateAVM(cores, notificationInboundPortURI);
	}

	/**
	 * 
	 * @param cores
	 *            : <code>AllocatedCore</code> array to connect with avm
	 * @param notificationInboundPortURI
	 *            : URI of the port to get notified
	 * @return URI of the allocated <code>ApplicationVM</code> connected with each
	 *         <code>AllocatedCore</code> in cores
	 * @throws Exception
	 */
	public String allocateAVM(AllocatedCore[] cores, String notificationInboundPortURI) throws Exception {

		String avmURI = LOCAL_AVM_URI + avms.values().size();
		System.out.println(
				String.format("CONTROLLER<%s>: Try to start avm<%s> with %d cores", this.acURI, avmURI, cores.length));
		avms.put(avmURI, null);
		String ApplicationVMManagementInboundPortURI = avmURI + LOCAL_AVM_MANAGEMENT_INPORT_SUFFIX;
		String ApplicationVMManagementOutboundPortURI = avmURI + LOCAL_AVM_MANAGEMENT_OUTPORT_SUFFIX;
		String RequestSubmissionInboundPortURI = avmURI + LOCAL_REQUEST_SUB_INPORT_PORT_SUFFIX;
		String RequestNotificationOutboundPortURI = avmURI + LOCAL_REQUEST_NOT_OUTPORT_PORT_SUFFIX;

		System.out.println(String.format("CONTROLLER<%s>: create avm<%s>", this.acURI, avmURI));
		this.dynamicComponentCreator.createComponent(ApplicationVM.class.getCanonicalName(),
				new Object[] { avmURI, ApplicationVMManagementInboundPortURI, RequestSubmissionInboundPortURI,
						RequestNotificationOutboundPortURI });
		ApplicationVMManagementOutboundPort avmPort = new ApplicationVMManagementOutboundPort(
				ApplicationVMManagementOutboundPortURI, new AbstractComponent(0, 0) {
				});
		avmPort.publishPort();
		avmPort.doConnection(ApplicationVMManagementInboundPortURI,
				ApplicationVMManagementConnector.class.getCanonicalName());

		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.

		avms.put(avmURI, avmPort);
		avmPort.allocateCores(cores);
		System.out.println(
				String.format("CONTROLLER<%s>: Start AVM<%s> with %d cores", this.acURI, avmURI, cores.length));

		return avmURI;
	}

	public void connectAVMNotificationPort(String avmURI, String notificationInboundPortURI) throws Exception {
		System.out.println("TRY TO CONNECT TO AVM");
		this.rop.doConnection(avmURI, ReflectionConnector.class.getCanonicalName());
		System.out.println("CONNECTED TO AVM, TRY TO DO PORT CONNECTION");
		this.rop.doPortConnection(avmURI + LOCAL_REQUEST_NOT_OUTPORT_PORT_SUFFIX, notificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName());
		System.out.println("PORTS CONNECTED, TRY TO DISCONNECT REFLECTION OUTBOUND PORT");
		this.rop.doDisconnection();
	}

	/**
	 * 
	 * @param avmNumber
	 *            : number of avm to allocate
	 * @param notificationInboundPortURI
	 *            : URI of the port to get notified
	 * @return
	 * @throws Exception
	 */
	public List<String> allocateAvms(int avmNumber, String notificationInboundPortURI) throws Exception {
		List<String> avmURIs = new ArrayList<>();
		for (int i = 0; i < AVMS_BY_APP; i++) {
			int idleCoreNumber = getIdleCoreNumber();
			String avmURI;
			if (idleCoreNumber > 0) {
				if (idleCoreNumber > CORES_BY_AVM) {
					avmURI = allocateAVM(CORES_BY_AVM, notificationInboundPortURI);
				} else {
					if (idleCoreNumber < CORES_BY_AVM)
						System.out.println(
								String.format("CONTROLLER<%s>: expected %d cores for the avm but remain %d cores",
										this.acURI, CORES_BY_AVM, idleCoreNumber));
					avmURI = allocateAVM(idleCoreNumber, notificationInboundPortURI);
				}
				connectAVMNotificationPort(avmURI, notificationInboundPortURI);
				avmURIs.add(avmURI);
			} else {
				System.out.println(String.format("CONTROLLER<%s>: no more cores for creating new avm", this.acURI));
			}
		}
		return avmURIs;
	}

	/**
	 * 
	 * @param admission
	 *            : <code>AdmissionI</code> that describe requests
	 *            <code>AdmissionI</code>
	 * @return URI of the <code>RequestSubmissionOutboundPort</code> allocated
	 *         <code>RequestDispatcher</code>
	 * @throws Exception
	 */
	public String allocateDispatcher(AdmissionI admission) throws Exception {

		System.out.println(
				String.format("CONTROLLER<%s>: Try to allocate a request dispatcher for application<%s> with %d avms",
						this.acURI, admission.getApplicationURI(), AVMS_BY_APP));

		int dispatcherNumber = dispatchers.values().size();
		String dispatcherURI = LOCAL_REQUEST_DISPATCHER_URI + dispatcherNumber;
		dispatchers.put(dispatcherURI, null);
		String dispatcherMIP = dispatcherURI + LOCAL_RD_MANAGEMENT_INBOUND_PORT_SUFFIX;
		String dispatcherMOP = dispatcherURI + LOCAL_RD_MANAGEMENT_OUTBOUND_PORT_SUFFIX;
		String dispatcherRSIP = dispatcherURI + LOCAL_REQUEST_SUB_INPORT_PORT_SUFFIX;
		String dispatcherRNIP = dispatcherURI + LOCAL_REQUEST_NOT_INPORT_PORT_SUFFIX;
		String dispatcherRNOP = dispatcherURI + LOCAL_REQUEST_NOT_OUTPORT_PORT_SUFFIX;

		System.out.println(String.format("CONTROLLER<%s>: create dispatcher<%s>", this.acURI, dispatcherURI));
		this.dynamicComponentCreator.createComponent(RequestDispatcher.class.getCanonicalName(),
				new Object[] { dispatcherURI, dispatcherMIP, dispatcherRSIP, dispatcherRNIP, dispatcherRNOP });
		System.out.println(
				String.format("CONTROLLER<%s>: create dispatcher outbound port<%s>", this.acURI, dispatcherMOP));
		RequestDispatcherManagementOutboundPort rdop = new RequestDispatcherManagementOutboundPort(dispatcherMOP,
				new AbstractComponent(0, 0) {
				});
		rdop.publishPort();
		rdop.doConnection(dispatcherMIP, RequestDispatcherManagementConnector.class.getCanonicalName());
		dispatchers.put(dispatcherURI, rdop);

		System.out.println(String.format("CONTROLLER<%s>: try to allocate %d avms for application<%s>", this.acURI,
				AVMS_BY_APP, admission.getApplicationURI()));
		List<String> avmURIs = allocateAvms(AVMS_BY_APP, dispatcherRNIP);
		for (String avmURI : avmURIs)
			rdop.connectAVM(avmURI, avmURI + LOCAL_REQUEST_SUB_INPORT_PORT_SUFFIX);
		rdop.connectNotificationOutboundPort(admission.getAdmissionNotificationInboundPortURI());

		System.out.println(String.format("CONTROLLER<%s>: dispatcher<%s> allocated for application<%s> with %d avms",
				this.acURI, dispatcherURI, admission.getApplicationURI(), avmURIs.size()));
		return dispatcherRSIP;
	}

	/*
	 * ALL FUNCTIONS FOR HANDLING ADMISSION REQUEST
	 */
	/**
	 * Notify the owner of the requested <code>AdmissionI</code>
	 * 
	 * @param admission
	 *            : <code>AdmissionI</code> that describe requested admission
	 * @throws Exception
	 */
	public void notify(AdmissionI admission) throws Exception {
		/*
		 * Generate a Class Connector (AdmissionNotificationConnector) using the
		 * abstract method of the class JavassistUtility using Javassist
		 */
		// HashMap<String, String> mapMethods = new HashMap<String, String>();
		// mapMethods.put("notifyAdmissionNotification", "notifyAdmissionNotification");
		// Class<?> admissionConnector = JavassistUtility.makeConnectorClassJavassist(
		// "AdmissionNotificationConnector",
		// AbstractConnector.class,
		// AdmissionNotificationI.class,
		// AdmissionNotificationI.class,
		// mapMethods);
		// this.admissionNotificationOutboundPort.doConnection(admission.getAdmissionNotificationInboundPortURI(),
		// admissionConnector.getCanonicalName());
		this.admissionNotificationOutboundPort.doConnection(admission.getAdmissionNotificationInboundPortURI(),
				AdmissionNotificationConnector.class.getCanonicalName());
		this.admissionNotificationOutboundPort.notifyAdmissionNotification(admission);
		if (admission.isAllowed())
			System.out.println(String.format("CONTROLLER<%s>: notify owner of application<%s>, request accepted",
					this.acURI, admission.getApplicationURI()));
		else
			System.out.println(String.format("CONTROLLER<%s>: notify owner of application<%s>, request refused",
					this.acURI, admission.getApplicationURI()));
		this.admissionNotificationOutboundPort.doDisconnection();
	}

	/**
	 * Inspect the resources in the computers, if there is any available Core than
	 * we call and answer the other side witch is the ApplicationContainer
	 */
	@Override
	synchronized public void inspectResources(AdmissionI admission) throws Exception {
		System.out.println(
				String.format("CONTROLLER<%s>: receive request for application <%s>, starting ressources inspection",
						this.acURI, admission.getApplicationURI()));
		if (gotIdleCore()) {
			System.out.println(String.format("CONTROLLER<%s>: got idle core", this.acURI));
			String requestURI = allocateDispatcher(admission);
			admission.setRequestSubmissionInboundPortRD(requestURI);
			admission.setAllowed(true);
			notify(admission);

		} else {
			System.out.println(String.format("CONTROLLER<%s>: no idle core", this.acURI));
			admission.setAllowed(false);
			notify(admission);
		}
	}

	@Override
	public void inspectResourcesAndNotifiy(AdmissionI admission) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdownServices(String URI) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
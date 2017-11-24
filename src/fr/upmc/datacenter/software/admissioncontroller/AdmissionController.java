package fr.upmc.datacenter.software.admissioncontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreator;
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
 * @version 2012.10.20.HK
 */

public class AdmissionController extends AbstractComponent implements AdmissionRequestHandlerI {

	private int CORES_BY_AVM = 3;
	private static final String LOCAL_AVM_URI = "CONTROLLER-avm";
	private static final String LOCAL_REQUEST_DISPATCHER_URI = "CONTROLLER-dispatcher";
	private static final String LOCAL_AVM_MANAGEMENT_INPORT_SUFFIX = "mibp";
	private static final String LOCAL_AVM_MANAGEMENT_OUTPORT_SUFFIX = "mobp";
	private static final String LOCAL_REQUEST_SUB_INPORT_PORT_SUFFIX = "rsibp";
	private static final String LOCAL_REQUEST_NOT_INPORT_PORT_SUFFIX = "rsnibp";
	private static final String LOCAL_REQUEST_NOT_OUTPORT_PORT_SUFFIX = "rnobp";
	private static final String LOCAL_RD_MANAGEMENT_INBOUND_PORT_SUFFIX = "rdmip";
	private static final String LOCAL_RD_MANAGEMENT_OUTBOUND_PORT_SUFFIX = "rdmop";

	protected DynamicComponentCreator dynamicComponentCreator;
	protected final String LOCAL_DYNAMIC_COMPONENT_CREATOR_URI = "creator";
	protected final String DCC_INBOUND_PORT_URI = "creator-ip";

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
	
	public AdmissionController(String acURI, AbstractCVM cvm, String asipURI, String anopURI) throws Exception {

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

		this.dynamicComponentCreator = new DynamicComponentCreator(DCC_INBOUND_PORT_URI);
		this.cvm.addDeployedComponent(this.dynamicComponentCreator);

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
	
	public AdmissionController(String acURI, AbstractCVM cvm, int cores, String asipURI, String anopURI, List<Computer> computers)
			throws Exception {
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

	public String allocateAVM(int coreNumber) throws Exception {
		AllocatedCore[] cores = this.allocateCores(coreNumber);
		return allocateAVM(cores);
	}

	public String allocateAVM(AllocatedCore[] cores) throws Exception {
		
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

	public String allocateDispatcher(AdmissionI admission) throws Exception {

		System.out.println(String.format("CONTROLLER<%s>: Try to allocate a request dispatcher for application<%s> with %d avms",
				this.acURI, admission.getApplicationURI(), admission.getAVMNumber()));

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

		int avmNumber = admission.getAVMNumber();
		System.out.println(String.format("CONTROLLER<%s>: try to allocate %d avms for application<%s>", this.acURI,
				admission.getAVMNumber(), admission.getApplicationURI()));
		int i;
		for (i = 0; i < avmNumber; i++) {
			int idleCoreNumber = getIdleCoreNumber();
			String avmURI;
			if (idleCoreNumber > 0) {
				if (idleCoreNumber > CORES_BY_AVM) {
					avmURI = allocateAVM(CORES_BY_AVM);
				} else {
					if (idleCoreNumber < CORES_BY_AVM)
						System.out.println(
								String.format("CONTROLLER<%s>: expected %d cores for the avm but remain %d cores",
										this.acURI, CORES_BY_AVM, idleCoreNumber));
					avmURI = allocateAVM(idleCoreNumber);
				}
				rdop.connectAVM(avmURI, avmURI + LOCAL_REQUEST_SUB_INPORT_PORT_SUFFIX,
						avmURI + LOCAL_REQUEST_NOT_OUTPORT_PORT_SUFFIX);
				System.out.println(String.format("CONTROLLER<%s>: dispatcher<%s> connected with avm<%s>", this.acURI,
						dispatcherURI, avmURI));
			} else {
				System.out.println(String.format("CONTROLLER<%s>: no idle core, cannot create more avm", this.acURI));
				admission.setAVMNumber(i);
				return dispatcherRSIP;
			}
		}

		admission.setAVMNumber(i + 1);
		System.out.println(String.format("CONTROLLER<%s>: dispatcher<%s> allocated for application<%s> with %d avms",
				this.acURI, dispatcherURI, admission.getApplicationURI(), i + 1));
		return dispatcherRSIP;
	}

	/*
	 * ALL FUNCTIONS FOR HANDLING ADMISSION REQUEST
	 */
	public void notify(AdmissionI admission) throws Exception {
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

}
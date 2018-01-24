package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.admissioncontrollercoordination;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissioncontroller.connectors.AdmissionRequestConnector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionRequestOutboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationHandlerI;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationInboundPort;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * The class <code>ApplicationContainer</code> contains 1
 * <code>RequestGenerator</code> who generate requests, in this context the
 * <code>ApplicationContainer</code> asks the <code>AdmissionController</code>
 * for available resources in the <code>Computer</code> the
 * <code>ApplicationContainer</code> asks for hosting the application.
 * 
 * @author Hacene KASDI & Marc REN
 *
 */
public class ApplicationContainer 
				extends 	AbstractComponent 
				implements 	AdmissionNotificationHandlerI {

	/**
	 * THE URI OF THE APPLICATION
	 */
	protected String APP_URI;
	/**
	 * THE ADMISSION INBOUND PORT OF THE APPLICATION, USED TO RECEIVE NOTIFICATIONS
	 * ---O
	 */
	protected AdmissionNotificationInboundPort admissionNotificationInboundPort;
	/**
	 * THE ADMISSION REQUET TO ASK FOR RESOURCES
	 */
	protected AdmissionRequestOutboundPort admissionRequestOutboundPort;

	// public String AdmissionNotificationInboundPortURI;
	// public String AdmissionControllerOutboundPortURI ;
	protected AdmissionI admission;

	protected AbstractCVM cvm;

	/** Request generator component. */
	protected RequestGenerator rg;
	/**
	 * Port connected to the request generator component to manage its execution
	 * (starting and stopping the request generation).
	 */
	protected RequestGeneratorManagementOutboundPort rgmop;

	public ApplicationContainer(String uri, AbstractCVM cvm, AdmissionI admission,
			String admissionNotificationInboundPortURI, String admissionControllerOutboundPortURI) throws Exception {
		super(1, 1);

		assert uri != null;
		assert cvm != null;
		assert admission != null;
		assert admissionNotificationInboundPortURI != null;
		assert admissionControllerOutboundPortURI != null;

		this.APP_URI = uri;
		this.admission = admission;
		this.cvm = cvm;
		admission.setAdmissionNotificationInboundPortURI(APP_URI + admissionNotificationInboundPortURI);

		// ADD THE INBOUND PORT O-- Notification
		this.addOfferedInterface(AdmissionNotificationI.class);
		this.admissionNotificationInboundPort = new AdmissionNotificationInboundPort(
				APP_URI + admissionNotificationInboundPortURI, this);
		this.addPort(this.admissionNotificationInboundPort);
		this.admissionNotificationInboundPort.publishPort();

		// ADD THE OUTBOUND PORT --C Request
		this.addRequiredInterface(AdmissionRequestI.class);
		this.admissionRequestOutboundPort = new AdmissionRequestOutboundPort(
				APP_URI + admissionControllerOutboundPortURI, this);
		this.addPort(this.admissionRequestOutboundPort);
		this.admissionRequestOutboundPort.publishPort();

		this.rg = new RequestGenerator(APP_URI + "RG", // generator component URI
				200.0, 			// mean time between two requests
				3000000000L, 	// mean number of instructions in requests
				APP_URI+"RGMIP",
				APP_URI+"RSOP",
				APP_URI+"RNIP");
//		this.rg.DEBUG_LEVEL = 2;
		this.cvm.addDeployedComponent(this.rg);
	}

	public RequestGenerator getRequestGenerator() {
		return rg;
	}

	/**
	 * Invoke the method on the Connector ( asks for hosting )
	 * 
	 * @throws Exception
	 */
	public void askForHostingApllication() throws Exception {
		admission.setApplicationURI(APP_URI);
		this.admissionRequestOutboundPort.askForHost(this.admission);
		System.out.println("====================");
	}

	/**
	 * Make a connection with the InboundPort of the
	 * <code>AdmissionController</code>
	 * 
	 * @param admissionControllerInboundPortURI
	 * @throws Exception
	 */
	public void connectWithAdmissionController(String admissionControllerInboundPortURI) throws Exception {
		
		this.admissionRequestOutboundPort.doConnection(admissionControllerInboundPortURI,
				AdmissionRequestConnector.class.getCanonicalName());
	}

	@Override
	public void allowOrRefuseAdmissionNotification(AdmissionI admission) throws Exception {
		if (!admission.isAllowed()) {

			System.out.println("----------------**-------------");
			System.out.println("---------------*--*------------");
			System.out.println("-------------*--||---*---------");
			System.out.println("-----------*----||-----*-------");
			System.out.println("---------*------**-------*-----");
			System.out.println("-------********************----");
			System.out.println("----- HOSTING REFUSED FOR " + admission.getApplicationURI() + "-------");
		} else {
			System.out.println("-------------------------------");
			System.out.println("-------- HOSTING ACCEPTED------");
			System.out.println("-------------------------------");
			this.admission = admission;
			startApplication();
		}

	}

	/**
	 * Starts a Synchronous request
	 * 
	 * @throws Exception
	 */
	public void startAsync() throws Exception {
		final ApplicationContainer application = this;
		this.handleRequestAsync(new ComponentService<Void>() {
			@Override
			public Void call() throws Exception {
				application.askForHostingApllication();
				return null;
			}
		});
	}

	/**
	 * Starts the sending of requests to the RequestDispatcher on the other side
	 * 
	 * @throws Exception
	 */
	public void startApplication() throws Exception {
		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------

		/**
		 * COMPONENT CONNECTIONS -----------------------------------------
		 */

		getRequestGenerator().doPortConnection(APP_URI+"RSOP",
				this.admission.getRequestSubmissionInboundPortRD(),
				RequestSubmissionConnector.class.getCanonicalName());

		this.rgmop = new RequestGeneratorManagementOutboundPort(APP_URI+"RGMOP",
				new AbstractComponent(0, 0) {
				});
		this.rgmop.publishPort();
		this.rgmop.doConnection(APP_URI+"RGMIP",
				RequestGeneratorManagementConnector.class.getCanonicalName());

		System.out.println("STARTING APPLICATION ....\n");
		rgmop.startGeneration();
		Thread.sleep(200000L);
		rgmop.stopGeneration();
		rgmop.doDisconnection();
		rg.doPortDisconnection(APP_URI+"RSOP");
		System.out.println("APPLICATION " + admission.getApplicationURI() + " STOPPED !");
		admissionRequestOutboundPort.shutdownApplicationServices(admission.getApplicationURI());
	}
}

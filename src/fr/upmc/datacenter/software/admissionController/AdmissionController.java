	package fr.upmc.datacenter.software.admissionController;
	
	import java.util.ArrayList;
	
	import fr.upmc.components.AbstractComponent;
	import fr.upmc.datacenter.hardware.computers.Computer;
	import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
	import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
	import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;
	import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestHandlerI;
	import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;
	import fr.upmc.datacenter.software.admissionController.ports.AdmissionRequestInboundPort;
	import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
	import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationOutboundPort;
	import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
	import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
	import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
	import fr.upmc.datacenter.software.requestDispatcher.RequestDispatcher;
	
	/**
	 * <p><strong>Description</string></p>
	 * 
	 * This component <code>AdmissionController</code> receives the requests from the Costumers
	 * in this context we can consider Costumers as <code>RequestGenerator</code> who want to host their
	 * applications <code>RequestI</code>. the controller can accept if there is some available
	 * resources or refuse if all computers are busy on handling the requests of other Costumers. 
	 * 
	 * @author Hacene Kasdi & Marc REN
	 * @version 2012.10.20.HK
	 */
	
	public class AdmissionController 
					extends AbstractComponent 
					implements AdmissionRequestHandlerI {
	
		
		//-----------------------------------------------//
		//---------------------URIs----------------------//
		//-----------------------------------------------//
		
		/** URI oOF THE ADMISSION CONTROLLER 			*/
		protected String admConURI;
		//-----------------------------------------------//
		//---------------------PORTS---------------------//
		//-----------------------------------------------//
		
		/**
		 * All URIs and ports for first AVM
		 */
		// URIs
		public static final String	avmURI0 = "avm0";
		public static final String	ApplicationVMManagementInboundPortURI0 = "avm0-ibp" ;
		public static final String	ApplicationVMManagementOutboundPortURI0 = "avm0-obp" ;
		public static final String	RequestSubmissionInboundPortURI0 = "rsibp0";
		public static final String	RequestNotificationOutboundPortURI0 = "rnobp0"; 
		// Ports
		protected ApplicationVMManagementOutboundPort avmPort0 ;
	
		/**
		 * All URIs and ports for second AVM
		 */
		// URIs
		public static final String	avmURI1 = "avm1";
		public static final String	ApplicationVMManagementInboundPortURI1 = "avm1-ibp" ;
		public static final String	ApplicationVMManagementOutboundPortURI1 = "avm1-obp" ;
		public static final String	RequestSubmissionInboundPortURI1 = "rsibp1";
		public static final String	RequestNotificationOutboundPortURI1 = "rnobp1"; 
		// Ports
		protected ApplicationVMManagementOutboundPort avmPort1 ;
		
	
	
		protected Computer c0, c1;
		protected ComputerMonitor cm0, cm1;
		protected ApplicationVM avm0, avm1;
	
	
			// PREDIFINED URI OF PORTS 
			public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
			public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
			public static final String	RequestNotificationInboundPortURI = "rnibp" ;
			public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
			
	
			
			/** 	Request Dispatcher component.							*/
			protected RequestDispatcher							rd ;
			
			
		
		/** OUTBOUND PORT SENDING THE NOTIFICATIONS     */
		protected AdmissionNotificationOutboundPort anOutboundPort;
		
		/** INBOUND PORT OFFERING THE ADMISSION SERVICE */
		protected AdmissionRequestInboundPort arip;
		
		protected ArrayList<Computer> listComputers;
		
		protected AdmissionI admission;
		public AdmissionController(
				String admConURI,
				String admisionRequestInboundPortURI,
				String admissionNotificationOutbounPortURI,
				ArrayList<Computer> listComputers) throws Exception{
	
			super(1, 1);
			
			
			// PRECONDITION
			assert admConURI != null;
			assert admisionRequestInboundPortURI		 != null;
			assert admissionNotificationOutbounPortURI   != null;
			
			this.listComputers=new ArrayList<Computer>(listComputers);
			this.admConURI=admConURI;
			
			// ADD THE INBOUND PORT 		O---
			this.addOfferedInterface(AdmissionRequestI.class);
			this.arip = 
					new AdmissionRequestInboundPort(admisionRequestInboundPortURI, this);
			this.addPort(this.arip);
			this.arip.publishPort();
			
			// ADD THE OUTBOUND PORT
			this.addRequiredInterface(AdmissionNotificationI.class);
			this.anOutboundPort=
					new AdmissionNotificationOutboundPort(admissionNotificationOutbounPortURI,this);
			this.addPort(this.anOutboundPort);
			this.anOutboundPort.publishPort();
			
			System.out.println("PORT CREATED ON THE ADMISSION CONTROLLER");
	
			
		}
		public AdmissionI getAdmission() {
			return this.admission;
		}
		public ArrayList<Computer> getListComputers(){
			return listComputers;
		}
		
		
		@Override
		public void inspectResources(AdmissionI admission) throws Exception {
		
			this.admission=admission;
			if (getListComputers().get(0).allocateCores(1).length >0) {
				System.out.println("RESOURCES DIPONIBLE ! "+"");
	
				this.createRequestDispatcher();
				Thread.sleep(1000) ;
				
			}
		}
	
		public void createRequestDispatcher() throws Exception {
			
			// --------------------------------------------------------------------
			// Create and deploy first avm component
			// --------------------------------------------------------------------
			this.avm0 = new ApplicationVM(
					avmURI0,	// application vm component URI
					ApplicationVMManagementInboundPortURI0,
					RequestSubmissionInboundPortURI0,
					RequestNotificationOutboundPortURI0) ;
			getAdmission().getAbstractCVM().addDeployedComponent(this.avm0) ;
	
			// Create a mock up port to manage the AVM component (allocate cores).
			this.avmPort0 = new ApplicationVMManagementOutboundPort(
					ApplicationVMManagementOutboundPortURI0,
					new AbstractComponent(0, 0) {}) ;
			this.avmPort0.publishPort() ;
			this.avmPort0.doConnection(
					ApplicationVMManagementInboundPortURI0,
					ApplicationVMManagementConnector.class.getCanonicalName()) ;
	
			// Toggle on tracing and logging in the application virtual machine to
			// follow the execution of individual requests.
			this.avm0.toggleTracing() ;
			this.avm0.toggleLogging() ;
			
			// --------------------------------------------------------------------
			// Create and deploy second avm component
			// --------------------------------------------------------------------
			avm1 = new ApplicationVM(
					avmURI1,	// application vm component URI
					ApplicationVMManagementInboundPortURI1,
					RequestSubmissionInboundPortURI1,
					RequestNotificationOutboundPortURI1) ;
			getAdmission().getAbstractCVM().addDeployedComponent(avm1) ;
	
			// Create a mock up port to manage the AVM component (allocate cores).
			this.avmPort1 = new ApplicationVMManagementOutboundPort(
					ApplicationVMManagementOutboundPortURI1,
					new AbstractComponent(0, 0) {}) ;
			this.avmPort1.publishPort() ;
			this.avmPort1.doConnection(
					ApplicationVMManagementInboundPortURI1,
					ApplicationVMManagementConnector.class.getCanonicalName()) ;
	
			// Toggle on tracing and logging in the application virtual machine to
			// follow the execution of individual requests.
			this.avm1.toggleTracing() ;
			this.avm1.toggleLogging() ;
	
	
			
	
	// --------------------------------------------------------------------
	// Creating the request Dispatcher component.
	// --------------------------------------------------------------------
	this.rd = new RequestDispatcher("RDispatcher", 
			RequestSubmissionInboundPortURI, 
			RequestNotificationOutboundPortURI);
	getAdmission().getAbstractCVM().addDeployedComponent(rd);
	
	
	//Allocate the 4 cores of the computer to the application virtual
			// machine.
			AllocatedCore[] ac0 = this.listComputers.get(0).allocateCores(2) ;
			this.avmPort0.allocateCores(ac0);
			
			AllocatedCore[] ac1 = this.listComputers.get(0).allocateCores(2) ;
			this.avmPort1.allocateCores(ac1);
			
			
			
	this.rd.connectAVM(avmURI0, RequestSubmissionInboundPortURI0, RequestNotificationOutboundPortURI0);
	this.rd.connectAVM(avmURI1, RequestSubmissionInboundPortURI1, RequestNotificationOutboundPortURI1);
	
	
	System.out.println("REQUEST DISPATCHER AND APPLICATION VM ARE CREATED ...");
	
	this.admission.setAllowed(true);
	this.admission.setRequestSubmissionInboundPortRD(RequestSubmissionInboundPortURI);
	
	this.anOutboundPort.notifyAdmissionNotification("YES");
	
	
	
		}
		
		@Override
		public void inspectResourcesAndNotifiy(AdmissionI admission) throws Exception {
			
	
		}
	
	}

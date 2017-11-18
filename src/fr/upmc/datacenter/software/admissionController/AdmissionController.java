	package fr.upmc.datacenter.software.admissionController;
	
	import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissionController.ports.AdmissionRequestInboundPort;
import fr.upmc.datacenter.software.applicationcontainer.connectors.AdmissionNotificationConnector;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationOutboundPort;
import fr.upmc.datacenter.software.javassist.JavassistUtility;
	
	/**
	 * <p><strong>Description</string></p>
	 * 
	 * This component <code>AdmissionController</code> receives the requests from the Costumers
	 * in this context we can consider Costumers as <code>ApplicationContainer</code> who want to host their
	 * applications <code>RequestI</code> of the <code>RequestGenerator</code>. the controller can accept if there is some available
	 * resources or refuse if all computers are busy on handling the requests of other Costumers. 
	 * 
	 * This Class knows the state of all computers available, Many methods can check the state of Cores of Processors.
	 * 
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
		
		
			// PREDIFINED URI OF PORTS 
			public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
			public static final String	RequestNotificationInboundPortURI = "rnibp" ;
			public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
			
		/**
		 * Synchronized access to the critical resource	
		 */
			private final Object monitor = new Object();
			private final Lock verrou = new ReentrantLock();
			
			
		
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
			
			/** List of computers					*/
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
			
			System.out.println("\n PORT CREATED ON THE ADMISSION CONTROLLER \n");
	
			
		}
		
		/**
		 * 
		 * @return the instance on the Object Admission
		 */
		public AdmissionI getAdmission() {
			return this.admission;
		}
		
		/**
		 * 
		 * @return the list of available computers
		 */
		public ArrayList<Computer> getListComputers(){
			return listComputers;
		}
		
		/**
		 * Inspect the resources in the computers, if there is any available Core than we call and answer the other side
		 * witch is the ApplicationContainer
		 */
		@Override
		public void inspectResources(AdmissionI admission) throws Exception {
		// TEST IF THERE ARE SOME DISPONIBLE RESOURSES
			
			this.admission=admission;

			/**
			 * ASK FOR CORES in order to host our application and to receive requests
			 * PRECONDITION 
			 * NbCores > 0 => THERE ARE RESOURCES
			 */

			
			System.out.println("---------------- CORES ALLOCATED COMPUTER 0 (BEFORE)------------------");
			System.out.println("------------------------------ PROCESSOR 1 -----------------------");
			System.out.print("\t \tCORE 1 : ["+listComputers.get(0).getDynamicState().getCurrentCoreReservations()[0][0]+"]");
			System.out.println("  CORE 2 : ["+listComputers.get(0).getDynamicState().getCurrentCoreReservations()[0][1]+"]");
			System.out.println("------------------------------ PROCESSOR 2 -----------------------");
			System.out.print("\t \tCORE 1 : ["+listComputers.get(0).getDynamicState().getCurrentCoreReservations()[1][0]+"]");
			System.out.println("  CORE 2 : ["+listComputers.get(0).getDynamicState().getCurrentCoreReservations()[1][1]+"]");
			System.out.println("-------------------------------------------------------------");
			System.err.println("\n");
			
			
	if (getListComputers().get(0).allocateCores(1).length > 0) {
		

				// ALLOW THE HOSTING 
				this.admission.setAllowed(true);
		
				System.out.println("AVAILABLE RESOURCES FOR : "+admission.getApplicationURI()+" \n");
				
				//ASK FOR ALLOCATE CORES ON THE COMPUTERS
				
				Thread.sleep(1000L);
				System.out.println("-------------------------------------------------------------");
				System.out.println("---------------- CORES ALLOCATED COMPUTER 0 (AFTER)------------------");
				System.out.println("------------------------------ PROCESSOR 1 -----------------------");
				System.out.print("\t \tCORE 1 : ["+listComputers.get(0).getDynamicState().getCurrentCoreReservations()[0][0]+"]");
				System.out.println("  CORE 2 : ["+listComputers.get(0).getDynamicState().getCurrentCoreReservations()[0][1]+"]");
				System.out.println("-------------------------------------------------------------");
				System.out.println("------------------------------ PROCESSOR 2 -----------------------");
				System.out.print("\t \tCORE 1 : ["+listComputers.get(0).getDynamicState().getCurrentCoreReservations()[1][0]+"]");
				System.out.println("  CORE 2 : ["+listComputers.get(0).getDynamicState().getCurrentCoreReservations()[1][1]+"]");
				System.out.println("-------------------------------------------------------------");
				System.err.println("\n");

				/*
				 * Generate a Class Connector (AdmissionNotificationConnector) using the abstract method of the class 
				 * JavassistUtility using Javassist
				 */
//				HashMap<String, String> mapMethods = new HashMap<String, String>();
//				mapMethods.put("notifyAdmissionNotification", "allowOrRefuseAdmissionNotification");
//				Class<?> admissionConnector = JavassistUtility.makeConnectorClassJavassist(
//						"fr.upmc.datacenter.software.applicationcontainer.connectors.AdmissionNotifConnector", 
//						AbstractConnector.class, 
//						AdmissionNotificationI.class, 
//						AdmissionNotificationI.class, 
//						mapMethods);
				
					System.out.println("========================");
					JavassistUtility.createRequestDispatcher(admission, listComputers);
					System.out.println(admission.getRequestSubmissionInboundPortRD());
					this.connectWithApplicationContainer(admission.getAdmissionNotificationInboundPortURI());
					this.anOutboundPort.notifyAdmissionNotification(admission);
					System.out.println("========================");
					anOutboundPort.doDisconnection();
					
				
				
			}else {
				System.out.println("NO AVAILABLE RESOURCES FOR : "+admission.getApplicationURI()+" \n");
				System.out.println("========================");
				this.connectWithApplicationContainer(admission.getAdmissionNotificationInboundPortURI());
				admission.setAllowed(false);
				this.anOutboundPort.notifyAdmissionNotification(admission);
				System.out.println("========================");
				anOutboundPort.doDisconnection();
			}
		}
		public void connectWithApplicationContainer(String AdmissionNotificationInboundPortURI) throws Exception {
		anOutboundPort.doConnection(AdmissionNotificationInboundPortURI, AdmissionNotificationConnector.class.getCanonicalName());	
		}
		
		@Override
		public void inspectResourcesAndNotifiy(AdmissionI admission) throws Exception {
			
	
		}
		
		/**
		 * 
		 * @return number of Cores (idle and allocated)
		 * @throws Exception
		 */
		public int getNumberOfCore() throws Exception {
			return getNumberOfCore(0);
		}
		/**
		 * 
		 * @return number of Idle cores
		 * @throws Exception
		 */
		public int getNumberOfIdleCore() throws Exception {
			return getNumberOfCore(1);
		}
		/**
		 * 
		 * @param c <code>Computer</code>
		 * @return Number of Idle cores of a specific <code>Computer</code>
		 * @throws Exception
		 */
		public int getNumberOfIdleCore(Computer c) throws Exception{
			return getNumberOfCore(c, 1);
		}
		
		public int getNumberOfCore(int state) throws Exception {
			int res = 0;
			for(Computer computer : listComputers) res += getNumberOfCore(computer, state);
			return res;
		}
		
		/**
		 * 
		 * @param c : <code>Computer</code>  
		 * @param state : 0 all, 1 just idle ones, -1 just allocated one
		 * @return Number of Cores according to the state
		 * @throws Exception
		 */
		public int getNumberOfCore(Computer c, int state) throws Exception{
			int res = 0;
			ComputerStaticStateI css = c.getStaticState();
			ComputerDynamicStateI cds = c.getDynamicState();
			boolean[][] processorsCoresState= cds.getCurrentCoreReservations();
			int nbProcessors = css.getNumberOfProcessors();
			int nbCoresByProcessor = css.getNumberOfCoresPerProcessor();
			
			for(int i = 0; i < nbProcessors; i++) {
				for(int j = 0; j < nbCoresByProcessor; j++) {
					if(state == 0) res++;
					if(state == 1 && !processorsCoresState[i][j]) res++;
					if(state == -1 && processorsCoresState[i][j]) res++;
				}
			}
			return res;
		}
		
	}

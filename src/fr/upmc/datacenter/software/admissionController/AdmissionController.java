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
import fr.upmc.datacenter.software.javassist.JavassistRD;
import fr.upmc.datacenter.software.javassist.JavassistUtility;
import fr.upmc.datacenter.software.requestDispatcher.RequestDispatcher;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
	
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
//			public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
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
			
			System.out.println("\n PORT CREATED ON THE ADMISSION CONTROLLER \n");
	
			
		}
		public AdmissionI getAdmission() {
			return this.admission;
		}
		public ArrayList<Computer> getListComputers(){
			return listComputers;
		}
		
		
		@Override
		public void inspectResources(AdmissionI admission) throws Exception {
		// TEST IF THERE ARE SOME DISPONIBLE RESOURSES
			
			this.admission=admission;
//			System.out.println(getListComputers().get(0).allocateCores(7).length +"========");

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
			
			
	if (getListComputers().get(0).allocateCores(3).length > 0) {
		

				// ALLOW THE HOSTING 
				this.admission.setAllowed(true);
		
				System.out.println("\n RESOURCES DIPONIBLE ! \n");
				
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

				/**
				 * CREATE REQUEST DISPATCHER AND APPLICATIONVM WITH JAVASSIST
				 */
//				ClassPool pool = ClassPool.getDefault() ;
//		        CtClass cii = pool.get("fr.upmc.datacenter.software.javassist.JavassistUtility") ;
//		        CtMethod[] methodsToImplement = cii.getDeclaredMethods() ;
//		        System.out.println("Nombre de methodes: "+methodsToImplement.length);
//		        CtMethod mCtMethod=null;
//		        for (int i = 0 ; i < methodsToImplement.length ; i++) {
//		         if (methodsToImplement[i].getName().equals("createRD")) {
//					System.out.println("found");
//					mCtMethod = methodsToImplement[i];
//		         }
//		        }
//		        JavassistRD.addInstrumentation(mCtMethod);
		        
//		        Class class1 = cii.toClass();
//				JavassistUtility.createRD(admission, listComputers);
				
				
		        JavassistUtility.createRequestDispatcher(admission, listComputers);
				Thread.sleep(1000) ;
				// NOTIFY THE APPLICATION CONTAINER
				this.anOutboundPort.notifyAdmissionNotification(this.admission);
				
			}
		}
		
		@Override
		public void inspectResourcesAndNotifiy(AdmissionI admission) throws Exception {
			
	
		}
	
	}

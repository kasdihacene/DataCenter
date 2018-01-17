	package fr.upmc.datacenter.software.admissioncontroller;
	
	import java.util.ArrayList;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionRequestInboundPort;
import fr.upmc.datacenter.software.applicationcontainer.connectors.AdmissionNotificationConnector;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationOutboundPort;
import javassist.ClassPool;
import javassist.CtClass;
	
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
	 * @version 2017.10.20.HK
	 */
	
	public class PreviousAdmissionController 
					extends AbstractComponent 
					implements AdmissionRequestHandlerI {
	
		
		//-----------------------------------------------//
		//---------------------URIs----------------------//
		//-----------------------------------------------//
		
		/** URI oOF THE ADMISSION CONTROLLER 			*/
		protected String uri;
		
		protected AbstractCVM cvm;
		
		
			// PREDIFINED URI OF PORTS 
			public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
			public static final String	RequestNotificationInboundPortURI = "rnibp" ;
			public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
			
			
			
		
		/** OUTBOUND PORT SENDING THE NOTIFICATIONS     */
		protected AdmissionNotificationOutboundPort anOutboundPort;
		
		/** INBOUND PORT OFFERING THE ADMISSION SERVICE */
		protected AdmissionRequestInboundPort arip;
		
		protected ArrayList<Computer> listComputers;
		
		protected AdmissionI admission;
		public PreviousAdmissionController(
				String uri,
				AbstractCVM cvm,
				String requestInboundPortURI,
				String notificationOutbounPortURI,
				ArrayList<Computer> listComputers) throws Exception{
	
			super(1, 1);
			
			
			// PRECONDITION
			assert uri != null;
			assert cvm != null;
			assert requestInboundPortURI != null;
			assert notificationOutbounPortURI != null;
			assert listComputers.size() > 0;
			
			this.listComputers=new ArrayList<Computer>(listComputers);
			this.uri=uri;
			this.cvm = cvm;
			
			// ADD THE INBOUND PORT 		O---
			this.addOfferedInterface(AdmissionRequestI.class);
			this.arip = 
					new AdmissionRequestInboundPort(requestInboundPortURI, this);
			this.addPort(this.arip);
			this.arip.publishPort();
			
			// ADD THE OUTBOUND PORT
			this.addRequiredInterface(AdmissionNotificationI.class);
			this.anOutboundPort=
					new AdmissionNotificationOutboundPort(notificationOutbounPortURI,this);
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
			
			System.out.println(getIndexAvailableComputer());
	if (getIndexAvailableComputer()>-1) {
		

				// ALLOW THE HOSTING 
				this.admission.setAllowed(true);
		
				System.out.println("AVAILABLE RESOURCES FOR : "+admission.getApplicationURI()+" \n");
				
				//ASK FOR ALLOCATE CORES ON THE COMPUTERS
				
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

					// Creation of the resquest Dispatcher with Javassist
					int indexAvailableComputer = getIndexAvailableComputer();
					this.connectWithApplicationContainer(admission.getAdmissionNotificationInboundPortURI());
					System.out.println("get classpool");
					ClassPool pool = ClassPool.getDefault();
					System.out.println("get javaassistutility class");
					CtClass cc = pool.get("fr.upmc.datacenter.software.javassist.JavassistUtility");
					System.out.println("?");
					cc.setName("fr.upmc.datacenter.software.javassist.JavassistCopy");
					System.out.println("name setted");
					cc.writeFile();
					Class<?> clazz = pool.toClass(cc);
					System.out.println("to class");
					java.lang.reflect.Method [] listM = clazz.getMethods();
					listM[0].invoke(
							clazz, new Object[]
							{cvm
							,admission
							,listComputers
							,indexAvailableComputer});
					
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
		 * @param state : 0 all, 1 just idle ones, -1 just allocated ones
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
		/**
		 * 
		 * @return the index of the available Computer
		 * @throws Exception
		 */
		public int getIndexAvailableComputer() throws Exception {
			int index=-1;
			for (int i = 0; i < this.listComputers.size(); i++) {
				if (getNumberOfCore(listComputers.get(i), 1) == 4) {
					index= i;
					break;
				}
			}
			return index;
		}

		@Override
		public void shutdownServices(String URI) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
	}
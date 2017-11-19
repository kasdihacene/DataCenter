package fr.upmc.datacenter.software.applicationcontainer;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.admissionController.Admission;
import fr.upmc.datacenter.software.admissionController.connectors.AdmissionRequestConnector;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissionController.ports.AdmissionRequestOutboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationHandlerI;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationInboundPort;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class ApplicationContainer 
			extends AbstractComponent
			implements AdmissionNotificationHandlerI{
	
	/**
	 * THE URI OF THE APPLICATION
	 */
	protected String APP_URI;
	/**
	 * THE ADMISSION INBOUND PORT OF THE APPLICATION, USED TO RECEIVE NOTIFICATIONS      ---O
	 */
	protected AdmissionNotificationInboundPort admissionNotificationInboundPort;
	/**
	 * THE ADMISSION REQUET TO ASK FOR RESOURCES
	 */
	protected AdmissionRequestOutboundPort admissionRequestOutboundPort;
	
//	public String	AdmissionNotificationInboundPortURI;
//	public String	AdmissionControllerOutboundPortURI ;
	protected Admission admission;
	

	/** 	Request generator component.										*/
	protected RequestGenerator							rg;
	/** Port connected to the request generator component to manage its
	 *  execution (starting and stopping the request generation).			*/
	protected RequestGeneratorManagementOutboundPort	rgmop ;

	// PREDIFINED URI OF PORTS 
	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String	RequestSubmissionOutboundPortURI = "rsobp_" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;
	
	public ApplicationContainer(
			String uri, 
			Admission admission,
			String	AdmissionNotificationInboundPortURI,
			String	AdmissionControllerOutboundPortURI) throws Exception{
		super(1,1);
		
		assert uri != null;
		
		this.APP_URI = uri;
		this.admission=admission;
		admission.setAdmissionNotificationInboundPortURI(APP_URI+AdmissionNotificationInboundPortURI);
		
		//ADD THE INBOUND PORT		O--		Notification
		this.addOfferedInterface(AdmissionNotificationI.class);
		this.admissionNotificationInboundPort =
				new AdmissionNotificationInboundPort(APP_URI+AdmissionNotificationInboundPortURI,this);
		this.addPort(this.admissionNotificationInboundPort);
		this.admissionNotificationInboundPort.publishPort();
		
		//ADD THE OUTBOUND PORT 	--C		Request
		this.addRequiredInterface(AdmissionRequestI.class);
		this.admissionRequestOutboundPort = 
				new AdmissionRequestOutboundPort(APP_URI+AdmissionControllerOutboundPortURI,this);
		this.addPort(this.admissionRequestOutboundPort);
		this.admissionRequestOutboundPort.publishPort();
		
		 rg = new RequestGenerator(
				 APP_URI+"rg",			// generator component URI
				500.0,			// mean time between two requests
				6000000000L,	// mean number of instructions in requests
				APP_URI+RequestGeneratorManagementInboundPortURI,
				APP_URI+RequestSubmissionOutboundPortURI,
				APP_URI+RequestNotificationInboundPortURI) ;
	admission.getAbstractCVM().addDeployedComponent(rg) ;
//	admission.setRequestSubmissionInboundPortRD(RequestSubmissionInboundPortURI);
		
		System.out.println("\n PORT CREATED ON THE APPLICATION CONTAINER "+APP_URI+"\n");
		
	}
	
	public RequestGenerator getRequestGenerator() {
		return rg;
	}

	public void askForHostingApllication() throws Exception{
		System.out.println("ASK FOR HOSTIN OF THE APPLICATION ...");

		admission.setApplicationURI(APP_URI);
		this.admissionRequestOutboundPort.askForHost(this.admission);
	}
	
	public void connectWithAdmissionController(String admissionControllerInboundPortURI) throws Exception {
		this.admissionRequestOutboundPort.doConnection(
				admissionControllerInboundPortURI,
				AdmissionRequestConnector.class.getCanonicalName());
	}

	@Override
	public void allowOrRefuseAdmissionNotification(AdmissionI admission) throws Exception {
		if(!admission.isAllowed()) {

			System.out.println("----------------**-------------");
			System.out.println("---------------*--*------------");
			System.out.println("-------------*--||---*---------");
			System.out.println("-----------*----||-----*-------");
			System.out.println("---------*------**-------*-----");
			System.out.println("-------********************----");
			System.out.println("----- HOSTING REFUSED FOR "+admission.getApplicationURI()+"-------");
		}else {
			
			System.out.println("-------------------------------");
			System.out.println("-------- HOSTING ACCEPTED------");
			System.out.println("-------------------------------");
			Thread.sleep(1000) ;
			startApplication();
		}
		
	}

	public void startAsync() throws Exception
	{
		final ApplicationContainer application = this;
		this.handleRequestAsync(new ComponentService<Void>() {
			@Override
			public Void call() throws Exception {
				application.askForHostingApllication();
				return null;
			}
		});
	}
	
	public void startApplication() throws Exception {
		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		
				
					/**
					 * COMPONENT CONNECTIONS -----------------------------------------
					 */
		
						getRequestGenerator().doPortConnection(
								APP_URI+RequestSubmissionOutboundPortURI,
//								RequestSubmissionInboundPortURI
								this.admission.getRequestSubmissionInboundPortRD(),
								RequestSubmissionConnector.class.getCanonicalName()) ;


						this.rgmop = new RequestGeneratorManagementOutboundPort(
								APP_URI+RequestGeneratorManagementOutboundPortURI,
								new AbstractComponent(0, 0) {}) ;
						this.rgmop.publishPort() ;
						this.rgmop.doConnection(
								APP_URI+RequestGeneratorManagementInboundPortURI,
					RequestGeneratorManagementConnector.class.getCanonicalName()) ;
						
					System.out.println("\n REQUEST GENERATOR CREATED ! \n");

					System.out.println("\n STARTING APPLICATION ....\n");
					rg.startGeneration();
					Thread.sleep(10000L);
					rg.stopGeneration();
					rgmop.doDisconnection();
					rg.doPortDisconnection(APP_URI+RequestSubmissionOutboundPortURI);
					System.out.println("APPLICATION "+admission.getApplicationURI()+" STOPPED !");
	}
}

package fr.upmc.datacenter.software.applicationcontainer;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.admissionController.Admission;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissionController.ports.AdmissionRequestOutboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationHandlerI;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationInboundPort;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
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
	public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
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
		
		//ADD THE INBOUND PORT		O--		Notification
		this.addOfferedInterface(AdmissionNotificationI.class);
		this.admissionNotificationInboundPort =
				new AdmissionNotificationInboundPort(AdmissionNotificationInboundPortURI,this);
		this.addPort(this.admissionNotificationInboundPort);
		this.admissionNotificationInboundPort.publishPort();
		
		//ADD THE OUTBOUND PORT 	--C		Request
		this.addRequiredInterface(AdmissionRequestI.class);
		this.admissionRequestOutboundPort = 
				new AdmissionRequestOutboundPort(AdmissionControllerOutboundPortURI,this);
		this.addPort(this.admissionRequestOutboundPort);
		this.admissionRequestOutboundPort.publishPort();
		
		 rg = new RequestGenerator(
				"rg",			// generator component URI
				500.0,			// mean time between two requests
				6000000000L,	// mean number of instructions in requests
				RequestGeneratorManagementInboundPortURI,
				RequestSubmissionOutboundPortURI,
				RequestNotificationInboundPortURI) ;
	admission.getAbstractCVM().addDeployedComponent(rg) ;
		
		System.out.println("PORT CREATED ON THE APPLICATION CONTAINER");
		
	}
	
	public RequestGenerator getRequestGenerator() {
		return rg;
	}

	public void askForHostingApllication() throws Exception{
		System.out.println("ASK FOR HOSTIN GTHE APPLICATION ...");
		this.admissionRequestOutboundPort.askForHost(this.admission);
	}

	@Override
	public void allowOrRefuseAdmissionNotification(String uri) throws Exception {
		if(uri.equals("NO")) {
			System.out.println("-------------------------------");
			System.out.println("-------- HOSTING REFUSED-------");
			System.out.println("-------------------------------");
		}else {
			
			System.out.println("-------------------------------");
			System.out.println("-------- HOSTING ACCEPTED------");
			System.out.println("-------------------------------");
			Thread.sleep(1000) ;
			startApplication();
		}
		
	}

	
	public void startApplication() throws Exception {
		System.out.println("\n STARTING APPLICATION ....");
		// --------------------------------------------------------------------
				// Creating the request generator component.
				// --------------------------------------------------------------------
		
				
					/**
					 * COMPONENT CONNECTIONS -----------------------------------------
					 */
		
						
		;
						getRequestGenerator().doPortConnection(
								RequestSubmissionOutboundPortURI,
								RequestSubmissionInboundPortURI,
								RequestSubmissionConnector.class.getCanonicalName()) ;


						this.rgmop = new RequestGeneratorManagementOutboundPort(
								RequestGeneratorManagementOutboundPortURI,
								new AbstractComponent(0, 0) {}) ;
						this.rgmop.publishPort() ;
						this.rgmop.doConnection(
					RequestGeneratorManagementInboundPortURI,
					RequestGeneratorManagementConnector.class.getCanonicalName()) ;
						
					System.out.println("REQUEST GENERATOR CREATED !");
					
					rgmop.startGeneration();
	}
}

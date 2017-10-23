package fr.upmc.datacenter.software.applicationcontainer;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissionController.ports.AdmissionRequestOutboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationHandlerI;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationInboundPort;

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
	
	
	public ApplicationContainer(
			String uri, 
			AbstractCVM acvm,
			String	AdmissionNotificationInboundPortURI,
			String	AdmissionControllerOutboundPortURI) throws Exception{
		super(1,1);
		
		assert uri != null;
		
		this.APP_URI = uri;
		
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
		
		System.out.println("PORT CREATED ON THE APPLICATION CONTAINER");
		
	}

	public void askForHostingApllication(String uri) throws Exception{
		System.out.println("ASK FOR HOSTIN GTHE APPLICATION ...");
		this.admissionRequestOutboundPort.askForHost(uri);
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
		}
	}

}

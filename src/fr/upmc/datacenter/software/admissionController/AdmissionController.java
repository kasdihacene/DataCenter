package fr.upmc.datacenter.software.admissionController;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;
import fr.upmc.datacenter.software.admissionController.ports.AdmissionRequestInboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.applicationcontainer.ports.AdmissionNotificationOutboundPort;

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
	
	/** OUTBOUND PORT SENDING THE NOTIFICATIONS     */
	protected AdmissionNotificationOutboundPort anOutboundPort;
	
	/** INBOUND PORT OFFERING THE ADMISSION SERVICE */
	protected AdmissionRequestInboundPort arip;
	
	public AdmissionController(
			String admConURI,
			String admisionRequestInboundPortURI,
			String admissionNotificationOutbounPortURI) throws Exception{

		super(1, 1);
		
		
		// PRECONDITION
		assert admConURI != null;
		assert admisionRequestInboundPortURI		 != null;
		assert admissionNotificationOutbounPortURI   != null;
		
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
	@Override
	public void inspectResources(String uri) throws Exception {
		
		if (uri.equals("HHHHH")) {
			System.out.println("IT WORKS ! "+uri);
			this.anOutboundPort.notifyAdmissionNotification("YES");
		}

	}

	@Override
	public void inspectResourcesAndNotifiy(String uri) throws Exception {
		

	}

}

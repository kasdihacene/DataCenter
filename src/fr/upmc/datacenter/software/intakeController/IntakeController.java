package fr.upmc.datacenter.software.intakeController;

import java.util.ArrayList;

import com.sun.glass.ui.Application;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

/**
 * THIS CLASS WILL RECEIVE THE REQUESTS OF MANY COSTUMERS TO HOST THEIR APPLICATIONS
 * HERE THE CONSTUMERS ARE (REQUEST GENERATORS), IntakeControler knows about all Computers settings and performances cores.
 * 
 * @author Mr Hacene KASDI <contact@hacene-kasdi.fr>
 *
 */
public class IntakeController extends AbstractComponent 
			implements RequestSubmissionHandlerI,RequestNotificationHandlerI {
	/**
	 * THE URI OF THE INTAKE CONTROLLER
	 */
	protected String intakeControlerURI;
	
	// PORT TO RECEIVE REQUEST TO EXECUTE APPLICATIONS
	protected RequestSubmissionInboundPort requestSubmissionInboundPort;
	
	// A PORT TO NOTIFY THE REQUEST GENERATOR AT THE END OF THE EXECUTION OF THE APPLICATION
	protected RequestNotificationOutboundPort	requestNotificationOutboundPort ;
	
	protected ArrayList<RequestGenerator> listGenerators;
	
	public IntakeController(
			String intakeContrURI,
			String rsip,
			String rnop
//			ArrayList<RequestGenerator> listGenerators
			)throws Exception {
		super(2, 2);
		/**
		 * PRECONDITIONS 
		 */
		assert intakeContrURI != null;
		assert rsip != null;
		assert rnop != null;
//		this.listGenerators=listGenerators;
		
		// CREATE PORT TO THE COMPONENT
		/**
		 * A PORT TO RECEIVE REQUESTS FROM THE RequestGenerator   O--
		 */
		this.addOfferedInterface(RequestSubmissionI.class);
		this.requestSubmissionInboundPort =
				new RequestSubmissionInboundPort(rsip, this);
		this.addPort(this.requestSubmissionInboundPort);
		this.requestSubmissionInboundPort.publishPort();
		
		/**
		 * A PORT TO NETIFY THE RequestGenerator 				  --C
		 */
		this.addRequiredInterface(RequestNotificationI.class);
		this.requestNotificationOutboundPort =
				new RequestNotificationOutboundPort(rnop, this);
		this.addPort(this.requestNotificationOutboundPort);
		this.requestNotificationOutboundPort.publishPort();
		
		
		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		System.out.println("REQUEST RECEIVED "+requestNotificationOutboundPort.getServerPortURI()+" "+requestSubmissionInboundPort.getServerPortURI());
		System.out.println("Result = ");
		
	}
	

}

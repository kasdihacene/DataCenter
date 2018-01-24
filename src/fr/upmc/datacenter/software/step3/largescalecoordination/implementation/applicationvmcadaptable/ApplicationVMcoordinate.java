package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable;

import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.interfaces.ConnectCoordinateAVMI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmcadaptable.ports.ConnectCoordinateAVMInboundPort;

public class ApplicationVMcoordinate extends ApplicationVMAdaptable implements ConnectCoordinateAVMI{

	private ConnectCoordinateAVMInboundPort connectCoordinateAVMInboundPort;
	public ApplicationVMcoordinate(
			String vmURI, 
			String applicationVMManagementInboundPortURI,
			String requestSubmissionInboundPortURI, 
			String requestNotificationOutboundPortURI) throws Exception {
		
		super(	vmURI, 
				applicationVMManagementInboundPortURI, 
				requestSubmissionInboundPortURI,
				requestNotificationOutboundPortURI);
		
		/** offer the interface which allows connection between ApplicationVM 
		 * and RequestDispatcher to send notifications from the AVM to the RD */
		
		this.addOfferedInterface(ConnectCoordinateAVMI.class);
		this.connectCoordinateAVMInboundPort = new ConnectCoordinateAVMInboundPort(vmURI+"_CCIP",this);
		this.addPort(connectCoordinateAVMInboundPort);
		this.connectCoordinateAVMInboundPort.publishPort();
		
		}

	/**
	 * @see {@link ConnectCoordinateAVMI#connectAVMwithSubmissioner(String)}
	 */
	@Override
	public void connectAVMwithSubmissioner(String uriDispatcher) throws Exception {
		requestNotificationOutboundPort.doConnection(	
														uriDispatcher, 
														RequestNotificationConnector.class.getCanonicalName());
	}
	
	

}

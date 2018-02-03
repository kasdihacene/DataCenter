package fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable;

import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.Coordinator;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.interfaces.ConnectCoordinateAVMI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.ports.ConnectCoordinateAVMInboundPort;

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
														uriDispatcher+"_RNIP", 
														RequestNotificationConnector.class.getCanonicalName());
	}
	/**
	 * Disconnect the AVM from RequestDispatcher and stop adaptation
	 * this method used on cooperation @see {@link Coordinator#removeAVM()}
	 * 
	 * @param uriDispatcher
	 * @throws Exception
	 */
	@Override
	public void disconnectAVMFromSubmissioner() throws Exception {
		requestNotificationOutboundPort.doDisconnection();
	}
	
	

}

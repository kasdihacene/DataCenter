package fr.upmc.datacenter.software.step2.requestresourcevm.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestVMI;
/**
 * 
 * @author Hacene KASDI
 * @version 26.12.17
 * 
 * VM Request OutboundPort
 * Using <code>RequestResourceVMOutboundPort</code> port we can receive invocation
 * of adding or deleting ApplicationVM 
 */
public class RequestResourceVMOutboundPort 
										extends AbstractOutboundPort 
										implements RequestResourceVMI {
	/**
	 * 
	 * @param owner
	 * @throws Exception
	 */
	public RequestResourceVMOutboundPort(ComponentI owner) throws Exception {
		super(AdmissionNotificationI.class, owner);
	}
	/**
	 * 
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public RequestResourceVMOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, AdmissionNotificationI.class, owner);
	}

	@Override
	public void requestAddVM(RequestVMI requestVMI) throws Exception {
		((RequestResourceVMI)this.connector).requestAddVM(requestVMI);
	}

	@Override
	public void requestRemoveVM(RequestVMI requestVMI) throws Exception {
		((RequestResourceVMI)this.connector).requestRemoveVM(requestVMI);
	}
	@Override
	public void requestRemoveAVMEnded(RequestVMI requestVMI) throws Exception {
		((RequestResourceVMI)this.connector).requestRemoveAVMEnded(requestVMI);
		
	}

}

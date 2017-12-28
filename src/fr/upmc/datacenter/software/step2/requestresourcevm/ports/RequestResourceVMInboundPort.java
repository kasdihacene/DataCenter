package fr.upmc.datacenter.software.step2.requestresourcevm.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMHandlerI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestVMI;
/**
 * 
 * @author Hacene KASDI
 * @version 26.12.17
 * 
 * VM Request InboundPort
 * Using <code>RequestResourceVMInboundPort</code> port we can 
 * invoke adding or deleting ApplicationVM
 */
public class RequestResourceVMInboundPort 
										extends AbstractInboundPort 
										implements RequestResourceVMI {

	private static final long serialVersionUID = 2612171L;
	/**
	 * 
	 * @param owner
	 * @throws Exception
	 */
	public RequestResourceVMInboundPort(ComponentI owner) throws Exception {
		super(AdmissionNotificationI.class,owner);
	}
	/**
	 * 
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public RequestResourceVMInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,AdmissionNotificationI.class,owner);
	}
	
	@Override
	public void requestAddVM(RequestVMI requestVMI) throws Exception {
		final RequestResourceVMHandlerI handler = (RequestResourceVMHandlerI) this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {
			@Override
			public Void call() throws Exception {
				handler.addVMApplication(requestVMI);
				return null;
			}
		});
	}

	@Override
	public void requestRemoveVM(RequestVMI requestVMI) throws Exception {
		final RequestResourceVMHandlerI handler = (RequestResourceVMHandlerI) this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {
			@Override
			public Void call() throws Exception {
				handler.removeVMAppication(requestVMI);
				return null;
			}
		});
	}

}

package fr.upmc.datacenter.software.step2.requestresourcevm.connector;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestResourceVMI;
import fr.upmc.datacenter.software.step2.requestresourcevm.interfaces.RequestVMI;
/**
 * The Connetor <code>RequestResourceVMConnector</code> used to 
 * connect the sub-component <code>ResourceInspector</code> and the 
 * <code>RequestDispatcher</code>
 * 
 * @author Hacene KASDI
 * @version 26.12.17
 *
 */
public class RequestResourceVMConnector 
										extends AbstractConnector 
										implements RequestResourceVMI {

	@Override
	public void requestAddVM(RequestVMI requestVMI) throws Exception {
		((RequestResourceVMI)this.offering).requestAddVM(requestVMI);
	}

	@Override
	public void requestRemoveVM(RequestVMI requestVMI) throws Exception {
		((RequestResourceVMI)this.offering).requestRemoveVM(requestVMI);
	}

}

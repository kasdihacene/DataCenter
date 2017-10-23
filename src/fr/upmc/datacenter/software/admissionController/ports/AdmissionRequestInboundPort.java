package fr.upmc.datacenter.software.admissionController.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;

/**
 * 
 * @author Hacene Kasdi & Marc REN
 * @version 2012.10.20.HK
 */
public class AdmissionRequestInboundPort 
			extends AbstractInboundPort
			implements AdmissionRequestI {


	private static final long serialVersionUID = 1L;
	
		//----------------------------------------------------//
		//------------------CONSTRUCTORS----------------------//
		//----------------------------------------------------//
	
	public AdmissionRequestInboundPort(
			String uri,
			ComponentI owner) throws Exception {
		super(uri,AdmissionRequestI.class, owner);
		assert	uri != null && owner instanceof AdmissionRequestHandlerI ;
	}
	
	public AdmissionRequestInboundPort(ComponentI owner)throws Exception {
		super(AdmissionRequestI.class, owner);
		assert owner instanceof AdmissionRequestHandlerI ;
	}

		//----------------------------------------------------//
		//-----------------------METHODS----------------------//
		//----------------------------------------------------//

	@Override
	public void askForHost(String uri) throws Exception {
		final AdmissionRequestHandlerI aHandlerI =
				(AdmissionRequestHandlerI)this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				aHandlerI.inspectResources(uri);
				return null;
			}
		});
	}

	@Override
	public void askForHostAndWaitResponse(String uri) throws Exception {
		final AdmissionRequestHandlerI aHandlerI =
				(AdmissionRequestHandlerI)this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				aHandlerI.inspectResourcesAndNotifiy(uri);
				return null;
			}
		});
	}

}

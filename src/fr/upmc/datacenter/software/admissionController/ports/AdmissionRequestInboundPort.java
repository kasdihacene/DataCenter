package fr.upmc.datacenter.software.admissionController.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestHandlerI;
import fr.upmc.datacenter.software.admissionController.interfaces.AdmissionRequestI;

/**
 * The class <code>AdmissionRequestInboundPort</code> implements the
 * inbound port through which the component receives requests, asking for resources
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
	
	/**
	 * Constructor of the inbound port
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public AdmissionRequestInboundPort(
			String uri,
			ComponentI owner) throws Exception {
		super(uri,AdmissionRequestI.class, owner);
		assert	uri != null && owner instanceof AdmissionRequestHandlerI ;
	}
	/**
	 * Constructor
	 * @param owner
	 * @throws Exception
	 */
	public AdmissionRequestInboundPort(ComponentI owner)throws Exception {
		super(AdmissionRequestI.class, owner);
		assert owner instanceof AdmissionRequestHandlerI ;
	}

		//----------------------------------------------------//
		//-----------------------METHODS----------------------//
		//----------------------------------------------------//

	/**
	 * Method invoked when asking for hosting an application
	 */
	@Override
	public void askForHost(AdmissionI admission) throws Exception {
		final AdmissionRequestHandlerI aHandlerI =
				(AdmissionRequestHandlerI)this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				aHandlerI.inspectResources(admission);
				return null;
			}
		});
	}

	/**
	 * Method invoked when asking and waiting a response 
	 */
	@Override
	public void askForHostAndWaitResponse(AdmissionI admission) throws Exception {
		final AdmissionRequestHandlerI aHandlerI =
				(AdmissionRequestHandlerI)this.owner;
		this.owner.handleRequestAsync(new ComponentI.ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				aHandlerI.inspectResourcesAndNotifiy(admission);
				return null;
			}
		});
	}

}

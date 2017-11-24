package fr.upmc.datacenter.software.applicationcontainer.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationHandlerI;
import fr.upmc.datacenter.software.applicationcontainer.interfaces.AdmissionNotificationI;

public class AdmissionNotificationInboundPort 
		extends AbstractInboundPort 
		implements AdmissionNotificationI {

	public AdmissionNotificationInboundPort
	(ComponentI owner) throws Exception
		{
			super(AdmissionNotificationI.class, owner) ;

			assert	owner instanceof AdmissionNotificationHandlerI ;
			assert	uri != null ;
		}
	
	public AdmissionNotificationInboundPort
			(String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, AdmissionNotificationI.class, owner) ;

			assert	uri != null && owner instanceof AdmissionNotificationHandlerI ;
		}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notifyAdmissionNotification(AdmissionI admission) throws Exception {
		final AdmissionNotificationHandlerI admissionNotificationHandlerI = 
				(AdmissionNotificationHandlerI)this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {

					@Override
					public Void call() throws Exception {
						admissionNotificationHandlerI.allowOrRefuseAdmissionNotification(admission);
						return null;
					}
					
				});
	}

}

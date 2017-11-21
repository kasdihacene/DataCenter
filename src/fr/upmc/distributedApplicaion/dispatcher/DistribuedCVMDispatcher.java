package fr.upmc.distributedApplicaion.dispatcher;

import fr.upmc.components.cvm.AbstractDistributedCVM;

public class DistribuedCVMDispatcher extends AbstractDistributedCVM{
	
	private final static String DISPATCHER_URI = "dispatcher";
	private final static String INTAKE_CONTROLLER_URI = "intake_controller";
	
	public DistribuedCVMDispatcher(String[] args) throws Exception {
		super(args);
	}

}

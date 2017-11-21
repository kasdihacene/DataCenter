package fr.upmc.distributedApplicaion.avm;

import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

public class DistribuedRG extends AbstractDistributedCVM {

	protected RequestGenerator rg;

	public DistribuedRG(String[] args) throws Exception {
		super(args);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		super.instantiateAndPublish();
		this.rg = new RequestGenerator("rg", 500.0, 6000000000L, "rg-management",
				StaticData.RG_REQUEST_SUBMISSION_OUTBOUND_PORT, StaticData.RG_REQUEST_NOTIFICATION_INBOUND_PORT);
		this.addDeployedComponent(rg);
	}

	@Override
	public void interconnect() throws Exception {
		super.interconnect();
		this.rg.doPortConnection(StaticData.RG_REQUEST_SUBMISSION_OUTBOUND_PORT,
				StaticData.AVM_REQUEST_SUBMISSION_INBOUND_PORT, RequestSubmissionConnector.class.getCanonicalName());
	}
	
	@Override
	public void start() throws Exception{
		super.start();
		this.rg.startGeneration();
	}
	
	public static void main(String[] args) {
		System.out.println("Beginning distribued Admission Controller");
		try {
			DistribuedRG drg = new DistribuedRG(args);
			drg.deploy();
			System.out.println("All component deployed");
			System.out.println("Start\n");
			drg.start();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}

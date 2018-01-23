package fr.upmc.datacenter.software.step3.largescalecoordination.implementation;

import fr.upmc.datacenter.software.step2.adapter.AdapterRequestDispatcher;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.CoordinationLargeScaleI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleInboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleOutboundPort;

/**
 * <h2>Coordinator</h2> represents an Adapter {@link fr.upmc.datacenter.software.step2.adapter.AdapterRequestDispatcher} 
 * and coordinator who interact with other coordinators and submit decisions to the <code>AdmissionController</code>
 *  
 * @author Hacene KASDI
 * @version 21.01.2018
 *
 */
public class Coordinator 	extends AdapterRequestDispatcher 
							implements CoordinationLargeScaleI{
	/** port to receive {@link TransitTokenI} from the network */
	private CoordinationLargeScaleInboundPort coordinationLargeScaleInboundPort;
	/** port to send to other autonomic controller a {@link TransitTokenI}*/
	private CoordinationLargeScaleOutboundPort coordinationLargeScaleOutboundPort;

	public Coordinator(String riURI, String applicationURI) throws Exception {
		super(riURI, applicationURI);
		
		/** interfaces used to send information through the topology */ 
		this.addOfferedInterface(CoordinationLargeScaleI.class);
		this.addRequiredInterface(CoordinationLargeScaleI.class);
		
		/** publish the port to receive tokens */
		this.coordinationLargeScaleInboundPort = new CoordinationLargeScaleInboundPort(applicationURI+"COOR_CLSIP", this);
		this.addPort(coordinationLargeScaleInboundPort);
		coordinationLargeScaleInboundPort.publishPort();
		
	}

	@Override
	public void submitChip(TransitTokenI tokenI) throws Exception {
		
		
	}

}

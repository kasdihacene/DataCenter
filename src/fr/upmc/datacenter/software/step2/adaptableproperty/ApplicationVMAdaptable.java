package fr.upmc.datacenter.software.step2.adaptableproperty;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterVMI;
import fr.upmc.datacenter.software.step2.adaptableproperty.ports.AdapterVMInboundPort;

/**
 * 
 * @author Hacene KASDI
 * @version 06.01.2018
 * 
 * this component <code>ApplicationVMAdaptable</code> is allocated cores on 
 * processors of a single computer and uses them
 * to execute the submitted requests.  It maintain a queue for requests waiting
 * a core to become idle before beginning their execution.
 * 
 * This class allows adapter entity to add new <code>AllocatedCore</code> to submit requests for execution
 */
public class ApplicationVMAdaptable extends ApplicationVM implements AdapterVMI {
	
	private AdapterVMInboundPort adapterVMInboundPort;
	public ApplicationVMAdaptable(
			String vmURI, 
			String applicationVMManagementInboundPortURI,
			String requestSubmissionInboundPortURI, 
			String requestNotificationOutboundPortURI) throws Exception {
		
		super(	vmURI, 
				applicationVMManagementInboundPortURI, 
				requestSubmissionInboundPortURI,
				requestNotificationOutboundPortURI);
		
		adapterVMInboundPort = new AdapterVMInboundPort(vmURI+"_AVMIP", this);
		this.addPort(adapterVMInboundPort);
		this.adapterVMInboundPort.publishPort();
	}

	/**
	 * Allocate core found on the Available resources (available computer)
	 */
	@Override
	public void allocateCore(AllocatedCore ac) throws Exception {
		AllocatedCore[] allocatedCoreTable = new AllocatedCore[1];
		allocatedCoreTable[0] = ac;
		allocateCores(allocatedCoreTable);
		startTask();
	}

	/**
	 * release a core
	 */
	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		/** status core allocated to this VM */
		System.err.println("BEFOR :::: core in use == "+ac.processorNo+""+ac.coreNo+" : "+this.allocatedCoresIdleStatus.size());
		this.allocatedCoresIdleStatus.remove(ac);
		System.err.println("AFTER :::: core in use == "+ac.processorNo+""+ac.coreNo+" : "+this.allocatedCoresIdleStatus.size());
	}

	/**
	 * release all cores
	 */
	@Override
	public void releaseAllCores() throws Exception {

	}

}

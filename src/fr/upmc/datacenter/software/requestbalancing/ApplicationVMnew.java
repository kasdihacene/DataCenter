package fr.upmc.datacenter.software.requestbalancing;

import java.util.Map;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;

public class ApplicationVMnew {
	
	/** URI of this application VM.											*/
	protected String						vmURI ;
	/** Status, idle or in use, of each core allocated to this VM.			*/
	protected Map<AllocatedCore,Boolean>	allocatedCoresIdleStatus ;
	/** Map between processor URIs and the outbound ports to call them.		*/
	protected Map<String,ProcessorServicesOutboundPort>
											processorServicesPorts ;
	/** Map between processor URIs and the inbound ports through which task
	 *  termination notifications are received from each processor.			*/
	protected Map<String,ProcessorServicesNotificationInboundPort>
											processorNotificationInboundPorts ;

}

package fr.upmc.datacenter.software.step2.adaptableproperty.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.software.step2.adaptableproperty.ComputerAdaptable;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterComputerI;
/**
 * 
 * @author Hacene KASDI
 * @version 08.01.2018.HKBIRTHDAY
 */
public class AdapterComputerInboundPort 	extends AbstractInboundPort 
											implements AdapterComputerI {

	public AdapterComputerInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, AdapterComputerI.class, owner);
	}

	private static final long serialVersionUID = 18888L;

/**
 * 	
 * @return
 * @throws Exception
 */
	@Override
	public AllocatedCore allocateCore() throws Exception {
		
		final ComputerAdaptable computerAdaptable = (ComputerAdaptable) this.owner;
		return computerAdaptable.handleRequestSync(
				new ComponentI.ComponentService<AllocatedCore>() 
				{
					@Override
					public AllocatedCore call() throws Exception {
						return computerAdaptable.allocateCore();
					}
				}) ;
	}
/**
 * 
 * @param allocatedCore
 * @param frequency
 * @throws UnavailableFrequencyException
 * @throws UnacceptableFrequencyException
 * @throws Exception
 */
	@Override
	public void updateCoreFrequency(AllocatedCore allocatedCore, int frequency)
			throws UnavailableFrequencyException, UnacceptableFrequencyException, Exception {
		final ComputerAdaptable computerAdaptable = (ComputerAdaptable) this.owner;
		computerAdaptable.handleRequestSync(
				new ComponentI.ComponentService<Void>() 
				{
					@Override
					public Void call() throws Exception {
						computerAdaptable.updateCoreFrequency(allocatedCore, frequency);
						return null;
					}
				}) ;
	}
/**
 * 
 * @param ac
 * @throws Exception
 */
	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		final ComputerAdaptable computerAdaptable = (ComputerAdaptable) this.owner;
		computerAdaptable.handleRequestSync(
				new ComponentI.ComponentService<Void>() 
				{
					@Override
					public Void call() throws Exception {
						computerAdaptable.releaseCore(ac);
						return null;
					}
				}) ;		
	}

}

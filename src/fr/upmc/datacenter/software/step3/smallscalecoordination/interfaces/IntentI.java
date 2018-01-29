package fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces;

import java.io.Serializable;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * The interface <code>IntentI</code> must be implemented by intent submitted to a <code>ComputerCoordinator</code>
 * to notify that you want to change the state of the computer
 * 
 * <p><strong>Description</strong></p>
 * Intent can be on <code>Computer</code>'s Cores number or frequency
 * @author jaunecitron
 *
 */
public interface IntentI extends Serializable{
	
	/**
	 * Enum of enable natures of the intent
	 * @author jaunecitron
	 *
	 */
	public enum Nature {
		FREQUENCY, CORE
	}
	
	/**
	 * Enum of type of modification on <code>Computer</code>
	 * @author jaunecitron
	 *
	 */
	public enum Type {
		PLUS, MINUS
	}
	
	/**
	 * Return the nature of modification on <code>Computer</code>
	 * @return
	 */
	public Nature getNature();
	
	/**
	 * Return the type of modification on <code>Computer</code>
	 * @return
	 */
	public Type getType();
	
	/**
	 * Return the value of modification on <code>Computer</code>
	 * @return
	 */
	public int getValue();
	
	/**
	 * Set the value of the intent
	 */
	public void setValue(int value);
	
	/**
	 * Return the URI of the one who send the intent
	 * @return
	 */
	public String getAppURI();
	
	/**
	 * Return the URI of the <code>Computer</code> which intent want to modify
	 */
	public String getComputerURI();
	
	/**
	 * Return the URI of the <code>ApplicationVM</code> which intent will impact
	 * @return
	 */
	public String getAvmURI();
	
	/**
	 * Return the <code>AllocatedCore</code> which intent want to modify, null if none
	 * @return
	 */
	public AllocatedCore getCore();
	
	/**
	 * Return the URI where you can send an intent notification
	 */
	public String getIntentNotificationURI();
	
}

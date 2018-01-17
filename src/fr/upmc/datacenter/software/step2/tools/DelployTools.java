package fr.upmc.datacenter.software.step2.tools;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;

/**
 * This class <code>DelployTools</code> had a reference to the <code>AbstractCVM</code>
 * component, it should be initialized when we launch the Hosting. It used to deploy a new 
 * Components created, especially when whe add new <code>ApplicationAVM</code>
 * to the DataCenter, they must be deployed and integrated to the <code>RequestDiaptcher</code> 
 * 
 * @author Hacene KASDI
 * @version 04.01.2018
 *
 */
public class DelployTools {
	
	private static AbstractCVM acvm;

	
	public static AbstractCVM getAcvm() {
		return acvm;
	}

	public static void setAcvm(AbstractCVM acvm) {
		DelployTools.acvm = acvm;
	}
	/**
	 * Deploy the Component
	 * @param component
	 */
	public static void deployComponent(AbstractComponent component) {
		acvm.addDeployedComponent(component);
	}

}

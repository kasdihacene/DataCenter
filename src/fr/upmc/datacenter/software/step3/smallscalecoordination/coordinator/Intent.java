package fr.upmc.datacenter.software.step3.smallscalecoordination.coordinator;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;

public class Intent implements IntentI{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Nature nature;
	private Type type;
	private int value;
	private AllocatedCore core;
	private String appURI;
	private String computerURI;
	private String avmURI;
	private String intentNotificationURI;
	
	public Intent(Nature nature, Type type, int value, String appURI, String computerURI, String avmURI, String intentNotificationURI) {
		this.nature = nature;
		this.type = type;
		this.value = value;
		this.appURI = appURI;
		this.computerURI = computerURI;
		this.avmURI = avmURI;
		this.intentNotificationURI = intentNotificationURI;
		
		assert intentNotificationURI != null && intentNotificationURI.length() > 0;
	}
	
	public Intent(Nature nature, Type type, int value, String appURI, String computerURI, AllocatedCore core, String intentNotificationURI) {
		this.nature = nature;
		this.type = type;
		this.value = value;
		this.appURI = appURI;
		this.computerURI = computerURI;
		this.core = core;
		this.intentNotificationURI = intentNotificationURI;
		
		assert intentNotificationURI != null && intentNotificationURI.length() > 0;
	}
	
	@Override
	public Nature getNature() {
		return nature;
	}
	@Override
	public Type getType() {
		return type;
	}
	@Override
	public int getValue() {
		return value;
	}
	@Override
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public String getAppURI() {
		return appURI;
	}
	@Override
	public String getComputerURI() {
		return computerURI;
	}
	@Override
	public String getAvmURI() {
		return avmURI;
	}
	@Override
	public AllocatedCore getCore() {
		return core;
	}
	@Override
	public String getIntentNotificationURI() {
		return intentNotificationURI;
	}

}

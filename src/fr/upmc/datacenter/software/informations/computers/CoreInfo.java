package fr.upmc.datacenter.software.informations.computers;

/**
 * The class <code>CoreInfo</code> stores the informations related to a Core in the Processor
 * @author Hacene
 *
 */
public class CoreInfo {
	
	private int coreURI;
	private int frequency;
	private boolean reserved;
	public CoreInfo(int coreURI, int frequency) {
		this.coreURI=coreURI;
		this.frequency=frequency;
		reserved=false;
	}
	
	public boolean isCoreInfoReserved() {
		return reserved;
	}

	public void setCoreInfoReserved(boolean reserved) {
		this.reserved = reserved;
	}

	public int getCoreURI() {
		return coreURI;
	}
	public int getFrequency() {
		return frequency;
	}
	public void updateFrequency(int newFrequency) {
		this.frequency=newFrequency;
	}

}

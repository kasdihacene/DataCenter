package fr.upmc.datacenter.software.informations.computers;

import java.util.Set;
/**
 * 
 * @author Hacene KASDI
 * @version 21.12.17.HK
 *
 */
public class ProcessorInfo {

	private String processorURI;
	private int processorNumber;
	private Set<Integer> possibleFrequencies;
	private int numberOfCores;
	private int maxFrequencyGap;
	private CoreInfo[] coreInfos;
	
	public ProcessorInfo(	String computerURI,
							int defaultFrequency,
							Set<Integer> possibleFrequencies,
							int maxFrequencyGap,
							int number,
							int numberOfCores) {
		super();
		this.processorURI = computerURI+"-Processor"+number;
		this.processorNumber=number;
		this.possibleFrequencies=possibleFrequencies;
		this.maxFrequencyGap=maxFrequencyGap;
		this.coreInfos=new CoreInfo[numberOfCores];
		for (int i = 0; i < coreInfos.length; i++) {
			coreInfos[i]=new CoreInfo(i, defaultFrequency);
		}
	}
	
	public CoreInfo[] getCoreInfos() {
		return coreInfos;
	}

	public String getProcessorURI() {
		return processorURI;
	}

	public int getProcessorNumber() {
		return processorNumber;
	}

	public int getNumberOfCores() {
		return numberOfCores;
	}

	public int getMaxFrequencyGap() {
		return maxFrequencyGap;
	}

	public CoreInfo getCoreInfo(int nbCore) {
		return coreInfos[nbCore];
	}


	/**
	 * 
	 * @param numberCore
	 * @param newFrequency
	 * @return 
	 */
	public boolean canChangeFrequency(int numberCore, int newFrequency) {
		boolean result=false;
		for(CoreInfo coreInfo : coreInfos) {
			if(coreInfo.getCoreURI() == numberCore) {
				if (Math.abs(coreInfo.getFrequency()-newFrequency)>maxFrequencyGap) { 
					result =false;
				}else {
					result = true;	
				}
				break;
		}
			}
		return result;
	}
	/**
	 * Update the frequency of the Core
	 * @param coreNumber
	 * @param newFrequency
	 */
	public void changeFrequencyOfCore(int coreNumber,int newFrequency) {
		for (int i = 0; i < coreInfos.length; i++) {
			if( coreInfos[i].getCoreURI()==coreNumber) {
				coreInfos[i].updateFrequency(newFrequency);
				return;
			}
		}
	}
	
	/**
	 * 
	 * @param frequency
	 * @return true if the processor accepts this frequency, false else
	 */
	public boolean isFrequencyAdmissible(int frequency) {
		return possibleFrequencies.contains(frequency);
	}
}

package fr.upmc.datacenter.software.informations.computers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * This class <code>ComputerInfo</code> store the data related to a computer,
 * it stocks performances and tuninigs 
 * of <code>Computer</code>
 * 
 * @author Hacene KASDI
 * @version 21.12.17.HK
 *
 */
public class ComputerInfo {
	
	private Set<Integer> possibleFrequencies;
	private int maxFrequencyGap;
	private String computerURI;
	private Integer sharedResource;
	private ProcessorInfo[] processorInfos;
	private boolean [][] coreState;
	
	public enum Frequency_gap{
		INCREASE,
		DECREASE
	}
	
	public enum State_change{
		ADD,
		REMOVE
	}
	
	
	public ComputerInfo(String computerURI,
			Set<Integer> possibleFrequencies,
			Map<Integer, Integer> processingPower,
			int defaultFrequency,
			int maxFrequencyGap,
			int numberOfProcessors,
			int numberOfCores) {
		super();
		// Verifying the preconditions
				assert	computerURI != null ;
				assert	possibleFrequencies != null ;
				boolean allPositive = true ;
				for(int f : possibleFrequencies) {
					allPositive = allPositive && (f > 0) ;
				}
				assert	allPositive ;
				assert	processingPower != null ;
				allPositive = true ;
				for(int ips : processingPower.values()) {
					allPositive = allPositive && ips > 0 ;
				}
				assert	allPositive ;
				assert	processingPower.keySet().containsAll(possibleFrequencies) ;
				assert	possibleFrequencies.contains(defaultFrequency) ;
				int max = -1 ;
				for(int f : possibleFrequencies) {
					if (max < f) {
						max = f ;
					}
				}
				assert	maxFrequencyGap >= 0 && maxFrequencyGap <= max ;
				assert	numberOfProcessors > 0 ;
				assert	numberOfCores > 0 ;
				
		// Construct a data related to Processors and Cores
				this.computerURI = computerURI;
				this.maxFrequencyGap=maxFrequencyGap;
				this.possibleFrequencies=possibleFrequencies;
				
				this.coreState=new boolean[1][1];
				this.sharedResource= new Integer(0);
				processorInfos=new ProcessorInfo[numberOfProcessors];
				for (int i = 0; i < processorInfos.length; i++) {
					processorInfos[i]=new ProcessorInfo(
							computerURI,
							defaultFrequency, 
							possibleFrequencies, 
							maxFrequencyGap, 
							i, 
							numberOfCores);
				}
	}
	
		
	public ProcessorInfo[] getProcessorInfos() {
		return processorInfos;
	}


	public int getMaxFrequencyGap() {
		return maxFrequencyGap;
	}

	public String getComputerURI() {
		return computerURI;
	}

	public Integer getSharedResource() {
		return sharedResource;
	}
	

	public void setSharedResource(Integer sharedResource) {
		this.sharedResource = sharedResource;
	}


	public boolean[][] getCoreState() {
		return coreState;
	}

	public void setCoreState(boolean[][] coreState) {
		this.coreState = coreState;
	}
	/**
	 * Update the state of the Cores on the Computer
	 * @param allocatedCores
	 * @param NBCORES
	 */
	public void updateCoresState(boolean allocatedCores[][], int NBCORES,State_change state) {
		if(state == State_change.ADD) {
		int nbCoresToAllocate=0;
		for (int i = 0; i < allocatedCores.length; i++) {
			for (int j = 0; j < allocatedCores[i].length; j++) {
				// Allocate just NBCORES
				if(nbCoresToAllocate<NBCORES) {
					if(!allocatedCores[i][j])
					{allocatedCores[i][j] = true; nbCoresToAllocate++;}
				}
			}
		}
		}else {
			int nbCoresToAllocate=0;
			for (int i = 0; i < allocatedCores.length; i++) {
				for (int j = 0; j < allocatedCores[i].length; j++) {
					// Release just NBCORES used 
					if(nbCoresToAllocate<NBCORES) {
						if(!allocatedCores[i][j])
						{allocatedCores[i][j] = false; nbCoresToAllocate++;}
					}
				}
			}
		}
		this.setCoreState(allocatedCores);
	}
	/**
	 * 
	 * @return number of available cores
	 */
	public int getNbCoreAvailable() {
		int nbCores = 0;
		for (int i = 0; i < coreState.length; i++) {
			for (int j = 0; j < coreState[i].length; j++) {
				if(!coreState[i][j])
					nbCores++;
			}
		}
		return nbCores;
	}

	/**
	 * Get the ProcessorInfo according to his URI
	 * @param processorURI
	 * @return the Processor Information
	 */
	public ProcessorInfo getProcessorInfo(String processorURI) {
		ProcessorInfo pi=null;
		for (int i = 0; i < processorInfos.length; i++) {
			if(processorInfos[i].getProcessorURI().equals(processorURI))
				pi=processorInfos[i];
		}
		return pi;
	}
	
	/**
	 * Get the CoreInfo according to AllocatedCore of an ApplicationVM
	 * @param ac
	 * @return Core Information of an AllocatedCore
	 */
	public CoreInfo getCoreInfo(AllocatedCore ac) {
		return processorInfos[ac.processorNo].getCoreInfo(ac.coreNo);
	}
	
	/**
	 * Get frequency of an AllocatedCore  
	 * @param ac
	 * @return the frequency of an AllocatedCore
	 */
	public int getAllocatedCoreFrequency(AllocatedCore ac) {
		return processorInfos[ac.processorNo].getCoreInfo(ac.coreNo).getFrequency();
	}
	
	/**
	 * Check if we can change frequency of AllocatedCore
	 * @param ac
	 * @param newFrequency
	 * @return true if we can change the frequency of AllocatedCore
	 */
	public boolean canChangeAllocatedCoreFrequency(AllocatedCore ac, int newFrequency) {
		return processorInfos[ac.processorNo].canChangeFrequency(ac.coreNo, newFrequency);
	}
	/**
	 * Change the frequency of AllocatedCore
	 * @param ac
	 * @param newFrequency
	 */
	public void changeAllocatedCoreFrequency(AllocatedCore ac, int newFrequency) {
		processorInfos[ac.processorNo].changeFrequencyOfCore(ac.coreNo, newFrequency);
	}
	
	/**
	 * Check if the frequency is admissible
	 * @param frequency
	 * @return true if the frequency is admissible
	 */
	public boolean isFrequencyAdmissible(int frequency) {
		return this.possibleFrequencies.contains(frequency);
	}
	
	/**
	 * 
	 * @param ac
	 * @return
	 */
	public int canChangeCurrentFrequency(AllocatedCore ac, Frequency_gap frequency_gap) {
		ArrayList<Integer> possibleFrequenciesArray = new ArrayList<>();
		for (int f : possibleFrequencies) {
			possibleFrequenciesArray.add(f);
		}
		Collections.sort(possibleFrequenciesArray);
		int frequency = processorInfos[ac.processorNo].getCoreInfo(ac.coreNo).getFrequency();
		
		if(frequency_gap == Frequency_gap.DECREASE) {
		for (int freq : possibleFrequencies ) {
			if(freq < frequency)
				return freq;
		}
		}
		
		if(frequency_gap == Frequency_gap.INCREASE) {
			for (int freq : possibleFrequencies ) {
				if(freq > frequency)
					return freq;
		}
		}
		return frequency;
	}
	/**
	 * 
	 * @return Set of admissible Frequencies
	 */
	public Set<Integer> getAdmissibleFrequencies() {
		return possibleFrequencies;
	}
}

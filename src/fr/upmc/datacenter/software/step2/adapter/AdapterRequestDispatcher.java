package fr.upmc.datacenter.software.step2.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.dataprovider.connectors.DataProviderConnector;
import fr.upmc.datacenter.dataprovider.connectors.DataProviderDispatcherConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderDispatcherI;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.dataprovider.ports.DataDispatcherOutboundPort;
import fr.upmc.datacenter.dataprovider.ports.DataProviderOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.admissioncontroller.ResourceInspector;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;
import fr.upmc.datacenter.software.step2.adaptableproperty.connector.AdapterComputerConnector;
import fr.upmc.datacenter.software.step2.adaptableproperty.connector.AdapterVMConnector;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterComputerI;
import fr.upmc.datacenter.software.step2.adaptableproperty.interfaces.AdapterVMI;
import fr.upmc.datacenter.software.step2.adaptableproperty.ports.AdapterComputerOutboundPort;
import fr.upmc.datacenter.software.step2.adaptableproperty.ports.AdapterVMOutboundPort;
import fr.upmc.datacenter.software.step2.adapter.interfaces.DataPushDispatcherReceiverI;
import fr.upmc.datacenter.software.step2.requestresourcevm.RequestVM;
import fr.upmc.datacenter.software.step2.requestresourcevm.connector.RequestResourceVMConnector;
import fr.upmc.datacenter.software.step2.sensor.DataPushDispatcherI;
import fr.upmc.datacenter.software.step2.sensor.ports.SensorDispatcherOutboundPort;
import fr.upmc.datacenter.software.step2.tools.DelployTools;

/**
 * 
 * @author Hacene KASDI
 * @version 02.01.2018
 * 
 * This class <code>AdapterRequestDispatcher</code> represents the Adapter of the current behavior 
 * of Application VM and allocated cores, we can do adaption as Changing the frequency of one or 
 * many cores adding or removing a core, adding an AVM or removing one.
 * 
 * The rolling-average is preferably an average of four consecutive required average amounts pushed
 * by the adaptable entity <code>RequestDispatcherComponent</code>
 *
 */
public class AdapterRequestDispatcher 	extends 	ResourceInspector 
										implements 	DataPushDispatcherReceiverI {

	private String appURI;
	/** future of the task scheduled to start adaption					*/
	protected ScheduledFuture<?>			pushingFuture ;
	private SensorDispatcherOutboundPort sdop;
	private LinkedList<InfoRequestResponse> requestResponsesInfo;
	private LinkedList<Double> rollingAverage;
	
	private DataProviderOutboundPort 			dpop;
	private DataDispatcherOutboundPort 			ddop;
	
	private AdapterComputerOutboundPort acop;
	private AdapterVMOutboundPort avmiop;
	
	private Double lastAverageIdentified = 0.;
	
	public AdapterRequestDispatcher(String riURI, String applicationURI) throws Exception {
		super(riURI);
		this.appURI				= applicationURI;
		requestResponsesInfo	= new LinkedList<InfoRequestResponse>();
		rollingAverage			= new LinkedList<Double>();
		
		//Used to ask for informations about ApplicationContainer and RequestDispatcher
		this.addRequiredInterface(DataProviderDispatcherI.class);
		//Used to ask for informations about Computer, processor and Core
		this.addRequiredInterface(DataProviderI.class);
		
		//=====================================================
		//		Interfaces used for Adaption
		//=====================================================
		
		this.addRequiredInterface(AdapterComputerI.class);
		this.addRequiredInterface(AdapterVMI.class);
		
		this.acop 		= new AdapterComputerOutboundPort(riURI+"ADAPTER_ACOP", this);
		this.avmiop		= new AdapterVMOutboundPort(riURI+"ADAPTER_AVMIOP", this);
		
		this.addPort(acop);
		this.addPort(avmiop);
		
		this.acop.publishPort();
		this.avmiop.publishPort();
		
		//=====================================================
		
		this.addRequiredInterface(DataRequiredI.PullI.class) ;
		this.addOfferedInterface(DataRequiredI.PushI.class);
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
		
		this.dpop 		= new DataProviderOutboundPort			(riURI+"ADAPTER_DPOP", this);
		this.ddop		= new DataDispatcherOutboundPort		(riURI+"ADAPTER_DDOP", this);

		this.addPort(dpop);
		this.addPort(ddop);
		
		this.dpop.publishPort();
		this.ddop.publishPort();
		
		this.sdop = new SensorDispatcherOutboundPort(riURI+"_SDOP", this);
		this.addPort(sdop);
		this.sdop.publishPort();
	}

	/**
	 * @return the URI
	 */
	public String getAppURI() {
		return appURI;
	}
	
	public void connectAdapterWithProvider(String providerURI) throws Exception {
		this.dpop	.doConnection(providerURI+"_DPIP", DataProviderConnector.class.getCanonicalName());
		this.ddop	.doConnection(providerURI+"_DDIP", DataProviderDispatcherConnector.class.getCanonicalName());
	}
	/**
	 * Connect the Adapter to Request Dispatcher and launch pushing
	 * @param appURI
	 * @throws Exception
	 */
	public void connectWithRequestDispatcher(String appURI) throws Exception {
		
		// connection with Request Dispatcher Component
		sdop.doConnection(appURI+"RD_SDIP", ControlledDataConnector.class.getCanonicalName());
		sdop.startUnlimitedPushing(3000);
		
		// Get configuration file to tune thresholds
		HashMap<String, String> reglages = ControllerSetting.getTuninigs("tuningsThreshold");
		try {
			
			
			ControllerSetting.averageThreshold 			= Double.valueOf(reglages.get("averageThreshold"));
			ControllerSetting.VMCoefficient 			= Double.valueOf(reglages.get("VMCoefficient"));
			ControllerSetting.coreCoefficient 			= Double.valueOf(reglages.get("coreCoefficient"));
			ControllerSetting.freaquenceCoefficient 	= Double.valueOf(reglages.get("freaquenceCoefficient"));
			ControllerSetting.NBCONSECUTIVEAVERAGE 		= Integer.valueOf(reglages.get("NBCONSECUTIVEAVERAGE"));
			ControllerSetting.interavalAdaption 		= Integer.valueOf(reglages.get("interavalAdaption"));
			
		} catch (Exception e) {
			System.err.println("Please verify your configuration file <tuningsThreshold> !");
			DelployTools.getAcvm().shutdownNow();
			System.err.println("Sorry, the system could not start!");
		}
		
	}
	
	/**
	 * receives the pushes from the Request Dispatcher component and 
	 * do adaptation if necessary.
	 */
	@Override
	public void receivePushedData(DataPushDispatcherI dataPushDispatcherI) throws Exception {
		
		requestResponsesInfo=dataPushDispatcherI.getListStatsAVMs();
		rollingAverage.add(dataPushDispatcherI.getExecutionAverage());
		
		if (rollingAverage.size() > ControllerSetting.NBCONSECUTIVEAVERAGE) {
			rollingAverage.removeFirst();
		}

	}
	
	/**
	 * 
	 * @return URI of the less efficient AVM
	 */
	public String getAVMlessEfficient() {
		String avmURI="";
		synchronized (requestResponsesInfo) {
			double avmAverage=0;
		for (InfoRequestResponse infoRequestResponse : requestResponsesInfo) {
			if(avmAverage <= infoRequestResponse.calculateAverage()) {
			avmAverage = infoRequestResponse.calculateAverage();
			avmURI = infoRequestResponse.getAvmURI();
			}
		}
			return avmURI;
		}
	}
	
	public void launchAdaption() throws Exception {
		// We can take measures only if we have 4 samples of averages on our list
		if(rollingAverage.size()==ControllerSetting.NBCONSECUTIVEAVERAGE) {
//			System.err.println("COLLECTED LIST OF "+getAppURI()+" : -----> "+rollingAverage);
			
		// Get the current rolling average that is collected
		Double CurrentRollingAverage = getRollingAverage();
		System.err.println(getAppURI()+" | AVM less efficient : "+getAVMlessEfficient()+" | EXECUTION AVERAGE : "+CurrentRollingAverage);

		synchronized (CurrentRollingAverage) {
			if(!CurrentRollingAverage.isNaN()) {
				//If rolling-average >
				if(CurrentRollingAverage > (ControllerSetting.averageThreshold * ControllerSetting.VMCoefficient)) {
					System.err.println("============ ADD AVM FOR : "+getAppURI());
					allocateNewAVM(getAppURI());
					// Reset calculation
					rollingAverage.clear();
				}else {
					// If rolling-average > 
					if (CurrentRollingAverage > (ControllerSetting.averageThreshold * ControllerSetting.coreCoefficient)) {
						System.err.println("============ ADD CORE FOR : "+getAppURI());
						addCoreToLessEfficientAVM();
						// Reset calculation
						rollingAverage.clear();
					}else {
						// If rolling-average > 
						if(CurrentRollingAverage > (ControllerSetting.averageThreshold * ControllerSetting.freaquenceCoefficient)){
							System.err.println("============  INCREASE FREQUENCY FOR : "+getAppURI());
							updateCoreFrequency(getAppURI(), 3000);
							// Reset calculation
							rollingAverage.clear();
						}
					}
				}
				
			}
		}
		}
	}
	/**
	 * Add an AllocatedCore to an AVM
	 * @param nbCores
	 * @throws Exception 
	 */
	public void addCoreToLessEfficientAVM() throws Exception {
				// Get available resources
				LinkedList<String> listComputers = this.dpop.getComputerListURIs();
				// List of available computers
				LinkedList<String> availableComputers = new LinkedList<String>();
				
				for (String uri : listComputers) {
					ComputerInfo computerInfo = dpop.getComputerInfos(uri);
					
					boolean[][] allocatedCores;
					int nbAvailableCores=0;
					
					synchronized (computerInfo) {
						// get number of available core of this computer
						allocatedCores=computerInfo.getCoreState();
						for (int i = 0; i < allocatedCores.length; i++) {
							for (int j = 0; j < allocatedCores[i].length; j++) {
								if(!allocatedCores[i][j]) nbAvailableCores++;
							}
						}
						// check if nbCoresAvailable >= NBCORES than set these cores as allocated
						if(nbAvailableCores >= 1) {
							availableComputers.add(uri);
						}
					}
				}
				
				if(!availableComputers.isEmpty()) {
					String computerURI = availableComputers.getFirst();
					// Connect to AdaptableComputer and ask for adding an AllocatedCore
					acop.doConnection(computerURI+"_ACIP",AdapterComputerConnector.class.getCanonicalName());
					AllocatedCore allocatedCore = acop.allocateCore();
					acop.doDisconnection();
					// add the AllocatedCore got from the computer to an ApplicationVM
					// Get information about Request Dispatcher Component who sends the Data to current Adapter
					RequestDispatcherInfo requestDispatcherInfo = dataProviderOutboundPort.getApplicationInfos(getAppURI());
					// Get All AVM used by the Request Dispatcher
					LinkedHashMap<String,ApplicationVMInfo> listAVMInformation = requestDispatcherInfo.getAllVmInformation();
					// get the AVM less efficient
					String avmURI = getAVMlessEfficient();
					if(!"".equals(avmURI)) {
						System.out.println();
						System.err.println(">>>>>>>>>>>>>>>>>>>>>>>> "+avmURI);	
						System.err.println(">>>>>>>>>>>>>>>>>>>>>>>> "+listAVMInformation.get(avmURI).getComputerURI());
						// Connect with the AVM less efficient and add him a Core
						avmiop.doConnection(avmURI+"_AVMIP",AdapterVMConnector.class.getCanonicalName());
						// Update ApllicationVm Informations
						listAVMInformation.get(avmURI).addCore(allocatedCore);
						// add concretely the AllocatedCore in the ApplicationVM component
						avmiop.allocateCore(allocatedCore);
						avmiop.doDisconnection();
					
					}else {
						System.err.println("No information about AVMs ! we cannot allocate new Core");
					}
				}else {
					System.err.println("Cannot add new Core ! ");
					System.err.println("No resource found ! ");
				}
	}
	
	/**
	 * Update frequency cores of all cores used by the AVMs
	 * @param applURI
	 * @param frequency
	 * @throws Exception
	 */
	public void updateCoreFrequency(String applURI, Integer frequency) throws Exception {
		// Get information about Request Dispatcher Component who sends the Data to current Adapter
		RequestDispatcherInfo requestDispatcherInfo = dataProviderOutboundPort.getApplicationInfos(applURI);
		// Get All AVM used by the Request Dispatcher
		LinkedHashMap<String,ApplicationVMInfo> listAVMInformation = requestDispatcherInfo.getAllVmInformation();
		// get the first AllocatedCore who accept this frequency found on the list 
		// of computers used by this AVM
		
		//update frequency of all cores used by the current Request Dispatcher
		HashMap<AllocatedCore,ApplicationVMInfo> computerCores = new HashMap<>();
		
		for (ApplicationVMInfo applicationVMInfo : listAVMInformation.values()) {

			for (AllocatedCore core :  applicationVMInfo.getAllocatedCores()) {
				ComputerInfo computerInfo = dataProviderOutboundPort.getComputerInfos(applicationVMInfo.getComputerURI());
				
				// return the first AllocatedCore found
				if(computerInfo.isFrequencyAdmissible(frequency) && computerInfo.canChangeAllocatedCoreFrequency(core, frequency)) {
					computerCores.put(core,applicationVMInfo);
				}
			}
		}
		
		// launch adaptation of a new Core frequency
		if(!computerCores.isEmpty()) {
			for (AllocatedCore core : computerCores.keySet()) {
			// Update core frequency on the ComputerInfo
			ComputerInfo computerInfo = dataProviderOutboundPort.getComputerInfos(computerCores.get(core).getComputerURI());
			computerInfo.changeAllocatedCoreFrequency(core, frequency);
			//  Update Processor Core frequency concretely
			acop.doConnection(computerInfo.getComputerURI()+"_ACIP",AdapterComputerConnector.class.getCanonicalName());
			acop.updateCoreFrequency(core, frequency);
			acop.doDisconnection();
			}
		}else {
			System.err.println("No core accept to increase frequency ! ");
		}
	}
	
	
	/**
	 * Allocate a new AVM to submit requests
	 * @param RDuri
	 * @throws Exception 
	 */
	public void allocateNewAVM(String ApplicationURI) throws Exception {
		// Get available resources
		LinkedList<String> listComputers = this.dpop.getComputerListURIs();
		
		// List of available computers
		LinkedList<String> availableComputers = new LinkedList<String>();
		for (String uri : listComputers) {
			ComputerInfo computerInfo = dpop.getComputerInfos(uri);
			
			boolean[][] allocatedCores;
			int nbAvailableCores=0;
			
			synchronized (computerInfo) {
				// get number of available core of this computer
				allocatedCores=computerInfo.getCoreState();
				for (int i = 0; i < allocatedCores.length; i++) {
					for (int j = 0; j < allocatedCores[i].length; j++) {
						if(!allocatedCores[i][j]) nbAvailableCores++;
					}
				}
				// check if nbCoresAvailable >= NBCORES than set these cores as allocated
				if(nbAvailableCores >= 4) {
					availableComputers.add(uri);
				}
			}
		}
		
		System.out.println();
		System.out.println("+++++++++++++++++++++ AVAILABLE COMPUTERS FOR "+getAppURI()+" : "+availableComputers.size());
		for (String uri : availableComputers) {
				System.out.println("+++++++++++++++++++++ "+uri);
		}
		// Create a new ApplicationVM and deploy it
		if(!availableComputers.isEmpty()) {
			String computerURI = availableComputers.getFirst();
			ApplicationVMAdaptable aVmAdaptable = createApplicationVM(getAppURI(), computerURI);
			
			
			RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort.getApplicationInfos(getAppURI());
			
			// Connect the ApplicationVM to the RequestDispatcher
			synchronized (dispatcherInfo) {
				String avmURI=getAppURI()+"AVM_"+(dispatcherInfo.getNbVMCreated()-1);
				// Connect the AVM to Request Dispatcher for sending Notifications
				aVmAdaptable.doPortConnection(
									avmURI+"_RNOP",
									getAppURI()+"RD_RNIP", 
									RequestNotificationConnector.class.getCanonicalName());
				
			}
			
			boolean isCreated = adapteNewComponent(computerURI, aVmAdaptable);
			System.out.println("+++++++++++++++++++++ THE AVM ADDED TO APPLICATION : "+isCreated);
			if(isCreated) {
				rollingAverage.clear();
			}
		}else {
			System.err.println("Cannot adapte new ApplicationVM ! ");
			System.err.println("No resource found ! ");
		}
		}

	
	/**
	 * 
	 * @param computerURI
	 * @param avm
	 * @return TRUE if the AVM was successefuly adapted
	 * @throws Exception
	 */
	public boolean adapteNewComponent(String computerURI, ApplicationVMAdaptable avm) throws Exception {
		try {
			// Get the ApplicationVM URI
			RequestDispatcherInfo dispatcherInfo = dpop.getApplicationInfos(getAppURI());
			String avmURIrecentlyAdded = dispatcherInfo.getAVMRecentlyAdded().getVmURI();
			
			// Update Cores state on Data Provider 
			ComputerInfo computerInfo = dpop.getComputerInfos(computerURI);
			computerInfo.updateCoresState(computerInfo.getCoreState(), 4);
			
			// Connect the AVM to Request Dispatcher for sending Notifications
			avm.doPortConnection(
								avmURIrecentlyAdded+"_RNOP",
								getAppURI()+"RD_RNIP", 
								RequestNotificationConnector.class.getCanonicalName());
			
			// Add AVM URI on the list of AVMs of the RequestDispatcher
			requestResourceVMOutboundPort.doConnection(
								getAppURI()+"RD_RVMIP",
								RequestResourceVMConnector.class.getCanonicalName());
			RequestVM requestVMI = new RequestVM(avmURIrecentlyAdded, getAppURI());
			requestResourceVMOutboundPort.requestAddVM(requestVMI);

		} catch (Exception e) {
			return false;
		}
			return true;
	}
	
	
	public void removeAVM() {
		
	}
	
	public void removeCore() {
		
	}
	/**
	 * start a periodic adaption
	 * @throws Exception
	 */
	public void	launchAdaptionEveryInterval() throws Exception
		{
		final AdapterRequestDispatcher AdapterRequestDispatcher = this ;
		this.pushingFuture =
				this.scheduleTask(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								try {
									AdapterRequestDispatcher.launchAdaption();
									launchAdaptionEveryInterval();
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						},
						TimeManagement.acceleratedDelay(ControllerSetting.interavalAdaption),
						TimeUnit.MILLISECONDS) ;
		}
	
	/**
	 * 
	 * @return the rolling average on amount of 4 samples
	 */
	public double getRollingAverage() {
		double rollinAverage =0;
		for (double average : rollingAverage ) {
			rollinAverage = rollinAverage + average;
		}
		return (rollinAverage/rollingAverage.size());
	}
	
	//===========================================================================
	/**
	 * This calss used to load data from the configuration file and to tune our 
	 * adaption and to launch our mechanism of adaption.
	 * 
	 * @author Hacene KASDI
	 * Class to initialize threshold variables read from an adjustment file
	 */
	public static class ControllerSetting{
		
		/**
		 * 
		 * @param file
		 * @return HashMap of variables to tune our adaption
		 * @throws Exception 
		 */
		public static HashMap<String, String> getTuninigs(String file) throws Exception{
			HashMap<String, String> tunings = new HashMap<String,String>();
			try {
			File fileTuninig = new File(file);
			Scanner reader = new Scanner(fileTuninig);
			
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String[] content = line.split(" "); 
				tunings.put(content[0], content[1]);
			}
			reader.close();
			}catch(FileNotFoundException fnf) {
				System.err.println("File not found on the root of the project !");
				DelployTools.getAcvm().shutdownNow();
				System.err.println("Sorry, the system could not start!");
			}
			return tunings;		
		}
		public static double averageThreshold;
		public static double freaquenceCoefficient;
		public static double coreCoefficient;
		public static double VMCoefficient;
		public static int NBCONSECUTIVEAVERAGE;
		public static int interavalAdaption;

		
		
	}

}

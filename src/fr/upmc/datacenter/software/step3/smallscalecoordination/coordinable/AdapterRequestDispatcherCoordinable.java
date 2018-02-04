package fr.upmc.datacenter.software.step3.smallscalecoordination.coordinable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo.Frequency_gap;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step2.adaptableproperty.connector.AdapterComputerConnector;
import fr.upmc.datacenter.software.step2.adaptableproperty.connector.AdapterVMConnector;
import fr.upmc.datacenter.software.step2.adapter.AdapterRequestDispatcher;
import fr.upmc.datacenter.software.step3.smallscalecoordination.connectors.IntentSubmissionConnector;
import fr.upmc.datacenter.software.step3.smallscalecoordination.coordinator.Intent;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI.Nature;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI.Type;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentNotificationHandlerI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentNotificationI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentSubmissionI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.ports.IntentNotificationInboundPort;
import fr.upmc.datacenter.software.step3.smallscalecoordination.ports.IntentSubmissionInboundPort;
import fr.upmc.datacenter.software.step3.smallscalecoordination.ports.IntentSubmissionOutboundPort;

public class AdapterRequestDispatcherCoordinable extends AdapterRequestDispatcher
		implements IntentNotificationHandlerI, IntentSubmissionI {

	private String inipURI;
	private IntentNotificationInboundPort inip;
	private String isopURI;
	private IntentSubmissionOutboundPort isop;

	public AdapterRequestDispatcherCoordinable(String riURI, String applicationURI) throws Exception {
		super(riURI, applicationURI);
		
		this.inipURI = riURI + IntentNotificationInboundPort.SUFFIX;
		this.addOfferedInterface(IntentNotificationI.class);
		this.inip = new IntentNotificationInboundPort(this.inipURI, this);
		this.addPort(this.inip);
		this.inip.publishPort();

		this.isopURI = riURI + IntentSubmissionOutboundPort.SUFFIX;
		this.addRequiredInterface(IntentSubmissionI.class);
		this.isop = new IntentSubmissionOutboundPort(this.isopURI, this);
		this.addPort(this.isop);
		this.isop.publishPort();
	}
	
	@Override
	public void launchAdaption() throws Exception {
		super.launchAdaption();
		System.out.println(String.format("RDAdapter<%s> : launch adaptation", this.riURI));
	}
	
	/**
	 * @see {@link AdapterRequestDispatcher#addCoreToLessEfficientAVM()} Send to
	 *      <code>ComputerCoordinator</code> an intent to add an
	 *      <code>AllocatedCore</code> to the less efficient
	 *      <code>ApplicationVM</code>
	 */
	@Override
	public void addCoreToLessEfficientAVM() throws Exception {
		// Get available resources
		LinkedList<String> listComputers = dataProviderOutboundPort.getComputerListURIs();
		// List of available computers
		ArrayList<String> availableComputers = new ArrayList<String>();

		for (String uri : listComputers) {
			ComputerInfo computerInfo = dataProviderOutboundPort.getComputerInfos(uri);

			boolean[][] allocatedCores;
			int nbAvailableCores = 0;

			synchronized (computerInfo) {
				// get number of available core of this computer
				allocatedCores = computerInfo.getCoreState();
				for (int i = 0; i < allocatedCores.length; i++) {
					for (int j = 0; j < allocatedCores[i].length; j++) {
						if (!allocatedCores[i][j])
							nbAvailableCores++;
					}
				}
				// check if nbCoresAvailable >= NBCORES than set these cores as allocated
				if (nbAvailableCores >= 1) {
					availableComputers.add(uri);
				}
			}
		}

		if (!availableComputers.isEmpty()) {
			Random r = new Random();
			String computerURI = availableComputers.get(r.nextInt(availableComputers.size()));
			String avmURI = getAVMlessEfficient();
			if (avmURI.length() > 0) {
				submitIntent(new Intent(Nature.CORE, Type.PLUS, 1, getAppURI(), computerURI, avmURI, this.inipURI));
			} else {
				System.err.println("No information about AVMs ! we cannot allocate new Core");
			}
		} else {
			System.err.println("Cannot add new Core ! ");
			System.err.println("No resource found ! ");
		}
	}

	/**
	 * Add to the less <code>ApplicationVM</code> an <code>ApplicationVM</code>
	 * 
	 * @param computerURI
	 *            : URI of the <code>Computer</code> which will provide the
	 *            <code>AllocatedCore</code>
	 * @param avmURI
	 *            : URI of the <code>ApplicationVM</code> which will obtain the new
	 *            <code>AllocatedCore</code>
	 * @throws Exception
	 */
	private void addCoreToLessEfficientAVM(String computerURI, String avmURI) throws Exception {
		// Connect to AdaptableComputer and ask for adding an AllocatedCore
		acop.doConnection(computerURI + "_ACIP", AdapterComputerConnector.class.getCanonicalName());
		AllocatedCore allocatedCore = acop.allocateCore();
		acop.doDisconnection();
		// add the AllocatedCore got from the computer to an ApplicationVM
		// Get information about Request Dispatcher Component who sends the Data to
		// current Adapter
		RequestDispatcherInfo requestDispatcherInfo = dataProviderOutboundPort.getApplicationInfos(getAppURI());
		// Get All AVM used by the Request Dispatcher
		LinkedHashMap<String, ApplicationVMInfo> listAVMInformation = requestDispatcherInfo.getAllVmInformation();
		// get the AVM less efficient

		if (!"".equals(avmURI)) {
			System.out.println();
			System.err.println(">>>>>>>>>>>>>>>>>>>>>>>> " + avmURI);
			System.err.println(">>>>>>>>>>>>>>>>>>>>>>>> " + listAVMInformation.get(avmURI).getComputerURI());
			// Connect with the AVM less efficient and add him a Core
			avmiop.doConnection(avmURI + "_AVMIP", AdapterVMConnector.class.getCanonicalName());
			// Update ApllicationVm Informations
			listAVMInformation.get(avmURI).addCore(allocatedCore);
			// add concretely the AllocatedCore in the ApplicationVM component
			avmiop.allocateCore(allocatedCore);
			avmiop.doDisconnection();

		} else {
			System.err.println("No information about AVMs ! we cannot allocate new Core");
		}
	}

	/**
	 * @see {@link AdapterRequestDispatcher#removeCoreFromAVM()} Send to
	 *      <code>ComputerCoordinator</code> an intent to remove an
	 *      <code>AllocatedCore</code> to the most efficient
	 *      <code>ApplicationVM</code>
	 */
	@Override
	public void removeCoreFromAVM() throws Exception {

		// add the AllocatedCore got from the computer to an ApplicationVM
		// Get information about Request Dispatcher Component who sends the Data to
		// current Adapter
		RequestDispatcherInfo requestDispatcherInfo = dataProviderOutboundPort.getApplicationInfos(getAppURI());

		// Get All AVM used by the Request Dispatcher
		LinkedHashMap<String, ApplicationVMInfo> listAVMInformation = requestDispatcherInfo.getAllVmInformation();

		// get the AVM more efficient
		String avmURI = this.getAVMmoreEfficient();

		if (!"".equals(avmURI)) {

		} else {
			System.err.println("No information about AVMs ! we cannot allocate new Core");
		}
	}

	public void removeCoreFromAVM(String computerURI, String avmURI) throws Exception {
		RequestDispatcherInfo requestDispatcherInfo = dataProviderOutboundPort.getApplicationInfos(getAppURI());
		// Get All AVM used by the Request Dispatcher
		LinkedHashMap<String, ApplicationVMInfo> listAVMInformation = requestDispatcherInfo.getAllVmInformation();
		AllocatedCore core = listAVMInformation.get(avmURI).getLastCore();
		listAVMInformation.get(avmURI).removeCore(core);

		System.err.println(
				String.format(">>>>>>>>>>>>>>>>>>>>>>>> AVM AND COMPUTER USED ==> %s  %s", computerURI, avmURI));
		// Connect with the AVM less efficient and add him a Core
		avmiop.doConnection(avmURI + "_AVMIP", AdapterVMConnector.class.getCanonicalName());
		// remove concretely the AllocatedCore in the <code>ApplicationVM</code>
		// Component
		avmiop.releaseCore(core);
		avmiop.doDisconnection();

		// remove Processor Core concretely on <code>Processor</code>
		acop.doConnection(computerURI + "_ACIP", AdapterComputerConnector.class.getCanonicalName());
		acop.releaseCore(core);
		acop.doDisconnection();
	}

	@Override
	public void updateCoreFrequency(String applURI, Frequency_gap frequency_gap) throws Exception {
		// Get information about Request Dispatcher Component who sends the Data to
		// current Adapter
		RequestDispatcherInfo requestDispatcherInfo = dataProviderOutboundPort.getApplicationInfos(applURI);
		// Get All AVM used by the Request Dispatcher
		LinkedHashMap<String, ApplicationVMInfo> listAVMInformation = requestDispatcherInfo.getAllVmInformation();
		// get the first AllocatedCore who accept this frequency found on the list
		// of computers used by this AVM

		// update frequency of all cores used by the current Request Dispatcher
		HashMap<AllocatedCore, ApplicationVMInfo> computerCores = new HashMap<>();

		for (ApplicationVMInfo applicationVMInfo : listAVMInformation.values()) {

			for (AllocatedCore core : applicationVMInfo.getAllocatedCores()) {
				ComputerInfo computerInfo = dataProviderOutboundPort
						.getComputerInfos(applicationVMInfo.getComputerURI());
				int freq = computerInfo.canChangeCurrentFrequency(core, frequency_gap);
				System.err.println(
						"Frequency changed from : " + computerInfo.getAllocatedCoreFrequency(core) + " to ==> " + freq);
				// return the first AllocatedCore found
				if (computerInfo.isFrequencyAdmissible(freq)
						&& computerInfo.canChangeAllocatedCoreFrequency(core, freq)) {
					computerCores.put(core, applicationVMInfo);
				}
			}
		}

		// launch adaptation of a new Core frequency
		if (!computerCores.isEmpty()) {
			for (AllocatedCore core : computerCores.keySet()) {
				// Update core frequency on the ComputerInfo
				ComputerInfo computerInfo = dataProviderOutboundPort
						.getComputerInfos(computerCores.get(core).getComputerURI());
				int frequency = computerInfo.canChangeCurrentFrequency(core, frequency_gap);
				submitIntent(
						new Intent(Nature.FREQUENCY, frequency_gap == Frequency_gap.INCREASE ? Type.PLUS : Type.MINUS,
								frequency, getAppURI(), computerInfo.getComputerURI(), core, this.inipURI));
			}
		} else {
			System.err.println("No core accept to increase frequency ! ");
		}
	}

	private void updateCoreFrequency(String computerURI, AllocatedCore core, int frequency) throws Exception {
		ComputerInfo computerInfo = dataProviderOutboundPort.getComputerInfos(computerURI);
		computerInfo.changeAllocatedCoreFrequency(core, frequency);
		// Update Processor Core frequency concretely
		acop.doConnection(computerInfo.getComputerURI() + "_ACIP", AdapterComputerConnector.class.getCanonicalName());
		acop.updateCoreFrequency(core, frequency);
		acop.doDisconnection();
	}

	@Override
	public void submitIntent(IntentI intent) throws Exception {
		System.out.println(
				String.format("RDAdapter<%s> : SUBMIT AN INTENT to %s to %s %d %s", this.riURI, intent.getComputerURI(),
						intent.getType().toString(), intent.getValue(), intent.getNature().toString()));
		this.isop.doConnection(intent.getComputerURI() + IntentSubmissionInboundPort.SUFFIX,
				IntentSubmissionConnector.class.getCanonicalName());
		this.isop.submitIntent(intent);
		this.isop.doDisconnection();
	}

	@Override
	public void acceptIntentNotification(IntentI previousIntent, IntentI intent) throws Exception{
		switch(intent.getNature()) {
		case CORE :
			if(intent.getType() == Type.PLUS)
				for(int i = 0; i < intent.getValue(); i++) addCoreToLessEfficientAVM(intent.getComputerURI(), intent.getAvmURI());
			if(intent.getType() == Type.MINUS)
				for(int i = 0; i < intent.getValue(); i++) removeCoreFromAVM(intent.getComputerURI(), intent.getAvmURI());
			break;
		case FREQUENCY :
			updateCoreFrequency(intent.getComputerURI(), intent.getCore(), intent.getValue());
			break;
		default :
			System.err.println(String.format("RDAdapter<%s> : Unexpected Nature", this.riURI));
		}
	}

}

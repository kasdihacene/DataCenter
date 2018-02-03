package fr.upmc.datacenter.software.step3.smallscalecoordination.coordinator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.dataprovider.connectors.DataProviderConnector;
import fr.upmc.datacenter.dataprovider.interfaces.DataProviderI;
import fr.upmc.datacenter.dataprovider.ports.DataProviderOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.informations.computers.ComputerInfo;
import fr.upmc.datacenter.software.step3.smallscalecoordination.connectors.IntentNotificationConnector;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI.Nature;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentI.Type;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentNotificationI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentSubmissionHandlerI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.interfaces.IntentSubmissionI;
import fr.upmc.datacenter.software.step3.smallscalecoordination.ports.IntentNotificationOutboundPort;
import fr.upmc.datacenter.software.step3.smallscalecoordination.ports.IntentSubmissionInboundPort;

public class ComputerCoordinator extends AbstractComponent implements IntentSubmissionHandlerI, IntentNotificationI {

	/**
	 * CORE_POLITIC is a static variable to choose which politic is used for core
	 * intents conflict Core intents conflict occurs when there are too much cores
	 * are asked for the computer capacity
	 */
	private int CORE_POLITIC = 0;
	/**
	 * FREQUENCY is a static variable to choose which politic is used for frequency
	 * intents conflict Frequency intents conflict occurs when at least two
	 * different frequencies are asked for on core
	 */
	private int FREQUENCY_POLITIC = 0;
	/**
	 * canCoordinate is a boolean to know if coordinate future must be created or
	 * not
	 */
	private boolean canCoordinate = true;

	public static String SUFFIX = "-cc";

	private String computerURI;
	private IntentSubmissionInboundPort isip;
	private IntentNotificationOutboundPort inop;
	private DataProviderOutboundPort dpop;

	private long intervalCoordinate;
	private List<IntentI> intents = new ArrayList<>();
	private ScheduledFuture<?> coordinateFuture = null;

	public ComputerCoordinator(String computerURI, long intervalCoordinate) throws Exception {
		super(1, 1);

		assert computerURI != null && computerURI.length() > 0;
		assert intervalCoordinate >= 0;

		this.computerURI = computerURI;
		this.intervalCoordinate = intervalCoordinate;

		this.addOfferedInterface(IntentSubmissionI.class);
		this.isip = new IntentSubmissionInboundPort(computerURI + IntentSubmissionInboundPort.SUFFIX, this);
		this.addPort(this.isip);
		this.isip.publishPort();

		this.addRequiredInterface(IntentNotificationI.class);
		this.inop = new IntentNotificationOutboundPort(computerURI + IntentNotificationOutboundPort.SUFFIX, this);
		this.addPort(this.inop);
		this.inop.publishPort();

		this.addRequiredInterface(DataProviderI.class);
		this.dpop = new DataProviderOutboundPort(computerURI + SUFFIX + "-dpop", this);
		this.addPort(this.dpop);
		this.dpop.publishPort();
	}

	/**
	 * Connect <code>DataProviderOutboundPort</code> with the
	 * <code>DataProvider</code> component
	 * 
	 * @param providerURI
	 * @throws Exception
	 */
	public void connectWithProvider(String providerURI) throws Exception {
		this.dpop.doConnection(providerURI + "_DPIP", DataProviderConnector.class.getCanonicalName());
	}

	/**
	 * Accept <code>IntentI</code> from
	 * <code>AdapterRequestDispatcherCoordinable</code> If none coordination is
	 * scheduled, schedule one for intervalCoordinate ms later
	 * 
	 * @param intent
	 * @throws Exception
	 */
	@Override
	public void acceptIntent(IntentI intent) throws Exception {
		System.err.println(String.format("ComputerCoordinator<%s : %s> : RECEIVED AN INTENT from %s to %s %d %s",
				this.computerURI + SUFFIX, intent.getComputerURI(), intent.getAppURI(), intent.getType().toString(),
				intent.getValue(), intent.getNature().toString()));
		computerURI = intent.getComputerURI();
		intents.add(intent);

		if (intents.size() > 0 && canCoordinate) {
			canCoordinate = false;
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			System.out.println(
					String.format("ComputerCoordinator<%s> : COORDINATION SCHEDULED for %s (if not accelerated)",
							this.computerURI + SUFFIX,
							dateFormat.format(new Date(System.currentTimeMillis() + intervalCoordinate))));
			this.scheduleTask(new ComponentI.ComponentTask() {
				@Override
				public void run() {
					try {
						coordinate();
					} catch (Exception e) {
						System.err.println(e);
					}
				}
			}, TimeManagement.acceleratedDelay(this.intervalCoordinate), TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Coordinate all intents. First, we split intents by nature and we coordinate
	 * each nature intents.
	 * 
	 * @throws Exception
	 */
	private void coordinate() throws Exception {
		System.out.println(String.format("ComputerCoordinator<%s> : COORDINATING", this.computerURI + SUFFIX));

		List<IntentI> mIntents = this.intents;
		intents = new ArrayList<>();
		canCoordinate = true;
		Map<String, List<IntentI>> appIntents = new HashMap<>();
		Map<AllocatedCore, List<IntentI>> coreIntents = new HashMap<>();

		for (IntentI intent : mIntents) {
			switch (intent.getNature()) {
			case FREQUENCY:
				List<IntentI> currentCoreIntents = coreIntents.get(intent.getCore());
				if (currentCoreIntents == null) {
					currentCoreIntents = new ArrayList<>();
					currentCoreIntents.add(intent);
					coreIntents.put(intent.getCore(), currentCoreIntents);
				} else {
					currentCoreIntents.add(intent);
				}
				break;

			case CORE:
				List<IntentI> currentAppIntents = appIntents.get(intent.getAppURI());
				if (currentAppIntents == null) {
					currentAppIntents = new ArrayList<>();
					currentAppIntents.add(intent);
					appIntents.put(intent.getAppURI(), currentAppIntents);
				} else {
					currentAppIntents.add(intent);
				}
				break;
			default:
				if (intent.getNature() == null)
					System.err.println(String.format("ComputerCoordinator<%s> : Received intent from %s without nature",
							this.computerURI + SUFFIX, intent.getAppURI()));
				else
					System.err.println(String.format("ComputerCoordinator<%s> : Received unexpected %s nature from %s",
							this.computerURI + SUFFIX, intent.getNature().toString(), intent.getAppURI()));
			}
		}

		coordinateFrequencyIntents(coreIntents);
		coordinateCoreIntents(appIntents);
	}

	/**
	 * Return the remain number of idle core for the computer
	 * 
	 * @return
	 * @throws Exception
	 */
	private int getRemainCoresNumber() throws Exception {
		int res = 0;

		ComputerInfo computerInfo = this.dpop.getComputerInfos(computerURI);
		boolean[][] processorsCoresState = computerInfo.getCoreState();
		for (boolean[] processor : processorsCoresState) {
			for (boolean core : processor) {
				if (!core)
					res++;
			}
		}
		return res;
	}

	/**
	 * Coordinate all core intents. First, we merge all core intents by app. Using
	 * <code>DataProvider</code>, we check if a conflict will occur. Second, if it
	 * occurs, we manage the conflict following to the politic choosed
	 * 
	 * @param appIntents
	 *            : map for each app all core intents
	 * @throws Exception
	 */
	private void coordinateCoreIntents(Map<String, List<IntentI>> appIntents) throws Exception {
		System.out.println(String.format("ComputerCoordinator<%s> : try to coordinate %d core nature intent",
				this.computerURI + SUFFIX, appIntents.size()));

		int remainCoresNumber = getRemainCoresNumber();
		int coresNumber = getRemainCoresNumber();
		Map<String, IntentI> previousIntents = new HashMap<>();
		Map<String, IntentI> newIntents = new HashMap<>();

		for (String app : appIntents.keySet()) {
			int appCores = 0;
			List<IntentI> intents = appIntents.get(app);
			IntentI lastIntent = null;
			for (IntentI intent : intents) {
				lastIntent = intent;
				if (intent.getType() == Type.PLUS)
					appCores += intent.getValue();
				if (intent.getType() == Type.MINUS)
					appCores -= intent.getValue();
			}

			if (appCores < 0) {
				IntentI newIntent = new Intent(Nature.CORE, Type.MINUS, Math.abs(appCores), lastIntent.getAppURI(),
						lastIntent.getComputerURI(), lastIntent.getAvmURI(), lastIntent.getIntentNotificationURI());
				submitIntentNotification(lastIntent, newIntent);
				coresNumber += Math.abs(appCores);
			}
			if (appCores > 0) {
				IntentI newIntent = new Intent(Nature.CORE, Type.PLUS, Math.abs(appCores), lastIntent.getAppURI(),
						lastIntent.getComputerURI(), lastIntent.getAvmURI(), lastIntent.getIntentNotificationURI());
				previousIntents.put(lastIntent.getAppURI(), lastIntent);
				newIntents.put(lastIntent.getAppURI(), newIntent);
				coresNumber -= appCores;
			}
		}

		if (coresNumber >= 0) {
			for (String app : newIntents.keySet())
				submitIntentNotification(previousIntents.get(app), newIntents.get(app));
		} else {
			arbitrateCoreConflict(remainCoresNumber, previousIntents, newIntents);
		}
	}

	/**
	 * Manage core intents conflict following the core politic choosed; Default
	 * politic is fifo, first app to ask for core will be first to get core Another
	 * politic is to split equally the remain cores number for each app
	 * 
	 * @param remainCoresNumber
	 *            : remain cores number of the computer
	 * @param previousIntents
	 *            : previous intent submitted
	 * @param newIntents
	 *            : new intent to manage
	 * @throws Exception
	 */
	private void arbitrateCoreConflict(int remainCoresNumber, Map<String, IntentI> previousIntents,
			Map<String, IntentI> newIntents) throws Exception {
		int remainNumber = remainCoresNumber;
		switch (CORE_POLITIC) {
		case 1:
			int coresNumber = (int) Math.round((1.0 * remainCoresNumber) / newIntents.size());
			for (String app : newIntents.keySet()) {
				if (remainNumber > 0) {
					if (coresNumber > remainNumber) {
						submitIntentNotification(previousIntents.get(app), newIntents.get(app));
						remainNumber -= coresNumber;
					} else {
						newIntents.get(app).setValue(remainNumber);
						submitIntentNotification(previousIntents.get(app), newIntents.get(app));
						remainNumber = 0;
					}
				}
			}
			break;
		default:
			for (String app : newIntents.keySet()) {
				int currentCoreNumber = newIntents.get(app).getValue();
				if (remainNumber > 0) {
					if (currentCoreNumber > remainNumber) {
						submitIntentNotification(previousIntents.get(app), newIntents.get(app));
						remainNumber -= currentCoreNumber;
					} else {
						newIntents.get(app).setValue(remainNumber);
						submitIntentNotification(previousIntents.get(app), newIntents.get(app));
						remainNumber = 0;
					}
				}
			}
		}
	}

	/**
	 * Search between all admissibles frequencies ot the <code>AllocatedCore</code>
	 * and return the closest possibility for the goal frequency
	 * 
	 * @param core
	 *            : Core which we intent to change the frequency
	 * @param frequency
	 *            : Goal frequency
	 * @return
	 * @throws Exception
	 */
	private int closestAvailableFrequency(AllocatedCore core, int frequency) throws Exception {
		ComputerInfo computerInfo = this.dpop.getComputerInfos(computerURI);
		Set<Integer> admissibleFrequencies = computerInfo.getAdmissibleFrequencies();
		int maxFrenquecyGap = computerInfo.getMaxFrequencyGap();
		int currentFrequency = computerInfo.getCoreInfo(core).getFrequency();
		int res = 0;
		int minGap = Integer.MAX_VALUE;

		for (int admissibleFrequency : admissibleFrequencies) {
			if (Math.abs(currentFrequency - admissibleFrequency) <= maxFrenquecyGap) {
				int currentGap = Math.abs(admissibleFrequency - frequency);
				if (currentGap < minGap) {
					minGap = currentGap;
					res = admissibleFrequency;
				}
			}
		}
		return res;
	}

	/**
	 * Coordinate all frequency intents. First, try to merge intents for each core.
	 * When two differents frequencies are asked for the same core, a conflict
	 * occurs. Second, when conflict occurs, we manage the conflict following to the
	 * politic choosed.
	 * 
	 * @param coreIntents
	 * @throws Exception
	 */
	private void coordinateFrequencyIntents(Map<AllocatedCore, List<IntentI>> coreIntents) throws Exception {
		System.out.println(String.format("ComputerCoordinator<%s> : try to coordinate %d frequency nature intent",
				this.computerURI + SUFFIX, coreIntents.size()));

		Map<AllocatedCore, List<IntentI>> conflictCoreIntents = new HashMap<>();
		for (AllocatedCore core : coreIntents.keySet()) {
			List<IntentI> intents = coreIntents.get(core);
			if (intents.size() == 1) {
				submitIntentNotification(intents.get(0), intents.get(0));
			} else {
				IntentI lastIntent = null;
				int goalFrequency = intents.get(0).getValue();
				boolean notInterrupted = true;

				for (int i = 1; i < intents.size() && notInterrupted; i++)
					notInterrupted = goalFrequency == intents.get(i).getValue();
				if (notInterrupted)
					submitIntentNotification(lastIntent, lastIntent);
				else
					conflictCoreIntents.put(core, intents);
			}
		}

		if (conflictCoreIntents.size() > 0)
			arbitrateFrequencyConflict(conflictCoreIntents);
	}

	/**
	 * Manage frequency conflict following the frequency politic choosed. Default
	 * politic is to always choose the max frequency. Another politic is to always
	 * choose the lowest frequency. The politic we choose is to process the average
	 * between all frequencies and find the closest admissible frequency of the
	 * core.
	 * 
	 * @param previousNewIntents
	 *            : map one of the previous intents in conflict to the new goal
	 *            frequency
	 */
	private void arbitrateFrequencyConflict(Map<AllocatedCore, List<IntentI>> conflictCoreIntents) throws Exception {
		switch (FREQUENCY_POLITIC) {
		case 1:
			for (AllocatedCore core : conflictCoreIntents.keySet()) {
				List<IntentI> intents = conflictCoreIntents.get(core);

				int minFrequency = Integer.MAX_VALUE;
				for (IntentI intent : intents)
					if (intent.getValue() < minFrequency)
						minFrequency = intent.getValue();

				IntentI previousIntent = intents.get(0);
				submitIntentNotification(previousIntent,
						new Intent(previousIntent.getNature(), previousIntent.getType(), minFrequency,
								previousIntent.getAppURI(), previousIntent.getComputerURI(), previousIntent.getCore(),
								previousIntent.getIntentNotificationURI()));
			}
			break;
		case 2:
			for (AllocatedCore core : conflictCoreIntents.keySet()) {
				List<IntentI> intents = conflictCoreIntents.get(core);
				int frequencySum = 0;

				for (IntentI intent : intents)
					frequencySum += intent.getValue();
				
				IntentI previousIntent = intents.get(0);
				int arbitratedFrequency = closestAvailableFrequency(core, frequencySum / intents.size());
				submitIntentNotification(previousIntent,
						new Intent(previousIntent.getNature(), previousIntent.getType(), arbitratedFrequency,
								previousIntent.getAppURI(), previousIntent.getComputerURI(), previousIntent.getCore(),
								previousIntent.getIntentNotificationURI()));
			}
			break;
		default:
			for (AllocatedCore core : conflictCoreIntents.keySet()) {
				List<IntentI> intents = conflictCoreIntents.get(core);

				int maxFrequency = Integer.MIN_VALUE;
				for (IntentI intent : intents)
					if (intent.getValue() > maxFrequency)
						maxFrequency = intent.getValue();

				IntentI previousIntent = intents.get(0);
				submitIntentNotification(previousIntent,
						new Intent(previousIntent.getNature(), previousIntent.getType(), maxFrequency,
								previousIntent.getAppURI(), previousIntent.getComputerURI(), previousIntent.getCore(),
								previousIntent.getIntentNotificationURI()));
			}
		}
	}

	/**
	 * Notify to the intent sender that his previous intent was processed and what
	 * he have to do
	 * 
	 * @param previousIntent
	 *            : previous intent submitted
	 * @param intent
	 *            : new intent to be executed by the IntentNotificationHandler
	 * @throws Exception
	 */
	@Override
	public void submitIntentNotification(IntentI previousIntent, IntentI intent) throws Exception {
		System.err.println("######################## HERE ########################");
		System.err.println(String.format("ComputerCoordinator<%s : %s> : SEND A RESPONSE TO %s to %s %d %s",
				this.computerURI + SUFFIX, intent.getComputerURI(), intent.getAppURI(), intent.getType().toString(),
				intent.getValue(), intent.getNature().toString()));
		this.inop.doConnection(intent.getIntentNotificationURI(), IntentNotificationConnector.class.getCanonicalName());
		this.inop.submitIntentNotification(previousIntent, intent);
		this.inop.doDisconnection();
	}

}

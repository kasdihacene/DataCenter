package fr.upmc.datacenter.software.step3.largescalecoordination.implementation;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.ComponentI;
import fr.upmc.datacenter.TimeManagement;
import fr.upmc.datacenter.software.informations.applicationvm.ApplicationVMInfo;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;
import fr.upmc.datacenter.software.step2.adaptableproperty.connector.AdapterVMConnector;
import fr.upmc.datacenter.software.step2.adapter.AdapterRequestDispatcher;
import fr.upmc.datacenter.software.step2.requestresourcevm.RequestVM;
import fr.upmc.datacenter.software.step2.requestresourcevm.connector.RequestResourceVMConnector;
import fr.upmc.datacenter.software.step2.sensor.DataPushDispatcherI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.ApplicationVMcoordinate;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.applicationvmadaptable.connectors.ConnectCoordinateAVMConnector;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.connectors.CoordinationLargeScaleConnector;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.CoordinationLargeScaleI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleInboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleOutboundPort;

/**
 * <h2>Coordinator</h2> represents an Adapter
 * {@link fr.upmc.datacenter.software.step2.adapter.AdapterRequestDispatcher}
 * and coordinator who interact with other coordinators and submit decisions to
 * the <code>AdmissionController</code>
 * 
 * @author Hacene KASDI
 * @version 21.01.2018
 *
 */
public class Coordinator extends AdapterRequestDispatcher implements CoordinationLargeScaleI {

	/**
	 * when the coordinator have intention of new adaptation ( adding new
	 * {@link ApplicationVMAdaptable}
	 */
	private boolean needCooperation = false;
	/** when the coordinator has canceled the intention of new adaptation */
	private boolean canceledCooperation = false;
	/**
	 * when we declare intention of future adaptation we put an available AVM URI in
	 * this static variable
	 */
	private static ApplicationVMInfo AVMCooperation = null;

	private int nbAvMnetwork = 0;

	/** This variable used to cooperate with other controllers */
	private boolean resourceToPutBack = false;
	private ApplicationVMInfo avmToPutBack=null;
	private ArrayList<ApplicationVMInfo> appInNet;

	private Double lastAmmountIdentified = 0.;

	/** port to receive {@link TransitTokenI} from the network */
	private CoordinationLargeScaleInboundPort coordinationLargeScaleInboundPort;
	/** port to send to other autonomic controller a {@link TransitTokenI} */
	private CoordinationLargeScaleOutboundPort coordinationLargeScaleOutboundPort;

	public Coordinator(String riURI, String applicationURI) throws Exception {
		super(riURI, applicationURI);

		appInNet = new ArrayList<>();

		/** interfaces used to exchange information through the topology */
		this.addOfferedInterface(CoordinationLargeScaleI.class);
		this.addRequiredInterface(CoordinationLargeScaleI.class);

		/** publish the port to receive tokens */
		this.coordinationLargeScaleInboundPort = new CoordinationLargeScaleInboundPort(applicationURI + "COOR_CLSIP",
				this);
		this.addPort(coordinationLargeScaleInboundPort);
		coordinationLargeScaleInboundPort.publishPort();

		/** publish the port to send token in network */
		this.coordinationLargeScaleOutboundPort = new CoordinationLargeScaleOutboundPort(applicationURI + "COOR_CLSOP",
				this);
		this.addPort(coordinationLargeScaleOutboundPort);
		coordinationLargeScaleOutboundPort.publishPort();

	}

	@Override
	public void submitChip(TransitTokenI tokenI) throws Exception {
		// we have to connect this outbound port with the next inbound port component
		// System.err.println(getAppURI()+" IN NOTWORK RING === TOKEN FROM :
		// "+tokenI.getSender());
		nbAvMnetwork = tokenI.getListURIs().size();
		appInNet = tokenI.getListURIs();

		String nextNode = dataProviderOutboundPort.getNextNode();
		// Ignore the case where the next node is Us
		if (getAppURI().equals(nextNode))
			return;

		// check if need cooperation
		if (needCooperation && AVMCooperation == null && !tokenI.getListURIs().isEmpty()) {
			System.err.println("NEED OF COOPERATION AND COORDINATION");
			ArrayList<ApplicationVMInfo> newListAVMs = cooperationAsked(tokenI.getListURIs());

			TransitToken token = new TransitToken(getAppURI(), nextNode, newListAVMs);
			coordinationLargeScaleOutboundPort.doConnection(nextNode + "COOR_CLSIP",
					CoordinationLargeScaleConnector.class.getCanonicalName());
			this.coordinationLargeScaleOutboundPort.submitChip(token);
			this.coordinationLargeScaleOutboundPort.doDisconnection();
		} else {
			// check if is there any cooperation intention canceled
			if (canceledCooperation && AVMCooperation != null) {
				ArrayList<ApplicationVMInfo> newListAVMs = cooperationCanceled(tokenI.getListURIs());
				System.err.println("################ COOPERATION TO CANCEL ##################");

				TransitToken token = new TransitToken(getAppURI(), nextNode, newListAVMs);
				coordinationLargeScaleOutboundPort.doConnection(nextNode + "COOR_CLSIP",
						CoordinationLargeScaleConnector.class.getCanonicalName());
				this.coordinationLargeScaleOutboundPort.submitChip(token);
				this.coordinationLargeScaleOutboundPort.doDisconnection();
			} else {

				// Here we check if there are resources to put back to the network
				TransitToken token;
				if (resourceToPutBack) {
					System.err.println("######################################################  ====> resourceToPutBack "
									+ avmToPutBack);
					// put back the resource information to the Data Provider
					dataProviderOutboundPort.addApplicationVM(avmToPutBack);
					ArrayList<ApplicationVMInfo> newListAVMs = new ArrayList<>(tokenI.getListURIs());
					newListAVMs.add(avmToPutBack);
					avmToPutBack=null;
					resourceToPutBack = false;
					token = new TransitToken(getAppURI(), nextNode, newListAVMs);
					coordinationLargeScaleOutboundPort.doConnection(nextNode + "COOR_CLSIP",
							CoordinationLargeScaleConnector.class.getCanonicalName());

				} else {
					if(canceledCooperation && AVMCooperation == null) {
						canceledCooperation=false;
						System.err.println("######################################### NO RESOURCE FOR : "+getAppURI());
					}
					token = new TransitToken(getAppURI(), nextNode, tokenI.getListURIs());
					coordinationLargeScaleOutboundPort.doConnection(nextNode + "COOR_CLSIP",
							CoordinationLargeScaleConnector.class.getCanonicalName());
				}
				this.coordinationLargeScaleOutboundPort.submitChip(token);
				this.coordinationLargeScaleOutboundPort.doDisconnection();
			}
		}
	}

	/**
	 * When the coordinator ask for holding an AVM because of a future intention of
	 * adaptation.
	 * 
	 * @param applicationVMInfos
	 * @return new Array list of ApplicationVm informations after extracting an AVM
	 *         uri due to intention of a future adaptation.
	 * @throws Exception
	 */
	private ArrayList<ApplicationVMInfo> cooperationAsked(ArrayList<ApplicationVMInfo> applicationVMInfos)
			throws Exception {
		ArrayList<ApplicationVMInfo> applicationVM = new ArrayList<>();
		synchronized (applicationVMInfos) {
			for (int i = 0; i < applicationVMInfos.size() - 1; i++) {
				System.err.println("##################### " + applicationVMInfos.get(i).getVmURI());
				applicationVM.add(applicationVMInfos.get(i));
			}
		}
		AVMCooperation = dataProviderOutboundPort.removeApplicationVM();
		System.err.println("++++++++++++++++++++ COOPERATION ASKED : " + AVMCooperation.getVmURI());
		return applicationVM;
	}

	/**
	 * When the intention of adaptation is finally canceled by the Coordinator. we
	 * have to put back the AVM not used to list of AVM which transit in the
	 * network.
	 * 
	 * @param applicationVMInfos
	 * @return new Array list of ApplicationVm informations after putting back the
	 *         AVM.
	 * @throws Exception
	 */
	private ArrayList<ApplicationVMInfo> cooperationCanceled(ArrayList<ApplicationVMInfo> applicationVMs)
			throws Exception {
		System.out.println("îîîîîîîîîîîîîîîîîîîîîîîîîîîîîîîîîîî " + applicationVMs.size());
		applicationVMs.add(AVMCooperation);
		dataProviderOutboundPort.addApplicationVM(AVMCooperation);
		canceledCooperation = false;
		AVMCooperation = null;
		System.out.println("îîîîîîîîîîîîîîîîîîîîîîîîîîîîîîîîîîî " + applicationVMs.size());
		return applicationVMs;
	}

	/**
	 * @see {@link AdapterRequestDispatcher#receivePushedData(DataPushDispatcherI)}
	 */
	public void receivePushedData(DataPushDispatcherI dataPushDispatcherI) throws Exception {
		requestResponsesInfo = dataPushDispatcherI.getListStatsAVMs();
		rollingAverage.add(dataPushDispatcherI.getExecutionAverage());

		if (rollingAverage.size() > ControllerSetting.NBCONSECUTIVEAVERAGE) {
			rollingAverage.removeFirst();
		}

	}

	/**
	 * @see {@link AdapterRequestDispatcher#launchAdaptionEveryInterval()}
	 */
	public void launchAdaptionEveryInterval() throws Exception {
		System.out.println("AVM created in DP : " + dataProviderOutboundPort.getNBAVMcreated()
				+ " ===================================== AVM in network " + nbAvMnetwork);

		for (ApplicationVMInfo app : appInNet) {
			System.err.println(app.getVmURI());
		}
		final AdapterRequestDispatcher AdapterRequestDispatcher = this;
		this.pushingFuture = this.scheduleTask(new ComponentI.ComponentTask() {
			@Override
			public void run() {
				try {
					AdapterRequestDispatcher.launchAdaption();
					launchAdaptionEveryInterval();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, TimeManagement.acceleratedDelay(ControllerSetting.interavalAdaption), TimeUnit.MILLISECONDS);
	}

	/**
	 * @see {@link AdapterRequestDispatcher#launchAdaption()}
	 */
	public void launchAdaption() throws Exception {

		// We start adaption only if we have 10 samples of averages on our list
		if (rollingAverage.size() == ControllerSetting.NBCONSECUTIVEAVERAGE) {
			// Get the current rolling average that is collected
			Double CurrentRollingAverage = getRollingAverage();
			System.err.println(getAppURI() + " | AVM less efficient : " + getAVMlessEfficient()
					+ " | EXECUTION AVERAGE : " + CurrentRollingAverage);

			synchronized (CurrentRollingAverage) {
				if (!CurrentRollingAverage.isNaN()) {

					// If rolling-average between 2000 and 2500 hold and predict need of new
					// adaptation
					if (CurrentRollingAverage > (avmAverageThreshold - 500)
							&& CurrentRollingAverage < (avmAverageThreshold)) {
						/**
						 * set static variable <needCooperation> to true when the method submitChip will
						 * be invoked he will check this variable than it will set the static variable
						 * <AVMCooperation> to an available AVM URI
						 */
						needCooperation = true;
						lastAmmountIdentified = CurrentRollingAverage;
					} else {
						if (needCooperation && lastAmmountIdentified > CurrentRollingAverage) {
							System.err.println("------------------------------- cooperation canceled *******");
							canceledCooperation = true;
							lastAmmountIdentified = 0.;
						}
					}

					// If rolling-average > (2500 + 500)
					if (CurrentRollingAverage > avmAverageThreshold) {

						if (CurrentRollingAverage >= lastAverageIdentified) {
							// In case of the average didn't decreased we have
							// to start a new adaption by adding a new AVM
							lastAverageIdentified = CurrentRollingAverage;
							System.err.println("============ ADD AVM FOR : " + getAppURI());

							if (AVMCooperation != null) {
								// Reset calculation
								rollingAverage.clear();

								System.err.println("COOPERATION SUCCEEDED : AVM ADDED == " + AVMCooperation.getVmURI());
								allocateAVM();

							} else {
								System.err.println(
										"SEND AN INTENTION OF COOPERATION [current need == " + needCooperation+"   "+AVMCooperation);
								needCooperation = true;
							}

						} else {
							if (CurrentRollingAverage < lastAverageIdentified) {
								if (needCooperation) {
									System.err.println(
											"=================================================== cooperation to cancel");
									needCooperation = false;
									canceledCooperation = true;
								} else {
									RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort.getApplicationInfos(getAppURI());
									// Allow release only when number AVM used by this Dispatcher > 2
									if (dispatcherInfo.getNbVMCreated() > 2) {
										lastAverageIdentified = CurrentRollingAverage;
										System.err.println("============ REMOVE AVM FROM : " + getAppURI());
										releaseAVM();
										rollingAverage.clear();
									}

								}
							}
						}

					}

				}
			}
		}
	}

	public ApplicationVMInfo delelteAVMinfos(String avmURI) throws Exception {
		ApplicationVMInfo applicationVMInfoReturned = null;
		RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort.getApplicationInfos(getAppURI());
		// update the state cores in the Computer Informations
		applicationVMInfoReturned = dispatcherInfo.getApplicationVMInformation(avmURI);
		System.err.println("#################################### AVM created before delete== " + dispatcherInfo.getNbVMCreated());
		
		dispatcherInfo.removeApplicationVM(avmURI);
		System.err.println("#################################### AVM created after delete== " + dispatcherInfo.getNbVMCreated());
		return applicationVMInfoReturned;
	}
	// ==================================================================================
	// Allocate Application VM coordinate
	// ==================================================================================

	/**
	 * 
	 * @param avmURI
	 *            URI of an {@link ApplicationVMcoordinate} located as available
	 * 
	 * @throws Exception
	 */
	protected void allocateAVM() throws Exception {

		// Ask for connecting the RequestDispatcher with the AVM for receiving
		// Notifications
		connectCoordinateAVMOutboundPort.doConnection(AVMCooperation.getVmURI() + "_CCIP",
				ConnectCoordinateAVMConnector.class.getCanonicalName());
		connectCoordinateAVMOutboundPort.connectAVMwithSubmissioner(getAppURI() + "RD");

		// Add AVM URI on the list of AVMs of the RequestDispatcher
		requestResourceVMOutboundPort.doConnection(getAppURI() + "RD_RVMIP",
				RequestResourceVMConnector.class.getCanonicalName());
		RequestVM requestVMI = new RequestVM(AVMCooperation.getVmURI(), getAppURI());
		requestResourceVMOutboundPort.requestAddVM(requestVMI);
		requestResourceVMOutboundPort.doDisconnection();

		RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort.getApplicationInfos(getAppURI());
		System.err.println("########## " + AVMCooperation.getVmURI());
		System.err.println("########## " + AVMCooperation.getComputerURI());
		System.err.println("########## " + AVMCooperation.getAllCoresCoordiantion().length);
		System.err.println("########## " + dispatcherInfo.getNbVMCreated());

		synchronized (dispatcherInfo) {
			dispatcherInfo.addApplicationVM(AVMCooperation.getVmURI(), AVMCooperation.getComputerURI(),
					AVMCooperation.getAllCoresCoordiantion());
		}
		System.err.println("########## " + dispatcherInfo.getNbVMCreated());

		needCooperation = false;
		AVMCooperation = null;
	}

	// ==================================================================================
	// Deallocate Application VM resources
	// ==================================================================================
	/**
	 * This method allows removing AVM from the DataCenter especially
	 * 
	 * @throws Exception
	 */
	public void releaseAVM() throws Exception {

		// Get the AVM less efficient
		String avmURI = getAVMlessEfficient();

		// if the avm is in use we can remove the
		// information about this AVM in the RequestDispatherInformation
		System.err.println("====== AVM TO DELETE == " + avmURI);

		// Remove this URI from List AVM URIs used by the current RequestDispatcher
		requestResourceVMOutboundPort.doConnection(getAppURI() + "RD_RVMIP",
				RequestResourceVMConnector.class.getCanonicalName());
		RequestVM requestVMI = new RequestVM(avmURI, getAppURI());
		requestResourceVMOutboundPort.requestRemoveVM(requestVMI);

		// Stop receiving requests on this AVM
		avmiop.doConnection(avmURI + "_AVMIP", AdapterVMConnector.class.getCanonicalName());
		sizeQueue = avmiop.sizeTaskQueue();
		avmiop.doDisconnection();
		synchronized (sizeQueue) {
			if (sizeQueue > 0) {
				System.err.println(" ************** WAIT ********");
				seekForStateTaskAVM(avmURI);
			} else {
				removeWhenEnds(avmURI);
			}
		}
	}

	/**
	 * After waiting the termination of the requests in the AVM Queue we can proceed
	 * to remove the the Informations about the AVM and to deallocate cores used by
	 * the AVM
	 * 
	 * @param avmURI
	 * @throws Exception
	 */
	private void removeWhenEnds(String avmURI) throws Exception {
		// After waiting termination of the execution of all requests in the Queue

		// remove the ApplicationVMInfo on the list of AVM in use in this
		// coordinator and put back the AVM URI to the network.
		ApplicationVMInfo appVM = delelteAVMinfos(avmURI);

		if (appVM != null) {
			avmToPutBack=appVM;
			resourceToPutBack = true;

			// Stop receiving notifications
			connectCoordinateAVMOutboundPort.doConnection(avmURI + "_CCIP",
					ConnectCoordinateAVMConnector.class.getCanonicalName());
			connectCoordinateAVMOutboundPort.disconnectAVMFromSubmissioner();

			// Remove AVM from statistics Sensor (Request Dispatcher component )
			requestResourceVMOutboundPort.doConnection(getAppURI() + "RD_RVMIP",
					RequestResourceVMConnector.class.getCanonicalName());
			RequestVM requestVMI = new RequestVM(avmURI, getAppURI());
			requestResourceVMOutboundPort.requestRemoveAVMEnded(requestVMI);
			requestResourceVMOutboundPort.doDisconnection();

			System.err.println("============================================ AVM REMOVED " + avmURI + "    "
					+ appVM.getVmURI() + "    " + appVM.getComputerURI());
		} else {
			System.err.println("============================================ RELEASING FAILED");
		}
	}

	/**
	 * 
	 * @param avmURI
	 * @return the number of remaining tasks in the ApplicationVM
	 * @throws Exception
	 */
	private Integer getsizeTasks(String avmURI) throws Exception {
		avmiop.doConnection(avmURI + "_AVMIP", AdapterVMConnector.class.getCanonicalName());
		synchronized (sizeQueue) {
			sizeQueue = avmiop.sizeTaskQueue();
			if (sizeQueue == 0) {
				System.err.println(" getsizeTasks FOR == " + avmURI + "   NOTIFY");
				removeWhenEnds(avmURI);
			} else {
				System.err.println(" getsizeTasks FOR == " + avmURI + "   NOT YET");
			}

		}
		avmiop.doDisconnection();
		return sizeQueue;
	}

	/**
	 * Periodic task used to check the state of the Queue then at the end we can
	 * remove the Cores used by the <code>ApplicationVM</code>
	 * 
	 * @param avmURI
	 * @throws Exception
	 */
	protected void seekForStateTaskAVM(String avmURI) throws Exception {
		final Coordinator coordinator = this;
		this.pushingFutureTasks = this.scheduleTask(new ComponentI.ComponentTask() {
			@Override
			public void run() {
				try {
					Integer size = coordinator.getsizeTasks(avmURI);
					if (size > 0)
						coordinator.seekForStateTaskAVM(avmURI);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, TimeManagement.acceleratedDelay(2000), TimeUnit.MILLISECONDS);
	}

	/**
	 * The first in will be the leader and he could initialize the first send of the
	 * token on the network and he still the leader until he quit the topology.
	 * 
	 * @throws Exception
	 */
	public void tryToSubmitToken() throws Exception {
		ArrayList<ApplicationVMInfo> applicationsVM = dataProviderOutboundPort.getCoordinateAVMs();
		String leader = dataProviderOutboundPort.whoIsNetworkLeader();
		if (leader.equals(getAppURI())) {

			String nextNode = dataProviderOutboundPort.getNextNode();

			// Ignore the case where the next node is Us
			if (getAppURI().equals(nextNode))
				return;
			TransitToken token = new TransitToken(getAppURI(), nextNode, applicationsVM);

			coordinationLargeScaleOutboundPort.doConnection(nextNode + "COOR_CLSIP",
					CoordinationLargeScaleConnector.class.getCanonicalName());
			this.coordinationLargeScaleOutboundPort.submitChip(token);
			this.coordinationLargeScaleOutboundPort.doDisconnection();
		}
	}
}
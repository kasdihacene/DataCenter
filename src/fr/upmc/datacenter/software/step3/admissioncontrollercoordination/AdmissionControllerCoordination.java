package fr.upmc.datacenter.software.step3.admissioncontrollercoordination;

import java.util.LinkedList;

import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherComponent;
import fr.upmc.datacenter.software.informations.requestdispatcher.RequestDispatcherInfo;
import fr.upmc.datacenter.software.step2.AdmissionController;
import fr.upmc.datacenter.software.step2.adaptableproperty.ApplicationVMAdaptable;
import fr.upmc.datacenter.software.step2.adapter.AdapterRequestDispatcher;
import fr.upmc.datacenter.software.step2.requestresourcevm.RequestVM;
import fr.upmc.datacenter.software.step2.requestresourcevm.connector.RequestResourceVMConnector;
import fr.upmc.datacenter.software.step2.tools.DelployTools;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.CoordinationLargeScaleI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.interfaces.TransitTokenI;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleInboundPort;
import fr.upmc.datacenter.software.step3.largescalecoordination.implementation.ports.CoordinationLargeScaleOutboundPort;
import fr.upmc.datacenter.software.step3.smallscalecoordination.coordinable.AdapterRequestDispatcherCoordinable;

public class AdmissionControllerCoordination extends AdmissionController implements CoordinationLargeScaleI {

	/** port to receive {@link TransitTokenI} from the network */
	private CoordinationLargeScaleInboundPort coordinationLargeScaleInboundPort;

	/** port to send to other autonomic controller a {@link TransitTokenI} */
	private CoordinationLargeScaleOutboundPort coordinationLargeScaleOutboundPort;

	private static Integer AVMCREATOR = 0;

	public AdmissionControllerCoordination(String acURI, AbstractCVM acvm) throws Exception {
		super(acURI, acvm);

		/** publish the port to receive tokens */
		this.coordinationLargeScaleInboundPort = new CoordinationLargeScaleInboundPort(acURI + "_COOR_CLSIP", this);
		this.addPort(coordinationLargeScaleInboundPort);
		coordinationLargeScaleInboundPort.publishPort();

		// 1-
		// Set an offered interface to receive the request of creation of the AVMs
		// we have to implement an Interface which offers possibilities of AVM creation
	}

	public void inspectResources(AdmissionI admission) throws Exception {
		System.out.println("REQUEST RECEIVED BY AdmissionControllerCoordination " + admission.getApplicationURI());

	}

	protected void createAVMsAndDeploy(AdmissionI admissionI) {
		synchronized (AVMCREATOR) {
			if (AVMCREATOR == 0) {
				for (int i = 0; i < 4; i++) {
					System.err.println(" TAKEN BY : " + admissionI.getApplicationURI());
				}
				AVMCREATOR = AVMCREATOR + 1;
			}
		}
	}

	@Override
	public void submitChip(TransitTokenI tokenI) throws Exception {

	}

	// 2- We have to create an sufficient number of ApplicationVM
	// and deploy it
	// 3- We have to add every AVM created to the DataProvider
	// 4- be careful about the inbound port of the AVMs we have to make link with
	// the DataProvider

	@Override
	protected void allowHostingApplication(AdmissionI admissionI, LinkedList<String> computerURIs) throws Exception {
		// Create the RequestDispatcher
		RequestDispatcherComponent RD = createRequestDispatcher(admissionI);

		for (String computerURI : computerURIs) {

			// Create an ApplicationVM
			ApplicationVMAdaptable avm = createApplicationVM(admissionI.getApplicationURI(), computerURI);

			// Get the ApplicationVM URI
			RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort
					.getApplicationInfos(admissionI.getApplicationURI());
			String avmURIrecentlyAdded = dispatcherInfo.getAVMRecentlyAdded().getVmURI();

			// Connect the AVM to Request Dispatcher for sending Notifications
			avm.doPortConnection(avmURIrecentlyAdded + "_RNOP", admissionI.getApplicationURI() + "RD_RNIP",
					RequestNotificationConnector.class.getCanonicalName());

			// Add AVM URI on the list of AVMs of the RequestDispatcher
			requestResourceVMOutboundPort.doConnection(admissionI.getApplicationURI() + "RD_RVMIP",
					RequestResourceVMConnector.class.getCanonicalName());
			RequestVM requestVMI = new RequestVM(avmURIrecentlyAdded, admissionI.getApplicationURI());
			requestResourceVMOutboundPort.requestAddVM(requestVMI);

			// Update AdmissionI informations
			RequestDispatcherInfo rdInfos = dataProviderOutboundPort
					.getApplicationInfos(admissionI.getApplicationURI());
			int nbCreated = rdInfos.getNbVMCreated();
			System.out.println("Nb VM created for " + admissionI.getApplicationURI() + " : " + nbCreated);

		}
		// set the RD URI on the AdmissionI response to send to the ApplicationContainer
		admissionI.setRequestSubmissionInboundPortRD(admissionI.getApplicationURI() + "RD_RSIP");

		// Create the Adapter Component and launch it
		AdapterRequestDispatcher adapterRequestDispatcher = new AdapterRequestDispatcherCoordinable(
				admissionI.getApplicationURI() + "RD", admissionI.getApplicationURI());
		DelployTools.deployComponent(adapterRequestDispatcher);

		// connect to DataProvider to get available resources
		adapterRequestDispatcher.connectWithDataProvider(providerURI);
		adapterRequestDispatcher.connectAdapterWithProvider(providerURI);
		// Create a SensorDispatcherOutboundPort and launch pushing
		adapterRequestDispatcher.connectWithRequestDispatcher(admissionI.getApplicationURI());
		adapterRequestDispatcher.launchAdaptionEveryInterval();
	}
}

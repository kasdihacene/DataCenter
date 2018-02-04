package fr.upmc.datacenter.software.step3.smallscalecoordination.coordinable;

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

public class AdmissionControllerCoordinable extends AdmissionController {

	public AdmissionControllerCoordinable(String acURI, AbstractCVM acvm) throws Exception {
		super(acURI, acvm);
	}

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
			requestResourceVMOutboundPort.doDisconnection();
			
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
		// adapterRequestDispatcher.connectAdapterWithProvider(providerURI);
		// Create a SensorDispatcherOutboundPort and launch pushing
		adapterRequestDispatcher.connectWithRequestDispatcher(admissionI.getApplicationURI());
		adapterRequestDispatcher.launchAdaptionEveryInterval();
	}
	
	/*
	@Override
	protected void hostApplicationAndLAunchCoordination(ArrayList<ApplicationVMInfo> applicationVMInfos,
			AdmissionI admissionI) throws Exception {
		RequestDispatcherComponent requestDispatcherComponent = createRequestDispatcher(admissionI);

		// Ask for connecting the RequestDispatcher with the AVM for receiving
		// Notifications
		for (ApplicationVMInfo applicationVMInfo : applicationVMInfos) {
			connectCoordinateAVMOutboundPort.doConnection(applicationVMInfo.getVmURI() + "_CCIP",
					ConnectCoordinateAVMConnector.class.getCanonicalName());
			connectCoordinateAVMOutboundPort
					.connectAVMwithSubmissioner(requestDispatcherComponent.getApplicationContainerURI());

			// Add AVM URI on the list of AVMs of the RequestDispatcher
			requestResourceVMOutboundPort.doConnection(admissionI.getApplicationURI() + "RD_RVMIP",
					RequestResourceVMConnector.class.getCanonicalName());
			RequestVM requestVMI = new RequestVM(applicationVMInfo.getVmURI(), admissionI.getApplicationURI());
			requestResourceVMOutboundPort.requestAddVM(requestVMI);
			requestResourceVMOutboundPort.doDisconnection();

			// Store informations about the RequestDispatcher
			RequestDispatcherInfo dispatcherInfo = dataProviderOutboundPort
					.getApplicationInfos(admissionI.getApplicationURI());
			// Add the ApplicationVM information to the RequestDispatcherInformation
			synchronized (dispatcherInfo) {

				dispatcherInfo.addApplicationVM(applicationVMInfo.getVmURI(), applicationVMInfo.getComputerURI(),
						applicationVMInfo.getAllCoresCoordiantion());
			}
			System.err
					.println("#################################### AVM created == " + dispatcherInfo.getNbVMCreated());
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
	*/
}

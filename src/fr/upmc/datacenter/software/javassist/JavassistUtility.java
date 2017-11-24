package fr.upmc.datacenter.software.javassist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.RequestDispatcher;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;


/**
 * <code>JavassistUtility</code> create a <code>RequestDipatcher</code> and 2 <code>ApplicationVM</code> and reserves 2 <code>Processor</code>
 * we generate a code of a connector with javassist
 * 
 * @author Hacene & Marc
 *
 */
public class JavassistUtility {

	/**
	 * All URIs and ports for first AVM
	 */
	// URIs
	public static final String	avmURI0 = "avm0";
	public static final String	ApplicationVMManagementInboundPortURI0 = "avm0-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI0 = "avm0-obp" ;
	public static final String	RequestSubmissionInboundPortURI0 = "rsibp0";
	public static final String	RequestNotificationOutboundPortURI0 = "rnobp0"; 
	// Ports
	protected static ApplicationVMManagementOutboundPort avmPort0 ;

	/**
	 * All URIs and ports for second AVM
	 */
	// URIs
	public static final String	avmURI1 = "avm1";
	public static final String	ApplicationVMManagementInboundPortURI1 = "avm1-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI1 = "avm1-obp" ;
	public static final String	RequestSubmissionInboundPortURI1 = "rsibp1";
	public static final String	RequestNotificationOutboundPortURI1 = "rnobp1"; 
	// Ports
	protected static ApplicationVMManagementOutboundPort avmPort1 ;
	


	protected Computer c0, c1;
	protected ComputerMonitor cm0, cm1;
	protected static ApplicationVM avm0, avm1;


		// PREDIFINED URI OF PORTS 
		public static final String RequestDispatcherManagementInboundPort = "rdmip";
		public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
		public static final String	RequestNotificationInboundPortURI = "rnibp" ;
		public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
		

		
		/** 	Request Dispatcher component.							*/
		protected RequestDispatcher							rd ;
		
		
    public JavassistUtility() {
        super();
    }

 

    /**
     * Static method to create a RequestDispatcher
     * @param admission 				: the Interface contains some information about the current state of the system and some 
     * 									  setters to update the state of the system
     * @param listComputers 			: list of Computers
     * @param indexAvailableComputer 	: the Index of the Available <code><computer</code> witch contains available resources 
     * @throws Exception
     */
    public static void createRequestDispatcher(
    		
    		AbstractCVM cvm,
    		AdmissionI admission,
    		ArrayList<Computer> listComputers,
    		int indexAvailableComputer
    		) throws Exception {

    	// --------------------------------------------------------------------
    	// Create and deploy first avm component
    	// --------------------------------------------------------------------
    	System.out.println("called");
    	avm0 = new ApplicationVM(
    						avmURI0,	// application vm component URI
    						ApplicationVMManagementInboundPortURI0,
    						RequestSubmissionInboundPortURI0,
    						RequestNotificationOutboundPortURI0) ;
    				cvm.addDeployedComponent(avm0) ;
    		
    				// Create a mock up port to manage the AVM component (allocate cores).
    				avmPort0 = new ApplicationVMManagementOutboundPort(
    						ApplicationVMManagementOutboundPortURI0,
    						new AbstractComponent(0, 0) {}) ;
    				avmPort0.publishPort() ;
    				avmPort0.doConnection(
    						ApplicationVMManagementInboundPortURI0,
    						ApplicationVMManagementConnector.class.getCanonicalName()) ;
    		
    				// Toggle on tracing and logging in the application virtual machine to
    				// follow the execution of individual requests.
    				avm0.toggleTracing() ;
    				avm0.toggleLogging() ;
    				
    				// --------------------------------------------------------------------
    				// Create and deploy second avm component
    				// --------------------------------------------------------------------
    				avm1 = new ApplicationVM(
    						avmURI1,	// application vm component URI
    						ApplicationVMManagementInboundPortURI1,
    						RequestSubmissionInboundPortURI1,
    						RequestNotificationOutboundPortURI1) ;
    				cvm.addDeployedComponent(avm1) ;
    		
    				// Create a mock up port to manage the AVM component (allocate cores).
    				avmPort1 = new ApplicationVMManagementOutboundPort(
    						ApplicationVMManagementOutboundPortURI1,
    						new AbstractComponent(0, 0) {}) ;
    				avmPort1.publishPort() ;
    				avmPort1.doConnection(
    						ApplicationVMManagementInboundPortURI1,
    						ApplicationVMManagementConnector.class.getCanonicalName()) ;
    		
    				// Toggle on tracing and logging in the application virtual machine to
    				// follow the execution of individual requests.
    				avm1.toggleTracing() ;
    				avm1.toggleLogging() ;
    	
    	System.out.println("avm created");			
    	// --------------------------------------------------------------------
    	// Creating the request Dispatcher component.
    	// --------------------------------------------------------------------
    	RequestDispatcher rd = new RequestDispatcher("RDispatcher",
    			RequestDispatcherManagementInboundPort+ admission.getApplicationURI(),
    			RequestSubmissionInboundPortURI+admission.getApplicationURI(),
    			RequestNotificationInboundPortURI+admission.getApplicationURI(),
    			RequestNotificationOutboundPortURI+admission.getApplicationURI());
    	cvm.addDeployedComponent(rd);
    	System.out.println("rd created");
    	
    	//Allocate the 4 cores of the computer to the application virtual machine
    			AllocatedCore[] ac0 = listComputers.get(indexAvailableComputer).allocateCores(2) ;
    			avmPort0.allocateCores(ac0);
    			
    			AllocatedCore[] ac1 = listComputers.get(indexAvailableComputer).allocateCores(2) ;
    			avmPort1.allocateCores(ac1);
    			
    			
    			rd.connectAVM(avmURI0, RequestSubmissionInboundPortURI0, RequestNotificationOutboundPortURI0);
    			rd.connectAVM(avmURI1, RequestSubmissionInboundPortURI1, RequestNotificationOutboundPortURI1);

    			String RSIP = RequestSubmissionInboundPortURI+admission.getApplicationURI();
    			admission.setRequestSubmissionInboundPortRD(RSIP);
    			System.out.println("RequestDispatcher created successefuly ...");
    }



    /**
     * This method creates a Class connector 
     * 
     * @param connectorCanonicalClassName
     * @param connectorSuperclass
     * @param connectorImplementedInterface
     * @param offeredInterface
     * @param methodNamesMap
     * @return a Class Connector
     * @throws Exception
     */
    public static Class<?> makeConnectorClassJavassist(String connectorCanonicalClassName,
                                                       Class<?> connectorSuperclass,
                                                       Class<?> connectorImplementedInterface,
                                                       Class<?> offeredInterface,
                                                       HashMap<String,String> methodNamesMap ) throws Exception
    {
    	System.out.println("get pool==============");
        ClassPool pool = ClassPool.getDefault() ;
        System.out.println("get connector superclass");
        CtClass cs = pool.get(connectorSuperclass.getCanonicalName()) ;
        System.out.println("get implemented interface");
        CtClass cii = pool.get(connectorImplementedInterface.getCanonicalName()) ;
        System.out.println("get offered interface");
        CtClass oi = pool.get(offeredInterface.getCanonicalName()) ;
        System.out.println("make class");
        CtClass connectorCtClass = pool.makeClass(connectorCanonicalClassName) ;

        connectorCtClass.setSuperclass(cs) ;
        Method[] methodsToImplement = connectorImplementedInterface.getDeclaredMethods() ;
        for (int i = 0 ; i < methodsToImplement.length ; i++) {
            String source = "public " ;
            source += methodsToImplement[i].getReturnType().getName() + " " ;
            source += methodsToImplement[i].getName() + "(" ; Class<?>[] pt = methodsToImplement[i].getParameterTypes() ;
            String callParam = "" ;
            for (int j = 0 ; j < pt.length ; j++) {
                String pName = "aaa" + j ;
                source += pt[j].getCanonicalName() + " " + pName ; callParam += pName ;
                if (j < pt.length - 1)
                {
                    source += ", " ;
                    callParam += ", " ;
                }
            }
            source += ")" ;
            Class<?>[] et = methodsToImplement[i].getExceptionTypes() ;
            if (et != null && et.length > 0)
            {
                source += " throws " ;


                for (int z = 0 ; z < et.length ; z++)
                {
                    source += et[z].getCanonicalName() ;
                    if (z < et.length - 1)
                    {
                        source += "," ;
                    }
                }
            }
            source += "\n{ return ((" ;
            source += offeredInterface.getCanonicalName() + ")this.offering)." ;
            source += methodNamesMap.get(methodsToImplement[i].getName()) ;
            source += "(" + callParam + ") ;\n}" ;
            CtMethod theCtMethod = CtMethod.make(source, connectorCtClass) ;
            connectorCtClass.addMethod(theCtMethod) ;
        }
        connectorCtClass.setInterfaces(new CtClass[]{cii}) ;
        cii.detach() ;
        cs.detach() ;
        if(!connectorImplementedInterface.equals(offeredInterface))
            oi.detach() ;
        Class<?> ret = connectorCtClass.toClass() ;
        connectorCtClass.detach() ;

        return ret ;
    }

}
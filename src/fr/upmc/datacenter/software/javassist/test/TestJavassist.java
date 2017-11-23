package fr.upmc.datacenter.software.javassist.test;

import java.util.ArrayList;

import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.requestDispatcher.RequestDispatcher;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class TestJavassist {

	public static void main(String[] args) throws NoSuchMethodException, NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
		
		
		
		
		
		// Ports
		
		ClassPool pool = ClassPool.getDefault() ;
		CtClass rd = pool.makeClass("fr.upmc.datacenter.software.javassist.test.JavassistTry");
		rd.addField(CtField.make("public static final String	avmURI0 = \"avm0\";", rd));
		rd.addField(CtField.make("public static final String	ApplicationVMManagementInboundPortURI0 = \"avm0-ibp\" ;", rd));
		rd.addField(CtField.make("public static final String	ApplicationVMManagementOutboundPortURI0 = \"avm0-obp\" ;", rd));
		rd.addField(CtField.make("public static final String	RequestSubmissionInboundPortURI0 = \"rsibp0\";", rd));
		rd.addField(CtField.make("public static final String	RequestNotificationOutboundPortURI0 = \"rnobp0\"; ", rd));
		rd.addField(CtField.make("protected static fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort avmPort0 ;", rd));
		
		rd.addField(CtField.make("public static final String	avmURI1 = \"avm1\";", rd));
		rd.addField(CtField.make("public static final String	ApplicationVMManagementInboundPortURI1 = \"avm1-ibp\" ;", rd));
		rd.addField(CtField.make("public static final String	ApplicationVMManagementOutboundPortURI1 = \"avm1-obp\" ;", rd));
		rd.addField(CtField.make("public static final String	RequestSubmissionInboundPortURI1 = \"rsibp1\";", rd));
		rd.addField(CtField.make("public static final String	RequestNotificationOutboundPortURI1 = \"rnobp1\"; ", rd));
		rd.addField(CtField.make("protected static fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort avmPort1 ;", rd));

		
		rd.addField(CtField.make("protected fr.upmc.datacenter.hardware.computers.Computer c0;", rd));
		rd.addField(CtField.make("protected fr.upmc.datacenter.hardware.tests.ComputerMonitor cm0;", rd));
		rd.addField(CtField.make("protected static fr.upmc.datacenter.software.applicationvm.ApplicationVM avm0;", rd));
		
		rd.addField(CtField.make("protected fr.upmc.datacenter.hardware.computers.Computer c1;", rd));
		rd.addField(CtField.make("protected fr.upmc.datacenter.hardware.tests.ComputerMonitor cm1;", rd));
		rd.addField(CtField.make("protected static fr.upmc.datacenter.software.applicationvm.ApplicationVM avm1;", rd));
		
		
		
		rd.addField(CtField.make("public static final String	RequestSubmissionInboundPortURI = \"rsibp\" ;", rd));
		rd.addField(CtField.make("public static final String	RequestNotificationInboundPortURI = \"rnibp\" ;", rd));
		rd.addField(CtField.make("public static final String	RequestNotificationOutboundPortURI = \"rnobp\" ;", rd));
		
		rd.addField(CtField.make("protected fr.upmc.datacenter.software.requestDispatcher.RequestDispatcher	rd ;", rd));
		
		CtMethod createRD1 =CtNewMethod.make("public static void createRD(){"
				+ "System.out.println(\"MAAAAAAAAAAAAARCHE !\");"
				+ "}",rd);
		rd.addMethod(createRD1);
		System.out.println("88888888888888888");
		
		CtMethod createRD =CtNewMethod.make("public static void createRD("
				+ "fr.upmc.datacenter.software.admissionController.interfaces.AdmissionI admission,"
				+ "java.util.ArrayList<fr.upmc.datacenter.hardware.computers.Computer> list,"
				+ "int indexAvailableComputer"
				+ ");", rd);
		createRD.setBody("{"
				+ "System.out.println(\"MAAAAAAAAAAAAARCHE !\");"
				+ "System.out.println($3);"
				+ "}");
		rd.addMethod(createRD);
		
		
		
		
//        CtClass rd = pool.makeClass("fr.upmc.datacenter.software.javassist.test.JavassistTry") ;
//		CtMethod methode = CtNewMethod.make("public String getPort(){"
//				+ "String port = \"hello\";\n"
//				+ "System.out.println(\"Cool\");" 
//				+ "return port;"
//				+ "}", rd);
//		rd.addMethod(methode);


		
		

				
	}

}

package fr.upmc.datacenter.software.javassist.test;

import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class TestJavassist {

	public static void main(String[] args) throws NoSuchMethodException, NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {


		HashMap<String,String>mapping = new HashMap<String,String>();
		mapping.put("submitRequest","acceptRequestSubmission");
		mapping.put("submitRequestAndNotify","acceptRequestSubmissionAndNotify");
//		Class<?> offeredInterface = RequestSubmissionI.class;
//		Class<?> connector = JavassistUtility.makeConnectorClassJavassist("fr.upmc.datacenter.javassist.connector.ApplicationRequestConnector", 
//																AbstractConnector.class, 
//																RequestSubmissionI.class, 
//																offeredInterface, 
//																mapping);
		
		ClassPool pool = ClassPool.getDefault() ;
		
        CtClass cii = pool.get("fr.upmc.datacenter.software.javassist.JavassistUtility") ;
       
        CtMethod[] methodsToImplement = cii.getDeclaredMethods() ;
        
        System.out.println("Nombre de methodes: "+methodsToImplement.length);
        
        
        CtMethod mCtMethod=null;
        for (int i = 0 ; i < methodsToImplement.length ; i++) {
         if (methodsToImplement[i].getName().equals("printThisHere")) {
			System.out.println("found");
			mCtMethod = methodsToImplement[i];
         }
        }
//        JavassistRD.addInstrumentation(mCtMethod);
        Class class1 = cii.toClass();
//        JavassistUtility.printThisHere();
	}

}

package fr.upmc.datacenter.software.javassist;

import javassist.CtMethod;

public class JavassistRD {
	
    
    public static void addInstrumentation(CtMethod ctMethod) throws Exception {
//        ctMethod.insertBefore("{ JavassistUtility.createRequestDispatcher(\""+admission+","+ listComputers+"\"); }");
    	String reString = "{ System.out.println(\"Salut par ici\"); }";
    	ctMethod.insertBefore(reString);
    
        
    }
    

}

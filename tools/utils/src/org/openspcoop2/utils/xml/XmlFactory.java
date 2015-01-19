package org.openspcoop2.utils.xml;

import javax.xml.transform.TransformerFactory;

public class XmlFactory {

	public static TransformerFactory newTransformerFactory() {
		
		/*
			Obtain a new instance of a TransformerFactory. 
			This static method creates a new factory instance This method uses the following ordered lookup procedure 
			to determine the TransformerFactory implementation class to load:

		    - Use the javax.xml.transform.TransformerFactory system property.
		    - Use the properties file "lib/jaxp.properties" in the JRE directory. 
		      This configuration file is in standard java.util.Properties format and contains the fully qualified name of the implementation class 
		      with the key being the system property defined above. 
		      The jaxp.properties file is read only once by the JAXP implementation and it's values are then cached for future use. 
		      If the file does not exist when the first attempt is made to read from it, no further attempts are made to check for its existence. 
		      It is not possible to change the value of any property in jaxp.properties after it has been read for the first time.
		    - Use the Services API (as detailed in the JAR specification), if available, to determine the classname. 
		      The Services API will look for a classname in the file META-INF/services/javax.xml.transform.TransformerFactory in jars available to the runtime.
		    - Platform default TransformerFactory instance.
		 */
		// BugFix per Caused by: java.lang.NullPointerException
		//        at com.sun.org.apache.xalan.internal.xsltc.trax.DOM2SAX.parse(DOM2SAX.java:244)
		
		// New Instance TransformerFactory
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		// La versione 'com.sun.org.apache.xalan.internal.xsltc.trax.*' non funziona correttamente per tutti gli xml.
		// Questa versione viene presa dalla ricerca nel classpath effettuato dal metodo 'newInstance' se nel classpath sono presenti i jar:
		// - jaxp-ri-1.4.5.jar (META-INF/services/javax.xml.transform.TransformerFactory contiene 'com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl')
		// - xalan-2.7.1.jar (META-INF/services/javax.xml.transform.TransformerFactory contiene 'org.apache.xalan.processor.TransformerFactoryImpl')
		if((transformerFactory instanceof org.apache.xalan.processor.TransformerFactoryImpl) == false){
			
			//System.out.println("IMPOSTO XALAN);
			
			String pName = "javax.xml.transform.TransformerFactory";
			String oldProperty = System.getProperty(pName);
			System.setProperty(pName, "org.apache.xalan.processor.TransformerFactoryImpl");
			//System.out.println("OLD ["+oldProperty+"] NEW ["+System.getProperty(pName)+"]");
			
			// New Instance TransformerFactory
			transformerFactory = TransformerFactory.newInstance();
			
			// ripristino
			if(oldProperty!=null){
				System.setProperty(pName,oldProperty);
			}
		}
//		else{
//			System.out.println("NON IMPOSTO XALAN");
//		}

		return transformerFactory;
	}
	
}

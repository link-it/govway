package org.openspcoop2.monitor.engine.dynamic;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * DynamicClassLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicClassLoader extends URLClassLoader {

	public DynamicClassLoader(URL[] urls) {
		super(urls);
	}

	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		
		//System.out.println("FIND CLASS ["+name+"] ...");
		
		try{
			return super.findClass(name);
		}catch(ClassNotFoundException cnf){
			//System.out.println("FIND CLASS ["+name+"] error: "+cnf.getMessage());
		}
		
		Class<?> c = null;
		try{
			c = Class.forName(name);
			//System.out.println("FIND CLASS ["+name+"] ok");
		}catch(ClassNotFoundException cnf){
			//System.out.println("FIND CLASS ["+name+"] error2: "+cnf.getMessage());
			throw cnf;
		}
		
		return c;
	}
}

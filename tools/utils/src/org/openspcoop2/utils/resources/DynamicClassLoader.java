/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.utils.resources;

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
	
	
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
    
    
}

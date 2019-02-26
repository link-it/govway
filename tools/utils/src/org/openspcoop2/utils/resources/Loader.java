/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Loader
 *
 *
 * @author Poli Andrea
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Loader {

	
	private static Loader loader = null;
	public static Loader getInstance(){
		if(Loader.loader==null){
			Loader.initialize();
		}
		return Loader.loader;
	}
	public static synchronized void initialize() {
		if(Loader.loader==null){
			Loader.loader = new Loader();
		}
	}
	public static synchronized void initialize(String classLoaderName) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if(Loader.loader==null){
			Loader.loader = new Loader(classLoaderName);
		}
	}
	public static synchronized void initialize(java.lang.ClassLoader classLoader){
		if(Loader.loader==null){
			Loader.loader = new Loader(classLoader);
		}
	}
	public static synchronized void update(String classLoaderName) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Loader.loader = new Loader(classLoaderName);
	}
	public static synchronized void update(java.lang.ClassLoader classLoader){
		Loader.loader = new Loader(classLoader);
	}
	
	
	java.lang.ClassLoader classLoader = null;
	public Loader(){
	}
	public Loader(String classLoaderName) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Loader lTmp = new Loader();
		Class<?> cLoader = lTmp.forName(classLoaderName);
		try{
			Constructor<?> constructor = cLoader.getConstructor(java.lang.ClassLoader.class);
			this.classLoader = (java.lang.ClassLoader ) constructor.newInstance(this.getClass().getClassLoader());
		}catch(NoSuchMethodException not){
			throw new InstantiationException(not.getMessage());
		}catch(InvocationTargetException not){
			throw new InstantiationException(not.getMessage());
		}
	}
	public Loader(java.lang.ClassLoader classLoader){
		this.classLoader = classLoader;
	}
	
	public Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		return this.newInstance(forName(className));
	}
	public Object newInstance(Class<?> c) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		return c.getConstructor().newInstance();
	}
	
	public Object newInstance(String className,Object ... params) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
		if(params==null || params.length<=0){
			return newInstance(className);
		}
		Class<?> c = forName(className);
		return this.newInstance(c, params);
	}
	public Object newInstance(Class<?> c,Object ... params) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
		if(params==null || params.length<=0){
			return newInstance(c);
		}
		
		Constructor<?> constructor = null;
		if(params.length==1){
			constructor = c.getConstructor(params[0].getClass());
		}
		else if(params.length==2){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass());
		}
		else if(params.length==3){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass());
		}
		else if(params.length==4){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass());
		}
		else if(params.length==5){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass(),params[4].getClass());
		}
		else if(params.length==6){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass(),params[4].getClass(),params[5].getClass());
		}
		else if(params.length==7){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass(),params[4].getClass(),params[5].getClass(),
					params[6].getClass());
		}
		else if(params.length==8){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass(),params[4].getClass(),params[5].getClass(),
					params[6].getClass(),params[7].getClass());
		}
		else if(params.length==9){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass(),params[4].getClass(),params[5].getClass(),
					params[6].getClass(),params[7].getClass(),params[8].getClass());
		}
		else if(params.length==10){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass(),params[4].getClass(),params[5].getClass(),
					params[6].getClass(),params[7].getClass(),params[8].getClass(),
					params[9].getClass());
		}
		else if(params.length==11){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass(),params[4].getClass(),params[5].getClass(),
					params[6].getClass(),params[7].getClass(),params[8].getClass(),
					params[9].getClass(),params[10].getClass());
		}
		else if(params.length==12){
			constructor = c.getConstructor(params[0].getClass(),params[1].getClass(),params[2].getClass(),
					params[3].getClass(),params[4].getClass(),params[5].getClass(),
					params[6].getClass(),params[7].getClass(),params[8].getClass(),
					params[9].getClass(),params[10].getClass(),params[11].getClass());
		}
		else{
			throw new InstantiationException("Method not supported more than 12 parameters");
		}
		return constructor.newInstance(params);
	}
	
	public Class<?> forName(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<?> c = null;
		if(this.classLoader!=null){
			//c = this.classLoader.loadClass(className);
			c = getClass(className, this.classLoader);
		}else{
			//c = Class.forName(className);
			c = getClass(className, null);
		}	
		return c;
	}
	private static HashMap<String, Class<?>> mapClass = new HashMap<String, Class<?>>();
	private static Class<?> getClass(String className,java.lang.ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if(mapClass.containsKey(className)==false){
			initClass(className,classLoader);
		}
		return mapClass.get(className);
	}
	private static synchronized void initClass(String className,java.lang.ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if(mapClass.containsKey(className)==false){
			Class<?> c = null;
			if(classLoader!=null){
				c = classLoader.loadClass(className);
			}else{
				c = Class.forName(className);
			}	
			mapClass.put(className, c);
		}
	}
}

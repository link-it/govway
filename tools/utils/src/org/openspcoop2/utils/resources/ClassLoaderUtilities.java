/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.lang.reflect.InvocationTargetException;

/**
 * ClassLoaderUtilities
 *
 *
 * @author Poli Andrea
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClassLoaderUtilities {

	
	public static Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		return Loader.getInstance().newInstance(className);
	}
	public static Object newInstance(Class<?> c) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		return Loader.getInstance().newInstance(c);
	}
	
	public static Object newInstance(String className,Object ... params) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
		return Loader.getInstance().newInstance(className,params);
	}
	public static Object newInstance(Class<?> c,Object ... params) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
		return Loader.getInstance().newInstance(c,params);
	}
	
	public static Class<?> forName(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		return Loader.getInstance().forName(className);
	}

}

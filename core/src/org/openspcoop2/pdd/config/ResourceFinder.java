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


package org.openspcoop2.pdd.config;

/**
* ResourceFinder
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class ResourceFinder extends ClassLoader  {

	public ResourceFinder(java.lang.ClassLoader parentLoader){
		super(parentLoader);
	}
	
	public Class<?> loadResource(String className,byte[] resource) throws ClassNotFoundException {
		
		try{
			if(className==null){
				throw new Exception("Nome della classe non fornito");
			}
			if(resource==null){
				throw new Exception("Risorsa non fornita");
			}
			Class<?> result = defineClass(className,resource,0,resource.length,null);
			return result;
		}catch(Exception e){
			throw new ClassNotFoundException("Classe ["+className+"] non caricata",e);
		}
	}
}

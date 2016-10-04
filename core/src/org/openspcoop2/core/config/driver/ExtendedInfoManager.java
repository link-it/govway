/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.core.config.driver;

import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.utils.resources.Loader;

/**
 * ExtendedInfoManager
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExtendedInfoManager {

	
	// STATIC
	
	private static ExtendedInfoManager staticInstance = null;
	public static synchronized void initialize(
			Loader openspcoop2Loader,
			String classExtendedInfoConfigurazione,
			String classExtendedInfoPortaDelegata, String classExtendedInfoPortaApplicativa){
		initialize(false, openspcoop2Loader, 
				classExtendedInfoConfigurazione, classExtendedInfoPortaDelegata, classExtendedInfoPortaApplicativa);
	}
	public static synchronized void initialize(
			boolean forceOverride,
			Loader openspcoop2Loader,
			String classExtendedInfoConfigurazione,
			String classExtendedInfoPortaDelegata, String classExtendedInfoPortaApplicativa){
		if(staticInstance==null || forceOverride){
			staticInstance = new ExtendedInfoManager(openspcoop2Loader,
					classExtendedInfoConfigurazione,
					classExtendedInfoPortaDelegata, classExtendedInfoPortaApplicativa);
		}
	}
	public static ExtendedInfoManager getInstance() throws DriverConfigurazioneException{
		if(staticInstance==null){
			throw new DriverConfigurazioneException("ExtendedInfoManager not initialized");
		}
		return staticInstance;
	}
	
	
	
	// INSTANCE
	
	private String classExtendedInfoConfigurazione;
	private String classExtendedInfoPortaDelegata;
	private String classExtendedInfoPortaApplicativa;
	private Loader openspcoop2Loader;
	
	private ExtendedInfoManager(Loader openspcoop2Loader,
			String classExtendedInfoConfigurazione,
			String classExtendedInfoPortaDelegata, String classExtendedInfoPortaApplicativa){
		this.openspcoop2Loader = openspcoop2Loader;
		this.classExtendedInfoConfigurazione = classExtendedInfoConfigurazione;
		this.classExtendedInfoPortaDelegata = classExtendedInfoPortaDelegata;
		this.classExtendedInfoPortaApplicativa = classExtendedInfoPortaApplicativa;
	} 
	
	
	public IExtendedInfo newInstanceExtendedInfoConfigurazione() throws DriverConfigurazioneException{
		try{
			if(this.classExtendedInfoConfigurazione==null){
				return null;
			}
			return (IExtendedInfo) this.openspcoop2Loader.newInstance(this.classExtendedInfoConfigurazione);
		}catch(Throwable e){
			throw new DriverConfigurazioneException("[ExtendedInfoConfigurazione] "+e.getMessage(),e);
		}
	} 
	
	public IExtendedInfo newInstanceExtendedInfoPortaDelegata() throws DriverConfigurazioneException{
		try{
			if(this.classExtendedInfoPortaDelegata==null){
				return null;
			}
			return (IExtendedInfo) this.openspcoop2Loader.newInstance(this.classExtendedInfoPortaDelegata);
		}catch(Throwable e){
			throw new DriverConfigurazioneException("[ExtendedInfoPortaDelegata] "+e.getMessage(),e);
		}
	} 
	
	public IExtendedInfo newInstanceExtendedInfoPortaApplicativa() throws DriverConfigurazioneException{
		try{
			if(this.classExtendedInfoPortaApplicativa==null){
				return null;
			}
			return (IExtendedInfo) this.openspcoop2Loader.newInstance(this.classExtendedInfoPortaApplicativa);
		}catch(Throwable e){
			throw new DriverConfigurazioneException("[ExtendedInfoPortaApplicativa] "+e.getMessage(),e);
		}
	} 
	
}

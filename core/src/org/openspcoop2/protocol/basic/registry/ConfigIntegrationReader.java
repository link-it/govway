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

package org.openspcoop2.protocol.basic.registry;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.slf4j.Logger;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class ConfigIntegrationReader implements IConfigIntegrationReader {

	private IDriverConfigurazioneGet driverConfigurazioneGET;
	private IDriverConfigurazioneCRUD driverConfigurazioneCRUD;
	@SuppressWarnings("unused")
	private Logger log;
	public ConfigIntegrationReader(IDriverConfigurazioneGet driverConfigurazione,Logger log) throws Exception{
		this.driverConfigurazioneGET = driverConfigurazione;
		if(this.driverConfigurazioneGET instanceof IDriverConfigurazioneCRUD){
			this.driverConfigurazioneCRUD = (IDriverConfigurazioneCRUD) this.driverConfigurazioneGET;
		}
		this.log = log;
	}
	

	
	// SERVIZI APPLICATIVI
	
	@Override
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) {
		try{
			if(this.driverConfigurazioneCRUD!=null){
				return this.driverConfigurazioneCRUD.existsServizioApplicativo(idServizioApplicativo);
			}
			else{
				return this.driverConfigurazioneGET.getServizioApplicativo(idServizioApplicativo)!=null;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiBasic(String username, String password){
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiBasic(username, password)!=null;
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiSsl(String subject){
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiSsl(subject)!=null;
		}catch(Exception e){
			return false;
		}	
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound{
		try{
			return this.driverConfigurazioneGET.getServizioApplicativo(idServizioApplicativo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	
	
	
	
	// PORTA DELEGATA
	
	@Override
	public IDPortaDelegata getIdPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound{
		
		try{
			return this.driverConfigurazioneGET.getIDPortaDelegata(nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}

	}
	
	@Override
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata){
		try{
			if(this.driverConfigurazioneCRUD!=null){
				return this.driverConfigurazioneCRUD.existsPortaDelegata(idPortaDelegata);
			}
			else{
				return this.driverConfigurazioneGET.getPortaDelegata(idPortaDelegata)!=null;
			}
		}catch(Exception e){
			return false;
		}	
	}
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound{
		try{
			return this.driverConfigurazioneGET.getPortaDelegata(idPortaDelegata);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
		
		
	// PORTA APPLICATIVA
		
	@Override
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound{
		
		try{
			return this.driverConfigurazioneGET.getIDPortaApplicativa(nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}

	}
	
	
	@Override
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa){
		try{
			if(this.driverConfigurazioneCRUD!=null){
				return this.driverConfigurazioneCRUD.existsPortaApplicativa(idPortaApplicativa);
			}
			else{
				return this.driverConfigurazioneGET.getPortaApplicativa(idPortaApplicativa)!=null;
			}
		}catch(Exception e){
			return false;
		}	
	}
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound{
		try{
			return this.driverConfigurazioneGET.getPortaApplicativa(idPortaApplicativa);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	
	
	
}

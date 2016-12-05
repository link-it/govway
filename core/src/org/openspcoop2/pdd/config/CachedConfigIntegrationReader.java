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

package org.openspcoop2.pdd.config;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.slf4j.Logger;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class CachedConfigIntegrationReader implements IConfigIntegrationReader {

	@SuppressWarnings("unused")
	private Logger log;
	private ConfigurazionePdDManager configurazionePdDMangager;
	@SuppressWarnings("unused")
	private IProtocolFactory<?> protocolFactory;
	public CachedConfigIntegrationReader(Logger log,IProtocolFactory<?> protocolFactory) throws Exception{
		this(log, protocolFactory, null);
	}
	public CachedConfigIntegrationReader(Logger log,IProtocolFactory<?> protocolFactory, IState state) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.configurazionePdDMangager = ConfigurazionePdDManager.getInstance(state);
	}

	
	
	// SERVIZI APPLICATIVI
	
	@Override
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) {
		try{
			return this.configurazionePdDMangager.getServizioApplicativo(idServizioApplicativo)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiBasic(String username, String password){
		try{
			return this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiBasic(username, password)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiSsl(String subject){
		try{
			return this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiSsl(subject)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound{
		try{
			return this.configurazionePdDMangager.getServizioApplicativo(idServizioApplicativo);
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
			return this.configurazionePdDMangager.getIDPortaDelegata(nome,protocolFactory);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	@Override
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata){
		try{
			return this.configurazionePdDMangager.getPortaDelegata(idPortaDelegata)!=null;
		} catch (DriverConfigurazioneNotFound de) {
			return false;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound{
		try{
			return this.configurazionePdDMangager.getPortaDelegata(idPortaDelegata);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	public String getAzione(PortaDelegata pd,URLProtocolContext transportContext, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, IdentificazioneDinamicaException{
		return this.configurazionePdDMangager.getAzione(pd, transportContext, null, null, false, protocolFactory);
	}
	
	
	// PORTA APPLICATIVA
	
	@Override
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound{
		try{
			return this.configurazionePdDMangager.getIDPortaApplicativa(nome,protocolFactory);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	@Override
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa){
		try{
			return this.configurazionePdDMangager.getPortaApplicativa(idPortaApplicativa)!=null;
		} catch (DriverConfigurazioneNotFound de) {
			return false;
		}catch(Exception e){
			return false;
		}
	} 
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound{
		try{
			return this.configurazionePdDMangager.getPortaApplicativa(idPortaApplicativa);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			return null;
		}
	}
	public String getAzione(PortaApplicativa pa,URLProtocolContext transportContext, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, IdentificazioneDinamicaException{
		return this.configurazionePdDMangager.getAzione(pa, transportContext, null, null, false, protocolFactory);
	}
	
}


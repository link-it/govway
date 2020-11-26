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

package org.openspcoop2.protocol.basic.registry;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.commons.Search;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.utils.ControlloTrafficoDriverUtils;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteDelegate;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServiziApplicativi;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReaderInUso;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigIntegrationReader implements IConfigIntegrationReader {

	private IDriverConfigurazioneGet driverConfigurazioneGET;
	private IDriverConfigurazioneCRUD driverConfigurazioneCRUD;
	
	private IConfigIntegrationReaderInUso inUsoDriver = null;
	
	@SuppressWarnings("unused")
	private Logger log;
	public ConfigIntegrationReader(IDriverConfigurazioneGet driverConfigurazione,Logger log) throws Exception{
		this.driverConfigurazioneGET = driverConfigurazione;
		if(this.driverConfigurazioneGET instanceof IDriverConfigurazioneCRUD){
			this.driverConfigurazioneCRUD = (IDriverConfigurazioneCRUD) this.driverConfigurazioneGET;
		}
		this.log = log;
		
		Loader loader = new Loader();
		this.inUsoDriver = (IConfigIntegrationReaderInUso) loader.newInstance("org.openspcoop2.protocol.engine.registry.ConfigIntegrationReaderInUso");
		this.inUsoDriver.init(this.driverConfigurazioneGET, log);
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
	public boolean existsServizioApplicativoByCredenzialiBasic(String username, String password, CryptConfig config){
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiBasic(username, password, config)!=null;
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String username, String password, CryptConfig config) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiBasic(username, password, config);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiSsl(String subject, String issuer){
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiSsl(subject, issuer)!=null;
		}catch(Exception e){
			return false;
		}	
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String subject, String issuer) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiSsl(subject, issuer);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier){
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiSsl(certificate, strictVerifier)!=null;
		}catch(Exception e){
			return false;
		}	
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiSsl(certificate, strictVerifier);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiPrincipal(String principal){
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiPrincipal(principal)!=null;
		}catch(Exception e){
			return false;
		}	
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiPrincipal(principal);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getServizioApplicativo(idServizioApplicativo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDServizioApplicativo> findIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getAllIdServiziApplicativi(filtroRicerca);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean inUso(IDServizioApplicativo idServizioApplicativo) throws RegistryException{
		return this.inUsoDriver.inUso(idServizioApplicativo);
	}
	
	@Override
	public String getDettagliInUso(IDServizioApplicativo idServizioApplicativo) throws RegistryException{
		return this.inUsoDriver.getDettagliInUso(idServizioApplicativo);
	}
	
	
	
	
	// PORTA DELEGATA
	
	@Override
	public IDPortaDelegata getIdPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException{
		
		try{
			return this.driverConfigurazioneGET.getIDPortaDelegata(nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
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
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getPortaDelegata(idPortaDelegata);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDPortaDelegata> findIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getAllIdPorteDelegate(filtroRicerca);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<AttivazionePolicy> getRateLimitingPolicy(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getRateLimitingPolicy(portaDelegata)");
				
				org.openspcoop2.core.commons.Search search = new Search(true);
				return ControlloTrafficoDriverUtils.configurazioneControlloTrafficoAttivazionePolicyList(search, RuoloPolicy.DELEGATA, idPortaDelegata.getNome(),
						con, this.log, driver.getTipoDB());
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				try {
					if(con!=null) {
						driver.releaseConnection(con);
					}
				}catch(Exception eClose) {}
			}
			
		}
		else {
			throw new RegistryException("Not Implemented");
		}
	}
		
	
	
	
	
		
	// PORTA APPLICATIVA
		
	@Override
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException{
		
		try{
			return this.driverConfigurazioneGET.getIDPortaApplicativa(nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
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
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getPortaApplicativa(idPortaApplicativa);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDPortaApplicativa> findIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getAllIdPorteApplicative(filtroRicerca);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	
	@Override
	public List<AttivazionePolicy> getRateLimitingPolicy(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getRateLimitingPolicy(portaApplicativa)");
				
				org.openspcoop2.core.commons.Search search = new Search(true);
				return ControlloTrafficoDriverUtils.configurazioneControlloTrafficoAttivazionePolicyList(search, RuoloPolicy.APPLICATIVA, idPortaApplicativa.getNome(),
						con, this.log, driver.getTipoDB());
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				try {
					if(con!=null) {
						driver.releaseConnection(con);
					}
				}catch(Exception eClose) {}
			}
			
		}
		else {
			throw new RegistryException("Not Implemented");
		}
	}
	
	

	// CONFIGURAZIONE
	
	@Override
	public CanaliConfigurazione getCanaliConfigurazione() throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getCanaliConfigurazione();
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<AttivazionePolicy> getRateLimitingPolicyGlobali() throws RegistryNotFound,RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getRateLimitingPolicyGlobali");
				
				org.openspcoop2.core.commons.Search search = new Search(true);
				return ControlloTrafficoDriverUtils.configurazioneControlloTrafficoAttivazionePolicyList(search, null, null,
						con, this.log, driver.getTipoDB());
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				try {
					if(con!=null) {
						driver.releaseConnection(con);
					}
				}catch(Exception eClose) {}
			}
			
		}
		else {
			throw new RegistryException("Not Implemented");
		}
	}
	
	@Override
	public Integer getFreeCounterForGlobalPolicy(String policyId) throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getRateLimitingPolicyGlobali");
				
				return ControlloTrafficoDriverUtils.getFreeCounterForGlobalPolicy(policyId,
						con, this.log, driver.getTipoDB());
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				try {
					if(con!=null) {
						driver.releaseConnection(con);
					}
				}catch(Exception eClose) {}
			}
			
		}
		else {
			throw new RegistryException("Not Implemented");
		}
	}

}

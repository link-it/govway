/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.Search;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.utils.ControlloTrafficoDriverUtils;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPorteDelegate;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServiziApplicativi;
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
	
	private Logger log;
	public ConfigIntegrationReader(IDriverConfigurazioneGet driverConfigurazione,Logger log) throws ProtocolException{
		this.driverConfigurazioneGET = driverConfigurazione;
		if(this.driverConfigurazioneGET instanceof IDriverConfigurazioneCRUD){
			this.driverConfigurazioneCRUD = (IDriverConfigurazioneCRUD) this.driverConfigurazioneGET;
		}
		this.log = log;
		
		Loader loader = new Loader();
		try {
			this.inUsoDriver = (IConfigIntegrationReaderInUso) loader.newInstance("org.openspcoop2.protocol.engine.registry.ConfigIntegrationReaderInUso");
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		this.inUsoDriver.init(this.driverConfigurazioneGET, log);
	}
	
	private static final String NOT_IMPLEMENTED = "Not Implemented";

	
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
	public boolean existsServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) {
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiToken(tokenPolicy, tokenClientId, tokenWithHttpsEnabled)!=null;
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getServizioApplicativoByCredenzialiToken(tokenPolicy, tokenClientId, tokenWithHttpsEnabled);
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
	public List<IDServizioApplicativo> findIdServiziApplicativi(ProtocolFiltroRicercaServiziApplicativi filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.driverConfigurazioneGET.getAllIdServiziApplicativi(filtroRicerca);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean inUso(IDServizioApplicativo idServizioApplicativo, boolean verificaRuoli) throws RegistryException{
		return this.inUsoDriver.inUso(idServizioApplicativo, verificaRuoli);
	}
	
	@Override
	public String getDettagliInUso(IDServizioApplicativo idServizioApplicativo, boolean verificaRuoli) throws RegistryException{
		return this.inUsoDriver.getDettagliInUso(idServizioApplicativo, verificaRuoli);
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
	public List<IDPortaDelegata> findIdPorteDelegate(ProtocolFiltroRicercaPorteDelegate filtroRicerca) throws RegistryNotFound,RegistryException{
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
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public List<Allarme> getAllarmi(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException{
		if(!CostantiDB.isAllarmiEnabled()) {
			return new ArrayList<>();
		}
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getAllarmi(portaDelegata)");
				
				org.openspcoop2.core.commons.Search search = new Search(true);
				return AllarmiDriverUtils.allarmiList(search, RuoloPorta.DELEGATA, idPortaDelegata.getNome(),
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
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public List<MappingFruizionePortaDelegata> getMappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio) throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getMappingFruizionePortaDelegataList");
				
				return DBMappingUtils.mappingFruizionePortaDelegataList(con, driver.getTipoDB(), idFruitore, idServizio, false);
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				try {
					if(con!=null) {
						driver.releaseConnection(con);
					}
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
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
	public List<IDPortaApplicativa> findIdPorteApplicative(ProtocolFiltroRicercaPorteApplicative filtroRicerca) throws RegistryNotFound,RegistryException{
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
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public List<Allarme> getAllarmi(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException{
		if(!CostantiDB.isAllarmiEnabled()) {
			return new ArrayList<>();
		}
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getAllarmi(portaApplicativa)");
				
				org.openspcoop2.core.commons.Search search = new Search(true);
				return AllarmiDriverUtils.allarmiList(search, RuoloPorta.APPLICATIVA, idPortaApplicativa.getNome(),
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
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public List<MappingErogazionePortaApplicativa> getMappingErogazionePortaApplicativaList(IDServizio idServizio) throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getMappingErogazionePortaApplicativaList");
				
				return DBMappingUtils.mappingErogazionePortaApplicativaList(con, driver.getTipoDB(), idServizio, false);
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				try {
					if(con!=null) {
						driver.releaseConnection(con);
					}
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	

	// CONFIGURAZIONE
	
	@Override
	public ConfigurazioneMultitenant getConfigurazioneMultitenant() throws RegistryNotFound,RegistryException{
		try{
			ConfigurazioneMultitenant conf = this.driverConfigurazioneGET.getConfigurazioneGenerale().getMultitenant();
			if(conf==null) {
				return new ConfigurazioneMultitenant();
			}
			return conf;
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
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
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public String getNextPolicyInstanceSerialId(String policyId) throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getNextPolicyInstanceSerialId");
				
				return ControlloTrafficoDriverUtils.getNextPolicyInstanceSerialId(policyId,
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
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}

	@Override
	public List<Allarme> getAllarmiGlobali() throws RegistryNotFound,RegistryException{
		if(!CostantiDB.isAllarmiEnabled()) {
			return new ArrayList<>();
		}
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getAllarmiGlobali");
				
				org.openspcoop2.core.commons.Search search = new Search(true);
				return AllarmiDriverUtils.allarmiList(search, null, null,
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
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public String getNextAlarmInstanceSerialId(String tipoPlugin) throws RegistryException{
		if(!CostantiDB.isAllarmiEnabled()) {
			throw new RegistryException("Alarm not supported");
		}
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection con = null;
			try {
				con = driver.getConnection("getNextAlarmInstanceSerialId");
				
				return AllarmiDriverUtils.getNextAlarmInstanceSerialId(tipoPlugin,
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
				}catch(Exception eClose) {
					// close
				}
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public List<GenericProperties> getTokenPolicyValidazione() throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			try {
				org.openspcoop2.core.commons.Search search = new Search(true);
				List<String> tipologiaList = new ArrayList<>();
				tipologiaList.add(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION);
				return driver.getGenericProperties(tipologiaList, Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN, search,false);
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	@Override
	public GenericProperties getTokenPolicyValidazione(String nome) throws RegistryNotFound,RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			try {
				GenericProperties gp = driver.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION, nome);
				if(gp==null) {
					throw new DriverConfigurazioneNotFound();
				}
				return gp;
			} catch (DriverConfigurazioneNotFound de) {
				throw new RegistryNotFound(de.getMessage(),de);
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public List<GenericProperties> getTokenPolicyNegoziazione() throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			try {
				org.openspcoop2.core.commons.Search search = new Search(true);
				List<String> tipologiaList = new ArrayList<>();
				tipologiaList.add(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE);
				return driver.getGenericProperties(tipologiaList, Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN, search,false);
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	@Override
	public GenericProperties getTokenPolicyNegoziazione(String nome) throws RegistryNotFound,RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			try {
				GenericProperties gp = driver.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE, nome);
				if(gp==null) {
					throw new DriverConfigurazioneNotFound();
				}
				return gp;
			} catch (DriverConfigurazioneNotFound de) {
				throw new RegistryNotFound(de.getMessage(),de);
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	
	@Override
	public List<GenericProperties> getAttributeAuthority() throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			try {
				org.openspcoop2.core.commons.Search search = new Search(true);
				List<String> tipologiaList = new ArrayList<>();
				tipologiaList.add(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY);
				return driver.getGenericProperties(tipologiaList, Liste.CONFIGURAZIONE_GESTIONE_ATTRIBUTE_AUTHORITY, search,false);
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}
	@Override
	public GenericProperties getAttributeAuthority(String nome) throws RegistryNotFound,RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			try {
				GenericProperties gp = driver.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY, nome);
				if(gp==null) {
					throw new DriverConfigurazioneNotFound();
				}
				return gp;
			} catch (DriverConfigurazioneNotFound de) {
				throw new RegistryNotFound(de.getMessage(),de);
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			
		}
		else {
			throw new RegistryException(NOT_IMPLEMENTED);
		}
	}

}

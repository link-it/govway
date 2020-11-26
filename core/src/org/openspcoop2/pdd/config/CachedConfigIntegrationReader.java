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

package org.openspcoop2.pdd.config;

import java.util.List;

import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteDelegate;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServiziApplicativi;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.slf4j.Logger;

/**
 *  ArchiveRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
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
	public boolean existsServizioApplicativoByCredenzialiBasic(String username, String password, CryptConfig config){
		try{
			return this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiBasic(username, password, config)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String username, String password, CryptConfig config) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiBasic(username, password, config);
			return this.configurazionePdDMangager.getServizioApplicativo(idSA);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiSsl(String subject, String aIssuer){
		try{
			return this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiSsl(subject,aIssuer)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String subject, String aIssuer) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiSsl(subject,aIssuer);
			return this.configurazionePdDMangager.getServizioApplicativo(idSA);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier){
		try{
			return this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiSsl(certificate,strictVerifier)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiSsl(certificate,strictVerifier);
			return this.configurazionePdDMangager.getServizioApplicativo(idSA);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiPrincipal(String principal){
		try{
			return this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiPrincipal(principal)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDMangager.getIdServizioApplicativoByCredenzialiPrincipal(principal);
			return this.configurazionePdDMangager.getServizioApplicativo(idSA);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getServizioApplicativo(idServizioApplicativo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDServizioApplicativo> findIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getAllIdServiziApplicativi(filtroRicerca);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean inUso(IDServizioApplicativo idServizioApplicativo) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public String getDettagliInUso(IDServizioApplicativo idServizioApplicativo) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	
	
	// PORTA DELEGATA
	
	@Override
	public IDPortaDelegata getIdPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getIDPortaDelegata(nome,protocolFactory);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
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
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getPortaDelegata(idPortaDelegata);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	public String getAzione(PortaDelegata pd,URLProtocolContext transportContext, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, IdentificazioneDinamicaException{
		return this.configurazionePdDMangager.getAzione(pd, transportContext, null, null, false, protocolFactory);
	}
	
	@Override
	public List<IDPortaDelegata> findIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getAllIdPorteDelegate(filtroRicerca);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<AttivazionePolicy> getRateLimitingPolicy(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException{
		throw new RegistryException("Not Implemented");
	}
	
	
	// PORTA APPLICATIVA
	
	@Override
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getIDPortaApplicativa(nome,protocolFactory);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
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
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getPortaApplicativa(idPortaApplicativa);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	public String getAzione(PortaApplicativa pa,URLProtocolContext transportContext, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, IdentificazioneDinamicaException{
		return this.configurazionePdDMangager.getAzione(pa, transportContext, null, null, false, protocolFactory);
	}
	
	@Override
	public List<IDPortaApplicativa> findIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getAllIdPorteApplicative(filtroRicerca);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<AttivazionePolicy> getRateLimitingPolicy(IDPortaApplicativa idPortaDelegata) throws RegistryNotFound,RegistryException{
		throw new RegistryException("Not Implemented");
	}
	
	
	// CONFIGURAZIONE
	
	@Override
	public CanaliConfigurazione getCanaliConfigurazione() throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDMangager.getCanaliConfigurazione();
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<AttivazionePolicy> getRateLimitingPolicyGlobali() throws RegistryNotFound,RegistryException{
		throw new RegistryException("Not Implemented");
	}
	
	@Override
	public Integer getFreeCounterForGlobalPolicy(String policyId) throws RegistryException{
		throw new RegistryException("Not Implemented");
	}
}


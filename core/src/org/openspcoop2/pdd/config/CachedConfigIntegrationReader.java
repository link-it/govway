/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPorteDelegate;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServiziApplicativi;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
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
	private ConfigurazionePdDManager configurazionePdDManager;
	@SuppressWarnings("unused")
	private IProtocolFactory<?> protocolFactory;
	private RequestInfo requestInfo;
	/*public CachedConfigIntegrationReader(Logger log,IProtocolFactory<?> protocolFactory) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.configurazionePdDMangager = ConfigurazionePdDManager.getInstance();
	}*/
	public CachedConfigIntegrationReader(Logger log,IProtocolFactory<?> protocolFactory, IState state, RequestInfo requestInfo) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
		this.requestInfo = requestInfo;
	}
	public CachedConfigIntegrationReader(Logger log,IProtocolFactory<?> protocolFactory, ConfigurazionePdDManager configurazionePdDManager, RequestInfo requestInfo) throws Exception{
		this.log = log;
		this.protocolFactory = protocolFactory;
		this.configurazionePdDManager = configurazionePdDManager;
		this.requestInfo = requestInfo;
	}

	
	
	// SERVIZI APPLICATIVI
	
	@Override
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) {
		try{
			return this.configurazionePdDManager.getServizioApplicativo(idServizioApplicativo, this.requestInfo)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiBasic(String username, String password, CryptConfig config){
		try{
			return this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiBasic(username, password, config)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String username, String password, CryptConfig config) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiBasic(username, password, config);
			return this.configurazionePdDManager.getServizioApplicativo(idSA, this.requestInfo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiSsl(String subject, String aIssuer){
		try{
			return this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject,aIssuer)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String subject, String aIssuer) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject,aIssuer);
			return this.configurazionePdDManager.getServizioApplicativo(idSA, this.requestInfo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier){
		try{
			return this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate,strictVerifier)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate,strictVerifier);
			return this.configurazionePdDManager.getServizioApplicativo(idSA, this.requestInfo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiPrincipal(String principal){
		try{
			return this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiPrincipal(principal)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiPrincipal(principal);
			return this.configurazionePdDManager.getServizioApplicativo(idSA, this.requestInfo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) {
		try{
			return this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiToken(tokenPolicy, tokenClientId)!=null;
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) throws RegistryNotFound,RegistryException{
		try{
			IDServizioApplicativo idSA = this.configurazionePdDManager.getIdServizioApplicativoByCredenzialiToken(tokenPolicy, tokenClientId);
			return this.configurazionePdDManager.getServizioApplicativo(idSA, this.requestInfo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getServizioApplicativo(idServizioApplicativo, this.requestInfo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<IDServizioApplicativo> findIdServiziApplicativi(ProtocolFiltroRicercaServiziApplicativi filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getAllIdServiziApplicativi(filtroRicerca);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean inUso(IDServizioApplicativo idServizioApplicativo, boolean verificaRuoli) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public String getDettagliInUso(IDServizioApplicativo idServizioApplicativo, boolean verificaRuoli) throws RegistryException{
		throw new RuntimeException("Not Implemented");
	}
	
	
	
	// PORTA DELEGATA
	
	@Override
	public IDPortaDelegata getIdPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getIDPortaDelegata(nome,this.requestInfo,protocolFactory);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata){
		try{
			return this.configurazionePdDManager.getPortaDelegata(idPortaDelegata, this.requestInfo)!=null;
		} catch (DriverConfigurazioneNotFound de) {
			return false;
		}catch(Exception e){
			return false;
		}
	}
	
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getPortaDelegata(idPortaDelegata, this.requestInfo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	public String getAzione(PortaDelegata pd,URLProtocolContext transportContext, RequestInfo requestInfo, IProtocolFactory<?> protocolFactory,
			OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, IdentificazioneDinamicaException{
		return this.configurazionePdDManager.getAzione(pd, transportContext, requestInfo, null, soapStreamReader, null, false, protocolFactory);
	}
	
	@Override
	public List<IDPortaDelegata> findIdPorteDelegate(ProtocolFiltroRicercaPorteDelegate filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getAllIdPorteDelegate(filtroRicerca);
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
	
	@Override
	public List<Allarme> getAllarmi(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException{
		throw new RegistryException("Not Implemented");
	}
	
	@Override
	public List<MappingFruizionePortaDelegata> getMappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio) throws RegistryException{
		try{
			return this.configurazionePdDManager.getMappingFruizionePortaDelegataList(idFruitore, idServizio, this.requestInfo);
		} catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	
	
	// PORTA APPLICATIVA
	
	@Override
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getIDPortaApplicativa(nome, this.requestInfo, protocolFactory);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	@Override
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa){
		try{
			return this.configurazionePdDManager.getPortaApplicativa(idPortaApplicativa, this.requestInfo)!=null;
		} catch (DriverConfigurazioneNotFound de) {
			return false;
		}catch(Exception e){
			return false;
		}
	} 
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getPortaApplicativa(idPortaApplicativa, this.requestInfo);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	public String getAzione(PortaApplicativa pa,URLProtocolContext transportContext, RequestInfo requestInfo, IProtocolFactory<?> protocolFactory,
			OpenSPCoop2Message message, OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, IdentificazioneDinamicaException{
		return this.configurazionePdDManager.getAzione(pa, transportContext, requestInfo, message, soapStreamReader, null, false, protocolFactory);
	}
	
	@Override
	public List<IDPortaApplicativa> findIdPorteApplicative(ProtocolFiltroRicercaPorteApplicative filtroRicerca) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getAllIdPorteApplicative(filtroRicerca);
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
	
	@Override
	public List<Allarme> getAllarmi(IDPortaApplicativa idPortaDelegata) throws RegistryNotFound,RegistryException{
		throw new RegistryException("Not Implemented");
	}
	
	@Override
	public List<MappingErogazionePortaApplicativa> getMappingErogazionePortaApplicativaList(IDServizio idServizio) throws RegistryException{
		try{
			return this.configurazionePdDManager.getMappingErogazionePortaApplicativaList(idServizio, this.requestInfo);
		} catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	
	
	// CONFIGURAZIONE
	
	@Override
	public ConfigurazioneMultitenant getConfigurazioneMultitenant() throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getConfigurazioneMultitenant();
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public CanaliConfigurazione getCanaliConfigurazione() throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getCanaliConfigurazione();
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
	public String getNextPolicyInstanceSerialId(String policyId) throws RegistryException{
		throw new RegistryException("Not Implemented");
	}
	
	@Override
	public List<Allarme> getAllarmiGlobali() throws RegistryNotFound,RegistryException{
		throw new RegistryException("Not Implemented");
	}
	
	@Override
	public String getNextAlarmInstanceSerialId(String tipoPlugin) throws RegistryException{
		throw new RegistryException("Not Implemented");
	}
	
	@Override
	public List<GenericProperties> getTokenPolicyValidazione() throws RegistryException {
		try{
			return this.configurazionePdDManager.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	@Override
	public GenericProperties getTokenPolicyValidazione(String nome) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION, nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<GenericProperties> getTokenPolicyNegoziazione() throws RegistryException {
		try{
			return this.configurazionePdDManager.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	@Override
	public GenericProperties getTokenPolicyNegoziazione(String nome) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE, nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	
	@Override
	public List<GenericProperties> getAttributeAuthority() throws RegistryException {
		try{
			return this.configurazionePdDManager.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
	@Override
	public GenericProperties getAttributeAuthority(String nome) throws RegistryNotFound,RegistryException{
		try{
			return this.configurazionePdDManager.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY, nome);
		} catch (DriverConfigurazioneNotFound de) {
			throw new RegistryNotFound(de.getMessage(),de);
		}catch(Exception e){
			throw new RegistryException(e.getMessage(),e);
		}
	}
}


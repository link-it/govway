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
package org.openspcoop2.protocol.sdk.registry;

import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;

/**
 * IRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IConfigIntegrationReader extends IConfigIntegrationReaderInUso {
	
	// SERVIZI APPLICATIVI
	
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo);
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiBasic(String username, String password, CryptConfig config);
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String username, String password, CryptConfig config) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiSsl(String subject, String aIssuer);
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String subject, String aIssuer) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiSsl(String subject, String aIssuer,
			List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni);
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String subject, String aIssuer,
			List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni) throws RegistryNotFound,RegistryException;
		
	public boolean existsServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier);
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier,
			List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni);
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier,
			List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiPrincipal(String principal);
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled);
	public ServizioApplicativo getServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) throws RegistryNotFound,RegistryException;
	
	public List<IDServizioApplicativo> findIdServiziApplicativi(ProtocolFiltroRicercaServiziApplicativi filtroRicerca) throws RegistryNotFound,RegistryException;
	
	public List<IDServizioApplicativo> findIdServiziApplicativiByPaAuth(PortaApplicativa pa, boolean authToken, boolean authTrasporto) throws RegistryNotFound,RegistryException;
	
	public List<IDServizioApplicativo> findIdServiziApplicativiByPdAuth(PortaDelegata pd, boolean authToken, boolean authTrasporto) throws RegistryNotFound,RegistryException;
	

	// PORTA DELEGATA
	
	public IDPortaDelegata getIdPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException;
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata); 
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException; 
	
	public List<IDPortaDelegata> findIdPorteDelegate(ProtocolFiltroRicercaPorteDelegate filtroRicerca) throws RegistryNotFound,RegistryException;
	
	public List<AttivazionePolicy> getRateLimitingPolicy(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException;
	
	public List<Allarme> getAllarmi(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException;
	
	public List<MappingFruizionePortaDelegata> getMappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio) throws RegistryException;
	
	// PORTA APPLICATIVA
	
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException;
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa); 
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException; 
	
	public List<IDPortaApplicativa> findIdPorteApplicative(ProtocolFiltroRicercaPorteApplicative filtroRicerca) throws RegistryNotFound,RegistryException;
	
	public List<AttivazionePolicy> getRateLimitingPolicy(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException;
	
	public List<Allarme> getAllarmi(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException;
	
	public List<MappingErogazionePortaApplicativa> getMappingErogazionePortaApplicativaList(IDServizio idServizio) throws RegistryException;
	
	// CONFIGURAZIONE
	
	public ConfigurazioneMultitenant getConfigurazioneMultitenant() throws RegistryNotFound,RegistryException;
	
	public CanaliConfigurazione getCanaliConfigurazione() throws RegistryNotFound,RegistryException;
	
	public List<AttivazionePolicy> getRateLimitingPolicyGlobali() throws RegistryNotFound,RegistryException;
	
	public String getNextPolicyInstanceSerialId(String policyId) throws RegistryException;
	
	public List<Allarme> getAllarmiGlobali() throws RegistryNotFound,RegistryException;
	
	public String getNextAlarmInstanceSerialId(String tipoPlugin) throws RegistryException;
	
	public List<GenericProperties> getTokenPolicyValidazione() throws RegistryException;
	public GenericProperties getTokenPolicyValidazione(String nome) throws RegistryNotFound,RegistryException;
	
	public List<GenericProperties> getTokenPolicyNegoziazione() throws RegistryException;
	public GenericProperties getTokenPolicyNegoziazione(String nome) throws RegistryNotFound,RegistryException;
	
	public List<GenericProperties> getAttributeAuthority() throws RegistryException;
	public GenericProperties getAttributeAuthority(String nome) throws RegistryNotFound,RegistryException;
}

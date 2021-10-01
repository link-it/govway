/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.validator;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaProtocolProperty;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServiziApplicativi;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.slf4j.Logger;

/**
 * ModIValidazioneSintatticaSoap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AbstractModIValidazioneSintatticaCommons {

	protected Logger log;
	protected ModIProperties modiProperties;
	protected ValidazioneUtils validazioneUtils;
	protected IState state;
	protected Context context;
	public AbstractModIValidazioneSintatticaCommons(Logger log, IState state, Context context, ModIProperties modiProperties, ValidazioneUtils validazioneUtils) {
		this.log = log;
		this.state = state;
		this.context = context;
		this.modiProperties = modiProperties;
		this.validazioneUtils = validazioneUtils;
	}
	
	protected void identificazioneApplicativoMittente(X509Certificate x509, Busta busta) throws Exception {
		try {
			
			IDServizioApplicativo idServizioApplicativo = null;
			
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(this.state);
			
			CertificateInfo certificate = new CertificateInfo(x509, "applicativoMittente");
			String subject = x509.getSubjectX500Principal().toString();
			String issuer = null;
			if(x509.getIssuerX500Principal()!=null) {
				issuer = x509.getIssuerX500Principal().toString();
			}
			
			// NOTA: il fatto di essersi registrati come strict o come non strict è insito nella registrazione dell'applicativo
			
			// 1. Prima si cerca per certificato strict
			if(certificate!=null) {
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate, true);
			}
			if(idServizioApplicativo==null) {
				// 2. Poi per certificato no strict
				if(certificate!=null) {
					idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate, false);
				}	
			}
			if(idServizioApplicativo==null) {
				// 3. per subject/issuer
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject, issuer);	
			}
			if(idServizioApplicativo==null) {
				// 4. solo per subject
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject, null);	
			}
			if(idServizioApplicativo==null) {
				// 5. provare a vedere se si tratta di un applicativo interno (multi-tenant)
				
				if(StatoFunzionalita.ABILITATO.equals(configurazionePdDManager.getConfigurazioneMultitenant().getStato()) &&
						!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazionePdDManager.getConfigurazioneMultitenant().getErogazioneSceltaSoggettiFruitori())) {
					
					FiltroRicercaServiziApplicativi filtro = new FiltroRicercaServiziApplicativi();
					
					filtro.setTipoSoggetto(CostantiLabel.MODIPA_PROTOCOL_NAME);
					
					FiltroRicercaProtocolProperty filtroSubject = new FiltroRicercaProtocolProperty();
					filtroSubject.setName(ModICostanti.MODIPA_KEY_CN_SUBJECT);
					filtroSubject.setValueAsString(subject);
					filtro.addProtocolProperty(filtroSubject);
					
					FiltroRicercaProtocolProperty filtroIssuer = new FiltroRicercaProtocolProperty();
					filtroIssuer.setName(ModICostanti.MODIPA_KEY_CN_ISSUER);
					filtroIssuer.setValueAsString(issuer);
					filtro.addProtocolProperty(filtroIssuer);
				
					List<IDServizioApplicativo> list = null;
					try {
						list = configurazionePdDManager.getAllIdServiziApplicativi(filtro);
					}catch(DriverConfigurazioneNotFound notFound) {}
					if(list!=null) {
						for (IDServizioApplicativo idServizioApplicativoSubjectIssuerCheck : list) {
							// Possono esistere piu' sil che hanno un CN con subject e issuer.
							
							ServizioApplicativo sa = configurazionePdDManager.getServizioApplicativo(idServizioApplicativoSubjectIssuerCheck);
							if(sa!=null && sa.sizeProtocolPropertyList()>0) {
								
								java.security.cert.Certificate certificatoCheck = null;
								
								String mode = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_MODE);
								if(mode!=null && !"".equals(mode) && !ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE.equals(mode)) {
									
									byte [] certificateBytes = ProtocolPropertiesUtils.getOptionalBinaryValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_CERTIFICATE);
									if(certificateBytes!=null) {
										org.openspcoop2.utils.certificate.Certificate c = ArchiveLoader.load(certificateBytes);
										if(c!=null && c.getCertificate()!=null) {
											certificatoCheck = c.getCertificate().getCertificate();
										}
									}
									
								}
								else {
								
									byte [] keystoreBytes = ProtocolPropertiesUtils.getOptionalBinaryValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_ARCHIVE);
									String keystoreType = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_TYPE);
									String keystorePassword = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEYSTORE_PASSWORD);
									String keyAlias = ProtocolPropertiesUtils.getOptionalStringValuePropertyConfig(sa.getProtocolPropertyList(), ModICostanti.MODIPA_KEY_ALIAS);
									if(keystoreBytes!=null && keystoreType!=null && keystorePassword!=null && keyAlias!=null) {
										ArchiveType archiveType = null;
										if(ModIConsoleCostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS.equals(keystoreType)) {
											archiveType = ArchiveType.JKS;
										}
										else {
											archiveType = ArchiveType.PKCS12;
										}
										MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(keystoreBytes, archiveType.name(), 
												keystorePassword);
										certificatoCheck = merlinKs.getCertificate(keyAlias);
									}
								}

								if(certificatoCheck!=null) {
									//if(certificate.equals(certificatoCheck.getCertificate(),true)) {
									if(certificatoCheck instanceof java.security.cert.X509Certificate) {
										if(certificate.equals(((java.security.cert.X509Certificate)certificatoCheck),true)) {
											idServizioApplicativo = idServizioApplicativoSubjectIssuerCheck;
											break;
										}
									}
								}
							}
							
						}
					}
				}

			}

			IDSoggetto idSoggettoMittente = null;
			if(busta.getTipoMittente()!=null && busta.getMittente()!=null) {
				idSoggettoMittente = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
			}
			
			if(idServizioApplicativo!=null) {
				if(idSoggettoMittente==null) {
					idSoggettoMittente = idServizioApplicativo.getIdSoggettoProprietario();
				}
				// Non ha senso poter identificare entrambi con le stesse credenziali
				else if(idServizioApplicativo.getIdSoggettoProprietario().equals(idSoggettoMittente)==false) {
					throw new Exception("Token di sicurezza firmato da un applicativo '"+idServizioApplicativo.getNome()+
							"' risiedente nel dominio del soggetto '"+idServizioApplicativo.getIdSoggettoProprietario().toString()+"', dominio differente dal soggetto identificato sul canale di trasporto ("+idSoggettoMittente+
							")");
				}
				
				busta.setTipoMittente(idSoggettoMittente.getTipo());
				busta.setMittente(idSoggettoMittente.getNome());
				busta.setServizioApplicativoFruitore(idServizioApplicativo.getNome());
				
				try {
					if(!this.context.containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE)) {
						RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(this.state);
						Soggetto soggetto = registroServiziManager.getSoggetto(idSoggettoMittente, null);
						Map<String, String> configProperties = registroServiziManager.getProprietaConfigurazione(soggetto);
			            if (configProperties != null && !configProperties.isEmpty()) {
			            	this.context.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE, configProperties);
						}	
					}
				}catch(Throwable t) {}
				try {
					ServizioApplicativo sa = configurazionePdDManager.getServizioApplicativo(idServizioApplicativo);
					Map<String, String> configProperties = configurazionePdDManager.getProprietaConfigurazione(sa);
		            if (configProperties != null && !configProperties.isEmpty()) {
		            	this.context.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO, configProperties);
					}	
				}catch(Throwable t) {}	
			}
			
		}catch(Exception e) {
			this.log.error("Errore durante il riconoscimento dell'applicativo mittente: "+e.getMessage(),e);
			throw e;
		}
	}
}

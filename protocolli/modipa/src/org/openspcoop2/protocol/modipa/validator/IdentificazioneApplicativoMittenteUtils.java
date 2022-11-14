/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaProtocolPropertyConfig;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.GestoreRichieste;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServiziApplicativi;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestFruitore;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.slf4j.Logger;

/**
 * IdentificazioneApplicativoMittenteUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificazioneApplicativoMittenteUtils {

	// invocato in AbstractModIValidazioneSintatticaCommons e di conseguenza in ModIValidazioneSintatticaRest e ModIValidazioneSintatticaSoap durante il trattamento del token di sicurezza
	public static IDServizioApplicativo identificazioneApplicativoMittenteByX509(Logger log, IState state, X509Certificate x509, OpenSPCoop2Message msg, Busta busta, 
			Context context, IProtocolFactory<?> factory, RequestInfo requestInfo) throws Exception {
		try {
			
			/** SICUREZZA MESSAGGIO **/
									
			IDSoggetto idSoggettoMittente = null;
			if(busta.getTipoMittente()!=null && busta.getMittente()!=null) {
				idSoggettoMittente = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
			}
			
			IDServizioApplicativo idServizioApplicativo = null;
			
			if(x509!=null) {
				
				CertificateInfo certificate = new CertificateInfo(x509, "applicativoMittente");
			
				RequestFruitore requestFruitoreToken = null;
				String certificateKey = null;
				try {
					certificateKey = GestoreRichieste.toCertificateKey(certificate);
					if(certificateKey!=null) {
						requestFruitoreToken = GestoreRichieste.readFruitoreTokenModI(requestInfo, certificateKey);
					}
				}catch(Throwable t) {}	
				
				if(certificateKey!=null && requestFruitoreToken!=null && requestFruitoreToken.getCertificateKey()!=null && 
						requestFruitoreToken.getCertificateKey().equals(certificateKey)) {
					idServizioApplicativo = requestFruitoreToken.getIdServizioApplicativoFruitore();
				}
				else {
					idServizioApplicativo = _identificazioneApplicativoMittenteByX509(state, requestInfo, certificate);
				}
				
				if(idServizioApplicativo!=null) {
					if(idSoggettoMittente==null) {
						idSoggettoMittente = idServizioApplicativo.getIdSoggettoProprietario();
					}
					// Non ha senso poter identificare entrambi con le stesse credenziali
					else if(idServizioApplicativo.getIdSoggettoProprietario().equals(idSoggettoMittente)==false) {
						String msgError = ModIUtils.getMessaggioErroreDominioCanaleDifferenteDominioApplicativo(idServizioApplicativo, idSoggettoMittente);
						throw new Exception(msgError);
					}
				}
				
				if(certificateKey!=null && requestFruitoreToken==null && idServizioApplicativo!=null) {
					
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
					RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(state);					
					
					RequestFruitore rf = new RequestFruitore(); 
					
					rf.setIdSoggettoFruitore(idServizioApplicativo.getIdSoggettoProprietario());
					try {
						org.openspcoop2.core.registry.Soggetto soggettoRegistry = registroServiziManager.getSoggetto(idServizioApplicativo.getIdSoggettoProprietario(), null, requestInfo); 
						rf.setSoggettoFruitoreRegistry(soggettoRegistry);
					}catch(Throwable t) {
						log.error("Errore durante la lettura del soggetto '"+idServizioApplicativo.getIdSoggettoProprietario()+"' dal registro: "+t.getMessage(),t);
					}	
					try {
						org.openspcoop2.core.config.Soggetto soggettoConfig = configurazionePdDManager.getSoggetto(idServizioApplicativo.getIdSoggettoProprietario(), requestInfo); 
						rf.setSoggettoFruitoreConfig(soggettoConfig);
					}catch(Throwable t) {
						log.error("Errore durante la lettura del soggetto '"+idServizioApplicativo.getIdSoggettoProprietario()+"' dalla configurazione: "+t.getMessage(),t);
					}	
					try {
						String idPorta = configurazionePdDManager.getIdentificativoPorta(idServizioApplicativo.getIdSoggettoProprietario(), factory, requestInfo);
						rf.setSoggettoFruitoreIdentificativoPorta(idPorta);
					}catch(Throwable t) {
						log.error("Errore durante la lettura dell'identificativo porta del soggetto '"+idServizioApplicativo.getIdSoggettoProprietario()+"' dal registro: "+t.getMessage(),t);
					}	
					try {
						boolean soggettoVirtualeFRU = configurazionePdDManager.isSoggettoVirtuale(idServizioApplicativo.getIdSoggettoProprietario(), requestInfo);
						rf.setSoggettoFruitoreSoggettoVirtuale(soggettoVirtualeFRU);
					}catch(Throwable t) {
						log.error("Errore durante la lettura dell'indicazione sul soggetto virtuale associato al soggetto '"+idServizioApplicativo.getIdSoggettoProprietario()+"' dal registro: "+t.getMessage(),t);
					}	
					try {
						if(rf!=null) {
							if(rf.getSoggettoFruitoreRegistry().getPortaDominio()!=null &&
									StringUtils.isNotEmpty(rf.getSoggettoFruitoreRegistry().getPortaDominio())) {
								PortaDominio pdd = registroServiziManager.getPortaDominio(rf.getSoggettoFruitoreRegistry().getPortaDominio(), null, requestInfo);
								rf.setSoggettoFruitorePddReaded(true);
								rf.setSoggettoFruitorePdd(pdd);
							}
							else {
								rf.setSoggettoFruitorePddReaded(true);
							}
						}
					}catch(Throwable t) {
						log.error("Errore durante la lettura dei dati della pdd del soggetto '"+idServizioApplicativo.getIdSoggettoProprietario()+"' dal registro: "+t.getMessage(),t);
					}	
					try {
						String impl = registroServiziManager.getImplementazionePdD(idServizioApplicativo.getIdSoggettoProprietario(), null, requestInfo);
						rf.setSoggettoFruitoreImplementazionePdd(impl);
					}catch(Throwable t) {
						log.error("Errore durante la lettura dell'implementazione pdd del soggetto '"+idServizioApplicativo.getIdSoggettoProprietario()+"' dal registro: "+t.getMessage(),t);
					}	
						
					rf.setIdServizioApplicativoFruitore(idServizioApplicativo);
					try {
						org.openspcoop2.core.config.ServizioApplicativo sa = configurazionePdDManager.getServizioApplicativo(idServizioApplicativo, requestInfo);
						rf.setServizioApplicativoFruitore(sa);
					}catch(Throwable t) {
						log.error("Errore durante la lettura del servizio applicativo '"+idServizioApplicativo+"' dalla configurazione: "+t.getMessage(),t);
					}
					// nel token non ha senso rf.setServizioApplicativoFruitoreAnonimo(saApplicativoToken==null);
					
					try {
						rf.setCertificateKey(certificateKey);
						GestoreRichieste.saveRequestFruitoreTokenModI(requestInfo, rf);
					} catch (Throwable e) {
						log.error("Errore durante il salvataggio nella cache e nel thread context delle informazioni sul fruitore token ModI: "+e.getMessage(),e);
					}
					
				}
			}
			
	    	/** IMPOSTAZIONI */
	    	
	    	setInformazioniInBustaAndContext(log, state, busta, context, requestInfo,
	    			idSoggettoMittente, idServizioApplicativo);
			
	    	return idServizioApplicativo;
	    	
		}catch(Exception e) {
			log.error("Errore durante il riconoscimento dell'applicativo mittente (tramite la sicurezza messaggio): "+e.getMessage(),e);
			throw e;
		}
	}
			
	private static IDServizioApplicativo _identificazioneApplicativoMittenteByX509(IState state, RequestInfo requestInfo, CertificateInfo certificate) throws Exception {
			
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
								
		String subject = null;
		if(certificate!=null && certificate.getCertificate()!=null) {
			subject = certificate.getCertificate().getSubjectX500Principal().toString();
		}
		String issuer = null;
		if(certificate!=null && certificate.getCertificate()!=null && certificate.getCertificate().getIssuerX500Principal()!=null) {
			issuer = certificate.getCertificate().getIssuerX500Principal().toString();
		}
		
		IDServizioApplicativo idServizioApplicativo = null;
		
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
				
				ProtocolFiltroRicercaServiziApplicativi filtro = createFilter(subject, issuer);
								
				List<IDServizioApplicativo> list = null;
				try {
					list = configurazionePdDManager.getAllIdServiziApplicativi(filtro);
				}catch(DriverConfigurazioneNotFound notFound) {}
				if(list!=null) {
					for (IDServizioApplicativo idServizioApplicativoSubjectIssuerCheck : list) {
						// Possono esistere piu' sil che hanno un CN con subject e issuer.
						
						ServizioApplicativo sa = configurazionePdDManager.getServizioApplicativo(idServizioApplicativoSubjectIssuerCheck, requestInfo);
							
						java.security.cert.Certificate certificatoCheck = readServizioApplicativoByCertificate(sa, requestInfo);

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
			
		return idServizioApplicativo;
	}
			
			
	public static IDServizioApplicativo identificazioneApplicativoMittenteByToken(Logger log, IState state, Busta busta, Context context, RequestInfo requestInfo, StringBuilder errorDetails) throws Exception {
		try {	
			
			// letto tramite la sicurezza messaggio nella validazione sintattica
			
			IDSoggetto idSoggettoMittente = null;
			if(busta.getTipoMittente()!=null && busta.getMittente()!=null) {
				idSoggettoMittente = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
			}
			
			IDServizioApplicativo idServizioApplicativo = null;
			if(busta.getServizioApplicativoFruitore()!=null && idSoggettoMittente!=null) {
				idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setNome(busta.getServizioApplicativoFruitore());
				idServizioApplicativo.setIdSoggettoProprietario(idSoggettoMittente);
			}

			
			/** SICUREZZA TOKEN **/
			
	    	IDServizioApplicativo idServizioApplicativoToken = null;
	    	if(context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN)) {
	    		idServizioApplicativoToken = (IDServizioApplicativo) context.getObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN);
	    		
	    		if(idSoggettoMittente==null) {
					idSoggettoMittente = idServizioApplicativoToken.getIdSoggettoProprietario();
				}
				// Non ha senso poter identificare entrambi con le stesse credenziali
				else if(idServizioApplicativoToken.getIdSoggettoProprietario().equals(idSoggettoMittente)==false) {
					String msgError = ModIUtils.getMessaggioErroreDominioCanaleDifferenteDominioApplicativo(idServizioApplicativoToken, idSoggettoMittente);
					errorDetails.append(msgError);
					throw new Exception(msgError);
				}
	    		
	    		if(idServizioApplicativo!=null) {
	    			// già identificato anche in token di sicurezza messaggio
	    			// DEPRECATO: non può succedere, poichè se vi è attivo una autenticazione token, viene usata quella per identificare l'applicativo mittente
	    			// questo caso quindi non può succedere, si lascia il codice per controllo di robustezza
	    			if(!idServizioApplicativo.equals(idServizioApplicativoToken)) {
	    				String msgError = "Rilevati due token di sicurezza firmati da applicativi differenti: '"+idServizioApplicativo.getNome()+
								"' e '"+idServizioApplicativoToken.getNome()+"'";
	    				errorDetails.append(msgError);
						throw new Exception(msgError);
	    			}
	    		}
	    		else {
	    			idServizioApplicativo = idServizioApplicativoToken;
	    		}
	    	}
	    	else {
	    		return null;
	    	}
	    	
	    	
	    	/** IMPOSTAZIONI */
	    	
	    	setInformazioniInBustaAndContext(log, state, busta, context, requestInfo,
	    			idSoggettoMittente, idServizioApplicativo);
	    	
	    	return idServizioApplicativo;
			
		}catch(Exception e) {
			log.error("Errore durante il riconoscimento dell'applicativo mittente (tramite il token): "+e.getMessage(),e);
			throw e;
		}
	}
	  
	
	public static void checkApplicativoTokenByX509(Logger log, IDServizioApplicativo idServizioApplicativo, 
			IState state, RequestInfo requestInfo, 
			String tipoToken, CertificateInfo certificatoSicurezzaMessaggio, StringBuilder errorDetails) throws Exception {
		try {
			
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
			
			ServizioApplicativo saToken = configurazionePdDManager.getServizioApplicativo(idServizioApplicativo, requestInfo);
			
			String cnSubjectSicurezzaMessaggio = certificatoSicurezzaMessaggio.getSubject().getCN();
			String subjectSicurezzaMessaggio = certificatoSicurezzaMessaggio.getSubject().toString();
			String issuerSicurezzaMessaggio = null;
			if(certificatoSicurezzaMessaggio.getIssuer()!=null) {
				issuerSicurezzaMessaggio = certificatoSicurezzaMessaggio.getIssuer().toString();
			}
			
			boolean registered = false;
			if(saToken.getInvocazionePorta()!=null){
				for(int z=0;z<saToken.getInvocazionePorta().sizeCredenzialiList();z++){
					
					if(saToken.getInvocazionePorta().getCredenziali(z).getTipo() == null // default
							||
							CostantiConfigurazione.CREDENZIALE_SSL.equals(saToken.getInvocazionePorta().getCredenziali(z).getTipo())){
						
						Credenziali c = saToken.getInvocazionePorta().getCredenziali(z);
						
						// 1. Prima si cerca per certificato strict
						// 2. Poi per certificato no strict
						if(c.getCertificate()!=null) {
							if(cnSubjectSicurezzaMessaggio.equals(c.getCnSubject())) {
								Certificate cer = ArchiveLoader.load(ArchiveType.CER, c.getCertificate(), 0, null);
								boolean strict = true;
								if(certificatoSicurezzaMessaggio.equals(cer.getCertificate(),strict)) {
									registered=true;
									break;
								}
								if(certificatoSicurezzaMessaggio.equals(cer.getCertificate(),!strict)) {
									registered=true;
									break;
								}
							}
						}
						
						// 3. per subject/issuer
						// 4. solo per subject
						if(c.getSubject()!=null) {
						
							boolean subjectValid = CertificateUtils.sslVerify(c.getSubject(), subjectSicurezzaMessaggio, PrincipalType.subject, log);
							boolean issuerValid = true;
							if(subjectValid) {
								if(c.getIssuer()!=null) {
									if(issuerSicurezzaMessaggio!=null) {
										issuerValid = CertificateUtils.sslVerify(c.getIssuer(), issuerSicurezzaMessaggio, PrincipalType.issuer, log);
									}
								}
							}
							
							if(subjectValid && issuerValid){
								registered=true;
								break;
							}
						}
												
					}
					
				}
			}
			
			// 5. provare a vedere se si tratta di un applicativo interno (multi-tenant)
			
			if(!registered) {
				if(StatoFunzionalita.ABILITATO.equals(configurazionePdDManager.getConfigurazioneMultitenant().getStato()) &&
						!PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI.equals(configurazionePdDManager.getConfigurazioneMultitenant().getErogazioneSceltaSoggettiFruitori())) {
						
					java.security.cert.Certificate certificatoCheck = readServizioApplicativoByCertificate(saToken, requestInfo);

					if(certificatoCheck!=null) {
						//if(certificate.equals(certificatoCheck.getCertificate(),true)) {
						if(certificatoCheck instanceof java.security.cert.X509Certificate) {
							if(certificatoSicurezzaMessaggio.equals(((java.security.cert.X509Certificate)certificatoCheck),true)) {
								registered=true;
							}
						}
					}
					
				}
			}
			
			if(!registered) {
				
				StringBuilder sb = new StringBuilder();
				sb.append("subject=\"").append(subjectSicurezzaMessaggio).append("\"");
				if(issuerSicurezzaMessaggio!=null) {
					sb.append(" issuer=\"").append(issuerSicurezzaMessaggio).append("\"");
				}
				
				String idApp = idServizioApplicativo.getNome() + " (Soggetto: "+idServizioApplicativo.getIdSoggettoProprietario().getNome()+")";
				String msgError = "Applicativo Mittente "+idApp+" non autorizzato; il certificato di firma ("+sb.toString()+") del security token ("+tipoToken+") non corrisponde all'applicativo";
				errorDetails.append(msgError);
				throw new Exception(msgError);
			}
	    	
		}catch(Exception e) {
			log.error("Errore durante la verifica del certificato di firma associato all'applicativo token: "+e.getMessage(),e);
			throw e;
		}
	}
	
	
	
	public static void setInformazioniInBustaAndContext(Logger log, IState state, Busta busta, Context context, RequestInfo requestInfo,
			IDSoggetto idSoggettoMittente, IDServizioApplicativo idServizioApplicativo) throws Exception {
		try {
						
	    	if(idSoggettoMittente!=null) {
	    		busta.setTipoMittente(idSoggettoMittente.getTipo());
	    		busta.setMittente(idSoggettoMittente.getNome());
	    		
				try {
					if(!context.containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE)) {
						RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(state);
						Soggetto soggetto = registroServiziManager.getSoggetto(idSoggettoMittente, null, requestInfo);
						Map<String, String> configProperties = registroServiziManager.getProprietaConfigurazione(soggetto);
			            if (configProperties != null && !configProperties.isEmpty()) {
			            	context.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE, configProperties);
						}	
					}
				}catch(Throwable t) {}
	    	}
	    	
		}catch(Exception e) {
			log.error("Errore durante l'impostazione nel contest del soggetto mittente: "+e.getMessage(),e);
			throw e;
		}
		
		try {
	    	
	    	if(idServizioApplicativo!=null) {
				busta.setServizioApplicativoFruitore(idServizioApplicativo.getNome());
				
				try {
					if(!context.containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO)) {
						ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
						ServizioApplicativo sa = configurazionePdDManager.getServizioApplicativo(idServizioApplicativo, requestInfo);
						Map<String, String> configProperties = configurazionePdDManager.getProprietaConfigurazione(sa);
			            if (configProperties != null && !configProperties.isEmpty()) {
			            	context.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO, configProperties);
						}	
					}
				}catch(Throwable t) {}	
			}
			
		}catch(Exception e) {
			log.error("Errore durante l'impostazione nel contest dell'applicativo mittente: "+e.getMessage(),e);
			throw e;
		}
	}
	
	
	
	
	public static ProtocolFiltroRicercaServiziApplicativi createFilter(String subject, String issuer) {
		ProtocolFiltroRicercaServiziApplicativi filtro = new ProtocolFiltroRicercaServiziApplicativi();
		
		filtro.setTipoSoggetto(CostantiLabel.MODIPA_PROTOCOL_NAME);
		
		FiltroRicercaProtocolPropertyConfig filtroSubject = new FiltroRicercaProtocolPropertyConfig();
		filtroSubject.setName(ModICostanti.MODIPA_KEY_CN_SUBJECT);
		filtroSubject.setValueAsString(subject);
		filtro.addProtocolProperty(filtroSubject);
		
		FiltroRicercaProtocolPropertyConfig filtroIssuer = new FiltroRicercaProtocolPropertyConfig();
		filtroIssuer.setName(ModICostanti.MODIPA_KEY_CN_ISSUER);
		filtroIssuer.setValueAsString(issuer);
		filtro.addProtocolProperty(filtroIssuer);
		
		return filtro;
	}
	
	public static java.security.cert.Certificate readServizioApplicativoByCertificate(ServizioApplicativo sa, RequestInfo requestInfo) throws Exception {
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
					MerlinKeystore merlinKs = GestoreKeystoreCache.getMerlinKeystore(requestInfo, keystoreBytes, archiveType.name(), 
							keystorePassword);
					certificatoCheck = merlinKs.getCertificate(keyAlias);
				}
			}

			return certificatoCheck;
		}
			
		return null;
	}
}

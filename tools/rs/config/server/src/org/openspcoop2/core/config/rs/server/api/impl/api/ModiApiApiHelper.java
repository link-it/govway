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
package org.openspcoop2.core.config.rs.server.api.impl.api;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiModI;
import org.openspcoop2.core.config.rs.server.model.ApiModIAzioneSoap;
import org.openspcoop2.core.config.rs.server.model.ApiModIPatternInterazioneRest;
import org.openspcoop2.core.config.rs.server.model.ApiModIRisorsaRest;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaCanale;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioApplicabilitaCustom;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioOperazione;
import org.openspcoop2.core.config.rs.server.model.ApiModISicurezzaMessaggioOperazioneRidefinito;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.ModIPatternInterazioneEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaCanaleEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioApplicabilitaCustomEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioApplicabilitaEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioOperazioneEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestHeaderEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

/**
 * ApiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ModiApiApiHelper {



	public static ApiModIAzioneSoap getApiAzioneModI(AccordoServizioParteComune as, Operation az, ProfiloEnum profilo,
			ApiEnv env) {

		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			return null; //TODO generalizzare 
		}


		Map<String, AbstractProperty<?>> p = new HashMap<>();
		try{
			IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			ConsoleConfiguration consoleConf = ApiApiHelper.getConsoleConfiguration(env, idAccordoFromAccordo);

			ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
			ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, as.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

			for(int i =0; i < prop.sizeProperties(); i++) {
				p.put(prop.getIdProperty(i), prop.getProperty(i));
			}

			ApiModIAzioneSoap apimodi = new ApiModIAzioneSoap();
			if(p!= null) {

				//TODO interazione tipo funz azione


				if(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, true)) {
					ApiModISicurezzaMessaggioOperazione op = new ApiModISicurezzaMessaggioOperazione();
					op.setStato(ModISicurezzaMessaggioOperazioneEnum.API);
					apimodi.setSicurezzaMessaggio(op);
				} else {

					ApiModISicurezzaMessaggioOperazioneRidefinito rid = new ApiModISicurezzaMessaggioOperazioneRidefinito();
					rid.setStato(ModISicurezzaMessaggioOperazioneEnum.RIDEFINITO);

					ApiModISicurezzaMessaggio conf = new ApiModISicurezzaMessaggio();
					ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

					String sicurezzaMessaggioApplicabilitaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

					if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
						conf.setSoapFirmaAllegati(true);
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
						conf.setSoapFirmaAllegati(true);
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
						conf.setSoapFirmaAllegati(true);
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
					} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA)) {
						applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
					}
					conf.setApplicabilita(applicabilita);
					conf.setDigestRichiesta(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, true));
					conf.setInformazioniUtente(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, true));

					rid.setConfigurazione(conf);
					apimodi.setSicurezzaMessaggio(rid);
				}

			}

			return apimodi;

		}catch(Exception e){
			throw FaultCode.ERRORE_INTERNO.toException(e.getMessage());
		}


	}


	public static ApiModIRisorsaRest getApiRisorsaModI(AccordoServizioParteComune as, Resource res, ProfiloEnum profilo, ApiEnv env) throws Exception {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			return null; //TODO generalizzare 
		}


		Map<String, AbstractProperty<?>> p = new HashMap<>();
		IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
		IDResource idres = new IDResource();
		idres.setIdAccordo(idAccordoFromAccordo);
		idres.setNome(res.getNome());
		ConsoleConfiguration consoleConf = ApiApiHelper.getConsoleConfiguration(env, idres, res.getMethod() != null ? res.getMethod().toString() : null, res.getPath());

		ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
		ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, res.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

		for(int i =0; i < prop.sizeProperties(); i++) {
			p.put(prop.getIdProperty(i), prop.getProperty(i));
		}

		ApiModIRisorsaRest apimodi = new ApiModIRisorsaRest();
		if(p!= null) {

			//TODO tipo funz azione

			ApiModIPatternInterazioneRest interazione = new ApiModIPatternInterazioneRest();

			if(getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD)) {
				interazione.setPattern(ModIPatternInterazioneEnum.CRUD);
			} else if(getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE)) {
				interazione.setPattern(ModIPatternInterazioneEnum.BLOCCANTE);
			} else if(getStringProperty(p, ModICostanti.MODIPA_PROFILO_INTERAZIONE, true).equals(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE)) {
				interazione.setPattern(ModIPatternInterazioneEnum.NON_BLOCCANTE);
			}
			
			apimodi.setInterazione(interazione);
			

			if(getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, true).equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
				ApiModISicurezzaMessaggioOperazione op = new ApiModISicurezzaMessaggioOperazione();
				op.setStato(ModISicurezzaMessaggioOperazioneEnum.API);
				apimodi.setSicurezzaMessaggio(op);
			} else {

				ApiModISicurezzaMessaggioOperazioneRidefinito rid = new ApiModISicurezzaMessaggioOperazioneRidefinito();
				rid.setStato(ModISicurezzaMessaggioOperazioneEnum.RIDEFINITO);

				ApiModISicurezzaMessaggio conf = new ApiModISicurezzaMessaggio();

				ModISicurezzaMessaggioRestHeaderEnum restHeader = null;
				String restHeaderString = getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, true);

				if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA)) {
					restHeader = ModISicurezzaMessaggioRestHeaderEnum.AGID;
				} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION)) {
					restHeader = ModISicurezzaMessaggioRestHeaderEnum.BEARER;
				}
				conf.setRestHeader(restHeader);

				ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

				String sicurezzaMessaggioApplicabilitaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

				if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI)) {
					applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
				} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA)) {
					applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
				} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA)) {
					applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
				} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO)) {
					applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM;

					ApiModISicurezzaMessaggioApplicabilitaCustom appCustom = new ApiModISicurezzaMessaggioApplicabilitaCustom();
					appCustom.setApplicabilita(applicabilita);


					String applicabilitaCustomRichiestaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, true);
					ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRichiesta = null;

					if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO)) {
						applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
					} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO)) {
						applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
					} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO)) {
						applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;
						appCustom.setRichiestaContentType(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID);
					} 

					appCustom.setRichiesta(applicabilitaCustomRichiesta);


					String applicabilitaCustomRispostaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, true);
					ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRisposta = null;

					if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO)) {
						applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
					} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO)) {
						applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
					} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO)) {
						applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;

						appCustom.setRispostaContentType(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID);
						appCustom.setRispostaCodice(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID);
					} 

					appCustom.setRisposta(applicabilitaCustomRisposta);

					conf.setApplicabilitaCustom(appCustom);
				}

				conf.setApplicabilita(applicabilita);

				conf.setDigestRichiesta(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, true));
				conf.setInformazioniUtente(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, true));

				rid.setConfigurazione(conf);
				apimodi.setSicurezzaMessaggio(rid);
			}

		}

		return apimodi;

	}

	public static ApiModI getApiModI(AccordoServizioParteComune as, ProfiloEnum profilo, ApiEnv env) {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		Map<String, AbstractProperty<?>> p = new HashMap<>();
		try{
			IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			ConsoleConfiguration consoleConf = ApiApiHelper.getConsoleConfiguration(env, idAccordoFromAccordo);

			ProtocolProperties prop = env.apcHelper.estraiProtocolPropertiesDaRequest(consoleConf, ConsoleOperationType.CHANGE);
			ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(prop, as.getProtocolPropertyList(), ConsoleOperationType.CHANGE);

			for(int i =0; i < prop.sizeProperties(); i++) {
				p.put(prop.getIdProperty(i), prop.getProperty(i));
			}


			ApiModI apimodi = new ApiModI();
			if(p!= null) {
				ApiModISicurezzaCanale sicurezzaCanale = new ApiModISicurezzaCanale();

				String sicurezzaCanalePatternString = getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE, true);

				sicurezzaCanale.setPattern(sicurezzaCanalePatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01) ? ModISicurezzaCanaleEnum.AUTH01:ModISicurezzaCanaleEnum.AUTH02);
				apimodi.setSicurezzaCanale(sicurezzaCanale);

				ApiModISicurezzaMessaggio sicurezzaMessaggio = new ApiModISicurezzaMessaggio();

				String sicurezzaMessaggioPatternString = getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, true);

				ModISicurezzaMessaggioEnum profiloSicurezzaMessaggioPattern = null;
				if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.AUTH01;
				} else if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.AUTH02;
				} else if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.DISABILITATO;
				} else if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH01;
				} else if(sicurezzaMessaggioPatternString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302)) {
					profiloSicurezzaMessaggioPattern = ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH02;
				}
				sicurezzaMessaggio.setPattern(profiloSicurezzaMessaggioPattern);

				if(!profiloSicurezzaMessaggioPattern.equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

					boolean isSoap = as.getServiceBinding().equals(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP);

					if(isSoap) {
						ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

						String sicurezzaMessaggioApplicabilitaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

						if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
							sicurezzaMessaggio.setSoapFirmaAllegati(true);
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
							sicurezzaMessaggio.setSoapFirmaAllegati(true);
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
							sicurezzaMessaggio.setSoapFirmaAllegati(true);
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
						}
						sicurezzaMessaggio.setApplicabilita(applicabilita);
					} else {

						ModISicurezzaMessaggioRestHeaderEnum restHeader = null;
						String restHeaderString = getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, true);

						if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA)) {
							restHeader = ModISicurezzaMessaggioRestHeaderEnum.AGID;
						} else if(restHeaderString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION)) {
							restHeader = ModISicurezzaMessaggioRestHeaderEnum.BEARER;
						}
						sicurezzaMessaggio.setRestHeader(restHeader);

						ModISicurezzaMessaggioApplicabilitaEnum applicabilita = null;

						String sicurezzaMessaggioApplicabilitaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, true);

						if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.QUALSIASI;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RICHIESTA;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.RISPOSTA;
						} else if(sicurezzaMessaggioApplicabilitaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO)) {
							applicabilita = ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM;

							ApiModISicurezzaMessaggioApplicabilitaCustom appCustom = new ApiModISicurezzaMessaggioApplicabilitaCustom();
							appCustom.setApplicabilita(applicabilita);


							String applicabilitaCustomRichiestaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, true);
							ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRichiesta = null;

							if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO)) {
								applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
							} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO)) {
								applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
							} else if(applicabilitaCustomRichiestaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO)) {
								applicabilitaCustomRichiesta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;
								appCustom.setRichiestaContentType(getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, true));
							} 

							appCustom.setRichiesta(applicabilitaCustomRichiesta);


							String applicabilitaCustomRispostaString = getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, true);
							ModISicurezzaMessaggioApplicabilitaCustomEnum applicabilitaCustomRisposta = null;

							if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO)) {
								applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.ABILITATO;
							} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO)) {
								applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.DISABILITATO;
							} else if(applicabilitaCustomRispostaString.equals(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO)) {
								applicabilitaCustomRisposta = ModISicurezzaMessaggioApplicabilitaCustomEnum.CUSTOM;

								appCustom.setRispostaContentType(getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, true));
								appCustom.setRispostaCodice(getStringProperty(p, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, true));
							} 

							appCustom.setRisposta(applicabilitaCustomRisposta);

							sicurezzaMessaggio.setApplicabilitaCustom(appCustom);
						}

						sicurezzaMessaggio.setApplicabilita(applicabilita);


					}

					sicurezzaMessaggio.setDigestRichiesta(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, true));
					sicurezzaMessaggio.setInformazioniUtente(getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, true));

				}

				apimodi.setSicurezzaMessaggio(sicurezzaMessaggio);

			}

			return apimodi;

		}catch(Exception e){
			throw FaultCode.ERRORE_INTERNO.toException(e.getMessage());
		}
	}


	private static Boolean getBooleanProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {
		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop instanceof BooleanProperty) {
			return ((BooleanProperty)prop).getValue();
		} else {
			throw new Exception("Property "+key+" non e' una Boolean:" + prop.getClass().getName());
		}
	}


	private static String getStringProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {

		AbstractProperty<?> prop = getProperty(p, key, required);
		if(prop instanceof StringProperty) {
			return ((StringProperty)prop).getValue();
		} else {
			throw new Exception("Property "+key+" non e' una StringProperty:" + prop.getClass().getName());
		}
	}

	private static AbstractProperty<?> getProperty(Map<String, AbstractProperty<?>> p, String key, boolean required) throws Exception {
		if(p.containsKey(key)) {
			return p.get(key);
		} else {
			if(required) {
				throw new Exception("Property "+key+" non trovata");
			} else {
				return null;
			}
		}
	}

	public static ProtocolProperties getProtocolProperties(ApiAzione body) {
		return getSOAPModiProtocolProperties(body.getModi());
	}
	

	public static ProtocolProperties getProtocolProperties(ApiRisorsa body) {
		return getRESTModiProtocolProperties(body.getModi());
	}

	public static ProtocolProperties getRESTModiProtocolProperties(ApiModIRisorsaRest modi) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		String profiloSicurezzaMessaggio = null;

		switch(modi.getInterazione().getPattern()) {
		case BLOCCANTE:  p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);
			break;
		case CRUD: p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD);
		break;
		case NON_BLOCCANTE:  p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE);
			break;
		default:
			break;

		}
		
		//TODO interazione tipo funz azione
		
		if(modi.getSicurezzaMessaggio().getStato().equals(ModISicurezzaMessaggioOperazioneEnum.API)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
			ApiModISicurezzaMessaggioOperazioneRidefinito rid = ((ApiModISicurezzaMessaggioOperazioneRidefinito)modi.getSicurezzaMessaggio());

			switch(rid.getConfigurazione().getPattern()) {
			case AUTH01: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01; 
			break;
			case AUTH02: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02;
			break;
			case DISABILITATO: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED;
			break;
			case INTEGRITY01_AUTH01: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301;
			break;
			case INTEGRITY01_AUTH02: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302;
			break;}


			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, profiloSicurezzaMessaggio);

			if(!rid.getConfigurazione().getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {
				getRESTProperties(rid.getConfigurazione(), p);
			}
		}

		return p;
	}


	public static ProtocolProperties getSOAPModiProtocolProperties(ApiModIAzioneSoap modi) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		switch(modi.getInterazione().getPattern()) {
		case BLOCCANTE:  p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);
			break;
		case CRUD: p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD);
		break;
		case NON_BLOCCANTE:  p.addProperty(ModICostanti.MODIPA_PROFILO_INTERAZIONE, ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE);
			break;
		default:
			break;

		}
		
		//TODO interazione tipo funz azione

		if(modi.getSicurezzaMessaggio().getStato().equals(ModISicurezzaMessaggioOperazioneEnum.API)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
		} else {
			ApiModISicurezzaMessaggioOperazioneRidefinito rid = ((ApiModISicurezzaMessaggioOperazioneRidefinito)modi.getSicurezzaMessaggio());
			getSOAPProperties(rid.getConfigurazione(), p);

		}

		return p;
	}

	public static ProtocolProperties getProtocolProperties(Api body) {
		return getModiProtocolProperties(body.getModi(), body.getTipoInterfaccia().getProtocollo());
	}

	public static ProtocolProperties updateModiProtocolProperties(AccordoServizioParteComune as, ProfiloEnum profilo, ApiModI modi) {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		TipoApiEnum protocollo = as.getFormatoSpecifica().equals(FormatoSpecifica.WSDL_11) ? TipoApiEnum.SOAP: TipoApiEnum.REST;
		return getModiProtocolProperties(modi, protocollo);

	}

	private static ProtocolProperties getModiProtocolProperties(ApiModI modi, TipoApiEnum protocollo) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		String chan = modi.getSicurezzaCanale().getPattern().equals(ModISicurezzaCanaleEnum.AUTH01) ? ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01 : ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02;
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE, chan);

		String profiloSicurezzaMessaggio = null;

		switch(modi.getSicurezzaMessaggio().getPattern()) {
		case AUTH01: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01; 
		break;
		case AUTH02: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02;
		break;
		case DISABILITATO: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED;
		break;
		case INTEGRITY01_AUTH01: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301;
		break;
		case INTEGRITY01_AUTH02: profiloSicurezzaMessaggio = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302;
		break;}


		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, profiloSicurezzaMessaggio);


		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties(modi.getSicurezzaMessaggio(), p);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties(modi.getSicurezzaMessaggio(), p);
		}


		return p;
	}


	private static void getRESTProperties(ApiModISicurezzaMessaggio sicurezzaMessaggio, ProtocolProperties p) {
		boolean cornicePropValue = false;
		boolean digestPropValue = false;


		if(sicurezzaMessaggio.isSoapFirmaAllegati()!= null && 
				sicurezzaMessaggio.isSoapFirmaAllegati()) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.soap_firma_allegati specificato con servizio di tipo REST");
		}
		
		if(!sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

			if(sicurezzaMessaggio.getRestHeader() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.rest_header deve essere specificato con servizio di tipo REST e pattern " + sicurezzaMessaggio.getPattern());
			}

			boolean integrity = sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH01) || 
					sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH02);


			boolean applicabilitaInfoUtente = false;
			boolean applicabilitaDigest = false;


			String headerHTTPREST = "";
			switch(sicurezzaMessaggio.getRestHeader()) {
			case AGID: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA;
			break;
			case BEARER: headerHTTPREST = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION;
			break;}


			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, headerHTTPREST);


			String applicabilita = "";


			if(sicurezzaMessaggio.getApplicabilita().equals(ModISicurezzaMessaggioApplicabilitaEnum.CUSTOM)) {

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO);

				if(sicurezzaMessaggio.getApplicabilitaCustom() == null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom deve essere specificato con servizio di tipo REST e applicabilita " + sicurezzaMessaggio.getApplicabilita());
				}

				ApiModISicurezzaMessaggioApplicabilitaCustom appCustom = sicurezzaMessaggio.getApplicabilitaCustom();

				String sicurezzaRichiesta = "";
				boolean requireContentTypeRequest = false;
				switch(appCustom.getRichiesta()) {
				case ABILITATO: sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO;
				break;
				case CUSTOM:sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO; requireContentTypeRequest = true;
				break;
				case DISABILITATO:sicurezzaRichiesta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO;
				break;}

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, sicurezzaRichiesta);

				if(appCustom.getRichiestaContentType()!=null) {
					if(!requireContentTypeRequest) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.richiesta_content_type non deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta " + appCustom.getRichiesta());
					}
					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, appCustom.getRichiestaContentType());
				} else {
					if(requireContentTypeRequest) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.richiesta_content_type deve essere specificato con servizio di tipo REST e applicabilita_custom.richiesta " + appCustom.getRichiesta());
					}
				}

				String sicurezzaRisposta = "";
				boolean requireContentTypeAndReturnCodeResponse = false;
				switch(appCustom.getRisposta()) {
				case ABILITATO: sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO;
				break;
				case CUSTOM:sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO; requireContentTypeAndReturnCodeResponse = true;
				break;
				case DISABILITATO:sicurezzaRisposta = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO;
				break;}

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, sicurezzaRisposta);


				if(appCustom.getRispostaContentType()!=null && appCustom.getRispostaCodice()!=null) {
					if(!requireContentTypeAndReturnCodeResponse) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice non devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta " + appCustom.getRisposta());
					}
					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, appCustom.getRispostaContentType());
					p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, appCustom.getRispostaCodice());
				} else {
					if(requireContentTypeAndReturnCodeResponse) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom.risposta_content_type e sicurezza_messaggio.applicabilita_custom.risposta_codice devono essere specificati con servizio di tipo REST e applicabilita_custom.risposta " + appCustom.getRisposta());
					}
				}

				applicabilitaDigest = true;
				applicabilitaInfoUtente= true;
				
				if((sicurezzaMessaggio.isInformazioniUtente() != null && sicurezzaMessaggio.isInformazioniUtente())) {

					if(integrity && applicabilitaInfoUtente) {
						cornicePropValue =  sicurezzaMessaggio.isInformazioniUtente();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
					}
				}

				if((sicurezzaMessaggio.isDigestRichiesta() != null && sicurezzaMessaggio.isDigestRichiesta())) {

					if(integrity && applicabilitaDigest) {
						digestPropValue = sicurezzaMessaggio.isDigestRichiesta();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
					}
				}

			} else {

				if(sicurezzaMessaggio.getApplicabilitaCustom() != null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita_custom non deve essere specificato con servizio di tipo REST e applicabilita " + sicurezzaMessaggio.getApplicabilita());
				}

				switch(sicurezzaMessaggio.getApplicabilita()) {
				case CUSTOM: //gestito nell'altro ramo if
					break;
				case QUALSIASI: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI; applicabilitaDigest = true; applicabilitaInfoUtente= true;
				break;
				case RICHIESTA: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA; applicabilitaInfoUtente= true;
				break;
				case RISPOSTA: applicabilita = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
				break;
				}

				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, applicabilita);
				
				if((sicurezzaMessaggio.isInformazioniUtente() != null && sicurezzaMessaggio.isInformazioniUtente())) {

					if(integrity && applicabilitaInfoUtente) {
						cornicePropValue =  sicurezzaMessaggio.isInformazioniUtente();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
					}
				}

				if((sicurezzaMessaggio.isDigestRichiesta() != null && sicurezzaMessaggio.isDigestRichiesta())) {

					if(integrity && applicabilitaDigest) {
						digestPropValue = sicurezzaMessaggio.isDigestRichiesta();
					} else {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
					}
				}

			}
			p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, digestPropValue));
			p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, cornicePropValue));

		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, "");

			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, "");
		}
	}


	private static void getSOAPProperties(ApiModISicurezzaMessaggio sicurezzaMessaggio, ProtocolProperties p) {
		boolean cornicePropValue = false;
		boolean digestPropValue = false;

		if(sicurezzaMessaggio.getRestHeader() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.rest_header specificato con servizio di tipo SOAP");
		}


		if(!sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.DISABILITATO)) {

			String applicabilita = "";

			boolean integrity = sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH01) || 
					sicurezzaMessaggio.getPattern().equals(ModISicurezzaMessaggioEnum.INTEGRITY01_AUTH02);

			if(sicurezzaMessaggio.isSoapFirmaAllegati() && !integrity) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.soap_firma_allegati specificato con pattern " + sicurezzaMessaggio.getPattern());
			}

			boolean applicabilitaInfoUtente = false;
			boolean applicabilitaDigest = false;

			if(sicurezzaMessaggio.getApplicabilita()!=null) {
				switch(sicurezzaMessaggio.getApplicabilita()) {
				case CUSTOM: throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.applicabilita custom specificato con servizio di tipo SOAP");
				case QUALSIASI: applicabilita = sicurezzaMessaggio.isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI; applicabilitaInfoUtente = true; applicabilitaDigest = true;
				break;
				case RICHIESTA: applicabilita = sicurezzaMessaggio.isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA; applicabilitaInfoUtente = true;
				break;
				case RISPOSTA: applicabilita = sicurezzaMessaggio.isSoapFirmaAllegati() ? ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS : ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
				break;
				}
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, applicabilita);
			} else {
				p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_DEFAULT);
				applicabilitaInfoUtente = true;
				applicabilitaDigest = true;
			}

			if((sicurezzaMessaggio.isInformazioniUtente() != null && sicurezzaMessaggio.isInformazioniUtente())) {

				if(integrity && applicabilitaInfoUtente) {
					cornicePropValue =  sicurezzaMessaggio.isInformazioniUtente();
				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.informazioni_utente specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
				}
			}

			if((sicurezzaMessaggio.isDigestRichiesta() != null && sicurezzaMessaggio.isDigestRichiesta())) {

				if(integrity && applicabilitaDigest) {
					digestPropValue = sicurezzaMessaggio.isDigestRichiesta();
				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("sicurezza_messaggio.digest_richiesta specificato con pattern " + sicurezzaMessaggio.getPattern() + " o applicabilita " + sicurezzaMessaggio.getApplicabilita());
				}
			}

			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, "");
		} else {
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE, "");

			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID, "");
			p.addProperty(ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID, "");
		}
		
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, digestPropValue));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, cornicePropValue));
	}
}

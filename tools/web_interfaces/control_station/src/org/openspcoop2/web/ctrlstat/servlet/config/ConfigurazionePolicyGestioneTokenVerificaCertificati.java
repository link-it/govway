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

package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.web.ctrlstat.core.CertificateChecker;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;
import org.openspcoop2.web.lib.mvc.properties.utils.ReadPropertiesUtilities;

/**
 * ConfigurazionePolicyGestioneTokenVerificaCertificati
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazionePolicyGestioneTokenVerificaCertificati extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			String id = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_ID);
			String infoType = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			if(infoType==null) {
				infoType = ServletUtils.getObjectFromSession(request, session, String.class, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			}
			boolean attributeAuthority = ConfigurazioneCostanti.isConfigurazioneAttributeAuthority(infoType);
			
			String alias = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			
			// Preparo il menu
			confHelper.makeMenu();
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			String verificaCertificatiFromLista = confHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			boolean arrivoDaLista = "true".equalsIgnoreCase(verificaCertificatiFromLista);
			
			// Prendo la lista di aliases
			List<String> aliases = confCore.getJmxPdD_aliases();
			if(aliases==null || aliases.size()<=0){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			
			GenericProperties genericProperties = confCore.getGenericProperties(Long.parseLong(id));
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = attributeAuthority ? 
					confCore.getAttributeAuthorityPropertiesSourceConfiguration() :
					confCore.getPolicyGestioneTokenPropertiesSourceConfiguration();
			
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);

			String verificaConnettivitaS = confHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTIVITA);
			boolean verificaConnettivita = "true".equalsIgnoreCase(verificaConnettivitaS);
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			String label = attributeAuthority ?
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY :
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN;
			
			lstParam.add(new Parameter(label, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST));
			
			if(arrivoDaLista) {
				String labelVerifica = 
						(verificaConnettivita ? ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CONNETTIVITA_DI : ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI_DI)
								+ genericProperties.getNome();
				lstParam.add(new Parameter(labelVerifica, null));
			}
			else {
				Parameter pPolicyId = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_ID, genericProperties.getId() + "");
				Parameter pInfoType = new Parameter(ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE, infoType); 
				
				lstParam.add(new Parameter(genericProperties.getNome(), 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_CHANGE, 
						pInfoType, pPolicyId));
				
				String labelVerifica = (verificaConnettivita ? ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CONNETTIVITA : ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI);
				lstParam.add(new Parameter(labelVerifica, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam );
			
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			// -- raccolgo dati
						
			boolean validazione = false;
			boolean negoziazione = false;
			if(!attributeAuthority) {
				validazione = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN.equals(genericProperties.getTipologia());
				negoziazione = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN.equals(genericProperties.getTipologia());
			}
			
			boolean httpsIntrospection = false;
			boolean httpsUserInfo = false;
			boolean validazioneJwt = false;
			boolean forwardToJwt = false;
			boolean https = false;
			boolean signedJwt = false;
			boolean jwtRichiesta = false;
			boolean jwtRisposta = false;
			
			boolean verificaCertificatiPossibile = false;
			
			if(!verificaConnettivita) {
				if(attributeAuthority) {
					PolicyAttributeAuthority policy = AttributeAuthorityUtilities.convertTo(genericProperties);
					https = policy.isEndpointHttps();
					if(policy.isRequestJws()) {
						KeystoreParams keystoreParams = AttributeAuthorityUtilities.getRequestJwsKeystoreParams(policy);
						if(keystoreParams!=null && !"jwk".equalsIgnoreCase(keystoreParams.getType())) {
							jwtRichiesta = true;
						}
					}
					if(policy.isResponseJws()) {
						KeystoreParams keystoreParams = AttributeAuthorityUtilities.getResponseJwsKeystoreParams(policy);
						if(keystoreParams!=null && !"jwk".equalsIgnoreCase(keystoreParams.getType())) {
							jwtRisposta = true;
						}
					}
					verificaCertificatiPossibile = https || jwtRichiesta || jwtRisposta;
				}
				else {
					if(validazione) {
						GestioneToken gestioneToken = new GestioneToken();
						gestioneToken.setIntrospection(StatoFunzionalitaConWarning.ABILITATO);
						gestioneToken.setUserInfo(StatoFunzionalitaConWarning.ABILITATO);
						gestioneToken.setValidazione(StatoFunzionalitaConWarning.ABILITATO);
						gestioneToken.setForward(StatoFunzionalita.ABILITATO);
						PolicyGestioneToken policyGestioneToken = TokenUtilities.convertTo(genericProperties, gestioneToken);
						if(policyGestioneToken.isIntrospection()) {
							httpsIntrospection = policyGestioneToken.isEndpointHttps();
						}
						if(policyGestioneToken.isUserInfo()) {
							httpsUserInfo = policyGestioneToken.isEndpointHttps();
						}
						if(policyGestioneToken.isValidazioneJWT()) {
							String tokenType = policyGestioneToken.getTipoToken();
							KeystoreParams keystoreParams = null;
							if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType) || 
									org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_TYPE_JWE.equals(tokenType)) {
				    			keystoreParams = TokenUtilities.getValidazioneJwtKeystoreParams(policyGestioneToken);
				    		}
							if(keystoreParams!=null && !"jwk".equalsIgnoreCase(keystoreParams.getType())) {
								validazioneJwt = true;
							}
						}
						if(policyGestioneToken.isForwardToken() && policyGestioneToken.isForwardToken_informazioniRaccolte()) {
							String forwardInformazioniRaccolteMode = policyGestioneToken.getForwardToken_informazioniRaccolteMode();
							KeystoreParams keystoreParams = null;
							if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInformazioniRaccolteMode) ||
									org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInformazioniRaccolteMode) ||
									org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInformazioniRaccolteMode)) {
				    			keystoreParams = TokenUtilities.getForwardToJwtKeystoreParams(policyGestioneToken);
				    		}
							if(keystoreParams!=null && !"jwk".equalsIgnoreCase(keystoreParams.getType())) {
								forwardToJwt = true;
							}
						}	
						verificaCertificatiPossibile = httpsIntrospection || httpsUserInfo || validazioneJwt || forwardToJwt;
					}
					else if(negoziazione) {
						PolicyNegoziazioneToken policy = TokenUtilities.convertTo(genericProperties);
						https = policy.isEndpointHttps();
						if(policy.isRfc7523_x509_Grant()) {
							KeystoreParams keystoreParams = TokenUtilities.getSignedJwtKeystoreParams(policy);
							if(keystoreParams!=null && !"jwk".equalsIgnoreCase(keystoreParams.getType())) {
								signedJwt = true;
							}
						}
						verificaCertificatiPossibile = https || signedJwt;
					}
					else {
						throw new Exception("Tipologia '"+genericProperties.getTipologia()+"' non gestita");
					}
				}
			}
			
			boolean verificaCertificatiEffettuata = false;
			
			

			
			if(!verificaConnettivita && !verificaCertificatiPossibile) {
				pd.setMessage(CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_NON_PRESENTI,
							Costanti.MESSAGE_TYPE_INFO);
				
				pd.disableEditMode();
				
				verificaCertificatiEffettuata = true;
			}
			else {
				
				boolean sceltaClusterId = true;
				if(!verificaConnettivita) {
					sceltaClusterId = confCore.isVerificaCertificati_sceltaClusterId();
				}
				
				if(aliases.size()==1 || alias!=null || !sceltaClusterId) {
					
					if(alias==null && !sceltaClusterId) {
						alias = CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI;
					}
			
					// -- verifica						
			
					if(verificaConnettivita) {
						confHelper.addDescrizioneVerificaConnettivitaToDati(dati, genericProperties, null, false, 
								(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) ? aliases.get(0) : (alias!=null ? alias : aliases.get(0))
								);
						
						if (!confHelper.isEditModeInProgress()) {

							List<String> aliases_for_check = new ArrayList<>();
							if(aliases.size()==1) {
								aliases_for_check.add(aliases.get(0));
							}
							else if(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) {
								aliases_for_check.addAll(aliases);
							}
							else {
								aliases_for_check.add(alias);
							}
														
							boolean rilevatoErrore = false;
							String messagePerOperazioneEffettuata = "";
							int index = 0;
							for (String aliasForVerificaConnettore : aliases_for_check) {
								
								String risorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(aliasForVerificaConnettore);

								StringBuilder bfExternal = new StringBuilder();
								String descrizione = confCore.getJmxPdD_descrizione(aliasForVerificaConnettore);
								if(aliases.size()>1) {
									if(index>0) {
										bfExternal.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
									}
									bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}						
								try{
									String nomeMetodo = null;
									if(attributeAuthority) {
										nomeMetodo = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreAttributeAuthority(aliasForVerificaConnettore);
									}
									else if(validazione) {
										nomeMetodo = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreTokenPolicyValidazione(aliasForVerificaConnettore);
									}
									else if(negoziazione) {
										nomeMetodo = confCore.getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreTokenPolicyNegoziazione(aliasForVerificaConnettore);
									}
									Boolean slowOperation = true; // altrimenti un eventuale connection timeout (es. 10 secondi) termina dopo il readTimeout associato all'invocazione dell'operazione via http check e quindi viene erroneamenteo ritornato un readTimeout
									String stato = confCore.getInvoker().invokeJMXMethod(aliasForVerificaConnettore, confCore.getJmxPdD_configurazioneSistema_type(aliasForVerificaConnettore),
											risorsa, 
											nomeMetodo, 
											slowOperation,
											genericProperties.getNome());
									if(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO.equals(stato)){
										bfExternal.append(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_EFFETTUATO_CON_SUCCESSO);
									}
									else{
										rilevatoErrore = true;
										bfExternal.append(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_FALLITA);
										if(stato.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
											bfExternal.append(stato.substring(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA.length()));
										}
										else {
											bfExternal.append(stato);
										}
									}
								}catch(Exception e){
									ControlStationCore.logError("Errore durante la verifica del connettore (jmxResource '"+risorsa+"') (node:"+aliasForVerificaConnettore+"): "+e.getMessage(),e);
									rilevatoErrore = true;
									String stato = e.getMessage();
									bfExternal.append(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_FALLITA);
									if(stato.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
										bfExternal.append(stato.substring(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA.length()));
									}
									else {
										bfExternal.append(stato);
									}
								}
			
								if(messagePerOperazioneEffettuata.length()>0){
									messagePerOperazioneEffettuata+= org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
								}
								messagePerOperazioneEffettuata+= bfExternal.toString();
								
								index++;
							}
							if(messagePerOperazioneEffettuata!=null){
								if(rilevatoErrore)
									pd.setMessage(messagePerOperazioneEffettuata);
								else 
									pd.setMessage(messagePerOperazioneEffettuata,Costanti.MESSAGE_TYPE_INFO);
							}
			
							pd.disableEditMode();
						}
					}
					else {
					
						List<String> aliases_for_check = new ArrayList<>();
						boolean all = false;
						if(aliases.size()==1) {
							aliases_for_check.add(aliases.get(0));
						}
						else if(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) {
							aliases_for_check.addAll(aliases);
							all = true;
						}
						else {
							aliases_for_check.add(alias);
						}
						
						CertificateChecker certificateChecker = null;
						if(all) {
							certificateChecker = confCore.getJmxPdD_certificateChecker();
						}
						else {
							certificateChecker = confCore.newJmxPdD_certificateChecker(aliases_for_check);
						}
						StringBuilder sbDetailsError = new StringBuilder(); 
						
						int sogliaWarningGiorni = confCore.getVerificaCertificati_warning_expirationDays();
						
						String labelPolicy = genericProperties.getNome();
						
						String posizioneErrore = null;
						String extraErrore = null;
						
						// verifica
						StringBuilder sbDetailsWarning_policy = new StringBuilder();
						String posizioneWarning_policy = null;
						if(attributeAuthority) {
							certificateChecker.checkAttributeAuthority(sbDetailsError, sbDetailsWarning_policy,
								https, jwtRichiesta, jwtRisposta,
								genericProperties,
								sogliaWarningGiorni);
						}
						else if(validazione) {
							certificateChecker.checkTokenPolicyValidazione(sbDetailsError, sbDetailsWarning_policy,
								httpsIntrospection, httpsUserInfo, validazioneJwt, forwardToJwt,
								genericProperties,
								sogliaWarningGiorni);
						}
						else if(negoziazione) {
							certificateChecker.checkTokenPolicyNegoziazione(sbDetailsError, sbDetailsWarning_policy,
									https, signedJwt,
									genericProperties,
									sogliaWarningGiorni);
						}
						if(sbDetailsError.length()>0) {
							posizioneErrore = labelPolicy;
						}
						else if(sbDetailsWarning_policy.length()>0) {
							posizioneWarning_policy = labelPolicy;
						}
						
						
						// analisi warning
						String warning = null;
						String posizioneWarning = null;
						String extraWarning = null;
						if(sbDetailsError.length()<=0) {
							if(sbDetailsWarning_policy.length()>0) {
								warning = sbDetailsWarning_policy.toString();
								posizioneWarning = posizioneWarning_policy;
							}
						}
						
						// esito
						List<String> formatIds = new ArrayList<String>();
						formatIds.add(RegistroServiziReader.ID_CONFIGURAZIONE_CONNETTORE_HTTPS);
						formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_TOKEN_VALIDAZIONE_JWT);
						formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_TOKEN_VALIDAZIONE_FORWARD_TO_JWT);
						formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_TOKEN_NEGOZIAZIONE_SIGNED_JWT);
						formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_JWT_RICHIESTA);
						formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_JWT_RISPOSTA);
						confCore.formatVerificaCertificatiEsito(pd, formatIds, 
								(sbDetailsError.length()>0 ? sbDetailsError.toString() : null), extraErrore, posizioneErrore,
								warning, extraWarning, posizioneWarning,
								false);
								
						pd.disableEditMode();
						
						verificaCertificatiEffettuata = true;

					}
					
				}
				
				else {
				
					DataElement deTestConnettivita = new DataElement();
					deTestConnettivita.setType(DataElementType.TITLE);
					if(verificaConnettivita) {
						deTestConnettivita.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CONNETTIVITA);
					}
					else {
						deTestConnettivita.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI);
					}
					dati.add(deTestConnettivita);
					
					confHelper.addVerificaCertificatoSceltaAlias(aliases, dati);
					
				}
			}
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);

			dati = confHelper.addTokenPolicyHiddenToDati(dati, id, infoType);

			DataElement	de = new DataElement();
			de.setValue(arrivoDaLista+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			dati.addElement(de);
			
			de = new DataElement();
			de.setValue(verificaConnettivita+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTIVITA);
			dati.addElement(de);
			
			pd.setDati(dati);
			
			if(verificaCertificatiEffettuata) {
				
				// verifica richiesta dal link nella lista, torno alla lista
				if(arrivoDaLista) {
					
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

					String infoTypeA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
					String infoTypeSession = ServletUtils.getObjectFromSession(request, session, String.class, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
					if(infoTypeA==null) {
						infoTypeA = infoTypeSession;
					}
					else {
						ServletUtils.setObjectIntoSession(request, session, infoTypeA, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
					}
					attributeAuthority = ConfigurazioneCostanti.isConfigurazioneAttributeAuthority(infoTypeA);
					
					// reset di eventuali configurazioni salvate in sessione
					Properties mapId = attributeAuthority ?
							confCore.getAttributeAuthorityTipologia() :
							confCore.getTokenPolicyTipologia();
					if(mapId!=null && !mapId.isEmpty()) {
						propertiesSourceConfiguration = attributeAuthority ? 
								confCore.getAttributeAuthorityPropertiesSourceConfiguration() :
								confCore.getPolicyGestioneTokenPropertiesSourceConfiguration();
						configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
						for (Object oTipo : mapId.keySet()) {
							if(oTipo!=null && oTipo instanceof String) {
								String ti = (String) oTipo;
								Config config = configManager.getConfigurazione(propertiesSourceConfiguration, ti);
								ServletUtils.removeConfigurazioneBeanFromSession(request, session, config.getId());
							}
						}
					}
					
					int idLista = attributeAuthority ? Liste.CONFIGURAZIONE_GESTIONE_ATTRIBUTE_AUTHORITY : Liste.CONFIGURAZIONE_GESTIONE_POLICY_TOKEN;
					
					ricerca = confHelper.checkSearchParameters(idLista, ricerca);

					List<String> tipologie = new ArrayList<>();
					if(attributeAuthority) {
						tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_ATTRIBUTE_AUTHORITY);
					}
					else {
						tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN);
						tipologie.add(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN);
					}
					
					List<GenericProperties> lista = confCore.gestorePolicyTokenList(idLista, tipologie, ricerca);
					
					confHelper.prepareGestorePolicyTokenList(ricerca, lista, idLista); 
					
					// salvo l'oggetto ricerca nella sessione
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
					// Forward control to the specified success URI
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI, CostantiControlStation.TIPO_OPERAZIONE_VERIFICA_CERTIFICATI);
					
				}
				// verifica richiesta dal dettaglio, torno al dettaglio
				else { 
				
					ServletUtils.setPageDataTitle(pd, lstParam);
					
					TipoOperazione tipoOperazione = TipoOperazione.CHANGE;
					
					String nome = genericProperties.getNome();
					String descrizione = genericProperties.getDescrizione();
					String tipo = genericProperties.getTipo();
					
					List<String> nomiConfigurazioniPolicyGestioneToken = configManager.getNomiConfigurazioni(propertiesSourceConfiguration);
					List<String> labelConfigurazioniPolicyGestioneToken = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniPolicyGestioneToken);
					
					String[] propConfigPolicyGestioneTokenLabelList = labelConfigurazioniPolicyGestioneToken.toArray(new String[labelConfigurazioniPolicyGestioneToken.size()]);
					String[] propConfigPolicyGestioneTokenList= nomiConfigurazioniPolicyGestioneToken.toArray(new String[nomiConfigurazioniPolicyGestioneToken.size()]);
					
					Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, genericProperties.getTipo()); 
					
					Map<String, Properties> mappaDB = confCore.readGestorePolicyTokenPropertiesConfiguration(genericProperties.getId()); 
					
					ConfigBean configurazioneBean = ReadPropertiesUtilities.leggiConfigurazione(configurazione, mappaDB);
					// non viene supportata la modalità completa. Visualizzo allora solamente il nome
//					confHelper.aggiornaConfigurazioneProperties(configurazioneBean);
//					
//					configurazioneBean.updateConfigurazione(configurazione);
//					ServletUtils.saveConfigurazioneBeanIntoSession(session, configurazioneBean, configurazioneBean.getId());
					
					// preparo i campi
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					dati = confHelper.addPolicyGestioneTokenToDati(tipoOperazione,dati,id,nome,descrizione,tipo,propConfigPolicyGestioneTokenLabelList,propConfigPolicyGestioneTokenList,
							attributeAuthority, genericProperties);
							
					dati = confHelper.addPropertiesConfigToDati(tipoOperazione,dati, tipo, configurazioneBean,false);
					
					pd.setDati(dati);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN, ForwardParams.CHANGE());
					
				}
				
			}
		
			else {

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
			}
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		}  
	}
}

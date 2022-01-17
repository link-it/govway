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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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
				infoType = ServletUtils.getObjectFromSession(session, String.class, ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE);
			}
			boolean attributeAuthority = ConfigurazioneCostanti.isConfigurazioneAttributeAuthority(infoType);
			
			String alias = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			
			// Preparo il menu
			confHelper.makeMenu();
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
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

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			String label = attributeAuthority ?
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY :
					ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN;
			
			lstParam.add(new Parameter(label, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST));
			String labelVerifica = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI_DI + genericProperties.getNome();
			lstParam.add(new Parameter(labelVerifica, null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam );
			
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			DataElement deTestConnettivita = new DataElement();
			deTestConnettivita.setType(DataElementType.TITLE);
			deTestConnettivita.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI);
			dati.add(deTestConnettivita);
			
			if(aliases.size()==1 || alias!=null) {
				confHelper.addDescrizioneVerificaCertificatoToDati(dati, genericProperties, attributeAuthority, null, true, 
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
						
						// TODO Poli rimuovere
						boolean connettoreRegistro = false;
						long idConnettore = 1; 
						
						String risorsa = null;
						if(connettoreRegistro) {
							risorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi(aliasForVerificaConnettore);
						}
						else {
							risorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(aliasForVerificaConnettore);
						}
						
						StringBuilder bfExternal = new StringBuilder();
						String descrizione = confCore.getJmxPdD_descrizione(aliasForVerificaConnettore);
						if(aliases.size()>1) {
							if(index>0) {
								bfExternal.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						}						
						try{
							String stato = confCore.getInvoker().invokeJMXMethod(aliasForVerificaConnettore, confCore.getJmxPdD_configurazioneSistema_type(aliasForVerificaConnettore),
									risorsa, 
									confCore.getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById(aliasForVerificaConnettore), 
									idConnettore+"");
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
				
			} else {
				confHelper.addVerificaCertificatoSceltaAlias(aliases, dati);
			}
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);

			dati = confHelper.addTokenPolicyHiddenToDati(dati, id, infoType);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		}  
	}
}

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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/***
 * 
 * PorteApplicativeVerificaConnettore
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateVerificaConnettore extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String id = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			
			String idTab = porteDelegateHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);
			ConnettoriCore connettoriCore = new ConnettoriCore(porteDelegateCore);

			String tmpIdConnettore = porteDelegateHelper.getParametroLong(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID);
			long idConnettore = Long.parseLong(tmpIdConnettore);
			
			String tmpAccessoDaGruppi = porteDelegateHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			boolean accessoDaGruppi = "true".equalsIgnoreCase(tmpAccessoDaGruppi);
			
			String tmpConnettoreRegistro = porteDelegateHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			boolean connettoreRegistro = "true".equalsIgnoreCase(tmpConnettoreRegistro);

			String alias = porteDelegateHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
						
			// Prendo la lista di aliases
			List<String> aliases = confCore.getJmxPdDAliases();
			if(aliases==null || aliases.isEmpty()){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			
			
			org.openspcoop2.core.config.Connettore connettore = null;
			if(connettoreRegistro) {
				connettore = connettoriCore.getConnettoreRegistro(idConnettore).mappingIntoConnettoreConfigurazione();
			}
			else {
				connettore = connettoriCore.getConnettoreConfig(idConnettore);
			}
			String labelConnettore = porteDelegateHelper.getLabelConnettore(connettore,false,false);
			
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();

			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				if(!accessoDaGruppi) {
					lstParam.remove(lstParam.size()-1);
				}
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VERIFICA_CONNETTORE_CONFIGURAZIONE_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VERIFICA_CONNETTORE_CONFIGURAZIONE,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VERIFICA_CONNETTORE_CONFIGURAZIONE_CONFIG_DI+idporta;
			}
			
			if(!accessoDaGruppi &&
				labelPerPorta.contains(CostantiControlStation.LABEL_DEL_GRUPPO)) {
				labelPerPorta = labelPerPorta.substring(0, labelPerPorta.indexOf(CostantiControlStation.LABEL_DEL_GRUPPO));
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));

			
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// imposto menu' contestuale
			porteDelegateHelper.impostaComandiMenuContestualePD(idSoggFruitore, idAsps, idFruizione);
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			DataElement deTestConnettivita = new DataElement();
			deTestConnettivita.setType(DataElementType.TITLE);
			deTestConnettivita.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VERIFICA_CONNETTORE_TITLE);
			dati.add(deTestConnettivita);
			
			if(aliases.size()==1 || alias!=null) {

				porteDelegateHelper.addDescrizioneVerificaConnettoreToDati(dati, null, labelConnettore, connettore, true, 
						(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) ? aliases.get(0) : (alias!=null ? alias : aliases.get(0))
						);
				
				if (!porteDelegateHelper.isEditModeInProgress()) {
				
					List<String> aliasesForCheck = new ArrayList<>();
					if(aliases.size()==1) {
						aliasesForCheck.add(aliases.get(0));
					}
					else if(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) {
						aliasesForCheck.addAll(aliases);
					}
					else {
						aliasesForCheck.add(alias);
					}
									
					boolean rilevatoErrore = false;
					String messagePerOperazioneEffettuata = "";
					int index = 0;
					for (String aliasForVerificaConnettore : aliasesForCheck) {
						
						String risorsa = null;
						if(connettoreRegistro) {
							risorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi(aliasForVerificaConnettore);
						}
						else {
							risorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(aliasForVerificaConnettore);
						}
						
						StringBuilder bfExternal = new StringBuilder();
						String descrizione = confCore.getJmxPdDDescrizione(aliasForVerificaConnettore);
						if(aliases.size()>1) {
							if(index>0) {
								bfExternal.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						}						
						try{
							Boolean slowOperation = true; // altrimenti un eventuale connection timeout (es. 10 secondi) termina dopo il readTimeout associato all'invocazione dell'operazione via http check e quindi viene erroneamenteo ritornato un readTimeout
							String stato = confCore.getInvoker().invokeJMXMethod(aliasForVerificaConnettore, confCore.getJmxPdDConfigurazioneSistemaType(aliasForVerificaConnettore),
									risorsa, 
									confCore.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById(aliasForVerificaConnettore),
									slowOperation,
									idConnettore+"");
							if(
									JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO.equals(stato)
									||
									(stato!=null && stato.startsWith(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX))
								){
								bfExternal.append(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_EFFETTUATO_CON_SUCCESSO);
							}
							else{
								rilevatoErrore = true;
								bfExternal.append(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_FALLITA);
								if(stato!=null) {
									if(stato.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
										bfExternal.append(stato.substring(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA.length()));
									}
									else {
										bfExternal.append(stato);
									}
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
				porteDelegateHelper.addVerificaConnettoreSceltaAlias(aliases, dati);			
			}
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);
			
			porteDelegateHelper.addVerificaConnettoreHidden(dati,
					id, idSoggFruitore, idAsps, idFruizione,
					idConnettore, accessoDaGruppi, connettoreRegistro);
			
			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
					idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VERIFICA_CONNETTORE,	ForwardParams.OTHER(""));
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VERIFICA_CONNETTORE, ForwardParams.OTHER(""));
		}
	}

}

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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneProxyPassRegolaChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneCanaliNodiChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			// Preparo il menu
			confHelper.makeMenu();
			
			String idNodoS = confHelper.getParametroLong(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_ID_NODO);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_NOME);
			String descrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_DESCRIZIONE);
			String [] canali = confHelper.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NODI_CANALI);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			Configurazione configurazioneGenerale = confCore.getConfigurazioneGenerale();
			CanaliConfigurazione gestioneCanali = configurazioneGenerale.getGestioneCanali();
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			
			long idNodo = Long.parseLong(idNodoS);
			CanaleConfigurazioneNodo oldNodo = null;
			String oldNome = null;
			if(configurazioneGenerale.getGestioneCanali() != null) {
				for (CanaleConfigurazioneNodo nodo : configurazioneGenerale.getGestioneCanali().getNodoList()) {
					if(nodo.getId().longValue() == idNodo) {
						oldNodo = nodo;
						oldNome = nodo.getNome();
						break;
					}
				}
			}
			
			List<Parameter> lstParam = new ArrayList<>();
			
			// setto la barra del titolo
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALI_NODI, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CANALI_NODI_LIST));
			if(oldNodo!=null) {
				lstParam.add(new Parameter(oldNodo.getNome(), null));
			}
			ServletUtils.setPageDataTitle(pd, lstParam);

			List<String> aliases = confCore.getJmxPdDAliases();
			boolean selectListNode = false;
			if(aliases!=null && aliases.size()>1){
				selectListNode = true;
			}
			if(selectListNode) {
				if(gestioneCanali.sizeNodoList()>0) {
					for (CanaleConfigurazioneNodo confNodo : gestioneCanali.getNodoList()) {
						if(!oldNome.equals(confNodo.getNome()) && aliases.contains(confNodo.getNome())) {
							aliases.remove(confNodo.getNome());
						}
					}
				}
				if(aliases.isEmpty()) {
					pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALI_NODI_TUTTI_REGISTRATI, MessageType.INFO);
					
					pd.disableEditMode();

					List<DataElement> dati = new ArrayList<>();

					dati.add(ServletUtils.getDataElementForEditModeFinished());

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI_NODI, 
							ForwardParams.CHANGE());
				}
			}
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				
				if(nome == null) {
					nome = oldNodo.getNome();
					descrizione = oldNodo.getDescrizione();
					canali = oldNodo.getCanaleList().toArray(new String[oldNodo.sizeCanaleList()]);
				}
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addCanaleNodoToDati(TipoOperazione.CHANGE, dati, idNodoS, oldNome, nome, descrizione, canali, canaleList,
						selectListNode, aliases);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI_NODI, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.canaleNodoCheckData(TipoOperazione.CHANGE, oldNodo.getNome());
			if (!isOk) {

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addCanaleNodoToDati(TipoOperazione.CHANGE, dati, idNodoS, oldNome, nome, descrizione, canali, canaleList,
						selectListNode, aliases);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI_NODI, 
						ForwardParams.CHANGE());
			}

			// rileggo la configurazione
			configurazioneGenerale = confCore.getConfigurazioneGenerale();
			
			if(configurazioneGenerale.getGestioneCanali() == null) // non dovrebbe mai essere null...
				configurazioneGenerale.setGestioneCanali(new CanaliConfigurazione());
			
			// salvataggio regola
			for (CanaleConfigurazioneNodo nodo : configurazioneGenerale.getGestioneCanali().getNodoList()) {
				if(nodo.getId().longValue() == idNodo) {
					nodo.setNome(nome);
					if(descrizione!=null && !"".equals(descrizione)) {
						nodo.setDescrizione(descrizione);
					}
					else {
						nodo.setDescrizione(null);
					}
					
					nodo.getCanaleList().clear();
					for(String canale : canali) {
						nodo.getCanaleList().add(canale);
					}
					
					break;
				}
			}
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneGenerale);
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_CANALI_NODI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);
			
			List<CanaleConfigurazioneNodo> lista = confCore.canaleNodoConfigurazioneList(ricerca); 
			
			confHelper.prepareCanaleNodoConfigurazioneList(ricerca, lista);

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_SISTEMA_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI_NODI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CANALI_NODI, ForwardParams.CHANGE());
		}
	}


}

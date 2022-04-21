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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.PluginProprietaCompatibilita;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazionePluginsClassiAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazionePluginsClassiAdd extends Action {

	

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
			
			String idPluginS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_ID_PLUGIN);
			String descrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_DESCRIZIONE);
			String stato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_STATO);
			String tipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_TIPO);
			String tipoPluginS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_TIPO_PLUGIN);
			TipoPlugin tipoPlugin = null;
			if(tipoPluginS != null) {
				tipoPlugin = TipoPlugin.toEnumConstant(tipoPluginS);
			}
			String label = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_LABEL);
			String className = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_CLASS_NAME);

			String ruolo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO);
			
			String shTipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_SERVICE_HANDLER);
			String mhTipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_FASE_MESSAGE_HANDLER);
			String mhRuolo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_RUOLO_MESSAGE_HANDLER);
			
			String applicabilita = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_FILTRO_APPLICABILITA);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();
			List<Parameter> lstParam = new ArrayList<Parameter>();
			
			String postBackElementName = confHelper.getPostBackElementName();
			
			// se ho modificato il tipo plugin possono apparire delle info da inserire
			if (postBackElementName != null) {
				if(postBackElementName.equals(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_CLASSI_TIPO_PLUGIN)) {
				}
			}

			// setto la barra del titolo
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO_CLASSI, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PLUGINS_CLASSI_LIST));
			lstParam.add(ServletUtils.getParameterAggiungi());
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				
				if(stato == null) {
					tipoPlugin = ConfigurazionePluginsTipoPluginUtils.getTipoPluginDefault();
					if(tipoPlugin!=null) {
						tipoPluginS = tipoPlugin.toString();
					}
					descrizione = "";
					stato = StatoFunzionalita.ABILITATO.toString();
					tipo = "";
					label = "";
					className = "";
					ruolo = PluginCostanti.FILTRO_RUOLO_VALORE_EROGAZIONE;
					shTipo = PluginCostanti.FILTRO_SERVICE_HANDLER_VALORE_INIT;
					mhTipo = PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORE_PRE_IN;
					mhRuolo = PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA;
					applicabilita = PluginCostanti.FILTRO_APPLICABILITA_VALORE_CONFIGURAZIONE;
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addPluginClassiToDati(TipoOperazione.ADD, dati, idPluginS, tipoPlugin, tipo, label, className, stato, descrizione, ruolo, shTipo, mhTipo, mhRuolo, applicabilita);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_CLASSI, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.pluginClassiCheckData(TipoOperazione.ADD, null, idPluginS, tipoPlugin, tipo, label, className, stato, descrizione, ruolo, shTipo, mhTipo, mhRuolo, applicabilita);
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addPluginClassiToDati(TipoOperazione.ADD, dati, idPluginS, tipoPlugin, tipo, label, className, stato, descrizione, ruolo, shTipo, mhTipo, mhRuolo, applicabilita);
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_CLASSI, 
						ForwardParams.ADD());
			}

			// salvataggio plugin
			Plugin plugin = new Plugin();
			
			plugin.setLabel(label);
			plugin.setTipo(tipo);
			plugin.setTipoPlugin(tipoPluginS);
			plugin.setDescrizione(descrizione);
			plugin.setStato(stato.equals(StatoFunzionalita.ABILITATO.getValue()) ? true : false);
			plugin.setClassName(className);
			
			List<PluginProprietaCompatibilita> listaProprieta = ConfigurazionePluginsTipoPluginUtils.getApplicabilitaClassePlugin(tipoPlugin, ruolo, shTipo, mhTipo, mhRuolo, applicabilita);
			
			if(listaProprieta.size() > 0) {
				plugin.getPluginProprietaCompatibilitaList().addAll(listaProprieta);
			}
			
			confCore.performCreateOperation(userLogin, confHelper.smista(), plugin);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_PLUGINS_CLASSI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<Plugin> lista = confCore.pluginsClassiList(ricerca); 
			
			confHelper.preparePluginsClassiList(ricerca, lista);
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_CLASSI,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_CLASSI, ForwardParams.ADD());
		}
	}


}

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
import java.util.Date;
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
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazionePluginsArchiviChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazionePluginsArchiviChange extends Action {

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
			
			String idArchivioS = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_ID_ARCHIVIO);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_NOME);
			String oldNome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_OLD_NOME);
			String descrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_DESCRIZIONE);
			String stato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_STATO);
			String sorgente = null;
			BinaryParameter jarArchivio = null;
			String urlArchivio = null;
			String dirArchivio = null;
			String classiPlugin = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN);
			String [] tipoPlugin = confHelper.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_TIPO_PLUGIN);
			
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			int numeroArchivi = confCore.getNumeroArchiviJarRegistroPlugin(oldNome);
			RegistroPlugin oldRegistro = confCore.getDatiRegistroPlugin(oldNome);

			// Preparo il menu
			confHelper.makeMenu();
			List<Parameter> lstParam = new ArrayList<Parameter>();
			
			String postBackElementName = confHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(postBackElementName.equals(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_SORGENTE)) {
				}
				
				if(postBackElementName.equals(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN)) {
				}
			}

			// setto la barra del titolo
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PLUGINS_REGISTRO_ARCHIVI, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_LIST));
			lstParam.add(new Parameter(oldRegistro.getNome(), null));
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				
				if(nome == null) {
					idArchivioS = oldRegistro.getId() + "";
					nome = oldRegistro.getNome();
					descrizione = oldRegistro.getDescrizione();
					stato = oldRegistro.getStato().toString();
					sorgente = null;
					classiPlugin = oldRegistro.getCompatibilitaList() == null || oldRegistro.getCompatibilitaList().isEmpty() ?
							ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_QUALSIASI: 
								ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE;
					if(classiPlugin.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_SELEZIONATE)) {
						tipoPlugin =  oldRegistro.getCompatibilitaList().toArray(new String[oldRegistro.sizeCompatibilitaList()]);
					} else {
						tipoPlugin = null;
					}
					dirArchivio = "";
					urlArchivio = "";
				}
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addRegistroPluginToDati(TipoOperazione.CHANGE, dati, oldNome, idArchivioS, nome, descrizione, stato, sorgente, jarArchivio, dirArchivio, urlArchivio, classiPlugin, tipoPlugin, numeroArchivi);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.registroPluginCheckData(TipoOperazione.CHANGE, oldNome, idArchivioS, nome, descrizione, 
					stato, sorgente, jarArchivio, dirArchivio, urlArchivio, classiPlugin, tipoPlugin);
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addRegistroPluginToDati(TipoOperazione.CHANGE, dati, oldNome, idArchivioS, nome, descrizione, stato, sorgente, jarArchivio, dirArchivio, urlArchivio, classiPlugin, tipoPlugin, numeroArchivi);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI, 
						ForwardParams.CHANGE());
			}

			// rileggo la configurazione
			RegistroPlugin registro = confCore.getDatiRegistroPlugin(oldNome);
			
			registro.setOldNome(oldNome);
			
			registro.setNome(nome);
			if(descrizione!=null && !"".equals(descrizione)) {
				registro.setDescrizione(descrizione);
			}
			else {
				registro.setDescrizione(null);
			}
			if(stato.equals(StatoFunzionalita.ABILITATO.getValue()))
				registro.setStato(StatoFunzionalita.ABILITATO);
			else 
				registro.setStato(StatoFunzionalita.DISABILITATO);
			registro.setData(new Date());
			
			registro.getCompatibilitaList().clear();
			if(!classiPlugin.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_QUALSIASI)) {
				for (String tipo : tipoPlugin) {
					registro.getCompatibilitaList().add(tipo);
				}
			}
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), registro);
			
			// Aggiorno classLoader interno
			confCore.updatePluginClassLoader();
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_PLUGINS_ARCHIVI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);
			
			List<RegistroPlugin> lista = confCore.pluginsArchiviList(ricerca); 
			
			confHelper.preparePluginsArchiviList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI, ForwardParams.CHANGE());
		}
	}


}

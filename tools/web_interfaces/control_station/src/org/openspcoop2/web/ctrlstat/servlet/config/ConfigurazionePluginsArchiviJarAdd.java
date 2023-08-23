/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDBLib;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
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
 * ConfigurazionePluginsArchiviJarAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazionePluginsArchiviJarAdd extends Action {

	

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
			
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_NOME);
			
			String sorgente = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_SORGENTE);
			BinaryParameter jarArchivio = confHelper.getBinaryParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR_ARCHIVIO);
			String urlArchivio = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_URL_ARCHIVIO);
			String dirArchivio = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_DIR_ARCHIVIO);
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();
			List<Parameter> lstParam = new ArrayList<>();
			
			// setto la barra del titolo
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PLUGINS_REGISTRO_ARCHIVI, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_LIST));
			lstParam.add(new Parameter(nome, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_CHANGE,
					new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_OLD_NOME, nome)));
			lstParam.add(ServletUtils.getParameterAggiungi());
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				
				if(sorgente == null) {
					sorgente = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_SORGENTE_JAR;
					dirArchivio = "";
					urlArchivio = "";
				}
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addRegistroPluginJarToDati(TipoOperazione.ADD, dati, true, nome, sorgente, jarArchivio, dirArchivio, urlArchivio);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.registroPluginArchivioCheckData(TipoOperazione.ADD, sorgente, nome, jarArchivio, dirArchivio, urlArchivio, true);
			if (!isOk) {

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addRegistroPluginJarToDati(TipoOperazione.ADD, dati, true, nome, sorgente, jarArchivio, dirArchivio, urlArchivio);
				 
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR, 
						ForwardParams.ADD());
			}

			// rileggo registro
			RegistroPlugin registro = confCore.getDatiRegistroPlugin(nome);
			registro.setData(new Date());
			
			RegistroPluginArchivio registroPluginArchivio = new RegistroPluginArchivio();
			registroPluginArchivio.setNomePlugin(nome);
			registroPluginArchivio.setData(registro.getData());
			
			registroPluginArchivio.setSorgente( DriverConfigurazioneDBLib.getEnumPluginSorgenteArchivio(sorgente));
			
			switch (registroPluginArchivio.getSorgente()) {
			case JAR:
				registroPluginArchivio.setNome(jarArchivio.getFilename());
				registroPluginArchivio.setContenuto(jarArchivio.getValue());
				break;
			case URL:
				registroPluginArchivio.setUrl(urlArchivio);
				/**URL url = new URL(urlArchivio);
				//registroPluginArchivio.setNome(FilenameUtils.getName(url.getPath()));*/
				// Fix: usando l'utility, url differenti che terminano con la stessa foglia vanno in errore di duplicate key
				registroPluginArchivio.setNome(UUIDUtilsGenerator.newUUID());
				break;
			case DIR:
				registroPluginArchivio.setDir(dirArchivio);
				/**registroPluginArchivio.setNome(FilenameUtils.getName(dirArchivio));*/ 
				// Fix: usando l'utility, path differenti che terminano con lo stesso file name vanno in errore di duplicate key
				registroPluginArchivio.setNome(UUIDUtilsGenerator.newUUID());
				break;
			}
			
			confCore.performCreateOperation(userLogin, confHelper.smista(), registroPluginArchivio);
			confCore.performUpdateOperation(userLogin, confHelper.smista(), registro);
			
			// Aggiorno classLoader interno
			confCore.updatePluginClassLoader();
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<RegistroPluginArchivio> lista = confCore.pluginsArchiviJarList(nome, ricerca);  
			
			confHelper.preparePluginsArchiviJarList(nome, ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR, ForwardParams.ADD());
		}
	}


}

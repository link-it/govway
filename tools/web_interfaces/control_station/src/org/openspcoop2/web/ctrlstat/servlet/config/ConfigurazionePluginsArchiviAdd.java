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
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB_LIB;
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
 * ConfigurazionePluginsArchiviAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazionePluginsArchiviAdd extends Action {

	

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
			String descrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_DESCRIZIONE);
			String stato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_STATO);
			String sorgente = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_SORGENTE);
			BinaryParameter jarArchivio = confHelper.getBinaryParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR_ARCHIVIO);
			String urlArchivio = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_URL_ARCHIVIO);
			String dirArchivio = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_DIR_ARCHIVIO);
			String classiPlugin = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN);
			String [] tipoPlugin = confHelper.getParameterValues(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_TIPO_PLUGIN);
			int numeroArchivi = 0;
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();
			List<Parameter> lstParam = new ArrayList<Parameter>();
			
//			String postBackElementName = confHelper.getPostBackElementName();
//			
//			// se ho modificato il soggetto ricalcolo il servizio e il service binding
//			if (postBackElementName != null) {
//				if(postBackElementName.equals(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_SORGENTE)) {
//				}
//				
//				if(postBackElementName.equals(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN)) {
//				}
//			}

			// setto la barra del titolo
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PLUGINS_REGISTRO_ARCHIVI, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_LIST));
			lstParam.add(ServletUtils.getParameterAggiungi());
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				
				if(nome == null) {
					nome = "";
					descrizione = "";
					stato = StatoFunzionalita.ABILITATO.toString();
					sorgente = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_SORGENTE_JAR;
					classiPlugin = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_QUALSIASI;
					tipoPlugin = null;
					dirArchivio = "";
					urlArchivio = "";
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addRegistroPluginToDati(TipoOperazione.ADD, dati, null, idArchivioS, nome, descrizione, stato, sorgente, jarArchivio, dirArchivio, urlArchivio, classiPlugin, tipoPlugin, numeroArchivi);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.registroPluginCheckData(TipoOperazione.ADD, null, idArchivioS, nome, descrizione, 
					stato, sorgente, jarArchivio, dirArchivio, urlArchivio, classiPlugin, tipoPlugin);
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addRegistroPluginToDati(TipoOperazione.ADD, dati, null, idArchivioS, nome, descrizione, stato, sorgente, jarArchivio, dirArchivio, urlArchivio, classiPlugin, tipoPlugin, numeroArchivi);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI, 
						ForwardParams.ADD());
			}

			// calcolo prossima posizione
			int posizione = confCore.getMaxPosizioneRegistroPlugin() + 1;
			
			// salvataggio regola
			RegistroPlugin registro = new RegistroPlugin();
			
			registro.setNome(nome);
			if(stato.equals(StatoFunzionalita.ABILITATO.getValue()))
				registro.setStato(StatoFunzionalita.ABILITATO);
			else 
				registro.setStato(StatoFunzionalita.DISABILITATO);
			
			registro.setPosizione(posizione);
			if(descrizione!=null && !"".equals(descrizione)) {
				registro.setDescrizione(descrizione);
			}
			registro.setData(new Date());
			registro.getCompatibilitaList().clear();
			if(!classiPlugin.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PLUGINS_ARCHIVI_CLASSI_PLUGIN_QUALSIASI)) {
				for (String tipo : tipoPlugin) {
					registro.getCompatibilitaList().add(tipo);
				}
			}
			
			RegistroPluginArchivio registroPluginArchivio = new RegistroPluginArchivio();
			registroPluginArchivio.setNomePlugin(nome);
			registroPluginArchivio.setData(registro.getData());
			
			registroPluginArchivio.setSorgente( DriverConfigurazioneDB_LIB.getEnumPluginSorgenteArchivio(sorgente));
			
			switch (registroPluginArchivio.getSorgente()) {
			case JAR:
				registroPluginArchivio.setNome(jarArchivio.getFilename());
				registroPluginArchivio.setContenuto(jarArchivio.getValue());
				break;
			case URL:
				registroPluginArchivio.setUrl(urlArchivio);
				//URL url = new URL(urlArchivio);
				//registroPluginArchivio.setNome(FilenameUtils.getName(url.getPath()));
				// Fix: usando l'utility, url differenti che terminano con la stessa foglia vanno in errore di duplicate key
				registroPluginArchivio.setNome(UUIDUtilsGenerator.newUUID());
				break;
			case DIR:
				registroPluginArchivio.setDir(dirArchivio);
				//registroPluginArchivio.setNome(FilenameUtils.getName(dirArchivio));
				// Fix: usando l'utility, path differenti che terminano con lo stesso file name vanno in errore di duplicate key
				registroPluginArchivio.setNome(UUIDUtilsGenerator.newUUID());
				break;
			}
			
			registro.getArchivioList().add(registroPluginArchivio );
			
			confCore.performCreateOperation(userLogin, confHelper.smista(), registro);
			
			// Aggiorno classLoader interno
			confCore.updatePluginClassLoader();
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_PLUGINS_ARCHIVI;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<RegistroPlugin> lista = confCore.pluginsArchiviList(ricerca); 
			
			confHelper.preparePluginsArchiviList(ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI, ForwardParams.ADD());
		}
	}


}

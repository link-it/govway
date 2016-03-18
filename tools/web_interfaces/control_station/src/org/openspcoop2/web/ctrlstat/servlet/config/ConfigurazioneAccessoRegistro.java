/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.constants.AlgoritmoCache;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * registro
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneAccessoRegistro extends Action {

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

			String statocache = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_REGISTRY);
			String dimensionecache = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_REGISTRY);
			String algoritmocache = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_REGISTRY);
			String idlecache = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_REGISTRY);
			String lifecache = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_REGISTRY);

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO, null));

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Prendo il registro (dalla tabella configurazione)
			AccessoRegistro ar = confCore.getAccessoRegistro();

			// Se statohid == null, visualizzo la pagina per la modifica dati
			// In caso contrario, modifico i dati della porta di dominio nel db
			if (ServletUtils.isEditModeInProgress(request)) {
				Cache arc = ar.getCache();
				if (arc == null) {
					if (statocache == null) {
						statocache = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					dimensionecache = "";
					algoritmocache = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_LRU;
					idlecache = "";
					lifecache = "";
				} else {
					if (statocache == null) {
						statocache = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
					dimensionecache = arc.getDimensione();
					if(arc.getAlgoritmo()!=null)
						algoritmocache = arc.getAlgoritmo().toString();
					idlecache = arc.getItemIdleTime();
					lifecache = arc.getItemLifeSecond();
				}
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addConfigurazioneRegistroToDati(statocache, dimensionecache, algoritmocache, idlecache, lifecache, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI,
						ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI);
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.registroCheckData();
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addConfigurazioneRegistroToDati(statocache, dimensionecache, algoritmocache, idlecache, lifecache, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI,
						ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI);
			}

			// Modifico i dati del registro nel db
			if (statocache.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)) {
				Cache arcNew = new Cache();
				arcNew.setDimensione(dimensionecache);
				arcNew.setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocache));
				arcNew.setItemIdleTime(idlecache);
				arcNew.setItemLifeSecond(lifecache);
				ar.setCache(arcNew);
			} else {
				ar.setCache(null);
			}

			confCore.performUpdateOperation(userLogin, confHelper.smista(), ar);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati =	confHelper.addConfigurazioneRegistroToDati(statocache, dimensionecache,
					algoritmocache, idlecache, lifecache, dati);


			pd.setDati(dati);
			
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ACCESSO_REGISTRO_MODIFICATA_CON_SUCCESSO);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI,
					ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI,
					ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI);
		}
	}


}

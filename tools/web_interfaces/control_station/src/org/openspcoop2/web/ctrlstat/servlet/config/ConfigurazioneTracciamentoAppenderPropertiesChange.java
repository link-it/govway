/*
 * OpenSPCoop - Customizable API Gateway 
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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * tracciamentoAppenderPropChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneTracciamentoAppenderPropertiesChange extends Action {

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

			String id = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String idprop = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID_PROPRIETA);
			int idPropInt = Integer.parseInt(idprop);
			String valore = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();

			// Prendo l'appender
			Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
			Tracciamento t = newConfigurazione.getTracciamento();
			OpenspcoopAppender oa = null;
			for (int j = 0; j < t.sizeOpenspcoopAppenderList(); j++) {
				oa = t.getOpenspcoopAppender(j);
				if (idInt == oa.getId().intValue()) {
					break;
				}
			}
			Property oap = null;
			for (int i = 0; i < oa.sizePropertyList(); i++) {
				oap = oa.getProperty(i);
				if (idPropInt == oap.getId().intValue()) {
					break;
				}
			}

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_MESSAGGI_DIAGNOSTICI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_LIST));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA + " di " + oa.getTipo(),
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES_LIST,
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, oa.getId()+"")
						));
				lstParam.add(new Parameter(oap.getNome(), null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// Prendo i dati dal db
				String nome = oap.getNome();
				valore = oap.getValore();

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addNomeValoreToDati(TipoOperazione.CHANGE, dati, nome, valore,false);		

				dati = confHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

				dati = confHelper.addIdProprietaToDati(TipoOperazione.CHANGE, idprop, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.tracciamentoAppenderPropCheckData(TipoOperazione.CHANGE);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_MESSAGGI_DIAGNOSTICI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_LIST));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA + " di " + oa.getTipo(),
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES_LIST,
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, oa.getId()+"")
						));
				lstParam.add(new Parameter(oap.getNome(), null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addNomeValoreToDati(TipoOperazione.CHANGE, dati, oap.getNome(), valore,false);

				dati = confHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

				dati = confHelper.addIdProprietaToDati(TipoOperazione.CHANGE, idprop, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati della property nel db
			for (int k = 0; k < oa.sizePropertyList(); k++) {
				Property tmpOap = oa.getProperty(k);
				if (idPropInt == tmpOap.getId().intValue()) {
					oa.removeProperty(k);
					break;
				}
			}

			Property newOap = new Property();
			newOap.setNome(oap.getNome());
			newOap.setValore(valore);
			oa.addProperty(newOap);

			confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);

			// Preparo la lista
			newConfigurazione = confCore.getConfigurazioneGenerale();
			t = newConfigurazione.getTracciamento();
			oa = null;
			for (int j = 0; j < t.sizeOpenspcoopAppenderList(); j++) {
				oa = t.getOpenspcoopAppender(j);
				if (idInt == oa.getId().intValue()) {
					break;
				}
			}

			confHelper.prepareTracciamentoAppenderPropList(oa, oa.getPropertyList());

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER_CON_SUCCESSO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_APPENDER_PROPERTIES, ForwardParams.CHANGE());
		}
	}
}

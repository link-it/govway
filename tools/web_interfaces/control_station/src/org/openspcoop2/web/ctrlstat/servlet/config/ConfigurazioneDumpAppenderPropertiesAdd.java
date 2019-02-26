/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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

/**
 * ConfigurazioneDumpAppenderPropertiesAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneDumpAppenderPropertiesAdd extends Action {

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

			String id = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String valore = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();

			// Prendo l'appender
			Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
			Dump t = newConfigurazione.getDump();
			OpenspcoopAppender oa = null;
			String oldTipo = null;
			for (int j = 0; j < t.sizeOpenspcoopAppenderList(); j++) {
				oa = t.getOpenspcoopAppender(j);
				if (idInt == oa.getId().intValue()) {
					oldTipo = oa.getTipo();
					break;
				}
			}

			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_DUMP, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_LIST));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA + " di " + oa.getTipo(),
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES_LIST,
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, oa.getId()+"")
						));
				lstParam.add(ServletUtils.getParameterAggiungi());

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				DataElement dataElement = new DataElement();
				dataElement.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA);
				dataElement.setType(DataElementType.TITLE);
				dati.add(dataElement);
				
				dati = confHelper.addNomeValoreToDati(TipoOperazione.ADD, dati, "", "", false);

				dati= confHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, null, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.dumpAppenderPropCheckData(TipoOperazione.ADD);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_APPENDER_DUMP, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_LIST));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA + " di " + oa.getTipo(),
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES_LIST,
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, oa.getId()+"")
						));
				lstParam.add(ServletUtils.getParameterAggiungi());

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				DataElement dataElement = new DataElement();
				dataElement.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA);
				dataElement.setType(DataElementType.TITLE);
				dati.add(dataElement);
				
				dati = confHelper.addNomeValoreToDati(TipoOperazione.ADD, dati, nome, valore, false);

				dati= confHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, null, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES, 
						ForwardParams.ADD());
			}

			// Inserisco la proprietà nel db
			Property oap = new Property();
			oap.setNome(nome);
			oap.setValore(valore);
			oa.addProperty(oap);

			confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);

			// Preparo la lista
			newConfigurazione = confCore.getConfigurazioneGenerale();
			t = newConfigurazione.getDump();
			oa = null;
			for (int j = 0; j < t.sizeOpenspcoopAppenderList(); j++) {
				oa = t.getOpenspcoopAppender(j);
				if (oldTipo.equals(oa.getTipo())) {
					break;
				}
			}

			confHelper.prepareDumpAppenderPropList(oa, oa.getPropertyList());

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_APPENDER_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_APPENDER_PROPERTIES, ForwardParams.ADD());
		}
	}
}

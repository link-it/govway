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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * diagnosticaDatasourcePropAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneDiagnosticaDatasourcePropertiesAdd extends Action {

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

			String id = confHelper.getParametroInteger(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID);
			int idInt = Integer.parseInt(id);
			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String valore = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALORE);

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Prendo il datasource
			Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
			MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
			List<OpenspcoopSorgenteDati> lista = md.getOpenspcoopSorgenteDatiList();
			OpenspcoopSorgenteDati od = null;
			for (int j = 0; j < md.sizeOpenspcoopSorgenteDatiList(); j++) {
				od = lista.get(j);
				if (idInt == od.getId().intValue()) {
					break;
				}
			}

			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();
				
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_SORGENTI_DATI_MESSAGGI_DIAGNOSTICI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_LIST));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA + " di " + od.getNome(),
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES_LIST,
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, od.getId()+"")
						));
				lstParam.add(ServletUtils.getParameterAggiungi());
				
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				DataElement dataElement = new DataElement();
				dataElement.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA);
				dataElement.setType(DataElementType.TITLE);
				dati.add(dataElement);
				
				dati = confHelper.addNomeValoreToDati(TipoOperazione.ADD, dati, "", "",false);

				dati = confHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, null, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.diagnosticaDatasourcePropCheckData(TipoOperazione.ADD);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();
				
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_SORGENTI_DATI_MESSAGGI_DIAGNOSTICI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_LIST));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA + " di " + od.getNome(),
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES_LIST,
						new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ID, od.getId()+"")
						));
				lstParam.add(ServletUtils.getParameterAggiungi());
				
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				DataElement dataElement = new DataElement();
				dataElement.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA);
				dataElement.setType(DataElementType.TITLE);
				dati.add(dataElement);
				
				dati = confHelper.addNomeValoreToDati(TipoOperazione.ADD, dati, nome, valore,  false);
				
				dati = confHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, null, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES, 
						ForwardParams.ADD());
			}

			// Inserisco la proprietà nel db
			Property odp = new Property();
			odp.setNome(nome);
			odp.setValore(valore);
			od.addProperty(odp);

			confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);

			// Preparo la lista
			newConfigurazione = confCore.getConfigurazioneGenerale();
			md = newConfigurazione.getMessaggiDiagnostici();
			lista = md.getOpenspcoopSorgenteDatiList();
			od = null;
			for (int j = 0; j < md.sizeOpenspcoopSorgenteDatiList(); j++) {
				od = lista.get(j);
				if (idInt == od.getId().intValue()) {
					break;
				}
			}
			
			if(od==null) {
				throw new Exception("Datasource non trovato");
			}
			
			List<Property> lista1 = od.getPropertyList();

			confHelper.prepareDiagnosticaDatasourcePropList(od, lista1);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DIAGNOSTICA_DATASOURCE_PROPERTIES, ForwardParams.ADD());
		}
	}
}

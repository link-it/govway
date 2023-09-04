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
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * registriChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneRegistriChange extends Action {

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

			String nome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String location = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOCATION);
			String tipo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
			String utente = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTENTE);
			String password = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PW);
			String confpw = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONFERMA_PW);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Prendo il registro
			AccessoRegistro ar = confCore.getAccessoRegistro();
			AccessoRegistroRegistro arr = null;
			for (int i = 0; i < ar.sizeRegistroList(); i++) {
				arr = ar.getRegistro(i);
				if (nome.equals(arr.getNome())) {
					break;
				}
			}

			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if (confHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_REGISTRI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_REGISTRI_LIST));
				lstParam.add(new Parameter(nome, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// Prendo i dati dal db solo se non sono stati passati
				if (location == null) {
					location = arr.getLocation();
				}
				if (tipo == null) {
					tipo = arr.getTipo().toString();
				}
				if (utente == null) {
					utente = arr.getUser();
					password = arr.getPassword();
					confpw = password;
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addRegistroToDati(TipoOperazione.CHANGE, nome, location, tipo, utente, password, confpw, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_REGISTRI, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.registriCheckData(TipoOperazione.CHANGE);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_REGISTRI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_REGISTRI_LIST));
				lstParam.add(new Parameter(nome, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addRegistroToDati(TipoOperazione.CHANGE, nome, location, tipo, utente, password, confpw, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_REGISTRI, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati del registro nel db
			for (int i = 0; i < ar.sizeRegistroList(); i++) {
				AccessoRegistroRegistro tmpArr = ar.getRegistro(i);
				if (nome.equals(tmpArr.getNome())) {
					ar.removeRegistro(i);
					break;
				}
			}

			AccessoRegistroRegistro newArr = new AccessoRegistroRegistro();
			newArr.setNome(nome);
			newArr.setLocation(location);
			newArr.setTipo(RegistroTipo.toEnumConstant(tipo));
			ar.addRegistro(newArr);

			confCore.performUpdateOperation(userLogin, confHelper.smista(), ar);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<AccessoRegistroRegistro> lista = confCore.registriList(ricerca);

			confHelper.prepareRegistriList(ricerca, lista);

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ACCESSO_REGISTRO_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_REGISTRI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_REGISTRI, ForwardParams.CHANGE());
		}
	}
}

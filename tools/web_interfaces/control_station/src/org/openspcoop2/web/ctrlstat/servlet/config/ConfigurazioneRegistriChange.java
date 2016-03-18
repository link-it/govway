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
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
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

			String nome = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME);
			String location = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LOCATION);
			String tipo = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO);
			String utente = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTENTE);
			String password = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PASSWORD);
			String confpw = request.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONFERMA_PASSWORD);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			// Preparo il menu
			confHelper.makeMenu();

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
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null));
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
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addRegistroToDati(TipoOperazione.CHANGE, nome, location, tipo, utente, password, confpw, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_REGISTRI, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.registriCheckData(TipoOperazione.CHANGE);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRO, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ACCESSO_REGISTRO_SERVIZI));
				lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ELENCO_REGISTRI, 
						ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_REGISTRI_LIST));
				lstParam.add(new Parameter(nome, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addRegistroToDati(TipoOperazione.CHANGE, nome, location, tipo, utente, password, confpw, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

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
			if (tipo.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TIPO_UDDI)) {
				newArr.setUser(utente);
				newArr.setPassword(password);
			}
			ar.addRegistro(newArr);

			confCore.performUpdateOperation(userLogin, confHelper.smista(), ar);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<AccessoRegistroRegistro> lista = confCore.registriList(ricerca);

			confHelper.prepareRegistriList(ricerca, lista);

			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ACCESSO_REGISTRO_MODIFICATA_CON_SUCCESSO);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_REGISTRI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_REGISTRI, ForwardParams.CHANGE());
		}
	}
}

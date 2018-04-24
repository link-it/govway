/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneTracciamentoTransazioni
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13708 $, $Date: 2018-03-08 10:53:05 +0100 (Thu, 08 Mar 2018) $
 * 
 */
public class ConfigurazioneTracciamentoTransazioni extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		TipoOperazione tipoOperazione = TipoOperazione.OTHER;
		
		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			
			// Preparo il menu
			confHelper.makeMenu();
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			Configurazione oldConfigurazione = confCore.getConfigurazioneGenerale();
			
			// Stato [si usa per capire se sono entrato per la prima volta nella schermata]		
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			
			String nuovaConfigurazioneEsiti = confHelper.readConfigurazioneRegistrazioneEsitiFromHttpParameters(oldConfigurazione.getTracciamento().getEsiti(), first);
			String severita = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			String severita_log4j = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
			String registrazioneTracce = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
			String dumpApplicativo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_APPLICATIVO);
			String dumpPD = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
			String dumpPA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, null));
			
			// edit in progress
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(severita == null) {
					if(oldConfigurazione.getMessaggiDiagnostici().getSeverita()!=null)
						severita = oldConfigurazione.getMessaggiDiagnostici().getSeverita().toString();
					if(oldConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j()!=null)
						severita_log4j = oldConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();
					if(oldConfigurazione.getDump().getStato()!=null)
						dumpApplicativo = oldConfigurazione.getDump().getStato().toString();
					if(oldConfigurazione.getDump().getDumpBinarioPortaDelegata()!=null)
						dumpPD = oldConfigurazione.getDump().getDumpBinarioPortaDelegata().toString();
					if(oldConfigurazione.getDump().getDumpBinarioPortaApplicativa()!=null)
						dumpPA = oldConfigurazione.getDump().getDumpBinarioPortaApplicativa().toString();
					if(oldConfigurazione.getTracciamento().getStato()!=null)
						registrazioneTracce = oldConfigurazione.getTracciamento().getStato().toString();
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, nuovaConfigurazioneEsiti); 
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
				
				confHelper.addMessaggiDiagnosticiToDati(severita, severita_log4j, oldConfigurazione, dati, contaListe);

				confHelper.addTracciamentoToDati(registrazioneTracce, oldConfigurazione, dati, contaListe);
				
				confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, oldConfigurazione, dati, contaListe); 
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI,	ForwardParams.OTHER(""));
			}
			
			// Controlli sui campi immessi
			boolean isOk = confHelper.checkConfigurazioneTracciamento(tipoOperazione, nuovaConfigurazioneEsiti);
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, nuovaConfigurazioneEsiti); 
				
				confHelper.addMessaggiDiagnosticiToDati(severita, severita_log4j, oldConfigurazione, dati, contaListe);

				confHelper.addTracciamentoToDati(registrazioneTracce, oldConfigurazione, dati, contaListe);
				
				confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, oldConfigurazione, dati, contaListe); 
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI,	ForwardParams.OTHER(""));
			}
			
			Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
			
			newConfigurazione.getTracciamento().setEsiti(nuovaConfigurazioneEsiti);
			
			if (newConfigurazione.getMessaggiDiagnostici() != null) {
				newConfigurazione.getMessaggiDiagnostici().setSeverita(Severita.toEnumConstant(severita));
				newConfigurazione.getMessaggiDiagnostici().setSeveritaLog4j(Severita.toEnumConstant(severita_log4j));
			} else {
				MessaggiDiagnostici md = new MessaggiDiagnostici();
				md.setSeverita(Severita.toEnumConstant(severita));
				md.setSeveritaLog4j(Severita.toEnumConstant(severita_log4j));
				newConfigurazione.setMessaggiDiagnostici(md);
			}
			
			if (newConfigurazione.getTracciamento() != null) {
				newConfigurazione.getTracciamento().setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
			} else {
				Tracciamento t = new Tracciamento();
				t.setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
				newConfigurazione.setTracciamento(t);
			}
			
			if (newConfigurazione.getDump() != null) {
				newConfigurazione.getDump().setStato(StatoFunzionalita.toEnumConstant(dumpApplicativo));
				newConfigurazione.getDump().setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
				newConfigurazione.getDump().setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
			} else {
				Dump d = new Dump();
				d.setStato(StatoFunzionalita.toEnumConstant(dumpApplicativo));
				d.setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
				d.setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
				newConfigurazione.setDump(d);
			}
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);

			// Preparo la lista
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO_ESITI_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			
			// ricarico la configurazione
			newConfigurazione = confCore.getConfigurazioneGenerale();
			
			confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, newConfigurazione.getTracciamento().getEsiti()); 
			
			confHelper.addMessaggiDiagnosticiToDati(severita, severita_log4j, oldConfigurazione, dati, contaListe);

			confHelper.addTracciamentoToDati(registrazioneTracce, oldConfigurazione, dati, contaListe);
			
			confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, oldConfigurazione, dati, contaListe); 
			
			// Set First is false
			confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			
			pd.setDati(dati);
			pd.disableEditMode();

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI, ForwardParams.OTHER(""));
		}
	}
}

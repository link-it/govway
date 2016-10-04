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


package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * pddAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PddAdd extends Action {


	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
		
			PddHelper pddHelper = new PddHelper(request, pd, session);
			PddCore pddCore = new PddCore();
			
			String nome = request.getParameter(PddCostanti.PARAMETRO_PDD_NOME);
			String tipo = request.getParameter(PddCostanti.PARAMETRO_PDD_TIPOLOGIA);
			String descrizione = request.getParameter(PddCostanti.PARAMETRO_PDD_DESCRIZIONE);
			String ip = request.getParameter(PddCostanti.PARAMETRO_PDD_INDIRIZZO_PUBBLICO);
			String ipGestione = request.getParameter(PddCostanti.PARAMETRO_PDD_INDIRIZZO_GESTIONE);
			String porta = request.getParameter(PddCostanti.PARAMETRO_PDD_PORTA_PUBBLICA);
			String portaGestione = request.getParameter(PddCostanti.PARAMETRO_PDD_PORTA_GESTIONE);
			String implementazione = request.getParameter(PddCostanti.PARAMETRO_PDD_IMPLEMENTAZIONE);
			String subject = request.getParameter(PddCostanti.PARAMETRO_PDD_SUBJECT);
			String clientAuth = request.getParameter(PddCostanti.PARAMETRO_PDD_CLIENT_AUTH);
			String protocollo = request.getParameter(PddCostanti.PARAMETRO_PDD_PROTOCOLLO);
			String protocolloGestione = request.getParameter(PddCostanti.PARAMETRO_PDD_PROTOCOLLO_GESTIONE);
			
			int portaInt = 0;
			int portaGestioneInt = 0;
			try {
				portaInt = Integer.parseInt(porta);
			} catch (NumberFormatException e) {
				portaInt = pddCore.getPortaPubblica();// porta default
			}
			try {
				portaGestioneInt = Integer.parseInt(portaGestione);
			} catch (NumberFormatException e) {
				portaGestioneInt = pddCore.getPortaGestione();// porta di default
			}

			// Preparo il menu
			pddHelper.makeMenu();
	
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(ServletUtils.isEditModeInProgress(request)){
	
				// default value
				if (porta == null || "".equals(porta))
					porta = pddCore.getPortaPubblica()+"";
				if (ip == null || "".equals(ip))
					ip = pddCore.getIndirizzoPubblico();
				if (portaGestione == null || "".equals(portaGestione))
					portaGestione = pddCore.getPortaGestione()+"";
				if (ipGestione == null || "".equals(ipGestione))
					ipGestione = pddCore.getIndirizzoGestione();
				if (implementazione == null || "".equals(implementazione))
					implementazione = PddCostanti.DEFAULT_PDD_IMPLEMENTAZIONE;
	
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, PddCostanti.LABEL_PORTE_DI_DOMINIO);
	
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				if (nome == null)
				    dati = pddHelper.addPddToDati(dati, "", null,  ip, "", "", "", 
				    		PddTipologia.OPERATIVO, TipoOperazione.ADD, 
				    		PddCostanti.DEFAULT_PDD_PROTOCOLLI, PddCostanti.DEFAULT_PDD_PROTOCOLLO, PddCostanti.DEFAULT_PDD_PROTOCOLLO, 
				    		portaInt, descrizione, ipGestione, portaGestioneInt, implementazione, CostantiConfigurazione.DISABILITATO.toString(), false);
				else
				    dati = pddHelper.addPddToDati(dati, nome, null,  ip, subject, "", "", 
				    		PddTipologia.toPddTipologia(tipo), TipoOperazione.ADD, 
				    		PddCostanti.DEFAULT_PDD_PROTOCOLLI, protocollo,  protocolloGestione,
				    		portaInt, descrizione, ipGestione, portaGestioneInt, implementazione, clientAuth, false);
	
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PddCostanti.OBJECT_NAME_PDD, ForwardParams.ADD());
				
			}
	
			// Controlli sui campi immessi
			boolean isOk = pddHelper.pddCheckData(TipoOperazione.ADD, false);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, PddCostanti.LABEL_PORTE_DI_DOMINIO);
	
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				dati = pddHelper.addPddToDati(dati, nome, null, ip, subject, "" /* password */, ""/* confpw */, 
						PddTipologia.toPddTipologia(tipo), TipoOperazione.ADD,
						PddCostanti.DEFAULT_PDD_PROTOCOLLI, protocollo,  protocolloGestione,
						portaInt, descrizione, ipGestione, portaGestioneInt, implementazione, clientAuth, false);
	
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PddCostanti.OBJECT_NAME_PDD, ForwardParams.ADD());
				
			}


			String userLogin = ServletUtils.getUserLoginFromSession(session);

			if ((subject == null) || subject.trim().equals("")) {
				subject="C=IT,O="+nome;
				if ((ip != null) && !ip.equals("")) {
					subject += ",OU=" + ip;
				}
			}

			PdDControlStation pdd = new PdDControlStation();
			pdd.setNome(nome);
			pdd.setTipo(tipo);
			if(subject!=null && !"".equals(subject))
				pdd.setSubject(subject);
			else
				pdd.setSubject(null);
			pdd.setDescrizione(descrizione);
			pdd.setImplementazione(implementazione);
			pdd.setClientAuth(StatoFunzionalita.toEnumConstant(clientAuth));
			if (tipo.equals(PddTipologia.OPERATIVO.toString()) || tipo.equals(PddTipologia.NONOPERATIVO.toString())) {
				// pdd.setPassword(password);
				pdd.setIp(ip);
				pdd.setProtocollo(protocollo);
				pdd.setPorta(portaInt);
				pdd.setIpGestione(ipGestione);
				pdd.setPortaGestione(portaGestioneInt);
				pdd.setProtocolloGestione(protocolloGestione);
			}
			pdd.setSuperUser(userLogin);

			pddCore.performCreateOperation(userLogin, pddHelper.smista(), pdd);

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session,Search.class); 

			List<PdDControlStation> lista = null;
			if(pddCore.isVisioneOggettiGlobale(userLogin)){
				lista = pddCore.pddList(null, ricerca);
			}else{
				lista = pddCore.pddList(userLogin, ricerca);
			}

			pddHelper.preparePddList(lista, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PddCostanti.OBJECT_NAME_PDD, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PddCostanti.OBJECT_NAME_PDD, ForwardParams.ADD());
		}

	}
}

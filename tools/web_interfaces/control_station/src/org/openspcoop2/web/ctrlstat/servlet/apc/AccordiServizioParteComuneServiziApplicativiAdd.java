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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;

/**
 * accordiServizioApplicativoAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneServiziApplicativiAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		@SuppressWarnings("unused")
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		@SuppressWarnings("unused")
		GeneralData gd = generalHelper.initGeneralData(request);

		//SERVE???
		
		/*
		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			String idAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idAccordoInt = Integer.parseInt(idAccordo);
			String idSoggErogatoreDelServizio = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVIZI_APPLICATIVI_ID_SOGGETTO);
			int idErogatoreDelServizioInt = Integer.parseInt(idSoggErogatoreDelServizio);
			//session.setAttribute("elemento", idAccordo);
			//session.setAttribute("elemento1", idSoggErogatoreDelServizio);
			String servizioApplicativohid = request.getParameter("servizioApplicativohid");
			String servizioApplicativo = request.getParameter("servizioApplicativo");

			String idSoggettoFruitoreDelServizioString = request.getParameter("idSoggettoFruitoreDelServizio");
			int idSoggettoFruitoreDelServizio = Integer.parseInt(idSoggettoFruitoreDelServizioString);
			String idDelServizioString = request.getParameter("idDelServizio");
			int idDelServizio = Integer.parseInt(idDelServizioString);

			String correlato = request.getParameter("correlato");

			String tipoAccordo = request.getParameter("tipoSICA");
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			// Prendo nome, tipo e pdd del soggetto fruitore del servizio
			ControlStationCore core = new ControlStationCore();
			SoggettiCore soggettiCore = new SoggettiCore(core);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(core);
			Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoFruitoreDelServizio);
			String nomeSoggFruitoreServ = soggetto.getNome();
			String tipoSoggFruitoreServ = soggetto.getTipo();
			String pdd = soggetto.getPortaDominio();

			// Prendo nome e tipo del servizio
			AccordoServizioParteSpecifica asps = core.getAccordoServizioParteSpecifica(idDelServizio);
			Servizio servizio = asps.getServizio();
			String nomeServizio = servizio.getNome();
			String tipoServizio = servizio.getTipo();

			PageData oldPD = (PageData) session.getAttribute("PageData");

			String myID = oldPD.getHidden("myId"); // request.getParameter("myId");
			// int myIDint = Integer.parseInt(myID);

			// String nomeServizio = (String)
			// session.getAttribute("nomeservizio");
			// String tipoServizio = (String)
			// session.getAttribute("tiposervizio");

			ArrayList<?> serviziAggiunti = (ArrayList<?>) session.getAttribute("serviziAggiunti");

			// Prendo nome e tipo del soggetto erogatore del servizio
			org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(idSoggettoFruitoreDelServizio);

			org.openspcoop2.core.config.Soggetto soggettoErogatore = soggettiCore.getSoggetto(idErogatoreDelServizioInt);
			String nomeSoggErogatoreServizio = soggettoErogatore.getNome();
			String tipoSoggErogServizio = soggettoErogatore.getTipo();

			// Preparo il menu
			ch.makeMenu();

			// Prendo la lista di servizi applicativi del soggetto erogatore del
			// servizio
			ArrayList<String> serviziApplicativiList = new ArrayList<String>();
			serviziApplicativiList.add("--"); // elemento nullo di default
			for (int i = 0; i < soggettoFruitore.sizeServizioApplicativoList(); i++) {
				// se il servizio nn e' tra quelli gia aggiunti allora lo
				// aggiungo
				ServizioApplicativo singleSil = soggettoFruitore.getServizioApplicativo(i);
				String nome = singleSil.getNome();
				if (!serviziAggiunti.contains(nome))
					serviziApplicativiList.add(nome);
			}

			// Prendo il nome dell'accordo
			AccordoServizioParteComune as = core.getAccordoServizio(idAccordoInt);
			String nomeAccordo = as.getNome();

			// cmq setto i dati hidden che mi servono sempre
			Hashtable<String, String> hidden = new Hashtable<String, String>();
			hidden.put("myId", myID);
			hidden.put("idSoggErogatore", idSoggErogatoreDelServizio);
			pd.setHidden(hidden);

			if (servizioApplicativohid == null) {

				Vector<GeneralLink> titlelist = new Vector<GeneralLink>();
				GeneralLink tl1 = new GeneralLink();
				tl1.setLabel(Utilities.getTerminologiaAccordoServizio(tipoAccordo));
				titlelist.addElement(tl1);
				GeneralLink tl2 = new GeneralLink();
				tl2.setLabel("Elenco");
				tl2.setUrl("accordiList.do"+Utilities.getParametroAccordoServizio(tipoAccordo, "?"));
				titlelist.addElement(tl2);
				GeneralLink tl3 = new GeneralLink();
				tl3.setLabel("Erogatori di " + nomeAccordo);
				tl3.setUrl("accordiErogatoriList.do?id=" + idAccordoInt + "&idsogg=" + idAccordo+Utilities.getParametroAccordoServizio(tipoAccordo, "&"));
				titlelist.addElement(tl3);
				GeneralLink tl4 = new GeneralLink();
				tl4.setLabel("Fruitori di " + tipoSoggErogServizio + "/" + nomeSoggErogatoreServizio + " - " + tipoServizio + "/" + nomeServizio);
				tl4.setUrl("accordiErogatoriFruitoriList.do?id=" + idAccordo + "&nomeprov=" + nomeSoggErogatoreServizio + "&tipoprov=" + tipoSoggErogServizio + "&nomeservizio=" + nomeServizio + "&tiposervizio=" + tipoServizio + "&myId=" + myID + "&correlato=" + correlato+Utilities.getParametroAccordoServizio(tipoAccordo, "&"));
				titlelist.addElement(tl4);
				GeneralLink tl5 = new GeneralLink();
				// tl5.setLabel("Servizi Applicativi di " +
				// tipoprov+"/"+nomeprov);
				tl5.setLabel("Politiche di Sicurezza di " + tipoSoggFruitoreServ + "/" + nomeSoggFruitoreServ);
				titlelist.addElement(tl5);
				pd.setTitleList(titlelist);

				Vector<DataElement> dati = new Vector<DataElement>();
				dati = gh.addPorteServizioApplicativoToDati(dati, "", serviziApplicativiList.toArray(new String[serviziApplicativiList.size()]));

				DataElement de = new DataElement();
				de.setType("hidden");
				de.setName("id");
				de.setValue(idAccordo);
				dati.addElement(de);

				de = new DataElement();
				de.setType("hidden");
				de.setName("idsogg");
				de.setValue(idSoggErogatoreDelServizio);
				dati.addElement(de);

				de = new DataElement();
				de.setType("hidden");
				de.setName("servizioApplicativohid");
				de.setValue("settato");
				dati.addElement(de);

				de = new DataElement();
				de.setName("tipoprov");
				de.setType("hidden");
				de.setValue(tipoSoggFruitoreServ);
				dati.addElement(de);

				de = new DataElement();
				de.setName("nomeprov");
				de.setType("hidden");
				de.setValue(nomeSoggFruitoreServ);
				dati.addElement(de);

				de = new DataElement();
				de.setName("nomeservizio");
				de.setType("hidden");
				de.setValue(nomeServizio);
				dati.addElement(de);

				de = new DataElement();
				de.setName("tiposervizio");
				de.setType("hidden");
				de.setValue(tipoServizio);
				dati.addElement(de);

				de = new DataElement();
				de.setName("myId");
				de.setType("hidden");
				de.setValue(myID);
				dati.addElement(de);

				de = new DataElement();
				de.setName("correlato");
				de.setType("hidden");
				de.setValue(correlato);
				dati.addElement(de);

				de = new DataElement();
				de.setName("idSoggettoFruitoreDelServizio");
				de.setType("hidden");
				de.setValue(idSoggettoFruitoreDelServizioString);
				dati.addElement(de);

				de = new DataElement();
				de.setName("idDelServizio");
				de.setType("hidden");
				de.setValue("" + idDelServizio);
				dati.addElement(de);

				de = new DataElement();
				de.setName("tipoSICA");
				de.setType("hidden");
				de.setValue(tipoAccordo);
				dati.addElement(de);
				
				pd.setDati(dati);

				dbM.releaseConnection(con);
				session.setAttribute("GeneralData", gd);
				session.setAttribute("PageData", pd);
				// Remove the Form Bean - don't need to carry values forward
				// con jboss 4.2.1 produce errore:
				// request.removeAttribute(mapping.getAttribute());
				return (mapping.findForward("AddAccordiServizioApplicativoForm"));
			}

			// Controlli sui campi immessi
			// nn c'e' nessun controllo da fare
			// boolean isOk = ch.accordiServizioApplicativoCheckData("add");
			// if (!isOk) {
			// TODO implementare
			// operazione da eseguire nel caso il check dati ritornasse false
			// }

			// TODO implementare
			// Inserisco il servizioApplicativo nel db
			try {

				// se il servizio applicativo e' diverso da quello di default
				// allora lo inserisco nel db
				if (!servizioApplicativo.equals("--")) {
					// recuper l'id del servizioApplicativo
					int idSA = 0;
					for (int i = 0; i < soggettoFruitore.sizeServizioApplicativoList(); i++) {
						ServizioApplicativo singleSil = soggettoFruitore.getServizioApplicativo(i);
						String nome = singleSil.getNome();
						if (servizioApplicativo.equals(nome)) {
							idSA = singleSil.getId().intValue();
							break;
						}
					}

					// creo la politica di sicurezza
					PoliticheSicurezza ps = new PoliticheSicurezza();
					ps.setNomeServizioApplicativo(servizioApplicativo);
					ps.setIdServizio(idDelServizio);
					ps.setIdFruitore(idSoggettoFruitoreDelServizio);
					ps.setIdServizioApplicativo(idSA);

					// effettuo le operazioni
					String superUser = (String) session.getAttribute("Login");
					core.performCreateOperation(superUser, ch.smista(), ps);

					// Prendo l'id della politica
					ps = core.getPoliticheSicurezza(idSoggettoFruitoreDelServizio, idDelServizio, idSA);
					int idPS = ps.getId().intValue();

					// inserisco l'associazione nella
					// portadom_porte_delegate_servizioApplicativo
					// mi servono il nome del ServizioApplicativo
					// e l'id della porta delegata (che recupero tramite
					// id_fruitore tipo e nome Erogatore, tipo e nome Servizio)
					String idporta = tipoSoggFruitoreServ + nomeSoggFruitoreServ + "/" + tipoSoggErogServizio + nomeSoggErogatoreServizio + "/" + tipoServizio + nomeServizio;
					IDSoggetto ids = new IDSoggetto(tipoSoggFruitoreServ, nomeSoggFruitoreServ);
					IDPortaDelegata idpd = new IDPortaDelegata();
					idpd.setSoggettoFruitore(ids);
					idpd.setLocationPD(idporta);
					PortaDelegata pde = core.getPortaDelegata(idpd);
					ServizioApplicativo sa = saCore.getServizioApplicativo(servizioApplicativo, nomeSoggFruitoreServ, tipoSoggFruitoreServ);
					pde.addServizioApplicativo(sa);

					core.performUpdateOperation(superUser, ch.smista(), pde);

					// Chiedo la setDati
					if (ch.smista()) {
						OperazioneDaSmistare opRegistroServizi = new OperazioneDaSmistare();
						opRegistroServizi.setOperazione(TipiOperazione.CREAZIONE);
						opRegistroServizi.setPdd(pdd);
						opRegistroServizi.setIDTable(idPS);
						opRegistroServizi.setSuperuser((String) session.getAttribute("Login"));
						opRegistroServizi.setOggetto("politicheSicurezza");

						BackendConnector bc = new BackendConnector();
						boolean bcOk = bc.setDati(opRegistroServizi);
						if (!bcOk) {
							// java.util.Date now = new java.util.Date();
							try {
								con.rollback();
							} catch (SQLException exrool) {
							}
							// Chiudo la connessione al DB
							dbM.releaseConnection(con);
							pd.setMessage("Si e' verificato un errore durante la setDati.<BR>Si prega di riprovare pi&ugrave; tardi");
							session.setAttribute("GeneralData", gd);
							session.setAttribute("PageData", pd);
							// Remove the Form Bean - don't need to carry values
							// forward
							// con jboss 4.2.1 produce errore:
							// request.removeAttribute(mapping.getAttribute());
							return (mapping.findForward("Error"));
						}
					}

				}

			} catch (Exception ex) {
				// java.util.Date now = new java.util.Date();
				try {
					con.rollback();
				} catch (SQLException exrool) {
				}
			}

			// preparo la lista
			List<ServizioApplicativo> lista = saCore.accordiServizioApplicativoList(idSoggettoFruitoreDelServizio, idDelServizio, new Search());

			ch.prepareAccordiServizioApplicativoList(lista, new Search(),tipoAccordo);

			// Chiudo la connessione al DB
			dbM.releaseConnection(con);

			session.setAttribute("GeneralData", gd);
			session.setAttribute("PageData", pd);

			// Remove the Form Bean - don't need to carry values forward
			// con jboss 4.2.1 produce errore:
			// request.removeAttribute(mapping.getAttribute());

			// Forward control to the specified success URI
			return (mapping.findForward("AddAccordiServizioApplicativoOk"));
		} catch (Exception e) {
			ControlStationCore.logError("SistemaNonDisponibile: "+e.getMessage(), e);
			pd.setMessage("Il sistema &egrave; momentaneamente indisponibile.<BR>Si prega di riprovare pi&ugrave; tardi");
			session.setAttribute("GeneralData", gd);
			session.setAttribute("PageData", pd);
			// Remove the Form Bean - don't need to carry values forward
			// con jboss 4.2.1 produce errore:
			// request.removeAttribute(mapping.getAttribute());
			return (mapping.findForward("Error"));
		} finally {
			dbM.releaseConnection(con);
		}
		
		*/
		
		return null;
	}
}

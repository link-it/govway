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

import java.util.ArrayList;
import java.util.Iterator;
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
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiAzioniChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAzioniChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
	

		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String id = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idAccordo = 0;
			try {
				idAccordo = Integer.parseInt(id);
			} catch (Exception e) {
			}
			String nomeaz = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_NOME);
			if (nomeaz == null) {
				nomeaz = "";
			}
			String azicorr = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CORRELATA);
			String profProtocollo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_BUSTA);
			String profcoll = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_COLLABORAZIONE);
			String filtrodupaz = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_FILTRO_DUPLICATI);
			if ((filtrodupaz != null) && filtrodupaz.equals("null")) {
				filtrodupaz = null;
			}
			String confricaz = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CONFERMA_RICEZIONE);
			if ((confricaz != null) && confricaz.equals("null")) {
				confricaz = null;
			}
			String idcollaz = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_COLLABORAZIONE);
			if ((idcollaz != null) && idcollaz.equals("null")) {
				idcollaz = null;
			}
			String consordaz = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CONSEGNA_ORDINE);
			if ((consordaz != null) && consordaz.equals("null")) {
				consordaz = null;
			}
			String scadenzaaz = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_SCADENZA);
			if (scadenzaaz == null) {
				scadenzaaz = "";
			}

			String tipoAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome dal db
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			
			AccordoServizioParteComune as = apcCore.getAccordoServizio(new Long(idAccordo));
			String uriAS = idAccordoFactory.getUriFromAccordo(as);
			
			String protocollo = null;
			//calcolo del protocollo implementato dall'accordo
			if(as != null){
				IdSoggetto soggettoReferente = as.getSoggettoReferente();
				String tipoSoggettoReferente = soggettoReferente.getTipo();
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoReferente);
			} else {
				protocollo = apcCore.getProtocolloDefault();
			}

			// Prendo la lista di azioni dell'accordo
			// (tranne quella che sto modificando)
			// e ne metto i nomi in un array di stringhe
			// prendo la lista delle azioni correlate con profilo
			// asincronoAsimmetrico
			List<Azione> azioniCorrelate = apcCore.accordiAzioniList(idAccordo, CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString(), new Search(true));
			List<String> azioniCorrelateUniche = null;
			String[] azioniList = null;
			if (azioniCorrelate.size() > 0) {
				azioniCorrelateUniche = new ArrayList<String>();
				azioniCorrelateUniche.add("-");
				for (Iterator<Azione> iterator = azioniCorrelate.iterator(); iterator.hasNext();) {
					Azione azione = iterator.next();
					if (!nomeaz.equals(azione.getNome())) {
						if ( 
								 (azione.getCorrelata()==null||"".equals(azione.getCorrelata())) &&
								 (!apcCore.isAzioneCorrelata(idAccordo, azione.getNome(), nomeaz))
							) {
							azioniCorrelateUniche.add(azione.getNome());
						}
					}
				}
			}

			if (azioniCorrelateUniche != null)
				azioniList = azioniCorrelateUniche.toArray(new String[azioniCorrelateUniche.size()]);

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(ServletUtils.isEditModeInProgress(request)){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + uriAS, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST+"?"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uriAS+"&"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(nomeaz, null)
				);

				
				// Prendo i dati dell'accordo
				String defprofcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());
				String deffiltrodupaz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
				String defconfricaz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
				String defidcollaz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
				String defconsordaz = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
				String defscadenzaaz = as.getScadenza();

				for (int i = 0; i < as.sizeAzioneList(); i++) {
					Azione az = as.getAzione(i);
					if (nomeaz.equals(az.getNome())) {
						filtrodupaz = filtrodupaz != null && !"".equals(filtrodupaz) ? filtrodupaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(az.getFiltroDuplicati());
						confricaz = confricaz != null && !"".equals(confricaz) ? confricaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(az.getConfermaRicezione());
						idcollaz = idcollaz != null && !"".equals(idcollaz) ? idcollaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(az.getIdCollaborazione());
						consordaz = consordaz != null && !"".equals(consordaz) ? consordaz : AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(az.getConsegnaInOrdine());
						scadenzaaz = scadenzaaz != null && !"".equals(scadenzaaz) ? scadenzaaz : az.getScadenza();
						profcoll = profcoll != null && !"".equals(profcoll) ? profcoll : AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(az.getProfiloCollaborazione());
						azicorr = azicorr != null && !"".equals(azicorr) ? azicorr : az.getCorrelata();
						if (azicorr == null)
							azicorr = "-";
						if (profProtocollo == null) {
							profProtocollo = az.getProfAzione();
						}
						break;
					}
				}

				if ((profProtocollo == null) || profProtocollo.equals("")) {
					profProtocollo = AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT;
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiAzioniToDati(dati, id, nomeaz, profProtocollo, 
						filtrodupaz, deffiltrodupaz, confricaz, defconfricaz, idcollaz, defidcollaz, consordaz, defconsordaz, scadenzaaz, 
						defscadenzaaz, defprofcoll, profcoll, TipoOperazione.CHANGE, 
						azicorr, azioniList, as.getStatoPackage(),tipoAccordo,protocollo);

				pd.setDati(dati);

				if(apcCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiAzioniCheckData(TipoOperazione.CHANGE);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + uriAS, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST+"?"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uriAS+"&"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(nomeaz, null)
				);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apcHelper.addAccordiAzioniToDati(dati, id, nomeaz, profProtocollo, 
						filtrodupaz, filtrodupaz, confricaz, confricaz, idcollaz, idcollaz, consordaz, consordaz, scadenzaaz, scadenzaaz, 
						profcoll, profcoll, TipoOperazione.CHANGE, 
						azicorr, azioniList, as.getStatoPackage(),tipoAccordo,protocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.CHANGE());
			}

			// Modifico i dati dell'azione nel db
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
				filtrodupaz = null;
				confricaz = null;
				idcollaz = null;
				consordaz = null;
				scadenzaaz = null;
			}

			for (int i = 0; i < as.sizeAzioneList(); i++) {
				Azione az = as.getAzione(i);
				if (nomeaz.equals(az.getNome())) {
					as.removeAzione(i);
					break;
				}
			}

			Azione newAz = new Azione();
			newAz.setNome(nomeaz);
			if (!azicorr.equals("-"))
				newAz.setCorrelata(azicorr);
			newAz.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(filtrodupaz)));
			newAz.setConfermaRicezione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(confricaz)));
			newAz.setIdCollaborazione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(idcollaz)));
			newAz.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(consordaz)));
			newAz.setScadenza(scadenzaaz);
			newAz.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(AccordiServizioParteComuneHelper.convertProfiloCollaborazioneView2DB(profcoll)));
			newAz.setProfAzione(profProtocollo);
			as.addAzione(newAz);

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Chiedo la setDati (Dovrebbe gia essere effettuata dal comando sopra)
//			if (ch.smista()) {
//				OperazioneDaSmistare opRegistroServizi = new OperazioneDaSmistare();
//				opRegistroServizi.setOperazione(TipiOperazione.MODIFICA);
//				opRegistroServizi.setIDTable(idAccordo);
//				opRegistroServizi.setSuperuser(userLogin);
//				opRegistroServizi.setOggetto("accordo");
//				opRegistroServizi.addParameter(OperationsParameter.NOME_ACCORDO, uriAS);
//				BackendConnector bc = new BackendConnector();
//				boolean bcOk = bc.setDati(opRegistroServizi);
//				if (!bcOk) {
//					// java.util.Date now = new java.util.Date();
//					try {
//						con.rollback();
//					} catch (SQLException exrool) {
//					}
//					// Chiudo la connessione al DB
//					dbM.releaseConnection(con);
//					pd.setMessage("Si e' verificato un errore durante la setDati.<BR>Si prega di riprovare pi&ugrave; tardi");
//					session.setAttribute("GeneralData", gd);
//					session.setAttribute("PageData", pd);
//					// Remove the Form Bean - don't need to carry values forward
//					// con jboss 4.2.1 produce errore:
//					// request.removeAttribute(mapping.getAttribute());
//					return (mapping.findForward("Error"));
//				}
//			}

			// devo aggiornare la lista dei servizi(serviziCorrelati) che
			// implementano l'accordo a cui e' stata aggiunta l'azione
			// basta fare un update del servizio per attivare le operazioni
			// necessarie all'aggiornamento
			List<AccordoServizioParteSpecifica> listaServizi = apsCore.serviziWithIdAccordoList(idAccordo);
			for (AccordoServizioParteSpecifica servizio : listaServizi) {
				apcCore.performUpdateOperation(userLogin, apcHelper.smista(), servizio);
			}

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.ACCORDI_AZIONI;

			ricerca = apcHelper.checkSearchParameters(idLista, ricerca);
			List<Azione> lista = apcCore.accordiAzioniList(idAccordo, ricerca);
			apcHelper.prepareAccordiAzioniList(as, lista, ricerca,tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.CHANGE());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI, ForwardParams.CHANGE());
		}
	}
}

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


package org.openspcoop2.web.ctrlstat.servlet.sa;

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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataServizioIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.Ruolo;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ServizioApplicativoRuoli
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ServiziApplicativiRuoli extends Action {

	@SuppressWarnings("incomplete-switch")
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

			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(saCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(saCore);
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);

			String idServizioApplicativo = null;
			String action = null;
			String descr = null;
			
			idServizioApplicativo = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			action = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION);

			if ((action == null) || action.equals("")) {
				action = TipoOperazione.LIST.toString();
			}

			// menu
			saHelper.makeMenu();

			long idSA = Long.parseLong(idServizioApplicativo);
			ServizioApplicativo servizioApplicativo = saCore.getServizioApplicativo(idSA);

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI,null),
					new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			// Azione in base alla action
			Ruolo ruolo = null;
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			switch (TipoOperazione.valueOf(action)) {
				case ADD:
					String nome = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);

					// se idhid==null mostro form per inserimento dati
					if(ServletUtils.isEditModeInProgress(request)){
						
						// titolo in caso di add
						ServletUtils.appendPageDataTitle(pd, 
								new Parameter("Ruoli di " + servizioApplicativo.getNome(),
										ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST+"?"+
										ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID+"="+servizioApplicativo.getId()+"&"+
										ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION+"="+TipoOperazione.LIST.toString()),
								new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null)
						);

						// prendo i ruoli disponibili
						List<Ruolo> ruoli = null;
						if(saCore.isVisioneOggettiGlobale(userLogin)){
							ruoli = saCore.getRuoli(null);
						}else{
							ruoli = saCore.getRuoli(userLogin);
						}
						
						dati = saHelper.addRuoliToDati(dati, ruoli, TipoOperazione.ADD, false, idServizioApplicativo);
						
						pd.setDati(dati);

						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
						
						return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, 
								ForwardParams.ADD());
						
					} else {
						// inserisco il ruolo nel db
						if ((nome == null) || nome.equals("")) {
							pd.setMessage("Il nome del Ruolo e' necessario.");
							
							ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
							
							return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, 
									ForwardParams.ADD());
						}
						
						ruolo = new Ruolo();
						// prendo valore correlato
						String correlato = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO);
						if(ServletUtils.isCheckBoxEnabled(correlato)==false){
							ruolo.setCorrelato(false);
						} else {
							ruolo.setCorrelato(true);
						}

						AccordoServizioParteComune as = null;

						try {

							as = apcCore.getAccordoServizio(idAccordoFactory.getIDAccordoFromUri(nome));
							servizioApplicativo = saCore.getServizioApplicativo(idSA);

							// il ruolo e' il nome dell'accordo di servizio
							// controllo se esiste
							boolean exist = saCore.existsRuoloServizioApplicativo(as.getId(), servizioApplicativo.getId(), (ruolo.isCorrelato() ? CostantiDB.TRUE : CostantiDB.FALSE));
							if (!exist) {
								ruolo.setIdAccordo(as.getId());
								ruolo.setNome(as.getNome());
								ruolo.setIdServizioApplicativo(idSA);
								ruolo.setDescrizione(descr != null ? descr.trim() : null);

								// creazione porta delegata in automatico
								String location = servizioApplicativo.getNome() + "/" + servizioApplicativo.getTipoSoggettoProprietario() + servizioApplicativo.getNomeSoggettoProprietario() + "/" + (ruolo.isCorrelato() ? as.getNome() + "Correlato" : as.getNome());

								PortaDelegata portaDelegata = new PortaDelegata();
								portaDelegata.setTipoSoggettoProprietario(servizioApplicativo.getTipoSoggettoProprietario());
								portaDelegata.setNomeSoggettoProprietario(servizioApplicativo.getNomeSoggettoProprietario());
								portaDelegata.setIdSoggetto(servizioApplicativo.getIdSoggetto());
								portaDelegata.setAutenticazione(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString());
								portaDelegata.setAutorizzazione(CostantiConfigurazione.AUTORIZZAZIONE_OPENSPCOOP);
								PortaDelegataAzione azione = new PortaDelegataAzione();
								azione.setIdentificazione(PortaDelegataAzioneIdentificazione.URL_BASED);
								azione.setPattern(".*" + location + "/[^/]+/([^/|^?]*).*");
								portaDelegata.setAzione(azione);
								portaDelegata.setDescrizione("Ruolo " + (ruolo.isCorrelato() ? as.getNome() + " (Correlato)" : as.getNome()) + " del Servizio Applicativo " + servizioApplicativo.getNome() + " appartenente a " + servizioApplicativo.getTipoSoggettoProprietario() + servizioApplicativo.getNomeSoggettoProprietario());
								portaDelegata.setLocation(location);
								portaDelegata.setNome(location);
								PortaDelegataServizio servizio = new PortaDelegataServizio();
								servizio.setIdentificazione(PortaDelegataServizioIdentificazione.STATIC);
								servizio.setTipo(servizioApplicativo.getTipoSoggettoProprietario()); // convenzione nell'usare lo stesso tipo
								servizio.setNome((ruolo.isCorrelato() ? as.getNome() + "Correlato" : as.getNome()));
								portaDelegata.setServizio(servizio);
								List<ServizioApplicativo> arr = new ArrayList<ServizioApplicativo>();
								arr.add(servizioApplicativo);
								portaDelegata.setServizioApplicativoList(arr);
								PortaDelegataSoggettoErogatore soggettoErogatore = new PortaDelegataSoggettoErogatore();
								soggettoErogatore.setTipo(servizioApplicativo.getTipoSoggettoProprietario());  // convenzione nell'usare lo stesso tipo
								soggettoErogatore.setIdentificazione(PortaDelegataSoggettoErogatoreIdentificazione.URL_BASED);
								soggettoErogatore.setPattern(".*" + location + "/([^/]*).*");
								portaDelegata.setSoggettoErogatore(soggettoErogatore);

								saCore.performCreateOperation(userLogin, saHelper.smista(), ruolo, portaDelegata);
							
							} else {

								List<Ruolo> ruoli = null;
								if(saCore.isVisioneOggettiGlobale(userLogin)){
									ruoli = saCore.getRuoli(null);
								}else{
									ruoli = saCore.getRuoli(userLogin);
								}
								
								dati = saHelper.addRuoliToDati(dati, ruoli, TipoOperazione.ADD, false, idServizioApplicativo);
								
								pd.setDati(dati);

								if (ruolo.isCorrelato()) {
									pd.setMessage("Esiste gia' un Ruolo Correlato [" + idAccordoFactory.getUriFromAccordo(as) + "] associato al Servizio Applicativo [" + servizioApplicativo.getNome()+" (proprietario:"+servizioApplicativo.getTipoSoggettoProprietario()+"/"+servizioApplicativo.getNomeSoggettoProprietario()+") " + "]");
								} else {
									pd.setMessage("Esiste gia' un Ruolo [" + idAccordoFactory.getUriFromAccordo(as) + "] associato al Servizio Applicativo [" + servizioApplicativo.getNome()+" (proprietario:"+servizioApplicativo.getTipoSoggettoProprietario()+"/"+servizioApplicativo.getNomeSoggettoProprietario()+") " + "]");
								}
								pd.setInserisciBottoni(false);
								
								ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
								
								return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, 
										ForwardParams.ADD());
							}
						} catch (NumberFormatException e) {
							List<Ruolo> ruoli = saCore.ruoliList(new Search(true));
							dati = saHelper.addRuoliToDati(dati, ruoli, TipoOperazione.ADD, false, idServizioApplicativo);
							pd.setMessage("Id del Servizio Applicativo non valido.");
							pd.setInserisciBottoni(false);
							pd.setAddButton(false);
							ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
							return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, 
									ForwardParams.ADD());
						} catch (DriverControlStationException e) {
							List<Ruolo> ruoli = saCore.ruoliList(new Search(true));
							dati = saHelper.addRuoliToDati(dati, ruoli, TipoOperazione.ADD, false, idServizioApplicativo);
							pd.setMessage("Impossibile trovare il Ruolo con nome: " + nome);
							pd.setInserisciBottoni(false);
							ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
							return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, 
									ForwardParams.ADD());
						}
					}

					break;
				case CHANGE:
					// non modifico il ruolo
					break;

				case DEL:

					String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);

					String msg = "";
					// Elimino i pdd dal db
					ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
					for (int i = 0; i < idsToRemove.size(); i++) {

						// id
						// DataElement de = (DataElement) ((Vector<?>)
						// pdold.getDati()
						// .elementAt(idToRemove[i])).elementAt(0);
						String[] ids = idsToRemove.get(i).split(":");
						long idRuolo = Long.parseLong(ids[0]);
						long idAccordo = Long.parseLong(ids[1]);
						// nome
						// de = (DataElement) ((Vector<?>)
						// pdold.getDati().elementAt(
						// idToRemove[i])).elementAt(1);
						// nome = de.getValue();

						try {
							AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordo);

							ruolo = saCore.getRuolo(idRuolo);

							// ruolo = new Ruolo();
							// ruolo.setIdAccordo(as.getId());
							// ruolo.setIdServizioApplicativo(Long.parseLong(this.idServizioApplicativo));

							// servizioApplicativo =
							// core.getServizioApplicativo(Long.parseLong(this.idServizioApplicativo));
							// creazione porta delegata in automatico
							String location = servizioApplicativo.getNome() + "/" + servizioApplicativo.getTipoSoggettoProprietario() + servizioApplicativo.getNomeSoggettoProprietario() + "/";
							IDAccordo idAccordoRuolo = null;
							if(ruolo.isCorrelato()){
								idAccordoRuolo = idAccordoFactory.getIDAccordoFromValues(as.getNome()+"Correlato", BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()), as.getVersione());
							}else{
								idAccordoRuolo = idAccordoFactory.getIDAccordoFromValues(as.getNome(), BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()), as.getVersione());
							}
							location = location + Ruolo.getNomeRuoloByIDAccordo(idAccordoRuolo);
							IDPortaDelegata idPD = new IDPortaDelegata();
							idPD.setLocationPD(location);
							idPD.setSoggettoFruitore(new IDSoggetto(servizioApplicativo.getTipoSoggettoProprietario(), servizioApplicativo.getNomeSoggettoProprietario()));

							// tento di recuperare la porta delegate
							// se nn esiste la porta delegata allora cancello
							// solo
							// il ruolo
							PortaDelegata portaDelegata = null;
							try {
								portaDelegata = porteDelegateCore.getPortaDelegata(idPD);
							} catch (DriverConfigurazioneNotFound e) {

							}

							if (portaDelegata != null) {
								saCore.performDeleteOperation(userLogin, saHelper.smista(), ruolo, portaDelegata);
							} else {
								saCore.performDeleteOperation(userLogin, saHelper.smista(), ruolo);
							}

						} catch (DriverControlStationException e) {
							pd.setMessage("Impossibile cancellare Ruolo con id: " + idRuolo);
							break;
						}
					}
					// se ci sono messaggi li visualizzo
					if ((msg != null) && !msg.equals("")) {
						pd.setMessage(msg);
					}
					break;

				case LIST:
					// effettuo la lista fuori dallo switch
					break;
			}

			// NavBar
			ServletUtils.appendPageDataTitle(pd, 
					new Parameter("Ruoli di " + servizioApplicativo.getNome(),
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST+"?"+
							ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID+"="+servizioApplicativo.getId()+"&"+
							ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION+"="+TipoOperazione.LIST.toString()),
					new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null)
			);

			// setto l'id del servizio applicativo a cui voglio associare i
			// ruoli
//			String params = "&id=" + idServizioApplicativo;
//			request.setAttribute("params", params);

			// List
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
	
			List<Ruolo> lista = saCore.ruoliWithIdServizioApplicativo(Long.parseLong(idServizioApplicativo));

			saHelper.prepareRuoliList(lista, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			return ServletUtils.getStrutsForward(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, 
					ForwardParams.LIST());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, ForwardParams.LIST());
		}

	}

}

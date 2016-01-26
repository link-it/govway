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
package org.openspcoop2.web.ctrlstat.servlet.operazioni;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.monitor.MonitorCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.queue.dao.Operation;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;


/****
 * 
 * OperazioniHelper
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperazioniHelper extends ConsoleHelper{

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss"); // SimpleDateFormat non e' thread-safe

	public OperazioniHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}

	public void prepareOperazioniList(Search ricerca, List<Operation> lista)throws Exception {
		try {

			ArrayList<String> errors = new ArrayList<String>();
			OperazioniFormBean formBean = this.getBeanForm(errors);

			Parameter pUtente = new Parameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_UTENTE, formBean.getUtente());
			Parameter pOperazione = new Parameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE, formBean.getTipo());
			Parameter pDetail = new Parameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_METHOD, OperazioniCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS);

			ServletUtils.addListElementIntoSession(this.session, OperazioniCostanti.OBJECT_NAME_OPERAZIONI, pOperazione, pUtente);

			int idLista =  this.operazioniCore.getIdLista(formBean); 
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			String elencoLabel = OperazioniCostanti.getTipoOperazioneLabelFromValue(formBean.getTipo());

			lstParam.add(new Parameter(CostantiControlStation.LABEL_STRUMENTI, null));
			lstParam.add(new Parameter(OperazioniCostanti.LABEL_OPERAZIONI, OperazioniCostanti.SERVLET_NAME_OPERAZIONI, pUtente, pOperazione));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(elencoLabel,null));
			}
			else{
				lstParam.add(new Parameter(elencoLabel, OperazioniCostanti.SERVLET_NAME_OPERAZIONI, pUtente, pOperazione));
				//					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Operazioni contenenti la stringa '" + search + "'");
			}

			// Genero l'elenco delle labels
			List<String> listaLabel = getListaLabel(formBean);  


			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			// Risultati Ricerca

			if (lista != null) {
				Iterator<Operation> it = lista.iterator();
				while (it.hasNext()) {
					Operation op = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					Parameter pId = new Parameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_ID, op.getId()+"");

					// Colonna Id
					DataElement	de = new DataElement();
					de.setUrl(OperazioniCostanti.SERVLET_NAME_OPERAZIONI, pId, pOperazione ,pUtente,pDetail);
					de.setValue(op.getId()+"");
					de.setIdToRemove(op.getId().toString());
					e.addElement(de);

					// Colonna Operazione

					de = new DataElement();
					de.setValue(op.getOperation());
					e.addElement(de);

					// Colonna Host

					de = new DataElement();
					de.setValue(op.getHostname());
					e.addElement(de);

					// Colonna Utente
					User user = ServletUtils.getUserFromSession(this.session);
					if(user.getPermessi().isUtenti()){
						de = new DataElement();
						de.setValue(op.getSuperUser());
						e.addElement(de);
					}
					
					// Colonna Data Richiesta

					de = new DataElement();
					de.setValue(this.formatter.format(op.getTimeReq().getTime()));
					e.addElement(de);

					// Colonna Data Esecuzione

					if(!formBean.getTipo().equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA)){
						de = new DataElement();
						de.setValue(this.formatter.format(op.getTimeExecute().getTime()));
						e.addElement(de);	
					}

					// Colonna Eliminata
					if(formBean.getTipo().equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE)){
						de = new DataElement();
						if (op.isDeleted())
							de.setValue(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_ELIMINAZIONE_OPERATORE);
						else
							de.setValue("");
						e.addElement(de);
					}

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);

			this.pd.setAddButton(false);

			if(formBean.getTipo().equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE)){
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}


	}

	private List<String> getListaLabel(OperazioniFormBean formBean) {
		List<String> listaLabel= 	new ArrayList<String>();

		User user = ServletUtils.getUserFromSession(this.session);
		
		String operazione = formBean.getTipo();
		// Label comuni
		listaLabel.add(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_ID);
		listaLabel.add(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE);
		listaLabel.add(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_HOST);
		if(user.getPermessi().isUtenti()){
			listaLabel.add(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_UTENTE);
		}
		listaLabel.add(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_DATA_RICHIESTA);

		// tutte tranne quelle in coda hanno la colonna data esecuzione
		if(!operazione.equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA))
			listaLabel.add(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_DATA_ESECUZIONE);

		// quelle eseguite hanno anche la colonne eliminate
		if(operazione.equals(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_ESEGUITE))
			listaLabel.add(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_ELIMINATA);

		return listaLabel;
	}


	public void showForm(String azione, String soglia, OperazioniFormBean formBean ) throws Exception {
		try {
			User user = ServletUtils.getUserFromSession(this.session);
			PermessiUtente permessi = user.getPermessi();
			boolean hasPermessiUtenti = permessi.isUtenti();

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(CostantiControlStation.LABEL_STRUMENTI, null));
			lstParam.add(new Parameter(OperazioniCostanti.LABEL_OPERAZIONI, null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());


			// Campi Ricerca

			// Tipo Operazione
			String[] tipoOperazioneValori = OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_LIST;
			String[] tipoOperazioneLabel = OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE_LIST;

			DataElement de = new DataElement();
			de.setLabel(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_OPERAZIONE);
			de.setValues(tipoOperazioneValori);
			de.setLabels(tipoOperazioneLabel);
			de.setSelected(formBean.getTipo());
			de.setType(DataElementType.SELECT);
			de.setName(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE);
			if(!hasPermessiUtenti)
				de.setSize(this.getSize());
			dati.addElement(de);



			// Utente

			de = new DataElement();

			if(hasPermessiUtenti){
				// Titolo Filter
				de.setLabel(MonitorCostanti.LABEL_MONITOR_FILTRO_RICERCA);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);

				de = new DataElement();
				de.setType(DataElementType.SELECT);
				List<User> listaUser =	formBean.getListaUser();
				List<String> utentiLabels = new ArrayList<String>();
				List<String> utentiValues = new ArrayList<String>();

				if(listaUser != null && listaUser.size() > 0){
					for (User ut : listaUser) {
						utentiLabels.add(ut.getLogin());
						utentiValues.add(ut.getLogin());
					}
				}

				utentiLabels.add(0, OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_UTENTE_ALL);
				utentiValues.add(0, OperazioniCostanti.PARAMETRO_OPERAZIONI_UTENTE_ALL);

				de.setLabels(utentiLabels);
				de.setValues(utentiValues);
				de.setSelected(formBean.getUtente());
			}else {
				de.setValue(formBean.getUtente());
				de.setType(DataElementType.HIDDEN);
			}
			de.setLabel(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_UTENTE);
			de.setName(OperazioniCostanti.PARAMETRO_OPERAZIONI_UTENTE);
			//			de.setSize(this.getSize());
			dati.addElement(de);

			this.pd.setDati(dati);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * Recupera i dati dalla request e riempe il form
	 * 
	 * @param errors Errori
	 * @return OperazioniFormBean
	 * @throws Exception
	 */
	public OperazioniFormBean getBeanForm(ArrayList<String> errors) throws Exception {
		try {
			OperazioniFormBean form = null;

			String operazione = null;
			// controllo se richiesta corretta
			boolean trovato = false;
			operazione = this.request.getParameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE);
			if ((operazione == null) || operazione.equals("")) {
				operazione = OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_IN_CODA;
			}


			String [] tipiOperazione = OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE_LIST;

			for (int i = 0; (i < tipiOperazione.length) && (trovato == false); i++) {
				if (operazione.equals(tipiOperazione[i])){ // || operazione.equals(OperazioniCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS)) {
					trovato = true;
					continue;
				}
			}

			if (trovato == false) {
				errors.add(OperazioniCostanti.ERRORE_TIPO_OPERAZIONE_SCONOSCIUTO);
				throw new Exception("Tipo Operazione selezionato SCONOSCIUTO.");
			}

			form = new OperazioniFormBean();

			form.setTipo(operazione);

			String idOperazione = this.request.getParameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_ID);
			if ((idOperazione != null) && !idOperazione.equals("")) {
				form.setIdOperazione(idOperazione);
			}

			// utente 

			String utente = this.request.getParameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_UTENTE);

			List<User> userList = new ArrayList<User>();
			String utenteTmp = null;
			HttpSession session = this.request.getSession(true);
			User user = ServletUtils.getUserFromSession(session);
			PermessiUtente permessi = user.getPermessi();

			if(permessi.isUtenti()) {
				utenteTmp = OperazioniCostanti.PARAMETRO_OPERAZIONI_UTENTE_ALL;
				userList = this.utentiCore.userList(new Search());
			} else {
				utenteTmp = user.getLogin();
				userList = new ArrayList<User>();
			}

			if(utente == null){	
				form.setUtente(utenteTmp);
			} else 
				form.setUtente(utente);


			form.setListaUser(userList); 

			// Pagina da visualizzare
			String metodo = this.request.getParameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_METHOD);

			if(metodo == null)
				metodo = OperazioniCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_FORM;

			form.setMethod(metodo); 

			return form;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	public List<Parameter> getFormBeanAsParameters(OperazioniFormBean formBean){
		List<Parameter> listaParametri = new ArrayList<Parameter>();
		
		if(formBean.getTipo() != null){
			listaParametri.add(new Parameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_OPERAZIONE, formBean.getTipo()));
		}
		
		if(formBean.getMethod() != null){
			listaParametri.add(new Parameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_METHOD, formBean.getMethod()));
		}
		
		if(formBean.getUtente() != null){
			listaParametri.add(new Parameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_UTENTE, formBean.getUtente()));
		}
		
		return listaParametri;
	}


	public void showDettagliMessaggio(Operation op, OperazioniFormBean formBean)
			throws Exception {
		try {

			List<Parameter> lstUrlParam = this.getFormBeanAsParameters(formBean);

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(CostantiControlStation.LABEL_STRUMENTI, null));
			lstParam.add(new Parameter(OperazioniCostanti.LABEL_OPERAZIONI, OperazioniCostanti.SERVLET_NAME_OPERAZIONI,lstUrlParam));

			String elencoLabel = OperazioniCostanti.getTipoOperazioneLabelFromValue(formBean.getTipo());
			lstParam.add(new Parameter(elencoLabel, OperazioniCostanti.SERVLET_NAME_OPERAZIONI_LIST, lstUrlParam));
			lstParam.add(new Parameter(
					op.getOperation() + " (" + op.getId() + ") - " + op.getHostname(), null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			Vector<DataElement> dati = new Vector<DataElement>();
			DataElement de = new DataElement();

			de.setType(DataElementType.TITLE);
			de.setLabel(OperazioniCostanti.LABEL_OPERAZIONI_DETTAGLI);
			dati.addElement(de);

			DataElement timereq = new DataElement();
			timereq.setLabel(OperazioniCostanti.LABEL_OPERAZIONI_RICHIESTA_IL);
			timereq.setValue("" + op.getTimeReq());
			dati.addElement(timereq);
			if (OperationStatus.NOT_SET.equals(op.getStatus())) {
				String tmpDet = op.getDetails();
				if ((tmpDet != null) && !tmpDet.equals("")) {
					// Questo caso si verifica quando l'host o il
					// superutente non sono abilitati ad effettuare
					// operazioni
					DataElement errore = new DataElement();
					errore.setLabel(OperazioniCostanti.LABEL_OPERAZIONI_PARAMETRO_ERRORE);
					errore.setValue(formatDetail(op.getDetails()));
					errore.setName(OperazioniCostanti.PARAMETRO_OPERAZIONI_ERRORE);
					dati.addElement(errore);
				}
			} else {
				DataElement timexecute = new DataElement();
				if (op.isDeleted()) {
					timexecute.setLabel(OperazioniCostanti.LABEL_OPERAZIONI_ELIMINATA_IL);
				} else {
					timexecute.setLabel(OperazioniCostanti.LABEL_OPERAZIONI_ESEGUITA_IL);
				}
				timexecute.setValue("" + op.getTimeExecute());
				timexecute.setName(OperazioniCostanti.PARAMETRO_OPERAZIONI_TEMPO_ESECUZIONE);
				dati.addElement(timexecute);
			}
			if (OperationStatus.ERROR.equals(op.getStatus()) ||
					OperationStatus.INVALID.equals(op.getStatus()) ||
					OperationStatus.WAIT.equals(op.getStatus()) ) {
				DataElement errore = new DataElement();
				errore.setLabel(OperazioniCostanti.LABEL_OPERAZIONI_PARAMETRO_ERRORE);
				errore.setValue(formatDetail(op.getDetails()));
				errore.setName(OperazioniCostanti.PARAMETRO_OPERAZIONI_ERRORE);
				dati.addElement(errore);
			}
			if (OperationStatus.WAIT.equals(op.getStatus())) {
				DataElement timeWait = new DataElement();
				timeWait.setLabel(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_WAITING_TIME);
				String tw = "";
				String val = op.getStatus().toString();
				if (val.equals(OperazioniCostanti.DEFAULT_VALUE_PARAMETRO_OPERAZIONI_WAIT_TIME_WAIT)) {
					tw = "" + op.getWaitTime();
				}
				timeWait.setValue(tw);
				timeWait.setName(OperazioniCostanti.PARAMETRO_OPERAZIONI_WAIT_TIME);
				dati.addElement(timeWait);
			}

			DataElement partitle = new DataElement();
			partitle.setType(DataElementType.TITLE);
			partitle.setLabel(OperazioniCostanti.LABEL_PARAMETRI);
			dati.addElement(partitle);

			org.openspcoop2.web.lib.queue.dao.Parameter[] parList = op.getParameters();
			for (int i = 0; i < parList.length; i++) {
				org.openspcoop2.web.lib.queue.dao.Parameter singlePar = parList[i];

				DataElement par = new DataElement();
				String name = singlePar.getName();
				par.setLabel(name);
				if (name.equals(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_PASSWORD) ||
						name.equals(OperazioniCostanti.LABEL_PARAMETRO_OPERAZIONI_NUOVA_PASSWORD)) {
					par.setValue("********");
				} else {
					par.setValue(singlePar.getValue()); 
				}
				par.setType(DataElementType.TEXT);
				par.setName(OperazioniCostanti.PARAMETRO_OPERAZIONI_PAR);
				dati.addElement(par);
			}

			this.pd.setDati(dati);
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	private String formatDetail(String detail){
		if(detail==null){
			return null;
		}
		else{
			return detail.replaceAll("\n", "<BR/>");
		}
	}
}

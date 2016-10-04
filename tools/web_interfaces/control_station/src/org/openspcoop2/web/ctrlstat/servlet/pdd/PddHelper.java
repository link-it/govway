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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * PddHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PddHelper extends ConsoleHelper {

	public PddHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) {
		super(request, pd,  session);
	}

	
	/**
	 * Controlla se una Porta di Dominio e' di tipo esterno
	 * 
	 * @param pdd
	 * @return indicazione se la pdd è esterna
	 */
	public boolean isPdDEsterna(PdDControlStation pdd) {

		return PddTipologia.ESTERNO.toString().equals(pdd.getTipo());

	}
	public boolean isPdDOperativa(PdDControlStation pdd) {

		return PddTipologia.OPERATIVO.equals(pdd.getTipo());

	}
	public boolean isPdDNonOperativa(PdDControlStation pdd) {

		return PddTipologia.NONOPERATIVO.equals(pdd.getTipo());

	}
	
	public Vector<DataElement> addPddToDati(Vector<DataElement> dati, String nome, String id, String ip, String subject, String password, String confpw, PddTipologia tipo, 
			TipoOperazione tipoOp, String[] protocolli, String protocollo, String protocolloGestione, int porta, String descrizione, 
			String ipGestione, int portaGestione, String implementazione, String clientAuth, boolean singlePdd) throws DriverRegistroServiziException {
		
		/** Id della Porta di Dominio */
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			DataElement de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(PddCostanti.PARAMETRO_PDD_ID);
			dati.addElement(de);
		}
		
		/** Nome della Porta di Dominio */
		DataElement de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_NOME);
		if (tipoOp.equals(TipoOperazione.ADD) || this.core.isSinglePdD()) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		} else {
			de.setType(DataElementType.TEXT);
		}
		de.setName(PddCostanti.PARAMETRO_PDD_NOME);
		de.setValue(nome);
		de.setSize(this.getSize());
		dati.addElement(de);

		/** Descrizione della Porta di Dominio */
		de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_DESCRIZIONE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PddCostanti.PARAMETRO_PDD_DESCRIZIONE);
		de.setValue(descrizione);
		de.setSize(this.getSize());
		dati.addElement(de);

		if (!singlePdd) {
			
			/** Protocollo (solo in caso di pdd control station) */
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_PROTOCOLLO);
			if (tipoOp.equals(TipoOperazione.ADD) || !tipo.equals(PddTipologia.ESTERNO)){
				de.setType(DataElementType.SELECT);
			}else
				de.setType(DataElementType.HIDDEN);
			de.setName(PddCostanti.PARAMETRO_PDD_PROTOCOLLO);
			de.setValues(protocolli);
			de.setSelected(protocollo);
			dati.addElement(de);

			/** Indirizzo Pubblico (solo in caso di pdd control station) */
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_INDIRIZZO_PUBBLICO);
			if (tipoOp.equals(TipoOperazione.ADD) || !tipo.equals(PddTipologia.ESTERNO)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}else
				de.setType(DataElementType.HIDDEN);
			de.setName(PddCostanti.PARAMETRO_PDD_INDIRIZZO_PUBBLICO);
			de.setValue(ip);
			de.setSize(this.getSize());
			dati.addElement(de);

			/** Porta Pubblica (solo in caso di pdd control station) */
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_PORTA_PUBBLICA);
			if (tipoOp.equals(TipoOperazione.ADD) || !tipo.equals(PddTipologia.ESTERNO)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}else
				de.setType(DataElementType.HIDDEN);
			de.setName(PddCostanti.PARAMETRO_PDD_PORTA_PUBBLICA);
			de.setValue(porta <= 0 ? PddCostanti.DEFAULT_PDD_PORTA : "" + porta);
			de.setSize(this.getSize());
			dati.addElement(de);

			/** Protocollo Gestione (solo in caso di pdd control station) */
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_PROTOCOLLO_GESTIONE);
			if (tipoOp.equals(TipoOperazione.ADD) || !tipo.equals(PddTipologia.ESTERNO)){
				de.setType(DataElementType.SELECT);
			}else
				de.setType(DataElementType.HIDDEN);
			de.setName(PddCostanti.PARAMETRO_PDD_PROTOCOLLO_GESTIONE);
			de.setValues(protocolli);
			de.setSelected(protocolloGestione);
			dati.addElement(de);
			
			/** Indirizzo Gestione (solo in caso di pdd control station) */
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_INDIRIZZO_GESTIONE);
			if (tipoOp.equals(TipoOperazione.ADD) || !tipo.equals(PddTipologia.ESTERNO)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}else
				de.setType(DataElementType.HIDDEN);
			de.setName(PddCostanti.PARAMETRO_PDD_INDIRIZZO_GESTIONE);
			de.setValue(ipGestione);
			de.setSize(this.getSize());
			dati.addElement(de);

			/** Porta Gestione (solo in caso di pdd control station) */
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_PORTA_GESTIONE);
			if (tipoOp.equals(TipoOperazione.ADD) || !tipo.equals(PddTipologia.ESTERNO)){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}else
				de.setType(DataElementType.HIDDEN);
			de.setName(PddCostanti.PARAMETRO_PDD_PORTA_GESTIONE);
			de.setValue(portaGestione <= 0 ? PddCostanti.DEFAULT_PDD_PORTA : "" + portaGestione);
			de.setSize(this.getSize());
			dati.addElement(de);

			/** Tipologia della Pdd (solo in caso di pdd control station) */
			// In caso di creazione posso scegliere il tipo che voglio
			// Se tipo=='esterno' non posso cambiare il tipo
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_TIPOLOGIA);
			de.setType(DataElementType.SELECT);
			de.setName(PddCostanti.PARAMETRO_PDD_TIPOLOGIA);
			if (tipoOp.equals(TipoOperazione.ADD)) {
				de.setLabels(PddCostanti.LABEL_TIPI);
				de.setValues(PddTipologia.TIPI);
			} else if (tipoOp.equals(TipoOperazione.CHANGE)) {
				if (tipo.equals(PddTipologia.ESTERNO)) {
					de.setLabels(PddCostanti.LABEL_TIPO_SOLO_ESTERNO);
					de.setValues(PddTipologia.TIPO_SOLO_ESTERNO);
				} else {
					de.setLabels(PddCostanti.LABEL_TIPI_SOLO_OPERATIVI);
					de.setValues(PddTipologia.TIPI_SOLO_OPERATIVI);
				}
			}
			de.setSelected(tipo.toString());
			if(this.core.isSinglePdD()){
				de.setPostBack(true);	
			}
			else{
				de.setOnChangeAlternativePostBack("changePdDType()");
			}
			dati.addElement(de);
		}else{
			
			/** Tipologia della Pdd */
			if(TipoOperazione.ADD.equals(tipoOp)==false){
				// SinglePdD
				de = new DataElement();
				de.setLabel(PddCostanti.LABEL_PDD_TIPOLOGIA);
				de.setType(DataElementType.TEXT);
				de.setName(PddCostanti.PARAMETRO_PDD_TIPOLOGIA);
				de.setValue(tipo.toString());
				dati.addElement(de);
			}
		}

		/** Implementazione della Pdd */
		de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_IMPLEMENTAZIONE);
		if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(PddCostanti.PARAMETRO_PDD_IMPLEMENTAZIONE);
		de.setValue(implementazione);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		/** ClientAuth della Pdd */
		String[] tipiAuth = { CostantiConfigurazione.DISABILITATO.toString(), CostantiConfigurazione.ABILITATO.toString() };
		de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_CLIENT_AUTH);
		de.setType(DataElementType.SELECT);
		de.setName(PddCostanti.PARAMETRO_PDD_CLIENT_AUTH);
		de.setSelected(clientAuth);
		de.setValues(tipiAuth);
		de.setSize(this.getSize());
//		de.setOnChange("CambiaClientAuth('" + tipoOp + "'," + singlePdd + ")");
		de.setPostBack(true);
		dati.addElement(de);
		
		/** Subject della Pdd */
		de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_SUBJECT);
		//if (clientAuth != null && clientAuth.equals("abilitato")) {
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(subject);
		if (clientAuth != null && clientAuth.equals(CostantiRegistroServizi.ABILITATO)) {
			de.setRequired(true);
		}
		else if(this.pddCore.fruzioniWithClientAuthAbilitato(nome).size() > 0 ){
			de.setRequired(true);
		}
		//}  else {
		//    de.setType(DataElementType.HIDDEN);
		//    de.setValue("");
		//}
		de.setName(PddCostanti.PARAMETRO_PDD_SUBJECT);
		de.setSize(this.getSize());
		dati.addElement(de);

		return dati;
	}
	
	
	// Controlla i dati dei Pdd
	boolean pddCheckData(TipoOperazione tipoOp, boolean singlePdd)
			throws Exception {

		try{

			String nome = this.request.getParameter(PddCostanti.PARAMETRO_PDD_NOME);
			String ip = this.request.getParameter(PddCostanti.PARAMETRO_PDD_INDIRIZZO_PUBBLICO);
			String ipGestione = this.request.getParameter(PddCostanti.PARAMETRO_PDD_INDIRIZZO_GESTIONE);
			String tipoParam = this.request.getParameter(PddCostanti.PARAMETRO_PDD_TIPOLOGIA);
			PddTipologia tipo = PddTipologia.toPddTipologia(tipoParam); 
			String implementazione = this.request.getParameter(PddCostanti.PARAMETRO_PDD_IMPLEMENTAZIONE);
			String clientAuth = this.request.getParameter(PddCostanti.PARAMETRO_PDD_CLIENT_AUTH);
			String subject = this.request.getParameter(PddCostanti.PARAMETRO_PDD_SUBJECT);
			String porta = this.request.getParameter(PddCostanti.PARAMETRO_PDD_PORTA_PUBBLICA);
			String portaGestione = this.request.getParameter(PddCostanti.PARAMETRO_PDD_PORTA_GESTIONE);

			if (implementazione == null || "".equals(implementazione)) {
				this.pd.setMessage("Il campo Implementazione deve essere specificato.");
				return false;
			}

			if(subject!=null && !"".equals(subject)){
				try{
					org.openspcoop2.utils.Utilities.validaSubject(subject);
				}catch(Exception e){
					this.pd.setMessage("Il subject fornito non è valido: "+e.getMessage());
					return false;
				}
			}

			if (CostantiConfigurazione.ABILITATO.toString().equals(clientAuth)){
				if (subject == null || "".equals(subject)) {
					this.pd.setMessage("E' necessario specificare il subject in caso di Client Auth abilitato.");
					return false;
				}
			}
			else{
				List<String> listaFruzioni = this.pddCore.fruzioniWithClientAuthAbilitato(nome);
				if(listaFruzioni.size() > 0 ){
					if (subject == null || "".equals(subject)) {
						StringBuffer bf = new StringBuffer();
						bf.append("Un subject deve essere obbligatoriamente definito poichè esistono ");
						bf.append("fruitori");
						bf.append(" che richiedono l'autenticazione della PdD mittente (client-auth):");
						for (int i = 0; i < listaFruzioni.size(); i++) {
							bf.append("<BR> -");
							bf.append(listaFruzioni.get(i));
						}
						this.pd.setMessage(bf.toString());
						return false;
					}
				}
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!singlePdd) {
				if (!tipo.equals(PddTipologia.OPERATIVO) && !tipo.equals(PddTipologia.NONOPERATIVO) && !tipo.equals(PddTipologia.ESTERNO)) {
					this.pd.setMessage("Tipo dev'essere operativo, non-operativo o esterno");
					return false;
				}
			}

			if (singlePdd || (!singlePdd && tipo.equals(PddTipologia.ESTERNO))) {
				// campi obbligatori

				// nome
				if ((nome == null) || nome.equals("")) {
					this.pd.setMessage("Dati incompleti. E' necessario indicare il Nome.");
					return false;
				}

				// controllo spazi
				if ((nome.indexOf(" ") != -1)) {
					this.pd.setMessage("Non inserire spazi nei campi di testo");
					return false;
				}

				// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
				if (!RegularExpressionEngine.isMatch(nome, "^[0-9A-Za-z_\\-\\.]+$")) {
					this.pd.setMessage("Il nome della pdd dev'essere formato solo caratteri, cifre, '_' , '-' e '.'");
					return false;
				}

			} else {// in caso di operativo o non-operativo
				// Campi obbligatori
				if (nome.equals("") || ip.equals("") || ipGestione.equals("") || porta.equals("") || portaGestione.equals("")) {
					String tmpElenco = "";
					if (nome.equals("")) {
						tmpElenco = "Nome";
					}
					if (ip.equals("")) {
						if (tmpElenco.equals("")) {
							tmpElenco = "Indirizzo IP";
						} else {
							tmpElenco = tmpElenco + ", Indirizzo IP";
						}
					}
					if (ipGestione.equals("")) {
						if (tmpElenco.equals("")) {
							tmpElenco = "Indirizzo IP Gestione";
						} else {
							tmpElenco = tmpElenco + ", Indirizzo IP Gestione";
						}
					}
					if (porta.equals("")) {
						if (tmpElenco.equals("")) {
							tmpElenco = "Porta";
						} else {
							tmpElenco = tmpElenco + ", Porta";
						}
					}
					if (portaGestione.equals("")) {
						if (tmpElenco.equals("")) {
							tmpElenco = "Porta Gestione";
						} else {
							tmpElenco = tmpElenco + ", Porta Gestione";
						}
					}
					this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
					return false;
				}

				// Controllo che non ci siano spazi nei campi di testo
				if ((nome.indexOf(" ") != -1) || (ip.indexOf(" ") != -1) || (ipGestione.indexOf(" ") != -1) || (porta.indexOf(" ") != -1) || (portaGestione.indexOf(" ") != -1)) {
					this.pd.setMessage("Non inserire spazi nei campi di testo");
					return false;
				}

				// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
				if (!RegularExpressionEngine.isMatch(nome,"^[0-9A-Za-z_\\-\\.]+$")) {
					this.pd.setMessage("Il nome della pdd dev'essere formato solo caratteri, cifre, '_' , '-' e '.'");
					return false;
				}

				// ip dev'essere un indirizzo ip o un hostname valido
				if (!org.openspcoop2.utils.regexp.RegExpUtilities.isIPOrHostname(ip)) {
					this.pd.setMessage("Indirizzo pubblico dev'essere un ip o un hostname valido");
					return false;
				}

				// ip_gestione dev'essere un indirizzo ip o un hostname valido
				if (!org.openspcoop2.utils.regexp.RegExpUtilities.isIPOrHostname(ipGestione)) {
					this.pd.setMessage("Indirizzo gestione dev'essere un ip o un hostname valido");
					return false;
				}

				// La porta deve essere un numero
				if (!RegularExpressionEngine.isMatch(porta,"^[0-9]+$")) {
					this.pd.setMessage("La porta della pdd dev'essere formato solo cifre");
					return false;
				}

				// La porta gestione deve essere un numero
				if (!RegularExpressionEngine.isMatch(portaGestione,"^[0-9]+$")) {
					this.pd.setMessage("La porta di gestione della pdd dev'essere formato solo cifre");
					return false;
				}
			}

			// Se tipoOp = add, controllo che il pdd non sia gia' stato registrato
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean trovatoPdd = this.pddCore.existPdd(nome) > 0;
				if (trovatoPdd) {
					this.pd.setMessage("Esiste gi&agrave; una Porta di Dominio con nome " + nome);
					return false;
				}
			}

			// Controllo non esista un altra pdd con stesso subject
			if(subject!=null && !"".equals(subject)){
				List<PortaDominio> pddList = this.pddCore.porteDominioWithSubject(subject);
				for (int i = 0; i < pddList.size(); i++) {
					PortaDominio pdd = pddList.get(i);

					if ((tipoOp.equals(TipoOperazione.CHANGE)) && (nome.equals(pdd.getNome()))) {
						continue;
					}

					// Messaggio di errore
					this.pd.setMessage("La Porta di Dominio " + pdd.getNome() + " possiede gia' le credenziali ssl indicate.");
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	public void preparePddSinglePddList(List<PdDControlStation> lista, ISearch ricerca) throws Exception {
		try {

			ServletUtils.addListElementIntoSession(this.session, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD);

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			int idLista = Liste.PDD;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,null), 
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA,null));	
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PddCostanti.LABEL_PORTE_DI_DOMINIO, search);
			}

			// setto le label delle colonne
			String[] labels = { PddCostanti.LABEL_PDD_NOME, PddCostanti.LABEL_PDD_TIPOLOGIA, PddCostanti.LABEL_PDD_IMPLEMENTAZIONE, PddCostanti.LABEL_PDD_SOGGETTI };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<PdDControlStation> it = lista.iterator();
			PdDControlStation pdd = null;
			while (it.hasNext()) {
				pdd = it.next();
				Vector<DataElement> e = new Vector<DataElement>();

				DataElement de = new DataElement();
				de.setUrl(PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_CHANGE,
						new Parameter(PddCostanti.PARAMETRO_PDD_ID, pdd.getId().toString()),
						new Parameter(PddCostanti.PARAMETRO_PDD_NOME, pdd.getNome()));
				de.setValue(pdd.getNome());
				de.setIdToRemove(pdd.getNome());
				e.addElement(de);

				de = new DataElement();
				de.setValue(pdd.getTipo());
				e.addElement(de);

				de = new DataElement();
				de.setValue(pdd.getImplementazione());
				e.addElement(de);

				de = new DataElement();
				de.setUrl(PddCostanti.SERVLET_NAME_PDD_SOGGETTI_LIST,
						new Parameter(PddCostanti.PARAMETRO_PDD_ID, pdd.getId().toString()));
				if (contaListe) {
					List<org.openspcoop2.core.config.Soggetto> lista1 = this.pddCore.pddSoggettiList(pdd.getId().intValue(), new Search(true));
					int numSog = 0;
					if (lista1 != null)
						numSog = lista1.size();
					ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSog));
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);

				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void preparePddList(List<PdDControlStation> lista, ISearch ricerca) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, PddCostanti.OBJECT_NAME_PDD);
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			int idLista = Liste.PDD;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,null), 
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PddCostanti.SERVLET_NAME_PDD_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA,null));	
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PddCostanti.LABEL_PORTE_DI_DOMINIO, search);
			}

			User user = ServletUtils.getUserFromSession(this.session);
			boolean showConfigurazioneSistema = user!=null && user.getPermessi()!=null && user.getPermessi().isSistema() &&
					this.pddCore.getJmxPdD_aliases()!=null && this.pddCore.getJmxPdD_aliases().size()>0;
			
			// setto le label delle colonne
			List<String> listLabels = new ArrayList<String>();
			listLabels.add(PddCostanti.LABEL_PDD_NOME);
			listLabels.add(PddCostanti.LABEL_PDD_INDIRIZZO);
			listLabels.add(PddCostanti.LABEL_PDD_TIPOLOGIA);
			listLabels.add(PddCostanti.LABEL_PDD_IMPLEMENTAZIONE);
			if(showConfigurazioneSistema){
				listLabels.add(PddCostanti.LABEL_PDD_CONFIGURAZIONE_SISTEMA);
			}
			listLabels.add(PddCostanti.LABEL_PDD_SOGGETTI);
			String[] labels = listLabels.toArray(new String[1]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<PdDControlStation> it = lista.iterator();
			PdDControlStation pdd = null;
			while (it.hasNext()) {
				pdd = it.next();
				Vector<DataElement> e = new Vector<DataElement>();

				DataElement de = new DataElement();
				de.setUrl(PddCostanti.SERVLET_NAME_PDD_CHANGE,
						new Parameter(PddCostanti.PARAMETRO_PDD_ID, pdd.getId().toString()),
						new Parameter(PddCostanti.PARAMETRO_PDD_NOME, pdd.getNome()));
				de.setValue(pdd.getNome());
				de.setIdToRemove(pdd.getId().toString());
				e.addElement(de);

				de = new DataElement();
				if (PddTipologia.ESTERNO.toString().equals(pdd.getTipo()))
					de.setValue("");
				else
					de.setValue(pdd.getIp());
				e.addElement(de);

				de = new DataElement();
				de.setValue(pdd.getTipo());
				e.addElement(de);

				de = new DataElement();
				de.setValue(pdd.getImplementazione());
				e.addElement(de);

				if(showConfigurazioneSistema){
					de = new DataElement();
					if(PddTipologia.OPERATIVO.toString().equals(pdd.getTipo())){
						de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD,
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER, pdd.getNome()));
						de.setValue(Costanti.LABEL_VISUALIZZA);
					}
					else{
						de.setType(DataElementType.TEXT);
						de.setValue("-");
					}
					e.addElement(de);
				}
				
				de = new DataElement();
				de.setUrl(PddCostanti.SERVLET_NAME_PDD_SOGGETTI_LIST,
						new Parameter(PddCostanti.PARAMETRO_PDD_ID, pdd.getId().toString()));
				if (contaListe) {
					List<org.openspcoop2.core.config.Soggetto> lista1 = this.pddCore.pddSoggettiList(pdd.getId().intValue(), new Search(true));
					int numSog = 0;
					if (lista1 != null)
						numSog = lista1.size();
					ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSog));
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);				

				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	// Prepara la lista di soggetti associati al pdd
	public void preparePddSoggettiList(String nomePdd, int id, List<org.openspcoop2.core.config.Soggetto> lista, ISearch ricerca)
			throws Exception {
		try {
			
			ServletUtils.addListElementIntoSession(this.session, PddCostanti.OBJECT_NAME_PDD_SOGGETTI,
					new Parameter(PddCostanti.PARAMETRO_PDD_ID, id+""));
			
			Boolean singlePdD = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
			
			int idLista = Liste.PDD_SOGGETTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
		
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// setto la barra del titolo
			String t2URL = PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST;
			boolean sPdD = singlePdD != null ? singlePdD.booleanValue() : false;
			if (sPdD == false){
				t2URL = PddCostanti.SERVLET_NAME_PDD_LIST;
			}
			String t3Label = "Soggetti associati alla Porta di Dominio " + nomePdd;
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,t2URL),
						new Parameter(t3Label,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,null), 
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, t2URL),
						new Parameter(t3Label, PddCostanti.SERVLET_NAME_PDD_SOGGETTI_LIST+"?"+PddCostanti.PARAMETRO_PDD_ID+"="+id),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA,null));	
			}
			

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, PddCostanti.LABEL_SOGGETTI, search);
			}

			// setto le label delle colonne
			String[] labels = { PddCostanti.LABEL_SOGGETTI };
			this.pd.setLabels(labels);


			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();
			// preparo i dati
			if (lista != null) {
				Iterator<org.openspcoop2.core.config.Soggetto> it = lista.iterator();
				while (it.hasNext()) {
					org.openspcoop2.core.config.Soggetto sog = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					//if (idsSogg.contains(sog.getId()))
					de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, sog.getId().toString()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, sog.getNome()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, sog.getTipo()));
					de.setValue(sog.getTipo() + "/" + sog.getNome());
					e.addElement(de);

					dati.addElement(e);
				}
			}
			
			this.pd.setDati(dati);
			this.pd.setAddButton(false);
			this.pd.setSelect(false);
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
}

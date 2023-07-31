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
package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

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
	public PddHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) {
		super(core, request, pd,  session);
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
	
	public List<DataElement> addPddToDati(List<DataElement> dati, String nome, String id, String ip, String subject, String password, String confpw, PddTipologia tipo, 
			TipoOperazione tipoOp, String[] protocolli, String protocollo, String protocolloGestione, int porta, String descrizione, 
			String ipGestione, int portaGestione, String implementazione, String clientAuth) {
		
		DataElement de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PORTA_DI_DOMINIO);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		/** Id della Porta di Dominio */
		if (tipoOp.equals(TipoOperazione.CHANGE)) {
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(PddCostanti.PARAMETRO_PDD_ID);
			dati.add(de);
		}
		
		/** Nome della Porta di Dominio */
		de = new DataElement();
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
		dati.add(de);

		/** Descrizione della Porta di Dominio */
		de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_DESCRIZIONE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(PddCostanti.PARAMETRO_PDD_DESCRIZIONE);
		de.setValue(descrizione);
		de.setSize(this.getSize());
		dati.add(de);
			
		/** Tipologia della Pdd */
		if(!TipoOperazione.ADD.equals(tipoOp)){
			de = new DataElement();
			de.setLabel(PddCostanti.LABEL_PDD_TIPOLOGIA);
			de.setType(DataElementType.TEXT);
			de.setName(PddCostanti.PARAMETRO_PDD_TIPOLOGIA);
			de.setValue(tipo.toString());
			dati.add(de);
		}

		/** Implementazione della Pdd */
		de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_IMPLEMENTAZIONE);
		if (this.isModalitaAvanzata()) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(PddCostanti.PARAMETRO_PDD_IMPLEMENTAZIONE);
		de.setValue(implementazione);
		de.setSize(this.getSize());
		dati.add(de);
		
		/** ClientAuth della Pdd */
		String[] tipiAuth = { CostantiConfigurazione.DISABILITATO.toString(), CostantiConfigurazione.ABILITATO.toString() };
		de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_CLIENT_AUTH);
		de.setType(DataElementType.SELECT);
		de.setName(PddCostanti.PARAMETRO_PDD_CLIENT_AUTH);
		de.setSelected(clientAuth);
		de.setValues(tipiAuth);
		de.setSize(this.getSize());
		de.setPostBack(true);
		dati.add(de);
		
		/** Subject della Pdd */
		de = new DataElement();
		de.setLabel(PddCostanti.LABEL_PDD_SUBJECT);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(StringEscapeUtils.escapeHtml(subject));
		if (clientAuth != null && clientAuth.equals(CostantiRegistroServizi.ABILITATO.getValue())) {
			de.setRequired(true);
		}
		de.setName(PddCostanti.PARAMETRO_PDD_SUBJECT);
		de.setSize(this.getSize());
		dati.add(de);

		return dati;
	}
	
	
	// Controlla i dati dei Pdd
	boolean pddCheckData(TipoOperazione tipoOp) throws ControlStationCoreException {

		try{

			String nome = this.getParameter(PddCostanti.PARAMETRO_PDD_NOME);
			String tipoParam = this.getParameter(PddCostanti.PARAMETRO_PDD_TIPOLOGIA);
			PddTipologia tipo = PddTipologia.toPddTipologia(tipoParam); 
			String implementazione = this.getParameter(PddCostanti.PARAMETRO_PDD_IMPLEMENTAZIONE);
			String clientAuth = this.getParameter(PddCostanti.PARAMETRO_PDD_CLIENT_AUTH);
			String subject = this.getParameter(PddCostanti.PARAMETRO_PDD_SUBJECT);

			if (implementazione == null || "".equals(implementazione)) {
				this.pd.setMessage("Il campo Implementazione deve essere specificato.");
				return false;
			}

			if(subject!=null && !"".equals(subject)){
				try{
					org.openspcoop2.utils.certificate.CertificateUtils.validaPrincipal(subject, PrincipalType.SUBJECT);
				}catch(Exception e){
					this.pd.setMessage("Il subject fornito non è valido: "+e.getMessage());
					return false;
				}
			}

			if (CostantiConfigurazione.ABILITATO.toString().equals(clientAuth) &&
				(subject == null || "".equals(subject))
				){
				this.pd.setMessage("&Egrave; necessario specificare il subject in caso di Client Auth abilitato.");
				return false;
			}

			if (tipo.equals(PddTipologia.ESTERNO)) {
				// campi obbligatori

				// nome
				if ((nome == null) || nome.equals("")) {
					this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare il Nome.");
					return false;
				}
				
				// controllo spazi
				if ((nome.indexOf(" ") != -1)) {
					this.pd.setMessage("Non inserire spazi nei campi di testo");
					return false;
				}
				
				// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
				if(this.checkNCName(nome, PddCostanti.LABEL_PDD_NOME)==false){
					return false;
				}

			} else {// in caso di operativo o non-operativo
				// Campi obbligatori
				if (nome.equals("")) {
					String tmpElenco = "";
					if (nome.equals("")) {
						tmpElenco = "Nome";
					}
					this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare: " + tmpElenco);
					return false;
				}

				// Controllo che non ci siano spazi nei campi di testo
				if ((nome.indexOf(" ") != -1)) {
					this.pd.setMessage("Non inserire spazi nei campi di testo");
					return false;
				}

				// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
				if(this.checkSimpleName(nome, PddCostanti.LABEL_PDD_NOME)==false){
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
			this.logError(e.getMessage(), e);
			throw new ControlStationCoreException(e.getMessage(),e);
		}
	}


	public void preparePddSinglePddList(List<PdDControlStation> lista, ISearch ricerca) throws ControlStationCoreException {
		try {

			ServletUtils.addListElementIntoSession(this.request, this.session, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD);

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
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST),
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
			List<List<DataElement>> dati = new ArrayList<>();

			Iterator<PdDControlStation> it = lista.iterator();
			PdDControlStation pdd = null;
			while (it.hasNext()) {
				pdd = it.next();
				List<DataElement> e = new ArrayList<>();

				DataElement de = new DataElement();
				de.setUrl(PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_CHANGE,
						new Parameter(PddCostanti.PARAMETRO_PDD_ID, pdd.getId().toString()),
						new Parameter(PddCostanti.PARAMETRO_PDD_NOME, pdd.getNome()));
				de.setValue(pdd.getNome());
				de.setIdToRemove(pdd.getNome());
				e.add(de);

				de = new DataElement();
				de.setValue(pdd.getTipo());
				e.add(de);

				de = new DataElement();
				de.setValue(pdd.getImplementazione());
				e.add(de);

				de = new DataElement();
				de.setUrl(PddCostanti.SERVLET_NAME_PDD_SOGGETTI_LIST,
						new Parameter(PddCostanti.PARAMETRO_PDD_ID, pdd.getId().toString()));
				if (contaListe!=null && contaListe.booleanValue()) {
					// BugFix OP-674
					ConsoleSearch searchForCount = new ConsoleSearch(true,1);
					this.pddCore.pddSoggettiList(pdd.getId().intValue(), searchForCount);
					int numSog = 0;
					numSog = searchForCount.getNumEntries(Liste.PDD_SOGGETTI);
					ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numSog));
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.add(de);

				dati.add(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.logError(e.getMessage(), e);
			throw new ControlStationCoreException(e.getMessage(),e);
		}
	}

	
	// Prepara la lista di soggetti associati al pdd
	public void preparePddSoggettiList(String nomePdd, int id, List<org.openspcoop2.core.config.Soggetto> lista, ISearch ricerca)
			throws ControlStationCoreException {
		try {
			
			ServletUtils.addListElementIntoSession(this.request, this.session, PddCostanti.OBJECT_NAME_PDD_SOGGETTI,
					new Parameter(PddCostanti.PARAMETRO_PDD_ID, id+""));
			
			int idLista = Liste.PDD_SOGGETTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
		
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// setto la barra del titolo
			String t2URL = PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST;
			String t3Label = "Soggetti associati alla Porta di Dominio " + nomePdd;
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,t2URL),
						new Parameter(t3Label,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(PddCostanti.LABEL_PORTE_DI_DOMINIO,t2URL), 
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


			List<List<DataElement>> dati = new ArrayList<>();
			// preparo i dati
			if (lista != null) {
				Iterator<org.openspcoop2.core.config.Soggetto> it = lista.iterator();
				while (it.hasNext()) {
					org.openspcoop2.core.config.Soggetto sog = it.next();

					List<DataElement> e = new ArrayList<>();

					DataElement de = new DataElement();
					de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, sog.getId().toString()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, sog.getNome()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, sog.getTipo()));
					de.setValue(sog.getTipo() + "/" + sog.getNome());
					e.add(de);

					dati.add(e);
				}
			}
			
			this.pd.setDati(dati);
			this.pd.setAddButton(false);
			this.pd.setSelect(false);
			
		} catch (Exception e) {
			this.logError(e.getMessage(), e);
			throw new ControlStationCoreException(e.getMessage(),e);
		}
	}
}

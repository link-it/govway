/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.gruppi;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * GruppiHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GruppiHelper extends ConsoleHelper{

	public GruppiHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public GruppiHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	public Vector<DataElement> addGruppoToDati(TipoOperazione tipoOP, Long gruppoId, String nome, String descrizione, String serviceBinding, Vector<DataElement> dati) {
		
		if(TipoOperazione.CHANGE.equals(tipoOP)){
			
			// In Uso Button
			this.addComandoInUsoButton(dati, nome,
					nome,
					InUsoType.GRUPPO);
						
		}
		
		DataElement de = new DataElement();
		de.setLabel(GruppiCostanti.LABEL_GRUPPO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		if(gruppoId!=null){
			de = new DataElement();
			de.setLabel(GruppiCostanti.PARAMETRO_GRUPPO_ID);
			de.setValue(gruppoId.longValue()+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(GruppiCostanti.PARAMETRO_GRUPPO_ID);
			de.setSize( getSize());
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME);
		de.setValue(nome);
		//if(TipoOperazione.ADD.equals(tipoOP)){
		de.setType(DataElementType.TEXT_EDIT);
		//}
		//else{
		//	de.setType(DataElementType.TEXT);
		//}
		de.setName(GruppiCostanti.PARAMETRO_GRUPPO_NOME);
		de.setSize( getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(GruppiCostanti.PARAMETRO_GRUPPO_DESCRIZIONE);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING);
		de.setType(DataElementType.SELECT);
		de.setName(GruppiCostanti.PARAMETRO_GRUPPO_SERVICE_BINDING);
		de.setLabels(GruppiCostanti.LABELS_SELECT_PARAMETRO_GRUPPO_SERVICE_BINDING);
		de.setValues(GruppiCostanti.VALUES_SELECT_PARAMETRO_GRUPPO_SERVICE_BINDING);
		de.setSelected(serviceBinding);
		dati.addElement(de);
	
		return dati;
	}
	
	
	// Controlla i dati del registro
	public boolean gruppoCheckData(TipoOperazione tipoOp, Gruppo gruppo) throws Exception {

		try{

			String nome = this.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_NOME);
			String descrizione = this.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_DESCRIZIONE);
			String serviceBinding = this.getParameter(GruppiCostanti.PARAMETRO_GRUPPO_SERVICE_BINDING);
			
			// Campi obbligatori
			if (nome.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME;
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nel campo '"+GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME+"'");
				return false;
			}
			if(this.checkNCName(nome, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME)==false){
				return false;
			}
			if(this.checkLength255(nome, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME)==false) {
				return false;
			}
			
			if(descrizione!=null && !"".equals(descrizione)) {
				if(this.checkLength255(descrizione, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_DESCRIZIONE)==false) {
					return false;
				}
			}

			// Se tipoOp = add, controllo che il registro non sia gia' stato
			// registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				
				if(this.gruppiCore.existsGruppo(nome)){
					this.pd.setMessage("Un gruppo con nome '" + nome + "' risulta gi&agrave; stato registrato");
					return false;
				}
				
			}
			else{
				
				// se ho modificato il service binding controllo che il gruppo non sia usato in un accordo con service binding non piu' compatibile.
				String oldServiceBinding = GruppiCostanti.DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI;
				if(gruppo.getServiceBinding() != null)
					oldServiceBinding = gruppo.getServiceBinding().name();
				
				// casi da controllare:
				// service binding era vuoto e viene impostato a SOAP o REST
				// service binding cambia da SOAP a REST o viceversa
				if(!serviceBinding.equals(GruppiCostanti.DEFAULT_VALUE_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI) && !serviceBinding.equals(oldServiceBinding)){
						// 
					HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
					boolean normalizeObjectIds = !this.isModalitaCompleta();
					boolean gruppoInUso = this.gruppiCore.isGruppoInUso(gruppo.getNome(),whereIsInUso,normalizeObjectIds);
					String newLine = org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
					StringBuilder inUsoMessage = new StringBuilder();
					
					if (gruppoInUso) {
						inUsoMessage.append(DBOggettiInUsoUtils.toString(new IDGruppo(gruppo.getNome()), whereIsInUso, true, newLine, " non modificabile perch&egrave; :"));
						inUsoMessage.append(newLine);
						
						this.pd.setMessage(inUsoMessage.toString());
						return false;
					}
				}
				
				if(gruppo.getNome().equals(nome)==false){
					// e' stato modificato ilnome
					
					// e' stato implementato l'update
//					java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>> whereIsInUso = new java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>>();
//					boolean gruppoInUso = this.confCore.isGruppoInUso(gruppo.getNome(),whereIsInUso);
//					if (gruppoInUso) {
//						String msg = "";
//						msg += org.openspcoop2.core.commons.DBOggettiInUsoUtils.toString(new org.openspcoop2.core.id.IDGruppo(gruppo.getNome()), whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE,
//								" non modificabile in '"+nome+"' perch&egrave; risulta utilizzato:");
//						msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
//						this.pd.setMessage(msg);
//						return false;
//					} 
//					
					if(this.gruppiCore.existsGruppo(nome)){
						this.pd.setMessage("Un gruppo con nome '" + nome + "' risulta gi&agrave; stato registrato");
						return false;
					}
					
				}
				
			}
				
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	// Prepara la lista di gruppi
	public void prepareGruppiList(ISearch ricerca, List<Gruppo> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, GruppiCostanti.OBJECT_NAME_GRUPPI);
			
			this.pd.setCustomListViewName(GruppiCostanti.GRUPPI_NOME_VISTA_CUSTOM_LISTA);

			int idLista = Liste.GRUPPI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			String filterGruppoServiceBinding = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
			this.addFilterServiceBinding(filterGruppoServiceBinding, false, false);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(GruppiCostanti.LABEL_GRUPPI, GruppiCostanti.SERVLET_NAME_GRUPPI_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(GruppiCostanti.LABEL_GRUPPI, GruppiCostanti.SERVLET_NAME_GRUPPI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, GruppiCostanti.LABEL_GRUPPI, search);
			}

			// setto le label delle colonne
//			String[] labels = {
//					GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME,
//					GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING,
//					CostantiControlStation.LABEL_IN_USO_COLONNA_HEADER // inuso
//			};
			
			String[] labels = {
					GruppiCostanti.LABEL_GRUPPI
			};
			
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Gruppo> it = lista.iterator();
				while (it.hasNext()) {
					Vector<DataElement> e = creaEntryGruppoCustom(it);
					
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
			
			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMode(org.openspcoop2.protocol.sdk.constants.ArchiveType.GRUPPO, this.session)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(GruppiCostanti.LABEL_GRUPPI_ESPORTA_SELEZIONATI);
						de.setOnClick(GruppiCostanti.LABEL_GRUPPI_ESPORTA_SELEZIONATI_ONCLICK);
						de.setDisabilitaAjaxStatus();
						otherbott.addElement(de);
						ab.setBottoni(otherbott);
						bottoni.addElement(ab);

						this.pd.setAreaBottoni(bottoni);

					}

				}
			}
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public Vector<DataElement> creaEntryGruppo(Iterator<Gruppo> it) {
		Gruppo gruppo = it.next();

		Vector<DataElement> e = new Vector<DataElement>();

		DataElement de = new DataElement();
		Parameter pId = new Parameter(GruppiCostanti.PARAMETRO_GRUPPO_ID, gruppo.getId()+"");
		de.setUrl(GruppiCostanti.SERVLET_NAME_GRUPPI_CHANGE , pId);
		de.setToolTip(gruppo.getDescrizione());
		de.setValue(gruppo.getNome());
		de.setIdToRemove(gruppo.getNome());
		de.setToolTip(gruppo.getDescrizione());
		e.addElement(de);

		de = new DataElement();
		if(gruppo.getServiceBinding() == null) {
			de.setValue(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI);
		} else {
			switch (gruppo.getServiceBinding()) {
			case REST:
				de.setValue(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_REST);
				break;
			case SOAP:
				de.setValue(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP);
				break;
			}
		}
		e.addElement(de);

		this.addInUsoButtonVisualizzazioneClassica(e, gruppo.getNome(), gruppo.getNome(), InUsoType.GRUPPO);
		return e;
	}
	
	private Vector<DataElement> creaEntryGruppoCustom(Iterator<Gruppo> it) {
		Gruppo gruppo = it.next();

		Vector<DataElement> e = new Vector<DataElement>();
		
		// Titolo (nome)
		DataElement de = new DataElement();
		Parameter pId = new Parameter(GruppiCostanti.PARAMETRO_GRUPPO_ID, gruppo.getId()+"");
		de.setUrl(GruppiCostanti.SERVLET_NAME_GRUPPI_CHANGE , pId);
		de.setValue(gruppo.getNome());
		de.setIdToRemove(gruppo.getNome());
		de.setToolTip(gruppo.getDescrizione());
		de.setType(DataElementType.TITLE);
		e.addElement(de);
		
		
		de = new DataElement();
		if(gruppo.getServiceBinding() == null) {
			de.setValue(MessageFormat.format(GruppiCostanti.MESSAGE_METADATI_GRUPPO_TIPO, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_QUALSIASI));
		} else {
			switch (gruppo.getServiceBinding()) {
			case REST:
				de.setValue(MessageFormat.format(GruppiCostanti.MESSAGE_METADATI_GRUPPO_TIPO, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_REST));
				break;
			case SOAP:
				de.setValue(MessageFormat.format(GruppiCostanti.MESSAGE_METADATI_GRUPPO_TIPO, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING_SOAP));
				break;
			}
		}
		
		de.setType(DataElementType.SUBTITLE);
		e.addElement(de);

		this.addInUsoButton(e, gruppo.getNome(), gruppo.getNome(), InUsoType.GRUPPO);
		
		return e;
	}
}

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
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

	public List<DataElement> addGruppoToDati(TipoOperazione tipoOP, Long gruppoId, String nome, String descrizione, String serviceBinding, List<DataElement> dati) throws DriverConfigurazioneException {
		
		Gruppo gruppo = null;
		if(TipoOperazione.CHANGE.equals(tipoOP) && nome!=null && StringUtils.isNotEmpty(nome)){
			gruppo = this.gruppiCore.getGruppo(nome);
		}
		
		if(TipoOperazione.CHANGE.equals(tipoOP)){
			
			// In Uso Button
			this.addComandoInUsoButton(nome,
					nome,
					InUsoType.GRUPPO);
					
			// Proprieta Button
			if(gruppo!=null && this.existsProprietaOggetto(gruppo.getProprietaOggetto(), gruppo.getDescrizione())) {
				this.addComandoProprietaOggettoButton(nome,
						nome, InUsoType.GRUPPO);
			}
		}
		
		DataElement de = new DataElement();
		de.setLabel(GruppiCostanti.LABEL_GRUPPO);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		if(gruppoId!=null){
			de = new DataElement();
			de.setLabel(GruppiCostanti.PARAMETRO_GRUPPO_ID);
			de.setValue(gruppoId.longValue()+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(GruppiCostanti.PARAMETRO_GRUPPO_ID);
			de.setSize( getSize());
			dati.add(de);
		}
		
		de = new DataElement();
		de.setLabel(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(GruppiCostanti.PARAMETRO_GRUPPO_NOME);
		de.setSize( getSize());
		de.setRequired(true);
		dati.add(de);

		de = new DataElement();
		de.setLabel(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(2);
		de.setName(GruppiCostanti.PARAMETRO_GRUPPO_DESCRIZIONE);
		de.setSize( getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(GruppiCostanti.LABEL_PARAMETRO_GRUPPO_SERVICE_BINDING);
		de.setType(DataElementType.SELECT);
		de.setName(GruppiCostanti.PARAMETRO_GRUPPO_SERVICE_BINDING);
		de.setLabels(GruppiCostanti.getLabelsSelectParametroGruppoServiceBinding());
		de.setValues(GruppiCostanti.getValuesSelectParametroGruppoServiceBinding());
		de.setSelected(serviceBinding);
		dati.add(de);
	
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
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_PREFISSO_DATI_INCOMPLETI_NECESSARIO_INDICARE + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nel campo '"+GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME+"'");
				return false;
			}
			if(!this.checkNCName(nome, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME)){
				return false;
			}
			if(!this.checkLength255(nome, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_NOME)) {
				return false;
			}
			
			if(descrizione!=null && !"".equals(descrizione) &&
				!this.checkLength4000(descrizione, GruppiCostanti.LABEL_PARAMETRO_GRUPPO_DESCRIZIONE)) {
				return false;
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
					HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<>();
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
				
				if(!gruppo.getNome().equals(nome) &&
					// e' stato modificato ilnome
					
					// e' stato implementato l'update
/**					java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>> whereIsInUso = new java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>>();
//					boolean gruppoInUso = this.confCore.isGruppoInUso(gruppo.getNome(),whereIsInUso);
//					if (gruppoInUso) {
//						String msg = "";
//						msg += org.openspcoop2.core.commons.DBOggettiInUsoUtils.toString(new org.openspcoop2.core.id.IDGruppo(gruppo.getNome()), whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE,
//								" non modificabile in '"+nome+"' perch&egrave; risulta utilizzato:");
//						msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
//						this.pd.setMessage(msg);
//						return false;
//					} 
//					*/
					(this.gruppiCore.existsGruppo(nome))
					){
					this.pd.setMessage("Un gruppo con nome '" + nome + "' risulta gi&agrave; stato registrato");
					return false;
					
				}
				
			}
				
			return true;

		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
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
			String[] labels = {
					GruppiCostanti.LABEL_GRUPPI
			};
			
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<Gruppo> it = lista.iterator();
				while (it.hasNext()) {
					List<DataElement> e = creaEntryGruppoCustom(it);
					
					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
			
			// preparo bottoni
			if(lista!=null && !lista.isEmpty() &&
				this.core.isShowPulsantiImportExport()) {

				ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
				if(exporterUtils.existsAtLeastOneExportMode(org.openspcoop2.protocol.sdk.constants.ArchiveType.GRUPPO, this.request, this.session)){

					List<AreaBottoni> bottoni = new ArrayList<>();

					AreaBottoni ab = new AreaBottoni();
					List<DataElement> otherbott = new ArrayList<>();
					DataElement de = new DataElement();
					de.setValue(GruppiCostanti.LABEL_GRUPPI_ESPORTA_SELEZIONATI);
					de.setOnClick(GruppiCostanti.LABEL_GRUPPI_ESPORTA_SELEZIONATI_ONCLICK);
					de.setDisabilitaAjaxStatus();
					otherbott.add(de);
					ab.setBottoni(otherbott);
					bottoni.add(ab);

					this.pd.setAreaBottoni(bottoni);

				}

			}
			
		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
	}
	
	public List<DataElement> creaEntryGruppo(Iterator<Gruppo> it) {
		Gruppo gruppo = it.next();

		List<DataElement> e = new ArrayList<>();

		DataElement de = new DataElement();
		Parameter pId = new Parameter(GruppiCostanti.PARAMETRO_GRUPPO_ID, gruppo.getId()+"");
		de.setUrl(GruppiCostanti.SERVLET_NAME_GRUPPI_CHANGE , pId);
		de.setToolTip(gruppo.getDescrizione());
		de.setValue(gruppo.getNome());
		de.setIdToRemove(gruppo.getNome());
		de.setToolTip(gruppo.getDescrizione());
		e.add(de);

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
			default:
				de.setValue("?");
				break;
			}
		}
		e.add(de);

		this.addInUsoButtonVisualizzazioneClassica(e, gruppo.getNome(), gruppo.getNome(), InUsoType.GRUPPO);
		return e;
	}
	
	private List<DataElement> creaEntryGruppoCustom(Iterator<Gruppo> it) {
		Gruppo gruppo = it.next();

		List<DataElement> e = new ArrayList<>();
		
		// Titolo (nome)
		DataElement de = new DataElement();
		Parameter pId = new Parameter(GruppiCostanti.PARAMETRO_GRUPPO_ID, gruppo.getId()+"");
		de.setUrl(GruppiCostanti.SERVLET_NAME_GRUPPI_CHANGE , pId);
		de.setValue(gruppo.getNome());
		de.setIdToRemove(gruppo.getNome());
		de.setToolTip(gruppo.getDescrizione());
		de.setType(DataElementType.TITLE);
		e.add(de);
		
		
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
			default:
				de.setValue("?");
				break;
			}
		}
		
		de.setType(DataElementType.SUBTITLE);
		e.add(de);

		this.addInUsoButton(e, gruppo.getNome(), gruppo.getNome(), InUsoType.GRUPPO);
		
		// Proprieta Button
		/**if(this.existsProprietaOggetto(gruppo.getProprietaOggetto(), gruppo.getDescrizione())) {
		 * ** la lista non riporta le proprietà. Ma esistono e poi sarà la servlet a gestirlo
		 */
		this.addProprietaOggettoButton(e, gruppo.getNome(), gruppo.getNome(), InUsoType.GRUPPO);
		
		return e;
	}
}

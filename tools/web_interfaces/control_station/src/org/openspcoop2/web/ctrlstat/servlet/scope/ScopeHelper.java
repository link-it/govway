/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.web.ctrlstat.servlet.scope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
//import org.openspcoop2.core.registry.constants.ScopeTipologia;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ScopeHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ScopeHelper extends ConsoleHelper{
	
	private boolean mostraFiltroScopeTipologia = false;

	public ScopeHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public ScopeHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	public Vector<DataElement> addScopeToDati(TipoOperazione tipoOP, Long scopeId, String nome, String descrizione, String tipologia,
			String nomeEsterno, String contesto, Vector<DataElement> dati) {
		
		DataElement de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_SCOPE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		if(scopeId!=null){
			de = new DataElement();
			de.setLabel(ScopeCostanti.PARAMETRO_SCOPE_ID);
			de.setValue(scopeId.longValue()+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(ScopeCostanti.PARAMETRO_SCOPE_ID);
			de.setSize( getSize());
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME);
		de.setValue(nome);
		//if(TipoOperazione.ADD.equals(tipoOP)){
		de.setType(DataElementType.TEXT_EDIT);
		//}
		//else{
		//	de.setType(DataElementType.TEXT);
		//}
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_NOME);
		de.setSize( getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_DESCRIZIONE);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_TIPOLOGIA);
		de.setType(DataElementType.SELECT);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_TIPOLOGIA);
		
		de.setValue(tipologia);
		if(this.mostraFiltroScopeTipologia)
			de.setType(DataElementType.TEXT_EDIT);
		else 
			de.setType(DataElementType.HIDDEN);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME_ESTERNO);
		de.setValue(nomeEsterno);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_NOME_ESTERNO);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_CONTESTO);
		de.setType(DataElementType.SELECT);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_CONTESTO);
		de.setLabels(ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL);
		de.setValues(ScopeCostanti.SCOPE_CONTESTO_UTILIZZO);
		de.setSelected(contesto);
		dati.addElement(de);
	
		return dati;
	}
	
	
	// Controlla i dati del registro
	public boolean scopeCheckData(TipoOperazione tipoOp, Scope scope) throws Exception {

		try{

			String nome = this.getParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME);
			String descrizione = this.getParameter(ScopeCostanti.PARAMETRO_SCOPE_DESCRIZIONE);
			String nomeEsterno = this.getParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME_ESTERNO);
			//String descrizione = this.scopeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_SCOPE_DESCRIZIONE);
			//String tipologia = this.scopeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_SCOPE_TIPOLOGIA);
			//String contesto = this.scopeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_SCOPE_CONTESTO);
			
			// Campi obbligatori
			if (nome.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME;
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nel campo '"+ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME+"'");
				return false;
			}
			if(this.checkNCName(nome, ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME)==false){
				return false;
			}
			if(this.checkLength255(nome, ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME)==false) {
				return false;
			}
			
			if(descrizione!=null && !"".equals(descrizione)) {
				if(this.checkLength255(descrizione, ScopeCostanti.LABEL_PARAMETRO_SCOPE_DESCRIZIONE)==false) {
					return false;
				}
			}
			if(nomeEsterno!=null && !"".equals(nomeEsterno)) {
				if(this.checkLength255(nomeEsterno, ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME_ESTERNO)==false) {
					return false;
				}
			}

			// Se tipoOp = add, controllo che il registro non sia gia' stato
			// registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				
				if(this.scopeCore.existsScope(nome)){
					this.pd.setMessage("Un scope con nome '" + nome + "' risulta gi&agrave; stato registrato");
					return false;
				}
				
			}
			else{
				
				if(scope.getNome().equals(nome)==false){
					// e' stato modificato ilnome
					
					// e' stato implementato l'update
//					java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>> whereIsInUso = new java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>>();
//					boolean scopeInUso = this.confCore.isScopeInUso(scope.getNome(),whereIsInUso);
//					if (scopeInUso) {
//						String msg = "";
//						msg += org.openspcoop2.core.commons.DBOggettiInUsoUtils.toString(new org.openspcoop2.core.id.IDScope(scope.getNome()), whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE,
//								" non modificabile in '"+nome+"' perch&egrave; risulta utilizzato:");
//						msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
//						this.pd.setMessage(msg);
//						return false;
//					} 
//					
					if(this.scopeCore.existsScope(nome)){
						this.pd.setMessage("Un scope con nome '" + nome + "' risulta gi&agrave; stato registrato");
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
	
	
	// Prepara la lista di scope
	public void prepareScopeList(ISearch ricerca, List<Scope> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ScopeCostanti.OBJECT_NAME_SCOPE);

			int idLista = Liste.SCOPE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			if(this.mostraFiltroScopeTipologia) {
				String filterScopeTipologia = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SCOPE_TIPOLOGIA);
				this.addFilterScopeTipologia(filterScopeTipologia, false);
			}
			
			String filterScopeContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SCOPE_CONTESTO);
			this.addFilterScopeContesto(filterScopeContesto, false);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(ScopeCostanti.LABEL_SCOPE, ScopeCostanti.SERVLET_NAME_SCOPE_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(ScopeCostanti.LABEL_SCOPE, ScopeCostanti.SERVLET_NAME_SCOPE_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ScopeCostanti.LABEL_SCOPE, search);
			}

			// setto le label delle colonne
			List<String> listLabels= new ArrayList<String>();
			listLabels.add(ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME);
			if(this.mostraFiltroScopeTipologia){
				listLabels.add(ScopeCostanti.LABEL_PARAMETRO_SCOPE_TIPOLOGIA);
			}
			listLabels.add(ScopeCostanti.LABEL_PARAMETRO_SCOPE_CONTESTO);
			
			String[] labels = listLabels.toArray(new String[listLabels.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Scope> it = lista.iterator();
				while (it.hasNext()) {
					Scope scope = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					Parameter pId = new Parameter(ScopeCostanti.PARAMETRO_SCOPE_ID, scope.getId()+"");
					de.setUrl(
							ScopeCostanti.SERVLET_NAME_SCOPE_CHANGE , pId);
					de.setToolTip(scope.getDescrizione());
					de.setValue(scope.getNome());
					de.setIdToRemove(scope.getNome());
					de.setToolTip(scope.getDescrizione());
					e.addElement(de);
					
					if(this.mostraFiltroScopeTipologia){
						de = new DataElement();
						de.setValue(scope.getTipologia());
						e.addElement(de);
					}
					
					de = new DataElement();
					if(ScopeContesto.PORTA_APPLICATIVA.getValue().equals(scope.getContestoUtilizzo().getValue())){
						de.setValue(ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE);
					}
					else if(ScopeContesto.PORTA_DELEGATA.getValue().equals(scope.getContestoUtilizzo().getValue())){
						de.setValue(ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE);
					}
					else{
						de.setValue(ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_QUALSIASI);
					}
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
}

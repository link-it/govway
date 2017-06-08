/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.ruoli;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * RuoliHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RuoliHelper extends ConsoleHelper{

	public RuoliHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}

	public Vector<DataElement> addRuoloToDati(TipoOperazione tipoOP, Long ruoloId, String nome, String descrizione, String tipologia,
			String contesto, Vector<DataElement> dati) {
		
		DataElement de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_RUOLO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		if(ruoloId!=null){
			de = new DataElement();
			de.setLabel(RuoliCostanti.PARAMETRO_RUOLO_ID);
			de.setValue(ruoloId.longValue()+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(RuoliCostanti.PARAMETRO_RUOLO_ID);
			de.setSize( getSize());
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME);
		de.setValue(nome);
		//if(TipoOperazione.ADD.equals(tipoOP)){
		de.setType(DataElementType.TEXT_EDIT);
		//}
		//else{
		//	de.setType(DataElementType.TEXT);
		//}
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_NOME);
		de.setSize( getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_TIPOLOGIA);
		de.setType(DataElementType.SELECT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_TIPOLOGIA);
		de.setLabels(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL);
		de.setValues(RuoliCostanti.RUOLI_TIPOLOGIA);
		de.setSelected(tipologia);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_CONTESTO);
		de.setType(DataElementType.SELECT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_CONTESTO);
		de.setLabels(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL);
		de.setValues(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO);
		de.setSelected(contesto);
		dati.addElement(de);
	
		return dati;
	}
	
	
	// Controlla i dati del registro
	public boolean ruoloCheckData(TipoOperazione tipoOp, Ruolo ruolo) throws Exception {

		try{

			String nome = this.request.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME);
			//String descrizione = this.request.getParameter(ConfigurazioneCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
			//String tipologia = this.request.getParameter(ConfigurazioneCostanti.PARAMETRO_RUOLO_TIPOLOGIA);
			//String contesto = this.request.getParameter(ConfigurazioneCostanti.PARAMETRO_RUOLO_CONTESTO);
			
			// Campi obbligatori
			if (nome.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME;
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nel campo '"+RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME+"'");
				return false;
			}
			if(this.checkNCName(nome, RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME)==false){
				return false;
			}

			// Se tipoOp = add, controllo che il registro non sia gia' stato
			// registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				
				if(this.ruoliCore.existsRuolo(nome)){
					this.pd.setMessage("Un ruolo con nome '" + nome + "' risulta gi&agrave; stato registrato");
					return false;
				}
				
			}
			else{
				
				if(ruolo.getNome().equals(nome)==false){
					// e' stato modificato ilnome
					
					// e' stato implementato l'update
//					java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>> whereIsInUso = new java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>>();
//					boolean ruoloInUso = this.confCore.isRuoloInUso(ruolo.getNome(),whereIsInUso);
//					if (ruoloInUso) {
//						String msg = "";
//						msg += org.openspcoop2.core.commons.DBOggettiInUsoUtils.toString(new org.openspcoop2.core.id.IDRuolo(ruolo.getNome()), whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE,
//								" non modificabile in '"+nome+"' perch&egrave; risulta utilizzato:");
//						msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
//						this.pd.setMessage(msg);
//						return false;
//					} 
//					
					if(this.ruoliCore.existsRuolo(nome)){
						this.pd.setMessage("Un ruolo con nome '" + nome + "' risulta gi&agrave; stato registrato");
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
	
	
	// Prepara la lista di ruoli
	public void prepareRuoliList(ISearch ricerca, List<Ruolo> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, RuoliCostanti.OBJECT_NAME_RUOLI);

			int idLista = Liste.RUOLI;
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
						new Parameter(RuoliCostanti.LABEL_RUOLI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(RuoliCostanti.LABEL_RUOLI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,RuoliCostanti.SERVLET_NAME_RUOLI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Ruoli contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {
					RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME,
					RuoliCostanti.LABEL_PARAMETRO_RUOLO_TIPOLOGIA,
					RuoliCostanti.LABEL_PARAMETRO_RUOLO_CONTESTO,
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Ruolo> it = lista.iterator();
				while (it.hasNext()) {
					Ruolo ruolo = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					Parameter pId = new Parameter(RuoliCostanti.PARAMETRO_RUOLO_ID, ruolo.getId()+"");
					de.setUrl(
							RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE , pId);
					de.setToolTip(ruolo.getDescrizione());
					de.setValue(ruolo.getNome());
					de.setIdToRemove(ruolo.getNome());
					de.setToolTip(ruolo.getDescrizione());
					e.addElement(de);

					de = new DataElement();
					if(RuoloTipologia.INTERNO.getValue().equals(ruolo.getTipologia().getValue())){
						de.setValue(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_INTERNO);
					}
					else if(RuoloTipologia.ESTERNO.getValue().equals(ruolo.getTipologia().getValue())){
						de.setValue(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_ESTERNO);
					}
					else{
						de.setValue(ruolo.getTipologia().getValue());
					}
					
					e.addElement(de);
					
					de = new DataElement();
					if(RuoloContesto.PORTA_APPLICATIVA.getValue().equals(ruolo.getContestoUtilizzo().getValue())){
						de.setValue(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE);
					}
					else if(RuoloContesto.PORTA_DELEGATA.getValue().equals(ruolo.getContestoUtilizzo().getValue())){
						de.setValue(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE);
					}
					else{
						de.setValue(ruolo.getContestoUtilizzo().getValue());
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

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.web.lib.audit.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.lib.audit.costanti.Costanti;
import org.openspcoop2.web.lib.audit.dao.Filtro;
import org.openspcoop2.web.lib.audit.log.Binary;
import org.openspcoop2.web.lib.audit.log.Operation;
import org.openspcoop2.web.lib.audit.log.constants.Stato;
import org.openspcoop2.web.lib.audit.log.constants.Tipologia;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralLink;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Utility per la gestione grafica delle servlet
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class AuditHelper {
	HttpServletRequest request;
	PageData pd;
	HttpSession session;

	// Costruttore per le list
	public AuditHelper(HttpServletRequest request, PageData pd, HttpSession session) {
		this.request = request;
		this.pd = pd;
		this.session = session;
	}

	private static boolean useXml = false; // deprecato
	public static boolean isUseXml() {
		return useXml;
	}
	public static void setUseXml(boolean useXml) {
		AuditHelper.useXml = useXml;
	}

	public List<DataElement> addAuditingToDati(String statoaudit,
			String stato, String dump, String formatodump, String log4j,
			int numFiltri) throws Exception {
		try {
			List<DataElement> dati = new ArrayList<>();

			String[] tipi = {  AuditCostanti.DEFAULT_VALUE_ABILITATO, AuditCostanti.DEFAULT_VALUE_DISABILITATO };
			String[] tipiLabels = { AuditCostanti.DEFAULT_VALUE_ABILITATO, AuditCostanti.DEFAULT_VALUE_DISABILITATO };

			DataElement de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO_AUDIT);
			de.setType(DataElementType.SELECT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_STATO_AUDIT);
			de.setValues(tipi);
			de.setLabels(tipiLabels);
			if (statoaudit != null)
				de.setSelected(statoaudit);
			//			de.setOnChange("CambiaStatoAudit()");
			de.setPostBack(true);
			dati.add(de);

			if (statoaudit != null && statoaudit.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				de = new DataElement();
				de.setLabel(AuditCostanti.LABEL_AUDIT_COMPORTAMENTO_DI_DEFAULT);
				de.setType(DataElementType.TITLE);
				dati.add(de);
			}

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO_2);
			if (statoaudit != null && statoaudit.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				de.setType(DataElementType.SELECT);
				de.setValues(tipi);
				de.setLabels(tipiLabels);
				if (stato != null)
					de.setSelected(stato);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(stato);
			}
			de.setName(AuditCostanti.PARAMETRO_AUDIT_STATO);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_DUMP);
			if (statoaudit != null && statoaudit.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				de.setType(DataElementType.SELECT);
				de.setValues(tipi);
				de.setLabels(tipiLabels);
				if (dump != null)
					de.setSelected(dump);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(dump);
			}
			de.setName(AuditCostanti.PARAMETRO_AUDIT_DUMP);
			dati.add(de);

			String[] tipiFormato =
				{ Costanti.DUMP_JSON_FORMAT };
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_FORMATO_DUMP);
			if(!useXml) {
				formatodump = Costanti.DUMP_JSON_FORMAT;
			}
			if (useXml && statoaudit != null && statoaudit.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				de.setType(DataElementType.SELECT);
				de.setValues(tipiFormato);
				if (formatodump != null)
					de.setSelected(formatodump);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(formatodump);
			}
			de.setName(AuditCostanti.PARAMETRO_AUDIT_FORMATO_DUMP);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_LOG4J);
			if (statoaudit != null && statoaudit.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				de.setType(DataElementType.SELECT);
				de.setValues(tipi);
				de.setLabels(tipiLabels);
				if (log4j != null)
					de.setSelected(log4j);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(log4j);
			}
			de.setName(AuditCostanti.PARAMETRO_AUDIT_LOG4J);
			dati.add(de);

			if (statoaudit != null && statoaudit.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				de = new DataElement();
				de.setLabel(AuditCostanti.LABEL_AUDIT_FILTRI);
				de.setType(DataElementType.TITLE);
				dati.add(de);

				de = new DataElement();
				de.setLabel(AuditCostanti.LABEL_AUDIT_VISUALIZZA);
				de.setType(DataElementType.LINK);
				de.setUrl(AuditCostanti.SERVLET_NAME_AUDIT_FILTRI_LIST);
				ServletUtils.setDataElementVisualizzaLabel(de,  (long)numFiltri);
				dati.add(de);
			}

			return dati;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public String auditCheckData(String statoaudit, String stato, String dump, String formatodump, String log4j)
			throws Exception {
		try {

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!statoaudit.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) && !statoaudit.equals(AuditCostanti.DEFAULT_VALUE_DISABILITATO)) {
				String msg = "Stato dev'essere abilitato o disabilitato";
				return msg;
			}
			if (!stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) && !stato.equals(AuditCostanti.DEFAULT_VALUE_DISABILITATO)) {
				String msg = "Audit dev'essere abilitato o disabilitato";
				return msg;
			}
			if (!dump.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) && !dump.equals(AuditCostanti.DEFAULT_VALUE_DISABILITATO)) {
				String msg = "Dump dev'essere abilitato o disabilitato";
				return msg;
			}
			if (!formatodump.equals(Costanti.DUMP_JSON_FORMAT)) {
				String msg = "Formato dump dev'essere "+Costanti.DUMP_JSON_FORMAT;
				return msg;
			}
			if (!log4j.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) && !log4j.equals(AuditCostanti.DEFAULT_VALUE_DISABILITATO)) {
				String msg = "Log4j Auditing dev'essere abilitato o disabilitato";
				return msg;
			}

			return "";
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	// Prepara la lista di filtri
	public void prepareFiltriList(ISearch ricerca, List<Filtro> lista,
			int idLista) throws Exception {
		try {

			ServletUtils.addListElementIntoSession(this.request, this.session, AuditCostanti.OBJECT_NAME_CONFIGURAZIONE_AUDITING_FILTRI);

			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();

			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, 
					AuditCostanti.SERVLET_NAME_AUDIT));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_FILTRI, 
						null));
			}else{
				lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_FILTRI, 
						AuditCostanti.SERVLET_NAME_AUDIT_FILTRI_LIST));
				lstParam.add(new Parameter(org.openspcoop2.web.lib.mvc.Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}
			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AuditCostanti.LABEL_AUDIT_FILTRI, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					AuditCostanti.LABEL_AUDIT_DESCRIZIONE,
					AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO,
					AuditCostanti.LABEL_PARAMETRO_AUDIT_DUMP
			};
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<Filtro> it = lista.iterator();
				while (it.hasNext()) {
					Filtro f = it.next();

					List<DataElement> e = new ArrayList<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(
							AuditCostanti.SERVLET_NAME_AUDIT_FILTRI_CHANGE,
							new Parameter(AuditCostanti.PARAMETRO_AUDIT_ID, f.getId() + "")
							);
					de.setValue(f.toString());
					de.setIdToRemove(""+f.getId());
					e.add(de);

					de = new DataElement();
					de.setValue(f.isAuditEnabled() ? AuditCostanti.DEFAULT_VALUE_ABILITATO : AuditCostanti.DEFAULT_VALUE_DISABILITATO);
					e.add(de);

					de = new DataElement();
					de.setValue(f.isDumpEnabled() ? AuditCostanti.DEFAULT_VALUE_ABILITATO : AuditCostanti.DEFAULT_VALUE_DISABILITATO);
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<DataElement> addFiltroToDati(Tipologia tipoOp, String utente,
			String tipooperazione, String[] tipiOgg, String tipooggetto,
			String statooperazione, String stato,
			String tipofiltro, String dump, String statoazione,
			String dumpazione, String id) throws Exception {
		try {
			List<DataElement> dati = new ArrayList<>();

			String[] tipi = { AuditCostanti.DEFAULT_VALUE_ABILITATO, AuditCostanti.DEFAULT_VALUE_DISABILITATO };
			String[] tipiLabels = { AuditCostanti.DEFAULT_VALUE_ABILITATO, AuditCostanti.DEFAULT_VALUE_DISABILITATO };

			DataElement de = null;


			if (tipoOp.equals(Tipologia.CHANGE)) {
				de = new DataElement();
				de.setValue(id);
				de.setType(DataElementType.HIDDEN);
				de.setName(AuditCostanti.PARAMETRO_AUDIT_ID);
				dati.add(de);
			}

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT_FILTRO_GENERICO);
			de.setType(DataElementType.TITLE);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_UTENTE);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(utente);
			dati.add(de);

			String[] tipiOp =
				{ "-", Tipologia.ADD.toString(),
					Tipologia.CHANGE.toString(),
					Tipologia.DEL.toString() };
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			de.setType(DataElementType.SELECT);
			de.setValues(tipiOp);
			if (tipooperazione != null)
				de.setSelected(tipooperazione);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_TIPO_OGGETTO);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			de.setType(DataElementType.SELECT);
			de.setValues(tipiOgg);
			if (tipooggetto != null)
				de.setSelected(tipooggetto);
			dati.add(de);

			String[] tipiStatoOp =
				{ "-", Stato.REQUESTING.toString(),
					Stato.ERROR.toString(),
					Stato.COMPLETED.toString() };
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO_OPERAZIONE);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			de.setType(DataElementType.SELECT);
			de.setValues(tipiStatoOp);
			if (statooperazione != null)
				de.setSelected(statooperazione);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT_FILTRO_CONTENUTO);
			de.setType(DataElementType.TITLE);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO);
			de.setType(DataElementType.SELECT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_STATO);
			de.setValues(tipi);
			de.setLabels(tipiLabels);
			if (stato != null)
				de.setSelected(stato);
			//			de.setOnChange("CambiaStatoFiltro('" + tipoOp + "')");
			de.setPostBack(true);
			dati.add(de);

			String[] tipiFiltro =
				{	AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_NORMALE,
					AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TIPO_FILTRO_ESPRESSIONE_REGOLARE
				};
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_TIPO);

			de.setName(AuditCostanti.PARAMETRO_AUDIT_TIPO_FILTRO);
			if (stato != null && stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				de.setType(DataElementType.SELECT);
				de.setValues(tipiFiltro);
				if (tipofiltro != null)
					de.setSelected(tipofiltro);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tipofiltro);
			}
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_DUMP);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_DUMP);
			if (stato != null && stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO)) {
				de.setType(DataElementType.TEXT_EDIT);
				de.setValue(dump);
				de.setRequired(true);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(dump);
			}
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT_AZIONE);
			de.setType(DataElementType.TITLE);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO);
			de.setType(DataElementType.SELECT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_STATO_AZIONE);
			de.setValues(tipi);
			de.setLabels(tipiLabels);
			if (statoazione != null)
				de.setSelected(statoazione);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_DUMP);
			de.setType(DataElementType.SELECT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_DUMP_AZIONE);
			de.setValues(tipi);
			de.setLabels(tipiLabels);
			if (dumpazione != null)
				de.setSelected(dumpazione);
			dati.add(de);

			return dati;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public String filtriCheckData(String utente, String tipooperazione,
			String tipooggetto, String statooperazione,
			String stato, String tipofiltro,
			String dump, String statoazione,
			String dumpazione, String[] tipiOgg)
			throws Exception {
		try {

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!tipooperazione.equals("-") &&
					!tipooperazione.equals(Tipologia.ADD.toString()) &&
					!tipooperazione.equals(Tipologia.CHANGE.toString()) &&
					!tipooperazione.equals(Tipologia.DEL.toString())) {
				String msg = "Tipo operazione dev'essere - o "+
						Tipologia.ADD.toString()+
						" o "+Tipologia.CHANGE.toString()+
						" o "+Tipologia.DEL.toString();
				return msg;
			}
			if (!statooperazione.equals("-") &&
					!statooperazione.equals(Stato.REQUESTING.toString()) &&
					!statooperazione.equals(Stato.ERROR.toString()) &&
					!statooperazione.equals(Stato.COMPLETED.toString())) {
				String msg = "Stato operazione dev'essere - o "+
						Stato.REQUESTING.toString()+
						" o "+Stato.ERROR.toString()+
						" o "+Stato.COMPLETED.toString();
				return msg;
			}
			if (!stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) && !stato.equals(AuditCostanti.DEFAULT_VALUE_DISABILITATO)) {
				String msg = "Stato dev'essere abilitato o disabilitato";
				return msg;
			}
			if (stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) &&
					!tipofiltro.equals("normale") && !tipofiltro.equals("espressioneRegolare")) {
				String msg = "Tipo dev'essere normale o espressioneRegolare";
				return msg;
			}
			if (!statoazione.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) && !statoazione.equals(AuditCostanti.DEFAULT_VALUE_DISABILITATO)) {
				String msg = "Stato dev'essere abilitato o disabilitato";
				return msg;
			}
			if (!dumpazione.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) && !dumpazione.equals(AuditCostanti.DEFAULT_VALUE_DISABILITATO)) {
				String msg = "Dump dev'essere abilitato o disabilitato";
				return msg;
			}

			// Controllo che il tipo oggetto appartenga alla lista di
			// tipi oggetto disponibili
			String allOgg = "";
			boolean trovatoOgg = false;
			for (int i = 0; i < tipiOgg.length; i++) {
				String tmpOgg = tipiOgg[i];
				if (tmpOgg.equals(tipooggetto)) {
					trovatoOgg = true;
				}
				if (allOgg.equals(""))
					allOgg = tmpOgg;
				else
					allOgg = allOgg+", "+tmpOgg;
			}
			if (!trovatoOgg) {
				String msg = "Il tipo oggetto dev'essere scelto tra: "+allOgg;
				return msg;
			}

			// Se lo stato è abilitato, dump dev'essere specificato
			if (stato.equals(AuditCostanti.DEFAULT_VALUE_ABILITATO) && dump.equals("")) {
				String msg = "Se Stato è abilitato, Dump dev'essere valorizzato";
				return msg;
			}

			// Errore se non è stato impostato nessuno tra l'username,
			// il tipo di operazione, il tipo di oggetto, lo stato operazione
			// e il filtro per contenuto abilitato
			if (utente.equals("") && tipooperazione.equals("-") &&
					tipooggetto.equals("-") && statooperazione.equals("-") &&
					!AuditCostanti.DEFAULT_VALUE_ABILITATO.equals(stato)) {
				String msg = "Specificare almeno un criterio di filtro generico o un filtro per contenuto";
				return msg;
			}

			return "";
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<DataElement> addAuditReportToDati(String datainizio,
			String datafine, String tipooperazione, String[] tipiOgg,
			String tipooggetto, String id, String oldid,
			String utente, String statooperazione, String contoggetto)
					throws Exception {
		try {
			List<DataElement> dati = new ArrayList<DataElement>();

			DataElement de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT_CRITERI_RICERCA);
			de.setType(DataElementType.TITLE);
			dati.add(de);

			int valueSize = 40;

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_DATA_INIZIO_LABEL);
			de.setNote(AuditCostanti.LABEL_PARAMETRO_AUDIT_DATA_INIZIO_NOTE);
			de.setValue(datainizio != null ? datainizio : "");
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO);
			de.setSize(valueSize);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_DATA_FINE_LABEL);
			de.setNote(AuditCostanti.LABEL_PARAMETRO_AUDIT_DATA_FINE_NOTE);
			de.setValue(datafine != null ? datafine : "");
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE);
			de.setSize(valueSize);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_UTENTE);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(utente);
			de.setSize(valueSize);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT_OPERAZIONE);
			de.setType(DataElementType.TITLE);
			dati.add(de);

			String[] tipiOp =
				{ "-", Tipologia.ADD.toString(),
					Tipologia.CHANGE.toString(),
					Tipologia.DEL.toString(),
					Tipologia.LOGIN.toString(),
					Tipologia.LOGOUT.toString() };
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_TIPO);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			de.setType(DataElementType.SELECT);
			de.setValues(tipiOp);
			if (tipooperazione != null)
				de.setSelected(tipooperazione);
			de.setSize(valueSize);
			dati.add(de);

			String[] tipiStatoOp =
				{ "-", Stato.REQUESTING.toString(),
					Stato.ERROR.toString(),
					Stato.COMPLETED.toString() };
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE);
			de.setType(DataElementType.SELECT);
			de.setValues(tipiStatoOp);
			if (statooperazione != null)
				de.setSelected(statooperazione);
			de.setSize(valueSize);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_OGGETTO);
			de.setType(DataElementType.TITLE);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_TIPO);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO);
			de.setType(DataElementType.SELECT);
			de.setValues(tipiOgg);
			if (tipooggetto != null)
				de.setSelected(tipooggetto);
			de.setSize(valueSize);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_ID);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_ID);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(id);
			de.setSize(valueSize);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_OPERATION_OLD_ID);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_OLD_ID);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(oldid);
			de.setSize(valueSize);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_CONTENUTO_OGGETTO);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO);
			de.setType(DataElementType.TEXT_EDIT);
			de.setValue(contoggetto);
			de.setSize(valueSize);
			dati.add(de);

			return dati;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public String auditReportCheckData(String datainizio, String datafine,
			String tipooperazione, String tipooggetto,
			String id, String oldid,
			String statooperazione,
			String[] tipiOgg)
			throws Exception {
		try {

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!tipooperazione.equals("-") &&
					!tipooperazione.equals(Tipologia.ADD.toString()) &&
					!tipooperazione.equals(Tipologia.CHANGE.toString()) &&
					!tipooperazione.equals(Tipologia.DEL.toString()) &&
					!tipooperazione.equals(Tipologia.LOGIN.toString()) &&
					!tipooperazione.equals(Tipologia.LOGOUT.toString())) {
				String msg = "Tipo operazione dev'essere - o "+
						Tipologia.ADD.toString()+
						" o "+Tipologia.CHANGE.toString()+
						" o "+Tipologia.DEL.toString()+
						" o "+Tipologia.LOGIN.toString()+
						" o "+Tipologia.LOGOUT.toString();
				return msg;
			}
			if (!statooperazione.equals("-") &&
					!statooperazione.equals(Stato.REQUESTING.toString()) &&
					!statooperazione.equals(Stato.ERROR.toString()) &&
					!statooperazione.equals(Stato.COMPLETED.toString())) {
				String msg = "Stato operazione dev'essere - o "+
						Stato.REQUESTING.toString()+
						" o "+Stato.ERROR.toString()+
						" o "+Stato.COMPLETED.toString();
				return msg;
			}

			// Controllo che il tipo oggetto appartenga alla lista di
			// tipi oggetto disponibili
			String allOgg = "";
			boolean trovatoOgg = false;
			for (int i = 0; i < tipiOgg.length; i++) {
				String tmpOgg = tipiOgg[i];
				if (tmpOgg.equals(tipooggetto)) {
					trovatoOgg = true;
				}
				if (allOgg.equals(""))
					allOgg = tmpOgg;
				else
					allOgg = allOgg+", "+tmpOgg;
			}
			if (!trovatoOgg) {
				String msg = "Il tipo oggetto dev'essere scelto tra: "+allOgg;
				return msg;
			}

			if ( (datainizio == null || "".equals(datainizio)) 
					&&
					(id == null || "".equals(id)) 
					&&
					(oldid == null || "".equals(oldid)) ) {
				String msg = "Deve essere indicato almeno uno dei seguenti criteri di ricerca: Intervallo Iniziale, Identificativo, Id precedente alla modifica";
				return msg;
			}
			
			// Controlli sulle date
			if (datainizio != null && !"".equals(datainizio)) {
				boolean dataInOk = true;
				if (datainizio.length() != 10) {
					dataInOk = false;
				} else {
					String anno = datainizio.substring(0, 4);
					String mese = datainizio.substring(5, 7);
					String giorno = datainizio.substring(8, 10);
					if (!RegularExpressionEngine.isMatch(anno,"^[0-9]+$")) {
						dataInOk = false;
					} else if (!datainizio.substring(4, 5).equals("-")) {
						dataInOk = false;
					} else if (!RegularExpressionEngine.isMatch(mese,"^[0-9]+$")) {
						dataInOk = false;
					} else if (!datainizio.substring(7, 8).equals("-")) {
						dataInOk = false;
					} else if (!RegularExpressionEngine.isMatch(giorno,"^[0-9]+$")) {
						dataInOk = false;
					} else {
						int annoI = Integer.parseInt(anno);
						int meseI = Integer.parseInt(mese);
						int giornoI = Integer.parseInt(giorno);
						if ((meseI == 1) || (meseI == 3) || (meseI == 5) || (meseI == 7) || (meseI == 8) || (meseI == 10) || (meseI == 12)) {
							if ((giornoI == 0) || (giornoI > 31)) {
								dataInOk = false;
							}
						} else {
							if ((meseI == 4) || (meseI == 6) || (meseI == 9) || (meseI == 11)) {
								if ((giornoI == 0) || (giornoI > 30)) {
									dataInOk = false;
								}
							} else {
								if (meseI == 2) {
									int bisestile = annoI / 4;
									if (annoI != bisestile * 4) {
										if ((giornoI == 0) || (giornoI > 28)) {
											dataInOk = false;
										}
									} else if ((giornoI == 0) || (giornoI > 29)) {
										dataInOk = false;
									}
								} else {
									dataInOk = false;
								}
							}
						}
					}
				}
				if (!dataInOk) {
					String msg = "Inizio intervallo espresso in una forma sbagliata. Esprimerlo come yyyy-MM-dd";
					return msg;
				}
			}

			if (datafine != null && !"".equals(datafine)) {
				boolean dataFineOk = true;
				if (datafine.length() != 10) {
					dataFineOk = false;
				} else {
					String anno = datafine.substring(0, 4);
					String mese = datafine.substring(5, 7);
					String giorno = datafine.substring(8, 10);
					if (!RegularExpressionEngine.isMatch(anno,"^[0-9]+$")) {
						dataFineOk = false;
					} else if (!datafine.substring(4, 5).equals("-")) {
						dataFineOk = false;
					} else if (!RegularExpressionEngine.isMatch(mese,"^[0-9]+$")) {
						dataFineOk = false;
					} else if (!datafine.substring(7, 8).equals("-")) {
						dataFineOk = false;
					} else if (!RegularExpressionEngine.isMatch(giorno,"^[0-9]+$")) {
						dataFineOk = false;
					} else {
						int annoI = Integer.parseInt(anno);
						int meseI = Integer.parseInt(mese);
						int giornoI = Integer.parseInt(giorno);
						if ((meseI == 1) || (meseI == 3) || (meseI == 5) || (meseI == 7) || (meseI == 8) || (meseI == 10) || (meseI == 12)) {
							if ((giornoI == 0) || (giornoI > 31)) {
								dataFineOk = false;
							}
						} else {
							if ((meseI == 4) || (meseI == 6) || (meseI == 9) || (meseI == 11)) {
								if ((giornoI == 0) || (giornoI > 30)) {
									dataFineOk = false;
								}
							} else {
								if (meseI == 2) {
									int bisestile = annoI / 4;
									if (annoI != bisestile * 4) {
										if ((giornoI == 0) || (giornoI > 28)) {
											dataFineOk = false;
										}
									} else if ((giornoI == 0) || (giornoI > 29)) {
										dataFineOk = false;
									}
								} else {
									dataFineOk = false;
								}
							}
						}
					}
				}
				if (!dataFineOk) {
					String msg = "Fine intervallo espresso in una forma sbagliata. Esprimerlo come yyyy-MM-dd";
					return msg;
				}
			}

			return "";
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	// Prepara la lista di operazioni
	public void prepareAuditReportList(ISearch ricerca, List<Operation> lista,
			int idLista,
			String datainizio, String datafine,
			String tipooperazione, String tipooggetto,
			String id, String oldid,
			String utente, String statooperazione,
			String contoggetto) throws Exception {
		try {

			ServletUtils.addListElementIntoSession(this.request, this.session, AuditCostanti.OBJECT_NAME_AUDITING);

			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			//String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			Parameter pDataInizio = new Parameter(AuditCostanti.PARAMETRO_AUDIT_DATA_INIZIO, datainizio);
			Parameter pDataFine = new Parameter(AuditCostanti.PARAMETRO_AUDIT_DATA_FINE, datafine);
			Parameter pTipoOperazione = new Parameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OPERAZIONE, tipooperazione);
			Parameter pTipoOggetto = new Parameter(AuditCostanti.PARAMETRO_AUDIT_TIPO_OGGETTO, tipooggetto);
			Parameter pId = new Parameter(AuditCostanti.PARAMETRO_AUDIT_ID, id);
			Parameter pOldId = new Parameter(AuditCostanti.PARAMETRO_AUDIT_OLD_ID, oldid);
			Parameter pUtente = new Parameter(AuditCostanti.PARAMETRO_AUDIT_UTENTE, utente);
			Parameter pStatoOperazione = new Parameter(AuditCostanti.PARAMETRO_AUDIT_STATO_OPERAZIONE, statooperazione);
			Parameter pContoggetto = new Parameter(AuditCostanti.PARAMETRO_AUDIT_CONTENUTO_OGGETTO, contoggetto);
			Parameter pEditModeFin = ServletUtils.getParameterForEditModeFinished();

			String params = ServletUtils.getParametersAsString(false, pDataInizio, pDataFine, pTipoOperazione, pTipoOggetto, pId, pOldId, pUtente, pStatoOperazione, pContoggetto, pEditModeFin);

			// setto la barra del titolo
			List<GeneralLink> titlelist = new ArrayList<>();
			GeneralLink tl1 = new GeneralLink();
			tl1.setLabel(AuditCostanti.LABEL_AUDIT);
			tl1.setUrl(AuditCostanti.SERVLET_NAME_AUDITING);
			titlelist.add(tl1);
			GeneralLink tl2 = new GeneralLink();
			tl2.setLabel(AuditCostanti.LABEL_AUDIT_OPERAZIONI);
			titlelist.add(tl2);
			this.pd.setTitleList(titlelist);

			this.pd.setSearch("off");

			// setto le label delle colonne
			String[] labels = {
					AuditCostanti.LABEL_PARAMETRO_AUDIT_DATA_ESECUZIONE,
					AuditCostanti.LABEL_PARAMETRO_AUDIT_OPERAZIONE,
					AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO,
					AuditCostanti.LABEL_PARAMETRO_AUDIT_OGGETTO,
					AuditCostanti.LABEL_PARAMETRO_AUDIT_ID_UPPER_CASE,
					AuditCostanti.LABEL_PARAMETRO_AUDIT_UTENTE,
			};
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<Operation> it = lista.iterator();
				while (it.hasNext()) {
					Operation op = it.next();

					List<DataElement> e = new ArrayList<>();

					DataElement de = new DataElement();
					de.setUrl(
							AuditCostanti.SERVLET_NAME_AUDITING_DETTAGLIO,
							new Parameter(AuditCostanti.PARAMETRO_AUDIT_OPERATION_ID_OP, op.getId() + ""),
							pDataInizio, pDataFine, pTipoOperazione, pTipoOggetto, pId, pOldId, pUtente, pStatoOperazione, pContoggetto

							);
					de.setValue(DateUtils.getSimpleDateFormatMs().format(op.getTimeExecute())); //+ " ["+op.getId()+"]");
					de.setIdToRemove(""+op.getId());
					e.add(de);

					de = new DataElement();
					de.setValue(op.getTipologia().getValue());
					e.add(de);

					de = new DataElement();
					de.setValue(op.getStato().getValue());
					e.add(de);

					de = new DataElement();
					de.setValue(op.getTipoOggetto());
					e.add(de);

					de = new DataElement();
					if(op.getObjectId()!=null && op.getObjectId().length()>60){
						de.setValue(op.getObjectId().substring(0, 60)+" ...");
						de.setToolTip(op.getObjectId());
					}else{
						de.setValue(op.getObjectId());
					}
					e.add(de);

					de = new DataElement();
					de.setValue(op.getUtente());
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(false);

			this.request.setAttribute(org.openspcoop2.web.lib.mvc.Costanti.REQUEST_ATTIBUTE_PARAMS, params);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<DataElement> addAuditReportDettaglioToDati(Operation op, boolean showLinkDocumentiBinari,
			Parameter ... params) throws Exception {
		try {
			List<DataElement> dati = new ArrayList<>();
			Parameter pIdOp = new Parameter(AuditCostanti.PARAMETRO_AUDIT_OPERATION_ID_OP, op.getId() + "");
			Parameter pTypeOggetto = new Parameter(AuditCostanti.PARAMETRO_AUDIT_TYPE, AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_OGGETTO);
			Parameter pTypeErrore = new Parameter(AuditCostanti.PARAMETRO_AUDIT_TYPE, AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_ERROR);

			DataElement de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT_DETTAGLIO_OPERAZIONE);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_OPERATION_TIME_REQUEST);
			de.setValue(""+op.getTimeRequest());
			de.setType(DataElementType.TEXT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_OPERATION_TIME_REQUEST);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_OPERATION_TIME_EXECUTE);
			de.setValue(""+op.getTimeExecute());
			de.setType(DataElementType.TEXT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_OPERATION_TIME_EXECUTE);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_ID_OPERAZIONE);
			de.setValue(""+op.getId());
			de.setType(DataElementType.TEXT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_OPERATION_ID_OPERAZIONE);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_TIPO_OPERAZIONE);
			de.setValue(op.getTipologia().getValue());
			de.setType(DataElementType.TEXT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_OPERATION_TIPO_OPERAZIONE);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_TIPO_OGGETTO);
			de.setValue(op.getTipoOggetto());
			de.setType(DataElementType.TEXT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_OPERATION_TIPO_OGGETTO);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_ID);
			de.setValue(op.getObjectId());
			de.setType(DataElementType.TEXT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_OPERATION_OBJECT_ID);
			dati.add(de);

			if (op.getObjectOldId() != null) {
				de = new DataElement();
				de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_OPERATION_OLD_ID);
				de.setValue(op.getObjectOldId());
				de.setType(DataElementType.TEXT);
				de.setName(AuditCostanti.PARAMETRO_AUDIT_OPERATION_OBJECT_OLD_ID  );
				dati.add(de);
			}

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_UTENTE);
			de.setValue(op.getUtente());
			de.setType(DataElementType.TEXT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_UTENTE);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_PARAMETRO_AUDIT_STATO);
			de.setValue(op.getStato().getValue());
			de.setType(DataElementType.TEXT);
			de.setName(AuditCostanti.PARAMETRO_AUDIT_STATO);
			dati.add(de);

			int size2 = params != null ? params.length +2 : 2;
			Parameter [] paramsErr = new Parameter[size2];
			Parameter [] paramsOgg = new Parameter[size2];
			paramsErr[0] = pIdOp;
			paramsErr[1] = pTypeErrore;
			paramsOgg[0] = pIdOp;
			paramsOgg[1] = pTypeOggetto;
			

			if(params != null){
			for (int i=0; i < params.length ; i++ ){
				paramsErr[i+2] = params[i];
				paramsOgg[i+2] = params[i];
			}
			}
			
			if (Stato.ERROR.equals(op.getStato())) {
				de = new DataElement();
				de.setLabel(AuditCostanti.LABEL_AUDIT_MOTIVO_ERRORE);
				de.setUrl(
						AuditCostanti.SERVLET_NAME_AUDITING_DETTAGLIO_INFO, paramsErr);
				de.setValue(AuditCostanti.LABEL_AUDIT_MOTIVO_ERRORE);
				de.setType(DataElementType.LINK);
				dati.add(de);
			}

			de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT_DETTAGLIO_OGGETTO);
			if(op.getObjectDetails()!=null && !"".equals(op.getObjectDetails())){
				de.setUrl(
						AuditCostanti.SERVLET_NAME_AUDITING_DETTAGLIO_INFO, paramsOgg);
				de.setType(DataElementType.LINK);
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setValue(AuditCostanti.LABEL_AUDIT_DETTAGLIO_OGGETTO);
			dati.add(de);

			int size = params != null ? params.length +1 : 1;
			Parameter [] params2 = new Parameter[size];
			params2[0] = pIdOp;

			if(params != null){
			for (int i=0; i < params.length ; i++ ){
				params2[i+1] = params[i];
			}
			}
			
			if(showLinkDocumentiBinari) {
				de = new DataElement();
				de.setLabel(AuditCostanti.LABEL_AUDIT_DOCUMENTI_BINARI);
				de.setUrl(AuditCostanti.SERVLET_NAME_AUDITING_DETTAGLIO_DOCUMENTI_BINARI, params2);
				de.setValue(AuditCostanti.LABEL_AUDIT_DOCUMENTI_BINARI + " ("+op.sizeBinaryList()+")");
				de.setType(DataElementType.LINK);
				dati.add(de);
			}

			return dati;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<DataElement> addAuditReportInfoToDati(String type,
			String info) throws Exception {
		try {
			List<DataElement> dati = new ArrayList<>();

			DataElement de = new DataElement();
			de.setLabel(AuditCostanti.LABEL_AUDIT_DETTAGLIO_OGGETTO);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			de = new DataElement();
			if (type.equals(AuditCostanti.DEFAULT_VALUE_PARAMETRO_AUDIT_TYPE_ERROR))
				de.setLabel(AuditCostanti.LABEL_AUDIT_MOTIVO_ERRORE);
//			else
//				de.setLabel(AuditCostanti.LABEL_AUDIT_DETTAGLIO_OGGETTO);
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setValue(info);
			de.setRows(30);
			de.setCols(80);
			dati.add(de);

			return dati;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	// Prepara la lista di documenti binari
	public void prepareDocBinList(Operation singleOp, Parameter ... params)
			throws Exception {
		try {
			this.pd.setNumEntries(singleOp.sizeBinaryList());

			Parameter pIdOp = new Parameter(AuditCostanti.PARAMETRO_AUDIT_OPERATION_ID_OP, singleOp.getId() + "");
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();

			int size = params != null ? params.length +1 : 1;
			Parameter [] params2 = new Parameter[size];
			params2[0] = pIdOp;

			if(params != null){
			for (int i=0; i < params.length ; i++ ){
				params2[i+1] = params[i];
			}
			}

			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT, AuditCostanti.SERVLET_NAME_AUDITING));
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_OPERAZIONI, AuditCostanti.SERVLET_NAME_AUDITING, params));
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_DETTAGLIO +" di " + singleOp.getId(), 
					AuditCostanti.SERVLET_NAME_AUDITING_DETTAGLIO, params2)); 
			lstParam.add(new Parameter(AuditCostanti.LABEL_AUDIT_DOCUMENTI_BINARI, null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			this.pd.setSearch("off");

			// setto le label delle colonne
			String[] labels = { 
					AuditCostanti.LABEL_PARAMETRO_AUDIT_ID_UPPER_CASE,
					AuditCostanti.LABEL_PARAMETRO_AUDIT_CHECKSUM
			};
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			for (int i=0; i<singleOp.sizeBinaryList(); i++) {
				Binary bin = singleOp.getBinary(i);

				List<DataElement> e = new ArrayList<>();

				DataElement de = new DataElement();
				de.setValue(bin.getBinaryId());
				e.add(de);

				de = new DataElement();
				de.setValue(""+bin.getChecksum());
				e.add(de);

				dati.add(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(false);
			this.pd.setSelect(false);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
}

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
package org.openspcoop2.web.monitor.transazioni.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.web.monitor.core.bean.SelectItem;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;

/**
 * ColonnaExportManager
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ColonnaExportManager {

	private static ColonnaExportManager instance = null;
	
	private Map<String, ColonnaExport> mapColonne = null;
	private List<String> colonneNonSelezionabiliDallUtente = null;
	private List<String> ordineInserimentoColonne = null;
	private List<String> ordineInserimentoColonneCustomView = null;
	
	private static synchronized void init(){
		if(ColonnaExportManager.instance == null)
			ColonnaExportManager.instance = new ColonnaExportManager();
	}
	
	public static ColonnaExportManager getInstance(){
		if(ColonnaExportManager.instance == null)
			ColonnaExportManager.init();
		
		return ColonnaExportManager.instance;
	}
	
	public ColonnaExportManager(){
		this.mapColonne = new HashMap<String, ColonnaExport>();
		this.colonneNonSelezionabiliDallUtente = new ArrayList<String>();
		this.ordineInserimentoColonne = new ArrayList<String>();
		this.ordineInserimentoColonneCustomView = new ArrayList<String>();
		
		boolean SHOW_IN_CUSTOM_VIEW = true; // rappresenta le colonne visualizzate nello storico
		
		this._addColonna(CostantiExport.KEY_COL_ID_TRANSAZIONE, CostantiExport.LABEL_COL_ID_TRANSAZIONE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_PROTOCOLLO, CostantiExport.LABEL_COL_PROTOCOLLO, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ESITO, CostantiExport.LABEL_COL_ESITO, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ESITO_CONTESTO, CostantiExport.LABEL_COL_ESITO_CONTESTO, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TIPO_RICHIESTA, CostantiExport.LABEL_COL_TIPO_RICHIESTA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_CLUSTER_ID, CostantiExport.LABEL_COL_CLUSTER_ID, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_EVENTI_GESTIONE, CostantiExport.LABEL_COL_EVENTI_GESTIONE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_STATO, CostantiExport.LABEL_COL_STATO, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_PDD_RUOLO, CostantiExport.LABEL_COL_PDD_RUOLO, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_PDD_CODICE, CostantiExport.LABEL_COL_PDD_CODICE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_PDD_SOGGETTO, CostantiExport.LABEL_COL_PDD_SOGGETTO, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_ACCETTAZIONE_RICHIESTA, CostantiExport.LABEL_COL_DATA_ACCETTAZIONE_RICHIESTA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_INGRESSO_RICHIESTA, CostantiExport.LABEL_COL_DATA_INGRESSO_RICHIESTA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_INGRESSO_RICHIESTA_STREAM, CostantiExport.LABEL_COL_DATA_INGRESSO_RICHIESTA_STREAM, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_USCITA_RICHIESTA, CostantiExport.LABEL_COL_DATA_USCITA_RICHIESTA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_USCITA_RICHIESTA_STREAM, CostantiExport.LABEL_COL_DATA_USCITA_RICHIESTA_STREAM, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_ACCETTAZIONE_RISPOSTA, CostantiExport.LABEL_COL_DATA_ACCETTAZIONE_RISPOSTA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_INGRESSO_RISPOSTA, CostantiExport.LABEL_COL_DATA_INGRESSO_RISPOSTA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_INGRESSO_RISPOSTA_STREAM, CostantiExport.LABEL_COL_DATA_INGRESSO_RISPOSTA_STREAM, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_USCITA_RISPOSTA, CostantiExport.LABEL_COL_DATA_USCITA_RISPOSTA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_USCITA_RISPOSTA_STREAM, CostantiExport.LABEL_COL_DATA_USCITA_RISPOSTA_STREAM, !SHOW_IN_CUSTOM_VIEW);
		boolean latenzaPorta = true;
		try {
			latenzaPorta = PddMonitorProperties.getInstance(LoggerWrapperFactory.getLogger(ColonnaExportManager.class)).isTransazioniLatenzaPortaEnabled();
		}catch(Throwable t) {
			// ignore
		}
		if(latenzaPorta) {
			this._addColonna(CostantiExport.KEY_COL_LATENZA_PORTA, CostantiExport.LABEL_COL_LATENZA_PORTA, SHOW_IN_CUSTOM_VIEW);
		}
//		else {
//			this._addColonna(CostantiExport.KEY_COL_LATENZA_PORTA, CostantiExport.LABEL_COL_LATENZA_PORTA, !SHOW_IN_CUSTOM_VIEW);
//		}
		this._addColonna(CostantiExport.KEY_COL_LATENZA_SERVIZIO, CostantiExport.LABEL_COL_LATENZA_SERVIZIO, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_LATENZA_TOTALE, CostantiExport.LABEL_COL_LATENZA_TOTALE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_CODICE_RISPOSTA_INGRESSO, CostantiExport.LABEL_COL_CODICE_RISPOSTA_INGRESSO, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_CODICE_RISPOSTA_USCITA, CostantiExport.LABEL_COL_CODICE_RISPOSTA_USCITA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_RICHIESTA_INGRESSO_BYTES, CostantiExport.LABEL_COL_RICHIESTA_INGRESSO_BYTES, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_RICHIESTA_USCITA_BYTES, CostantiExport.LABEL_COL_RICHIESTA_USCITA_BYTES, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_RISPOSTA_INGRESSO_BYTES, CostantiExport.LABEL_COL_RISPOSTA_INGRESSO_BYTES, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_RISPOSTA_USCITA_BYTES, CostantiExport.LABEL_COL_RISPOSTA_USCITA_BYTES, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_SOGGETTO_FRUITORE, CostantiExport.LABEL_COL_SOGGETTO_FRUITORE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ID_PORTA_SOGGETTO_FRUITORE, CostantiExport.LABEL_COL_ID_PORTA_SOGGETTO_FRUITORE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_INDIRIZZO_SOGGETTO_FRUITORE, CostantiExport.LABEL_COL_INDIRIZZO_SOGGETTO_FRUITORE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_SOGGETTO_EROGATORE, CostantiExport.LABEL_COL_SOGGETTO_EROGATORE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ID_PORTA_SOGGETTO_EROGATORE, CostantiExport.LABEL_COL_ID_PORTA_SOGGETTO_EROGATORE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_INDIRIZZO_SOGGETTO_EROGATORE, CostantiExport.LABEL_COL_INDIRIZZO_SOGGETTO_EROGATORE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ID_MESSAGGIO_RICHIESTA, CostantiExport.LABEL_COL_ID_MESSAGGIO_RICHIESTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ID_MESSAGGIO_RISPOSTA, CostantiExport.LABEL_COL_ID_MESSAGGIO_RISPOSTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_ID_MSG_RICHIESTA, CostantiExport.LABEL_COL_DATA_ID_MSG_RICHIESTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DATA_ID_MSG_RISPOSTA, CostantiExport.LABEL_COL_DATA_ID_MSG_RISPOSTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_RUOLO_TRANSAZIONE, CostantiExport.LABEL_COL_RUOLO_TRANSAZIONE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_PROFILO_COLLABORAZIONE_OP2, CostantiExport.LABEL_COL_PROFILO_COLLABORAZIONE_OP2, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_PROFILO_COLLABORAZIONE_PROT, CostantiExport.LABEL_COL_PROFILO_COLLABORAZIONE_PROT, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ID_COLLABORAZIONE, CostantiExport.LABEL_COL_ID_COLLABORAZIONE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_URI_ACCORDO_SERVIZIO, CostantiExport.LABEL_COL_URI_ACCORDO_SERVIZIO, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TIPO_API, CostantiExport.LABEL_COL_TIPO_API, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TAGS, CostantiExport.LABEL_COL_TAGS, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_SERVIZIO, CostantiExport.LABEL_COL_SERVIZIO, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_AZIONE, CostantiExport.LABEL_COL_AZIONE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ID_CORRELAZIONE_APPLICATIVA, CostantiExport.LABEL_COL_ID_CORRELAZIONE_APPLICATIVA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ID_CORRELAZIONEAPPLICATIVARISPOSTA, CostantiExport.LABEL_COL_ID_CORRELAZIONEAPPLICATIVARISPOSTA, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO, CostantiExport.LABEL_COL_SERVIZIO_APPLICATIVO, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO_EROGATORE, CostantiExport.LABEL_COL_SERVIZIO_APPLICATIVO_EROGATORE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO_FRUITORE, CostantiExport.LABEL_COL_SERVIZIO_APPLICATIVO_FRUITORE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_FAULT_INTEGRAZIONE, CostantiExport.LABEL_COL_FAULT_INTEGRAZIONE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_FAULT_COOPERAZIONE, CostantiExport.LABEL_COL_FAULT_COOPERAZIONE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_ID_ASINCRONO, CostantiExport.LABEL_COL_ID_ASINCRONO, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_SERVIZIO_CORRELATO, CostantiExport.LABEL_COL_SERVIZIO_CORRELATO, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_HEADER_PROTOCOLLO_RICHIESTA, CostantiExport.LABEL_COL_HEADER_PROTOCOLLO_RICHIESTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DIGEST_RICHIESTA, CostantiExport.LABEL_COL_DIGEST_RICHIESTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_PROTOCOLLO_EXT_INFO_RICHIESTA, CostantiExport.LABEL_COL_PROTOCOLLO_EXT_INFO_RICHIESTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_HEADER_PROTOCOLLO_RISPOSTA, CostantiExport.LABEL_COL_HEADER_PROTOCOLLO_RISPOSTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DIGEST_RISPOSTA, CostantiExport.LABEL_COL_DIGEST_RISPOSTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_PROTOCOLLO_EXT_INFO_RISPOSTA, CostantiExport.LABEL_COL_PROTOCOLLO_EXT_INFO_RISPOSTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_OPERAZIONE_IM, CostantiExport.LABEL_COL_OPERAZIONE_IM, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_LOCATION_RICHIESTA, CostantiExport.LABEL_COL_LOCATION_RICHIESTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_LOCATION_RISPOSTA, CostantiExport.LABEL_COL_LOCATION_RISPOSTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_NOME_PORTA, CostantiExport.LABEL_COL_NOME_PORTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_CREDENZIALI, CostantiExport.LABEL_COL_CREDENZIALI, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_LOCATION_CONNETTORE, CostantiExport.LABEL_COL_LOCATION_CONNETTORE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_URL_INVOCAZIONE, CostantiExport.LABEL_COL_URL_INVOCAZIONE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DUPLICATI_RICHIESTA, CostantiExport.LABEL_COL_DUPLICATI_RICHIESTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DUPLICATI_RISPOSTA, CostantiExport.LABEL_COL_DUPLICATI_RISPOSTA, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_INDIRIZZO_CLIENT, CostantiExport.LABEL_COL_INDIRIZZO_CLIENT, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_X_FORWARDED_FOR, CostantiExport.LABEL_COL_X_FORWARDED_FOR, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_RICHIEDENTE, CostantiExport.LABEL_COL_RICHIEDENTE, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_DETTAGLIO_ERRORE, CostantiExport.LABEL_COL_DETTAGLIO_ERRORE, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TOKEN_ISSUER, CostantiExport.LABEL_COL_TOKEN_ISSUER, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TOKEN_SUBJECT, CostantiExport.LABEL_COL_TOKEN_SUBJECT, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TOKEN_CLIENT, CostantiExport.LABEL_COL_TOKEN_CLIENT, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TOKEN_USERNAME, CostantiExport.LABEL_COL_TOKEN_USERNAME, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TOKEN_EMAIL, CostantiExport.LABEL_COL_TOKEN_EMAIL, !SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TOKEN_CLIENT_APPLICATIVO, CostantiExport.LABEL_COL_TOKEN_CLIENT_APPLICATIVO, SHOW_IN_CUSTOM_VIEW);
		this._addColonna(CostantiExport.KEY_COL_TOKEN_CLIENT_SOGGETTO, CostantiExport.LABEL_COL_TOKEN_CLIENT_SOGGETTO, SHOW_IN_CUSTOM_VIEW);

		
//		// AGGIUNTA TRACCE GESTITE DALL'UTENTE NELLA FORM NELLA PAGINA
//		this._addColonna(CostantiExport.KEY_COL_TRACCIA_RICHIESTA, CostantiExport.LABEL_COL_TRACCIA_RICHIESTA,true);
//		this._addColonna(CostantiExport.KEY_COL_TRACCIA_RISPOSTA, CostantiExport.LABEL_COL_TRACCIA_RISPOSTA,true);
//		
//		// AGGIUNTA DIAGNOSTICI GESTITA DALL'UTENTE NELLA FORM NELLA PAGINA
//		this._addColonna(CostantiExport.KEY_COL_DIAGNOSTICI, CostantiExport.LABEL_COL_DIAGNOSTICI,true);
		
//		this._addColonna(ColonnaExportCostanti.KEY_COL_DIAGNOSTICI_LIST1, ColonnaExportCostanti.LABEL_COL_DIAGNOSTICI_LIST1);
//		this._addColonna(ColonnaExportCostanti.KEY_COL_DIAGNOSTICI_LIST2, ColonnaExportCostanti.LABEL_COL_DIAGNOSTICI_LIST2);
//		this._addColonna(ColonnaExportCostanti.KEY_COL_DIAGNOSTICI_LIST_EXT, ColonnaExportCostanti.LABEL_COL_DIAGNOSTICI_LIST_EXT);
//		this._addColonna(ColonnaExportCostanti.KEY_COL_DIAGNOSTICI_EXT, ColonnaExportCostanti.LABEL_COL_DIAGNOSTICI_EXT);
	
	}
	
	private void _addColonna(String key, String label, boolean showInCustomView){
		this.addColonna(key, label, false);
		if(showInCustomView) {
			this.ordineInserimentoColonneCustomView.add(key);
		}
	}
	
	public void addColonna(String key, String label){
		this.addColonna(key, label, false);
	}
	
	public void addColonna(String key, ColonnaExport value){
		this.addColonna(key, value, false);
	}	
	
	public void addColonna(String key, String label,boolean escludi){
		this.mapColonne.put(key, new ColonnaExport(key, label, false));
		this.ordineInserimentoColonne.add(key);
		if(escludi)
			this.colonneNonSelezionabiliDallUtente.add(key);
	}
	
	public void addColonna(String key, String label, boolean visibile,boolean escludi){
		this.mapColonne.put(key, new ColonnaExport(key, label, visibile));
		this.ordineInserimentoColonne.add(key);
		if(escludi)
			this.colonneNonSelezionabiliDallUtente.add(key);
	}
	
	public void addColonna(String key, ColonnaExport value,boolean escludi){
		this.mapColonne.put(key, value);
		this.ordineInserimentoColonne.add(key);
		if(escludi)
			this.colonneNonSelezionabiliDallUtente.add(key);
	}
	
	public List<SelectItem> getColonne(){
		List<org.openspcoop2.web.monitor.core.bean.SelectItem> colonne = new  ArrayList<org.openspcoop2.web.monitor.core.bean.SelectItem>();
	
		// stringhe ordinate per ordine inserimento
		for (String key : this.ordineInserimentoColonne) {
			// utente vede solo le colonne che puo' visualizzare
			if(!this.colonneNonSelezionabiliDallUtente.contains(key))
				colonne.add(new SelectItem(key, this.mapColonne.get(key).getLabel())); 
		}
		
		return colonne;
	}
	
	public List<String> getKeysColonneTracce(){
		List<String> lst = new ArrayList<String>();
		lst.add(CostantiExport.KEY_COL_TRACCIA_RICHIESTA);
		lst.add(CostantiExport.KEY_COL_TRACCIA_RISPOSTA);
		return lst;
	}
	
	public List<String> getKeysColonneDiagnostici(){
		List<String> lst = new ArrayList<String>();
		lst.add(CostantiExport.KEY_COL_DIAGNOSTICI);
		return lst;
	}
	
	public List<String> getKeysColonne(){
		List<String> lst = new ArrayList<String>();
		
		for (String string : this.ordineInserimentoColonne) {
			if(!this.colonneNonSelezionabiliDallUtente.contains(string))
				lst.add(string);
		}
		
//		lst.addAll(this.ordineInserimentoColonne);
		return lst;
	}
	
	public List<String> getKeysColonneCustomView(){
		List<String> lst = new ArrayList<String>();
		
		for (String string : this.ordineInserimentoColonneCustomView) {
			lst.add(string);
		}
		
//		lst.addAll(this.ordineInserimentoColonne);
		return lst;
	}
	
	public boolean containsColonna(String key){
		return this.mapColonne.containsKey(key);
	}
	
	public ColonnaExport getColonna(String key){
		if(this.containsColonna(key))
			return this.mapColonne.get(key);
		
		return null;
	}
}

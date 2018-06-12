package org.openspcoop2.web.monitor.transazioni.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.web.monitor.core.bean.SelectItem;

public class ColonnaExportManager {

	private static ColonnaExportManager instance = null;
	
	private Map<String, ColonnaExport> mapColonne = null;
	private List<String> colonneNonSelezionabiliDallUtente = null;
	private List<String> ordineInserimentoColonne = null;
	
	private static synchronized void init(){
		if(ColonnaExportManager.instance == null)
			ColonnaExportManager.instance = new ColonnaExportManager();
	}
	
	public static ColonnaExportManager getInstance(){
		if(ColonnaExportManager.instance == null)
			init();
		
		return ColonnaExportManager.instance;
	}
	
	public ColonnaExportManager(){
		this.mapColonne = new HashMap<String, ColonnaExport>();
		this.colonneNonSelezionabiliDallUtente = new ArrayList<String>();
		this.ordineInserimentoColonne = new ArrayList<String>();
		
		this.addColonna(CostantiExport.KEY_COL_ID_TRANSAZIONE, CostantiExport.LABEL_COL_ID_TRANSAZIONE);
		this.addColonna(CostantiExport.KEY_COL_PROTOCOLLO, CostantiExport.LABEL_COL_PROTOCOLLO);
		this.addColonna(CostantiExport.KEY_COL_ESITO, CostantiExport.LABEL_COL_ESITO);
		this.addColonna(CostantiExport.KEY_COL_ESITO_CONTESTO, CostantiExport.LABEL_COL_ESITO_CONTESTO);
		this.addColonna(CostantiExport.KEY_COL_TIPO_RICHIESTA, CostantiExport.LABEL_COL_TIPO_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_CLUSTER_ID, CostantiExport.LABEL_COL_CLUSTER_ID);
		this.addColonna(CostantiExport.KEY_COL_EVENTI_GESTIONE, CostantiExport.LABEL_COL_EVENTI_GESTIONE);
		this.addColonna(CostantiExport.KEY_COL_STATO, CostantiExport.LABEL_COL_STATO);
		this.addColonna(CostantiExport.KEY_COL_PDD_RUOLO, CostantiExport.LABEL_COL_PDD_RUOLO);
		this.addColonna(CostantiExport.KEY_COL_PDD_CODICE, CostantiExport.LABEL_COL_PDD_CODICE);
		this.addColonna(CostantiExport.KEY_COL_PDD_SOGGETTO, CostantiExport.LABEL_COL_PDD_SOGGETTO);
		this.addColonna(CostantiExport.KEY_COL_DATA_ACCETTAZIONE_RICHIESTA, CostantiExport.LABEL_COL_DATA_ACCETTAZIONE_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_DATA_INGRESSO_RICHIESTA, CostantiExport.LABEL_COL_DATA_INGRESSO_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_DATA_USCITA_RICHIESTA, CostantiExport.LABEL_COL_DATA_USCITA_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_DATA_ACCETTAZIONE_RISPOSTA, CostantiExport.LABEL_COL_DATA_ACCETTAZIONE_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_DATA_INGRESSO_RISPOSTA, CostantiExport.LABEL_COL_DATA_INGRESSO_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_DATA_USCITA_RISPOSTA, CostantiExport.LABEL_COL_DATA_USCITA_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_LATENZA_PORTA, CostantiExport.LABEL_COL_LATENZA_PORTA);
		this.addColonna(CostantiExport.KEY_COL_LATENZA_SERVIZIO, CostantiExport.LABEL_COL_LATENZA_SERVIZIO);
		this.addColonna(CostantiExport.KEY_COL_LATENZA_TOTALE, CostantiExport.LABEL_COL_LATENZA_TOTALE);
		this.addColonna(CostantiExport.KEY_COL_CODICE_RISPOSTA_INGRESSO, CostantiExport.LABEL_COL_CODICE_RISPOSTA_INGRESSO);
		this.addColonna(CostantiExport.KEY_COL_CODICE_RISPOSTA_USCITA, CostantiExport.LABEL_COL_CODICE_RISPOSTA_USCITA);
		this.addColonna(CostantiExport.KEY_COL_RICHIESTA_INGRESSO_BYTES, CostantiExport.LABEL_COL_RICHIESTA_INGRESSO_BYTES);
		this.addColonna(CostantiExport.KEY_COL_RICHIESTA_USCITA_BYTES, CostantiExport.LABEL_COL_RICHIESTA_USCITA_BYTES);
		this.addColonna(CostantiExport.KEY_COL_RISPOSTA_INGRESSO_BYTES, CostantiExport.LABEL_COL_RISPOSTA_INGRESSO_BYTES);
		this.addColonna(CostantiExport.KEY_COL_RISPOSTA_USCITA_BYTES, CostantiExport.LABEL_COL_RISPOSTA_USCITA_BYTES);
		this.addColonna(CostantiExport.KEY_COL_SOGGETTO_FRUITORE, CostantiExport.LABEL_COL_SOGGETTO_FRUITORE);
		this.addColonna(CostantiExport.KEY_COL_ID_PORTA_SOGGETTO_FRUITORE, CostantiExport.LABEL_COL_ID_PORTA_SOGGETTO_FRUITORE);
		this.addColonna(CostantiExport.KEY_COL_INDIRIZZO_SOGGETTO_FRUITORE, CostantiExport.LABEL_COL_INDIRIZZO_SOGGETTO_FRUITORE);
		this.addColonna(CostantiExport.KEY_COL_SOGGETTO_EROGATORE, CostantiExport.LABEL_COL_SOGGETTO_EROGATORE);
		this.addColonna(CostantiExport.KEY_COL_ID_PORTA_SOGGETTO_EROGATORE, CostantiExport.LABEL_COL_ID_PORTA_SOGGETTO_EROGATORE);
		this.addColonna(CostantiExport.KEY_COL_INDIRIZZO_SOGGETTO_EROGATORE, CostantiExport.LABEL_COL_INDIRIZZO_SOGGETTO_EROGATORE);
		this.addColonna(CostantiExport.KEY_COL_ID_MESSAGGIO_RICHIESTA, CostantiExport.LABEL_COL_ID_MESSAGGIO_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_ID_MESSAGGIO_RISPOSTA, CostantiExport.LABEL_COL_ID_MESSAGGIO_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_DATA_ID_MSG_RICHIESTA, CostantiExport.LABEL_COL_DATA_ID_MSG_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_DATA_ID_MSG_RISPOSTA, CostantiExport.LABEL_COL_DATA_ID_MSG_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_RUOLO_TRANSAZIONE, CostantiExport.LABEL_COL_RUOLO_TRANSAZIONE);
		this.addColonna(CostantiExport.KEY_COL_PROFILO_COLLABORAZIONE_OP2, CostantiExport.LABEL_COL_PROFILO_COLLABORAZIONE_OP2);
		this.addColonna(CostantiExport.KEY_COL_PROFILO_COLLABORAZIONE_PROT, CostantiExport.LABEL_COL_PROFILO_COLLABORAZIONE_PROT);
		this.addColonna(CostantiExport.KEY_COL_ID_COLLABORAZIONE, CostantiExport.LABEL_COL_ID_COLLABORAZIONE);
		this.addColonna(CostantiExport.KEY_COL_URI_ACCORDO_SERVIZIO, CostantiExport.LABEL_COL_URI_ACCORDO_SERVIZIO);
		this.addColonna(CostantiExport.KEY_COL_SERVIZIO, CostantiExport.LABEL_COL_SERVIZIO);
		this.addColonna(CostantiExport.KEY_COL_AZIONE, CostantiExport.LABEL_COL_AZIONE);	
		this.addColonna(CostantiExport.KEY_COL_ID_CORRELAZIONE_APPLICATIVA, CostantiExport.LABEL_COL_ID_CORRELAZIONE_APPLICATIVA);
		this.addColonna(CostantiExport.KEY_COL_ID_CORRELAZIONEAPPLICATIVARISPOSTA, CostantiExport.LABEL_COL_ID_CORRELAZIONEAPPLICATIVARISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO, CostantiExport.LABEL_COL_SERVIZIO_APPLICATIVO);
		this.addColonna(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO_EROGATORE, CostantiExport.LABEL_COL_SERVIZIO_APPLICATIVO_EROGATORE);
		this.addColonna(CostantiExport.KEY_COL_SERVIZIO_APPLICATIVO_FRUITORE, CostantiExport.LABEL_COL_SERVIZIO_APPLICATIVO_FRUITORE);
		this.addColonna(CostantiExport.KEY_COL_FAULT_INTEGRAZIONE, CostantiExport.LABEL_COL_FAULT_INTEGRAZIONE);
		this.addColonna(CostantiExport.KEY_COL_FAULT_COOPERAZIONE, CostantiExport.LABEL_COL_FAULT_COOPERAZIONE);
		this.addColonna(CostantiExport.KEY_COL_ID_ASINCRONO, CostantiExport.LABEL_COL_ID_ASINCRONO);
		this.addColonna(CostantiExport.KEY_COL_SERVIZIO_CORRELATO, CostantiExport.LABEL_COL_SERVIZIO_CORRELATO);
		this.addColonna(CostantiExport.KEY_COL_HEADER_PROTOCOLLO_RICHIESTA, CostantiExport.LABEL_COL_HEADER_PROTOCOLLO_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_DIGEST_RICHIESTA, CostantiExport.LABEL_COL_DIGEST_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_PROTOCOLLO_EXT_INFO_RICHIESTA, CostantiExport.LABEL_COL_PROTOCOLLO_EXT_INFO_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_HEADER_PROTOCOLLO_RISPOSTA, CostantiExport.LABEL_COL_HEADER_PROTOCOLLO_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_DIGEST_RISPOSTA, CostantiExport.LABEL_COL_DIGEST_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_PROTOCOLLO_EXT_INFO_RISPOSTA, CostantiExport.LABEL_COL_PROTOCOLLO_EXT_INFO_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_OPERAZIONE_IM, CostantiExport.LABEL_COL_OPERAZIONE_IM);
		this.addColonna(CostantiExport.KEY_COL_LOCATION_RICHIESTA, CostantiExport.LABEL_COL_LOCATION_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_LOCATION_RISPOSTA, CostantiExport.LABEL_COL_LOCATION_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_NOME_PORTA, CostantiExport.LABEL_COL_NOME_PORTA);
		this.addColonna(CostantiExport.KEY_COL_CREDENZIALI, CostantiExport.LABEL_COL_CREDENZIALI);
		this.addColonna(CostantiExport.KEY_COL_LOCATION_CONNETTORE, CostantiExport.LABEL_COL_LOCATION_CONNETTORE);
		this.addColonna(CostantiExport.KEY_COL_URL_INVOCAZIONE, CostantiExport.LABEL_COL_URL_INVOCAZIONE);
		this.addColonna(CostantiExport.KEY_COL_DUPLICATI_RICHIESTA, CostantiExport.LABEL_COL_DUPLICATI_RICHIESTA);
		this.addColonna(CostantiExport.KEY_COL_DUPLICATI_RISPOSTA, CostantiExport.LABEL_COL_DUPLICATI_RISPOSTA);
		this.addColonna(CostantiExport.KEY_COL_INDIRIZZO_CLIENT, CostantiExport.LABEL_COL_INDIRIZZO_CLIENT);
		this.addColonna(CostantiExport.KEY_COL_X_FORWARDED_FOR, CostantiExport.LABEL_COL_X_FORWARDED_FOR);

		
		// AGGIUNTA TRACCE GESTITE DALL'UTENTE NELLA FORM NELLA PAGINA
		this.addColonna(CostantiExport.KEY_COL_TRACCIA_RICHIESTA, CostantiExport.LABEL_COL_TRACCIA_RICHIESTA,true);
		this.addColonna(CostantiExport.KEY_COL_TRACCIA_RISPOSTA, CostantiExport.LABEL_COL_TRACCIA_RISPOSTA,true);
		
		// AGGIUNTA DIAGNOSTICI GESTITA DALL'UTENTE NELLA FORM NELLA PAGINA
		this.addColonna(CostantiExport.KEY_COL_DIAGNOSTICI, CostantiExport.LABEL_COL_DIAGNOSTICI,true);
		
//		this.addColonna(ColonnaExportCostanti.KEY_COL_DIAGNOSTICI_LIST1, ColonnaExportCostanti.LABEL_COL_DIAGNOSTICI_LIST1);
//		this.addColonna(ColonnaExportCostanti.KEY_COL_DIAGNOSTICI_LIST2, ColonnaExportCostanti.LABEL_COL_DIAGNOSTICI_LIST2);
//		this.addColonna(ColonnaExportCostanti.KEY_COL_DIAGNOSTICI_LIST_EXT, ColonnaExportCostanti.LABEL_COL_DIAGNOSTICI_LIST_EXT);
//		this.addColonna(ColonnaExportCostanti.KEY_COL_DIAGNOSTICI_EXT, ColonnaExportCostanti.LABEL_COL_DIAGNOSTICI_EXT);
	
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
	
	public boolean containsColonna(String key){
		return this.mapColonne.containsKey(key);
	}
	
	public ColonnaExport getColonna(String key){
		if(this.containsColonna(key))
			return this.mapColonne.get(key);
		
		return null;
	}
}

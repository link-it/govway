package org.openspcoop2.web.monitor.core.dao;

import it.link.pdd.core.plugins.base.ConfigurazioneServizio;
import it.link.pdd.core.plugins.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import it.link.pdd.core.plugins.transazioni.ConfigurazioneTransazioneStato;
import it.link.pdd.core.utenti.StatoTabella;
import it.link.pdd.core.utenti.Utente;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.web.monitor.core.costants.NomiTabelle;

import java.util.List;

import org.openspcoop2.core.id.IDAccordo;

public interface IGenericService {


	public List<ConfigurazioneServizio> findServiziByIDAccordo(IDAccordo idAccordo);

//	public List<ServizioAzioneConfig> findAzioni(IDAccordo idAccordoSelezionato,	String nomeServizioSelezionato);
	/**
	 * verifica l'esistenza di un'azione di configurazione tramite il nome
	 * @param nomeAzione
	 * @return
	 */
	public boolean existServizioAzioneConfigByName(IDAccordo idAccordo, String nomeServizio, String nomeAzione);
	
	public List<ConfigurazioneTransazioneStato> findStatiByAzione(IDAccordo idAccordoSelezionato, String nomeServizioSelezionato, String azione);
	
	/**
	 * Recupera la risorse di configurazione
	 * @param idAccordoSelezionato (required)
	 * @param nomeServizioSelezionato (required)
	 * @param nomeAzioneSelezionata (required)
	 * @param nomeStatoSelezionato (opzionale)
	 * @return la lista di risorse
	 */
	public List<ConfigurazioneTransazioneRisorsaContenuto> getRisorseByValues(IDAccordo idAccordoSelezionato, String nomeServizioSelezionato,
			String nomeAzioneSelezionata, String nomeStatoSelezionato);
	
	public StatoTabella getTableState(Utente user, NomiTabelle nomeTabella);
	public void saveTableState(Utente user,StatoTabella stato);
	
	public List<Soggetto> soggettiAutoComplete(String input);
}

package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.constants.NomiTabelle;

import java.util.List;

import org.openspcoop2.core.id.IDAccordo;

public interface IGenericService {


	public List<ConfigurazioneServizio> findServiziByIDAccordo(IDAccordo idAccordo);

//	public List<ServizioAzioneConfig> findAzioni(IDAccordo idAccordoSelezionato,	String nomeServizioSelezionato);
	/**
	 * verifica l'esistenza di un'azione di configurazione tramite il nome
	 * @param nomeAzione
	 * @return true se l'azione e' presente false altrimenti
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
	
	public Stato getTableState(User user, NomiTabelle nomeTabella);
	public void saveTableState(User user,Stato stato);
	
	public List<Soggetto> soggettiAutoComplete(String input);
}

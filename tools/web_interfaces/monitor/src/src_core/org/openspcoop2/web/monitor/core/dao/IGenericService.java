/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.dao;

import org.openspcoop2.core.plugins.ConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.constants.NomiTabelle;

import java.util.List;

import org.openspcoop2.core.id.IDAccordo;

/**
 * IGenericService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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

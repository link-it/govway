/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.sonde.impl;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.sonde.ParametriSonda;
import org.openspcoop2.utils.sonde.Sonda;
import org.openspcoop2.utils.sonde.SondaException;

/**
 * Classe di implementazione della Sonda per i batch
 * 
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SondaBatch extends Sonda {

	/**
	 * Costruttore per la classe SondaBatch
	 * @param param parametri costruttivi della sonda
	 * @throws Exception
	 */
	public SondaBatch(ParametriSonda param) {
		super(param);
		Set<String> reserved = new HashSet<String>();
		reserved.add("data_ultimo_batch");
		reserved.add("esito_batch");
		reserved.add("interazioni_fallite");
		reserved.add("descrizione_errore");
		this.getParam().setReserved(reserved);

	}

	@Override
	public StatoSonda getStatoSonda(){


		Date now = new Date();
		Date data_warn = new Date(now.getTime() - super.getParam().getSogliaWarn());
		Date data_err = new Date(now.getTime() - super.getParam().getSogliaError());

		StatoSonda statoSonda = new StatoSonda();
		boolean esito_batch = false;
		if(super.getParam().getDatiCheck().containsKey("esito_batch")) {
			String esitoBatchString = (String) super.getParam().getDatiCheck().getProperty("esito_batch");
			try{
				esito_batch = Boolean.parseBoolean(esitoBatchString);
			} catch(NumberFormatException e){
				e.printStackTrace(System.err);
				System.err.println("Errore durante il parsing del parametro esito_batch: " + super.getParam().getDatiCheck().getProperty("esito_batch") + ". Elimino il valore");
				super.getParam().getDatiCheck().remove("esito_batch");
			}
		} else {
			System.err.println("Parametro esito_batch non trovato");
		}

		Long dataUltimoBatchLong = null;
		if(super.getParam().getDatiCheck().containsKey("data_ultimo_batch")) {
			try {
				
				dataUltimoBatchLong = Long.valueOf(super.getParam().getDatiCheck().getProperty("data_ultimo_batch"));
			} catch(NumberFormatException e) {
				e.printStackTrace(System.err);
				System.err.println("Errore durante il parsing del parametro data_ultimo_batch: " + super.getParam().getDatiCheck().getProperty("data_ultimo_batch") + ". Elimino il valore");
				super.getParam().getDatiCheck().remove("data_ultimo_batch");
			}
		} else {
			System.err.println("Parametro data_ultimo_batch non trovato");
		}

		if(dataUltimoBatchLong == null) {
			statoSonda.setStato(2);
			statoSonda.setDescrizione("Il batch "+super.getParam().getNome()+" risulta non essere eseguito.");
			return statoSonda;
		}

		Date data_ultimo_batch = new Date(dataUltimoBatchLong);

		SimpleDateFormat format = new SimpleDateFormat(PATTERN);
		String dataUltimoBatchString = format.format(data_ultimo_batch);
		
		if(esito_batch) {

			//Valuto l'eventuale superamento delle soglie e calcolo lo stato
			if(data_ultimo_batch.before(data_err)) {
				String errorDateFormatted = format.format(data_err);
				statoSonda.setStato(2);
				statoSonda.setDescrizione("Il batch "+super.getParam().getNome()+" risulta non essere eseguito dal "+dataUltimoBatchString+". Data di error ("+errorDateFormatted+") superata.");
			} else if(data_ultimo_batch.before(data_warn)) {
				String warnDateFormatted = format.format(data_warn);
				statoSonda.setStato(1);
				statoSonda.setDescrizione("Il batch "+super.getParam().getNome()+" risulta non essere eseguito dal "+dataUltimoBatchString+". Data di warn ("+warnDateFormatted+") superata.");
			} else {
				statoSonda.setStato(0);
				statoSonda.setDescrizione("Batch "+super.getParam().getNome()+" eseguito con successo il "+dataUltimoBatchString+".");
			}
	 
			return statoSonda;
		} else {
			
			Integer interazioniFallite = -1;
			if(super.getParam().getDatiCheck().containsKey("interazioni_fallite")) {
				try{
					interazioniFallite = Integer.parseInt(super.getParam().getDatiCheck().getProperty("interazioni_fallite"));
				} catch(NumberFormatException e) {
					e.printStackTrace(System.err);
					System.err.println("Errore durante il parsing del parametro interazioni_fallite: " + super.getParam().getDatiCheck().getProperty("interazioni_fallite") + ". Elimino il valore");
					super.getParam().getDatiCheck().remove("interazioni_fallite");
				}
			} else {
				System.err.println("Parametro interazioni_fallite non trovato");
			}

			statoSonda.setStato(2);
			String descr = null;
			if(super.getParam().getDatiCheck().containsKey("descrizione_errore")) {
				descr = new String(Base64Utilities.decode((String) super.getParam().getDatiCheck().get("descrizione_errore")));
			}
			statoSonda.setDescrizione("Il batch "+super.getParam().getNome()+" risulta fallire dal "+dataUltimoBatchString+" (fallite "+interazioniFallite+" iterazioni). Descrizione dell'ultimo errore:" + descr);
			return statoSonda;
		}
	}

	
	//retrocompatibilita
	public StatoSonda aggiornaStatoSonda(boolean esito_batch, Date data_ultimo_batch, String descrizioneErrore, Connection connection, TipiDatabase tipoDatabase) throws SondaException {
		return this.aggiornaStatoSonda(esito_batch, null, data_ultimo_batch, descrizioneErrore, connection, tipoDatabase);
	}
	
	/**
	 * @param esito_batch true se l'esito e' positivo, false altrimenti
	 * @param data_ultimo_batch ultima data di esecuzione del batch 
	 * @param descrizioneErrore eventuale descrizione dell'errore (in caso di esito negativo)
	 * @param connection connessione per il DB
	 * @param tipoDatabase tipo database
	 * @return lo stato attuale della sonda
	 * @throws SondaException
	 */
	public StatoSonda aggiornaStatoSonda(boolean esito_batch, Properties params, Date data_ultimo_batch, String descrizioneErrore, Connection connection, TipiDatabase tipoDatabase) throws SondaException {
		// inserisce i dati nel properties
		
		super.getParam().putAllCheck(params);
		
		super.getParam().getDatiCheck().put("data_ultimo_batch", data_ultimo_batch.getTime()+ "");
		super.getParam().getDatiCheck().put("esito_batch", String.valueOf(esito_batch));
		if(!esito_batch) {
			if(super.getParam().getDatiCheck().containsKey("interazioni_fallite")) {
				Integer interazioniFallite = Integer.parseInt(super.getParam().getDatiCheck().getProperty("interazioni_fallite"));
				super.getParam().getDatiCheck().put("interazioni_fallite", (interazioniFallite+1) + "");
			} else {
				super.getParam().getDatiCheck().put("interazioni_fallite", 1 + "");
			}
			if(descrizioneErrore != null) {
				super.getParam().getDatiCheck().put("descrizione_errore", Base64Utilities.encodeAsString(descrizioneErrore.getBytes()));
			}
		} else {
			if(super.getParam().getDatiCheck().containsKey("descrizione_errore"))
				super.getParam().getDatiCheck().remove("descrizione_errore");
			if(super.getParam().getDatiCheck().containsKey("interazioni_fallite"))
				super.getParam().getDatiCheck().remove("interazioni_fallite");
		}
		return updateSonda(connection, tipoDatabase);
	}

}

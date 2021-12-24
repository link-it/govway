/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.pdd.timers;

import java.io.Serializable;

import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;

/**
 * Contiene i tipi di messaggio
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum TipoLock implements Serializable {

	// Utilizzato sia nel GestoreMessaggi, che nel RepositoryBuste, che nel ConsegnaMesssaggi per risolvere il problema descritto in GestoreMessaggi per quanto concerne l'eliminazione messaggi solo se abilitata la gestione tramite lock
	@Deprecated
	GESTIONE_REPOSITORY_MESSAGGI ("RepositoryMessaggi"), 
	
	GESTIONE_PULIZIA_REPOSITORY_MESSAGGI ("PuliziaRepositoryMessaggi"), 
	
	GESTIONE_PULIZIA_REPOSITORY_BUSTE ("PuliziaRepositoryBuste"), 
	
	GESTIONE_CORRELAZIONE_APPLICATIVA ("CorrelazioneApplicativa"),
	
	GESTIONE_BUSTE_NON_RISCONTRATE ("BusteNonRiscontrate"), 
	
	GESTIONE_PULIZIA_MESSAGGI_ANOMALI ("PuliziaMessaggiAnomali"),
	
	GENERAZIONE_STATISTICHE_ORARIE (getLockStatistico(TipoIntervalloStatistico.STATISTICHE_ORARIE)), 
	GENERAZIONE_STATISTICHE_GIORNALIERE (getLockStatistico(TipoIntervalloStatistico.STATISTICHE_GIORNALIERE)), 
	GENERAZIONE_STATISTICHE_SETTIMANALI (getLockStatistico(TipoIntervalloStatistico.STATISTICHE_SETTIMANALI)), 
	GENERAZIONE_STATISTICHE_MENSILI (getLockStatistico(TipoIntervalloStatistico.STATISTICHE_MENSILI)), 
	
	CONSEGNA_NOTIFICHE ("ConsegnaNotifiche"),
	
	STARTUP ("StartupGateway"), 
	
	CUSTOM ("Custom");
	
	
	private final static String getLockStatistico(TipoIntervalloStatistico tipo) {
		return "Generazione"+tipo.getValue();
	}
	
	private final String tipo;

	TipoLock(String tipo)
	{
		this.tipo = tipo;
	}

	public String getTipo()
	{
		return this.tipo;
	}
	
	public boolean equals(TipoLock tipo){
		return this.tipo.equals(tipo.getTipo());
	}
	
	public static TipoLock _getLockGestioneRepositoryMessaggi() {
		return TipoLock.GESTIONE_REPOSITORY_MESSAGGI;
	}
}


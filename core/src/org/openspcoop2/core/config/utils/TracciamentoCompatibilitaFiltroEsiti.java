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

package org.openspcoop2.core.config.utils;

import org.openspcoop2.core.config.TracciamentoConfigurazione;

/**
 * TracciamentoCompatibilitaFiltroEsiti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciamentoCompatibilitaFiltroEsiti {

	private TracciamentoConfigurazione database;
	private TracciamentoConfigurazione filetrace;
	
	public  TracciamentoCompatibilitaFiltroEsiti(TracciamentoConfigurazione database, TracciamentoConfigurazione filetrace) {
		this.database = database;
		this.filetrace = filetrace;
	}
	
	// METODI SPECIFICI DATABASE
	
	public boolean isTracciamentoDBEnabled() {
		if (this.database==null 
				|| this.database.getStato()==null) {
			return true; // default
		} 
		return !org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.DISABILITATO.equals(this.database.getStato());
	}
	
	public boolean isTracciamentoDBRequestInEnabledBloccante() {
		return this.database!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.database.getStato()) &&
				this.database.getRequestIn()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.equals(this.database.getRequestIn());
	}
	public boolean isTracciamentoDBRequestInEnabledNonBloccante() {
		return this.database!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.database.getStato()) &&
				this.database.getRequestIn()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.equals(this.database.getRequestIn());
	}
	
	public boolean isTracciamentoDBRequestOutEnabledBloccante() {
		return this.database!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.database.getStato()) &&
				this.database.getRequestOut()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.equals(this.database.getRequestOut());
	}
	public boolean isTracciamentoDBRequestOutEnabledNonBloccante() {
		return this.database!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.database.getStato()) &&
				this.database.getRequestOut()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.equals(this.database.getRequestOut());
	}
	
	public boolean isTracciamentoDBResponseOutEnabledBloccante() {
		return this.database!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.database.getStato()) &&
				this.database.getResponseOut()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.equals(this.database.getResponseOut());
	}
	public boolean isTracciamentoDBResponseOutEnabledNonBloccante() {
		return this.database!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.database.getStato()) &&
				this.database.getResponseOut()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.equals(this.database.getResponseOut());
	}
	
	public boolean isTracciamentoDBResponseOutCompleteEnabled() {
		if (this.database==null ||
				this.database.getStato()==null) {
			return true; // default
		} 
		switch (this.database.getStato()) {
		case ABILITATO:
		case CONFIGURAZIONE_ESTERNA: // abilitato; non esiste una configurazione su proprietà
			return true;
		case DISABILITATO:
			return false;
		case PERSONALIZZATO:
			return (
					this.database.getResponseOutComplete()==null // default
					||
					org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(this.database.getResponseOutComplete())
				);
		}
		return true;
	}
	
	public boolean isFilterDBEnabled() {
		if(!this.isTracciamentoDBEnabled()) {
			return false;
		}
		if (this.database==null) {
			return true; // default
		} 
		if (this.database.getFiltroEsiti()==null) {
			return true; // default
		} 
		if(org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO.equals(this.database.getFiltroEsiti())) {
			return false;
		}
		
		// se e' abilitata la registrazione iniziale, non può essere effettuato alcun filtro sull'esito. 
		// gli altri filtri invece possono essere sensati, a patto che non sia già stata effettuata una registrazione
		// nel contesto, prima di fare lo skip, verrà verificato se è già stata attuata una registrazione
		return !isTracciamentoDBRequestInEnabledBloccante() && !isTracciamentoDBRequestInEnabledNonBloccante();
	}
	
	
	// METODI SPECIFICI FILETRACE
	
	public boolean isTracciamentoFileTraceEnabled() {
		if (this.filetrace==null 
				|| this.filetrace.getStato()==null) {
			return true; // default: verrò consultata la configurazione su file di proprietà
		} 
		return !org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.DISABILITATO.equals(this.filetrace.getStato());
	}
	public boolean isTracciamentoFileTraceEnabledByExternalProperties() {
		if (this.filetrace==null 
				|| this.filetrace.getStato()==null) {
			return true; // default: verrò consultata la configurazione su file di proprietà
		} 
		return org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.CONFIGURAZIONE_ESTERNA.equals(this.filetrace.getStato());
	}
	
	public boolean isTracciamentoFileTraceRequestInEnabledBloccante() {
		return this.filetrace!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.filetrace.getStato()) &&
				this.filetrace.getRequestIn()!=null &&
				(org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.equals(this.filetrace.getRequestIn()));
	}
	public boolean isTracciamentoFileTraceRequestInEnabledNonBloccante() {
		return this.filetrace!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.filetrace.getStato()) &&
				this.filetrace.getRequestIn()!=null &&
				(org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.equals(this.filetrace.getRequestIn()));
	}
	
	public boolean isTracciamentoFileTraceRequestOutEnabledBloccante() {
		return this.filetrace!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.filetrace.getStato()) &&
				this.filetrace.getRequestOut()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.equals(this.filetrace.getRequestOut());
	}
	public boolean isTracciamentoFileTraceRequestOutEnabledNonBloccante() {
		return this.filetrace!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.filetrace.getStato()) &&
				this.filetrace.getRequestOut()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.equals(this.filetrace.getRequestOut());
	}
	
	public boolean isTracciamentoFileTraceResponseOutEnabledBloccante() {
		return this.filetrace!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.filetrace.getStato()) &&
				this.filetrace.getResponseOut()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.ABILITATO.equals(this.filetrace.getResponseOut());
	}
	public boolean isTracciamentoFileTraceResponseOutEnabledNonBloccante() {
		return this.filetrace!=null && 
				org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione.PERSONALIZZATO.equals(this.filetrace.getStato()) &&
				this.filetrace.getResponseOut()!=null &&
				org.openspcoop2.core.config.constants.StatoFunzionalitaBloccante.NON_BLOCCANTE.equals(this.filetrace.getResponseOut());
	}
	
	public boolean isTracciamentoFileTraceResponseOutCompleteEnabled() {
		if (this.filetrace==null ||
				this.filetrace.getStato()==null) {
			return false; // default
		} 
		switch (this.filetrace.getStato()) {
		case ABILITATO:
		case CONFIGURAZIONE_ESTERNA: // si assume per default abilitato se è abilitato via file esterno
			return true;
		case DISABILITATO:
			return false;
		case PERSONALIZZATO:
			return (
					this.filetrace.getResponseOutComplete()==null // default
					||
					org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(this.filetrace.getResponseOutComplete())
				);
		}
		return true;
	}
	
	public boolean isFilterFileTraceEnabled() {
		if(!this.isTracciamentoFileTraceEnabled()) {
			return false;
		}
		if (this.filetrace==null) {
			return false; // default
		} 
		if (this.filetrace.getFiltroEsiti()==null) {
			return false; // default
		} 
		if(org.openspcoop2.core.config.constants.StatoFunzionalita.DISABILITATO.equals(this.filetrace.getFiltroEsiti())) {
			return false;
		}

		// se e' abilitata la registrazione iniziale, non può essere effettuato alcun filtro sull'esito. 
		// gli altri filtri invece possono essere sensati, a patto che non sia già stata effettuata una registrazione
		// nel contesto, prima di fare lo skip, verrà verificato se è già stata attuata una registrazione
		return !isTracciamentoFileTraceRequestInEnabledBloccante() && !isTracciamentoFileTraceRequestInEnabledNonBloccante();
	}

	
	
	
	// METODI GENERICI
	

	public boolean isTracciamentoEnabled() {
		return this.isTracciamentoDBEnabled() || this.isTracciamentoFileTraceEnabled();
	}
	
	public boolean isTracciamentoRequestInEnabledBloccante() {
		return this.isTracciamentoDBRequestInEnabledBloccante() || this.isTracciamentoFileTraceRequestInEnabledBloccante();
	}
	public boolean isTracciamentoRequestInEnabledNonBloccante() {
		return this.isTracciamentoDBRequestInEnabledNonBloccante() || this.isTracciamentoFileTraceRequestInEnabledNonBloccante();
	}
	@SuppressWarnings("unused")
	private boolean isTracciamentoRequestInEnabled() {
		return isTracciamentoRequestInEnabledBloccante() || isTracciamentoRequestInEnabledNonBloccante();
	}
	
	public boolean isTracciamentoRequestOutEnabledBloccante() {
		return this.isTracciamentoDBRequestOutEnabledBloccante() || this.isTracciamentoFileTraceRequestOutEnabledBloccante();
	}
	public boolean isTracciamentoRequestOutEnabledNonBloccante() {
		return this.isTracciamentoDBRequestOutEnabledNonBloccante() || this.isTracciamentoFileTraceRequestOutEnabledNonBloccante();
	}
	@SuppressWarnings("unused")
	private boolean isTracciamentoRequestOutEnabled() {
		return isTracciamentoRequestOutEnabledBloccante() || isTracciamentoRequestOutEnabledNonBloccante();
	}
	
	public boolean isTracciamentoResponseOutEnabledBloccante() {
		return this.isTracciamentoDBResponseOutEnabledBloccante() || this.isTracciamentoFileTraceResponseOutEnabledBloccante();
	}
	public boolean isTracciamentoResponseOutEnabledNonBloccante() {
		return this.isTracciamentoDBResponseOutEnabledNonBloccante() || this.isTracciamentoFileTraceResponseOutEnabledNonBloccante();
	}
	@SuppressWarnings("unused")
	private boolean isTracciamentoResponseOutEnabled() {
		return isTracciamentoResponseOutEnabledBloccante() || isTracciamentoResponseOutEnabledNonBloccante();
	}
	
	public boolean isTracciamentoResponseOutCompleteEnabled() {
		return this.isTracciamentoDBResponseOutCompleteEnabled() || this.isTracciamentoFileTraceResponseOutCompleteEnabled();
	}
	
	public boolean isFilterEnabled() {
		return this.isFilterDBEnabled() || this.isFilterFileTraceEnabled();
	}
	public String getWarningMessageFilter() {
		boolean dbCase = false;
		boolean filetraceCase = false;
		if(this.isFilterDBEnabled() &&
			(
					this.isTracciamentoDBRequestOutEnabledBloccante() || this.isTracciamentoDBRequestOutEnabledNonBloccante() 
					||
					this.isTracciamentoDBResponseOutEnabledBloccante() || this.isTracciamentoDBResponseOutEnabledNonBloccante()
			) 
		) {
			dbCase = true;
		}
		if(this.isFilterFileTraceEnabled() &&
			(
					this.isTracciamentoFileTraceRequestOutEnabledBloccante() ||  this.isTracciamentoFileTraceRequestOutEnabledNonBloccante()
					|| 
					this.isTracciamentoFileTraceResponseOutEnabledBloccante() || this.isTracciamentoFileTraceResponseOutEnabledNonBloccante()
			) 
		) {
			filetraceCase = true;
		}
		
		String posizione = null;
		if(dbCase && !filetraceCase) {
			posizione = " su database";
		}
		else if(!dbCase && filetraceCase) {
			posizione = " tramite filetrace";
		}
		else if(dbCase 
				//&& filetrace
				) {
			posizione = "";
		}
		
		if(posizione!=null) {
			return "La registrazione"+posizione+", attiva per la fase di consegna di una richiesta e/o di una risposta,<br/>non consente di filtrare esiti relativi ad errori di consegna o di processamento della risposta.";
		}
		
		return null;
	}
	
}

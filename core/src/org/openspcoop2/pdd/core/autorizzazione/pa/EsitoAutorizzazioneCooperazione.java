/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.pdd.core.autorizzazione.pa;

import org.openspcoop2.pdd.core.autorizzazione.EsitoAutorizzazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;

/**
 * Esito di un processo di autorizzazione.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoAutorizzazioneCooperazione extends EsitoAutorizzazione {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** codice di errore */
	private ErroreCooperazione erroreCooperazione;

	public ErroreCooperazione getErroreCooperazione() {
		return this.erroreCooperazione;
	}

	public void setErroreCooperazione(ErroreCooperazione erroreCooperazione) {
		this.erroreCooperazione = erroreCooperazione;
	}

	@Override
	public String getHeader(){
		StringBuffer bf = new StringBuffer(super.getHeader());
		if(this.erroreCooperazione!=null){
			bf.append(" ErroreCooperazione(");
			bf.append(this.erroreCooperazione.getCodiceErrore().name());
			bf.append("):");
			bf.append(this.erroreCooperazione.getDescrizioneRawValue());
		}
		return bf.toString();
	}
	
	

	
}

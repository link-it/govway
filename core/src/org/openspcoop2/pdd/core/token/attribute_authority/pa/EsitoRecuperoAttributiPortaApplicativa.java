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



package org.openspcoop2.pdd.core.token.attribute_authority.pa;

import java.io.Serializable;

import org.openspcoop2.pdd.core.token.attribute_authority.EsitoRecuperoAttributi;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;

/**
 * Esito di un processo di recupero degli attributi.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoRecuperoAttributiPortaApplicativa extends EsitoRecuperoAttributi implements Serializable {

	
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
	
	
	/** codice di errore */
	private ErroreIntegrazione erroreIntegrazione;

	public ErroreIntegrazione getErroreIntegrazione() {
		return this.erroreIntegrazione;
	}

	public void setErroreIntegrazione(ErroreIntegrazione erroreIntegrazione) {
		this.erroreIntegrazione = erroreIntegrazione;
	}
	

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder(super.toString());
		if(this.erroreCooperazione!=null){
			bf.append(" ErroreCooperazione(");
			bf.append(this.erroreCooperazione.getCodiceErrore().name());
			bf.append("):");
			bf.append(this.erroreCooperazione.getDescrizioneRawValue());
		}
		if(this.erroreIntegrazione!=null){
			bf.append(" ErroreIntegrazione(");
			bf.append(this.erroreIntegrazione.getCodiceErrore().name());
			bf.append("):");
			bf.append(this.erroreIntegrazione.getDescrizioneRawValue());
		}
		return bf.toString();
	}
}

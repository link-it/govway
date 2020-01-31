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



package org.openspcoop2.pdd.core.autenticazione.pa;

import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.autenticazione.EsitoAutenticazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;

/**
 * Esito di un processo di autenticazione.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoAutenticazionePortaApplicativa extends EsitoAutenticazione {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/** IDSoggetto */
	private IDSoggetto idSoggetto;
	
	public IDSoggetto getIdSoggetto() {
		return this.idSoggetto;
	}

	public void setIdSoggetto(IDSoggetto idSoggetto) {
		this.idSoggetto = idSoggetto;
	}
	
	/** IDServizioApplicativo */
	private IDServizioApplicativo idServizioApplicativo;
	
	public IDServizioApplicativo getIdServizioApplicativo() {
		return this.idServizioApplicativo;
	}

	public void setIdServizioApplicativo(IDServizioApplicativo idServizioApplicativo) {
		this.idServizioApplicativo = idServizioApplicativo;
	}

	
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
	public String getHeader(){
		StringBuilder bf = new StringBuilder(super.getHeader());
		if(this.idSoggetto!=null){
			bf.append(" IDSoggetto");
			bf.append(this.idSoggetto.toString());
		}
		if(this.idServizioApplicativo!=null){
			bf.append(" IDServizioApplicativo");
			if(this.idServizioApplicativo.getIdSoggettoProprietario()!=null && this.idSoggetto==null){
				bf.append(" IDSoggetto(");
				bf.append(this.idServizioApplicativo.getIdSoggettoProprietario().toString());
				bf.append(")");
			}
			bf.append(":");
			bf.append(this.idServizioApplicativo.getNome());
		}
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

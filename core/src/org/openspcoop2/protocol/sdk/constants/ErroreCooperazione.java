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
package org.openspcoop2.protocol.sdk.constants;

import java.io.Serializable;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ErroreCooperazione implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String descrizione;
	private CodiceErroreCooperazione codiceErrore;
	
	public ErroreCooperazione(String descrizione,CodiceErroreCooperazione codiceErrore){
		this.descrizione = descrizione;
		this.codiceErrore = codiceErrore;
	}
		
	// metodo senza protocol factory utilizzato dai traduttori
	public String getDescrizioneRawValue() {
		return this.descrizione;
	}
	public String getDescrizione(IProtocolFactory protocolFactory) throws ProtocolException {
		return protocolFactory.createTraduttore().toString(this);
	}
	protected void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public CodiceErroreCooperazione getCodiceErrore() {
		return this.codiceErrore;
	}
	protected void setCodiceErrore(CodiceErroreCooperazione codiceErrore) {
		this.codiceErrore = codiceErrore;
	}
	
	@Override
	public String toString(){
		try{
			return toString(null);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	public String toString(IProtocolFactory protocolFactory) throws ProtocolException{
		StringBuffer bf = new StringBuffer();
		
		if(this.descrizione!=null){
			bf.append(" descrizione errore: ");
			if(protocolFactory==null)
				bf.append(this.getDescrizioneRawValue());
			else
				bf.append(this.getDescrizione(protocolFactory));
		}
		
		if(this.codiceErrore!=null){
			bf.append(" codice errore: ");
			if(protocolFactory==null)
				bf.append(this.codiceErrore.toString());
			else
				bf.append(protocolFactory.createTraduttore().toString(this.codiceErrore));
		}
		
		if(bf.length()>0)
			return bf.toString().substring(1);
		else
			return null;
	}
	
	@Override
	public ErroreCooperazione clone(){
		ErroreCooperazione err = 
				new ErroreCooperazione( (this.descrizione!=null ? new String(this.descrizione) : null) , this.codiceErrore);
		return err;
	}
}

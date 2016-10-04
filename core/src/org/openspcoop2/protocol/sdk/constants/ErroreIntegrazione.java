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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.SOAPFaultCode;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ErroreIntegrazione implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String descrizione;
	private CodiceErroreIntegrazione codiceErrore;
	private List<KeyValueObject> keyValueObjects = new ArrayList<KeyValueObject>();
	private final SOAPFaultCode soapFaultCode;
	
	public ErroreIntegrazione(String descrizione, CodiceErroreIntegrazione codiceErrore, KeyValueObject ... keyValueObjects){
		this.descrizione = descrizione;
		this.codiceErrore = codiceErrore;
		if(keyValueObjects!=null){
			for (int i = 0; i < keyValueObjects.length; i++) {
				this.keyValueObjects.add(keyValueObjects[i]);
			}
		}
		this.soapFaultCode = null;
	}
	
	public ErroreIntegrazione(String descrizione, CodiceErroreIntegrazione codiceErrore, SOAPFaultCode soapFaultCode, KeyValueObject ... keyValueObjects){
		this.descrizione = descrizione;
		this.codiceErrore = codiceErrore;
		if(keyValueObjects!=null){
			for (int i = 0; i < keyValueObjects.length; i++) {
				this.keyValueObjects.add(keyValueObjects[i]);
			}
		}
		this.soapFaultCode = soapFaultCode;
	}
	
	// metodo senza protocol factory utilizzato dai traduttori
	public String getDescrizioneRawValue() {
		return replaceAll(this.descrizione);
	}
	public String getDescrizione(IProtocolFactory protocolFactory) throws ProtocolException {
		return replaceAll(protocolFactory.createTraduttore().toString(this));
	}
	protected void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public CodiceErroreIntegrazione getCodiceErrore() {
		return this.codiceErrore;
	}
	protected void setCodiceErrore(CodiceErroreIntegrazione codiceErrore) {
		this.codiceErrore = codiceErrore;
	}

	public SOAPFaultCode getSoapFaultCode() {
		return this.soapFaultCode;
	}

	public List<KeyValueObject> getKeyValueObjects() {
		return this.keyValueObjects;
	}
	protected void addKeyValueObject(KeyValueObject o){
		this.keyValueObjects.add(o);
	}
	
	private String replaceAll(String v){
		if(v==null){
			return null;
		}
		String tmp = new String(v);
		for (int i = 0; i < this.keyValueObjects.size(); i++) {
			KeyValueObject keyValue = this.keyValueObjects.get(i);
			String key = keyValue.getKey();
			int limite = 100;
			int index = 0;
			while(tmp.contains(key) && index<limite){
				tmp = tmp.replace(key, keyValue.getValue());
				index++;
			}
		}
		return tmp;
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
			if(protocolFactory==null){
				bf.append(this.getDescrizioneRawValue());
			}else{
				bf.append(this.getDescrizione(protocolFactory));
			}
		}
		
		if(this.codiceErrore!=null){
			bf.append(" codice errore: ");
			if(protocolFactory==null)
				bf.append(this.codiceErrore.toString());
			else
				bf.append(protocolFactory.createTraduttore().toString(this.codiceErrore,null,false));
		}
		
		if(bf.length()>0)
			return bf.toString().substring(1);
		else
			return null;
	}
}

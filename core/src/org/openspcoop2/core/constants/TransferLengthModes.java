/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.core.constants;

/**
 * Contiene i tipi di transfer length utilizzati dai servizi PD e PA
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum TransferLengthModes {

	TRANSFER_ENCODING_CHUNKED ("transfer-encoding-chunked"),
	CONTENT_LENGTH ("content-length"),
	WEBSERVER_DEFAULT ("webserver-default");
	
	
	private final String nome;

	TransferLengthModes(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return this.nome;
	}
	
	public static TransferLengthModes getTransferLengthModes(String value) throws Exception{
		if(TRANSFER_ENCODING_CHUNKED.toString().equals(value)){
			return TRANSFER_ENCODING_CHUNKED;
		}
		else if(CONTENT_LENGTH.toString().equals(value)){
			return CONTENT_LENGTH;
		}
		else if(WEBSERVER_DEFAULT.toString().equals(value)){
			return WEBSERVER_DEFAULT;
		}
		else{
			throw new Exception("Tipo transfer-length non supportato (valori supportati "+TransferLengthModes.stringValues()+"): "+value);
		}
	}
	
	public static String stringValues(){
		StringBuffer res = new StringBuffer();
		int i=0;
		for (TransferLengthModes tmp : TransferLengthModes.values()) {
			if(i>0)
				res.append(",");
			res.append(tmp.getNome());
			i++;
		}
		return res.toString();
	}
	
	public static String[] toStringArray(){
		String[] res = new String[TransferLengthModes.values().length];
		int i=0;
		for (TransferLengthModes tmp : TransferLengthModes.values()) {
			res[i]=tmp.getNome();
			i++;
		}
		return res;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}
	
	public boolean equals(TransferLengthModes tlm){
		if(tlm!=null){
			return this.toString().equals(tlm.toString());
		}else{
			return false;
		}
	}
}


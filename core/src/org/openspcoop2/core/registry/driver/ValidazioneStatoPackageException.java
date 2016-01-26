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



package org.openspcoop2.core.registry.driver;

import java.util.Vector;


/**
 * Contiene la definizione di una eccezione lanciata dal driver che gestisce il registro dei servizi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ValidazioneStatoPackageException extends Exception {
    
	private Vector<String> errori_validazione = new Vector<String>();
	private String tipoPackage = null;
	private String stato = null;
	
	 public ValidazioneStatoPackageException(String tipoPackage,String stato,String[] message, Throwable cause)
	{
		super(cause);
		
		if(message!=null){
			for(int i=0; i<message.length; i++){
				this.errori_validazione.add(message[i]);
			}
		}
		this.tipoPackage = tipoPackage;
		this.stato = stato;

		// TODO Auto-generated constructor stub
	}
	public ValidazioneStatoPackageException(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public ValidazioneStatoPackageException() {
		super();
    }
	public ValidazioneStatoPackageException(String tipoPackage,String stato,String[] msg) {
		if(msg!=null){
			for(int i=0; i<msg.length; i++){
				this.errori_validazione.add(msg[i]);
			}
		}
		this.tipoPackage = tipoPackage;
		this.stato = stato;
    }
	
	
	@Override
	public String toString(){
		return this.toString(" "," , ");
	}
	public String toString(String firstSeparator, String separator){
		StringBuffer bf = new StringBuffer();
		bf.append("Stato "+this.tipoPackage+" ["+this.stato+"] ");
		bf.append("non utilizzabile");
		bf.append(":");
		bf.append(firstSeparator);
		for(int i=0;i<this.errori_validazione.size();i++){
			if(i>0)
				bf.append(separator);
			bf.append(this.errori_validazione.get(i));
		}
		return bf.toString();
	}
	
	public void addErroreValidazione(String e){
		this.errori_validazione.add(e);
	}
	
	public int sizeErroriValidazione(){
		return this.errori_validazione.size();
	}
}


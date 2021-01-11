/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.registry.driver;

import java.util.ArrayList;
import java.util.List;


/**
 * Contiene la definizione di una eccezione lanciata dal driver che gestisce il registro dei servizi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ValidazioneStatoPackageException extends Exception {
    
	private List<String> errori_validazione = new ArrayList<String>();
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
		StringBuilder bf = new StringBuilder();
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
	
	@Override
	public String getMessage() {
		return this.toString();
	}
	
	public void addErroreValidazione(String e){
		this.errori_validazione.add(e);
	}
	
	public int sizeErroriValidazione(){
		return this.errori_validazione.size();
	}
}


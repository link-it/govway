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




package org.openspcoop2.utils.cache;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * Classe utilizzata per contenere la risposta ottenuta da una query effettuata su di un registro dei servizi.
 * La risposta puo' essere un oggetto serializzabile, memorizzato nella proprieta' <code>object</code> o un valore di tipo boolean,
 * memorizzato nella proprieta' <code>value</code>.
 * Inoltre la risposta ritornata da una query effettuata sul registro puo' contenere anche un oggetto null.
 * La presenza di un oggetto CacheResponse in cache con la proprieta' <code>object</object> impostata a null 
 * indica proprio che esiste una risposta di una query con valore null.  
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CacheResponse implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Object Response. */
	private java.io.Serializable object;
	/** Object Response null */
	private boolean objectNull = false;
	/** Exception Response. */
	private java.io.Serializable exception;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public CacheResponse(){  
	}
	/**
	 * Costruttore. 
	 *
	 * @param o ObjectResponse
	 * 
	 */
	public CacheResponse(java.io.Serializable o){
		this.object = o;
	}






	/* ********  S E T T E R   ******** */

	/**
	 * Imposta la proprieta' <code>object</code> (deve implementare l'interfaccia java.io.Serializable)
	 *
	 * @param o valore della proprieta' <code>object</code>
	 * 
	 */
	public void setObject(java.io.Serializable o){
		this.object = o;
	}
	public void setException(java.io.Serializable exception) {
		this.exception = exception;
	}
	public void setObjectNull(boolean objectNull) {
		this.objectNull = objectNull;
	}



	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna la proprieta' <code>object</code>
	 *
	 * @return valore della proprieta' <code>object</code>
	 * 
	 */
	public java.io.Serializable getObject(){
		return this.object;
	}
	public java.io.Serializable getException() {
		return this.exception;
	}
	public boolean isObjectNull() {
		return this.objectNull;
	}



	
	@Override
	public String toString(){
		if(this.object!=null){
			return "OBJECT: \n"+this.object.toString();
		}
		else if(this.objectNull){
			return "CACHED NULL VALUE";
		}
		else if(this.exception!=null){
			try{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintStream p = new PrintStream(bout);
				((Exception)this.exception).printStackTrace(p);
				p.flush();
				p.close();
				bout.flush();
				bout.close();
				return bout.toString();
			}catch(Exception e){
				return "CACHE EXCEPTION ERROR: "+e.getMessage();
			}
		}
		else{
			return "CACHE OBJECT EMPTY ?";
		}
	}
}

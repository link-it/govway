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


package org.openspcoop2.core.id;


/**
 * Classe utilizzata per rappresentare un identificatore di una Porta Applicativa nella configurazione
 * 
 * </pre>
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDPortaApplicativa implements java.io.Serializable{
	
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** Identificatore del Servizio richiesto */
	private IDServizio servizio;
	
	/**
	 * Imposta l'identificativo del servizio.
	 *
	 * @param idS Identificativo del servizio
	 * 
	 */
	public void setIDServizio(IDServizio idS){
		this.servizio = idS;
	}
	
	/**
	 * Ritorna l'identificativo del servizio all'interno del registro dei servizi.
	 *
	 * @return Identificativo del servizio
	 * 
	 */
	public IDServizio getIDServizio(){
		return this.servizio;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		if(this.servizio!=null)
			bf.append("PA del Servizio:"+this.servizio.toString());
		else
			bf.append("PA:NonDefinita");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(object.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDPortaApplicativa id = (IDPortaApplicativa) object;
		
		if(this.servizio==null){
			if(id.servizio!=null)
				return false;
		}else{
			if(this.servizio.equals(id.servizio)==false)
				return false;
		}
		
		return true;
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDPortaApplicativa clone(){
		IDPortaApplicativa idPA = new IDPortaApplicativa();
		if(this.servizio!=null){
			idPA.servizio = this.servizio.clone();
		}
		return idPA;
	}
}

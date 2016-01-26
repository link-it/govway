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


package org.openspcoop2.core.id;


/**
 * Classe utilizzata per rappresentare un identificatore di una Porta Delegata nella configurazione
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDPortaDelegata implements java.io.Serializable{

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** Identificatore del Soggetto che sta' richiedendo il servizio */
	private IDSoggetto soggettoFruitore;
	/** Location della Porta Delegata Richiesta */
	private String locationPD;

	/**
	 * Imposta l'identificatore del Soggetto che sta' richiedendo il servizio
	 *
	 * @param idSoggetto Identificatore del Soggetto.
	 * 
	 */
	public void setSoggettoFruitore(IDSoggetto idSoggetto){
		this.soggettoFruitore = idSoggetto;
	}
	/**
	 * Imposta la Location della Porta Delegata Richiesta
	 *
	 * @param aLocation Location della Porta Delegata.
	 * 
	 */
	public void setLocationPD(String aLocation){
		this.locationPD = aLocation;
	}
	
	/**
	 * Ritorna l'identificatore del Soggetto che sta' richiedendo il servizio
	 *
	 * @return Identificatore del Soggetto.
	 * 
	 */
	public IDSoggetto getSoggettoFruitore(){
		return this.soggettoFruitore;
	}
	/**
	 * Ritorna la Location della Porta Delegata Richiesta
	 *
	 * @return Location della Porta Delegata.
	 * 
	 */
	public String getLocationPD(){
		return this.locationPD;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		if(this.locationPD!=null)
			bf.append("PD:"+this.locationPD);
		else
			bf.append("PD:NonDefinita");
		bf.append(" ");
		if(this.soggettoFruitore!=null)
			bf.append("SoggettoFruitore:"+this.soggettoFruitore.toString());
		else
			bf.append("SoggettoFruitore:NonDefinito");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(object.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDPortaDelegata id = (IDPortaDelegata) object;
		
		if(this.locationPD==null){
			if(id.locationPD!=null)
				return false;
		}else{
			if(this.locationPD.equals(id.locationPD)==false)
				return false;
		}

		if(this.soggettoFruitore==null){
			if(id.soggettoFruitore!=null)
				return false;
		}else{
			if(this.soggettoFruitore.equals(id.soggettoFruitore)==false)
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
	public IDPortaDelegata clone(){
		IDPortaDelegata idPD = new IDPortaDelegata();
		if(this.locationPD!=null){
			idPD.locationPD = new String(this.locationPD);
		}
		if(this.soggettoFruitore!=null){
			idPD.soggettoFruitore = this.soggettoFruitore.clone();
		}
		return idPD;
	}
}

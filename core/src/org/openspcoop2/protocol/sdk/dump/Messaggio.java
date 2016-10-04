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


package org.openspcoop2.protocol.sdk.dump;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.openspcoop2.core.constants.TipoMessaggio;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;

/**
 * Messaggio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Messaggio implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4718160136521047108L;
	
	private OpenSPCoop2Message msg;
	private TipoMessaggio tipoMessaggio;
	private Date gdo;
	private IDSoggetto dominio;
	private String idFunzione;
	private String idBusta;
	private IDSoggetto fruitore;
	private IDServizio servizio;
	private java.util.Properties transportHeader;
	private String location;
	private TipoPdD tipoPdD;
	private long id;
	private Hashtable<String, String> properties = new Hashtable<String, String>();
	private String protocollo;
	
	public OpenSPCoop2Message getMsg() {
		return this.msg;
	}
	public void setMsg(OpenSPCoop2Message msg) {
		this.msg = msg;
	}
	public TipoMessaggio getTipoMessaggio() {
		return this.tipoMessaggio;
	}
	public void setTipoMessaggio(TipoMessaggio tipoMessaggio) {
		this.tipoMessaggio = tipoMessaggio;
	}
	public Date getGdo() {
		return this.gdo;
	}
	public void setGdo(Date gdo) {
		this.gdo = gdo;
	}
	public IDSoggetto getIdPorta() {
		return this.dominio;
	}
	public void setIdPorta(IDSoggetto idPorta) {
		this.dominio = idPorta;
	}
	public String getIdFunzione() {
		return this.idFunzione;
	}
	public void setIdFunzione(String idFunzione) {
		this.idFunzione = idFunzione;
	}
	public String getIdBusta() {
		return this.idBusta;
	}
	public void setIdBusta(String idBusta) {
		this.idBusta = idBusta;
	}
	public IDSoggetto getFruitore() {
		return this.fruitore;
	}
	public void setFruitore(IDSoggetto fruitore) {
		this.fruitore = fruitore;
	}
	public IDServizio getServizio() {
		return this.servizio;
	}
	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}
	public java.util.Properties getTransportHeader() {
		return this.transportHeader;
	}
	public void setTransportHeader(java.util.Properties transportHeader) {
		this.transportHeader = transportHeader;
	}
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public void addProperty(String key,String value){
    	this.properties.put(key,value);
    }
    public int sizeProperties(){
    	return this.properties.size();
    }
    public String getProperty(String key){
    	return this.properties.get(key);
    }
    public String removeProperty(String key){
    	return this.properties.remove(key);
    }
    public String[] getPropertiesValues() {
    	String[] array = new String[1];
    	if(this.properties.size()>0){
    		return this.properties.values().toArray(array);
    	}else{
    		return null;
    	}
    }
    public String[] getPropertiesNames() {
    	Vector<String> nomi = new Vector<String>();
    	Enumeration<String> en = this.properties.keys();
    	while(en.hasMoreElements()){
    		nomi.add(en.nextElement());
    	}	    	
    	String[] array = new String[1];
    	if(nomi.size()>0){
    		return nomi.toArray(array);
    	}else{
    		return null;
    	}
    }
    public void setProperties(Hashtable<String, String> params) {
    	this.properties = params;
    }
	public String getLocation() {
		return this.location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}
	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
}

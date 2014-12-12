/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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



package org.openspcoop2.protocol.sdk;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.utils.jaxb.DateTime2Date;


/**
 * Classe utilizzata per rappresentare un Riscontro.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */

/**
 * <p>Java class for riscontro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="riscontro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oraRegistrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="tipoOraRegistrazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "riscontro", propOrder = {
    "id",
    "oraRegistrazione",
    "tipoOraRegistrazione"
})

public class Riscontro implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "ID")
    protected String id;
	
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(DateTime2Date .class)
    @XmlSchemaType(name = "dateTime")
    protected Date oraRegistrazione;
    
    @XmlTransient
    private TipoOraRegistrazione tipoOraRegistrazione;
    @XmlElement(name="tipoOraRegistrazione")
    protected String tipoOraRegistrazioneValue;
    
    
    /* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public Riscontro(){}





	/* ********  S E T T E R   ******** */

	/**
	 * Imposta Identificatore della busta  da riscontrare.
	 *
	 * @param id identificatore della busta  da riscontrare.
	 * 
	 */
	public void setID(String id ){
		this.id = id;
	}
	/**
	 * Imposta la Data di registrazione, in formato ,  del messaggio.
	 *
	 * @param data Data di registrazione del messaggio.
	 * 
	 */
	public void setOraRegistrazione(Date data){
		this.oraRegistrazione = data;
	}
	
	/**
	 * Imposta il tipo della Data di registrazione, in formato ,  del messaggio.
	 *
	 * @param tipo Tipo della Data di registrazione del messaggio.
	 * 
	 */
	public void setTipoOraRegistrazione(TipoOraRegistrazione tipo){
		this.tipoOraRegistrazione = tipo;
	} 





	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna l'identificatore della busta  riscontrata.
	 *
	 * @return identificatore della busta  riscontrata.
	 * 
	 */
	public String getID(){
		return this.id;
	}
	/**
	 * Ritorna la Data di registrazione, in formato ,  del messaggio.
	 *
	 * @return Data di registrazione del messaggio.
	 * 
	 */

	public Date getOraRegistrazione(){
		return this.oraRegistrazione;
	}
	/**
	 * Ritorna il tipo della Data di registrazione, in formato ,  del messaggio.
	 *
	 * @return Tipo della Data di registrazione del messaggio.
	 * 
	 */
	public TipoOraRegistrazione getTipoOraRegistrazione(){
		return this.tipoOraRegistrazione;
	}

	public String getTipoOraRegistrazioneValue(IProtocolFactory protocolFactory) throws ProtocolException {
		return this.tipoOraRegistrazioneValue == null ? protocolFactory.createTraduttore().toString(this.tipoOraRegistrazione) : this.tipoOraRegistrazioneValue;
	}

	public void setTipoOraRegistrazioneValue(String tipoOraRegistrazioneValue) {
		this.tipoOraRegistrazioneValue = tipoOraRegistrazioneValue;
	} 

	@Override
	public Riscontro clone(){
		
		Riscontro clone = new Riscontro();
		
		clone.setID(this.id!=null ? new String(this.id) : null);
		
		clone.setOraRegistrazione(this.oraRegistrazione!=null ? new Date(this.oraRegistrazione.getTime()) : null);
		
		clone.setTipoOraRegistrazione(this.tipoOraRegistrazione);
		clone.setTipoOraRegistrazioneValue(this.tipoOraRegistrazioneValue!=null ? new String(this.tipoOraRegistrazioneValue) : null);
		
		return clone;
	}
}






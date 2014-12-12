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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Oggetto contenente informazioni per la ricerca di loggedEntry
 * 
 * <p>Java class for FilterSearch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FilterSearch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="correlatiOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="correlazioneApplicativa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dataFine" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dataInizio" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="delegata" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="busta" type="{http://logger.dto.openspcoop.org}Busta"/>
 *         &lt;element name="filtroSoggetti" type="{http://ws.monitor.openspcoop.org}ArrayOf_tns2_IDSoggetto"/>
 *         &lt;element name="filtroSoggetto" type="{http://commons.dao.openspcoop.org}IDSoggetto" maxOccurs="unbounded"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="idBusta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="idFunzione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificativoPorta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="nomePorta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="offsetMap" maxOccurs="unbounded" nillable="true" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="partial" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="pdd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="properties">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="servizioApplicativo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="severita" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterSearch", propOrder = {
    "correlatiOnly",
    "correlazioneApplicativa",
    "dataFine",
    "dataInizio",
    "delegata",
    "busta",
    "filtroSoggetti",
    "id",
    "idBusta",
    "idFunzione",
    "identificativoPorta",
    "limit",
    "nomePorta",
    "offset",
    "offsetMap",
    "partial",
    "pdd",
    "properties",
    "servizioApplicativo",
    "severita"
})

public class FiltroRicercaDiagnosticiConPaginazione extends FiltroRicercaDiagnostici implements Serializable{


	private static final long serialVersionUID = 2103096411857601491L;
    
    protected int limit;
    protected int offset;
    protected boolean asc = true;

	@XmlElement(required = true, nillable = true)
    protected long[] offsetMap = {0,0,0};
    protected boolean partial;
    
    public FiltroRicercaDiagnosticiConPaginazione() {
    	super();
	}
    
  
	
	public boolean isPartial() {
		return this.partial;
	}
	
    public void setPartial(boolean partial) {
		this.partial = partial;
	}
    
    /**
     * Mappa per gli offset
     * In caso di search complessa (union) mi serve questa mappa per gestire gli offset
     * offsetmap[0] = offset correlazione
     * offsetmap[1] = offset diagnostici
     * offsetmap[2] = offset notexist
     */
    public long[] getOffsetMap() {
		return this.offsetMap;
	}
    /**
     * Mappa per gli offset
     * In caso di search complessa (union) mi serve questa mappa per gestire gli offset
     * offsetmap[0] = offset correlazione
     * offsetmap[1] = offset diagnostici
     * offsetmap[2] = offset notexist
     */
    public void setOffsetMap(long[] offsetMap) {
		this.offsetMap = offsetMap;
	}
    
	public int getOffset() {
		return this.offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLimit() {
		return this.limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
    public boolean isAsc() {
		return this.asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}
	
	@Override
	public String toString() {
				
		String pattern=
				" offset [{0}]" +
				" limit  [{1}]" +
				" asc  [{2}]";
				
		return super.toString() + " " + MessageFormat.format(pattern,
				this.offset,
				this.limit,
				this.asc
				);
	}

 
}



/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for dump-allegato complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dump-allegato"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="allegato" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="header" type="{http://www.openspcoop2.org/core/transazioni}dump-header-allegato" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="dump-timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="header-ext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dump-allegato", 
  propOrder = {
  	"contentType",
  	"contentId",
  	"contentLocation",
  	"allegato",
  	"header",
  	"dumpTimestamp",
  	"headerExt"
  }
)

@XmlRootElement(name = "dump-allegato")

public class DumpAllegato extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public DumpAllegato() {
    super();
  }

  public java.lang.String getContentType() {
    return this.contentType;
  }

  public void setContentType(java.lang.String contentType) {
    this.contentType = contentType;
  }

  public java.lang.String getContentId() {
    return this.contentId;
  }

  public void setContentId(java.lang.String contentId) {
    this.contentId = contentId;
  }

  public java.lang.String getContentLocation() {
    return this.contentLocation;
  }

  public void setContentLocation(java.lang.String contentLocation) {
    this.contentLocation = contentLocation;
  }

  public byte[] getAllegato() {
    return this.allegato;
  }

  public void setAllegato(byte[] allegato) {
    this.allegato = allegato;
  }

  public void addHeader(DumpHeaderAllegato header) {
    this.header.add(header);
  }

  public DumpHeaderAllegato getHeader(int index) {
    return this.header.get( index );
  }

  public DumpHeaderAllegato removeHeader(int index) {
    return this.header.remove( index );
  }

  public List<DumpHeaderAllegato> getHeaderList() {
    return this.header;
  }

  public void setHeaderList(List<DumpHeaderAllegato> header) {
    this.header=header;
  }

  public int sizeHeaderList() {
    return this.header.size();
  }

  public java.util.Date getDumpTimestamp() {
    return this.dumpTimestamp;
  }

  public void setDumpTimestamp(java.util.Date dumpTimestamp) {
    this.dumpTimestamp = dumpTimestamp;
  }

  public java.lang.String getHeaderExt() {
    return this.headerExt;
  }

  public void setHeaderExt(java.lang.String headerExt) {
    this.headerExt = headerExt;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-type",required=false,nillable=false)
  protected java.lang.String contentType;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-id",required=false,nillable=false)
  protected java.lang.String contentId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-location",required=false,nillable=false)
  protected java.lang.String contentLocation;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.HexBinaryAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="hexBinary")
  @XmlElement(type=String.class, name="allegato",required=false,nillable=false)
  protected byte[] allegato;

  @XmlElement(name="header",required=true,nillable=false)
  private List<DumpHeaderAllegato> header = new ArrayList<>();

  /**
   * Use method getHeaderList
   * @return List&lt;DumpHeaderAllegato&gt;
  */
  public List<DumpHeaderAllegato> getHeader() {
  	return this.getHeaderList();
  }

  /**
   * Use method setHeaderList
   * @param header List&lt;DumpHeaderAllegato&gt;
  */
  public void setHeader(List<DumpHeaderAllegato> header) {
  	this.setHeaderList(header);
  }

  /**
   * Use method sizeHeaderList
   * @return lunghezza della lista
  */
  public int sizeHeader() {
  	return this.sizeHeaderList();
  }

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="dump-timestamp",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dumpTimestamp;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="header-ext",required=false,nillable=false)
  protected java.lang.String headerExt;

}

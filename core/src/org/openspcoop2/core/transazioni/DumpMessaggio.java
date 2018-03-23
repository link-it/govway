/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for dump-messaggio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dump-messaggio">
 * 		&lt;sequence>
 * 			&lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tipo-messaggio" type="{http://www.openspcoop2.org/core/transazioni}tipo-messaggio" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="body" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="allegato" type="{http://www.openspcoop2.org/core/transazioni}dump-allegato" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="contenuto" type="{http://www.openspcoop2.org/core/transazioni}dump-contenuto" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="header-trasporto" type="{http://www.openspcoop2.org/core/transazioni}dump-header-trasporto" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="dump-timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="post-process-content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="post-process-header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="post-process-filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="post-process-content" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="post-process-config-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="post-process-timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="post-processed" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1" default="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dump-messaggio", 
  propOrder = {
  	"idTransazione",
  	"tipoMessaggio",
  	"body",
  	"allegato",
  	"contenuto",
  	"headerTrasporto",
  	"dumpTimestamp",
  	"postProcessContentType",
  	"postProcessHeader",
  	"postProcessFilename",
  	"postProcessContent",
  	"postProcessConfigId",
  	"postProcessTimestamp",
  	"postProcessed"
  }
)

@XmlRootElement(name = "dump-messaggio")

public class DumpMessaggio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DumpMessaggio() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public java.lang.String getIdTransazione() {
    return this.idTransazione;
  }

  public void setIdTransazione(java.lang.String idTransazione) {
    this.idTransazione = idTransazione;
  }

  public void set_value_tipoMessaggio(String value) {
    this.tipoMessaggio = (TipoMessaggio) TipoMessaggio.toEnumConstantFromString(value);
  }

  public String get_value_tipoMessaggio() {
    if(this.tipoMessaggio == null){
    	return null;
    }else{
    	return this.tipoMessaggio.toString();
    }
  }

  public org.openspcoop2.core.transazioni.constants.TipoMessaggio getTipoMessaggio() {
    return this.tipoMessaggio;
  }

  public void setTipoMessaggio(org.openspcoop2.core.transazioni.constants.TipoMessaggio tipoMessaggio) {
    this.tipoMessaggio = tipoMessaggio;
  }

  public byte[] getBody() {
    return this.body;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public void addAllegato(DumpAllegato allegato) {
    this.allegato.add(allegato);
  }

  public DumpAllegato getAllegato(int index) {
    return this.allegato.get( index );
  }

  public DumpAllegato removeAllegato(int index) {
    return this.allegato.remove( index );
  }

  public List<DumpAllegato> getAllegatoList() {
    return this.allegato;
  }

  public void setAllegatoList(List<DumpAllegato> allegato) {
    this.allegato=allegato;
  }

  public int sizeAllegatoList() {
    return this.allegato.size();
  }

  public void addContenuto(DumpContenuto contenuto) {
    this.contenuto.add(contenuto);
  }

  public DumpContenuto getContenuto(int index) {
    return this.contenuto.get( index );
  }

  public DumpContenuto removeContenuto(int index) {
    return this.contenuto.remove( index );
  }

  public List<DumpContenuto> getContenutoList() {
    return this.contenuto;
  }

  public void setContenutoList(List<DumpContenuto> contenuto) {
    this.contenuto=contenuto;
  }

  public int sizeContenutoList() {
    return this.contenuto.size();
  }

  public void addHeaderTrasporto(DumpHeaderTrasporto headerTrasporto) {
    this.headerTrasporto.add(headerTrasporto);
  }

  public DumpHeaderTrasporto getHeaderTrasporto(int index) {
    return this.headerTrasporto.get( index );
  }

  public DumpHeaderTrasporto removeHeaderTrasporto(int index) {
    return this.headerTrasporto.remove( index );
  }

  public List<DumpHeaderTrasporto> getHeaderTrasportoList() {
    return this.headerTrasporto;
  }

  public void setHeaderTrasportoList(List<DumpHeaderTrasporto> headerTrasporto) {
    this.headerTrasporto=headerTrasporto;
  }

  public int sizeHeaderTrasportoList() {
    return this.headerTrasporto.size();
  }

  public java.util.Date getDumpTimestamp() {
    return this.dumpTimestamp;
  }

  public void setDumpTimestamp(java.util.Date dumpTimestamp) {
    this.dumpTimestamp = dumpTimestamp;
  }

  public java.lang.String getPostProcessContentType() {
    return this.postProcessContentType;
  }

  public void setPostProcessContentType(java.lang.String postProcessContentType) {
    this.postProcessContentType = postProcessContentType;
  }

  public java.lang.String getPostProcessHeader() {
    return this.postProcessHeader;
  }

  public void setPostProcessHeader(java.lang.String postProcessHeader) {
    this.postProcessHeader = postProcessHeader;
  }

  public java.lang.String getPostProcessFilename() {
    return this.postProcessFilename;
  }

  public void setPostProcessFilename(java.lang.String postProcessFilename) {
    this.postProcessFilename = postProcessFilename;
  }

  public byte[] getPostProcessContent() {
    return this.postProcessContent;
  }

  public void setPostProcessContent(byte[] postProcessContent) {
    this.postProcessContent = postProcessContent;
  }

  public java.lang.String getPostProcessConfigId() {
    return this.postProcessConfigId;
  }

  public void setPostProcessConfigId(java.lang.String postProcessConfigId) {
    this.postProcessConfigId = postProcessConfigId;
  }

  public java.util.Date getPostProcessTimestamp() {
    return this.postProcessTimestamp;
  }

  public void setPostProcessTimestamp(java.util.Date postProcessTimestamp) {
    this.postProcessTimestamp = postProcessTimestamp;
  }

  public int getPostProcessed() {
    return this.postProcessed;
  }

  public void setPostProcessed(int postProcessed) {
    this.postProcessed = postProcessed;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.transazioni.model.DumpMessaggioModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.transazioni.DumpMessaggio.modelStaticInstance==null){
  			org.openspcoop2.core.transazioni.DumpMessaggio.modelStaticInstance = new org.openspcoop2.core.transazioni.model.DumpMessaggioModel();
	  }
  }
  public static org.openspcoop2.core.transazioni.model.DumpMessaggioModel model(){
	  if(org.openspcoop2.core.transazioni.DumpMessaggio.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.transazioni.DumpMessaggio.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=true,nillable=false)
  protected java.lang.String idTransazione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoMessaggio;

  @XmlElement(name="tipo-messaggio",required=true,nillable=false)
  protected TipoMessaggio tipoMessaggio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.HexBinaryAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="hexBinary")
  @XmlElement(type=String.class, name="body",required=false,nillable=false)
  protected byte[] body;

  @XmlElement(name="allegato",required=true,nillable=false)
  protected List<DumpAllegato> allegato = new ArrayList<DumpAllegato>();

  /**
   * @deprecated Use method getAllegatoList
   * @return List<DumpAllegato>
  */
  @Deprecated
  public List<DumpAllegato> getAllegato() {
  	return this.allegato;
  }

  /**
   * @deprecated Use method setAllegatoList
   * @param allegato List<DumpAllegato>
  */
  @Deprecated
  public void setAllegato(List<DumpAllegato> allegato) {
  	this.allegato=allegato;
  }

  /**
   * @deprecated Use method sizeAllegatoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAllegato() {
  	return this.allegato.size();
  }

  @XmlElement(name="contenuto",required=true,nillable=false)
  protected List<DumpContenuto> contenuto = new ArrayList<DumpContenuto>();

  /**
   * @deprecated Use method getContenutoList
   * @return List<DumpContenuto>
  */
  @Deprecated
  public List<DumpContenuto> getContenuto() {
  	return this.contenuto;
  }

  /**
   * @deprecated Use method setContenutoList
   * @param contenuto List<DumpContenuto>
  */
  @Deprecated
  public void setContenuto(List<DumpContenuto> contenuto) {
  	this.contenuto=contenuto;
  }

  /**
   * @deprecated Use method sizeContenutoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeContenuto() {
  	return this.contenuto.size();
  }

  @XmlElement(name="header-trasporto",required=true,nillable=false)
  protected List<DumpHeaderTrasporto> headerTrasporto = new ArrayList<DumpHeaderTrasporto>();

  /**
   * @deprecated Use method getHeaderTrasportoList
   * @return List<DumpHeaderTrasporto>
  */
  @Deprecated
  public List<DumpHeaderTrasporto> getHeaderTrasporto() {
  	return this.headerTrasporto;
  }

  /**
   * @deprecated Use method setHeaderTrasportoList
   * @param headerTrasporto List<DumpHeaderTrasporto>
  */
  @Deprecated
  public void setHeaderTrasporto(List<DumpHeaderTrasporto> headerTrasporto) {
  	this.headerTrasporto=headerTrasporto;
  }

  /**
   * @deprecated Use method sizeHeaderTrasportoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeHeaderTrasporto() {
  	return this.headerTrasporto.size();
  }

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="dump-timestamp",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dumpTimestamp;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="post-process-content-type",required=false,nillable=false)
  protected java.lang.String postProcessContentType;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="post-process-header",required=false,nillable=false)
  protected java.lang.String postProcessHeader;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="post-process-filename",required=false,nillable=false)
  protected java.lang.String postProcessFilename;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.HexBinaryAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="hexBinary")
  @XmlElement(type=String.class, name="post-process-content",required=false,nillable=false)
  protected byte[] postProcessContent;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="post-process-config-id",required=false,nillable=false)
  protected java.lang.String postProcessConfigId;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="post-process-timestamp",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date postProcessTimestamp;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="post-processed",required=false,nillable=false,defaultValue="1")
  protected int postProcessed = 1;

}

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for dump-messaggio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dump-messaggio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="data-consegna-erogatore" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-messaggio" type="{http://www.openspcoop2.org/core/transazioni}tipo-messaggio" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="formato-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-length" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="multipart-content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="multipart-content-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="multipart-content-location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="multipart-header" type="{http://www.openspcoop2.org/core/transazioni}dump-multipart-header" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="body" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="header-trasporto" type="{http://www.openspcoop2.org/core/transazioni}dump-header-trasporto" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="allegato" type="{http://www.openspcoop2.org/core/transazioni}dump-allegato" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="contenuto" type="{http://www.openspcoop2.org/core/transazioni}dump-contenuto" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="dump-timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="post-process-header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="post-process-filename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="post-process-content" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="post-process-config-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="post-process-timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="post-processed" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1" default="1"/&gt;
 * 			&lt;element name="multipart-header-ext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "dump-messaggio", 
  propOrder = {
  	"idTransazione",
  	"protocollo",
  	"servizioApplicativoErogatore",
  	"dataConsegnaErogatore",
  	"tipoMessaggio",
  	"formatoMessaggio",
  	"contentType",
  	"contentLength",
  	"multipartContentType",
  	"multipartContentId",
  	"multipartContentLocation",
  	"multipartHeader",
  	"body",
  	"headerTrasporto",
  	"allegato",
  	"contenuto",
  	"dumpTimestamp",
  	"postProcessHeader",
  	"postProcessFilename",
  	"postProcessContent",
  	"postProcessConfigId",
  	"postProcessTimestamp",
  	"postProcessed",
  	"multipartHeaderExt",
  	"headerExt"
  }
)

@XmlRootElement(name = "dump-messaggio")

public class DumpMessaggio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public DumpMessaggio() {
    super();
  }

  public java.lang.String getIdTransazione() {
    return this.idTransazione;
  }

  public void setIdTransazione(java.lang.String idTransazione) {
    this.idTransazione = idTransazione;
  }

  public java.lang.String getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(java.lang.String protocollo) {
    this.protocollo = protocollo;
  }

  public java.lang.String getServizioApplicativoErogatore() {
    return this.servizioApplicativoErogatore;
  }

  public void setServizioApplicativoErogatore(java.lang.String servizioApplicativoErogatore) {
    this.servizioApplicativoErogatore = servizioApplicativoErogatore;
  }

  public java.util.Date getDataConsegnaErogatore() {
    return this.dataConsegnaErogatore;
  }

  public void setDataConsegnaErogatore(java.util.Date dataConsegnaErogatore) {
    this.dataConsegnaErogatore = dataConsegnaErogatore;
  }

  public void setTipoMessaggioRawEnumValue(String value) {
    this.tipoMessaggio = (TipoMessaggio) TipoMessaggio.toEnumConstantFromString(value);
  }

  public String getTipoMessaggioRawEnumValue() {
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

  public java.lang.String getFormatoMessaggio() {
    return this.formatoMessaggio;
  }

  public void setFormatoMessaggio(java.lang.String formatoMessaggio) {
    this.formatoMessaggio = formatoMessaggio;
  }

  public java.lang.String getContentType() {
    return this.contentType;
  }

  public void setContentType(java.lang.String contentType) {
    this.contentType = contentType;
  }

  public java.lang.Long getContentLength() {
    return this.contentLength;
  }

  public void setContentLength(java.lang.Long contentLength) {
    this.contentLength = contentLength;
  }

  public java.lang.String getMultipartContentType() {
    return this.multipartContentType;
  }

  public void setMultipartContentType(java.lang.String multipartContentType) {
    this.multipartContentType = multipartContentType;
  }

  public java.lang.String getMultipartContentId() {
    return this.multipartContentId;
  }

  public void setMultipartContentId(java.lang.String multipartContentId) {
    this.multipartContentId = multipartContentId;
  }

  public java.lang.String getMultipartContentLocation() {
    return this.multipartContentLocation;
  }

  public void setMultipartContentLocation(java.lang.String multipartContentLocation) {
    this.multipartContentLocation = multipartContentLocation;
  }

  public void addMultipartHeader(DumpMultipartHeader multipartHeader) {
    this.multipartHeader.add(multipartHeader);
  }

  public DumpMultipartHeader getMultipartHeader(int index) {
    return this.multipartHeader.get( index );
  }

  public DumpMultipartHeader removeMultipartHeader(int index) {
    return this.multipartHeader.remove( index );
  }

  public List<DumpMultipartHeader> getMultipartHeaderList() {
    return this.multipartHeader;
  }

  public void setMultipartHeaderList(List<DumpMultipartHeader> multipartHeader) {
    this.multipartHeader=multipartHeader;
  }

  public int sizeMultipartHeaderList() {
    return this.multipartHeader.size();
  }

  public byte[] getBody() {
    return this.body;
  }

  public void setBody(byte[] body) {
    this.body = body;
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

  public java.util.Date getDumpTimestamp() {
    return this.dumpTimestamp;
  }

  public void setDumpTimestamp(java.util.Date dumpTimestamp) {
    this.dumpTimestamp = dumpTimestamp;
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

  public java.lang.String getMultipartHeaderExt() {
    return this.multipartHeaderExt;
  }

  public void setMultipartHeaderExt(java.lang.String multipartHeaderExt) {
    this.multipartHeaderExt = multipartHeaderExt;
  }

  public java.lang.String getHeaderExt() {
    return this.headerExt;
  }

  public void setHeaderExt(java.lang.String headerExt) {
    this.headerExt = headerExt;
  }

  private static final long serialVersionUID = 1L;

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


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=true,nillable=false)
  protected java.lang.String idTransazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocollo",required=true,nillable=false)
  protected java.lang.String protocollo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-erogatore",required=false,nillable=false)
  protected java.lang.String servizioApplicativoErogatore;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-consegna-erogatore",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataConsegnaErogatore;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoMessaggioRawEnumValue;

  @XmlElement(name="tipo-messaggio",required=true,nillable=false)
  protected TipoMessaggio tipoMessaggio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="formato-messaggio",required=false,nillable=false)
  protected java.lang.String formatoMessaggio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-type",required=false,nillable=false)
  protected java.lang.String contentType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="content-length",required=false,nillable=false)
  protected java.lang.Long contentLength;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="multipart-content-type",required=false,nillable=false)
  protected java.lang.String multipartContentType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="multipart-content-id",required=false,nillable=false)
  protected java.lang.String multipartContentId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="multipart-content-location",required=false,nillable=false)
  protected java.lang.String multipartContentLocation;

  @XmlElement(name="multipart-header",required=true,nillable=false)
  private List<DumpMultipartHeader> multipartHeader = new ArrayList<>();

  /**
   * Use method getMultipartHeaderList
   * @return List&lt;DumpMultipartHeader&gt;
  */
  public List<DumpMultipartHeader> getMultipartHeader() {
  	return this.getMultipartHeaderList();
  }

  /**
   * Use method setMultipartHeaderList
   * @param multipartHeader List&lt;DumpMultipartHeader&gt;
  */
  public void setMultipartHeader(List<DumpMultipartHeader> multipartHeader) {
  	this.setMultipartHeaderList(multipartHeader);
  }

  /**
   * Use method sizeMultipartHeaderList
   * @return lunghezza della lista
  */
  public int sizeMultipartHeader() {
  	return this.sizeMultipartHeaderList();
  }

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.HexBinaryAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="hexBinary")
  @XmlElement(type=String.class, name="body",required=false,nillable=false)
  protected byte[] body;

  @XmlElement(name="header-trasporto",required=true,nillable=false)
  private List<DumpHeaderTrasporto> headerTrasporto = new ArrayList<>();

  /**
   * Use method getHeaderTrasportoList
   * @return List&lt;DumpHeaderTrasporto&gt;
  */
  public List<DumpHeaderTrasporto> getHeaderTrasporto() {
  	return this.getHeaderTrasportoList();
  }

  /**
   * Use method setHeaderTrasportoList
   * @param headerTrasporto List&lt;DumpHeaderTrasporto&gt;
  */
  public void setHeaderTrasporto(List<DumpHeaderTrasporto> headerTrasporto) {
  	this.setHeaderTrasportoList(headerTrasporto);
  }

  /**
   * Use method sizeHeaderTrasportoList
   * @return lunghezza della lista
  */
  public int sizeHeaderTrasporto() {
  	return this.sizeHeaderTrasportoList();
  }

  @XmlElement(name="allegato",required=true,nillable=false)
  private List<DumpAllegato> allegato = new ArrayList<>();

  /**
   * Use method getAllegatoList
   * @return List&lt;DumpAllegato&gt;
  */
  public List<DumpAllegato> getAllegato() {
  	return this.getAllegatoList();
  }

  /**
   * Use method setAllegatoList
   * @param allegato List&lt;DumpAllegato&gt;
  */
  public void setAllegato(List<DumpAllegato> allegato) {
  	this.setAllegatoList(allegato);
  }

  /**
   * Use method sizeAllegatoList
   * @return lunghezza della lista
  */
  public int sizeAllegato() {
  	return this.sizeAllegatoList();
  }

  @XmlElement(name="contenuto",required=true,nillable=false)
  private List<DumpContenuto> contenuto = new ArrayList<>();

  /**
   * Use method getContenutoList
   * @return List&lt;DumpContenuto&gt;
  */
  public List<DumpContenuto> getContenuto() {
  	return this.getContenutoList();
  }

  /**
   * Use method setContenutoList
   * @param contenuto List&lt;DumpContenuto&gt;
  */
  public void setContenuto(List<DumpContenuto> contenuto) {
  	this.setContenutoList(contenuto);
  }

  /**
   * Use method sizeContenutoList
   * @return lunghezza della lista
  */
  public int sizeContenuto() {
  	return this.sizeContenutoList();
  }

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="dump-timestamp",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dumpTimestamp;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="post-process-header",required=false,nillable=false)
  protected java.lang.String postProcessHeader;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="post-process-filename",required=false,nillable=false)
  protected java.lang.String postProcessFilename;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.HexBinaryAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="hexBinary")
  @XmlElement(type=String.class, name="post-process-content",required=false,nillable=false)
  protected byte[] postProcessContent;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="post-process-config-id",required=false,nillable=false)
  protected java.lang.String postProcessConfigId;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="post-process-timestamp",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date postProcessTimestamp;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="post-processed",required=false,nillable=false,defaultValue="1")
  protected int postProcessed = 1;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="multipart-header-ext",required=false,nillable=false)
  protected java.lang.String multipartHeaderExt;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="header-ext",required=false,nillable=false)
  protected java.lang.String headerExt;

}

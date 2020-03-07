/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for trasformazione-regola-richiesta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazione-regola-richiesta"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="conversione-template" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="header" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-parametro" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="parametro-url" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-parametro" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="trasformazione-rest" type="{http://www.openspcoop2.org/core/config}trasformazione-rest" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="trasformazione-soap" type="{http://www.openspcoop2.org/core/config}trasformazione-soap" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="conversione" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="conversione-tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trasformazione-regola-richiesta", 
  propOrder = {
  	"conversioneTemplate",
  	"header",
  	"parametroUrl",
  	"trasformazioneRest",
  	"trasformazioneSoap"
  }
)

@XmlRootElement(name = "trasformazione-regola-richiesta")

public class TrasformazioneRegolaRichiesta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TrasformazioneRegolaRichiesta() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public byte[] getConversioneTemplate() {
    return this.conversioneTemplate;
  }

  public void setConversioneTemplate(byte[] conversioneTemplate) {
    this.conversioneTemplate = conversioneTemplate;
  }

  public void addHeader(TrasformazioneRegolaParametro header) {
    this.header.add(header);
  }

  public TrasformazioneRegolaParametro getHeader(int index) {
    return this.header.get( index );
  }

  public TrasformazioneRegolaParametro removeHeader(int index) {
    return this.header.remove( index );
  }

  public List<TrasformazioneRegolaParametro> getHeaderList() {
    return this.header;
  }

  public void setHeaderList(List<TrasformazioneRegolaParametro> header) {
    this.header=header;
  }

  public int sizeHeaderList() {
    return this.header.size();
  }

  public void addParametroUrl(TrasformazioneRegolaParametro parametroUrl) {
    this.parametroUrl.add(parametroUrl);
  }

  public TrasformazioneRegolaParametro getParametroUrl(int index) {
    return this.parametroUrl.get( index );
  }

  public TrasformazioneRegolaParametro removeParametroUrl(int index) {
    return this.parametroUrl.remove( index );
  }

  public List<TrasformazioneRegolaParametro> getParametroUrlList() {
    return this.parametroUrl;
  }

  public void setParametroUrlList(List<TrasformazioneRegolaParametro> parametroUrl) {
    this.parametroUrl=parametroUrl;
  }

  public int sizeParametroUrlList() {
    return this.parametroUrl.size();
  }

  public TrasformazioneRest getTrasformazioneRest() {
    return this.trasformazioneRest;
  }

  public void setTrasformazioneRest(TrasformazioneRest trasformazioneRest) {
    this.trasformazioneRest = trasformazioneRest;
  }

  public TrasformazioneSoap getTrasformazioneSoap() {
    return this.trasformazioneSoap;
  }

  public void setTrasformazioneSoap(TrasformazioneSoap trasformazioneSoap) {
    this.trasformazioneSoap = trasformazioneSoap;
  }

  public boolean isConversione() {
    return this.conversione;
  }

  public boolean getConversione() {
    return this.conversione;
  }

  public void setConversione(boolean conversione) {
    this.conversione = conversione;
  }

  public java.lang.String getConversioneTipo() {
    return this.conversioneTipo;
  }

  public void setConversioneTipo(java.lang.String conversioneTipo) {
    this.conversioneTipo = conversioneTipo;
  }

  public java.lang.String getContentType() {
    return this.contentType;
  }

  public void setContentType(java.lang.String contentType) {
    this.contentType = contentType;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="conversione-template",required=false,nillable=false)
  protected byte[] conversioneTemplate;

  @XmlElement(name="header",required=true,nillable=false)
  protected List<TrasformazioneRegolaParametro> header = new ArrayList<TrasformazioneRegolaParametro>();

  /**
   * @deprecated Use method getHeaderList
   * @return List&lt;TrasformazioneRegolaParametro&gt;
  */
  @Deprecated
  public List<TrasformazioneRegolaParametro> getHeader() {
  	return this.header;
  }

  /**
   * @deprecated Use method setHeaderList
   * @param header List&lt;TrasformazioneRegolaParametro&gt;
  */
  @Deprecated
  public void setHeader(List<TrasformazioneRegolaParametro> header) {
  	this.header=header;
  }

  /**
   * @deprecated Use method sizeHeaderList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeHeader() {
  	return this.header.size();
  }

  @XmlElement(name="parametro-url",required=true,nillable=false)
  protected List<TrasformazioneRegolaParametro> parametroUrl = new ArrayList<TrasformazioneRegolaParametro>();

  /**
   * @deprecated Use method getParametroUrlList
   * @return List&lt;TrasformazioneRegolaParametro&gt;
  */
  @Deprecated
  public List<TrasformazioneRegolaParametro> getParametroUrl() {
  	return this.parametroUrl;
  }

  /**
   * @deprecated Use method setParametroUrlList
   * @param parametroUrl List&lt;TrasformazioneRegolaParametro&gt;
  */
  @Deprecated
  public void setParametroUrl(List<TrasformazioneRegolaParametro> parametroUrl) {
  	this.parametroUrl=parametroUrl;
  }

  /**
   * @deprecated Use method sizeParametroUrlList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeParametroUrl() {
  	return this.parametroUrl.size();
  }

  @XmlElement(name="trasformazione-rest",required=false,nillable=false)
  protected TrasformazioneRest trasformazioneRest;

  @XmlElement(name="trasformazione-soap",required=false,nillable=false)
  protected TrasformazioneSoap trasformazioneSoap;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="conversione",required=false)
  protected boolean conversione = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="conversione-tipo",required=false)
  protected java.lang.String conversioneTipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="content-type",required=false)
  protected java.lang.String contentType;

}

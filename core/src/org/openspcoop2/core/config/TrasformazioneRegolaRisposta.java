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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for trasformazione-regola-risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazione-regola-risposta"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="conversione-template" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="applicabilita" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-applicabilita-risposta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="header" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-parametro" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="trasformazione-soap" type="{http://www.openspcoop2.org/core/config}trasformazione-soap-risposta" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="posizione" type="{http://www.w3.org/2001/XMLSchema}int" use="required"/&gt;
 * 		&lt;attribute name="conversione" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="conversione-tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="return-code" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trasformazione-regola-risposta", 
  propOrder = {
  	"conversioneTemplate",
  	"applicabilita",
  	"header",
  	"trasformazioneSoap"
  }
)

@XmlRootElement(name = "trasformazione-regola-risposta")

public class TrasformazioneRegolaRisposta extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TrasformazioneRegolaRisposta() {
    super();
  }

  public byte[] getConversioneTemplate() {
    return this.conversioneTemplate;
  }

  public void setConversioneTemplate(byte[] conversioneTemplate) {
    this.conversioneTemplate = conversioneTemplate;
  }

  public TrasformazioneRegolaApplicabilitaRisposta getApplicabilita() {
    return this.applicabilita;
  }

  public void setApplicabilita(TrasformazioneRegolaApplicabilitaRisposta applicabilita) {
    this.applicabilita = applicabilita;
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

  public TrasformazioneSoapRisposta getTrasformazioneSoap() {
    return this.trasformazioneSoap;
  }

  public void setTrasformazioneSoap(TrasformazioneSoapRisposta trasformazioneSoap) {
    this.trasformazioneSoap = trasformazioneSoap;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public int getPosizione() {
    return this.posizione;
  }

  public void setPosizione(int posizione) {
    this.posizione = posizione;
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

  public java.lang.String getReturnCode() {
    return this.returnCode;
  }

  public void setReturnCode(java.lang.String returnCode) {
    this.returnCode = returnCode;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="conversione-template",required=false,nillable=false)
  protected byte[] conversioneTemplate;

  @XmlElement(name="applicabilita",required=false,nillable=false)
  protected TrasformazioneRegolaApplicabilitaRisposta applicabilita;

  @XmlElement(name="header",required=true,nillable=false)
  private List<TrasformazioneRegolaParametro> header = new ArrayList<>();

  /**
   * Use method getHeaderList
   * @return List&lt;TrasformazioneRegolaParametro&gt;
  */
  public List<TrasformazioneRegolaParametro> getHeader() {
  	return this.getHeaderList();
  }

  /**
   * Use method setHeaderList
   * @param header List&lt;TrasformazioneRegolaParametro&gt;
  */
  public void setHeader(List<TrasformazioneRegolaParametro> header) {
  	this.setHeaderList(header);
  }

  /**
   * Use method sizeHeaderList
   * @return lunghezza della lista
  */
  public int sizeHeader() {
  	return this.sizeHeaderList();
  }

  @XmlElement(name="trasformazione-soap",required=false,nillable=false)
  protected TrasformazioneSoapRisposta trasformazioneSoap;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="posizione",required=true)
  protected int posizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="conversione",required=false)
  protected boolean conversione = false;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="conversione-tipo",required=false)
  protected java.lang.String conversioneTipo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="content-type",required=false)
  protected java.lang.String contentType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="return-code",required=false)
  protected java.lang.String returnCode;

}

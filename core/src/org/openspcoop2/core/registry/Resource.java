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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for resource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="request" type="{http://www.openspcoop2.org/core/registry}resource-request" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="response" type="{http://www.openspcoop2.org/core/registry}resource-response" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/registry}protocol-property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="prof-azione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="id-accordo" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="method" type="{http://www.openspcoop2.org/core/registry}HttpMethod" use="optional"/&gt;
 * 		&lt;attribute name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/&gt;
 * 		&lt;attribute name="request-message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/&gt;
 * 		&lt;attribute name="response-message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/&gt;
 * 		&lt;attribute name="filtro-duplicati" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="conferma-ricezione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="id-collaborazione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="id-riferimento-richiesta" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="consegna-in-ordine" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="scadenza" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource", 
  propOrder = {
  	"request",
  	"response",
  	"protocolProperty"
  }
)

@XmlRootElement(name = "resource")

public class Resource extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Resource() {
    super();
  }

  public ResourceRequest getRequest() {
    return this.request;
  }

  public void setRequest(ResourceRequest request) {
    this.request = request;
  }

  public void addResponse(ResourceResponse response) {
    this.response.add(response);
  }

  public ResourceResponse getResponse(int index) {
    return this.response.get( index );
  }

  public ResourceResponse removeResponse(int index) {
    return this.response.remove( index );
  }

  public List<ResourceResponse> getResponseList() {
    return this.response;
  }

  public void setResponseList(List<ResourceResponse> response) {
    this.response=response;
  }

  public int sizeResponseList() {
    return this.response.size();
  }

  public void addProtocolProperty(ProtocolProperty protocolProperty) {
    this.protocolProperty.add(protocolProperty);
  }

  public ProtocolProperty getProtocolProperty(int index) {
    return this.protocolProperty.get( index );
  }

  public ProtocolProperty removeProtocolProperty(int index) {
    return this.protocolProperty.remove( index );
  }

  public List<ProtocolProperty> getProtocolPropertyList() {
    return this.protocolProperty;
  }

  public void setProtocolPropertyList(List<ProtocolProperty> protocolProperty) {
    this.protocolProperty=protocolProperty;
  }

  public int sizeProtocolPropertyList() {
    return this.protocolProperty.size();
  }

  public java.lang.String getProfAzione() {
    return this.profAzione;
  }

  public void setProfAzione(java.lang.String profAzione) {
    this.profAzione = profAzione;
  }

  public java.lang.Long getIdAccordo() {
    return this.idAccordo;
  }

  public void setIdAccordo(java.lang.Long idAccordo) {
    this.idAccordo = idAccordo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getPath() {
    return this.path;
  }

  public void setPath(java.lang.String path) {
    this.path = path;
  }

  public void setMethodRawEnumValue(String value) {
    this.method = (HttpMethod) HttpMethod.toEnumConstantFromString(value);
  }

  public String getMethodRawEnumValue() {
    if(this.method == null){
    	return null;
    }else{
    	return this.method.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.HttpMethod getMethod() {
    return this.method;
  }

  public void setMethod(org.openspcoop2.core.registry.constants.HttpMethod method) {
    this.method = method;
  }

  public void setMessageTypeRawEnumValue(String value) {
    this.messageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String getMessageTypeRawEnumValue() {
    if(this.messageType == null){
    	return null;
    }else{
    	return this.messageType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.MessageType getMessageType() {
    return this.messageType;
  }

  public void setMessageType(org.openspcoop2.core.registry.constants.MessageType messageType) {
    this.messageType = messageType;
  }

  public void setRequestMessageTypeRawEnumValue(String value) {
    this.requestMessageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String getRequestMessageTypeRawEnumValue() {
    if(this.requestMessageType == null){
    	return null;
    }else{
    	return this.requestMessageType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.MessageType getRequestMessageType() {
    return this.requestMessageType;
  }

  public void setRequestMessageType(org.openspcoop2.core.registry.constants.MessageType requestMessageType) {
    this.requestMessageType = requestMessageType;
  }

  public void setResponseMessageTypeRawEnumValue(String value) {
    this.responseMessageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String getResponseMessageTypeRawEnumValue() {
    if(this.responseMessageType == null){
    	return null;
    }else{
    	return this.responseMessageType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.MessageType getResponseMessageType() {
    return this.responseMessageType;
  }

  public void setResponseMessageType(org.openspcoop2.core.registry.constants.MessageType responseMessageType) {
    this.responseMessageType = responseMessageType;
  }

  public void setFiltroDuplicatiRawEnumValue(String value) {
    this.filtroDuplicati = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getFiltroDuplicatiRawEnumValue() {
    if(this.filtroDuplicati == null){
    	return null;
    }else{
    	return this.filtroDuplicati.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getFiltroDuplicati() {
    return this.filtroDuplicati;
  }

  public void setFiltroDuplicati(org.openspcoop2.core.registry.constants.StatoFunzionalita filtroDuplicati) {
    this.filtroDuplicati = filtroDuplicati;
  }

  public void setConfermaRicezioneRawEnumValue(String value) {
    this.confermaRicezione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getConfermaRicezioneRawEnumValue() {
    if(this.confermaRicezione == null){
    	return null;
    }else{
    	return this.confermaRicezione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getConfermaRicezione() {
    return this.confermaRicezione;
  }

  public void setConfermaRicezione(org.openspcoop2.core.registry.constants.StatoFunzionalita confermaRicezione) {
    this.confermaRicezione = confermaRicezione;
  }

  public void setIdCollaborazioneRawEnumValue(String value) {
    this.idCollaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getIdCollaborazioneRawEnumValue() {
    if(this.idCollaborazione == null){
    	return null;
    }else{
    	return this.idCollaborazione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getIdCollaborazione() {
    return this.idCollaborazione;
  }

  public void setIdCollaborazione(org.openspcoop2.core.registry.constants.StatoFunzionalita idCollaborazione) {
    this.idCollaborazione = idCollaborazione;
  }

  public void setIdRiferimentoRichiestaRawEnumValue(String value) {
    this.idRiferimentoRichiesta = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getIdRiferimentoRichiestaRawEnumValue() {
    if(this.idRiferimentoRichiesta == null){
    	return null;
    }else{
    	return this.idRiferimentoRichiesta.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getIdRiferimentoRichiesta() {
    return this.idRiferimentoRichiesta;
  }

  public void setIdRiferimentoRichiesta(org.openspcoop2.core.registry.constants.StatoFunzionalita idRiferimentoRichiesta) {
    this.idRiferimentoRichiesta = idRiferimentoRichiesta;
  }

  public void setConsegnaInOrdineRawEnumValue(String value) {
    this.consegnaInOrdine = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getConsegnaInOrdineRawEnumValue() {
    if(this.consegnaInOrdine == null){
    	return null;
    }else{
    	return this.consegnaInOrdine.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getConsegnaInOrdine() {
    return this.consegnaInOrdine;
  }

  public void setConsegnaInOrdine(org.openspcoop2.core.registry.constants.StatoFunzionalita consegnaInOrdine) {
    this.consegnaInOrdine = consegnaInOrdine;
  }

  public java.lang.String getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(java.lang.String scadenza) {
    this.scadenza = scadenza;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="request",required=false,nillable=false)
  protected ResourceRequest request;

  @XmlElement(name="response",required=true,nillable=false)
  private List<ResourceResponse> response = new ArrayList<>();

  /**
   * Use method getResponseList
   * @return List&lt;ResourceResponse&gt;
  */
  public List<ResourceResponse> getResponse() {
  	return this.getResponseList();
  }

  /**
   * Use method setResponseList
   * @param response List&lt;ResourceResponse&gt;
  */
  public void setResponse(List<ResourceResponse> response) {
  	this.setResponseList(response);
  }

  /**
   * Use method sizeResponseList
   * @return lunghezza della lista
  */
  public int sizeResponse() {
  	return this.sizeResponseList();
  }

  @XmlElement(name="protocol-property",required=true,nillable=false)
  private List<ProtocolProperty> protocolProperty = new ArrayList<>();

  /**
   * Use method getProtocolPropertyList
   * @return List&lt;ProtocolProperty&gt;
  */
  public List<ProtocolProperty> getProtocolProperty() {
  	return this.getProtocolPropertyList();
  }

  /**
   * Use method setProtocolPropertyList
   * @param protocolProperty List&lt;ProtocolProperty&gt;
  */
  public void setProtocolProperty(List<ProtocolProperty> protocolProperty) {
  	this.setProtocolPropertyList(protocolProperty);
  }

  /**
   * Use method sizeProtocolPropertyList
   * @return lunghezza della lista
  */
  public int sizeProtocolProperty() {
  	return this.sizeProtocolPropertyList();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="prof-azione",required=false)
  protected java.lang.String profAzione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idAccordo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="path",required=false)
  protected java.lang.String path;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String methodRawEnumValue;

  @XmlAttribute(name="method",required=false)
  protected HttpMethod method;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String messageTypeRawEnumValue;

  @XmlAttribute(name="message-type",required=false)
  protected MessageType messageType;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String requestMessageTypeRawEnumValue;

  @XmlAttribute(name="request-message-type",required=false)
  protected MessageType requestMessageType;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String responseMessageTypeRawEnumValue;

  @XmlAttribute(name="response-message-type",required=false)
  protected MessageType responseMessageType;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String filtroDuplicatiRawEnumValue;

  @XmlAttribute(name="filtro-duplicati",required=false)
  protected StatoFunzionalita filtroDuplicati;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String confermaRicezioneRawEnumValue;

  @XmlAttribute(name="conferma-ricezione",required=false)
  protected StatoFunzionalita confermaRicezione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String idCollaborazioneRawEnumValue;

  @XmlAttribute(name="id-collaborazione",required=false)
  protected StatoFunzionalita idCollaborazione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String idRiferimentoRichiestaRawEnumValue;

  @XmlAttribute(name="id-riferimento-richiesta",required=false)
  protected StatoFunzionalita idRiferimentoRichiesta;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String consegnaInOrdineRawEnumValue;

  @XmlAttribute(name="consegna-in-ordine",required=false)
  protected StatoFunzionalita consegnaInOrdine;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="scadenza",required=false)
  protected java.lang.String scadenza;

}

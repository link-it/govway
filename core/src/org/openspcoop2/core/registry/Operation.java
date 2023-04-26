/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for operation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operation"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="message-input" type="{http://www.openspcoop2.org/core/registry}message" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="message-output" type="{http://www.openspcoop2.org/core/registry}message" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/registry}protocol-property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="prof-azione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="id-port-type" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="style" type="{http://www.openspcoop2.org/core/registry}BindingStyle" use="optional"/&gt;
 * 		&lt;attribute name="soap-action" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="profilo-collaborazione" type="{http://www.openspcoop2.org/core/registry}ProfiloCollaborazione" use="optional"/&gt;
 * 		&lt;attribute name="filtro-duplicati" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="conferma-ricezione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="id-collaborazione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="id-riferimento-richiesta" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="consegna-in-ordine" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="scadenza" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="correlata-servizio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="correlata" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operation", 
  propOrder = {
  	"messageInput",
  	"messageOutput",
  	"protocolProperty"
  }
)

@XmlRootElement(name = "operation")

public class Operation extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Operation() {
    super();
  }

  public Message getMessageInput() {
    return this.messageInput;
  }

  public void setMessageInput(Message messageInput) {
    this.messageInput = messageInput;
  }

  public Message getMessageOutput() {
    return this.messageOutput;
  }

  public void setMessageOutput(Message messageOutput) {
    this.messageOutput = messageOutput;
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

  public java.lang.Long getIdPortType() {
    return this.idPortType;
  }

  public void setIdPortType(java.lang.Long idPortType) {
    this.idPortType = idPortType;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void setStyleRawEnumValue(String value) {
    this.style = (BindingStyle) BindingStyle.toEnumConstantFromString(value);
  }

  public String getStyleRawEnumValue() {
    if(this.style == null){
    	return null;
    }else{
    	return this.style.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.BindingStyle getStyle() {
    return this.style;
  }

  public void setStyle(org.openspcoop2.core.registry.constants.BindingStyle style) {
    this.style = style;
  }

  public java.lang.String getSoapAction() {
    return this.soapAction;
  }

  public void setSoapAction(java.lang.String soapAction) {
    this.soapAction = soapAction;
  }

  public void setProfiloCollaborazioneRawEnumValue(String value) {
    this.profiloCollaborazione = (ProfiloCollaborazione) ProfiloCollaborazione.toEnumConstantFromString(value);
  }

  public String getProfiloCollaborazioneRawEnumValue() {
    if(this.profiloCollaborazione == null){
    	return null;
    }else{
    	return this.profiloCollaborazione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.ProfiloCollaborazione getProfiloCollaborazione() {
    return this.profiloCollaborazione;
  }

  public void setProfiloCollaborazione(org.openspcoop2.core.registry.constants.ProfiloCollaborazione profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
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

  public java.lang.String getCorrelataServizio() {
    return this.correlataServizio;
  }

  public void setCorrelataServizio(java.lang.String correlataServizio) {
    this.correlataServizio = correlataServizio;
  }

  public java.lang.String getCorrelata() {
    return this.correlata;
  }

  public void setCorrelata(java.lang.String correlata) {
    this.correlata = correlata;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="message-input",required=false,nillable=false)
  protected Message messageInput;

  @XmlElement(name="message-output",required=false,nillable=false)
  protected Message messageOutput;

  @XmlElement(name="protocol-property",required=true,nillable=false)
  private List<ProtocolProperty> protocolProperty = new ArrayList<>();

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="prof-azione",required=false)
  protected java.lang.String profAzione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idPortType;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String styleRawEnumValue;

  @XmlAttribute(name="style",required=false)
  protected BindingStyle style;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="soap-action",required=false)
  protected java.lang.String soapAction;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String profiloCollaborazioneRawEnumValue;

  @XmlAttribute(name="profilo-collaborazione",required=false)
  protected ProfiloCollaborazione profiloCollaborazione;

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

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="correlata-servizio",required=false)
  protected java.lang.String correlataServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="correlata",required=false)
  protected java.lang.String correlata;

}

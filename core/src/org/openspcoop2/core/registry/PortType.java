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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for port-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="port-type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="azione" type="{http://www.openspcoop2.org/core/registry}operation" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/registry}protocol-property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="profilo-p-t" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="id-accordo" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="style" type="{http://www.openspcoop2.org/core/registry}BindingStyle" use="optional" default="document"/&gt;
 * 		&lt;attribute name="profilo-collaborazione" type="{http://www.openspcoop2.org/core/registry}ProfiloCollaborazione" use="optional"/&gt;
 * 		&lt;attribute name="filtro-duplicati" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="conferma-ricezione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="id-collaborazione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="id-riferimento-richiesta" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="consegna-in-ordine" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="scadenza" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "port-type", 
  propOrder = {
  	"azione",
  	"protocolProperty"
  }
)

@XmlRootElement(name = "port-type")

public class PortType extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortType() {
    super();
  }

  public void addAzione(Operation azione) {
    this.azione.add(azione);
  }

  public Operation getAzione(int index) {
    return this.azione.get( index );
  }

  public Operation removeAzione(int index) {
    return this.azione.remove( index );
  }

  public List<Operation> getAzioneList() {
    return this.azione;
  }

  public void setAzioneList(List<Operation> azione) {
    this.azione=azione;
  }

  public int sizeAzioneList() {
    return this.azione.size();
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

  public java.lang.String getProfiloPT() {
    return this.profiloPT;
  }

  public void setProfiloPT(java.lang.String profiloPT) {
    this.profiloPT = profiloPT;
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

  private static final long serialVersionUID = 1L;



  @XmlElement(name="azione",required=true,nillable=false)
  private List<Operation> azione = new ArrayList<>();

  /**
   * Use method getAzioneList
   * @return List&lt;Operation&gt;
  */
  public List<Operation> getAzione() {
  	return this.getAzioneList();
  }

  /**
   * Use method setAzioneList
   * @param azione List&lt;Operation&gt;
  */
  public void setAzione(List<Operation> azione) {
  	this.setAzioneList(azione);
  }

  /**
   * Use method sizeAzioneList
   * @return lunghezza della lista
  */
  public int sizeAzione() {
  	return this.sizeAzioneList();
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

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="profilo-p-t",required=false)
  protected java.lang.String profiloPT;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.Long idAccordo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String styleRawEnumValue;

  @XmlAttribute(name="style",required=false)
  protected BindingStyle style = (BindingStyle) BindingStyle.toEnumConstantFromString("document");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String profiloCollaborazioneRawEnumValue;

  @XmlAttribute(name="profilo-collaborazione",required=false)
  protected ProfiloCollaborazione profiloCollaborazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String filtroDuplicatiRawEnumValue;

  @XmlAttribute(name="filtro-duplicati",required=false)
  protected StatoFunzionalita filtroDuplicati;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String confermaRicezioneRawEnumValue;

  @XmlAttribute(name="conferma-ricezione",required=false)
  protected StatoFunzionalita confermaRicezione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String idCollaborazioneRawEnumValue;

  @XmlAttribute(name="id-collaborazione",required=false)
  protected StatoFunzionalita idCollaborazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String idRiferimentoRichiestaRawEnumValue;

  @XmlAttribute(name="id-riferimento-richiesta",required=false)
  protected StatoFunzionalita idRiferimentoRichiesta;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String consegnaInOrdineRawEnumValue;

  @XmlAttribute(name="consegna-in-ordine",required=false)
  protected StatoFunzionalita consegnaInOrdine;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="scadenza",required=false)
  protected java.lang.String scadenza;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String messageTypeRawEnumValue;

  @XmlAttribute(name="message-type",required=false)
  protected MessageType messageType;

}

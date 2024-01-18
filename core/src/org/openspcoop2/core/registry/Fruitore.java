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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for fruitore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fruitore"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="configurazione-azione" type="{http://www.openspcoop2.org/core/registry}configurazione-servizio-azione" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/registry}protocol-property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta-oggetto" type="{http://www.openspcoop2.org/core/registry}proprieta-oggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="byte-wsdl-implementativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/&gt;
 * 		&lt;attribute name="byte-wsdl-implementativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/&gt;
 * 		&lt;attribute name="id-soggetto" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="id-servizio" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="wsdl-implementativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="wsdl-implementativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fruitore", 
  propOrder = {
  	"servizioApplicativo",
  	"connettore",
  	"configurazioneAzione",
  	"protocolProperty",
  	"proprietaOggetto"
  }
)

@XmlRootElement(name = "fruitore")

public class Fruitore extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Fruitore() {
    super();
  }

  public void addServizioApplicativo(java.lang.String servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public java.lang.String getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public java.lang.String removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<java.lang.String> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<java.lang.String> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public void addConfigurazioneAzione(ConfigurazioneServizioAzione configurazioneAzione) {
    this.configurazioneAzione.add(configurazioneAzione);
  }

  public ConfigurazioneServizioAzione getConfigurazioneAzione(int index) {
    return this.configurazioneAzione.get( index );
  }

  public ConfigurazioneServizioAzione removeConfigurazioneAzione(int index) {
    return this.configurazioneAzione.remove( index );
  }

  public List<ConfigurazioneServizioAzione> getConfigurazioneAzioneList() {
    return this.configurazioneAzione;
  }

  public void setConfigurazioneAzioneList(List<ConfigurazioneServizioAzione> configurazioneAzione) {
    this.configurazioneAzione=configurazioneAzione;
  }

  public int sizeConfigurazioneAzioneList() {
    return this.configurazioneAzione.size();
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

  public ProprietaOggetto getProprietaOggetto() {
    return this.proprietaOggetto;
  }

  public void setProprietaOggetto(ProprietaOggetto proprietaOggetto) {
    this.proprietaOggetto = proprietaOggetto;
  }

  public java.lang.String getStatoPackage() {
    return this.statoPackage;
  }

  public void setStatoPackage(java.lang.String statoPackage) {
    this.statoPackage = statoPackage;
  }

  public byte[] getByteWsdlImplementativoErogatore() {
    return this.byteWsdlImplementativoErogatore;
  }

  public void setByteWsdlImplementativoErogatore(byte[] byteWsdlImplementativoErogatore) {
    this.byteWsdlImplementativoErogatore = byteWsdlImplementativoErogatore;
  }

  public byte[] getByteWsdlImplementativoFruitore() {
    return this.byteWsdlImplementativoFruitore;
  }

  public void setByteWsdlImplementativoFruitore(byte[] byteWsdlImplementativoFruitore) {
    this.byteWsdlImplementativoFruitore = byteWsdlImplementativoFruitore;
  }

  public java.lang.Long getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(java.lang.Long idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public java.lang.Long getIdServizio() {
    return this.idServizio;
  }

  public void setIdServizio(java.lang.Long idServizio) {
    this.idServizio = idServizio;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getWsdlImplementativoErogatore() {
    return this.wsdlImplementativoErogatore;
  }

  public void setWsdlImplementativoErogatore(java.lang.String wsdlImplementativoErogatore) {
    this.wsdlImplementativoErogatore = wsdlImplementativoErogatore;
  }

  public java.lang.String getWsdlImplementativoFruitore() {
    return this.wsdlImplementativoFruitore;
  }

  public void setWsdlImplementativoFruitore(java.lang.String wsdlImplementativoFruitore) {
    this.wsdlImplementativoFruitore = wsdlImplementativoFruitore;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  private List<java.lang.String> servizioApplicativo = new ArrayList<>();

  /**
   * Use method getServizioApplicativoList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getServizioApplicativo() {
  	return this.getServizioApplicativoList();
  }

  /**
   * Use method setServizioApplicativoList
   * @param servizioApplicativo List&lt;java.lang.String&gt;
  */
  public void setServizioApplicativo(List<java.lang.String> servizioApplicativo) {
  	this.setServizioApplicativoList(servizioApplicativo);
  }

  /**
   * Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  public int sizeServizioApplicativo() {
  	return this.sizeServizioApplicativoList();
  }

  @XmlElement(name="connettore",required=false,nillable=false)
  protected Connettore connettore;

  @XmlElement(name="configurazione-azione",required=true,nillable=false)
  private List<ConfigurazioneServizioAzione> configurazioneAzione = new ArrayList<>();

  /**
   * Use method getConfigurazioneAzioneList
   * @return List&lt;ConfigurazioneServizioAzione&gt;
  */
  public List<ConfigurazioneServizioAzione> getConfigurazioneAzione() {
  	return this.getConfigurazioneAzioneList();
  }

  /**
   * Use method setConfigurazioneAzioneList
   * @param configurazioneAzione List&lt;ConfigurazioneServizioAzione&gt;
  */
  public void setConfigurazioneAzione(List<ConfigurazioneServizioAzione> configurazioneAzione) {
  	this.setConfigurazioneAzioneList(configurazioneAzione);
  }

  /**
   * Use method sizeConfigurazioneAzioneList
   * @return lunghezza della lista
  */
  public int sizeConfigurazioneAzione() {
  	return this.sizeConfigurazioneAzioneList();
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

  @XmlElement(name="proprieta-oggetto",required=false,nillable=false)
  protected ProprietaOggetto proprietaOggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="stato-package",required=false)
  protected java.lang.String statoPackage;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-implementativo-erogatore",required=false)
  protected byte[] byteWsdlImplementativoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-implementativo-fruitore",required=false)
  protected byte[] byteWsdlImplementativoFruitore;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idSoggetto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-implementativo-erogatore",required=false)
  protected java.lang.String wsdlImplementativoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-implementativo-fruitore",required=false)
  protected java.lang.String wsdlImplementativoFruitore;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

}

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
package it.gov.fatturapa.sdi.messaggi.v1_0;

import it.gov.fatturapa.sdi.messaggi.v1_0.constants.ScartoType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for ScartoEsitoCommittente_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScartoEsitoCommittente_Type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="RiferimentoFattura" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}RiferimentoFattura_Type" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Scarto" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}Scarto_Type" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="MessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="MessageIdCommittente" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="PecMessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="versione" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScartoEsitoCommittente_Type", 
  propOrder = {
  	"identificativoSdI",
  	"riferimentoFattura",
  	"scarto",
  	"messageId",
  	"messageIdCommittente",
  	"pecMessageId",
  	"note"
  }
)

@XmlRootElement(name = "ScartoEsitoCommittente_Type")

public class ScartoEsitoCommittenteType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ScartoEsitoCommittenteType() {
    super();
  }

  public java.lang.String getIdentificativoSdI() {
    return this.identificativoSdI;
  }

  public void setIdentificativoSdI(java.lang.String identificativoSdI) {
    this.identificativoSdI = identificativoSdI;
  }

  public RiferimentoFatturaType getRiferimentoFattura() {
    return this.riferimentoFattura;
  }

  public void setRiferimentoFattura(RiferimentoFatturaType riferimentoFattura) {
    this.riferimentoFattura = riferimentoFattura;
  }

  public void setScartoRawEnumValue(String value) {
    this.scarto = (ScartoType) ScartoType.toEnumConstantFromString(value);
  }

  public String getScartoRawEnumValue() {
    if(this.scarto == null){
    	return null;
    }else{
    	return this.scarto.toString();
    }
  }

  public it.gov.fatturapa.sdi.messaggi.v1_0.constants.ScartoType getScarto() {
    return this.scarto;
  }

  public void setScarto(it.gov.fatturapa.sdi.messaggi.v1_0.constants.ScartoType scarto) {
    this.scarto = scarto;
  }

  public java.lang.String getMessageId() {
    return this.messageId;
  }

  public void setMessageId(java.lang.String messageId) {
    this.messageId = messageId;
  }

  public java.lang.String getMessageIdCommittente() {
    return this.messageIdCommittente;
  }

  public void setMessageIdCommittente(java.lang.String messageIdCommittente) {
    this.messageIdCommittente = messageIdCommittente;
  }

  public java.lang.String getPecMessageId() {
    return this.pecMessageId;
  }

  public void setPecMessageId(java.lang.String pecMessageId) {
    this.pecMessageId = pecMessageId;
  }

  public java.lang.String getNote() {
    return this.note;
  }

  public void setNote(java.lang.String note) {
    this.note = note;
  }

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.fatturapa.sdi.messaggi.v1_0.model.ScartoEsitoCommittenteTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType.modelStaticInstance = new it.gov.fatturapa.sdi.messaggi.v1_0.model.ScartoEsitoCommittenteTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.messaggi.v1_0.model.ScartoEsitoCommittenteTypeModel model(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IdentificativoSdI",required=true,nillable=false)
  protected java.lang.String identificativoSdI;

  @XmlElement(name="RiferimentoFattura",required=false,nillable=false)
  protected RiferimentoFatturaType riferimentoFattura;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String scartoRawEnumValue;

  @XmlElement(name="Scarto",required=true,nillable=false)
  protected ScartoType scarto;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="MessageId",required=true,nillable=false)
  protected java.lang.String messageId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="MessageIdCommittente",required=false,nillable=false)
  protected java.lang.String messageIdCommittente;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="PecMessageId",required=false,nillable=false)
  protected java.lang.String pecMessageId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Note",required=false,nillable=false)
  protected java.lang.String note;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=true)
  protected java.lang.String versione;

}

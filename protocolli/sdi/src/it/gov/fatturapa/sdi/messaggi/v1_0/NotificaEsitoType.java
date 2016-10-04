/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for NotificaEsito_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotificaEsito_Type">
 * 		&lt;sequence>
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}integer" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NomeFile" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="EsitoCommittente" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}NotificaEsitoCommittente_Type" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="MessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="PecMessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="versione" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" use="required"/>
 * 		&lt;attribute name="IntermediarioConDupliceRuolo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificaEsito_Type", 
  propOrder = {
  	"_decimalWrapper_identificativoSdI",
  	"nomeFile",
  	"esitoCommittente",
  	"messageId",
  	"pecMessageId",
  	"note"
  }
)

@XmlRootElement(name = "NotificaEsito_Type")

public class NotificaEsitoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public NotificaEsitoType() {
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

  public java.lang.Integer getIdentificativoSdI() {
    if(this._decimalWrapper_identificativoSdI!=null){
		return (java.lang.Integer) this._decimalWrapper_identificativoSdI.getObject(java.lang.Integer.class);
	}else{
		return this.identificativoSdI;
	}
  }

  public void setIdentificativoSdI(java.lang.Integer identificativoSdI) {
    if(identificativoSdI!=null){
		this._decimalWrapper_identificativoSdI = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,12,identificativoSdI);
	}
  }

  public java.lang.String getNomeFile() {
    return this.nomeFile;
  }

  public void setNomeFile(java.lang.String nomeFile) {
    this.nomeFile = nomeFile;
  }

  public NotificaEsitoCommittenteType getEsitoCommittente() {
    return this.esitoCommittente;
  }

  public void setEsitoCommittente(NotificaEsitoCommittenteType esitoCommittente) {
    this.esitoCommittente = esitoCommittente;
  }

  public java.lang.String getMessageId() {
    return this.messageId;
  }

  public void setMessageId(java.lang.String messageId) {
    this.messageId = messageId;
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

  public java.lang.String getIntermediarioConDupliceRuolo() {
    return this.intermediarioConDupliceRuolo;
  }

  public void setIntermediarioConDupliceRuolo(java.lang.String intermediarioConDupliceRuolo) {
    this.intermediarioConDupliceRuolo = intermediarioConDupliceRuolo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaEsitoTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType.modelStaticInstance = new it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaEsitoTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaEsitoTypeModel model(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType.modelStaticInstance;
  }


  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="IdentificativoSdI",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_identificativoSdI = null;

  @XmlTransient
  protected java.lang.Integer identificativoSdI;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="NomeFile",required=true,nillable=false)
  protected java.lang.String nomeFile;

  @XmlElement(name="EsitoCommittente",required=true,nillable=false)
  protected NotificaEsitoCommittenteType esitoCommittente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="MessageId",required=true,nillable=false)
  protected java.lang.String messageId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="PecMessageId",required=false,nillable=false)
  protected java.lang.String pecMessageId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Note",required=false,nillable=false)
  protected java.lang.String note;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=true)
  protected java.lang.String versione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="IntermediarioConDupliceRuolo",required=false)
  protected java.lang.String intermediarioConDupliceRuolo;

}

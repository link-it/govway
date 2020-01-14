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
package it.gov.fatturapa.sdi.messaggi.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for NotificaScarto_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NotificaScarto_Type">
 * 		&lt;sequence>
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NomeFile" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="DataOraRicezione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoArchivio" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}RiferimentoArchivio_Type" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ListaErrori" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}ListaErrori_Type" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="MessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="PecMessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="versione" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificaScarto_Type", 
  propOrder = {
  	"identificativoSdI",
  	"nomeFile",
  	"dataOraRicezione",
  	"riferimentoArchivio",
  	"listaErrori",
  	"messageId",
  	"pecMessageId",
  	"note"
  }
)

@XmlRootElement(name = "NotificaScarto_Type")

public class NotificaScartoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public NotificaScartoType() {
  }

  public java.lang.String getIdentificativoSdI() {
    return this.identificativoSdI;
  }

  public void setIdentificativoSdI(java.lang.String identificativoSdI) {
    this.identificativoSdI = identificativoSdI;
  }

  public java.lang.String getNomeFile() {
    return this.nomeFile;
  }

  public void setNomeFile(java.lang.String nomeFile) {
    this.nomeFile = nomeFile;
  }

  public java.util.Date getDataOraRicezione() {
    return this.dataOraRicezione;
  }

  public void setDataOraRicezione(java.util.Date dataOraRicezione) {
    this.dataOraRicezione = dataOraRicezione;
  }

  public RiferimentoArchivioType getRiferimentoArchivio() {
    return this.riferimentoArchivio;
  }

  public void setRiferimentoArchivio(RiferimentoArchivioType riferimentoArchivio) {
    this.riferimentoArchivio = riferimentoArchivio;
  }

  public ListaErroriType getListaErrori() {
    return this.listaErrori;
  }

  public void setListaErrori(ListaErroriType listaErrori) {
    this.listaErrori = listaErrori;
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

  private static final long serialVersionUID = 1L;

  private static it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaScartoTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType.modelStaticInstance = new it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaScartoTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.messaggi.v1_0.model.NotificaScartoTypeModel model(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IdentificativoSdI",required=true,nillable=false)
  protected java.lang.String identificativoSdI;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="NomeFile",required=true,nillable=false)
  protected java.lang.String nomeFile;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="DataOraRicezione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataOraRicezione;

  @XmlElement(name="RiferimentoArchivio",required=false,nillable=false)
  protected RiferimentoArchivioType riferimentoArchivio;

  @XmlElement(name="ListaErrori",required=true,nillable=false)
  protected ListaErroriType listaErrori;

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

}

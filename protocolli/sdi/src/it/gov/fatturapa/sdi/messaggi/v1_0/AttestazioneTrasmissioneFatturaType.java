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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for AttestazioneTrasmissioneFattura_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AttestazioneTrasmissioneFattura_Type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="NomeFile" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DataOraRicezione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="RiferimentoArchivio" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}RiferimentoArchivio_Type" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Destinatario" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}Destinatario_Type" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="MessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="PecMessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="HashFileOriginale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "AttestazioneTrasmissioneFattura_Type", 
  propOrder = {
  	"identificativoSdI",
  	"nomeFile",
  	"dataOraRicezione",
  	"riferimentoArchivio",
  	"destinatario",
  	"messageId",
  	"pecMessageId",
  	"note",
  	"hashFileOriginale"
  }
)

@XmlRootElement(name = "AttestazioneTrasmissioneFattura_Type")

public class AttestazioneTrasmissioneFatturaType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AttestazioneTrasmissioneFatturaType() {
    super();
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

  public DestinatarioType getDestinatario() {
    return this.destinatario;
  }

  public void setDestinatario(DestinatarioType destinatario) {
    this.destinatario = destinatario;
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

  public java.lang.String getHashFileOriginale() {
    return this.hashFileOriginale;
  }

  public void setHashFileOriginale(java.lang.String hashFileOriginale) {
    this.hashFileOriginale = hashFileOriginale;
  }

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.fatturapa.sdi.messaggi.v1_0.model.AttestazioneTrasmissioneFatturaTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType.modelStaticInstance = new it.gov.fatturapa.sdi.messaggi.v1_0.model.AttestazioneTrasmissioneFatturaTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.messaggi.v1_0.model.AttestazioneTrasmissioneFatturaTypeModel model(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IdentificativoSdI",required=true,nillable=false)
  protected java.lang.String identificativoSdI;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="NomeFile",required=true,nillable=false)
  protected java.lang.String nomeFile;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="DataOraRicezione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataOraRicezione;

  @XmlElement(name="RiferimentoArchivio",required=false,nillable=false)
  protected RiferimentoArchivioType riferimentoArchivio;

  @XmlElement(name="Destinatario",required=true,nillable=false)
  protected DestinatarioType destinatario;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="MessageId",required=true,nillable=false)
  protected java.lang.String messageId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="PecMessageId",required=false,nillable=false)
  protected java.lang.String pecMessageId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Note",required=false,nillable=false)
  protected java.lang.String note;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="HashFileOriginale",required=true,nillable=false)
  protected java.lang.String hashFileOriginale;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=true)
  protected java.lang.String versione;

}

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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for RicevutaImpossibilitaRecapito_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RicevutaImpossibilitaRecapito_Type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="IdentificativoSdI" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="NomeFile" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Hash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DataOraRicezione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DataMessaADisposizione" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="RiferimentoArchivio" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}RiferimentoArchivio_Type" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Descrizione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="MessageId" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="PecMessageId" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="versione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RicevutaImpossibilitaRecapito_Type", 
  propOrder = {
  	"identificativoSdI",
  	"nomeFile",
  	"hash",
  	"dataOraRicezione",
  	"dataMessaADisposizione",
  	"riferimentoArchivio",
  	"descrizione",
  	"messageId",
  	"pecMessageId",
  	"note"
  }
)

@XmlRootElement(name = "RicevutaImpossibilitaRecapito_Type")

public class RicevutaImpossibilitaRecapitoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RicevutaImpossibilitaRecapitoType() {
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

  public java.lang.String getHash() {
    return this.hash;
  }

  public void setHash(java.lang.String hash) {
    this.hash = hash;
  }

  public java.util.Date getDataOraRicezione() {
    return this.dataOraRicezione;
  }

  public void setDataOraRicezione(java.util.Date dataOraRicezione) {
    this.dataOraRicezione = dataOraRicezione;
  }

  public java.util.Date getDataMessaADisposizione() {
    return this.dataMessaADisposizione;
  }

  public void setDataMessaADisposizione(java.util.Date dataMessaADisposizione) {
    this.dataMessaADisposizione = dataMessaADisposizione;
  }

  public RiferimentoArchivioType getRiferimentoArchivio() {
    return this.riferimentoArchivio;
  }

  public void setRiferimentoArchivio(RiferimentoArchivioType riferimentoArchivio) {
    this.riferimentoArchivio = riferimentoArchivio;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
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

  private static it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.model.RicevutaImpossibilitaRecapitoTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType.modelStaticInstance==null){
  			it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType.modelStaticInstance = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.model.RicevutaImpossibilitaRecapitoTypeModel();
	  }
  }
  public static it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.model.RicevutaImpossibilitaRecapitoTypeModel model(){
	  if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="IdentificativoSdI",required=true,nillable=false)
  protected java.lang.String identificativoSdI;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="NomeFile",required=true,nillable=false)
  protected java.lang.String nomeFile;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Hash",required=true,nillable=false)
  protected java.lang.String hash;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="DataOraRicezione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataOraRicezione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataMessaADisposizione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataMessaADisposizione;

  @XmlElement(name="RiferimentoArchivio",required=false,nillable=false)
  protected RiferimentoArchivioType riferimentoArchivio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

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

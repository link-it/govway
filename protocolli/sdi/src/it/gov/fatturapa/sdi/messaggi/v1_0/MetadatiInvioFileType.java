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


/** <p>Java class for MetadatiInvioFile_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MetadatiInvioFile_Type">
 * 		&lt;sequence>
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}integer" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NomeFile" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CodiceDestinatario" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Formato" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="TentativiInvio" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="MessageId" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}string" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "MetadatiInvioFile_Type", 
  propOrder = {
  	"_decimalWrapper_identificativoSdI",
  	"nomeFile",
  	"codiceDestinatario",
  	"formato",
  	"tentativiInvio",
  	"messageId",
  	"note"
  }
)

@XmlRootElement(name = "MetadatiInvioFile_Type")

public class MetadatiInvioFileType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MetadatiInvioFileType() {
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

  public java.lang.String getCodiceDestinatario() {
    return this.codiceDestinatario;
  }

  public void setCodiceDestinatario(java.lang.String codiceDestinatario) {
    this.codiceDestinatario = codiceDestinatario;
  }

  public java.lang.String getFormato() {
    return this.formato;
  }

  public void setFormato(java.lang.String formato) {
    this.formato = formato;
  }

  public java.lang.Integer getTentativiInvio() {
    return this.tentativiInvio;
  }

  public void setTentativiInvio(java.lang.Integer tentativiInvio) {
    this.tentativiInvio = tentativiInvio;
  }

  public java.lang.String getMessageId() {
    return this.messageId;
  }

  public void setMessageId(java.lang.String messageId) {
    this.messageId = messageId;
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

  @XmlTransient
  private Long id;

  private static it.gov.fatturapa.sdi.messaggi.v1_0.model.MetadatiInvioFileTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType.modelStaticInstance = new it.gov.fatturapa.sdi.messaggi.v1_0.model.MetadatiInvioFileTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.messaggi.v1_0.model.MetadatiInvioFileTypeModel model(){
	  if(it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType.modelStaticInstance;
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

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CodiceDestinatario",required=true,nillable=false)
  protected java.lang.String codiceDestinatario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Formato",required=true,nillable=false)
  protected java.lang.String formato;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="TentativiInvio",required=true,nillable=false)
  protected java.lang.Integer tentativiInvio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="MessageId",required=true,nillable=false)
  protected java.lang.String messageId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Note",required=false,nillable=false)
  protected java.lang.String note;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=true)
  protected java.lang.String versione;

}

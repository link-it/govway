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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for fileSdIConMetadati_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fileSdIConMetadati_Type">
 * 		&lt;sequence>
 * 			&lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}integer" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NomeFile" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="File" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NomeFileMetadati" type="{http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0/types}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Metadati" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileSdIConMetadati_Type", 
  propOrder = {
  	"_decimalWrapper_identificativoSdI",
  	"nomeFile",
  	"file",
  	"nomeFileMetadati",
  	"metadati"
  }
)

@XmlRootElement(name = "fileSdIConMetadati_Type")

public class FileSdIConMetadatiType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FileSdIConMetadatiType() {
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

  public byte[] getFile() {
    return this.file;
  }

  public void setFile(byte[] file) {
    this.file = file;
  }

  public java.lang.String getNomeFileMetadati() {
    return this.nomeFileMetadati;
  }

  public void setNomeFileMetadati(java.lang.String nomeFileMetadati) {
    this.nomeFileMetadati = nomeFileMetadati;
  }

  public byte[] getMetadati() {
    return this.metadati;
  }

  public void setMetadati(byte[] metadati) {
    this.metadati = metadati;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.FileSdIConMetadatiTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType.modelStaticInstance = new it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.FileSdIConMetadatiTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model.FileSdIConMetadatiTypeModel model(){
	  if(it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType.modelStaticInstance;
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

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="File",required=true,nillable=false)
  protected byte[] file;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="NomeFileMetadati",required=true,nillable=false)
  protected java.lang.String nomeFileMetadati;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="Metadati",required=true,nillable=false)
  protected byte[] metadati;

}

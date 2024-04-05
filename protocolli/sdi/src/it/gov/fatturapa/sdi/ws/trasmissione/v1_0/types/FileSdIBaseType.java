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
package it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for fileSdIBase_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fileSdIBase_Type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="NomeFile" type="{http://www.fatturapa.gov.it/sdi/ws/trasmissione/v1.0/types}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="File" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileSdIBase_Type", 
  propOrder = {
  	"nomeFile",
  	"file"
  }
)

@XmlRootElement(name = "fileSdIBase_Type")

public class FileSdIBaseType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FileSdIBaseType() {
    super();
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

  private static final long serialVersionUID = 1L;

  private static it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.model.FileSdIBaseTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType.modelStaticInstance = new it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.model.FileSdIBaseTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.model.FileSdIBaseTypeModel model(){
	  if(it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIBaseType.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="NomeFile",required=true,nillable=false)
  protected java.lang.String nomeFile;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="File",required=true,nillable=false)
  protected byte[] file;

}

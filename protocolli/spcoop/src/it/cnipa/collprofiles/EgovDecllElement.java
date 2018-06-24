/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package it.cnipa.collprofiles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for egovDecllElement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="egovDecllElement">
 * 		&lt;sequence>
 * 			&lt;element name="e-govVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="rifDefinizioneInterfaccia" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="operationList" type="{http://www.cnipa.it/collProfiles}operationListType" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "egovDecllElement", 
  propOrder = {
  	"eGovVersion",
  	"rifDefinizioneInterfaccia",
  	"operationList"
  }
)

@XmlRootElement(name = "egovDecllElement")

public class EgovDecllElement extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public EgovDecllElement() {
  }

  public java.lang.String getEGovVersion() {
    return this.eGovVersion;
  }

  public void setEGovVersion(java.lang.String eGovVersion) {
    this.eGovVersion = eGovVersion;
  }

  public java.net.URI getRifDefinizioneInterfaccia() {
    return this.rifDefinizioneInterfaccia;
  }

  public void setRifDefinizioneInterfaccia(java.net.URI rifDefinizioneInterfaccia) {
    this.rifDefinizioneInterfaccia = rifDefinizioneInterfaccia;
  }

  public OperationListType getOperationList() {
    return this.operationList;
  }

  public void setOperationList(OperationListType operationList) {
    this.operationList = operationList;
  }

  private static final long serialVersionUID = 1L;

  private static it.cnipa.collprofiles.model.EgovDecllElementModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.cnipa.collprofiles.EgovDecllElement.modelStaticInstance==null){
  			it.cnipa.collprofiles.EgovDecllElement.modelStaticInstance = new it.cnipa.collprofiles.model.EgovDecllElementModel();
	  }
  }
  public static it.cnipa.collprofiles.model.EgovDecllElementModel model(){
	  if(it.cnipa.collprofiles.EgovDecllElement.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.cnipa.collprofiles.EgovDecllElement.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="e-govVersion",required=true,nillable=false)
  protected java.lang.String eGovVersion;

  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlElement(name="rifDefinizioneInterfaccia",required=true,nillable=false)
  protected java.net.URI rifDefinizioneInterfaccia;

  @XmlElement(name="operationList",required=true,nillable=false)
  protected OperationListType operationList;

}

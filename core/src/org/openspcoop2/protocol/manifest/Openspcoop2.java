/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for openspcoop2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openspcoop2">
 * 		&lt;sequence>
 * 			&lt;element name="protocolName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="factory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="web" type="{http://www.openspcoop2.org/protocol/manifest}web" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="registroServizi" type="{http://www.openspcoop2.org/protocol/manifest}registroServizi" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="urlMapping" type="{http://www.openspcoop2.org/protocol/manifest}urlMapping" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="binding" type="{http://www.openspcoop2.org/protocol/manifest}binding" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "openspcoop2", 
  propOrder = {
  	"protocolName",
  	"factory",
  	"web",
  	"registroServizi",
  	"urlMapping",
  	"binding"
  }
)

@XmlRootElement(name = "openspcoop2")

public class Openspcoop2 extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Openspcoop2() {
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

  public java.lang.String getProtocolName() {
    return this.protocolName;
  }

  public void setProtocolName(java.lang.String protocolName) {
    this.protocolName = protocolName;
  }

  public java.lang.String getFactory() {
    return this.factory;
  }

  public void setFactory(java.lang.String factory) {
    this.factory = factory;
  }

  public Web getWeb() {
    return this.web;
  }

  public void setWeb(Web web) {
    this.web = web;
  }

  public RegistroServizi getRegistroServizi() {
    return this.registroServizi;
  }

  public void setRegistroServizi(RegistroServizi registroServizi) {
    this.registroServizi = registroServizi;
  }

  public UrlMapping getUrlMapping() {
    return this.urlMapping;
  }

  public void setUrlMapping(UrlMapping urlMapping) {
    this.urlMapping = urlMapping;
  }

  public Binding getBinding() {
    return this.binding;
  }

  public void setBinding(Binding binding) {
    this.binding = binding;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.protocol.manifest.model.Openspcoop2Model modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.protocol.manifest.Openspcoop2.modelStaticInstance==null){
  			org.openspcoop2.protocol.manifest.Openspcoop2.modelStaticInstance = new org.openspcoop2.protocol.manifest.model.Openspcoop2Model();
	  }
  }
  public static org.openspcoop2.protocol.manifest.model.Openspcoop2Model model(){
	  if(org.openspcoop2.protocol.manifest.Openspcoop2.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.protocol.manifest.Openspcoop2.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocolName",required=true,nillable=false)
  protected java.lang.String protocolName;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="factory",required=true,nillable=false)
  protected java.lang.String factory;

  @XmlElement(name="web",required=true,nillable=false)
  protected Web web;

  @XmlElement(name="registroServizi",required=true,nillable=false)
  protected RegistroServizi registroServizi;

  @XmlElement(name="urlMapping",required=true,nillable=false)
  protected UrlMapping urlMapping;

  @XmlElement(name="binding",required=true,nillable=false)
  protected Binding binding;

}

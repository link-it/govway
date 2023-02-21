/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for openspcoop2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openspcoop2"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="protocol" type="{http://www.openspcoop2.org/protocol/manifest}protocol" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="binding" type="{http://www.openspcoop2.org/protocol/manifest}binding" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="web" type="{http://www.openspcoop2.org/protocol/manifest}web" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="registry" type="{http://www.openspcoop2.org/protocol/manifest}registry" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="urlMapping" type="{http://www.openspcoop2.org/protocol/manifest}urlMapping" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "openspcoop2", 
  propOrder = {
  	"protocol",
  	"binding",
  	"web",
  	"registry",
  	"urlMapping"
  }
)

@XmlRootElement(name = "openspcoop2")

public class Openspcoop2 extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Openspcoop2() {
    super();
  }

  public Protocol getProtocol() {
    return this.protocol;
  }

  public void setProtocol(Protocol protocol) {
    this.protocol = protocol;
  }

  public Binding getBinding() {
    return this.binding;
  }

  public void setBinding(Binding binding) {
    this.binding = binding;
  }

  public Web getWeb() {
    return this.web;
  }

  public void setWeb(Web web) {
    this.web = web;
  }

  public Registry getRegistry() {
    return this.registry;
  }

  public void setRegistry(Registry registry) {
    this.registry = registry;
  }

  public UrlMapping getUrlMapping() {
    return this.urlMapping;
  }

  public void setUrlMapping(UrlMapping urlMapping) {
    this.urlMapping = urlMapping;
  }

  private static final long serialVersionUID = 1L;

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


  @XmlElement(name="protocol",required=true,nillable=false)
  protected Protocol protocol;

  @XmlElement(name="binding",required=true,nillable=false)
  protected Binding binding;

  @XmlElement(name="web",required=true,nillable=false)
  protected Web web;

  @XmlElement(name="registry",required=true,nillable=false)
  protected Registry registry;

  @XmlElement(name="urlMapping",required=true,nillable=false)
  protected UrlMapping urlMapping;

}

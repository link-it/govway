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
package org.openspcoop2.core.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for invocation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocation">
 * 		&lt;sequence>
 * 			&lt;element name="resource" type="{http://www.openspcoop2.org/core/api}resource" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="url-parameters" type="{http://www.openspcoop2.org/core/api}url-parameters" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="header-parameters" type="{http://www.openspcoop2.org/core/api}header-parameters" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "invocation", 
  propOrder = {
  	"resource",
  	"urlParameters",
  	"headerParameters"
  }
)

@XmlRootElement(name = "invocation")

public class Invocation extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Invocation() {
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

  public Resource getResource() {
    return this.resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public UrlParameters getUrlParameters() {
    return this.urlParameters;
  }

  public void setUrlParameters(UrlParameters urlParameters) {
    this.urlParameters = urlParameters;
  }

  public HeaderParameters getHeaderParameters() {
    return this.headerParameters;
  }

  public void setHeaderParameters(HeaderParameters headerParameters) {
    this.headerParameters = headerParameters;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.api.model.InvocationModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.api.Invocation.modelStaticInstance==null){
  			org.openspcoop2.core.api.Invocation.modelStaticInstance = new org.openspcoop2.core.api.model.InvocationModel();
	  }
  }
  public static org.openspcoop2.core.api.model.InvocationModel model(){
	  if(org.openspcoop2.core.api.Invocation.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.api.Invocation.modelStaticInstance;
  }


  @XmlElement(name="resource",required=true,nillable=false)
  protected Resource resource;

  @XmlElement(name="url-parameters",required=false,nillable=false)
  protected UrlParameters urlParameters;

  @XmlElement(name="header-parameters",required=false,nillable=false)
  protected HeaderParameters headerParameters;

}

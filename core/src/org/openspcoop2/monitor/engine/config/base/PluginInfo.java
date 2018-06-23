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
package org.openspcoop2.monitor.engine.config.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for plugin-info complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="plugin-info">
 * 		&lt;sequence>
 * 			&lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "plugin-info", 
  propOrder = {
  	"content"
  }
)

@XmlRootElement(name = "plugin-info")

public class PluginInfo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PluginInfo() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public byte[] getContent() {
    return this.content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.monitor.engine.config.base.model.PluginInfoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.monitor.engine.config.base.PluginInfo.modelStaticInstance==null){
  			org.openspcoop2.monitor.engine.config.base.PluginInfo.modelStaticInstance = new org.openspcoop2.monitor.engine.config.base.model.PluginInfoModel();
	  }
  }
  public static org.openspcoop2.monitor.engine.config.base.model.PluginInfoModel model(){
	  if(org.openspcoop2.monitor.engine.config.base.PluginInfo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.monitor.engine.config.base.PluginInfo.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="content",required=true,nillable=false)
  protected byte[] content;

}

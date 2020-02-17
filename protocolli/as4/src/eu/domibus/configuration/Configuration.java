/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configuration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configuration"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="mpcs" type="{http://www.domibus.eu/configuration}mpcs" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="businessProcesses" type="{http://www.domibus.eu/configuration}businessProcesses" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="party" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configuration", 
  propOrder = {
  	"mpcs",
  	"businessProcesses"
  }
)

@XmlRootElement(name = "configuration")

public class Configuration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Configuration() {
  }

  public Mpcs getMpcs() {
    return this.mpcs;
  }

  public void setMpcs(Mpcs mpcs) {
    this.mpcs = mpcs;
  }

  public BusinessProcesses getBusinessProcesses() {
    return this.businessProcesses;
  }

  public void setBusinessProcesses(BusinessProcesses businessProcesses) {
    this.businessProcesses = businessProcesses;
  }

  public java.lang.String getParty() {
    return this.party;
  }

  public void setParty(java.lang.String party) {
    this.party = party;
  }

  private static final long serialVersionUID = 1L;

  private static eu.domibus.configuration.model.ConfigurationModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(eu.domibus.configuration.Configuration.modelStaticInstance==null){
  			eu.domibus.configuration.Configuration.modelStaticInstance = new eu.domibus.configuration.model.ConfigurationModel();
	  }
  }
  public static eu.domibus.configuration.model.ConfigurationModel model(){
	  if(eu.domibus.configuration.Configuration.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return eu.domibus.configuration.Configuration.modelStaticInstance;
  }


  @XmlElement(name="mpcs",required=true,nillable=false)
  protected Mpcs mpcs;

  @XmlElement(name="businessProcesses",required=true,nillable=false)
  protected BusinessProcesses businessProcesses;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="party",required=true)
  protected java.lang.String party;

}

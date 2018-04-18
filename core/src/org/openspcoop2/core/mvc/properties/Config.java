/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.core.mvc.properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for config complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="config">
 * 		&lt;sequence>
 * 			&lt;element name="properties" type="{http://www.openspcoop2.org/core/mvc/properties}properties" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="section" type="{http://www.openspcoop2.org/core/mvc/properties}section" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "config", 
  propOrder = {
  	"properties",
  	"section"
  }
)

@XmlRootElement(name = "config")

public class Config extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Config() {
  }

  public Properties getProperties() {
    return this.properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public void addSection(Section section) {
    this.section.add(section);
  }

  public Section getSection(int index) {
    return this.section.get( index );
  }

  public Section removeSection(int index) {
    return this.section.remove( index );
  }

  public List<Section> getSectionList() {
    return this.section;
  }

  public void setSectionList(List<Section> section) {
    this.section=section;
  }

  public int sizeSectionList() {
    return this.section.size();
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.mvc.properties.model.ConfigModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.mvc.properties.Config.modelStaticInstance==null){
  			org.openspcoop2.core.mvc.properties.Config.modelStaticInstance = new org.openspcoop2.core.mvc.properties.model.ConfigModel();
	  }
  }
  public static org.openspcoop2.core.mvc.properties.model.ConfigModel model(){
	  if(org.openspcoop2.core.mvc.properties.Config.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.mvc.properties.Config.modelStaticInstance;
  }


  @XmlElement(name="properties",required=false,nillable=false)
  protected Properties properties;

  @XmlElement(name="section",required=true,nillable=false)
  protected List<Section> section = new ArrayList<Section>();

  /**
   * @deprecated Use method getSectionList
   * @return List<Section>
  */
  @Deprecated
  public List<Section> getSection() {
  	return this.section;
  }

  /**
   * @deprecated Use method setSectionList
   * @param section List<Section>
  */
  @Deprecated
  public void setSection(List<Section> section) {
  	this.section=section;
  }

  /**
   * @deprecated Use method sizeSectionList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSection() {
  	return this.section.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

}

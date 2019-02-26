/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
 * 			&lt;element name="compatibility" type="{http://www.openspcoop2.org/core/mvc/properties}compatibility" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="properties" type="{http://www.openspcoop2.org/core/mvc/properties}properties" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="section" type="{http://www.openspcoop2.org/core/mvc/properties}section" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="sortLabel" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="provider" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
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
  	"compatibility",
  	"properties",
  	"section"
  }
)

@XmlRootElement(name = "config")

public class Config extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Config() {
  }

  public Compatibility getCompatibility() {
    return this.compatibility;
  }

  public void setCompatibility(Compatibility compatibility) {
    this.compatibility = compatibility;
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

  public java.lang.String getId() {
    return this.id;
  }

  public void setId(java.lang.String id) {
    this.id = id;
  }

  public java.lang.String getLabel() {
    return this.label;
  }

  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  public java.lang.String getSortLabel() {
    return this.sortLabel;
  }

  public void setSortLabel(java.lang.String sortLabel) {
    this.sortLabel = sortLabel;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getProvider() {
    return this.provider;
  }

  public void setProvider(java.lang.String provider) {
    this.provider = provider;
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


  @XmlElement(name="compatibility",required=false,nillable=false)
  protected Compatibility compatibility;

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
  @XmlAttribute(name="id",required=true)
  protected java.lang.String id;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="label",required=true)
  protected java.lang.String label;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="sortLabel",required=false)
  protected java.lang.String sortLabel;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="provider",required=false)
  protected java.lang.String provider;

}

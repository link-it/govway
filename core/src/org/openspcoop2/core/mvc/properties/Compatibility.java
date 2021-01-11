/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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


/** <p>Java class for compatibility complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="compatibility"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="tags" type="{http://www.openspcoop2.org/core/mvc/properties}tags" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="and" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="not" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "compatibility", 
  propOrder = {
  	"tags"
  }
)

@XmlRootElement(name = "compatibility")

public class Compatibility extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Compatibility() {
  }

  public void addTags(Tags tags) {
    this.tags.add(tags);
  }

  public Tags getTags(int index) {
    return this.tags.get( index );
  }

  public Tags removeTags(int index) {
    return this.tags.remove( index );
  }

  public List<Tags> getTagsList() {
    return this.tags;
  }

  public void setTagsList(List<Tags> tags) {
    this.tags=tags;
  }

  public int sizeTagsList() {
    return this.tags.size();
  }

  public boolean isAnd() {
    return this.and;
  }

  public boolean getAnd() {
    return this.and;
  }

  public void setAnd(boolean and) {
    this.and = and;
  }

  public boolean isNot() {
    return this.not;
  }

  public boolean getNot() {
    return this.not;
  }

  public void setNot(boolean not) {
    this.not = not;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="tags",required=true,nillable=false)
  protected List<Tags> tags = new ArrayList<Tags>();

  /**
   * @deprecated Use method getTagsList
   * @return List&lt;Tags&gt;
  */
  @Deprecated
  public List<Tags> getTags() {
  	return this.tags;
  }

  /**
   * @deprecated Use method setTagsList
   * @param tags List&lt;Tags&gt;
  */
  @Deprecated
  public void setTags(List<Tags> tags) {
  	this.tags=tags;
  }

  /**
   * @deprecated Use method sizeTagsList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeTags() {
  	return this.tags.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="and",required=false)
  protected boolean and = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="not",required=false)
  protected boolean not = false;

}

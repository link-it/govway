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
package org.openspcoop2.core.mvc.properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for properties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="properties"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="collection" type="{http://www.openspcoop2.org/core/mvc/properties}collection" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "properties", 
  propOrder = {
  	"collection"
  }
)

@XmlRootElement(name = "properties")

public class Properties extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Properties() {
    super();
  }

  public void addCollection(Collection collection) {
    this.collection.add(collection);
  }

  public Collection getCollection(int index) {
    return this.collection.get( index );
  }

  public Collection removeCollection(int index) {
    return this.collection.remove( index );
  }

  public List<Collection> getCollectionList() {
    return this.collection;
  }

  public void setCollectionList(List<Collection> collection) {
    this.collection=collection;
  }

  public int sizeCollectionList() {
    return this.collection.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="collection",required=true,nillable=false)
  private List<Collection> collection = new ArrayList<>();

  /**
   * Use method getCollectionList
   * @return List&lt;Collection&gt;
  */
  public List<Collection> getCollection() {
  	return this.getCollectionList();
  }

  /**
   * Use method setCollectionList
   * @param collection List&lt;Collection&gt;
  */
  public void setCollection(List<Collection> collection) {
  	this.setCollectionList(collection);
  }

  /**
   * Use method sizeCollectionList
   * @return lunghezza della lista
  */
  public int sizeCollection() {
  	return this.sizeCollectionList();
  }

}

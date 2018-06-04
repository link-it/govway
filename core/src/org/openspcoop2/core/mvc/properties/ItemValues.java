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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for itemValues complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="itemValues">
 * 		&lt;sequence>
 * 			&lt;element name="value" type="{http://www.openspcoop2.org/core/mvc/properties}itemValue" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "itemValues", 
  propOrder = {
  	"value"
  }
)

@XmlRootElement(name = "itemValues")

public class ItemValues extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ItemValues() {
  }

  public void addValue(ItemValue value) {
    this.value.add(value);
  }

  public ItemValue getValue(int index) {
    return this.value.get( index );
  }

  public ItemValue removeValue(int index) {
    return this.value.remove( index );
  }

  public List<ItemValue> getValueList() {
    return this.value;
  }

  public void setValueList(List<ItemValue> value) {
    this.value=value;
  }

  public int sizeValueList() {
    return this.value.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="value",required=true,nillable=false)
  protected List<ItemValue> value = new ArrayList<ItemValue>();

  /**
   * @deprecated Use method getValueList
   * @return List<ItemValue>
  */
  @Deprecated
  public List<ItemValue> getValue() {
  	return this.value;
  }

  /**
   * @deprecated Use method setValueList
   * @param value List<ItemValue>
  */
  @Deprecated
  public void setValue(List<ItemValue> value) {
  	this.value=value;
  }

  /**
   * @deprecated Use method sizeValueList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeValue() {
  	return this.value.size();
  }

}

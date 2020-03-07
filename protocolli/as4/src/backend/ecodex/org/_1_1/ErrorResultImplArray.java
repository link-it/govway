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
package backend.ecodex.org._1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for errorResultImplArray complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="errorResultImplArray"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="item" type="{http://org.ecodex.backend/1_1/}errorResultImpl" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "errorResultImplArray", 
  propOrder = {
  	"item"
  }
)

@XmlRootElement(name = "errorResultImplArray")

public class ErrorResultImplArray extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ErrorResultImplArray() {
  }

  public void addItem(ErrorResultImpl item) {
    this.item.add(item);
  }

  public ErrorResultImpl getItem(int index) {
    return this.item.get( index );
  }

  public ErrorResultImpl removeItem(int index) {
    return this.item.remove( index );
  }

  public List<ErrorResultImpl> getItemList() {
    return this.item;
  }

  public void setItemList(List<ErrorResultImpl> item) {
    this.item=item;
  }

  public int sizeItemList() {
    return this.item.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="item",required=true,nillable=false)
  protected List<ErrorResultImpl> item = new ArrayList<ErrorResultImpl>();

  /**
   * @deprecated Use method getItemList
   * @return List&lt;ErrorResultImpl&gt;
  */
  @Deprecated
  public List<ErrorResultImpl> getItem() {
  	return this.item;
  }

  /**
   * @deprecated Use method setItemList
   * @param item List&lt;ErrorResultImpl&gt;
  */
  @Deprecated
  public void setItem(List<ErrorResultImpl> item) {
  	this.item=item;
  }

  /**
   * @deprecated Use method sizeItemList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeItem() {
  	return this.item.size();
  }

}

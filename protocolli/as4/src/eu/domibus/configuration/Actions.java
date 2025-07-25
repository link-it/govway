/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for actions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="actions"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="action" type="{http://www.domibus.eu/configuration}action" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "actions", 
  propOrder = {
  	"action"
  }
)

@XmlRootElement(name = "actions")

public class Actions extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Actions() {
    super();
  }

  public void addAction(Action action) {
    this.action.add(action);
  }

  public Action getAction(int index) {
    return this.action.get( index );
  }

  public Action removeAction(int index) {
    return this.action.remove( index );
  }

  public List<Action> getActionList() {
    return this.action;
  }

  public void setActionList(List<Action> action) {
    this.action=action;
  }

  public int sizeActionList() {
    return this.action.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="action",required=true,nillable=false)
  private List<Action> action = new ArrayList<>();

  /**
   * Use method getActionList
   * @return List&lt;Action&gt;
  */
  public List<Action> getAction() {
  	return this.getActionList();
  }

  /**
   * Use method setActionList
   * @param action List&lt;Action&gt;
  */
  public void setAction(List<Action> action) {
  	this.setActionList(action);
  }

  /**
   * Use method sizeActionList
   * @return lunghezza della lista
  */
  public int sizeAction() {
  	return this.sizeActionList();
  }

}

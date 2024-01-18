/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.wsbl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for transitionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transitionsType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="transition" type="{http://spcoop.gov.it/sica/wsbl}transitionType" minOccurs="2" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "transitionsType", 
  propOrder = {
  	"transition"
  }
)

@XmlRootElement(name = "transitionsType")

public class TransitionsType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TransitionsType() {
    super();
  }

  public void addTransition(TransitionType transition) {
    this.transition.add(transition);
  }

  public TransitionType getTransition(int index) {
    return this.transition.get( index );
  }

  public TransitionType removeTransition(int index) {
    return this.transition.remove( index );
  }

  public List<TransitionType> getTransitionList() {
    return this.transition;
  }

  public void setTransitionList(List<TransitionType> transition) {
    this.transition=transition;
  }

  public int sizeTransitionList() {
    return this.transition.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="transition",required=true,nillable=false)
  private List<TransitionType> transition = new ArrayList<>();

  /**
   * Use method getTransitionList
   * @return List&lt;TransitionType&gt;
  */
  public List<TransitionType> getTransition() {
  	return this.getTransitionList();
  }

  /**
   * Use method setTransitionList
   * @param transition List&lt;TransitionType&gt;
  */
  public void setTransition(List<TransitionType> transition) {
  	this.setTransitionList(transition);
  }

  /**
   * Use method sizeTransitionList
   * @return lunghezza della lista
  */
  public int sizeTransition() {
  	return this.sizeTransitionList();
  }

}

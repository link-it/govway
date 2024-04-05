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
package eu.domibus.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for legs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="legs"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="leg" type="{http://www.domibus.eu/configuration}leg" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "legs", 
  propOrder = {
  	"leg"
  }
)

@XmlRootElement(name = "legs")

public class Legs extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Legs() {
    super();
  }

  public void addLeg(Leg leg) {
    this.leg.add(leg);
  }

  public Leg getLeg(int index) {
    return this.leg.get( index );
  }

  public Leg removeLeg(int index) {
    return this.leg.remove( index );
  }

  public List<Leg> getLegList() {
    return this.leg;
  }

  public void setLegList(List<Leg> leg) {
    this.leg=leg;
  }

  public int sizeLegList() {
    return this.leg.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="leg",required=true,nillable=false)
  private List<Leg> leg = new ArrayList<>();

  /**
   * Use method getLegList
   * @return List&lt;Leg&gt;
  */
  public List<Leg> getLeg() {
  	return this.getLegList();
  }

  /**
   * Use method setLegList
   * @param leg List&lt;Leg&gt;
  */
  public void setLeg(List<Leg> leg) {
  	this.setLegList(leg);
  }

  /**
   * Use method sizeLegList
   * @return lunghezza della lista
  */
  public int sizeLeg() {
  	return this.sizeLegList();
  }

}

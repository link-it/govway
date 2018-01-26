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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for legs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="legs">
 * 		&lt;sequence>
 * 			&lt;element name="leg" type="{http://www.domibus.eu/configuration}leg" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "legs", 
  propOrder = {
  	"leg"
  }
)

@XmlRootElement(name = "legs")

public class Legs extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Legs() {
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
  protected List<Leg> leg = new ArrayList<Leg>();

  /**
   * @deprecated Use method getLegList
   * @return List<Leg>
  */
  @Deprecated
  public List<Leg> getLeg() {
  	return this.leg;
  }

  /**
   * @deprecated Use method setLegList
   * @param leg List<Leg>
  */
  @Deprecated
  public void setLeg(List<Leg> leg) {
  	this.leg=leg;
  }

  /**
   * @deprecated Use method sizeLegList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeLeg() {
  	return this.leg.size();
  }

}

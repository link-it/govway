/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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


/** <p>Java class for as4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="as4">
 * 		&lt;sequence>
 * 			&lt;element name="receptionAwareness" type="{http://www.domibus.eu/configuration}receptionAwareness" minOccurs="1" maxOccurs="unbounded"/>
 * 			&lt;element name="reliability" type="{http://www.domibus.eu/configuration}reliability" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "as4", 
  propOrder = {
  	"receptionAwareness",
  	"reliability"
  }
)

@XmlRootElement(name = "as4")

public class As4 extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public As4() {
  }

  public void addReceptionAwareness(ReceptionAwareness receptionAwareness) {
    this.receptionAwareness.add(receptionAwareness);
  }

  public ReceptionAwareness getReceptionAwareness(int index) {
    return this.receptionAwareness.get( index );
  }

  public ReceptionAwareness removeReceptionAwareness(int index) {
    return this.receptionAwareness.remove( index );
  }

  public List<ReceptionAwareness> getReceptionAwarenessList() {
    return this.receptionAwareness;
  }

  public void setReceptionAwarenessList(List<ReceptionAwareness> receptionAwareness) {
    this.receptionAwareness=receptionAwareness;
  }

  public int sizeReceptionAwarenessList() {
    return this.receptionAwareness.size();
  }

  public void addReliability(Reliability reliability) {
    this.reliability.add(reliability);
  }

  public Reliability getReliability(int index) {
    return this.reliability.get( index );
  }

  public Reliability removeReliability(int index) {
    return this.reliability.remove( index );
  }

  public List<Reliability> getReliabilityList() {
    return this.reliability;
  }

  public void setReliabilityList(List<Reliability> reliability) {
    this.reliability=reliability;
  }

  public int sizeReliabilityList() {
    return this.reliability.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="receptionAwareness",required=true,nillable=false)
  protected List<ReceptionAwareness> receptionAwareness = new ArrayList<ReceptionAwareness>();

  /**
   * @deprecated Use method getReceptionAwarenessList
   * @return List<ReceptionAwareness>
  */
  @Deprecated
  public List<ReceptionAwareness> getReceptionAwareness() {
  	return this.receptionAwareness;
  }

  /**
   * @deprecated Use method setReceptionAwarenessList
   * @param receptionAwareness List<ReceptionAwareness>
  */
  @Deprecated
  public void setReceptionAwareness(List<ReceptionAwareness> receptionAwareness) {
  	this.receptionAwareness=receptionAwareness;
  }

  /**
   * @deprecated Use method sizeReceptionAwarenessList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeReceptionAwareness() {
  	return this.receptionAwareness.size();
  }

  @XmlElement(name="reliability",required=true,nillable=false)
  protected List<Reliability> reliability = new ArrayList<Reliability>();

  /**
   * @deprecated Use method getReliabilityList
   * @return List<Reliability>
  */
  @Deprecated
  public List<Reliability> getReliability() {
  	return this.reliability;
  }

  /**
   * @deprecated Use method setReliabilityList
   * @param reliability List<Reliability>
  */
  @Deprecated
  public void setReliability(List<Reliability> reliability) {
  	this.reliability=reliability;
  }

  /**
   * @deprecated Use method sizeReliabilityList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeReliability() {
  	return this.reliability.size();
  }

}

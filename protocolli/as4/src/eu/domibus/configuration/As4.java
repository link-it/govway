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
 * &lt;complexType name="as4"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="receptionAwareness" type="{http://www.domibus.eu/configuration}receptionAwareness" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="reliability" type="{http://www.domibus.eu/configuration}reliability" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "as4", 
  propOrder = {
  	"receptionAwareness",
  	"reliability"
  }
)

@XmlRootElement(name = "as4")

public class As4 extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public As4() {
    super();
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
  private List<ReceptionAwareness> receptionAwareness = new ArrayList<>();

  /**
   * Use method getReceptionAwarenessList
   * @return List&lt;ReceptionAwareness&gt;
  */
  public List<ReceptionAwareness> getReceptionAwareness() {
  	return this.getReceptionAwarenessList();
  }

  /**
   * Use method setReceptionAwarenessList
   * @param receptionAwareness List&lt;ReceptionAwareness&gt;
  */
  public void setReceptionAwareness(List<ReceptionAwareness> receptionAwareness) {
  	this.setReceptionAwarenessList(receptionAwareness);
  }

  /**
   * Use method sizeReceptionAwarenessList
   * @return lunghezza della lista
  */
  public int sizeReceptionAwareness() {
  	return this.sizeReceptionAwarenessList();
  }

  @XmlElement(name="reliability",required=true,nillable=false)
  private List<Reliability> reliability = new ArrayList<>();

  /**
   * Use method getReliabilityList
   * @return List&lt;Reliability&gt;
  */
  public List<Reliability> getReliability() {
  	return this.getReliabilityList();
  }

  /**
   * Use method setReliabilityList
   * @param reliability List&lt;Reliability&gt;
  */
  public void setReliability(List<Reliability> reliability) {
  	this.setReliabilityList(reliability);
  }

  /**
   * Use method sizeReliabilityList
   * @return lunghezza della lista
  */
  public int sizeReliability() {
  	return this.sizeReliabilityList();
  }

}

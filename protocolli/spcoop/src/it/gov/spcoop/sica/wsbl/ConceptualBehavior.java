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


/** <p>Java class for ConceptualBehavior complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConceptualBehavior"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="name" type="{http://spcoop.gov.it/sica/wsbl}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="states" type="{http://spcoop.gov.it/sica/wsbl}statesType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="transitions" type="{http://spcoop.gov.it/sica/wsbl}transitionsType" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "ConceptualBehavior", 
  propOrder = {
  	"name",
  	"states",
  	"transitions"
  }
)

@XmlRootElement(name = "ConceptualBehavior")

public class ConceptualBehavior extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConceptualBehavior() {
    super();
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public StatesType getStates() {
    return this.states;
  }

  public void setStates(StatesType states) {
    this.states = states;
  }

  public TransitionsType getTransitions() {
    return this.transitions;
  }

  public void setTransitions(TransitionsType transitions) {
    this.transitions = transitions;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.spcoop.sica.wsbl.model.ConceptualBehaviorModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.spcoop.sica.wsbl.ConceptualBehavior.modelStaticInstance==null){
  			it.gov.spcoop.sica.wsbl.ConceptualBehavior.modelStaticInstance = new it.gov.spcoop.sica.wsbl.model.ConceptualBehaviorModel();
	  }
  }
  public static it.gov.spcoop.sica.wsbl.model.ConceptualBehaviorModel model(){
	  if(it.gov.spcoop.sica.wsbl.ConceptualBehavior.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.spcoop.sica.wsbl.ConceptualBehavior.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="name",required=true,nillable=false)
  protected java.lang.String name;

  @XmlElement(name="states",required=true,nillable=false)
  protected StatesType states;

  @XmlElement(name="transitions",required=true,nillable=false)
  protected TransitionsType transitions;

}

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for Input complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Input"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="conditions" type="{http://www.openspcoop2.org/protocol/information_missing}ConditionsType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/protocol/information_missing}Proprieta" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Input", 
  propOrder = {
  	"conditions",
  	"proprieta"
  }
)

@XmlRootElement(name = "Input")

public class Input extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Input() {
  }

  public ConditionsType getConditions() {
    return this.conditions;
  }

  public void setConditions(ConditionsType conditions) {
    this.conditions = conditions;
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="conditions",required=false,nillable=false)
  protected ConditionsType conditions;

  @XmlElement(name="proprieta",required=true,nillable=false)
  protected List<Proprieta> proprieta = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaList
   * @return List&lt;Proprieta&gt;
  */
  @Deprecated
  public List<Proprieta> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List&lt;Proprieta&gt;
  */
  @Deprecated
  public void setProprieta(List<Proprieta> proprieta) {
  	this.proprieta=proprieta;
  }

  /**
   * @deprecated Use method sizeProprietaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprieta() {
  	return this.proprieta.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=true)
  protected java.lang.String descrizione;

}

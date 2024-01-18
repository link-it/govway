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
package org.openspcoop2.core.registry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for resource-request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource-request"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="parameter" type="{http://www.openspcoop2.org/core/registry}resource-parameter" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="representation" type="{http://www.openspcoop2.org/core/registry}resource-representation" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="id-resource" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource-request", 
  propOrder = {
  	"parameter",
  	"representation"
  }
)

@XmlRootElement(name = "resource-request")

public class ResourceRequest extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ResourceRequest() {
    super();
  }

  public void addParameter(ResourceParameter parameter) {
    this.parameter.add(parameter);
  }

  public ResourceParameter getParameter(int index) {
    return this.parameter.get( index );
  }

  public ResourceParameter removeParameter(int index) {
    return this.parameter.remove( index );
  }

  public List<ResourceParameter> getParameterList() {
    return this.parameter;
  }

  public void setParameterList(List<ResourceParameter> parameter) {
    this.parameter=parameter;
  }

  public int sizeParameterList() {
    return this.parameter.size();
  }

  public void addRepresentation(ResourceRepresentation representation) {
    this.representation.add(representation);
  }

  public ResourceRepresentation getRepresentation(int index) {
    return this.representation.get( index );
  }

  public ResourceRepresentation removeRepresentation(int index) {
    return this.representation.remove( index );
  }

  public List<ResourceRepresentation> getRepresentationList() {
    return this.representation;
  }

  public void setRepresentationList(List<ResourceRepresentation> representation) {
    this.representation=representation;
  }

  public int sizeRepresentationList() {
    return this.representation.size();
  }

  public java.lang.Long getIdResource() {
    return this.idResource;
  }

  public void setIdResource(java.lang.Long idResource) {
    this.idResource = idResource;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="parameter",required=true,nillable=false)
  private List<ResourceParameter> parameter = new ArrayList<>();

  /**
   * Use method getParameterList
   * @return List&lt;ResourceParameter&gt;
  */
  public List<ResourceParameter> getParameter() {
  	return this.getParameterList();
  }

  /**
   * Use method setParameterList
   * @param parameter List&lt;ResourceParameter&gt;
  */
  public void setParameter(List<ResourceParameter> parameter) {
  	this.setParameterList(parameter);
  }

  /**
   * Use method sizeParameterList
   * @return lunghezza della lista
  */
  public int sizeParameter() {
  	return this.sizeParameterList();
  }

  @XmlElement(name="representation",required=true,nillable=false)
  private List<ResourceRepresentation> representation = new ArrayList<>();

  /**
   * Use method getRepresentationList
   * @return List&lt;ResourceRepresentation&gt;
  */
  public List<ResourceRepresentation> getRepresentation() {
  	return this.getRepresentationList();
  }

  /**
   * Use method setRepresentationList
   * @param representation List&lt;ResourceRepresentation&gt;
  */
  public void setRepresentation(List<ResourceRepresentation> representation) {
  	this.setRepresentationList(representation);
  }

  /**
   * Use method sizeRepresentationList
   * @return lunghezza della lista
  */
  public int sizeRepresentation() {
  	return this.sizeRepresentationList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.Long idResource;

}

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.FunctionType;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for Context complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Context"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="subContext" type="{http://www.openspcoop2.org/protocol/manifest}SubContextMapping" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="emptySubContext" type="{http://www.openspcoop2.org/protocol/manifest}EmptySubContextMapping" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="soapMediaTypeCollection" type="{http://www.openspcoop2.org/protocol/manifest}SoapMediaTypeCollection" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="restMediaTypeCollection" type="{http://www.openspcoop2.org/protocol/manifest}RestMediaTypeCollection" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="emptyFunction" type="{http://www.openspcoop2.org/protocol/manifest}FunctionType" use="optional"/&gt;
 * 		&lt;attribute name="binding" type="{http://www.openspcoop2.org/protocol/manifest}ServiceBinding" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Context", 
  propOrder = {
  	"subContext",
  	"emptySubContext",
  	"soapMediaTypeCollection",
  	"restMediaTypeCollection"
  }
)

@XmlRootElement(name = "Context")

public class Context extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Context() {
    super();
  }

  public void addSubContext(SubContextMapping subContext) {
    this.subContext.add(subContext);
  }

  public SubContextMapping getSubContext(int index) {
    return this.subContext.get( index );
  }

  public SubContextMapping removeSubContext(int index) {
    return this.subContext.remove( index );
  }

  public List<SubContextMapping> getSubContextList() {
    return this.subContext;
  }

  public void setSubContextList(List<SubContextMapping> subContext) {
    this.subContext=subContext;
  }

  public int sizeSubContextList() {
    return this.subContext.size();
  }

  public EmptySubContextMapping getEmptySubContext() {
    return this.emptySubContext;
  }

  public void setEmptySubContext(EmptySubContextMapping emptySubContext) {
    this.emptySubContext = emptySubContext;
  }

  public SoapMediaTypeCollection getSoapMediaTypeCollection() {
    return this.soapMediaTypeCollection;
  }

  public void setSoapMediaTypeCollection(SoapMediaTypeCollection soapMediaTypeCollection) {
    this.soapMediaTypeCollection = soapMediaTypeCollection;
  }

  public RestMediaTypeCollection getRestMediaTypeCollection() {
    return this.restMediaTypeCollection;
  }

  public void setRestMediaTypeCollection(RestMediaTypeCollection restMediaTypeCollection) {
    this.restMediaTypeCollection = restMediaTypeCollection;
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public void setEmptyFunctionRawEnumValue(String value) {
    this.emptyFunction = (FunctionType) FunctionType.toEnumConstantFromString(value);
  }

  public String getEmptyFunctionRawEnumValue() {
    if(this.emptyFunction == null){
    	return null;
    }else{
    	return this.emptyFunction.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.FunctionType getEmptyFunction() {
    return this.emptyFunction;
  }

  public void setEmptyFunction(org.openspcoop2.protocol.manifest.constants.FunctionType emptyFunction) {
    this.emptyFunction = emptyFunction;
  }

  public void setBindingRawEnumValue(String value) {
    this.binding = (ServiceBinding) ServiceBinding.toEnumConstantFromString(value);
  }

  public String getBindingRawEnumValue() {
    if(this.binding == null){
    	return null;
    }else{
    	return this.binding.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.ServiceBinding getBinding() {
    return this.binding;
  }

  public void setBinding(org.openspcoop2.protocol.manifest.constants.ServiceBinding binding) {
    this.binding = binding;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="subContext",required=true,nillable=false)
  private List<SubContextMapping> subContext = new ArrayList<>();

  /**
   * Use method getSubContextList
   * @return List&lt;SubContextMapping&gt;
  */
  public List<SubContextMapping> getSubContext() {
  	return this.getSubContextList();
  }

  /**
   * Use method setSubContextList
   * @param subContext List&lt;SubContextMapping&gt;
  */
  public void setSubContext(List<SubContextMapping> subContext) {
  	this.setSubContextList(subContext);
  }

  /**
   * Use method sizeSubContextList
   * @return lunghezza della lista
  */
  public int sizeSubContext() {
  	return this.sizeSubContextList();
  }

  @XmlElement(name="emptySubContext",required=false,nillable=false)
  protected EmptySubContextMapping emptySubContext;

  @XmlElement(name="soapMediaTypeCollection",required=false,nillable=false)
  protected SoapMediaTypeCollection soapMediaTypeCollection;

  @XmlElement(name="restMediaTypeCollection",required=false,nillable=false)
  protected RestMediaTypeCollection restMediaTypeCollection;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String emptyFunctionRawEnumValue;

  @XmlAttribute(name="emptyFunction",required=false)
  protected FunctionType emptyFunction;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String bindingRawEnumValue;

  @XmlAttribute(name="binding",required=false)
  protected ServiceBinding binding;

}

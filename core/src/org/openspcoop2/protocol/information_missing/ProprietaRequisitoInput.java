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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.information_missing.constants.ProprietaRequisitoInputType;
import java.io.Serializable;


/** <p>Java class for ProprietaRequisitoInput complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProprietaRequisitoInput"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="conditions" type="{http://www.openspcoop2.org/protocol/information_missing}ConditionsType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="header" type="{http://www.openspcoop2.org/protocol/information_missing}Description" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="footer" type="{http://www.openspcoop2.org/protocol/information_missing}Description" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/protocol/information_missing}ProprietaRequisitoInputType" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="use-in-delete" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="step-increment-condition" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="step-increment" type="{http://www.w3.org/2001/XMLSchema}int" use="optional" default="1"/&gt;
 * 		&lt;attribute name="reload-on-change" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProprietaRequisitoInput", 
  propOrder = {
  	"conditions",
  	"header",
  	"footer"
  }
)

@XmlRootElement(name = "ProprietaRequisitoInput")

public class ProprietaRequisitoInput extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ProprietaRequisitoInput() {
    super();
  }

  public ConditionsType getConditions() {
    return this.conditions;
  }

  public void setConditions(ConditionsType conditions) {
    this.conditions = conditions;
  }

  public Description getHeader() {
    return this.header;
  }

  public void setHeader(Description header) {
    this.header = header;
  }

  public Description getFooter() {
    return this.footer;
  }

  public void setFooter(Description footer) {
    this.footer = footer;
  }

  public void setTipoRawEnumValue(String value) {
    this.tipo = (ProprietaRequisitoInputType) ProprietaRequisitoInputType.toEnumConstantFromString(value);
  }

  public String getTipoRawEnumValue() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.protocol.information_missing.constants.ProprietaRequisitoInputType getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.protocol.information_missing.constants.ProprietaRequisitoInputType tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getLabel() {
    return this.label;
  }

  public void setLabel(java.lang.String label) {
    this.label = label;
  }

  public java.lang.String getDefault() {
    return this._default;
  }

  public void setDefault(java.lang.String _default) {
    this._default = _default;
  }

  public boolean isUseInDelete() {
    return this.useInDelete;
  }

  public boolean getUseInDelete() {
    return this.useInDelete;
  }

  public void setUseInDelete(boolean useInDelete) {
    this.useInDelete = useInDelete;
  }

  public java.lang.String getStepIncrementCondition() {
    return this.stepIncrementCondition;
  }

  public void setStepIncrementCondition(java.lang.String stepIncrementCondition) {
    this.stepIncrementCondition = stepIncrementCondition;
  }

  public int getStepIncrement() {
    return this.stepIncrement;
  }

  public void setStepIncrement(int stepIncrement) {
    this.stepIncrement = stepIncrement;
  }

  public boolean isReloadOnChange() {
    return this.reloadOnChange;
  }

  public boolean getReloadOnChange() {
    return this.reloadOnChange;
  }

  public void setReloadOnChange(boolean reloadOnChange) {
    this.reloadOnChange = reloadOnChange;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="conditions",required=false,nillable=false)
  protected ConditionsType conditions;

  @XmlElement(name="header",required=false,nillable=false)
  protected Description header;

  @XmlElement(name="footer",required=false,nillable=false)
  protected Description footer;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRawEnumValue;

  @XmlAttribute(name="tipo",required=true)
  protected ProprietaRequisitoInputType tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="label",required=false)
  protected java.lang.String label;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="default",required=false)
  protected java.lang.String _default;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="use-in-delete",required=false)
  protected boolean useInDelete = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="step-increment-condition",required=false)
  protected java.lang.String stepIncrementCondition;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="step-increment",required=false)
  protected int stepIncrement = 1;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="reload-on-change",required=false)
  protected boolean reloadOnChange = false;

}

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.core.statistiche;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for statistica-giornaliera-llm complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-giornaliera-llm"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="llm-provider" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="llm-model" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="llm-provider-binding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="token-input" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="token-output" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="cost-estimated" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statistica-giornaliera-llm", 
  propOrder = {
  	"data",
  	"llmProvider",
  	"llmModel",
  	"llmProviderBinding",
  	"tokenInput",
  	"tokenOutput",
  	"costEstimated"
  }
)

@XmlRootElement(name = "statistica-giornaliera-llm")

public class StatisticaGiornalieraLlm extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public StatisticaGiornalieraLlm() {
    super();
  }

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  public java.lang.String getLlmProvider() {
    return this.llmProvider;
  }

  public void setLlmProvider(java.lang.String llmProvider) {
    this.llmProvider = llmProvider;
  }

  public java.lang.String getLlmModel() {
    return this.llmModel;
  }

  public void setLlmModel(java.lang.String llmModel) {
    this.llmModel = llmModel;
  }

  public java.lang.String getLlmProviderBinding() {
    return this.llmProviderBinding;
  }

  public void setLlmProviderBinding(java.lang.String llmProviderBinding) {
    this.llmProviderBinding = llmProviderBinding;
  }

  public java.lang.Long getTokenInput() {
    return this.tokenInput;
  }

  public void setTokenInput(java.lang.Long tokenInput) {
    this.tokenInput = tokenInput;
  }

  public java.lang.Long getTokenOutput() {
    return this.tokenOutput;
  }

  public void setTokenOutput(java.lang.Long tokenOutput) {
    this.tokenOutput = tokenOutput;
  }

  public double getCostEstimated() {
    return this.costEstimated;
  }

  public void setCostEstimated(double costEstimated) {
    this.costEstimated = costEstimated;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date data;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="llm-provider",required=true,nillable=false)
  protected java.lang.String llmProvider;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="llm-model",required=true,nillable=false)
  protected java.lang.String llmModel;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="llm-provider-binding",required=false,nillable=false)
  protected java.lang.String llmProviderBinding;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="token-input",required=true,nillable=false)
  protected java.lang.Long tokenInput;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="token-output",required=true,nillable=false)
  protected java.lang.Long tokenOutput;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="double")
  @XmlElement(name="cost-estimated",required=false,nillable=false)
  protected double costEstimated;

}

/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for trasformazione-regola-applicabilita-risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazione-regola-applicabilita-risposta">
 * 		&lt;sequence>
 * 			&lt;element name="return-code-min" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="return-code-max" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="content-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "trasformazione-regola-applicabilita-risposta", 
  propOrder = {
  	"returnCodeMin",
  	"returnCodeMax",
  	"contentType",
  	"pattern"
  }
)

@XmlRootElement(name = "trasformazione-regola-applicabilita-risposta")

public class TrasformazioneRegolaApplicabilitaRisposta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TrasformazioneRegolaApplicabilitaRisposta() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public java.lang.Integer getReturnCodeMin() {
    return this.returnCodeMin;
  }

  public void setReturnCodeMin(java.lang.Integer returnCodeMin) {
    this.returnCodeMin = returnCodeMin;
  }

  public java.lang.Integer getReturnCodeMax() {
    return this.returnCodeMax;
  }

  public void setReturnCodeMax(java.lang.Integer returnCodeMax) {
    this.returnCodeMax = returnCodeMax;
  }

  public void addContentType(java.lang.String contentType) {
    this.contentType.add(contentType);
  }

  public java.lang.String getContentType(int index) {
    return this.contentType.get( index );
  }

  public java.lang.String removeContentType(int index) {
    return this.contentType.remove( index );
  }

  public List<java.lang.String> getContentTypeList() {
    return this.contentType;
  }

  public void setContentTypeList(List<java.lang.String> contentType) {
    this.contentType=contentType;
  }

  public int sizeContentTypeList() {
    return this.contentType.size();
  }

  public java.lang.String getPattern() {
    return this.pattern;
  }

  public void setPattern(java.lang.String pattern) {
    this.pattern = pattern;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="return-code-min",required=false,nillable=false)
  protected java.lang.Integer returnCodeMin;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="return-code-max",required=false,nillable=false)
  protected java.lang.Integer returnCodeMax;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="content-type",required=true,nillable=false)
  protected List<java.lang.String> contentType = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getContentTypeList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getContentType() {
  	return this.contentType;
  }

  /**
   * @deprecated Use method setContentTypeList
   * @param contentType List<java.lang.String>
  */
  @Deprecated
  public void setContentType(List<java.lang.String> contentType) {
  	this.contentType=contentType;
  }

  /**
   * @deprecated Use method sizeContentTypeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeContentType() {
  	return this.contentType.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="pattern",required=false,nillable=false)
  protected java.lang.String pattern;

}

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
import org.openspcoop2.protocol.information_missing.constants.PddType;
import org.openspcoop2.protocol.information_missing.constants.SoggettoReplaceType;
import java.io.Serializable;


/** <p>Java class for Soggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Soggetto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="conditions" type="{http://www.openspcoop2.org/protocol/information_missing}ConditionsType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="replace-match" type="{http://www.openspcoop2.org/protocol/information_missing}replaceMatchType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="header" type="{http://www.openspcoop2.org/protocol/information_missing}Description" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="footer" type="{http://www.openspcoop2.org/protocol/information_missing}Description" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="default" type="{http://www.openspcoop2.org/protocol/information_missing}Default" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/protocol/information_missing}SoggettoReplaceType" use="required"/&gt;
 * 		&lt;attribute name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipo-pdd" type="{http://www.openspcoop2.org/protocol/information_missing}pddType" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Soggetto", 
  propOrder = {
  	"conditions",
  	"replaceMatch",
  	"header",
  	"footer",
  	"_default"
  }
)

@XmlRootElement(name = "Soggetto")

public class Soggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Soggetto() {
    super();
  }

  public ConditionsType getConditions() {
    return this.conditions;
  }

  public void setConditions(ConditionsType conditions) {
    this.conditions = conditions;
  }

  public ReplaceMatchType getReplaceMatch() {
    return this.replaceMatch;
  }

  public void setReplaceMatch(ReplaceMatchType replaceMatch) {
    this.replaceMatch = replaceMatch;
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

  public Default getDefault() {
    return this._default;
  }

  public void setDefault(Default _default) {
    this._default = _default;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public void set_value_tipo(String value) {
    this.tipo = (SoggettoReplaceType) SoggettoReplaceType.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.protocol.information_missing.constants.SoggettoReplaceType getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.protocol.information_missing.constants.SoggettoReplaceType tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(java.lang.String protocollo) {
    this.protocollo = protocollo;
  }

  public void set_value_tipoPdd(String value) {
    this.tipoPdd = (PddType) PddType.toEnumConstantFromString(value);
  }

  public String get_value_tipoPdd() {
    if(this.tipoPdd == null){
    	return null;
    }else{
    	return this.tipoPdd.toString();
    }
  }

  public org.openspcoop2.protocol.information_missing.constants.PddType getTipoPdd() {
    return this.tipoPdd;
  }

  public void setTipoPdd(org.openspcoop2.protocol.information_missing.constants.PddType tipoPdd) {
    this.tipoPdd = tipoPdd;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="conditions",required=false,nillable=false)
  protected ConditionsType conditions;

  @XmlElement(name="replace-match",required=true,nillable=false)
  protected ReplaceMatchType replaceMatch;

  @XmlElement(name="header",required=false,nillable=false)
  protected Description header;

  @XmlElement(name="footer",required=false,nillable=false)
  protected Description footer;

  @XmlElement(name="default",required=false,nillable=false)
  protected Default _default;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=true)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=true)
  protected SoggettoReplaceType tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="protocollo",required=false)
  protected java.lang.String protocollo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoPdd;

  @XmlAttribute(name="tipo-pdd",required=false)
  protected PddType tipoPdd;

}

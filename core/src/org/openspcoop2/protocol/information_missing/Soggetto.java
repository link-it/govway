/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.information_missing.constants.PddType;
import org.openspcoop2.protocol.information_missing.constants.SoggettoReplaceType;
import java.io.Serializable;


/** <p>Java class for Soggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Soggetto">
 * 		&lt;sequence>
 * 			&lt;element name="replace-match" type="{http://www.openspcoop2.org/protocol/information_missing}replaceMatchType" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/protocol/information_missing}SoggettoReplaceType" use="required"/>
 * 		&lt;attribute name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipo-pdd" type="{http://www.openspcoop2.org/protocol/information_missing}pddType" use="optional"/>
 * &lt;/complexType>
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
  	"replaceMatch"
  }
)

@XmlRootElement(name = "Soggetto")

public class Soggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Soggetto() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public ReplaceMatchType getReplaceMatch() {
    return this.replaceMatch;
  }

  public void setReplaceMatch(ReplaceMatchType replaceMatch) {
    this.replaceMatch = replaceMatch;
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

  @XmlTransient
  private Long id;



  @XmlElement(name="replace-match",required=true,nillable=false)
  protected ReplaceMatchType replaceMatch;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=true)
  protected java.lang.String descrizione;

  @XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=true)
  protected SoggettoReplaceType tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="protocollo",required=false)
  protected java.lang.String protocollo;

  @XmlTransient
  protected java.lang.String _value_tipoPdd;

  @XmlAttribute(name="tipo-pdd",required=false)
  protected PddType tipoPdd;

}

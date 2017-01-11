/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eccezione.errore_applicativo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD;
import java.io.Serializable;


/** <p>Java class for dominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dominio">
 * 		&lt;sequence>
 * 			&lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/eccezione/errore_applicativo}dominio-soggetto" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="funzione" type="{http://www.openspcoop2.org/core/eccezione/errore_applicativo}TipoPdD" use="optional"/>
 * 		&lt;attribute name="modulo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dominio", 
  propOrder = {
  	"identificativoPorta",
  	"soggetto"
  }
)

@XmlRootElement(name = "dominio")

public class Dominio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Dominio() {
  }

  public java.lang.String getIdentificativoPorta() {
    return this.identificativoPorta;
  }

  public void setIdentificativoPorta(java.lang.String identificativoPorta) {
    this.identificativoPorta = identificativoPorta;
  }

  public DominioSoggetto getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(DominioSoggetto soggetto) {
    this.soggetto = soggetto;
  }

  public void set_value_funzione(String value) {
    this.funzione = (TipoPdD) TipoPdD.toEnumConstantFromString(value);
  }

  public String get_value_funzione() {
    if(this.funzione == null){
    	return null;
    }else{
    	return this.funzione.toString();
    }
  }

  public org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD getFunzione() {
    return this.funzione;
  }

  public void setFunzione(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD funzione) {
    this.funzione = funzione;
  }

  public java.lang.String getModulo() {
    return this.modulo;
  }

  public void setModulo(java.lang.String modulo) {
    this.modulo = modulo;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-porta",required=true,nillable=false)
  protected java.lang.String identificativoPorta;

  @XmlElement(name="soggetto",required=true,nillable=false)
  protected DominioSoggetto soggetto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_funzione;

  @XmlAttribute(name="funzione",required=false)
  protected TipoPdD funzione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="modulo",required=false)
  protected java.lang.String modulo;

}

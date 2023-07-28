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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import java.io.Serializable;


/** <p>Java class for gestione-errore-codice-trasporto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-errore-codice-trasporto"&gt;
 * 		&lt;attribute name="valore-minimo" type="{http://www.w3.org/2001/XMLSchema}integer" use="optional"/&gt;
 * 		&lt;attribute name="valore-massimo" type="{http://www.w3.org/2001/XMLSchema}integer" use="optional"/&gt;
 * 		&lt;attribute name="comportamento" type="{http://www.openspcoop2.org/core/config}GestioneErroreComportamento" use="required"/&gt;
 * 		&lt;attribute name="cadenza-rispedizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gestione-errore-codice-trasporto")

@XmlRootElement(name = "gestione-errore-codice-trasporto")

public class GestioneErroreCodiceTrasporto extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public GestioneErroreCodiceTrasporto() {
    super();
  }

  public java.lang.Integer getValoreMinimo() {
    return this.valoreMinimo;
  }

  public void setValoreMinimo(java.lang.Integer valoreMinimo) {
    this.valoreMinimo = valoreMinimo;
  }

  public java.lang.Integer getValoreMassimo() {
    return this.valoreMassimo;
  }

  public void setValoreMassimo(java.lang.Integer valoreMassimo) {
    this.valoreMassimo = valoreMassimo;
  }

  public void setComportamentoRawEnumValue(String value) {
    this.comportamento = (GestioneErroreComportamento) GestioneErroreComportamento.toEnumConstantFromString(value);
  }

  public String getComportamentoRawEnumValue() {
    if(this.comportamento == null){
    	return null;
    }else{
    	return this.comportamento.toString();
    }
  }

  public org.openspcoop2.core.config.constants.GestioneErroreComportamento getComportamento() {
    return this.comportamento;
  }

  public void setComportamento(org.openspcoop2.core.config.constants.GestioneErroreComportamento comportamento) {
    this.comportamento = comportamento;
  }

  public java.lang.String getCadenzaRispedizione() {
    return this.cadenzaRispedizione;
  }

  public void setCadenzaRispedizione(java.lang.String cadenzaRispedizione) {
    this.cadenzaRispedizione = cadenzaRispedizione;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="valore-minimo",required=false)
  protected java.lang.Integer valoreMinimo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="valore-massimo",required=false)
  protected java.lang.Integer valoreMassimo;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String comportamentoRawEnumValue;

  @XmlAttribute(name="comportamento",required=true)
  protected GestioneErroreComportamento comportamento;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cadenza-rispedizione",required=false)
  protected java.lang.String cadenzaRispedizione;

}

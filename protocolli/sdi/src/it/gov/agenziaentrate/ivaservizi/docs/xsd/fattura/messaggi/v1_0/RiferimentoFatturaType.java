/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for RiferimentoFattura_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RiferimentoFattura_Type"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="NumeroFattura" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="AnnoFattura" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fattura/messaggi/v1.0}nonNegativeInteger" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="PosizioneFattura" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RiferimentoFattura_Type", 
  propOrder = {
  	"numeroFattura",
  	"annoFattura",
  	"posizioneFattura"
  }
)

@XmlRootElement(name = "RiferimentoFattura_Type")

public class RiferimentoFatturaType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RiferimentoFatturaType() {
  }

  public java.lang.String getNumeroFattura() {
    return this.numeroFattura;
  }

  public void setNumeroFattura(java.lang.String numeroFattura) {
    this.numeroFattura = numeroFattura;
  }

  public java.math.BigInteger getAnnoFattura() {
    return this.annoFattura;
  }

  public void setAnnoFattura(java.math.BigInteger annoFattura) {
    this.annoFattura = annoFattura;
  }

  public java.math.BigInteger getPosizioneFattura() {
    return this.posizioneFattura;
  }

  public void setPosizioneFattura(java.math.BigInteger posizioneFattura) {
    this.posizioneFattura = posizioneFattura;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroFattura",required=true,nillable=false)
  protected java.lang.String numeroFattura;

  @javax.xml.bind.annotation.XmlSchemaType(name="nonNegativeInteger")
  @XmlElement(name="AnnoFattura",required=true,nillable=false)
  protected java.math.BigInteger annoFattura;

  @javax.xml.bind.annotation.XmlSchemaType(name="positiveInteger")
  @XmlElement(name="PosizioneFattura",required=false,nillable=false)
  protected java.math.BigInteger posizioneFattura;

}

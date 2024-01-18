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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiFatturaRettificataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiFatturaRettificataType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="NumeroFR" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DataFR" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}date" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ElementiRettificati" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiFatturaRettificataType", 
  propOrder = {
  	"numeroFR",
  	"dataFR",
  	"elementiRettificati"
  }
)

@XmlRootElement(name = "DatiFatturaRettificataType")

public class DatiFatturaRettificataType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiFatturaRettificataType() {
    super();
  }

  public java.lang.String getNumeroFR() {
    return this.numeroFR;
  }

  public void setNumeroFR(java.lang.String numeroFR) {
    this.numeroFR = numeroFR;
  }

  public java.util.Date getDataFR() {
    return this.dataFR;
  }

  public void setDataFR(java.util.Date dataFR) {
    this.dataFR = dataFR;
  }

  public java.lang.String getElementiRettificati() {
    return this.elementiRettificati;
  }

  public void setElementiRettificati(java.lang.String elementiRettificati) {
    this.elementiRettificati = elementiRettificati;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroFR",required=true,nillable=false)
  protected java.lang.String numeroFR;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataFR",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataFR;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="ElementiRettificati",required=true,nillable=false)
  protected java.lang.String elementiRettificati;

}

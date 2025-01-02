/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiDDTType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiDDTType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="NumeroDDT" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DataDDT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="RiferimentoNumeroLinea" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}integer" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "DatiDDTType", 
  propOrder = {
  	"numeroDDT",
  	"dataDDT",
  	"riferimentoNumeroLinea"
  }
)

@XmlRootElement(name = "DatiDDTType")

public class DatiDDTType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiDDTType() {
    super();
  }

  public java.lang.String getNumeroDDT() {
    return this.numeroDDT;
  }

  public void setNumeroDDT(java.lang.String numeroDDT) {
    this.numeroDDT = numeroDDT;
  }

  public java.util.Date getDataDDT() {
    return this.dataDDT;
  }

  public void setDataDDT(java.util.Date dataDDT) {
    this.dataDDT = dataDDT;
  }

  public void addRiferimentoNumeroLinea(java.math.BigInteger riferimentoNumeroLinea) {
    this.riferimentoNumeroLinea.add(riferimentoNumeroLinea);
  }

  public java.math.BigInteger getRiferimentoNumeroLinea(int index) {
    return this.riferimentoNumeroLinea.get( index );
  }

  public java.math.BigInteger removeRiferimentoNumeroLinea(int index) {
    return this.riferimentoNumeroLinea.remove( index );
  }

  public List<java.math.BigInteger> getRiferimentoNumeroLineaList() {
    return this.riferimentoNumeroLinea;
  }

  public void setRiferimentoNumeroLineaList(List<java.math.BigInteger> riferimentoNumeroLinea) {
    this.riferimentoNumeroLinea=riferimentoNumeroLinea;
  }

  public int sizeRiferimentoNumeroLineaList() {
    return this.riferimentoNumeroLinea.size();
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroDDT",required=true,nillable=false)
  protected java.lang.String numeroDDT;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataDDT",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataDDT;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="RiferimentoNumeroLinea",required=true,nillable=false)
  private List<java.math.BigInteger> riferimentoNumeroLinea = new ArrayList<>();

  /**
   * Use method getRiferimentoNumeroLineaList
   * @return List&lt;java.math.BigInteger&gt;
  */
  public List<java.math.BigInteger> getRiferimentoNumeroLinea() {
  	return this.getRiferimentoNumeroLineaList();
  }

  /**
   * Use method setRiferimentoNumeroLineaList
   * @param riferimentoNumeroLinea List&lt;java.math.BigInteger&gt;
  */
  public void setRiferimentoNumeroLinea(List<java.math.BigInteger> riferimentoNumeroLinea) {
  	this.setRiferimentoNumeroLineaList(riferimentoNumeroLinea);
  }

  /**
   * Use method sizeRiferimentoNumeroLineaList
   * @return lunghezza della lista
  */
  public int sizeRiferimentoNumeroLinea() {
  	return this.sizeRiferimentoNumeroLineaList();
  }

}

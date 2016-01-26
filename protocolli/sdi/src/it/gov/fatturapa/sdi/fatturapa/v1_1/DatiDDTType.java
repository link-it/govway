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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiDDTType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiDDTType">
 * 		&lt;sequence>
 * 			&lt;element name="NumeroDDT" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="DataDDT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoNumeroLinea" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}integer" minOccurs="0" maxOccurs="unbounded"/>
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

  public void addRiferimentoNumeroLinea(java.lang.Integer riferimentoNumeroLinea) {
    this.riferimentoNumeroLinea.add(riferimentoNumeroLinea);
  }

  public java.lang.Integer getRiferimentoNumeroLinea(int index) {
    return this.riferimentoNumeroLinea.get( index );
  }

  public java.lang.Integer removeRiferimentoNumeroLinea(int index) {
    return this.riferimentoNumeroLinea.remove( index );
  }

  public List<java.lang.Integer> getRiferimentoNumeroLineaList() {
    return this.riferimentoNumeroLinea;
  }

  public void setRiferimentoNumeroLineaList(List<java.lang.Integer> riferimentoNumeroLinea) {
    this.riferimentoNumeroLinea=riferimentoNumeroLinea;
  }

  public int sizeRiferimentoNumeroLineaList() {
    return this.riferimentoNumeroLinea.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroDDT",required=true,nillable=false)
  protected java.lang.String numeroDDT;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataDDT",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataDDT;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="RiferimentoNumeroLinea",required=true,nillable=false)
  protected List<java.lang.Integer> riferimentoNumeroLinea = new ArrayList<java.lang.Integer>();

  /**
   * @deprecated Use method getRiferimentoNumeroLineaList
   * @return List<java.lang.Integer>
  */
  @Deprecated
  public List<java.lang.Integer> getRiferimentoNumeroLinea() {
  	return this.riferimentoNumeroLinea;
  }

  /**
   * @deprecated Use method setRiferimentoNumeroLineaList
   * @param riferimentoNumeroLinea List<java.lang.Integer>
  */
  @Deprecated
  public void setRiferimentoNumeroLinea(List<java.lang.Integer> riferimentoNumeroLinea) {
  	this.riferimentoNumeroLinea=riferimentoNumeroLinea;
  }

  /**
   * @deprecated Use method sizeRiferimentoNumeroLineaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRiferimentoNumeroLinea() {
  	return this.riferimentoNumeroLinea.size();
  }

}

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


/** <p>Java class for DatiDocumentiCorrelatiType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiDocumentiCorrelatiType">
 * 		&lt;sequence>
 * 			&lt;element name="RiferimentoNumeroLinea" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}integer" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="IdDocumento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Data" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="NumItem" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CodiceCommessaConvenzione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CodiceCUP" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CodiceCIG" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "DatiDocumentiCorrelatiType", 
  propOrder = {
  	"riferimentoNumeroLinea",
  	"idDocumento",
  	"data",
  	"numItem",
  	"codiceCommessaConvenzione",
  	"codiceCUP",
  	"codiceCIG"
  }
)

@XmlRootElement(name = "DatiDocumentiCorrelatiType")

public class DatiDocumentiCorrelatiType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiDocumentiCorrelatiType() {
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

  public java.lang.String getIdDocumento() {
    return this.idDocumento;
  }

  public void setIdDocumento(java.lang.String idDocumento) {
    this.idDocumento = idDocumento;
  }

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  public java.lang.String getNumItem() {
    return this.numItem;
  }

  public void setNumItem(java.lang.String numItem) {
    this.numItem = numItem;
  }

  public java.lang.String getCodiceCommessaConvenzione() {
    return this.codiceCommessaConvenzione;
  }

  public void setCodiceCommessaConvenzione(java.lang.String codiceCommessaConvenzione) {
    this.codiceCommessaConvenzione = codiceCommessaConvenzione;
  }

  public java.lang.String getCodiceCUP() {
    return this.codiceCUP;
  }

  public void setCodiceCUP(java.lang.String codiceCUP) {
    this.codiceCUP = codiceCUP;
  }

  public java.lang.String getCodiceCIG() {
    return this.codiceCIG;
  }

  public void setCodiceCIG(java.lang.String codiceCIG) {
    this.codiceCIG = codiceCIG;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



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

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="IdDocumento",required=true,nillable=false)
  protected java.lang.String idDocumento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="Data",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date data;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumItem",required=false,nillable=false)
  protected java.lang.String numItem;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="CodiceCommessaConvenzione",required=false,nillable=false)
  protected java.lang.String codiceCommessaConvenzione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="CodiceCUP",required=false,nillable=false)
  protected java.lang.String codiceCUP;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="CodiceCIG",required=false,nillable=false)
  protected java.lang.String codiceCIG;

}

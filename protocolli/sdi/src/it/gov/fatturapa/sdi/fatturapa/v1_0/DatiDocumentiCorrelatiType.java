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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiDocumentiCorrelatiType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiDocumentiCorrelatiType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="RiferimentoNumeroLinea" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}integer" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="IdDocumento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Data" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="NumItem" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="CodiceCommessaConvenzione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="CodiceCUP" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="CodiceCIG" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
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
    super();
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



  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
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

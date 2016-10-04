/*
 * OpenSPCoop - Customizable API Gateway 
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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiAnagraficiVettoreType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiAnagraficiVettoreType">
 * 		&lt;sequence>
 * 			&lt;element name="IdFiscaleIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}IdFiscaleType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CodiceFiscale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Anagrafica" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}AnagraficaType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NumeroLicenzaGuida" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "DatiAnagraficiVettoreType", 
  propOrder = {
  	"idFiscaleIVA",
  	"codiceFiscale",
  	"anagrafica",
  	"numeroLicenzaGuida"
  }
)

@XmlRootElement(name = "DatiAnagraficiVettoreType")

public class DatiAnagraficiVettoreType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiAnagraficiVettoreType() {
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

  public IdFiscaleType getIdFiscaleIVA() {
    return this.idFiscaleIVA;
  }

  public void setIdFiscaleIVA(IdFiscaleType idFiscaleIVA) {
    this.idFiscaleIVA = idFiscaleIVA;
  }

  public java.lang.String getCodiceFiscale() {
    return this.codiceFiscale;
  }

  public void setCodiceFiscale(java.lang.String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
  }

  public AnagraficaType getAnagrafica() {
    return this.anagrafica;
  }

  public void setAnagrafica(AnagraficaType anagrafica) {
    this.anagrafica = anagrafica;
  }

  public java.lang.String getNumeroLicenzaGuida() {
    return this.numeroLicenzaGuida;
  }

  public void setNumeroLicenzaGuida(java.lang.String numeroLicenzaGuida) {
    this.numeroLicenzaGuida = numeroLicenzaGuida;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="IdFiscaleIVA",required=true,nillable=false)
  protected IdFiscaleType idFiscaleIVA;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CodiceFiscale",required=false,nillable=false)
  protected java.lang.String codiceFiscale;

  @XmlElement(name="Anagrafica",required=true,nillable=false)
  protected AnagraficaType anagrafica;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroLicenzaGuida",required=false,nillable=false)
  protected java.lang.String numeroLicenzaGuida;

}

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

import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.Art73Type;
import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoDocumentoType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiGeneraliDocumentoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiGeneraliDocumentoType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="TipoDocumento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}TipoDocumentoType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Divisa" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Data" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Numero" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiRitenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiRitenutaType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiBollo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiBolloType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiCassaPrevidenziale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiCassaPrevidenzialeType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="ScontoMaggiorazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}ScontoMaggiorazioneType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="ImportoTotaleDocumento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Arrotondamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Causale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Art73" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}Art73Type" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiGeneraliDocumentoType", 
  propOrder = {
  	"tipoDocumento",
  	"divisa",
  	"data",
  	"numero",
  	"datiRitenuta",
  	"datiBollo",
  	"datiCassaPrevidenziale",
  	"scontoMaggiorazione",
  	"_decimalWrapper_importoTotaleDocumento",
  	"_decimalWrapper_arrotondamento",
  	"causale",
  	"art73"
  }
)

@XmlRootElement(name = "DatiGeneraliDocumentoType")

public class DatiGeneraliDocumentoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiGeneraliDocumentoType() {
    super();
  }

  public void set_value_tipoDocumento(String value) {
    this.tipoDocumento = (TipoDocumentoType) TipoDocumentoType.toEnumConstantFromString(value);
  }

  public String get_value_tipoDocumento() {
    if(this.tipoDocumento == null){
    	return null;
    }else{
    	return this.tipoDocumento.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoDocumentoType getTipoDocumento() {
    return this.tipoDocumento;
  }

  public void setTipoDocumento(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoDocumentoType tipoDocumento) {
    this.tipoDocumento = tipoDocumento;
  }

  public java.lang.String getDivisa() {
    return this.divisa;
  }

  public void setDivisa(java.lang.String divisa) {
    this.divisa = divisa;
  }

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  public java.lang.String getNumero() {
    return this.numero;
  }

  public void setNumero(java.lang.String numero) {
    this.numero = numero;
  }

  public DatiRitenutaType getDatiRitenuta() {
    return this.datiRitenuta;
  }

  public void setDatiRitenuta(DatiRitenutaType datiRitenuta) {
    this.datiRitenuta = datiRitenuta;
  }

  public DatiBolloType getDatiBollo() {
    return this.datiBollo;
  }

  public void setDatiBollo(DatiBolloType datiBollo) {
    this.datiBollo = datiBollo;
  }

  public void addDatiCassaPrevidenziale(DatiCassaPrevidenzialeType datiCassaPrevidenziale) {
    this.datiCassaPrevidenziale.add(datiCassaPrevidenziale);
  }

  public DatiCassaPrevidenzialeType getDatiCassaPrevidenziale(int index) {
    return this.datiCassaPrevidenziale.get( index );
  }

  public DatiCassaPrevidenzialeType removeDatiCassaPrevidenziale(int index) {
    return this.datiCassaPrevidenziale.remove( index );
  }

  public List<DatiCassaPrevidenzialeType> getDatiCassaPrevidenzialeList() {
    return this.datiCassaPrevidenziale;
  }

  public void setDatiCassaPrevidenzialeList(List<DatiCassaPrevidenzialeType> datiCassaPrevidenziale) {
    this.datiCassaPrevidenziale=datiCassaPrevidenziale;
  }

  public int sizeDatiCassaPrevidenzialeList() {
    return this.datiCassaPrevidenziale.size();
  }

  public void addScontoMaggiorazione(ScontoMaggiorazioneType scontoMaggiorazione) {
    this.scontoMaggiorazione.add(scontoMaggiorazione);
  }

  public ScontoMaggiorazioneType getScontoMaggiorazione(int index) {
    return this.scontoMaggiorazione.get( index );
  }

  public ScontoMaggiorazioneType removeScontoMaggiorazione(int index) {
    return this.scontoMaggiorazione.remove( index );
  }

  public List<ScontoMaggiorazioneType> getScontoMaggiorazioneList() {
    return this.scontoMaggiorazione;
  }

  public void setScontoMaggiorazioneList(List<ScontoMaggiorazioneType> scontoMaggiorazione) {
    this.scontoMaggiorazione=scontoMaggiorazione;
  }

  public int sizeScontoMaggiorazioneList() {
    return this.scontoMaggiorazione.size();
  }

  public java.math.BigDecimal getImportoTotaleDocumento() {
    if(this._decimalWrapper_importoTotaleDocumento!=null){
		return (java.math.BigDecimal) this._decimalWrapper_importoTotaleDocumento.getObject(java.math.BigDecimal.class);
	}else{
		return this.importoTotaleDocumento;
	}
  }

  public void setImportoTotaleDocumento(java.math.BigDecimal importoTotaleDocumento) {
    if(importoTotaleDocumento!=null){
		this._decimalWrapper_importoTotaleDocumento = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importoTotaleDocumento);
	}
  }

  public java.math.BigDecimal getArrotondamento() {
    if(this._decimalWrapper_arrotondamento!=null){
		return (java.math.BigDecimal) this._decimalWrapper_arrotondamento.getObject(java.math.BigDecimal.class);
	}else{
		return this.arrotondamento;
	}
  }

  public void setArrotondamento(java.math.BigDecimal arrotondamento) {
    if(arrotondamento!=null){
		this._decimalWrapper_arrotondamento = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,arrotondamento);
	}
  }

  public java.lang.String getCausale() {
    return this.causale;
  }

  public void setCausale(java.lang.String causale) {
    this.causale = causale;
  }

  public void set_value_art73(String value) {
    this.art73 = (Art73Type) Art73Type.toEnumConstantFromString(value);
  }

  public String get_value_art73() {
    if(this.art73 == null){
    	return null;
    }else{
    	return this.art73.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.Art73Type getArt73() {
    return this.art73;
  }

  public void setArt73(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.Art73Type art73) {
    this.art73 = art73;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoDocumento;

  @XmlElement(name="TipoDocumento",required=true,nillable=false)
  protected TipoDocumentoType tipoDocumento;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Divisa",required=true,nillable=false)
  protected java.lang.String divisa;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="Data",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date data;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Numero",required=true,nillable=false)
  protected java.lang.String numero;

  @XmlElement(name="DatiRitenuta",required=false,nillable=false)
  protected DatiRitenutaType datiRitenuta;

  @XmlElement(name="DatiBollo",required=false,nillable=false)
  protected DatiBolloType datiBollo;

  @XmlElement(name="DatiCassaPrevidenziale",required=true,nillable=false)
  protected List<DatiCassaPrevidenzialeType> datiCassaPrevidenziale = new ArrayList<DatiCassaPrevidenzialeType>();

  /**
   * @deprecated Use method getDatiCassaPrevidenzialeList
   * @return List&lt;DatiCassaPrevidenzialeType&gt;
  */
  @Deprecated
  public List<DatiCassaPrevidenzialeType> getDatiCassaPrevidenziale() {
  	return this.datiCassaPrevidenziale;
  }

  /**
   * @deprecated Use method setDatiCassaPrevidenzialeList
   * @param datiCassaPrevidenziale List&lt;DatiCassaPrevidenzialeType&gt;
  */
  @Deprecated
  public void setDatiCassaPrevidenziale(List<DatiCassaPrevidenzialeType> datiCassaPrevidenziale) {
  	this.datiCassaPrevidenziale=datiCassaPrevidenziale;
  }

  /**
   * @deprecated Use method sizeDatiCassaPrevidenzialeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiCassaPrevidenziale() {
  	return this.datiCassaPrevidenziale.size();
  }

  @XmlElement(name="ScontoMaggiorazione",required=true,nillable=false)
  protected List<ScontoMaggiorazioneType> scontoMaggiorazione = new ArrayList<ScontoMaggiorazioneType>();

  /**
   * @deprecated Use method getScontoMaggiorazioneList
   * @return List&lt;ScontoMaggiorazioneType&gt;
  */
  @Deprecated
  public List<ScontoMaggiorazioneType> getScontoMaggiorazione() {
  	return this.scontoMaggiorazione;
  }

  /**
   * @deprecated Use method setScontoMaggiorazioneList
   * @param scontoMaggiorazione List&lt;ScontoMaggiorazioneType&gt;
  */
  @Deprecated
  public void setScontoMaggiorazione(List<ScontoMaggiorazioneType> scontoMaggiorazione) {
  	this.scontoMaggiorazione=scontoMaggiorazione;
  }

  /**
   * @deprecated Use method sizeScontoMaggiorazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeScontoMaggiorazione() {
  	return this.scontoMaggiorazione.size();
  }

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoTotaleDocumento",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoTotaleDocumento = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importoTotaleDocumento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Arrotondamento",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_arrotondamento = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal arrotondamento;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Causale",required=false,nillable=false)
  protected java.lang.String causale;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_art73;

  @XmlElement(name="Art73",required=false,nillable=false)
  protected Art73Type art73;

}

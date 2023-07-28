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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.Art73Type;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.TipoDocumentoType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
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
 * 			&lt;element name="TipoDocumento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}TipoDocumentoType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Divisa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Data" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}date" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Numero" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiRitenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiRitenutaType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="DatiBollo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiBolloType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiCassaPrevidenziale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiCassaPrevidenzialeType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="ScontoMaggiorazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}ScontoMaggiorazioneType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="ImportoTotaleDocumento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Arrotondamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Causale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}normalizedString" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="Art73" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Art73Type" minOccurs="0" maxOccurs="1"/&gt;
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

  public void setTipoDocumentoRawEnumValue(String value) {
    this.tipoDocumento = (TipoDocumentoType) TipoDocumentoType.toEnumConstantFromString(value);
  }

  public String getTipoDocumentoRawEnumValue() {
    if(this.tipoDocumento == null){
    	return null;
    }else{
    	return this.tipoDocumento.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.TipoDocumentoType getTipoDocumento() {
    return this.tipoDocumento;
  }

  public void setTipoDocumento(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.TipoDocumentoType tipoDocumento) {
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

  public void addDatiRitenuta(DatiRitenutaType datiRitenuta) {
    this.datiRitenuta.add(datiRitenuta);
  }

  public DatiRitenutaType getDatiRitenuta(int index) {
    return this.datiRitenuta.get( index );
  }

  public DatiRitenutaType removeDatiRitenuta(int index) {
    return this.datiRitenuta.remove( index );
  }

  public List<DatiRitenutaType> getDatiRitenutaList() {
    return this.datiRitenuta;
  }

  public void setDatiRitenutaList(List<DatiRitenutaType> datiRitenuta) {
    this.datiRitenuta=datiRitenuta;
  }

  public int sizeDatiRitenutaList() {
    return this.datiRitenuta.size();
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

  public void addCausale(java.lang.String causale) {
    this.causale.add(causale);
  }

  public java.lang.String getCausale(int index) {
    return this.causale.get( index );
  }

  public java.lang.String removeCausale(int index) {
    return this.causale.remove( index );
  }

  public List<java.lang.String> getCausaleList() {
    return this.causale;
  }

  public void setCausaleList(List<java.lang.String> causale) {
    this.causale=causale;
  }

  public int sizeCausaleList() {
    return this.causale.size();
  }

  public void setArt73RawEnumValue(String value) {
    this.art73 = (Art73Type) Art73Type.toEnumConstantFromString(value);
  }

  public String getArt73RawEnumValue() {
    if(this.art73 == null){
    	return null;
    }else{
    	return this.art73.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.Art73Type getArt73() {
    return this.art73;
  }

  public void setArt73(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.Art73Type art73) {
    this.art73 = art73;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoDocumentoRawEnumValue;

  @XmlElement(name="TipoDocumento",required=true,nillable=false)
  protected TipoDocumentoType tipoDocumento;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Divisa",required=true,nillable=false)
  protected java.lang.String divisa;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="Data",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date data;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Numero",required=true,nillable=false)
  protected java.lang.String numero;

  @XmlElement(name="DatiRitenuta",required=true,nillable=false)
  private List<DatiRitenutaType> datiRitenuta = new ArrayList<>();

  /**
   * Use method getDatiRitenutaList
   * @return List&lt;DatiRitenutaType&gt;
  */
  public List<DatiRitenutaType> getDatiRitenuta() {
  	return this.getDatiRitenutaList();
  }

  /**
   * Use method setDatiRitenutaList
   * @param datiRitenuta List&lt;DatiRitenutaType&gt;
  */
  public void setDatiRitenuta(List<DatiRitenutaType> datiRitenuta) {
  	this.setDatiRitenutaList(datiRitenuta);
  }

  /**
   * Use method sizeDatiRitenutaList
   * @return lunghezza della lista
  */
  public int sizeDatiRitenuta() {
  	return this.sizeDatiRitenutaList();
  }

  @XmlElement(name="DatiBollo",required=false,nillable=false)
  protected DatiBolloType datiBollo;

  @XmlElement(name="DatiCassaPrevidenziale",required=true,nillable=false)
  private List<DatiCassaPrevidenzialeType> datiCassaPrevidenziale = new ArrayList<>();

  /**
   * Use method getDatiCassaPrevidenzialeList
   * @return List&lt;DatiCassaPrevidenzialeType&gt;
  */
  public List<DatiCassaPrevidenzialeType> getDatiCassaPrevidenziale() {
  	return this.getDatiCassaPrevidenzialeList();
  }

  /**
   * Use method setDatiCassaPrevidenzialeList
   * @param datiCassaPrevidenziale List&lt;DatiCassaPrevidenzialeType&gt;
  */
  public void setDatiCassaPrevidenziale(List<DatiCassaPrevidenzialeType> datiCassaPrevidenziale) {
  	this.setDatiCassaPrevidenzialeList(datiCassaPrevidenziale);
  }

  /**
   * Use method sizeDatiCassaPrevidenzialeList
   * @return lunghezza della lista
  */
  public int sizeDatiCassaPrevidenziale() {
  	return this.sizeDatiCassaPrevidenzialeList();
  }

  @XmlElement(name="ScontoMaggiorazione",required=true,nillable=false)
  private List<ScontoMaggiorazioneType> scontoMaggiorazione = new ArrayList<>();

  /**
   * Use method getScontoMaggiorazioneList
   * @return List&lt;ScontoMaggiorazioneType&gt;
  */
  public List<ScontoMaggiorazioneType> getScontoMaggiorazione() {
  	return this.getScontoMaggiorazioneList();
  }

  /**
   * Use method setScontoMaggiorazioneList
   * @param scontoMaggiorazione List&lt;ScontoMaggiorazioneType&gt;
  */
  public void setScontoMaggiorazione(List<ScontoMaggiorazioneType> scontoMaggiorazione) {
  	this.setScontoMaggiorazioneList(scontoMaggiorazione);
  }

  /**
   * Use method sizeScontoMaggiorazioneList
   * @return lunghezza della lista
  */
  public int sizeScontoMaggiorazione() {
  	return this.sizeScontoMaggiorazioneList();
  }

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoTotaleDocumento",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoTotaleDocumento = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importoTotaleDocumento;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Arrotondamento",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_arrotondamento = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal arrotondamento;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Causale",required=true,nillable=false)
  private List<java.lang.String> causale = new ArrayList<>();

  /**
   * Use method getCausaleList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getCausale() {
  	return this.getCausaleList();
  }

  /**
   * Use method setCausaleList
   * @param causale List&lt;java.lang.String&gt;
  */
  public void setCausale(List<java.lang.String> causale) {
  	this.setCausaleList(causale);
  }

  /**
   * Use method sizeCausaleList
   * @return lunghezza della lista
  */
  public int sizeCausale() {
  	return this.sizeCausaleList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String art73RawEnumValue;

  @XmlElement(name="Art73",required=false,nillable=false)
  protected Art73Type art73;

}

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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.NaturaType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.RitenutaType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoCessionePrestazioneType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DettaglioLineeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DettaglioLineeType">
 * 		&lt;sequence>
 * 			&lt;element name="NumeroLinea" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}integer" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="TipoCessionePrestazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}TipoCessionePrestazioneType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CodiceArticolo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}CodiceArticoloType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="Descrizione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Quantita" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="UnitaMisura" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="DataInizioPeriodo" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="DataFinePeriodo" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="PrezzoUnitario" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ScontoMaggiorazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}ScontoMaggiorazioneType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="PrezzoTotale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="AliquotaIVA" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Ritenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}RitenutaType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Natura" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}NaturaType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoAmministrazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="AltriDatiGestionali" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}AltriDatiGestionaliType" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "DettaglioLineeType", 
  propOrder = {
  	"numeroLinea",
  	"tipoCessionePrestazione",
  	"codiceArticolo",
  	"descrizione",
  	"_decimalWrapper_quantita",
  	"unitaMisura",
  	"dataInizioPeriodo",
  	"dataFinePeriodo",
  	"_decimalWrapper_prezzoUnitario",
  	"scontoMaggiorazione",
  	"_decimalWrapper_prezzoTotale",
  	"_decimalWrapper_aliquotaIVA",
  	"ritenuta",
  	"natura",
  	"riferimentoAmministrazione",
  	"altriDatiGestionali"
  }
)

@XmlRootElement(name = "DettaglioLineeType")

public class DettaglioLineeType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DettaglioLineeType() {
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

  public java.lang.Integer getNumeroLinea() {
    return this.numeroLinea;
  }

  public void setNumeroLinea(java.lang.Integer numeroLinea) {
    this.numeroLinea = numeroLinea;
  }

  public void set_value_tipoCessionePrestazione(String value) {
    this.tipoCessionePrestazione = (TipoCessionePrestazioneType) TipoCessionePrestazioneType.toEnumConstantFromString(value);
  }

  public String get_value_tipoCessionePrestazione() {
    if(this.tipoCessionePrestazione == null){
    	return null;
    }else{
    	return this.tipoCessionePrestazione.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoCessionePrestazioneType getTipoCessionePrestazione() {
    return this.tipoCessionePrestazione;
  }

  public void setTipoCessionePrestazione(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoCessionePrestazioneType tipoCessionePrestazione) {
    this.tipoCessionePrestazione = tipoCessionePrestazione;
  }

  public void addCodiceArticolo(CodiceArticoloType codiceArticolo) {
    this.codiceArticolo.add(codiceArticolo);
  }

  public CodiceArticoloType getCodiceArticolo(int index) {
    return this.codiceArticolo.get( index );
  }

  public CodiceArticoloType removeCodiceArticolo(int index) {
    return this.codiceArticolo.remove( index );
  }

  public List<CodiceArticoloType> getCodiceArticoloList() {
    return this.codiceArticolo;
  }

  public void setCodiceArticoloList(List<CodiceArticoloType> codiceArticolo) {
    this.codiceArticolo=codiceArticolo;
  }

  public int sizeCodiceArticoloList() {
    return this.codiceArticolo.size();
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.Double getQuantita() {
    if(this._decimalWrapper_quantita!=null){
		return (java.lang.Double) this._decimalWrapper_quantita.getObject(java.lang.Double.class);
	}else{
		return this.quantita;
	}
  }

  public void setQuantita(java.lang.Double quantita) {
    if(quantita!=null){
		this._decimalWrapper_quantita = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,12,2,8,quantita);
	}
  }

  public java.lang.String getUnitaMisura() {
    return this.unitaMisura;
  }

  public void setUnitaMisura(java.lang.String unitaMisura) {
    this.unitaMisura = unitaMisura;
  }

  public java.util.Date getDataInizioPeriodo() {
    return this.dataInizioPeriodo;
  }

  public void setDataInizioPeriodo(java.util.Date dataInizioPeriodo) {
    this.dataInizioPeriodo = dataInizioPeriodo;
  }

  public java.util.Date getDataFinePeriodo() {
    return this.dataFinePeriodo;
  }

  public void setDataFinePeriodo(java.util.Date dataFinePeriodo) {
    this.dataFinePeriodo = dataFinePeriodo;
  }

  public java.lang.Double getPrezzoUnitario() {
    if(this._decimalWrapper_prezzoUnitario!=null){
		return (java.lang.Double) this._decimalWrapper_prezzoUnitario.getObject(java.lang.Double.class);
	}else{
		return this.prezzoUnitario;
	}
  }

  public void setPrezzoUnitario(java.lang.Double prezzoUnitario) {
    if(prezzoUnitario!=null){
		this._decimalWrapper_prezzoUnitario = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,8,prezzoUnitario);
	}
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

  public java.lang.Double getPrezzoTotale() {
    if(this._decimalWrapper_prezzoTotale!=null){
		return (java.lang.Double) this._decimalWrapper_prezzoTotale.getObject(java.lang.Double.class);
	}else{
		return this.prezzoTotale;
	}
  }

  public void setPrezzoTotale(java.lang.Double prezzoTotale) {
    if(prezzoTotale!=null){
		this._decimalWrapper_prezzoTotale = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,8,prezzoTotale);
	}
  }

  public java.lang.Double getAliquotaIVA() {
    if(this._decimalWrapper_aliquotaIVA!=null){
		return (java.lang.Double) this._decimalWrapper_aliquotaIVA.getObject(java.lang.Double.class);
	}else{
		return this.aliquotaIVA;
	}
  }

  public void setAliquotaIVA(java.lang.Double aliquotaIVA) {
    if(aliquotaIVA!=null){
		this._decimalWrapper_aliquotaIVA = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,3,2,2,aliquotaIVA);
	}
  }

  public void set_value_ritenuta(String value) {
    this.ritenuta = (RitenutaType) RitenutaType.toEnumConstantFromString(value);
  }

  public String get_value_ritenuta() {
    if(this.ritenuta == null){
    	return null;
    }else{
    	return this.ritenuta.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.RitenutaType getRitenuta() {
    return this.ritenuta;
  }

  public void setRitenuta(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.RitenutaType ritenuta) {
    this.ritenuta = ritenuta;
  }

  public void set_value_natura(String value) {
    this.natura = (NaturaType) NaturaType.toEnumConstantFromString(value);
  }

  public String get_value_natura() {
    if(this.natura == null){
    	return null;
    }else{
    	return this.natura.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.NaturaType getNatura() {
    return this.natura;
  }

  public void setNatura(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.NaturaType natura) {
    this.natura = natura;
  }

  public java.lang.String getRiferimentoAmministrazione() {
    return this.riferimentoAmministrazione;
  }

  public void setRiferimentoAmministrazione(java.lang.String riferimentoAmministrazione) {
    this.riferimentoAmministrazione = riferimentoAmministrazione;
  }

  public void addAltriDatiGestionali(AltriDatiGestionaliType altriDatiGestionali) {
    this.altriDatiGestionali.add(altriDatiGestionali);
  }

  public AltriDatiGestionaliType getAltriDatiGestionali(int index) {
    return this.altriDatiGestionali.get( index );
  }

  public AltriDatiGestionaliType removeAltriDatiGestionali(int index) {
    return this.altriDatiGestionali.remove( index );
  }

  public List<AltriDatiGestionaliType> getAltriDatiGestionaliList() {
    return this.altriDatiGestionali;
  }

  public void setAltriDatiGestionaliList(List<AltriDatiGestionaliType> altriDatiGestionali) {
    this.altriDatiGestionali=altriDatiGestionali;
  }

  public int sizeAltriDatiGestionaliList() {
    return this.altriDatiGestionali.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="NumeroLinea",required=true,nillable=false)
  protected java.lang.Integer numeroLinea;

  @XmlTransient
  protected java.lang.String _value_tipoCessionePrestazione;

  @XmlElement(name="TipoCessionePrestazione",required=false,nillable=false)
  protected TipoCessionePrestazioneType tipoCessionePrestazione;

  @XmlElement(name="CodiceArticolo",required=true,nillable=false)
  protected List<CodiceArticoloType> codiceArticolo = new ArrayList<CodiceArticoloType>();

  /**
   * @deprecated Use method getCodiceArticoloList
   * @return List<CodiceArticoloType>
  */
  @Deprecated
  public List<CodiceArticoloType> getCodiceArticolo() {
  	return this.codiceArticolo;
  }

  /**
   * @deprecated Use method setCodiceArticoloList
   * @param codiceArticolo List<CodiceArticoloType>
  */
  @Deprecated
  public void setCodiceArticolo(List<CodiceArticoloType> codiceArticolo) {
  	this.codiceArticolo=codiceArticolo;
  }

  /**
   * @deprecated Use method sizeCodiceArticoloList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeCodiceArticolo() {
  	return this.codiceArticolo.size();
  }

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Descrizione",required=true,nillable=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Quantita",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_quantita = null;

  @XmlTransient
  protected java.lang.Double quantita;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="UnitaMisura",required=false,nillable=false)
  protected java.lang.String unitaMisura;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataInizioPeriodo",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataInizioPeriodo;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="DataFinePeriodo",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataFinePeriodo;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="PrezzoUnitario",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_prezzoUnitario = null;

  @XmlTransient
  protected java.lang.Double prezzoUnitario;

  @XmlElement(name="ScontoMaggiorazione",required=true,nillable=false)
  protected List<ScontoMaggiorazioneType> scontoMaggiorazione = new ArrayList<ScontoMaggiorazioneType>();

  /**
   * @deprecated Use method getScontoMaggiorazioneList
   * @return List<ScontoMaggiorazioneType>
  */
  @Deprecated
  public List<ScontoMaggiorazioneType> getScontoMaggiorazione() {
  	return this.scontoMaggiorazione;
  }

  /**
   * @deprecated Use method setScontoMaggiorazioneList
   * @param scontoMaggiorazione List<ScontoMaggiorazioneType>
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
  @XmlElement(name="PrezzoTotale",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_prezzoTotale = null;

  @XmlTransient
  protected java.lang.Double prezzoTotale;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaIVA",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaIVA = null;

  @XmlTransient
  protected java.lang.Double aliquotaIVA;

  @XmlTransient
  protected java.lang.String _value_ritenuta;

  @XmlElement(name="Ritenuta",required=false,nillable=false)
  protected RitenutaType ritenuta;

  @XmlTransient
  protected java.lang.String _value_natura;

  @XmlElement(name="Natura",required=false,nillable=false)
  protected NaturaType natura;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoAmministrazione",required=false,nillable=false)
  protected java.lang.String riferimentoAmministrazione;

  @XmlElement(name="AltriDatiGestionali",required=true,nillable=false)
  protected List<AltriDatiGestionaliType> altriDatiGestionali = new ArrayList<AltriDatiGestionaliType>();

  /**
   * @deprecated Use method getAltriDatiGestionaliList
   * @return List<AltriDatiGestionaliType>
  */
  @Deprecated
  public List<AltriDatiGestionaliType> getAltriDatiGestionali() {
  	return this.altriDatiGestionali;
  }

  /**
   * @deprecated Use method setAltriDatiGestionaliList
   * @param altriDatiGestionali List<AltriDatiGestionaliType>
  */
  @Deprecated
  public void setAltriDatiGestionali(List<AltriDatiGestionaliType> altriDatiGestionali) {
  	this.altriDatiGestionali=altriDatiGestionali;
  }

  /**
   * @deprecated Use method sizeAltriDatiGestionaliList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAltriDatiGestionali() {
  	return this.altriDatiGestionali.size();
  }

}

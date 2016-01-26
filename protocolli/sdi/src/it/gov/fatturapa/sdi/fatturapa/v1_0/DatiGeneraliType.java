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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiGeneraliType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiGeneraliType">
 * 		&lt;sequence>
 * 			&lt;element name="DatiGeneraliDocumento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiGeneraliDocumentoType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="DatiOrdineAcquisto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="DatiContratto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="DatiConvenzione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="DatiRicezione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="DatiFattureCollegate" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="DatiSAL" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiSALType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="DatiDDT" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiDDTType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="DatiTrasporto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiTrasportoType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="NormaDiRiferimento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="FatturaPrincipale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}FatturaPrincipaleType" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "DatiGeneraliType", 
  propOrder = {
  	"datiGeneraliDocumento",
  	"datiOrdineAcquisto",
  	"datiContratto",
  	"datiConvenzione",
  	"datiRicezione",
  	"datiFattureCollegate",
  	"datiSAL",
  	"datiDDT",
  	"datiTrasporto",
  	"normaDiRiferimento",
  	"fatturaPrincipale"
  }
)

@XmlRootElement(name = "DatiGeneraliType")

public class DatiGeneraliType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiGeneraliType() {
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

  public DatiGeneraliDocumentoType getDatiGeneraliDocumento() {
    return this.datiGeneraliDocumento;
  }

  public void setDatiGeneraliDocumento(DatiGeneraliDocumentoType datiGeneraliDocumento) {
    this.datiGeneraliDocumento = datiGeneraliDocumento;
  }

  public void addDatiOrdineAcquisto(DatiDocumentiCorrelatiType datiOrdineAcquisto) {
    this.datiOrdineAcquisto.add(datiOrdineAcquisto);
  }

  public DatiDocumentiCorrelatiType getDatiOrdineAcquisto(int index) {
    return this.datiOrdineAcquisto.get( index );
  }

  public DatiDocumentiCorrelatiType removeDatiOrdineAcquisto(int index) {
    return this.datiOrdineAcquisto.remove( index );
  }

  public List<DatiDocumentiCorrelatiType> getDatiOrdineAcquistoList() {
    return this.datiOrdineAcquisto;
  }

  public void setDatiOrdineAcquistoList(List<DatiDocumentiCorrelatiType> datiOrdineAcquisto) {
    this.datiOrdineAcquisto=datiOrdineAcquisto;
  }

  public int sizeDatiOrdineAcquistoList() {
    return this.datiOrdineAcquisto.size();
  }

  public void addDatiContratto(DatiDocumentiCorrelatiType datiContratto) {
    this.datiContratto.add(datiContratto);
  }

  public DatiDocumentiCorrelatiType getDatiContratto(int index) {
    return this.datiContratto.get( index );
  }

  public DatiDocumentiCorrelatiType removeDatiContratto(int index) {
    return this.datiContratto.remove( index );
  }

  public List<DatiDocumentiCorrelatiType> getDatiContrattoList() {
    return this.datiContratto;
  }

  public void setDatiContrattoList(List<DatiDocumentiCorrelatiType> datiContratto) {
    this.datiContratto=datiContratto;
  }

  public int sizeDatiContrattoList() {
    return this.datiContratto.size();
  }

  public void addDatiConvenzione(DatiDocumentiCorrelatiType datiConvenzione) {
    this.datiConvenzione.add(datiConvenzione);
  }

  public DatiDocumentiCorrelatiType getDatiConvenzione(int index) {
    return this.datiConvenzione.get( index );
  }

  public DatiDocumentiCorrelatiType removeDatiConvenzione(int index) {
    return this.datiConvenzione.remove( index );
  }

  public List<DatiDocumentiCorrelatiType> getDatiConvenzioneList() {
    return this.datiConvenzione;
  }

  public void setDatiConvenzioneList(List<DatiDocumentiCorrelatiType> datiConvenzione) {
    this.datiConvenzione=datiConvenzione;
  }

  public int sizeDatiConvenzioneList() {
    return this.datiConvenzione.size();
  }

  public void addDatiRicezione(DatiDocumentiCorrelatiType datiRicezione) {
    this.datiRicezione.add(datiRicezione);
  }

  public DatiDocumentiCorrelatiType getDatiRicezione(int index) {
    return this.datiRicezione.get( index );
  }

  public DatiDocumentiCorrelatiType removeDatiRicezione(int index) {
    return this.datiRicezione.remove( index );
  }

  public List<DatiDocumentiCorrelatiType> getDatiRicezioneList() {
    return this.datiRicezione;
  }

  public void setDatiRicezioneList(List<DatiDocumentiCorrelatiType> datiRicezione) {
    this.datiRicezione=datiRicezione;
  }

  public int sizeDatiRicezioneList() {
    return this.datiRicezione.size();
  }

  public void addDatiFattureCollegate(DatiDocumentiCorrelatiType datiFattureCollegate) {
    this.datiFattureCollegate.add(datiFattureCollegate);
  }

  public DatiDocumentiCorrelatiType getDatiFattureCollegate(int index) {
    return this.datiFattureCollegate.get( index );
  }

  public DatiDocumentiCorrelatiType removeDatiFattureCollegate(int index) {
    return this.datiFattureCollegate.remove( index );
  }

  public List<DatiDocumentiCorrelatiType> getDatiFattureCollegateList() {
    return this.datiFattureCollegate;
  }

  public void setDatiFattureCollegateList(List<DatiDocumentiCorrelatiType> datiFattureCollegate) {
    this.datiFattureCollegate=datiFattureCollegate;
  }

  public int sizeDatiFattureCollegateList() {
    return this.datiFattureCollegate.size();
  }

  public void addDatiSAL(DatiSALType datiSAL) {
    this.datiSAL.add(datiSAL);
  }

  public DatiSALType getDatiSAL(int index) {
    return this.datiSAL.get( index );
  }

  public DatiSALType removeDatiSAL(int index) {
    return this.datiSAL.remove( index );
  }

  public List<DatiSALType> getDatiSALList() {
    return this.datiSAL;
  }

  public void setDatiSALList(List<DatiSALType> datiSAL) {
    this.datiSAL=datiSAL;
  }

  public int sizeDatiSALList() {
    return this.datiSAL.size();
  }

  public void addDatiDDT(DatiDDTType datiDDT) {
    this.datiDDT.add(datiDDT);
  }

  public DatiDDTType getDatiDDT(int index) {
    return this.datiDDT.get( index );
  }

  public DatiDDTType removeDatiDDT(int index) {
    return this.datiDDT.remove( index );
  }

  public List<DatiDDTType> getDatiDDTList() {
    return this.datiDDT;
  }

  public void setDatiDDTList(List<DatiDDTType> datiDDT) {
    this.datiDDT=datiDDT;
  }

  public int sizeDatiDDTList() {
    return this.datiDDT.size();
  }

  public DatiTrasportoType getDatiTrasporto() {
    return this.datiTrasporto;
  }

  public void setDatiTrasporto(DatiTrasportoType datiTrasporto) {
    this.datiTrasporto = datiTrasporto;
  }

  public java.lang.String getNormaDiRiferimento() {
    return this.normaDiRiferimento;
  }

  public void setNormaDiRiferimento(java.lang.String normaDiRiferimento) {
    this.normaDiRiferimento = normaDiRiferimento;
  }

  public FatturaPrincipaleType getFatturaPrincipale() {
    return this.fatturaPrincipale;
  }

  public void setFatturaPrincipale(FatturaPrincipaleType fatturaPrincipale) {
    this.fatturaPrincipale = fatturaPrincipale;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="DatiGeneraliDocumento",required=true,nillable=false)
  protected DatiGeneraliDocumentoType datiGeneraliDocumento;

  @XmlElement(name="DatiOrdineAcquisto",required=true,nillable=false)
  protected List<DatiDocumentiCorrelatiType> datiOrdineAcquisto = new ArrayList<DatiDocumentiCorrelatiType>();

  /**
   * @deprecated Use method getDatiOrdineAcquistoList
   * @return List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public List<DatiDocumentiCorrelatiType> getDatiOrdineAcquisto() {
  	return this.datiOrdineAcquisto;
  }

  /**
   * @deprecated Use method setDatiOrdineAcquistoList
   * @param datiOrdineAcquisto List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public void setDatiOrdineAcquisto(List<DatiDocumentiCorrelatiType> datiOrdineAcquisto) {
  	this.datiOrdineAcquisto=datiOrdineAcquisto;
  }

  /**
   * @deprecated Use method sizeDatiOrdineAcquistoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiOrdineAcquisto() {
  	return this.datiOrdineAcquisto.size();
  }

  @XmlElement(name="DatiContratto",required=true,nillable=false)
  protected List<DatiDocumentiCorrelatiType> datiContratto = new ArrayList<DatiDocumentiCorrelatiType>();

  /**
   * @deprecated Use method getDatiContrattoList
   * @return List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public List<DatiDocumentiCorrelatiType> getDatiContratto() {
  	return this.datiContratto;
  }

  /**
   * @deprecated Use method setDatiContrattoList
   * @param datiContratto List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public void setDatiContratto(List<DatiDocumentiCorrelatiType> datiContratto) {
  	this.datiContratto=datiContratto;
  }

  /**
   * @deprecated Use method sizeDatiContrattoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiContratto() {
  	return this.datiContratto.size();
  }

  @XmlElement(name="DatiConvenzione",required=true,nillable=false)
  protected List<DatiDocumentiCorrelatiType> datiConvenzione = new ArrayList<DatiDocumentiCorrelatiType>();

  /**
   * @deprecated Use method getDatiConvenzioneList
   * @return List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public List<DatiDocumentiCorrelatiType> getDatiConvenzione() {
  	return this.datiConvenzione;
  }

  /**
   * @deprecated Use method setDatiConvenzioneList
   * @param datiConvenzione List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public void setDatiConvenzione(List<DatiDocumentiCorrelatiType> datiConvenzione) {
  	this.datiConvenzione=datiConvenzione;
  }

  /**
   * @deprecated Use method sizeDatiConvenzioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiConvenzione() {
  	return this.datiConvenzione.size();
  }

  @XmlElement(name="DatiRicezione",required=true,nillable=false)
  protected List<DatiDocumentiCorrelatiType> datiRicezione = new ArrayList<DatiDocumentiCorrelatiType>();

  /**
   * @deprecated Use method getDatiRicezioneList
   * @return List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public List<DatiDocumentiCorrelatiType> getDatiRicezione() {
  	return this.datiRicezione;
  }

  /**
   * @deprecated Use method setDatiRicezioneList
   * @param datiRicezione List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public void setDatiRicezione(List<DatiDocumentiCorrelatiType> datiRicezione) {
  	this.datiRicezione=datiRicezione;
  }

  /**
   * @deprecated Use method sizeDatiRicezioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiRicezione() {
  	return this.datiRicezione.size();
  }

  @XmlElement(name="DatiFattureCollegate",required=true,nillable=false)
  protected List<DatiDocumentiCorrelatiType> datiFattureCollegate = new ArrayList<DatiDocumentiCorrelatiType>();

  /**
   * @deprecated Use method getDatiFattureCollegateList
   * @return List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public List<DatiDocumentiCorrelatiType> getDatiFattureCollegate() {
  	return this.datiFattureCollegate;
  }

  /**
   * @deprecated Use method setDatiFattureCollegateList
   * @param datiFattureCollegate List<DatiDocumentiCorrelatiType>
  */
  @Deprecated
  public void setDatiFattureCollegate(List<DatiDocumentiCorrelatiType> datiFattureCollegate) {
  	this.datiFattureCollegate=datiFattureCollegate;
  }

  /**
   * @deprecated Use method sizeDatiFattureCollegateList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiFattureCollegate() {
  	return this.datiFattureCollegate.size();
  }

  @XmlElement(name="DatiSAL",required=true,nillable=false)
  protected List<DatiSALType> datiSAL = new ArrayList<DatiSALType>();

  /**
   * @deprecated Use method getDatiSALList
   * @return List<DatiSALType>
  */
  @Deprecated
  public List<DatiSALType> getDatiSAL() {
  	return this.datiSAL;
  }

  /**
   * @deprecated Use method setDatiSALList
   * @param datiSAL List<DatiSALType>
  */
  @Deprecated
  public void setDatiSAL(List<DatiSALType> datiSAL) {
  	this.datiSAL=datiSAL;
  }

  /**
   * @deprecated Use method sizeDatiSALList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiSAL() {
  	return this.datiSAL.size();
  }

  @XmlElement(name="DatiDDT",required=true,nillable=false)
  protected List<DatiDDTType> datiDDT = new ArrayList<DatiDDTType>();

  /**
   * @deprecated Use method getDatiDDTList
   * @return List<DatiDDTType>
  */
  @Deprecated
  public List<DatiDDTType> getDatiDDT() {
  	return this.datiDDT;
  }

  /**
   * @deprecated Use method setDatiDDTList
   * @param datiDDT List<DatiDDTType>
  */
  @Deprecated
  public void setDatiDDT(List<DatiDDTType> datiDDT) {
  	this.datiDDT=datiDDT;
  }

  /**
   * @deprecated Use method sizeDatiDDTList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiDDT() {
  	return this.datiDDT.size();
  }

  @XmlElement(name="DatiTrasporto",required=false,nillable=false)
  protected DatiTrasportoType datiTrasporto;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NormaDiRiferimento",required=false,nillable=false)
  protected java.lang.String normaDiRiferimento;

  @XmlElement(name="FatturaPrincipale",required=false,nillable=false)
  protected FatturaPrincipaleType fatturaPrincipale;

}

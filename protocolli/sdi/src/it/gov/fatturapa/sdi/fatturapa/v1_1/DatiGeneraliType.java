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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiGeneraliType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiGeneraliType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="DatiGeneraliDocumento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiGeneraliDocumentoType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiOrdineAcquisto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="DatiContratto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="DatiConvenzione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="DatiRicezione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="DatiFattureCollegate" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiDocumentiCorrelatiType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="DatiSAL" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiSALType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="DatiDDT" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiDDTType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="DatiTrasporto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DatiTrasportoType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="FatturaPrincipale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}FatturaPrincipaleType" minOccurs="0" maxOccurs="1"/&gt;
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
  	"fatturaPrincipale"
  }
)

@XmlRootElement(name = "DatiGeneraliType")

public class DatiGeneraliType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiGeneraliType() {
    super();
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

  public FatturaPrincipaleType getFatturaPrincipale() {
    return this.fatturaPrincipale;
  }

  public void setFatturaPrincipale(FatturaPrincipaleType fatturaPrincipale) {
    this.fatturaPrincipale = fatturaPrincipale;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="DatiGeneraliDocumento",required=true,nillable=false)
  protected DatiGeneraliDocumentoType datiGeneraliDocumento;

  @XmlElement(name="DatiOrdineAcquisto",required=true,nillable=false)
  private List<DatiDocumentiCorrelatiType> datiOrdineAcquisto = new ArrayList<>();

  /**
   * Use method getDatiOrdineAcquistoList
   * @return List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public List<DatiDocumentiCorrelatiType> getDatiOrdineAcquisto() {
  	return this.getDatiOrdineAcquistoList();
  }

  /**
   * Use method setDatiOrdineAcquistoList
   * @param datiOrdineAcquisto List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public void setDatiOrdineAcquisto(List<DatiDocumentiCorrelatiType> datiOrdineAcquisto) {
  	this.setDatiOrdineAcquistoList(datiOrdineAcquisto);
  }

  /**
   * Use method sizeDatiOrdineAcquistoList
   * @return lunghezza della lista
  */
  public int sizeDatiOrdineAcquisto() {
  	return this.sizeDatiOrdineAcquistoList();
  }

  @XmlElement(name="DatiContratto",required=true,nillable=false)
  private List<DatiDocumentiCorrelatiType> datiContratto = new ArrayList<>();

  /**
   * Use method getDatiContrattoList
   * @return List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public List<DatiDocumentiCorrelatiType> getDatiContratto() {
  	return this.getDatiContrattoList();
  }

  /**
   * Use method setDatiContrattoList
   * @param datiContratto List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public void setDatiContratto(List<DatiDocumentiCorrelatiType> datiContratto) {
  	this.setDatiContrattoList(datiContratto);
  }

  /**
   * Use method sizeDatiContrattoList
   * @return lunghezza della lista
  */
  public int sizeDatiContratto() {
  	return this.sizeDatiContrattoList();
  }

  @XmlElement(name="DatiConvenzione",required=true,nillable=false)
  private List<DatiDocumentiCorrelatiType> datiConvenzione = new ArrayList<>();

  /**
   * Use method getDatiConvenzioneList
   * @return List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public List<DatiDocumentiCorrelatiType> getDatiConvenzione() {
  	return this.getDatiConvenzioneList();
  }

  /**
   * Use method setDatiConvenzioneList
   * @param datiConvenzione List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public void setDatiConvenzione(List<DatiDocumentiCorrelatiType> datiConvenzione) {
  	this.setDatiConvenzioneList(datiConvenzione);
  }

  /**
   * Use method sizeDatiConvenzioneList
   * @return lunghezza della lista
  */
  public int sizeDatiConvenzione() {
  	return this.sizeDatiConvenzioneList();
  }

  @XmlElement(name="DatiRicezione",required=true,nillable=false)
  private List<DatiDocumentiCorrelatiType> datiRicezione = new ArrayList<>();

  /**
   * Use method getDatiRicezioneList
   * @return List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public List<DatiDocumentiCorrelatiType> getDatiRicezione() {
  	return this.getDatiRicezioneList();
  }

  /**
   * Use method setDatiRicezioneList
   * @param datiRicezione List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public void setDatiRicezione(List<DatiDocumentiCorrelatiType> datiRicezione) {
  	this.setDatiRicezioneList(datiRicezione);
  }

  /**
   * Use method sizeDatiRicezioneList
   * @return lunghezza della lista
  */
  public int sizeDatiRicezione() {
  	return this.sizeDatiRicezioneList();
  }

  @XmlElement(name="DatiFattureCollegate",required=true,nillable=false)
  private List<DatiDocumentiCorrelatiType> datiFattureCollegate = new ArrayList<>();

  /**
   * Use method getDatiFattureCollegateList
   * @return List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public List<DatiDocumentiCorrelatiType> getDatiFattureCollegate() {
  	return this.getDatiFattureCollegateList();
  }

  /**
   * Use method setDatiFattureCollegateList
   * @param datiFattureCollegate List&lt;DatiDocumentiCorrelatiType&gt;
  */
  public void setDatiFattureCollegate(List<DatiDocumentiCorrelatiType> datiFattureCollegate) {
  	this.setDatiFattureCollegateList(datiFattureCollegate);
  }

  /**
   * Use method sizeDatiFattureCollegateList
   * @return lunghezza della lista
  */
  public int sizeDatiFattureCollegate() {
  	return this.sizeDatiFattureCollegateList();
  }

  @XmlElement(name="DatiSAL",required=true,nillable=false)
  private List<DatiSALType> datiSAL = new ArrayList<>();

  /**
   * Use method getDatiSALList
   * @return List&lt;DatiSALType&gt;
  */
  public List<DatiSALType> getDatiSAL() {
  	return this.getDatiSALList();
  }

  /**
   * Use method setDatiSALList
   * @param datiSAL List&lt;DatiSALType&gt;
  */
  public void setDatiSAL(List<DatiSALType> datiSAL) {
  	this.setDatiSALList(datiSAL);
  }

  /**
   * Use method sizeDatiSALList
   * @return lunghezza della lista
  */
  public int sizeDatiSAL() {
  	return this.sizeDatiSALList();
  }

  @XmlElement(name="DatiDDT",required=true,nillable=false)
  private List<DatiDDTType> datiDDT = new ArrayList<>();

  /**
   * Use method getDatiDDTList
   * @return List&lt;DatiDDTType&gt;
  */
  public List<DatiDDTType> getDatiDDT() {
  	return this.getDatiDDTList();
  }

  /**
   * Use method setDatiDDTList
   * @param datiDDT List&lt;DatiDDTType&gt;
  */
  public void setDatiDDT(List<DatiDDTType> datiDDT) {
  	this.setDatiDDTList(datiDDT);
  }

  /**
   * Use method sizeDatiDDTList
   * @return lunghezza della lista
  */
  public int sizeDatiDDT() {
  	return this.sizeDatiDDTList();
  }

  @XmlElement(name="DatiTrasporto",required=false,nillable=false)
  protected DatiTrasportoType datiTrasporto;

  @XmlElement(name="FatturaPrincipale",required=false,nillable=false)
  protected FatturaPrincipaleType fatturaPrincipale;

}

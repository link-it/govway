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
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for FatturaElettronicaBodyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaElettronicaBodyType">
 * 		&lt;sequence>
 * 			&lt;element name="DatiGenerali" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiGeneraliType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="DatiBeniServizi" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiBeniServiziType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="DatiVeicoli" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiVeicoliType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="DatiPagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiPagamentoType" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="Allegati" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}AllegatiType" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "FatturaElettronicaBodyType", 
  propOrder = {
  	"datiGenerali",
  	"datiBeniServizi",
  	"datiVeicoli",
  	"datiPagamento",
  	"allegati"
  }
)

@XmlRootElement(name = "FatturaElettronicaBodyType")

public class FatturaElettronicaBodyType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FatturaElettronicaBodyType() {
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

  public DatiGeneraliType getDatiGenerali() {
    return this.datiGenerali;
  }

  public void setDatiGenerali(DatiGeneraliType datiGenerali) {
    this.datiGenerali = datiGenerali;
  }

  public DatiBeniServiziType getDatiBeniServizi() {
    return this.datiBeniServizi;
  }

  public void setDatiBeniServizi(DatiBeniServiziType datiBeniServizi) {
    this.datiBeniServizi = datiBeniServizi;
  }

  public DatiVeicoliType getDatiVeicoli() {
    return this.datiVeicoli;
  }

  public void setDatiVeicoli(DatiVeicoliType datiVeicoli) {
    this.datiVeicoli = datiVeicoli;
  }

  public void addDatiPagamento(DatiPagamentoType datiPagamento) {
    this.datiPagamento.add(datiPagamento);
  }

  public DatiPagamentoType getDatiPagamento(int index) {
    return this.datiPagamento.get( index );
  }

  public DatiPagamentoType removeDatiPagamento(int index) {
    return this.datiPagamento.remove( index );
  }

  public List<DatiPagamentoType> getDatiPagamentoList() {
    return this.datiPagamento;
  }

  public void setDatiPagamentoList(List<DatiPagamentoType> datiPagamento) {
    this.datiPagamento=datiPagamento;
  }

  public int sizeDatiPagamentoList() {
    return this.datiPagamento.size();
  }

  public void addAllegati(AllegatiType allegati) {
    this.allegati.add(allegati);
  }

  public AllegatiType getAllegati(int index) {
    return this.allegati.get( index );
  }

  public AllegatiType removeAllegati(int index) {
    return this.allegati.remove( index );
  }

  public List<AllegatiType> getAllegatiList() {
    return this.allegati;
  }

  public void setAllegatiList(List<AllegatiType> allegati) {
    this.allegati=allegati;
  }

  public int sizeAllegatiList() {
    return this.allegati.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="DatiGenerali",required=true,nillable=false)
  protected DatiGeneraliType datiGenerali;

  @XmlElement(name="DatiBeniServizi",required=true,nillable=false)
  protected DatiBeniServiziType datiBeniServizi;

  @XmlElement(name="DatiVeicoli",required=false,nillable=false)
  protected DatiVeicoliType datiVeicoli;

  @XmlElement(name="DatiPagamento",required=true,nillable=false)
  protected List<DatiPagamentoType> datiPagamento = new ArrayList<DatiPagamentoType>();

  /**
   * @deprecated Use method getDatiPagamentoList
   * @return List<DatiPagamentoType>
  */
  @Deprecated
  public List<DatiPagamentoType> getDatiPagamento() {
  	return this.datiPagamento;
  }

  /**
   * @deprecated Use method setDatiPagamentoList
   * @param datiPagamento List<DatiPagamentoType>
  */
  @Deprecated
  public void setDatiPagamento(List<DatiPagamentoType> datiPagamento) {
  	this.datiPagamento=datiPagamento;
  }

  /**
   * @deprecated Use method sizeDatiPagamentoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiPagamento() {
  	return this.datiPagamento.size();
  }

  @XmlElement(name="Allegati",required=true,nillable=false)
  protected List<AllegatiType> allegati = new ArrayList<AllegatiType>();

  /**
   * @deprecated Use method getAllegatiList
   * @return List<AllegatiType>
  */
  @Deprecated
  public List<AllegatiType> getAllegati() {
  	return this.allegati;
  }

  /**
   * @deprecated Use method setAllegatiList
   * @param allegati List<AllegatiType>
  */
  @Deprecated
  public void setAllegati(List<AllegatiType> allegati) {
  	this.allegati=allegati;
  }

  /**
   * @deprecated Use method sizeAllegatiList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAllegati() {
  	return this.allegati.size();
  }

}

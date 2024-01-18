/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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


/** <p>Java class for FatturaElettronicaBodyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaElettronicaBodyType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="DatiGenerali" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiGeneraliType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiBeniServizi" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiBeniServiziType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiVeicoli" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiVeicoliType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="DatiPagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}DatiPagamentoType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="Allegati" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}AllegatiType" minOccurs="0" maxOccurs="unbounded"/&gt;
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
    super();
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



  @XmlElement(name="DatiGenerali",required=true,nillable=false)
  protected DatiGeneraliType datiGenerali;

  @XmlElement(name="DatiBeniServizi",required=true,nillable=false)
  protected DatiBeniServiziType datiBeniServizi;

  @XmlElement(name="DatiVeicoli",required=false,nillable=false)
  protected DatiVeicoliType datiVeicoli;

  @XmlElement(name="DatiPagamento",required=true,nillable=false)
  private List<DatiPagamentoType> datiPagamento = new ArrayList<>();

  /**
   * Use method getDatiPagamentoList
   * @return List&lt;DatiPagamentoType&gt;
  */
  public List<DatiPagamentoType> getDatiPagamento() {
  	return this.getDatiPagamentoList();
  }

  /**
   * Use method setDatiPagamentoList
   * @param datiPagamento List&lt;DatiPagamentoType&gt;
  */
  public void setDatiPagamento(List<DatiPagamentoType> datiPagamento) {
  	this.setDatiPagamentoList(datiPagamento);
  }

  /**
   * Use method sizeDatiPagamentoList
   * @return lunghezza della lista
  */
  public int sizeDatiPagamento() {
  	return this.sizeDatiPagamentoList();
  }

  @XmlElement(name="Allegati",required=true,nillable=false)
  private List<AllegatiType> allegati = new ArrayList<>();

  /**
   * Use method getAllegatiList
   * @return List&lt;AllegatiType&gt;
  */
  public List<AllegatiType> getAllegati() {
  	return this.getAllegatiList();
  }

  /**
   * Use method setAllegatiList
   * @param allegati List&lt;AllegatiType&gt;
  */
  public void setAllegati(List<AllegatiType> allegati) {
  	this.setAllegatiList(allegati);
  }

  /**
   * Use method sizeAllegatiList
   * @return lunghezza della lista
  */
  public int sizeAllegati() {
  	return this.sizeAllegatiList();
  }

}

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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.CondizioniPagamentoType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiPagamentoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiPagamentoType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="CondizioniPagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}CondizioniPagamentoType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="DettaglioPagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DettaglioPagamentoType" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "DatiPagamentoType", 
  propOrder = {
  	"condizioniPagamento",
  	"dettaglioPagamento"
  }
)

@XmlRootElement(name = "DatiPagamentoType")

public class DatiPagamentoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiPagamentoType() {
    super();
  }

  public void setCondizioniPagamentoRawEnumValue(String value) {
    this.condizioniPagamento = (CondizioniPagamentoType) CondizioniPagamentoType.toEnumConstantFromString(value);
  }

  public String getCondizioniPagamentoRawEnumValue() {
    if(this.condizioniPagamento == null){
    	return null;
    }else{
    	return this.condizioniPagamento.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.CondizioniPagamentoType getCondizioniPagamento() {
    return this.condizioniPagamento;
  }

  public void setCondizioniPagamento(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.CondizioniPagamentoType condizioniPagamento) {
    this.condizioniPagamento = condizioniPagamento;
  }

  public void addDettaglioPagamento(DettaglioPagamentoType dettaglioPagamento) {
    this.dettaglioPagamento.add(dettaglioPagamento);
  }

  public DettaglioPagamentoType getDettaglioPagamento(int index) {
    return this.dettaglioPagamento.get( index );
  }

  public DettaglioPagamentoType removeDettaglioPagamento(int index) {
    return this.dettaglioPagamento.remove( index );
  }

  public List<DettaglioPagamentoType> getDettaglioPagamentoList() {
    return this.dettaglioPagamento;
  }

  public void setDettaglioPagamentoList(List<DettaglioPagamentoType> dettaglioPagamento) {
    this.dettaglioPagamento=dettaglioPagamento;
  }

  public int sizeDettaglioPagamentoList() {
    return this.dettaglioPagamento.size();
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String condizioniPagamentoRawEnumValue;

  @XmlElement(name="CondizioniPagamento",required=true,nillable=false)
  protected CondizioniPagamentoType condizioniPagamento;

  @XmlElement(name="DettaglioPagamento",required=true,nillable=false)
  private List<DettaglioPagamentoType> dettaglioPagamento = new ArrayList<>();

}

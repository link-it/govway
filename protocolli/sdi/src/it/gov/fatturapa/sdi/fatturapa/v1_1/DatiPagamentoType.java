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

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.CondizioniPagamentoType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiPagamentoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiPagamentoType">
 * 		&lt;sequence>
 * 			&lt;element name="CondizioniPagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}CondizioniPagamentoType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="DettaglioPagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}DettaglioPagamentoType" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "DatiPagamentoType", 
  propOrder = {
  	"condizioniPagamento",
  	"dettaglioPagamento"
  }
)

@XmlRootElement(name = "DatiPagamentoType")

public class DatiPagamentoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiPagamentoType() {
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

  public void set_value_condizioniPagamento(String value) {
    this.condizioniPagamento = (CondizioniPagamentoType) CondizioniPagamentoType.toEnumConstantFromString(value);
  }

  public String get_value_condizioniPagamento() {
    if(this.condizioniPagamento == null){
    	return null;
    }else{
    	return this.condizioniPagamento.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.CondizioniPagamentoType getCondizioniPagamento() {
    return this.condizioniPagamento;
  }

  public void setCondizioniPagamento(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.CondizioniPagamentoType condizioniPagamento) {
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

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_condizioniPagamento;

  @XmlElement(name="CondizioniPagamento",required=true,nillable=false)
  protected CondizioniPagamentoType condizioniPagamento;

  @XmlElement(name="DettaglioPagamento",required=true,nillable=false)
  protected List<DettaglioPagamentoType> dettaglioPagamento = new ArrayList<DettaglioPagamentoType>();

  /**
   * @deprecated Use method getDettaglioPagamentoList
   * @return List<DettaglioPagamentoType>
  */
  @Deprecated
  public List<DettaglioPagamentoType> getDettaglioPagamento() {
  	return this.dettaglioPagamento;
  }

  /**
   * @deprecated Use method setDettaglioPagamentoList
   * @param dettaglioPagamento List<DettaglioPagamentoType>
  */
  @Deprecated
  public void setDettaglioPagamento(List<DettaglioPagamentoType> dettaglioPagamento) {
  	this.dettaglioPagamento=dettaglioPagamento;
  }

  /**
   * @deprecated Use method sizeDettaglioPagamentoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDettaglioPagamento() {
  	return this.dettaglioPagamento.size();
  }

}

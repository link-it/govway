/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for gestione-errore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-errore"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="codice-trasporto" type="{http://www.openspcoop2.org/core/config}gestione-errore-codice-trasporto" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="soap-fault" type="{http://www.openspcoop2.org/core/config}gestione-errore-soap-fault" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="comportamento" type="{http://www.openspcoop2.org/core/config}GestioneErroreComportamento" use="required"/&gt;
 * 		&lt;attribute name="cadenza-rispedizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gestione-errore", 
  propOrder = {
  	"codiceTrasporto",
  	"soapFault"
  }
)

@XmlRootElement(name = "gestione-errore")

public class GestioneErrore extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public GestioneErrore() {
    super();
  }

  public void addCodiceTrasporto(GestioneErroreCodiceTrasporto codiceTrasporto) {
    this.codiceTrasporto.add(codiceTrasporto);
  }

  public GestioneErroreCodiceTrasporto getCodiceTrasporto(int index) {
    return this.codiceTrasporto.get( index );
  }

  public GestioneErroreCodiceTrasporto removeCodiceTrasporto(int index) {
    return this.codiceTrasporto.remove( index );
  }

  public List<GestioneErroreCodiceTrasporto> getCodiceTrasportoList() {
    return this.codiceTrasporto;
  }

  public void setCodiceTrasportoList(List<GestioneErroreCodiceTrasporto> codiceTrasporto) {
    this.codiceTrasporto=codiceTrasporto;
  }

  public int sizeCodiceTrasportoList() {
    return this.codiceTrasporto.size();
  }

  public void addSoapFault(GestioneErroreSoapFault soapFault) {
    this.soapFault.add(soapFault);
  }

  public GestioneErroreSoapFault getSoapFault(int index) {
    return this.soapFault.get( index );
  }

  public GestioneErroreSoapFault removeSoapFault(int index) {
    return this.soapFault.remove( index );
  }

  public List<GestioneErroreSoapFault> getSoapFaultList() {
    return this.soapFault;
  }

  public void setSoapFaultList(List<GestioneErroreSoapFault> soapFault) {
    this.soapFault=soapFault;
  }

  public int sizeSoapFaultList() {
    return this.soapFault.size();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void setComportamentoRawEnumValue(String value) {
    this.comportamento = (GestioneErroreComportamento) GestioneErroreComportamento.toEnumConstantFromString(value);
  }

  public String getComportamentoRawEnumValue() {
    if(this.comportamento == null){
    	return null;
    }else{
    	return this.comportamento.toString();
    }
  }

  public org.openspcoop2.core.config.constants.GestioneErroreComportamento getComportamento() {
    return this.comportamento;
  }

  public void setComportamento(org.openspcoop2.core.config.constants.GestioneErroreComportamento comportamento) {
    this.comportamento = comportamento;
  }

  public java.lang.String getCadenzaRispedizione() {
    return this.cadenzaRispedizione;
  }

  public void setCadenzaRispedizione(java.lang.String cadenzaRispedizione) {
    this.cadenzaRispedizione = cadenzaRispedizione;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="codice-trasporto",required=true,nillable=false)
  private List<GestioneErroreCodiceTrasporto> codiceTrasporto = new ArrayList<>();

  /**
   * Use method getCodiceTrasportoList
   * @return List&lt;GestioneErroreCodiceTrasporto&gt;
  */
  public List<GestioneErroreCodiceTrasporto> getCodiceTrasporto() {
  	return this.getCodiceTrasportoList();
  }

  /**
   * Use method setCodiceTrasportoList
   * @param codiceTrasporto List&lt;GestioneErroreCodiceTrasporto&gt;
  */
  public void setCodiceTrasporto(List<GestioneErroreCodiceTrasporto> codiceTrasporto) {
  	this.setCodiceTrasportoList(codiceTrasporto);
  }

  /**
   * Use method sizeCodiceTrasportoList
   * @return lunghezza della lista
  */
  public int sizeCodiceTrasporto() {
  	return this.sizeCodiceTrasportoList();
  }

  @XmlElement(name="soap-fault",required=true,nillable=false)
  private List<GestioneErroreSoapFault> soapFault = new ArrayList<>();

  /**
   * Use method getSoapFaultList
   * @return List&lt;GestioneErroreSoapFault&gt;
  */
  public List<GestioneErroreSoapFault> getSoapFault() {
  	return this.getSoapFaultList();
  }

  /**
   * Use method setSoapFaultList
   * @param soapFault List&lt;GestioneErroreSoapFault&gt;
  */
  public void setSoapFault(List<GestioneErroreSoapFault> soapFault) {
  	this.setSoapFaultList(soapFault);
  }

  /**
   * Use method sizeSoapFaultList
   * @return lunghezza della lista
  */
  public int sizeSoapFault() {
  	return this.sizeSoapFaultList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String comportamentoRawEnumValue;

  @XmlAttribute(name="comportamento",required=true)
  protected GestioneErroreComportamento comportamento;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cadenza-rispedizione",required=false)
  protected java.lang.String cadenzaRispedizione;

}

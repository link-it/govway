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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for gestione-errore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-errore">
 * 		&lt;sequence>
 * 			&lt;element name="codice-trasporto" type="{http://www.openspcoop2.org/core/config}gestione-errore-codice-trasporto" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="soap-fault" type="{http://www.openspcoop2.org/core/config}gestione-errore-soap-fault" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="comportamento" type="{http://www.openspcoop2.org/core/config}GestioneErroreComportamento" use="required"/>
 * 		&lt;attribute name="cadenza-rispedizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
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

public class GestioneErrore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public GestioneErrore() {
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

  public void set_value_comportamento(String value) {
    this.comportamento = (GestioneErroreComportamento) GestioneErroreComportamento.toEnumConstantFromString(value);
  }

  public String get_value_comportamento() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="codice-trasporto",required=true,nillable=false)
  protected List<GestioneErroreCodiceTrasporto> codiceTrasporto = new ArrayList<GestioneErroreCodiceTrasporto>();

  /**
   * @deprecated Use method getCodiceTrasportoList
   * @return List<GestioneErroreCodiceTrasporto>
  */
  @Deprecated
  public List<GestioneErroreCodiceTrasporto> getCodiceTrasporto() {
  	return this.codiceTrasporto;
  }

  /**
   * @deprecated Use method setCodiceTrasportoList
   * @param codiceTrasporto List<GestioneErroreCodiceTrasporto>
  */
  @Deprecated
  public void setCodiceTrasporto(List<GestioneErroreCodiceTrasporto> codiceTrasporto) {
  	this.codiceTrasporto=codiceTrasporto;
  }

  /**
   * @deprecated Use method sizeCodiceTrasportoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeCodiceTrasporto() {
  	return this.codiceTrasporto.size();
  }

  @XmlElement(name="soap-fault",required=true,nillable=false)
  protected List<GestioneErroreSoapFault> soapFault = new ArrayList<GestioneErroreSoapFault>();

  /**
   * @deprecated Use method getSoapFaultList
   * @return List<GestioneErroreSoapFault>
  */
  @Deprecated
  public List<GestioneErroreSoapFault> getSoapFault() {
  	return this.soapFault;
  }

  /**
   * @deprecated Use method setSoapFaultList
   * @param soapFault List<GestioneErroreSoapFault>
  */
  @Deprecated
  public void setSoapFault(List<GestioneErroreSoapFault> soapFault) {
  	this.soapFault=soapFault;
  }

  /**
   * @deprecated Use method sizeSoapFaultList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSoapFault() {
  	return this.soapFault.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=false)
  protected java.lang.String nome;

  @XmlTransient
  protected java.lang.String _value_comportamento;

  @XmlAttribute(name="comportamento",required=true)
  protected GestioneErroreComportamento comportamento;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="cadenza-rispedizione",required=false)
  protected java.lang.String cadenzaRispedizione;

}

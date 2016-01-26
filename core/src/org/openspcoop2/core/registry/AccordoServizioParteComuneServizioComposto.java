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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accordo-servizio-parte-comune-servizio-composto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune-servizio-composto">
 * 		&lt;sequence>
 * 			&lt;element name="servizio-componente" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-comune-servizio-composto-servizio-componente" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="specifica-coordinamento" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="id-accordo-cooperazione" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="accordo-cooperazione" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordo-servizio-parte-comune-servizio-composto", 
  propOrder = {
  	"servizioComponente",
  	"specificaCoordinamento"
  }
)

@XmlRootElement(name = "accordo-servizio-parte-comune-servizio-composto")

public class AccordoServizioParteComuneServizioComposto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoServizioParteComuneServizioComposto() {
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

  public void addServizioComponente(AccordoServizioParteComuneServizioCompostoServizioComponente servizioComponente) {
    this.servizioComponente.add(servizioComponente);
  }

  public AccordoServizioParteComuneServizioCompostoServizioComponente getServizioComponente(int index) {
    return this.servizioComponente.get( index );
  }

  public AccordoServizioParteComuneServizioCompostoServizioComponente removeServizioComponente(int index) {
    return this.servizioComponente.remove( index );
  }

  public List<AccordoServizioParteComuneServizioCompostoServizioComponente> getServizioComponenteList() {
    return this.servizioComponente;
  }

  public void setServizioComponenteList(List<AccordoServizioParteComuneServizioCompostoServizioComponente> servizioComponente) {
    this.servizioComponente=servizioComponente;
  }

  public int sizeServizioComponenteList() {
    return this.servizioComponente.size();
  }

  public void addSpecificaCoordinamento(Documento specificaCoordinamento) {
    this.specificaCoordinamento.add(specificaCoordinamento);
  }

  public Documento getSpecificaCoordinamento(int index) {
    return this.specificaCoordinamento.get( index );
  }

  public Documento removeSpecificaCoordinamento(int index) {
    return this.specificaCoordinamento.remove( index );
  }

  public List<Documento> getSpecificaCoordinamentoList() {
    return this.specificaCoordinamento;
  }

  public void setSpecificaCoordinamentoList(List<Documento> specificaCoordinamento) {
    this.specificaCoordinamento=specificaCoordinamento;
  }

  public int sizeSpecificaCoordinamentoList() {
    return this.specificaCoordinamento.size();
  }

  public java.lang.Long getIdAccordoCooperazione() {
    return this.idAccordoCooperazione;
  }

  public void setIdAccordoCooperazione(java.lang.Long idAccordoCooperazione) {
    this.idAccordoCooperazione = idAccordoCooperazione;
  }

  public java.lang.String getAccordoCooperazione() {
    return this.accordoCooperazione;
  }

  public void setAccordoCooperazione(java.lang.String accordoCooperazione) {
    this.accordoCooperazione = accordoCooperazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="servizio-componente",required=true,nillable=false)
  protected List<AccordoServizioParteComuneServizioCompostoServizioComponente> servizioComponente = new ArrayList<AccordoServizioParteComuneServizioCompostoServizioComponente>();

  /**
   * @deprecated Use method getServizioComponenteList
   * @return List<AccordoServizioParteComuneServizioCompostoServizioComponente>
  */
  @Deprecated
  public List<AccordoServizioParteComuneServizioCompostoServizioComponente> getServizioComponente() {
  	return this.servizioComponente;
  }

  /**
   * @deprecated Use method setServizioComponenteList
   * @param servizioComponente List<AccordoServizioParteComuneServizioCompostoServizioComponente>
  */
  @Deprecated
  public void setServizioComponente(List<AccordoServizioParteComuneServizioCompostoServizioComponente> servizioComponente) {
  	this.servizioComponente=servizioComponente;
  }

  /**
   * @deprecated Use method sizeServizioComponenteList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioComponente() {
  	return this.servizioComponente.size();
  }

  @XmlElement(name="specifica-coordinamento",required=true,nillable=false)
  protected List<Documento> specificaCoordinamento = new ArrayList<Documento>();

  /**
   * @deprecated Use method getSpecificaCoordinamentoList
   * @return List<Documento>
  */
  @Deprecated
  public List<Documento> getSpecificaCoordinamento() {
  	return this.specificaCoordinamento;
  }

  /**
   * @deprecated Use method setSpecificaCoordinamentoList
   * @param specificaCoordinamento List<Documento>
  */
  @Deprecated
  public void setSpecificaCoordinamento(List<Documento> specificaCoordinamento) {
  	this.specificaCoordinamento=specificaCoordinamento;
  }

  /**
   * @deprecated Use method sizeSpecificaCoordinamentoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSpecificaCoordinamento() {
  	return this.specificaCoordinamento.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idAccordoCooperazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="accordo-cooperazione",required=true)
  protected java.lang.String accordoCooperazione;

}

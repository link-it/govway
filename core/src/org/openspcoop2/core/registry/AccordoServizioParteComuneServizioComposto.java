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
package org.openspcoop2.core.registry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accordo-servizio-parte-comune-servizio-composto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune-servizio-composto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="servizio-componente" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-comune-servizio-composto-servizio-componente" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="specifica-coordinamento" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="id-accordo-cooperazione" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="accordo-cooperazione" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
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

public class AccordoServizioParteComuneServizioComposto extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AccordoServizioParteComuneServizioComposto() {
    super();
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



  @XmlElement(name="servizio-componente",required=true,nillable=false)
  private List<AccordoServizioParteComuneServizioCompostoServizioComponente> servizioComponente = new ArrayList<>();

  /**
   * Use method getServizioComponenteList
   * @return List&lt;AccordoServizioParteComuneServizioCompostoServizioComponente&gt;
  */
  public List<AccordoServizioParteComuneServizioCompostoServizioComponente> getServizioComponente() {
  	return this.getServizioComponenteList();
  }

  /**
   * Use method setServizioComponenteList
   * @param servizioComponente List&lt;AccordoServizioParteComuneServizioCompostoServizioComponente&gt;
  */
  public void setServizioComponente(List<AccordoServizioParteComuneServizioCompostoServizioComponente> servizioComponente) {
  	this.setServizioComponenteList(servizioComponente);
  }

  /**
   * Use method sizeServizioComponenteList
   * @return lunghezza della lista
  */
  public int sizeServizioComponente() {
  	return this.sizeServizioComponenteList();
  }

  @XmlElement(name="specifica-coordinamento",required=true,nillable=false)
  private List<Documento> specificaCoordinamento = new ArrayList<>();

  /**
   * Use method getSpecificaCoordinamentoList
   * @return List&lt;Documento&gt;
  */
  public List<Documento> getSpecificaCoordinamento() {
  	return this.getSpecificaCoordinamentoList();
  }

  /**
   * Use method setSpecificaCoordinamentoList
   * @param specificaCoordinamento List&lt;Documento&gt;
  */
  public void setSpecificaCoordinamento(List<Documento> specificaCoordinamento) {
  	this.setSpecificaCoordinamentoList(specificaCoordinamento);
  }

  /**
   * Use method sizeSpecificaCoordinamentoList
   * @return lunghezza della lista
  */
  public int sizeSpecificaCoordinamento() {
  	return this.sizeSpecificaCoordinamentoList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.Long idAccordoCooperazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="accordo-cooperazione",required=true)
  protected java.lang.String accordoCooperazione;

}

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.statistiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for statistica-contenuti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-contenuti"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="risorsa-nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="risorsa-valore" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-1" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-2" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-3" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-4" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-5" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-6" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-6" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-7" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-7" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-8" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-8" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-9" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-9" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-10" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-valore-10" type="{http://www.openspcoop2.org/core/statistiche}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="numero-transazioni" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="dimensioni-bytes-banda-complessiva" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dimensioni-bytes-banda-interna" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dimensioni-bytes-banda-esterna" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="latenza-totale" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="latenza-porta" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="latenza-servizio" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "statistica-contenuti", 
  propOrder = {
  	"data",
  	"risorsaNome",
  	"risorsaValore",
  	"filtroNome1",
  	"filtroValore1",
  	"filtroNome2",
  	"filtroValore2",
  	"filtroNome3",
  	"filtroValore3",
  	"filtroNome4",
  	"filtroValore4",
  	"filtroNome5",
  	"filtroValore5",
  	"filtroNome6",
  	"filtroValore6",
  	"filtroNome7",
  	"filtroValore7",
  	"filtroNome8",
  	"filtroValore8",
  	"filtroNome9",
  	"filtroValore9",
  	"filtroNome10",
  	"filtroValore10",
  	"numeroTransazioni",
  	"dimensioniBytesBandaComplessiva",
  	"dimensioniBytesBandaInterna",
  	"dimensioniBytesBandaEsterna",
  	"latenzaTotale",
  	"latenzaPorta",
  	"latenzaServizio"
  }
)

@XmlRootElement(name = "statistica-contenuti")

public class StatisticaContenuti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatisticaContenuti() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  public java.lang.String getRisorsaNome() {
    return this.risorsaNome;
  }

  public void setRisorsaNome(java.lang.String risorsaNome) {
    this.risorsaNome = risorsaNome;
  }

  public java.lang.String getRisorsaValore() {
    return this.risorsaValore;
  }

  public void setRisorsaValore(java.lang.String risorsaValore) {
    this.risorsaValore = risorsaValore;
  }

  public java.lang.String getFiltroNome1() {
    return this.filtroNome1;
  }

  public void setFiltroNome1(java.lang.String filtroNome1) {
    this.filtroNome1 = filtroNome1;
  }

  public java.lang.String getFiltroValore1() {
    return this.filtroValore1;
  }

  public void setFiltroValore1(java.lang.String filtroValore1) {
    this.filtroValore1 = filtroValore1;
  }

  public java.lang.String getFiltroNome2() {
    return this.filtroNome2;
  }

  public void setFiltroNome2(java.lang.String filtroNome2) {
    this.filtroNome2 = filtroNome2;
  }

  public java.lang.String getFiltroValore2() {
    return this.filtroValore2;
  }

  public void setFiltroValore2(java.lang.String filtroValore2) {
    this.filtroValore2 = filtroValore2;
  }

  public java.lang.String getFiltroNome3() {
    return this.filtroNome3;
  }

  public void setFiltroNome3(java.lang.String filtroNome3) {
    this.filtroNome3 = filtroNome3;
  }

  public java.lang.String getFiltroValore3() {
    return this.filtroValore3;
  }

  public void setFiltroValore3(java.lang.String filtroValore3) {
    this.filtroValore3 = filtroValore3;
  }

  public java.lang.String getFiltroNome4() {
    return this.filtroNome4;
  }

  public void setFiltroNome4(java.lang.String filtroNome4) {
    this.filtroNome4 = filtroNome4;
  }

  public java.lang.String getFiltroValore4() {
    return this.filtroValore4;
  }

  public void setFiltroValore4(java.lang.String filtroValore4) {
    this.filtroValore4 = filtroValore4;
  }

  public java.lang.String getFiltroNome5() {
    return this.filtroNome5;
  }

  public void setFiltroNome5(java.lang.String filtroNome5) {
    this.filtroNome5 = filtroNome5;
  }

  public java.lang.String getFiltroValore5() {
    return this.filtroValore5;
  }

  public void setFiltroValore5(java.lang.String filtroValore5) {
    this.filtroValore5 = filtroValore5;
  }

  public java.lang.String getFiltroNome6() {
    return this.filtroNome6;
  }

  public void setFiltroNome6(java.lang.String filtroNome6) {
    this.filtroNome6 = filtroNome6;
  }

  public java.lang.String getFiltroValore6() {
    return this.filtroValore6;
  }

  public void setFiltroValore6(java.lang.String filtroValore6) {
    this.filtroValore6 = filtroValore6;
  }

  public java.lang.String getFiltroNome7() {
    return this.filtroNome7;
  }

  public void setFiltroNome7(java.lang.String filtroNome7) {
    this.filtroNome7 = filtroNome7;
  }

  public java.lang.String getFiltroValore7() {
    return this.filtroValore7;
  }

  public void setFiltroValore7(java.lang.String filtroValore7) {
    this.filtroValore7 = filtroValore7;
  }

  public java.lang.String getFiltroNome8() {
    return this.filtroNome8;
  }

  public void setFiltroNome8(java.lang.String filtroNome8) {
    this.filtroNome8 = filtroNome8;
  }

  public java.lang.String getFiltroValore8() {
    return this.filtroValore8;
  }

  public void setFiltroValore8(java.lang.String filtroValore8) {
    this.filtroValore8 = filtroValore8;
  }

  public java.lang.String getFiltroNome9() {
    return this.filtroNome9;
  }

  public void setFiltroNome9(java.lang.String filtroNome9) {
    this.filtroNome9 = filtroNome9;
  }

  public java.lang.String getFiltroValore9() {
    return this.filtroValore9;
  }

  public void setFiltroValore9(java.lang.String filtroValore9) {
    this.filtroValore9 = filtroValore9;
  }

  public java.lang.String getFiltroNome10() {
    return this.filtroNome10;
  }

  public void setFiltroNome10(java.lang.String filtroNome10) {
    this.filtroNome10 = filtroNome10;
  }

  public java.lang.String getFiltroValore10() {
    return this.filtroValore10;
  }

  public void setFiltroValore10(java.lang.String filtroValore10) {
    this.filtroValore10 = filtroValore10;
  }

  public java.lang.Integer getNumeroTransazioni() {
    return this.numeroTransazioni;
  }

  public void setNumeroTransazioni(java.lang.Integer numeroTransazioni) {
    this.numeroTransazioni = numeroTransazioni;
  }

  public java.lang.Long getDimensioniBytesBandaComplessiva() {
    return this.dimensioniBytesBandaComplessiva;
  }

  public void setDimensioniBytesBandaComplessiva(java.lang.Long dimensioniBytesBandaComplessiva) {
    this.dimensioniBytesBandaComplessiva = dimensioniBytesBandaComplessiva;
  }

  public java.lang.Long getDimensioniBytesBandaInterna() {
    return this.dimensioniBytesBandaInterna;
  }

  public void setDimensioniBytesBandaInterna(java.lang.Long dimensioniBytesBandaInterna) {
    this.dimensioniBytesBandaInterna = dimensioniBytesBandaInterna;
  }

  public java.lang.Long getDimensioniBytesBandaEsterna() {
    return this.dimensioniBytesBandaEsterna;
  }

  public void setDimensioniBytesBandaEsterna(java.lang.Long dimensioniBytesBandaEsterna) {
    this.dimensioniBytesBandaEsterna = dimensioniBytesBandaEsterna;
  }

  public java.lang.Long getLatenzaTotale() {
    return this.latenzaTotale;
  }

  public void setLatenzaTotale(java.lang.Long latenzaTotale) {
    this.latenzaTotale = latenzaTotale;
  }

  public java.lang.Long getLatenzaPorta() {
    return this.latenzaPorta;
  }

  public void setLatenzaPorta(java.lang.Long latenzaPorta) {
    this.latenzaPorta = latenzaPorta;
  }

  public java.lang.Long getLatenzaServizio() {
    return this.latenzaServizio;
  }

  public void setLatenzaServizio(java.lang.Long latenzaServizio) {
    this.latenzaServizio = latenzaServizio;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date data;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="risorsa-nome",required=true,nillable=false)
  protected java.lang.String risorsaNome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="risorsa-valore",required=true,nillable=false)
  protected java.lang.String risorsaValore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-1",required=false,nillable=false)
  protected java.lang.String filtroNome1;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-1",required=false,nillable=false)
  protected java.lang.String filtroValore1;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-2",required=false,nillable=false)
  protected java.lang.String filtroNome2;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-2",required=false,nillable=false)
  protected java.lang.String filtroValore2;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-3",required=false,nillable=false)
  protected java.lang.String filtroNome3;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-3",required=false,nillable=false)
  protected java.lang.String filtroValore3;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-4",required=false,nillable=false)
  protected java.lang.String filtroNome4;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-4",required=false,nillable=false)
  protected java.lang.String filtroValore4;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-5",required=false,nillable=false)
  protected java.lang.String filtroNome5;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-5",required=false,nillable=false)
  protected java.lang.String filtroValore5;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-6",required=false,nillable=false)
  protected java.lang.String filtroNome6;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-6",required=false,nillable=false)
  protected java.lang.String filtroValore6;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-7",required=false,nillable=false)
  protected java.lang.String filtroNome7;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-7",required=false,nillable=false)
  protected java.lang.String filtroValore7;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-8",required=false,nillable=false)
  protected java.lang.String filtroNome8;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-8",required=false,nillable=false)
  protected java.lang.String filtroValore8;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-9",required=false,nillable=false)
  protected java.lang.String filtroNome9;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-9",required=false,nillable=false)
  protected java.lang.String filtroValore9;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-10",required=false,nillable=false)
  protected java.lang.String filtroNome10;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-valore-10",required=false,nillable=false)
  protected java.lang.String filtroValore10;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="numero-transazioni",required=true,nillable=false)
  protected java.lang.Integer numeroTransazioni;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="dimensioni-bytes-banda-complessiva",required=false,nillable=false)
  protected java.lang.Long dimensioniBytesBandaComplessiva;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="dimensioni-bytes-banda-interna",required=false,nillable=false)
  protected java.lang.Long dimensioniBytesBandaInterna;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="dimensioni-bytes-banda-esterna",required=false,nillable=false)
  protected java.lang.Long dimensioniBytesBandaEsterna;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="latenza-totale",required=false,nillable=false)
  protected java.lang.Long latenzaTotale;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="latenza-porta",required=false,nillable=false)
  protected java.lang.Long latenzaPorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="latenza-servizio",required=false,nillable=false)
  protected java.lang.Long latenzaServizio;

}

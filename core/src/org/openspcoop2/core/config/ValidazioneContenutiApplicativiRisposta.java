/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for validazione-contenuti-applicativi-risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi-risposta"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi-stato" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="applicabilita" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi-risposta-applicabilita" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="posizione" type="{http://www.w3.org/2001/XMLSchema}int" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validazione-contenuti-applicativi-risposta", 
  propOrder = {
  	"configurazione",
  	"applicabilita"
  }
)

@XmlRootElement(name = "validazione-contenuti-applicativi-risposta")

public class ValidazioneContenutiApplicativiRisposta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ValidazioneContenutiApplicativiRisposta() {
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

  public ValidazioneContenutiApplicativiStato getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(ValidazioneContenutiApplicativiStato configurazione) {
    this.configurazione = configurazione;
  }

  public ValidazioneContenutiApplicativiRispostaApplicabilita getApplicabilita() {
    return this.applicabilita;
  }

  public void setApplicabilita(ValidazioneContenutiApplicativiRispostaApplicabilita applicabilita) {
    this.applicabilita = applicabilita;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public int getPosizione() {
    return this.posizione;
  }

  public void setPosizione(int posizione) {
    this.posizione = posizione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="configurazione",required=true,nillable=false)
  protected ValidazioneContenutiApplicativiStato configurazione;

  @XmlElement(name="applicabilita",required=true,nillable=false)
  protected ValidazioneContenutiApplicativiRispostaApplicabilita applicabilita;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="posizione",required=true)
  protected int posizione;

}

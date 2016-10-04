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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione;
import java.io.Serializable;


/** <p>Java class for correlazione-applicativa-risposta-elemento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="correlazione-applicativa-risposta-elemento">
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="identificazione" type="{http://www.openspcoop2.org/core/config}CorrelazioneApplicativaRispostaIdentificazione" use="optional" default="contentBased"/>
 * 		&lt;attribute name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="identificazione-fallita" type="{http://www.openspcoop2.org/core/config}CorrelazioneApplicativaGestioneIdentificazioneFallita" use="optional" default="blocca"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "correlazione-applicativa-risposta-elemento")

@XmlRootElement(name = "correlazione-applicativa-risposta-elemento")

public class CorrelazioneApplicativaRispostaElemento extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CorrelazioneApplicativaRispostaElemento() {
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void set_value_identificazione(String value) {
    this.identificazione = (CorrelazioneApplicativaRispostaIdentificazione) CorrelazioneApplicativaRispostaIdentificazione.toEnumConstantFromString(value);
  }

  public String get_value_identificazione() {
    if(this.identificazione == null){
    	return null;
    }else{
    	return this.identificazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione identificazione) {
    this.identificazione = identificazione;
  }

  public java.lang.String getPattern() {
    return this.pattern;
  }

  public void setPattern(java.lang.String pattern) {
    this.pattern = pattern;
  }

  public void set_value_identificazioneFallita(String value) {
    this.identificazioneFallita = (CorrelazioneApplicativaGestioneIdentificazioneFallita) CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstantFromString(value);
  }

  public String get_value_identificazioneFallita() {
    if(this.identificazioneFallita == null){
    	return null;
    }else{
    	return this.identificazioneFallita.toString();
    }
  }

  public org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita getIdentificazioneFallita() {
    return this.identificazioneFallita;
  }

  public void setIdentificazioneFallita(org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita identificazioneFallita) {
    this.identificazioneFallita = identificazioneFallita;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=false)
  protected java.lang.String nome;

  @XmlTransient
  protected java.lang.String _value_identificazione;

  @XmlAttribute(name="identificazione",required=false)
  protected CorrelazioneApplicativaRispostaIdentificazione identificazione = (CorrelazioneApplicativaRispostaIdentificazione) CorrelazioneApplicativaRispostaIdentificazione.toEnumConstantFromString("contentBased");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="pattern",required=false)
  protected java.lang.String pattern;

  @XmlTransient
  protected java.lang.String _value_identificazioneFallita;

  @XmlAttribute(name="identificazione-fallita",required=false)
  protected CorrelazioneApplicativaGestioneIdentificazioneFallita identificazioneFallita = (CorrelazioneApplicativaGestioneIdentificazioneFallita) CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstantFromString("blocca");

}

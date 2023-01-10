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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for correlazione-applicativa-elemento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="correlazione-applicativa-elemento"&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="identificazione" type="{http://www.openspcoop2.org/core/config}CorrelazioneApplicativaRichiestaIdentificazione" use="optional" default="contentBased"/&gt;
 * 		&lt;attribute name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="identificazione-fallita" type="{http://www.openspcoop2.org/core/config}CorrelazioneApplicativaGestioneIdentificazioneFallita" use="optional" default="blocca"/&gt;
 * 		&lt;attribute name="riuso-identificativo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "correlazione-applicativa-elemento")

@XmlRootElement(name = "correlazione-applicativa-elemento")

public class CorrelazioneApplicativaElemento extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CorrelazioneApplicativaElemento() {
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void set_value_identificazione(String value) {
    this.identificazione = (CorrelazioneApplicativaRichiestaIdentificazione) CorrelazioneApplicativaRichiestaIdentificazione.toEnumConstantFromString(value);
  }

  public String get_value_identificazione() {
    if(this.identificazione == null){
    	return null;
    }else{
    	return this.identificazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione identificazione) {
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

  public void set_value_riusoIdentificativo(String value) {
    this.riusoIdentificativo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_riusoIdentificativo() {
    if(this.riusoIdentificativo == null){
    	return null;
    }else{
    	return this.riusoIdentificativo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRiusoIdentificativo() {
    return this.riusoIdentificativo;
  }

  public void setRiusoIdentificativo(org.openspcoop2.core.config.constants.StatoFunzionalita riusoIdentificativo) {
    this.riusoIdentificativo = riusoIdentificativo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_identificazione;

  @XmlAttribute(name="identificazione",required=false)
  protected CorrelazioneApplicativaRichiestaIdentificazione identificazione = (CorrelazioneApplicativaRichiestaIdentificazione) CorrelazioneApplicativaRichiestaIdentificazione.toEnumConstantFromString("contentBased");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="pattern",required=false)
  protected java.lang.String pattern;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_identificazioneFallita;

  @XmlAttribute(name="identificazione-fallita",required=false)
  protected CorrelazioneApplicativaGestioneIdentificazioneFallita identificazioneFallita = (CorrelazioneApplicativaGestioneIdentificazioneFallita) CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstantFromString("blocca");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_riusoIdentificativo;

  @XmlAttribute(name="riuso-identificativo",required=false)
  protected StatoFunzionalita riusoIdentificativo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}

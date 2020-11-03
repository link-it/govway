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
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for validazione-contenuti-applicativi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi-stato" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="richiesta" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi-richiesta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="risposta" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi-risposta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/config}ValidazioneContenutiApplicativiTipo" use="optional" default="xsd"/&gt;
 * 		&lt;attribute name="accept-mtom-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validazione-contenuti-applicativi", 
  propOrder = {
  	"configurazione",
  	"richiesta",
  	"risposta"
  }
)

@XmlRootElement(name = "validazione-contenuti-applicativi")

public class ValidazioneContenutiApplicativi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ValidazioneContenutiApplicativi() {
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

  public void addRichiesta(ValidazioneContenutiApplicativiRichiesta richiesta) {
    this.richiesta.add(richiesta);
  }

  public ValidazioneContenutiApplicativiRichiesta getRichiesta(int index) {
    return this.richiesta.get( index );
  }

  public ValidazioneContenutiApplicativiRichiesta removeRichiesta(int index) {
    return this.richiesta.remove( index );
  }

  public List<ValidazioneContenutiApplicativiRichiesta> getRichiestaList() {
    return this.richiesta;
  }

  public void setRichiestaList(List<ValidazioneContenutiApplicativiRichiesta> richiesta) {
    this.richiesta=richiesta;
  }

  public int sizeRichiestaList() {
    return this.richiesta.size();
  }

  public void addRisposta(ValidazioneContenutiApplicativiRisposta risposta) {
    this.risposta.add(risposta);
  }

  public ValidazioneContenutiApplicativiRisposta getRisposta(int index) {
    return this.risposta.get( index );
  }

  public ValidazioneContenutiApplicativiRisposta removeRisposta(int index) {
    return this.risposta.remove( index );
  }

  public List<ValidazioneContenutiApplicativiRisposta> getRispostaList() {
    return this.risposta;
  }

  public void setRispostaList(List<ValidazioneContenutiApplicativiRisposta> risposta) {
    this.risposta=risposta;
  }

  public int sizeRispostaList() {
    return this.risposta.size();
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning stato) {
    this.stato = stato;
  }

  public void set_value_tipo(String value) {
    this.tipo = (ValidazioneContenutiApplicativiTipo) ValidazioneContenutiApplicativiTipo.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo tipo) {
    this.tipo = tipo;
  }

  public void set_value_acceptMtomMessage(String value) {
    this.acceptMtomMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_acceptMtomMessage() {
    if(this.acceptMtomMessage == null){
    	return null;
    }else{
    	return this.acceptMtomMessage.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAcceptMtomMessage() {
    return this.acceptMtomMessage;
  }

  public void setAcceptMtomMessage(org.openspcoop2.core.config.constants.StatoFunzionalita acceptMtomMessage) {
    this.acceptMtomMessage = acceptMtomMessage;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="configurazione",required=false,nillable=false)
  protected ValidazioneContenutiApplicativiStato configurazione;

  @XmlElement(name="richiesta",required=true,nillable=false)
  protected List<ValidazioneContenutiApplicativiRichiesta> richiesta = new ArrayList<ValidazioneContenutiApplicativiRichiesta>();

  /**
   * @deprecated Use method getRichiestaList
   * @return List&lt;ValidazioneContenutiApplicativiRichiesta&gt;
  */
  @Deprecated
  public List<ValidazioneContenutiApplicativiRichiesta> getRichiesta() {
  	return this.richiesta;
  }

  /**
   * @deprecated Use method setRichiestaList
   * @param richiesta List&lt;ValidazioneContenutiApplicativiRichiesta&gt;
  */
  @Deprecated
  public void setRichiesta(List<ValidazioneContenutiApplicativiRichiesta> richiesta) {
  	this.richiesta=richiesta;
  }

  /**
   * @deprecated Use method sizeRichiestaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRichiesta() {
  	return this.richiesta.size();
  }

  @XmlElement(name="risposta",required=true,nillable=false)
  protected List<ValidazioneContenutiApplicativiRisposta> risposta = new ArrayList<ValidazioneContenutiApplicativiRisposta>();

  /**
   * @deprecated Use method getRispostaList
   * @return List&lt;ValidazioneContenutiApplicativiRisposta&gt;
  */
  @Deprecated
  public List<ValidazioneContenutiApplicativiRisposta> getRisposta() {
  	return this.risposta;
  }

  /**
   * @deprecated Use method setRispostaList
   * @param risposta List&lt;ValidazioneContenutiApplicativiRisposta&gt;
  */
  @Deprecated
  public void setRisposta(List<ValidazioneContenutiApplicativiRisposta> risposta) {
  	this.risposta=risposta;
  }

  /**
   * @deprecated Use method sizeRispostaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRisposta() {
  	return this.risposta.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalitaConWarning stato = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=false)
  protected ValidazioneContenutiApplicativiTipo tipo = (ValidazioneContenutiApplicativiTipo) ValidazioneContenutiApplicativiTipo.toEnumConstantFromString("xsd");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_acceptMtomMessage;

  @XmlAttribute(name="accept-mtom-message",required=false)
  protected StatoFunzionalita acceptMtomMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}

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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for trasformazione-regola complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazione-regola"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="applicabilita" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-applicabilita-richiesta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="richiesta" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-richiesta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="risposta" type="{http://www.openspcoop2.org/core/config}trasformazione-regola-risposta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="posizione" type="{http://www.w3.org/2001/XMLSchema}int" use="required"/&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trasformazione-regola", 
  propOrder = {
  	"applicabilita",
  	"richiesta",
  	"risposta"
  }
)

@XmlRootElement(name = "trasformazione-regola")

public class TrasformazioneRegola extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TrasformazioneRegola() {
    super();
  }

  public TrasformazioneRegolaApplicabilitaRichiesta getApplicabilita() {
    return this.applicabilita;
  }

  public void setApplicabilita(TrasformazioneRegolaApplicabilitaRichiesta applicabilita) {
    this.applicabilita = applicabilita;
  }

  public TrasformazioneRegolaRichiesta getRichiesta() {
    return this.richiesta;
  }

  public void setRichiesta(TrasformazioneRegolaRichiesta richiesta) {
    this.richiesta = richiesta;
  }

  public void addRisposta(TrasformazioneRegolaRisposta risposta) {
    this.risposta.add(risposta);
  }

  public TrasformazioneRegolaRisposta getRisposta(int index) {
    return this.risposta.get( index );
  }

  public TrasformazioneRegolaRisposta removeRisposta(int index) {
    return this.risposta.remove( index );
  }

  public List<TrasformazioneRegolaRisposta> getRispostaList() {
    return this.risposta;
  }

  public void setRispostaList(List<TrasformazioneRegolaRisposta> risposta) {
    this.risposta=risposta;
  }

  public int sizeRispostaList() {
    return this.risposta.size();
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

  public void setStatoRawEnumValue(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="applicabilita",required=false,nillable=false)
  protected TrasformazioneRegolaApplicabilitaRichiesta applicabilita;

  @XmlElement(name="richiesta",required=false,nillable=false)
  protected TrasformazioneRegolaRichiesta richiesta;

  @XmlElement(name="risposta",required=true,nillable=false)
  private List<TrasformazioneRegolaRisposta> risposta = new ArrayList<>();

  /**
   * Use method getRispostaList
   * @return List&lt;TrasformazioneRegolaRisposta&gt;
  */
  public List<TrasformazioneRegolaRisposta> getRisposta() {
  	return this.getRispostaList();
  }

  /**
   * Use method setRispostaList
   * @param risposta List&lt;TrasformazioneRegolaRisposta&gt;
  */
  public void setRisposta(List<TrasformazioneRegolaRisposta> risposta) {
  	this.setRispostaList(risposta);
  }

  /**
   * Use method sizeRispostaList
   * @return lunghezza della lista
  */
  public int sizeRisposta() {
  	return this.sizeRispostaList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="posizione",required=true)
  protected int posizione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}

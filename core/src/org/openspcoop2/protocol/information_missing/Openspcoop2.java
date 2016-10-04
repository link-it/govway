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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for openspcoop2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openspcoop2">
 * 		&lt;sequence>
 * 			&lt;element name="wizard" type="{http://www.openspcoop2.org/protocol/information_missing}Wizard" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/protocol/information_missing}Soggetto" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="input" type="{http://www.openspcoop2.org/protocol/information_missing}Input" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/protocol/information_missing}ServizioApplicativo" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="accordo-cooperazione" type="{http://www.openspcoop2.org/protocol/information_missing}AccordoCooperazione" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="accordo-servizio-parte-comune" type="{http://www.openspcoop2.org/protocol/information_missing}AccordoServizioParteComune" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="accordo-servizio-parte-specifica" type="{http://www.openspcoop2.org/protocol/information_missing}AccordoServizioParteSpecifica" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="accordo-servizio-composto" type="{http://www.openspcoop2.org/protocol/information_missing}AccordoServizioParteComune" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="fruitore" type="{http://www.openspcoop2.org/protocol/information_missing}Fruitore" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "openspcoop2", 
  propOrder = {
  	"wizard",
  	"soggetto",
  	"input",
  	"servizioApplicativo",
  	"accordoCooperazione",
  	"accordoServizioParteComune",
  	"accordoServizioParteSpecifica",
  	"accordoServizioComposto",
  	"fruitore"
  }
)

@XmlRootElement(name = "openspcoop2")

public class Openspcoop2 extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Openspcoop2() {
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

  public Wizard getWizard() {
    return this.wizard;
  }

  public void setWizard(Wizard wizard) {
    this.wizard = wizard;
  }

  public void addSoggetto(Soggetto soggetto) {
    this.soggetto.add(soggetto);
  }

  public Soggetto getSoggetto(int index) {
    return this.soggetto.get( index );
  }

  public Soggetto removeSoggetto(int index) {
    return this.soggetto.remove( index );
  }

  public List<Soggetto> getSoggettoList() {
    return this.soggetto;
  }

  public void setSoggettoList(List<Soggetto> soggetto) {
    this.soggetto=soggetto;
  }

  public int sizeSoggettoList() {
    return this.soggetto.size();
  }

  public void addInput(Input input) {
    this.input.add(input);
  }

  public Input getInput(int index) {
    return this.input.get( index );
  }

  public Input removeInput(int index) {
    return this.input.remove( index );
  }

  public List<Input> getInputList() {
    return this.input;
  }

  public void setInputList(List<Input> input) {
    this.input=input;
  }

  public int sizeInputList() {
    return this.input.size();
  }

  public void addServizioApplicativo(ServizioApplicativo servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public ServizioApplicativo getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public ServizioApplicativo removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<ServizioApplicativo> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<ServizioApplicativo> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  public void addAccordoCooperazione(AccordoCooperazione accordoCooperazione) {
    this.accordoCooperazione.add(accordoCooperazione);
  }

  public AccordoCooperazione getAccordoCooperazione(int index) {
    return this.accordoCooperazione.get( index );
  }

  public AccordoCooperazione removeAccordoCooperazione(int index) {
    return this.accordoCooperazione.remove( index );
  }

  public List<AccordoCooperazione> getAccordoCooperazioneList() {
    return this.accordoCooperazione;
  }

  public void setAccordoCooperazioneList(List<AccordoCooperazione> accordoCooperazione) {
    this.accordoCooperazione=accordoCooperazione;
  }

  public int sizeAccordoCooperazioneList() {
    return this.accordoCooperazione.size();
  }

  public void addAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) {
    this.accordoServizioParteComune.add(accordoServizioParteComune);
  }

  public AccordoServizioParteComune getAccordoServizioParteComune(int index) {
    return this.accordoServizioParteComune.get( index );
  }

  public AccordoServizioParteComune removeAccordoServizioParteComune(int index) {
    return this.accordoServizioParteComune.remove( index );
  }

  public List<AccordoServizioParteComune> getAccordoServizioParteComuneList() {
    return this.accordoServizioParteComune;
  }

  public void setAccordoServizioParteComuneList(List<AccordoServizioParteComune> accordoServizioParteComune) {
    this.accordoServizioParteComune=accordoServizioParteComune;
  }

  public int sizeAccordoServizioParteComuneList() {
    return this.accordoServizioParteComune.size();
  }

  public void addAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) {
    this.accordoServizioParteSpecifica.add(accordoServizioParteSpecifica);
  }

  public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(int index) {
    return this.accordoServizioParteSpecifica.get( index );
  }

  public AccordoServizioParteSpecifica removeAccordoServizioParteSpecifica(int index) {
    return this.accordoServizioParteSpecifica.remove( index );
  }

  public List<AccordoServizioParteSpecifica> getAccordoServizioParteSpecificaList() {
    return this.accordoServizioParteSpecifica;
  }

  public void setAccordoServizioParteSpecificaList(List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica) {
    this.accordoServizioParteSpecifica=accordoServizioParteSpecifica;
  }

  public int sizeAccordoServizioParteSpecificaList() {
    return this.accordoServizioParteSpecifica.size();
  }

  public void addAccordoServizioComposto(AccordoServizioParteComune accordoServizioComposto) {
    this.accordoServizioComposto.add(accordoServizioComposto);
  }

  public AccordoServizioParteComune getAccordoServizioComposto(int index) {
    return this.accordoServizioComposto.get( index );
  }

  public AccordoServizioParteComune removeAccordoServizioComposto(int index) {
    return this.accordoServizioComposto.remove( index );
  }

  public List<AccordoServizioParteComune> getAccordoServizioCompostoList() {
    return this.accordoServizioComposto;
  }

  public void setAccordoServizioCompostoList(List<AccordoServizioParteComune> accordoServizioComposto) {
    this.accordoServizioComposto=accordoServizioComposto;
  }

  public int sizeAccordoServizioCompostoList() {
    return this.accordoServizioComposto.size();
  }

  public void addFruitore(Fruitore fruitore) {
    this.fruitore.add(fruitore);
  }

  public Fruitore getFruitore(int index) {
    return this.fruitore.get( index );
  }

  public Fruitore removeFruitore(int index) {
    return this.fruitore.remove( index );
  }

  public List<Fruitore> getFruitoreList() {
    return this.fruitore;
  }

  public void setFruitoreList(List<Fruitore> fruitore) {
    this.fruitore=fruitore;
  }

  public int sizeFruitoreList() {
    return this.fruitore.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.protocol.information_missing.model.Openspcoop2Model modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.protocol.information_missing.Openspcoop2.modelStaticInstance==null){
  			org.openspcoop2.protocol.information_missing.Openspcoop2.modelStaticInstance = new org.openspcoop2.protocol.information_missing.model.Openspcoop2Model();
	  }
  }
  public static org.openspcoop2.protocol.information_missing.model.Openspcoop2Model model(){
	  if(org.openspcoop2.protocol.information_missing.Openspcoop2.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.protocol.information_missing.Openspcoop2.modelStaticInstance;
  }


  @XmlElement(name="wizard",required=false,nillable=false)
  protected Wizard wizard;

  @XmlElement(name="soggetto",required=true,nillable=false)
  protected List<Soggetto> soggetto = new ArrayList<Soggetto>();

  /**
   * @deprecated Use method getSoggettoList
   * @return List<Soggetto>
  */
  @Deprecated
  public List<Soggetto> getSoggetto() {
  	return this.soggetto;
  }

  /**
   * @deprecated Use method setSoggettoList
   * @param soggetto List<Soggetto>
  */
  @Deprecated
  public void setSoggetto(List<Soggetto> soggetto) {
  	this.soggetto=soggetto;
  }

  /**
   * @deprecated Use method sizeSoggettoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSoggetto() {
  	return this.soggetto.size();
  }

  @XmlElement(name="input",required=true,nillable=false)
  protected List<Input> input = new ArrayList<Input>();

  /**
   * @deprecated Use method getInputList
   * @return List<Input>
  */
  @Deprecated
  public List<Input> getInput() {
  	return this.input;
  }

  /**
   * @deprecated Use method setInputList
   * @param input List<Input>
  */
  @Deprecated
  public void setInput(List<Input> input) {
  	this.input=input;
  }

  /**
   * @deprecated Use method sizeInputList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeInput() {
  	return this.input.size();
  }

  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  protected List<ServizioApplicativo> servizioApplicativo = new ArrayList<ServizioApplicativo>();

  /**
   * @deprecated Use method getServizioApplicativoList
   * @return List<ServizioApplicativo>
  */
  @Deprecated
  public List<ServizioApplicativo> getServizioApplicativo() {
  	return this.servizioApplicativo;
  }

  /**
   * @deprecated Use method setServizioApplicativoList
   * @param servizioApplicativo List<ServizioApplicativo>
  */
  @Deprecated
  public void setServizioApplicativo(List<ServizioApplicativo> servizioApplicativo) {
  	this.servizioApplicativo=servizioApplicativo;
  }

  /**
   * @deprecated Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioApplicativo() {
  	return this.servizioApplicativo.size();
  }

  @XmlElement(name="accordo-cooperazione",required=true,nillable=false)
  protected List<AccordoCooperazione> accordoCooperazione = new ArrayList<AccordoCooperazione>();

  /**
   * @deprecated Use method getAccordoCooperazioneList
   * @return List<AccordoCooperazione>
  */
  @Deprecated
  public List<AccordoCooperazione> getAccordoCooperazione() {
  	return this.accordoCooperazione;
  }

  /**
   * @deprecated Use method setAccordoCooperazioneList
   * @param accordoCooperazione List<AccordoCooperazione>
  */
  @Deprecated
  public void setAccordoCooperazione(List<AccordoCooperazione> accordoCooperazione) {
  	this.accordoCooperazione=accordoCooperazione;
  }

  /**
   * @deprecated Use method sizeAccordoCooperazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAccordoCooperazione() {
  	return this.accordoCooperazione.size();
  }

  @XmlElement(name="accordo-servizio-parte-comune",required=true,nillable=false)
  protected List<AccordoServizioParteComune> accordoServizioParteComune = new ArrayList<AccordoServizioParteComune>();

  /**
   * @deprecated Use method getAccordoServizioParteComuneList
   * @return List<AccordoServizioParteComune>
  */
  @Deprecated
  public List<AccordoServizioParteComune> getAccordoServizioParteComune() {
  	return this.accordoServizioParteComune;
  }

  /**
   * @deprecated Use method setAccordoServizioParteComuneList
   * @param accordoServizioParteComune List<AccordoServizioParteComune>
  */
  @Deprecated
  public void setAccordoServizioParteComune(List<AccordoServizioParteComune> accordoServizioParteComune) {
  	this.accordoServizioParteComune=accordoServizioParteComune;
  }

  /**
   * @deprecated Use method sizeAccordoServizioParteComuneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAccordoServizioParteComune() {
  	return this.accordoServizioParteComune.size();
  }

  @XmlElement(name="accordo-servizio-parte-specifica",required=true,nillable=false)
  protected List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica = new ArrayList<AccordoServizioParteSpecifica>();

  /**
   * @deprecated Use method getAccordoServizioParteSpecificaList
   * @return List<AccordoServizioParteSpecifica>
  */
  @Deprecated
  public List<AccordoServizioParteSpecifica> getAccordoServizioParteSpecifica() {
  	return this.accordoServizioParteSpecifica;
  }

  /**
   * @deprecated Use method setAccordoServizioParteSpecificaList
   * @param accordoServizioParteSpecifica List<AccordoServizioParteSpecifica>
  */
  @Deprecated
  public void setAccordoServizioParteSpecifica(List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica) {
  	this.accordoServizioParteSpecifica=accordoServizioParteSpecifica;
  }

  /**
   * @deprecated Use method sizeAccordoServizioParteSpecificaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAccordoServizioParteSpecifica() {
  	return this.accordoServizioParteSpecifica.size();
  }

  @XmlElement(name="accordo-servizio-composto",required=true,nillable=false)
  protected List<AccordoServizioParteComune> accordoServizioComposto = new ArrayList<AccordoServizioParteComune>();

  /**
   * @deprecated Use method getAccordoServizioCompostoList
   * @return List<AccordoServizioParteComune>
  */
  @Deprecated
  public List<AccordoServizioParteComune> getAccordoServizioComposto() {
  	return this.accordoServizioComposto;
  }

  /**
   * @deprecated Use method setAccordoServizioCompostoList
   * @param accordoServizioComposto List<AccordoServizioParteComune>
  */
  @Deprecated
  public void setAccordoServizioComposto(List<AccordoServizioParteComune> accordoServizioComposto) {
  	this.accordoServizioComposto=accordoServizioComposto;
  }

  /**
   * @deprecated Use method sizeAccordoServizioCompostoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAccordoServizioComposto() {
  	return this.accordoServizioComposto.size();
  }

  @XmlElement(name="fruitore",required=true,nillable=false)
  protected List<Fruitore> fruitore = new ArrayList<Fruitore>();

  /**
   * @deprecated Use method getFruitoreList
   * @return List<Fruitore>
  */
  @Deprecated
  public List<Fruitore> getFruitore() {
  	return this.fruitore;
  }

  /**
   * @deprecated Use method setFruitoreList
   * @param fruitore List<Fruitore>
  */
  @Deprecated
  public void setFruitore(List<Fruitore> fruitore) {
  	this.fruitore=fruitore;
  }

  /**
   * @deprecated Use method sizeFruitoreList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeFruitore() {
  	return this.fruitore.size();
  }

}

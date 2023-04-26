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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for operazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/protocol/information_missing}Soggetto" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="input" type="{http://www.openspcoop2.org/protocol/information_missing}Input" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="servizio-applicativo" type="{http://www.openspcoop2.org/protocol/information_missing}ServizioApplicativo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="accordo-cooperazione" type="{http://www.openspcoop2.org/protocol/information_missing}AccordoCooperazione" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="accordo-servizio-parte-comune" type="{http://www.openspcoop2.org/protocol/information_missing}AccordoServizioParteComune" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="accordo-servizio-parte-specifica" type="{http://www.openspcoop2.org/protocol/information_missing}AccordoServizioParteSpecifica" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="accordo-servizio-composto" type="{http://www.openspcoop2.org/protocol/information_missing}AccordoServizioParteComune" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="fruitore" type="{http://www.openspcoop2.org/protocol/information_missing}Fruitore" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="porta-delegata" type="{http://www.openspcoop2.org/protocol/information_missing}PortaDelegata" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="porta-applicativa" type="{http://www.openspcoop2.org/protocol/information_missing}PortaApplicativa" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "operazione", 
  propOrder = {
  	"soggetto",
  	"input",
  	"servizioApplicativo",
  	"accordoCooperazione",
  	"accordoServizioParteComune",
  	"accordoServizioParteSpecifica",
  	"accordoServizioComposto",
  	"fruitore",
  	"portaDelegata",
  	"portaApplicativa"
  }
)

@XmlRootElement(name = "operazione")

public class Operazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Operazione() {
    super();
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

  public void addPortaDelegata(PortaDelegata portaDelegata) {
    this.portaDelegata.add(portaDelegata);
  }

  public PortaDelegata getPortaDelegata(int index) {
    return this.portaDelegata.get( index );
  }

  public PortaDelegata removePortaDelegata(int index) {
    return this.portaDelegata.remove( index );
  }

  public List<PortaDelegata> getPortaDelegataList() {
    return this.portaDelegata;
  }

  public void setPortaDelegataList(List<PortaDelegata> portaDelegata) {
    this.portaDelegata=portaDelegata;
  }

  public int sizePortaDelegataList() {
    return this.portaDelegata.size();
  }

  public void addPortaApplicativa(PortaApplicativa portaApplicativa) {
    this.portaApplicativa.add(portaApplicativa);
  }

  public PortaApplicativa getPortaApplicativa(int index) {
    return this.portaApplicativa.get( index );
  }

  public PortaApplicativa removePortaApplicativa(int index) {
    return this.portaApplicativa.remove( index );
  }

  public List<PortaApplicativa> getPortaApplicativaList() {
    return this.portaApplicativa;
  }

  public void setPortaApplicativaList(List<PortaApplicativa> portaApplicativa) {
    this.portaApplicativa=portaApplicativa;
  }

  public int sizePortaApplicativaList() {
    return this.portaApplicativa.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="soggetto",required=true,nillable=false)
  private List<Soggetto> soggetto = new ArrayList<>();

  @XmlElement(name="input",required=true,nillable=false)
  private List<Input> input = new ArrayList<>();

  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  private List<ServizioApplicativo> servizioApplicativo = new ArrayList<>();

  @XmlElement(name="accordo-cooperazione",required=true,nillable=false)
  private List<AccordoCooperazione> accordoCooperazione = new ArrayList<>();

  @XmlElement(name="accordo-servizio-parte-comune",required=true,nillable=false)
  private List<AccordoServizioParteComune> accordoServizioParteComune = new ArrayList<>();

  @XmlElement(name="accordo-servizio-parte-specifica",required=true,nillable=false)
  private List<AccordoServizioParteSpecifica> accordoServizioParteSpecifica = new ArrayList<>();

  @XmlElement(name="accordo-servizio-composto",required=true,nillable=false)
  private List<AccordoServizioParteComune> accordoServizioComposto = new ArrayList<>();

  @XmlElement(name="fruitore",required=true,nillable=false)
  private List<Fruitore> fruitore = new ArrayList<>();

  @XmlElement(name="porta-delegata",required=true,nillable=false)
  private List<PortaDelegata> portaDelegata = new ArrayList<>();

  @XmlElement(name="porta-applicativa",required=true,nillable=false)
  private List<PortaApplicativa> portaApplicativa = new ArrayList<>();

}

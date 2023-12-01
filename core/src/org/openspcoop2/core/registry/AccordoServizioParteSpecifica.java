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
package org.openspcoop2.core.registry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accordo-servizio-parte-specifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-specifica"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione-servizio" type="{http://www.openspcoop2.org/core/registry}configurazione-servizio" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="fruitore" type="{http://www.openspcoop2.org/core/registry}fruitore" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="allegato" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="specifica-semiformale" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="specifica-livello-servizio" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="specifica-sicurezza" type="{http://www.openspcoop2.org/core/registry}documento" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/registry}protocol-property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta-oggetto" type="{http://www.openspcoop2.org/core/registry}proprieta-oggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="privato" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="false"/&gt;
 * 		&lt;attribute name="id-accordo" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="id-soggetto" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="byte-wsdl-implementativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/&gt;
 * 		&lt;attribute name="byte-wsdl-implementativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}base64Binary" use="optional"/&gt;
 * 		&lt;attribute name="tipo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="versione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional" default="1"/&gt;
 * 		&lt;attribute name="accordo-servizio-parte-comune" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="tipologia-servizio" type="{http://www.openspcoop2.org/core/registry}TipologiaServizio" use="optional" default="normale"/&gt;
 * 		&lt;attribute name="port-type" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="wsdl-implementativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="wsdl-implementativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="filtro-duplicati" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="conferma-ricezione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="id-collaborazione" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="id-riferimento-richiesta" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="consegna-in-ordine" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="scadenza" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="versione-protocollo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordo-servizio-parte-specifica", 
  propOrder = {
  	"configurazioneServizio",
  	"fruitore",
  	"allegato",
  	"specificaSemiformale",
  	"specificaLivelloServizio",
  	"specificaSicurezza",
  	"protocolProperty",
  	"proprietaOggetto"
  }
)

@XmlRootElement(name = "accordo-servizio-parte-specifica")

public class AccordoServizioParteSpecifica extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AccordoServizioParteSpecifica() {
    super();
  }

  public IDServizio getOldIDServizioForUpdate() {
    return this.oldIDServizioForUpdate;
  }

  public void setOldIDServizioForUpdate(IDServizio oldIDServizioForUpdate) {
    this.oldIDServizioForUpdate=oldIDServizioForUpdate;
  }

  public ConfigurazioneServizio getConfigurazioneServizio() {
    return this.configurazioneServizio;
  }

  public void setConfigurazioneServizio(ConfigurazioneServizio configurazioneServizio) {
    this.configurazioneServizio = configurazioneServizio;
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

  public void addAllegato(Documento allegato) {
    this.allegato.add(allegato);
  }

  public Documento getAllegato(int index) {
    return this.allegato.get( index );
  }

  public Documento removeAllegato(int index) {
    return this.allegato.remove( index );
  }

  public List<Documento> getAllegatoList() {
    return this.allegato;
  }

  public void setAllegatoList(List<Documento> allegato) {
    this.allegato=allegato;
  }

  public int sizeAllegatoList() {
    return this.allegato.size();
  }

  public void addSpecificaSemiformale(Documento specificaSemiformale) {
    this.specificaSemiformale.add(specificaSemiformale);
  }

  public Documento getSpecificaSemiformale(int index) {
    return this.specificaSemiformale.get( index );
  }

  public Documento removeSpecificaSemiformale(int index) {
    return this.specificaSemiformale.remove( index );
  }

  public List<Documento> getSpecificaSemiformaleList() {
    return this.specificaSemiformale;
  }

  public void setSpecificaSemiformaleList(List<Documento> specificaSemiformale) {
    this.specificaSemiformale=specificaSemiformale;
  }

  public int sizeSpecificaSemiformaleList() {
    return this.specificaSemiformale.size();
  }

  public void addSpecificaLivelloServizio(Documento specificaLivelloServizio) {
    this.specificaLivelloServizio.add(specificaLivelloServizio);
  }

  public Documento getSpecificaLivelloServizio(int index) {
    return this.specificaLivelloServizio.get( index );
  }

  public Documento removeSpecificaLivelloServizio(int index) {
    return this.specificaLivelloServizio.remove( index );
  }

  public List<Documento> getSpecificaLivelloServizioList() {
    return this.specificaLivelloServizio;
  }

  public void setSpecificaLivelloServizioList(List<Documento> specificaLivelloServizio) {
    this.specificaLivelloServizio=specificaLivelloServizio;
  }

  public int sizeSpecificaLivelloServizioList() {
    return this.specificaLivelloServizio.size();
  }

  public void addSpecificaSicurezza(Documento specificaSicurezza) {
    this.specificaSicurezza.add(specificaSicurezza);
  }

  public Documento getSpecificaSicurezza(int index) {
    return this.specificaSicurezza.get( index );
  }

  public Documento removeSpecificaSicurezza(int index) {
    return this.specificaSicurezza.remove( index );
  }

  public List<Documento> getSpecificaSicurezzaList() {
    return this.specificaSicurezza;
  }

  public void setSpecificaSicurezzaList(List<Documento> specificaSicurezza) {
    this.specificaSicurezza=specificaSicurezza;
  }

  public int sizeSpecificaSicurezzaList() {
    return this.specificaSicurezza.size();
  }

  public void addProtocolProperty(ProtocolProperty protocolProperty) {
    this.protocolProperty.add(protocolProperty);
  }

  public ProtocolProperty getProtocolProperty(int index) {
    return this.protocolProperty.get( index );
  }

  public ProtocolProperty removeProtocolProperty(int index) {
    return this.protocolProperty.remove( index );
  }

  public List<ProtocolProperty> getProtocolPropertyList() {
    return this.protocolProperty;
  }

  public void setProtocolPropertyList(List<ProtocolProperty> protocolProperty) {
    this.protocolProperty=protocolProperty;
  }

  public int sizeProtocolPropertyList() {
    return this.protocolProperty.size();
  }

  public ProprietaOggetto getProprietaOggetto() {
    return this.proprietaOggetto;
  }

  public void setProprietaOggetto(ProprietaOggetto proprietaOggetto) {
    this.proprietaOggetto = proprietaOggetto;
  }

  public java.lang.String getSuperUser() {
    return this.superUser;
  }

  public void setSuperUser(java.lang.String superUser) {
    this.superUser = superUser;
  }

  public java.lang.String getStatoPackage() {
    return this.statoPackage;
  }

  public void setStatoPackage(java.lang.String statoPackage) {
    this.statoPackage = statoPackage;
  }

  public Boolean getPrivato() {
    return this.privato;
  }

  public void setPrivato(Boolean privato) {
    this.privato = privato;
  }

  public java.lang.Long getIdAccordo() {
    return this.idAccordo;
  }

  public void setIdAccordo(java.lang.Long idAccordo) {
    this.idAccordo = idAccordo;
  }

  public java.lang.Long getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(java.lang.Long idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public byte[] getByteWsdlImplementativoErogatore() {
    return this.byteWsdlImplementativoErogatore;
  }

  public void setByteWsdlImplementativoErogatore(byte[] byteWsdlImplementativoErogatore) {
    this.byteWsdlImplementativoErogatore = byteWsdlImplementativoErogatore;
  }

  public byte[] getByteWsdlImplementativoFruitore() {
    return this.byteWsdlImplementativoFruitore;
  }

  public void setByteWsdlImplementativoFruitore(byte[] byteWsdlImplementativoFruitore) {
    this.byteWsdlImplementativoFruitore = byteWsdlImplementativoFruitore;
  }

  public java.lang.String getTipoSoggettoErogatore() {
    return this.tipoSoggettoErogatore;
  }

  public void setTipoSoggettoErogatore(java.lang.String tipoSoggettoErogatore) {
    this.tipoSoggettoErogatore = tipoSoggettoErogatore;
  }

  public java.lang.String getNomeSoggettoErogatore() {
    return this.nomeSoggettoErogatore;
  }

  public void setNomeSoggettoErogatore(java.lang.String nomeSoggettoErogatore) {
    this.nomeSoggettoErogatore = nomeSoggettoErogatore;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.Integer getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.Integer versione) {
    this.versione = versione;
  }

  public java.lang.String getAccordoServizioParteComune() {
    return this.accordoServizioParteComune;
  }

  public void setAccordoServizioParteComune(java.lang.String accordoServizioParteComune) {
    this.accordoServizioParteComune = accordoServizioParteComune;
  }

  public void setTipologiaServizioRawEnumValue(String value) {
    this.tipologiaServizio = (TipologiaServizio) TipologiaServizio.toEnumConstantFromString(value);
  }

  public String getTipologiaServizioRawEnumValue() {
    if(this.tipologiaServizio == null){
    	return null;
    }else{
    	return this.tipologiaServizio.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.TipologiaServizio getTipologiaServizio() {
    return this.tipologiaServizio;
  }

  public void setTipologiaServizio(org.openspcoop2.core.registry.constants.TipologiaServizio tipologiaServizio) {
    this.tipologiaServizio = tipologiaServizio;
  }

  public java.lang.String getPortType() {
    return this.portType;
  }

  public void setPortType(java.lang.String portType) {
    this.portType = portType;
  }

  public java.lang.String getWsdlImplementativoErogatore() {
    return this.wsdlImplementativoErogatore;
  }

  public void setWsdlImplementativoErogatore(java.lang.String wsdlImplementativoErogatore) {
    this.wsdlImplementativoErogatore = wsdlImplementativoErogatore;
  }

  public java.lang.String getWsdlImplementativoFruitore() {
    return this.wsdlImplementativoFruitore;
  }

  public void setWsdlImplementativoFruitore(java.lang.String wsdlImplementativoFruitore) {
    this.wsdlImplementativoFruitore = wsdlImplementativoFruitore;
  }

  public void setFiltroDuplicatiRawEnumValue(String value) {
    this.filtroDuplicati = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getFiltroDuplicatiRawEnumValue() {
    if(this.filtroDuplicati == null){
    	return null;
    }else{
    	return this.filtroDuplicati.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getFiltroDuplicati() {
    return this.filtroDuplicati;
  }

  public void setFiltroDuplicati(org.openspcoop2.core.registry.constants.StatoFunzionalita filtroDuplicati) {
    this.filtroDuplicati = filtroDuplicati;
  }

  public void setConfermaRicezioneRawEnumValue(String value) {
    this.confermaRicezione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getConfermaRicezioneRawEnumValue() {
    if(this.confermaRicezione == null){
    	return null;
    }else{
    	return this.confermaRicezione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getConfermaRicezione() {
    return this.confermaRicezione;
  }

  public void setConfermaRicezione(org.openspcoop2.core.registry.constants.StatoFunzionalita confermaRicezione) {
    this.confermaRicezione = confermaRicezione;
  }

  public void setIdCollaborazioneRawEnumValue(String value) {
    this.idCollaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getIdCollaborazioneRawEnumValue() {
    if(this.idCollaborazione == null){
    	return null;
    }else{
    	return this.idCollaborazione.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getIdCollaborazione() {
    return this.idCollaborazione;
  }

  public void setIdCollaborazione(org.openspcoop2.core.registry.constants.StatoFunzionalita idCollaborazione) {
    this.idCollaborazione = idCollaborazione;
  }

  public void setIdRiferimentoRichiestaRawEnumValue(String value) {
    this.idRiferimentoRichiesta = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getIdRiferimentoRichiestaRawEnumValue() {
    if(this.idRiferimentoRichiesta == null){
    	return null;
    }else{
    	return this.idRiferimentoRichiesta.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getIdRiferimentoRichiesta() {
    return this.idRiferimentoRichiesta;
  }

  public void setIdRiferimentoRichiesta(org.openspcoop2.core.registry.constants.StatoFunzionalita idRiferimentoRichiesta) {
    this.idRiferimentoRichiesta = idRiferimentoRichiesta;
  }

  public void setConsegnaInOrdineRawEnumValue(String value) {
    this.consegnaInOrdine = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getConsegnaInOrdineRawEnumValue() {
    if(this.consegnaInOrdine == null){
    	return null;
    }else{
    	return this.consegnaInOrdine.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getConsegnaInOrdine() {
    return this.consegnaInOrdine;
  }

  public void setConsegnaInOrdine(org.openspcoop2.core.registry.constants.StatoFunzionalita consegnaInOrdine) {
    this.consegnaInOrdine = consegnaInOrdine;
  }

  public java.lang.String getScadenza() {
    return this.scadenza;
  }

  public void setScadenza(java.lang.String scadenza) {
    this.scadenza = scadenza;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getVersioneProtocollo() {
    return this.versioneProtocollo;
  }

  public void setVersioneProtocollo(java.lang.String versioneProtocollo) {
    this.versioneProtocollo = versioneProtocollo;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public void setMessageTypeRawEnumValue(String value) {
    this.messageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String getMessageTypeRawEnumValue() {
    if(this.messageType == null){
    	return null;
    }else{
    	return this.messageType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.MessageType getMessageType() {
    return this.messageType;
  }

  public void setMessageType(org.openspcoop2.core.registry.constants.MessageType messageType) {
    this.messageType = messageType;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.registry.model.AccordoServizioParteSpecificaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.registry.AccordoServizioParteSpecifica.modelStaticInstance==null){
  			org.openspcoop2.core.registry.AccordoServizioParteSpecifica.modelStaticInstance = new org.openspcoop2.core.registry.model.AccordoServizioParteSpecificaModel();
	  }
  }
  public static org.openspcoop2.core.registry.model.AccordoServizioParteSpecificaModel model(){
	  if(org.openspcoop2.core.registry.AccordoServizioParteSpecifica.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.registry.AccordoServizioParteSpecifica.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlTransient
  protected IDServizio oldIDServizioForUpdate;

  @XmlElement(name="configurazione-servizio",required=false,nillable=false)
  protected ConfigurazioneServizio configurazioneServizio;

  @XmlElement(name="fruitore",required=true,nillable=false)
  private List<Fruitore> fruitore = new ArrayList<>();

  /**
   * Use method getFruitoreList
   * @return List&lt;Fruitore&gt;
  */
  public List<Fruitore> getFruitore() {
  	return this.getFruitoreList();
  }

  /**
   * Use method setFruitoreList
   * @param fruitore List&lt;Fruitore&gt;
  */
  public void setFruitore(List<Fruitore> fruitore) {
  	this.setFruitoreList(fruitore);
  }

  /**
   * Use method sizeFruitoreList
   * @return lunghezza della lista
  */
  public int sizeFruitore() {
  	return this.sizeFruitoreList();
  }

  @XmlElement(name="allegato",required=true,nillable=false)
  private List<Documento> allegato = new ArrayList<>();

  /**
   * Use method getAllegatoList
   * @return List&lt;Documento&gt;
  */
  public List<Documento> getAllegato() {
  	return this.getAllegatoList();
  }

  /**
   * Use method setAllegatoList
   * @param allegato List&lt;Documento&gt;
  */
  public void setAllegato(List<Documento> allegato) {
  	this.setAllegatoList(allegato);
  }

  /**
   * Use method sizeAllegatoList
   * @return lunghezza della lista
  */
  public int sizeAllegato() {
  	return this.sizeAllegatoList();
  }

  @XmlElement(name="specifica-semiformale",required=true,nillable=false)
  private List<Documento> specificaSemiformale = new ArrayList<>();

  /**
   * Use method getSpecificaSemiformaleList
   * @return List&lt;Documento&gt;
  */
  public List<Documento> getSpecificaSemiformale() {
  	return this.getSpecificaSemiformaleList();
  }

  /**
   * Use method setSpecificaSemiformaleList
   * @param specificaSemiformale List&lt;Documento&gt;
  */
  public void setSpecificaSemiformale(List<Documento> specificaSemiformale) {
  	this.setSpecificaSemiformaleList(specificaSemiformale);
  }

  /**
   * Use method sizeSpecificaSemiformaleList
   * @return lunghezza della lista
  */
  public int sizeSpecificaSemiformale() {
  	return this.sizeSpecificaSemiformaleList();
  }

  @XmlElement(name="specifica-livello-servizio",required=true,nillable=false)
  private List<Documento> specificaLivelloServizio = new ArrayList<>();

  /**
   * Use method getSpecificaLivelloServizioList
   * @return List&lt;Documento&gt;
  */
  public List<Documento> getSpecificaLivelloServizio() {
  	return this.getSpecificaLivelloServizioList();
  }

  /**
   * Use method setSpecificaLivelloServizioList
   * @param specificaLivelloServizio List&lt;Documento&gt;
  */
  public void setSpecificaLivelloServizio(List<Documento> specificaLivelloServizio) {
  	this.setSpecificaLivelloServizioList(specificaLivelloServizio);
  }

  /**
   * Use method sizeSpecificaLivelloServizioList
   * @return lunghezza della lista
  */
  public int sizeSpecificaLivelloServizio() {
  	return this.sizeSpecificaLivelloServizioList();
  }

  @XmlElement(name="specifica-sicurezza",required=true,nillable=false)
  private List<Documento> specificaSicurezza = new ArrayList<>();

  /**
   * Use method getSpecificaSicurezzaList
   * @return List&lt;Documento&gt;
  */
  public List<Documento> getSpecificaSicurezza() {
  	return this.getSpecificaSicurezzaList();
  }

  /**
   * Use method setSpecificaSicurezzaList
   * @param specificaSicurezza List&lt;Documento&gt;
  */
  public void setSpecificaSicurezza(List<Documento> specificaSicurezza) {
  	this.setSpecificaSicurezzaList(specificaSicurezza);
  }

  /**
   * Use method sizeSpecificaSicurezzaList
   * @return lunghezza della lista
  */
  public int sizeSpecificaSicurezza() {
  	return this.sizeSpecificaSicurezzaList();
  }

  @XmlElement(name="protocol-property",required=true,nillable=false)
  private List<ProtocolProperty> protocolProperty = new ArrayList<>();

  /**
   * Use method getProtocolPropertyList
   * @return List&lt;ProtocolProperty&gt;
  */
  public List<ProtocolProperty> getProtocolProperty() {
  	return this.getProtocolPropertyList();
  }

  /**
   * Use method setProtocolPropertyList
   * @param protocolProperty List&lt;ProtocolProperty&gt;
  */
  public void setProtocolProperty(List<ProtocolProperty> protocolProperty) {
  	this.setProtocolPropertyList(protocolProperty);
  }

  /**
   * Use method sizeProtocolPropertyList
   * @return lunghezza della lista
  */
  public int sizeProtocolProperty() {
  	return this.sizeProtocolPropertyList();
  }

  @XmlElement(name="proprieta-oggetto",required=false,nillable=false)
  protected ProprietaOggetto proprietaOggetto;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="stato-package",required=false)
  protected java.lang.String statoPackage;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="privato",required=false)
  protected Boolean privato = Boolean.valueOf("false");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.Long idAccordo;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.Long idSoggetto;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-implementativo-erogatore",required=false)
  protected byte[] byteWsdlImplementativoErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlAttribute(name="byte-wsdl-implementativo-fruitore",required=false)
  protected byte[] byteWsdlImplementativoFruitore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-erogatore",required=false)
  protected java.lang.String tipoSoggettoErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-soggetto-erogatore",required=false)
  protected java.lang.String nomeSoggettoErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlAttribute(name="versione",required=false)
  protected java.lang.Integer versione = java.lang.Integer.valueOf("1");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="accordo-servizio-parte-comune",required=true)
  protected java.lang.String accordoServizioParteComune;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipologiaServizioRawEnumValue;

  @XmlAttribute(name="tipologia-servizio",required=false)
  protected TipologiaServizio tipologiaServizio = (TipologiaServizio) TipologiaServizio.toEnumConstantFromString("normale");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="port-type",required=false)
  protected java.lang.String portType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-implementativo-erogatore",required=false)
  protected java.lang.String wsdlImplementativoErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="wsdl-implementativo-fruitore",required=false)
  protected java.lang.String wsdlImplementativoFruitore;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String filtroDuplicatiRawEnumValue;

  @XmlAttribute(name="filtro-duplicati",required=false)
  protected StatoFunzionalita filtroDuplicati;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String confermaRicezioneRawEnumValue;

  @XmlAttribute(name="conferma-ricezione",required=false)
  protected StatoFunzionalita confermaRicezione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String idCollaborazioneRawEnumValue;

  @XmlAttribute(name="id-collaborazione",required=false)
  protected StatoFunzionalita idCollaborazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String idRiferimentoRichiestaRawEnumValue;

  @XmlAttribute(name="id-riferimento-richiesta",required=false)
  protected StatoFunzionalita idRiferimentoRichiesta;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String consegnaInOrdineRawEnumValue;

  @XmlAttribute(name="consegna-in-ordine",required=false)
  protected StatoFunzionalita consegnaInOrdine;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="scadenza",required=false)
  protected java.lang.String scadenza;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione-protocollo",required=false)
  protected java.lang.String versioneProtocollo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String messageTypeRawEnumValue;

  @XmlAttribute(name="message-type",required=false)
  protected MessageType messageType;

}

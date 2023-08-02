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
package org.openspcoop2.core.config.rs.server.model;

import java.util.List;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class FruizioneModISoapRichiestaSicurezzaMessaggio  {
  
  @Schema(description = "")
  private ModISicurezzaMessaggioSoapAlgoritmoFirma algoritmo = null;
  
  @Schema(description = "")
  private ModISicurezzaMessaggioSoapFormaCanonicaXml formaCanonicaXml = null;
  
  @Schema(description = "")
  private List<String> headerSoapFirmare = null;
  
  @Schema(description = "")
  private ModISicurezzaMessaggioSoapRiferimentoX509 riferimentoX509 = null;
  
  @Schema(description = "")
  private Boolean certificateChain = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreRidefinito.class, name = "ridefinito")  })
  private OneOfFruizioneModISoapRichiestaSicurezzaMessaggioKeystore keystore = null;
  
  @Schema(description = "")
  private Boolean includiSignatureToken = null;
  
  @Schema(description = "")
  private Integer timeToLive = 300;
  
  @Schema(description = "")
  private String wsaTo = null;
  
  @Schema(description = "")
  private FruizioneModIRichiestaInformazioneUtente informazioniUtenteCodiceEnte = null;
  
  @Schema(description = "")
  private FruizioneModIRichiestaInformazioneUtente informazioniUtenteUserid = null;
  
  @Schema(description = "")
  private FruizioneModIRichiestaInformazioneUtente informazioniUtenteIndirizzoIp = null;
  
  @Schema(description = "")
  private FruizioneModIRichiestaInformazioniUtenteAudit audit = null;
 /**
   * Get algoritmo
   * @return algoritmo
  **/
  @JsonProperty("algoritmo")
  @Valid
  public ModISicurezzaMessaggioSoapAlgoritmoFirma getAlgoritmo() {
    return this.algoritmo;
  }

  public void setAlgoritmo(ModISicurezzaMessaggioSoapAlgoritmoFirma algoritmo) {
    this.algoritmo = algoritmo;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio algoritmo(ModISicurezzaMessaggioSoapAlgoritmoFirma algoritmo) {
    this.algoritmo = algoritmo;
    return this;
  }

 /**
   * Get formaCanonicaXml
   * @return formaCanonicaXml
  **/
  @JsonProperty("forma_canonica_xml")
  @Valid
  public ModISicurezzaMessaggioSoapFormaCanonicaXml getFormaCanonicaXml() {
    return this.formaCanonicaXml;
  }

  public void setFormaCanonicaXml(ModISicurezzaMessaggioSoapFormaCanonicaXml formaCanonicaXml) {
    this.formaCanonicaXml = formaCanonicaXml;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio formaCanonicaXml(ModISicurezzaMessaggioSoapFormaCanonicaXml formaCanonicaXml) {
    this.formaCanonicaXml = formaCanonicaXml;
    return this;
  }

 /**
   * Get headerSoapFirmare
   * @return headerSoapFirmare
  **/
  @JsonProperty("header_soap_firmare")
  @Valid
  public List<String> getHeaderSoapFirmare() {
    return this.headerSoapFirmare;
  }

  public void setHeaderSoapFirmare(List<String> headerSoapFirmare) {
    this.headerSoapFirmare = headerSoapFirmare;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio headerSoapFirmare(List<String> headerSoapFirmare) {
    this.headerSoapFirmare = headerSoapFirmare;
    return this;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio addHeaderSoapFirmareItem(String headerSoapFirmareItem) {
    this.headerSoapFirmare.add(headerSoapFirmareItem);
    return this;
  }

 /**
   * Get riferimentoX509
   * @return riferimentoX509
  **/
  @JsonProperty("riferimento_x509")
  @Valid
  public ModISicurezzaMessaggioSoapRiferimentoX509 getRiferimentoX509() {
    return this.riferimentoX509;
  }

  public void setRiferimentoX509(ModISicurezzaMessaggioSoapRiferimentoX509 riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio riferimentoX509(ModISicurezzaMessaggioSoapRiferimentoX509 riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
    return this;
  }

 /**
   * Get certificateChain
   * @return certificateChain
  **/
  @JsonProperty("certificate_chain")
  @Valid
  public Boolean isCertificateChain() {
    return this.certificateChain;
  }

  public void setCertificateChain(Boolean certificateChain) {
    this.certificateChain = certificateChain;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio certificateChain(Boolean certificateChain) {
    this.certificateChain = certificateChain;
    return this;
  }

 /**
   * Get keystore
   * @return keystore
  **/
  @JsonProperty("keystore")
  @Valid
  public OneOfFruizioneModISoapRichiestaSicurezzaMessaggioKeystore getKeystore() {
    return this.keystore;
  }

  public void setKeystore(OneOfFruizioneModISoapRichiestaSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio keystore(OneOfFruizioneModISoapRichiestaSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
    return this;
  }

 /**
   * Get includiSignatureToken
   * @return includiSignatureToken
  **/
  @JsonProperty("includi_signature_token")
  @Valid
  public Boolean isIncludiSignatureToken() {
    return this.includiSignatureToken;
  }

  public void setIncludiSignatureToken(Boolean includiSignatureToken) {
    this.includiSignatureToken = includiSignatureToken;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio includiSignatureToken(Boolean includiSignatureToken) {
    this.includiSignatureToken = includiSignatureToken;
    return this;
  }

 /**
   * Get timeToLive
   * @return timeToLive
  **/
  @JsonProperty("time_to_live")
  @Valid
  public Integer getTimeToLive() {
    return this.timeToLive;
  }

  public void setTimeToLive(Integer timeToLive) {
    this.timeToLive = timeToLive;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio timeToLive(Integer timeToLive) {
    this.timeToLive = timeToLive;
    return this;
  }

 /**
   * Get wsaTo
   * @return wsaTo
  **/
  @JsonProperty("wsa_to")
  @Valid
 @Size(max=4000)  public String getWsaTo() {
    return this.wsaTo;
  }

  public void setWsaTo(String wsaTo) {
    this.wsaTo = wsaTo;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio wsaTo(String wsaTo) {
    this.wsaTo = wsaTo;
    return this;
  }

 /**
   * Get informazioniUtenteCodiceEnte
   * @return informazioniUtenteCodiceEnte
  **/
  @JsonProperty("informazioni_utente_codice_ente")
  @Valid
  public FruizioneModIRichiestaInformazioneUtente getInformazioniUtenteCodiceEnte() {
    return this.informazioniUtenteCodiceEnte;
  }

  public void setInformazioniUtenteCodiceEnte(FruizioneModIRichiestaInformazioneUtente informazioniUtenteCodiceEnte) {
    this.informazioniUtenteCodiceEnte = informazioniUtenteCodiceEnte;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio informazioniUtenteCodiceEnte(FruizioneModIRichiestaInformazioneUtente informazioniUtenteCodiceEnte) {
    this.informazioniUtenteCodiceEnte = informazioniUtenteCodiceEnte;
    return this;
  }

 /**
   * Get informazioniUtenteUserid
   * @return informazioniUtenteUserid
  **/
  @JsonProperty("informazioni_utente_userid")
  @Valid
  public FruizioneModIRichiestaInformazioneUtente getInformazioniUtenteUserid() {
    return this.informazioniUtenteUserid;
  }

  public void setInformazioniUtenteUserid(FruizioneModIRichiestaInformazioneUtente informazioniUtenteUserid) {
    this.informazioniUtenteUserid = informazioniUtenteUserid;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio informazioniUtenteUserid(FruizioneModIRichiestaInformazioneUtente informazioniUtenteUserid) {
    this.informazioniUtenteUserid = informazioniUtenteUserid;
    return this;
  }

 /**
   * Get informazioniUtenteIndirizzoIp
   * @return informazioniUtenteIndirizzoIp
  **/
  @JsonProperty("informazioni_utente_indirizzo_ip")
  @Valid
  public FruizioneModIRichiestaInformazioneUtente getInformazioniUtenteIndirizzoIp() {
    return this.informazioniUtenteIndirizzoIp;
  }

  public void setInformazioniUtenteIndirizzoIp(FruizioneModIRichiestaInformazioneUtente informazioniUtenteIndirizzoIp) {
    this.informazioniUtenteIndirizzoIp = informazioniUtenteIndirizzoIp;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio informazioniUtenteIndirizzoIp(FruizioneModIRichiestaInformazioneUtente informazioniUtenteIndirizzoIp) {
    this.informazioniUtenteIndirizzoIp = informazioniUtenteIndirizzoIp;
    return this;
  }

 /**
   * Get audit
   * @return audit
  **/
  @JsonProperty("audit")
  @Valid
  public FruizioneModIRichiestaInformazioniUtenteAudit getAudit() {
    return this.audit;
  }

  public void setAudit(FruizioneModIRichiestaInformazioniUtenteAudit audit) {
    this.audit = audit;
  }

  public FruizioneModISoapRichiestaSicurezzaMessaggio audit(FruizioneModIRichiestaInformazioniUtenteAudit audit) {
    this.audit = audit;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModISoapRichiestaSicurezzaMessaggio {\n");
    
    sb.append("    algoritmo: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.algoritmo)).append("\n");
    sb.append("    formaCanonicaXml: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.formaCanonicaXml)).append("\n");
    sb.append("    headerSoapFirmare: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.headerSoapFirmare)).append("\n");
    sb.append("    riferimentoX509: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.riferimentoX509)).append("\n");
    sb.append("    certificateChain: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.certificateChain)).append("\n");
    sb.append("    keystore: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.keystore)).append("\n");
    sb.append("    includiSignatureToken: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.includiSignatureToken)).append("\n");
    sb.append("    timeToLive: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.timeToLive)).append("\n");
    sb.append("    wsaTo: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.wsaTo)).append("\n");
    sb.append("    informazioniUtenteCodiceEnte: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.informazioniUtenteCodiceEnte)).append("\n");
    sb.append("    informazioniUtenteUserid: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.informazioniUtenteUserid)).append("\n");
    sb.append("    informazioniUtenteIndirizzoIp: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.informazioniUtenteIndirizzoIp)).append("\n");
    sb.append("    audit: ").append(FruizioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.audit)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

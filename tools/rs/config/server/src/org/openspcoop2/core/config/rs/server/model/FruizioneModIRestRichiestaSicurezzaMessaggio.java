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
package org.openspcoop2.core.config.rs.server.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class FruizioneModIRestRichiestaSicurezzaMessaggio  {
  
  @Schema(description = "")
  private ModISicurezzaMessaggioRestAlgoritmoFirma algoritmo = null;
  
  @Schema(description = "")
  private List<String> headerHttpFirmare = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509 = new ArrayList<>();
  
  @Schema(description = "")
  private Boolean certificateChain = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreRidefinito.class, name = "ridefinito")  })
  private OneOfFruizioneModIRestRichiestaSicurezzaMessaggioKeystore keystore = null;
  
  @Schema(description = "")
  private Boolean keystoreTokenPolicy = null;
  
  @Schema(description = "")
  private Integer timeToLive = 300;
  
  @Schema(description = "")
  private String audience = null;
  
  @Schema(description = "")
  private FruizioneModIRichiestaInformazioneUtente informazioniUtenteCodiceEnte = null;
  
  @Schema(description = "")
  private FruizioneModIRichiestaInformazioneUtente informazioniUtenteUserid = null;
  
  @Schema(description = "")
  private FruizioneModIRichiestaInformazioneUtente informazioniUtenteIndirizzoIp = null;
  
  @Schema(description = "")
  private FruizioneModIRichiestaInformazioniUtenteAudit audit = null;
  
  @Schema(description = "Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola")
 /**
   * Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola  
  **/
  private List<String> claims = null;
  
  @Schema(description = "")
  private FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita contemporaneita = null;
 /**
   * Get algoritmo
   * @return algoritmo
  **/
  @JsonProperty("algoritmo")
  @Valid
  public ModISicurezzaMessaggioRestAlgoritmoFirma getAlgoritmo() {
    return this.algoritmo;
  }

  public void setAlgoritmo(ModISicurezzaMessaggioRestAlgoritmoFirma algoritmo) {
    this.algoritmo = algoritmo;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio algoritmo(ModISicurezzaMessaggioRestAlgoritmoFirma algoritmo) {
    this.algoritmo = algoritmo;
    return this;
  }

 /**
   * Get headerHttpFirmare
   * @return headerHttpFirmare
  **/
  @JsonProperty("header_http_firmare")
  @Valid
  public List<String> getHeaderHttpFirmare() {
    return this.headerHttpFirmare;
  }

  public void setHeaderHttpFirmare(List<String> headerHttpFirmare) {
    this.headerHttpFirmare = headerHttpFirmare;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio headerHttpFirmare(List<String> headerHttpFirmare) {
    this.headerHttpFirmare = headerHttpFirmare;
    return this;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio addHeaderHttpFirmareItem(String headerHttpFirmareItem) {
    this.headerHttpFirmare.add(headerHttpFirmareItem);
    return this;
  }

 /**
   * Get riferimentoX509
   * @return riferimentoX509
  **/
  @JsonProperty("riferimento_x509")
  @NotNull
  @Valid
 @Size(min=1,max=3)  public List<ModISicurezzaMessaggioRestRiferimentoX509> getRiferimentoX509() {
    return this.riferimentoX509;
  }

  public void setRiferimentoX509(List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio riferimentoX509(List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
    return this;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio addRiferimentoX509Item(ModISicurezzaMessaggioRestRiferimentoX509 riferimentoX509Item) {
    this.riferimentoX509.add(riferimentoX509Item);
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

  public FruizioneModIRestRichiestaSicurezzaMessaggio certificateChain(Boolean certificateChain) {
    this.certificateChain = certificateChain;
    return this;
  }

 /**
   * Get keystore
   * @return keystore
  **/
  @JsonProperty("keystore")
  @Valid
  public OneOfFruizioneModIRestRichiestaSicurezzaMessaggioKeystore getKeystore() {
    return this.keystore;
  }

  public void setKeystore(OneOfFruizioneModIRestRichiestaSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio keystore(OneOfFruizioneModIRestRichiestaSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
    return this;
  }

 /**
   * Get keystoreTokenPolicy
   * @return keystoreTokenPolicy
  **/
  @JsonProperty("keystore_token_policy")
  @Valid
  public Boolean isKeystoreTokenPolicy() {
    return this.keystoreTokenPolicy;
  }

  public void setKeystoreTokenPolicy(Boolean keystoreTokenPolicy) {
    this.keystoreTokenPolicy = keystoreTokenPolicy;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio keystoreTokenPolicy(Boolean keystoreTokenPolicy) {
    this.keystoreTokenPolicy = keystoreTokenPolicy;
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

  public FruizioneModIRestRichiestaSicurezzaMessaggio timeToLive(Integer timeToLive) {
    this.timeToLive = timeToLive;
    return this;
  }

 /**
   * Get audience
   * @return audience
  **/
  @JsonProperty("audience")
  @Valid
 @Size(max=4000)  public String getAudience() {
    return this.audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio audience(String audience) {
    this.audience = audience;
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

  public FruizioneModIRestRichiestaSicurezzaMessaggio informazioniUtenteCodiceEnte(FruizioneModIRichiestaInformazioneUtente informazioniUtenteCodiceEnte) {
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

  public FruizioneModIRestRichiestaSicurezzaMessaggio informazioniUtenteUserid(FruizioneModIRichiestaInformazioneUtente informazioniUtenteUserid) {
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

  public FruizioneModIRestRichiestaSicurezzaMessaggio informazioniUtenteIndirizzoIp(FruizioneModIRichiestaInformazioneUtente informazioniUtenteIndirizzoIp) {
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

  public FruizioneModIRestRichiestaSicurezzaMessaggio audit(FruizioneModIRichiestaInformazioniUtenteAudit audit) {
    this.audit = audit;
    return this;
  }

 /**
   * Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola
   * @return claims
  **/
  @JsonProperty("claims")
  @Valid
  public List<String> getClaims() {
    return this.claims;
  }

  public void setClaims(List<String> claims) {
    this.claims = claims;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio claims(List<String> claims) {
    this.claims = claims;
    return this;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio addClaimsItem(String claimsItem) {
    this.claims.add(claimsItem);
    return this;
  }

 /**
   * Get contemporaneita
   * @return contemporaneita
  **/
  @JsonProperty("contemporaneita")
  @Valid
  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita getContemporaneita() {
    return this.contemporaneita;
  }

  public void setContemporaneita(FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita contemporaneita) {
    this.contemporaneita = contemporaneita;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggio contemporaneita(FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita contemporaneita) {
    this.contemporaneita = contemporaneita;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIRestRichiestaSicurezzaMessaggio {\n");
    
    sb.append("    algoritmo: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.algoritmo)).append("\n");
    sb.append("    headerHttpFirmare: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.headerHttpFirmare)).append("\n");
    sb.append("    riferimentoX509: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.riferimentoX509)).append("\n");
    sb.append("    certificateChain: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.certificateChain)).append("\n");
    sb.append("    keystore: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.keystore)).append("\n");
    sb.append("    keystoreTokenPolicy: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.keystoreTokenPolicy)).append("\n");
    sb.append("    timeToLive: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.timeToLive)).append("\n");
    sb.append("    audience: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.audience)).append("\n");
    sb.append("    informazioniUtenteCodiceEnte: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.informazioniUtenteCodiceEnte)).append("\n");
    sb.append("    informazioniUtenteUserid: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.informazioniUtenteUserid)).append("\n");
    sb.append("    informazioniUtenteIndirizzoIp: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.informazioniUtenteIndirizzoIp)).append("\n");
    sb.append("    audit: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.audit)).append("\n");
    sb.append("    claims: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.claims)).append("\n");
    sb.append("    contemporaneita: ").append(FruizioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.contemporaneita)).append("\n");
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

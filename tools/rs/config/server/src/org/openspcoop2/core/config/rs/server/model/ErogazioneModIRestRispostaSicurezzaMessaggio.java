/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ErogazioneModIRestRispostaSicurezzaMessaggio  {
  
  @Schema(description = "")
  private ModISicurezzaMessaggioRestAlgoritmoFirma algoritmo = null;
  
  @Schema(description = "")
  private ModISicurezzaMessaggioRestRiferimentoX509Risposta riferimentoX509 = null;
  
  @Schema(description = "")
  private List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509Risposta = null;
  
  @Schema(description = "")
  private Boolean certificateChain = null;
  
  @Schema(description = "")
  private String url = null;
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreRidefinito.class, name = "ridefinito")  })
  private OneOfErogazioneModIRestRispostaSicurezzaMessaggioKeystore keystore = null;
  
  @Schema(description = "")
  private Integer timeToLive = 300;
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

  public ErogazioneModIRestRispostaSicurezzaMessaggio algoritmo(ModISicurezzaMessaggioRestAlgoritmoFirma algoritmo) {
    this.algoritmo = algoritmo;
    return this;
  }

 /**
   * Get riferimentoX509
   * @return riferimentoX509
  **/
  @JsonProperty("riferimento_x509")
  @Valid
  public ModISicurezzaMessaggioRestRiferimentoX509Risposta getRiferimentoX509() {
    return this.riferimentoX509;
  }

  public void setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
  }

  public ErogazioneModIRestRispostaSicurezzaMessaggio riferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
    return this;
  }

 /**
   * Get riferimentoX509Risposta
   * @return riferimentoX509Risposta
  **/
  @JsonProperty("riferimento_x509_risposta")
  @Valid
 @Size(min=1,max=3)  public List<ModISicurezzaMessaggioRestRiferimentoX509> getRiferimentoX509Risposta() {
    return this.riferimentoX509Risposta;
  }

  public void setRiferimentoX509Risposta(List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509Risposta) {
    this.riferimentoX509Risposta = riferimentoX509Risposta;
  }

  public ErogazioneModIRestRispostaSicurezzaMessaggio riferimentoX509Risposta(List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509Risposta) {
    this.riferimentoX509Risposta = riferimentoX509Risposta;
    return this;
  }

  public ErogazioneModIRestRispostaSicurezzaMessaggio addRiferimentoX509RispostaItem(ModISicurezzaMessaggioRestRiferimentoX509 riferimentoX509RispostaItem) {
    this.riferimentoX509Risposta.add(riferimentoX509RispostaItem);
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

  public ErogazioneModIRestRispostaSicurezzaMessaggio certificateChain(Boolean certificateChain) {
    this.certificateChain = certificateChain;
    return this;
  }

 /**
   * Get url
   * @return url
  **/
  @JsonProperty("url")
  @Valid
 @Size(max=4000)  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ErogazioneModIRestRispostaSicurezzaMessaggio url(String url) {
    this.url = url;
    return this;
  }

 /**
   * Get keystore
   * @return keystore
  **/
  @JsonProperty("keystore")
  @NotNull
  @Valid
  public OneOfErogazioneModIRestRispostaSicurezzaMessaggioKeystore getKeystore() {
    return this.keystore;
  }

  public void setKeystore(OneOfErogazioneModIRestRispostaSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
  }

  public ErogazioneModIRestRispostaSicurezzaMessaggio keystore(OneOfErogazioneModIRestRispostaSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
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

  public ErogazioneModIRestRispostaSicurezzaMessaggio timeToLive(Integer timeToLive) {
    this.timeToLive = timeToLive;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModIRestRispostaSicurezzaMessaggio {\n");
    
    sb.append("    algoritmo: ").append(ErogazioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.algoritmo)).append("\n");
    sb.append("    riferimentoX509: ").append(ErogazioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.riferimentoX509)).append("\n");
    sb.append("    riferimentoX509Risposta: ").append(ErogazioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.riferimentoX509Risposta)).append("\n");
    sb.append("    certificateChain: ").append(ErogazioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.certificateChain)).append("\n");
    sb.append("    url: ").append(ErogazioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.url)).append("\n");
    sb.append("    keystore: ").append(ErogazioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.keystore)).append("\n");
    sb.append("    timeToLive: ").append(ErogazioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.timeToLive)).append("\n");
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

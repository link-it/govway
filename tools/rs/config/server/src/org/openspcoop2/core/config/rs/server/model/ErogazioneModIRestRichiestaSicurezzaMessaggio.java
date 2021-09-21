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

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ErogazioneModIRestRichiestaSicurezzaMessaggio  {
  
  @Schema(required = true, description = "")
  private List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509 = new ArrayList<ModISicurezzaMessaggioRestRiferimentoX509>();
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModITrustStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModITrustStoreRidefinito.class, name = "ridefinito")  })
  private OneOfErogazioneModIRestRichiestaSicurezzaMessaggioTruststore truststore = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModITrustStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModITrustStoreRidefinito.class, name = "ridefinito")  })
  private OneOfErogazioneModIRestRichiestaSicurezzaMessaggioTruststoreSsl truststoreSsl = null;
  
  @Schema(description = "")
  private Integer timeToLive = null;
  
  @Schema(description = "")
  private String audience = null;
  
  @Schema(description = "")
  private ErogazioneModIRestRichiestaSicurezzaMessaggioContemporaneita contemporaneita = null;
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

  public ErogazioneModIRestRichiestaSicurezzaMessaggio riferimentoX509(List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
    return this;
  }

  public ErogazioneModIRestRichiestaSicurezzaMessaggio addRiferimentoX509Item(ModISicurezzaMessaggioRestRiferimentoX509 riferimentoX509Item) {
    this.riferimentoX509.add(riferimentoX509Item);
    return this;
  }

 /**
   * Get truststore
   * @return truststore
  **/
  @JsonProperty("truststore")
  @NotNull
  @Valid
  public OneOfErogazioneModIRestRichiestaSicurezzaMessaggioTruststore getTruststore() {
    return this.truststore;
  }

  public void setTruststore(OneOfErogazioneModIRestRichiestaSicurezzaMessaggioTruststore truststore) {
    this.truststore = truststore;
  }

  public ErogazioneModIRestRichiestaSicurezzaMessaggio truststore(OneOfErogazioneModIRestRichiestaSicurezzaMessaggioTruststore truststore) {
    this.truststore = truststore;
    return this;
  }

 /**
   * Get truststoreSsl
   * @return truststoreSsl
  **/
  @JsonProperty("truststore_ssl")
  @Valid
  public OneOfErogazioneModIRestRichiestaSicurezzaMessaggioTruststoreSsl getTruststoreSsl() {
    return this.truststoreSsl;
  }

  public void setTruststoreSsl(OneOfErogazioneModIRestRichiestaSicurezzaMessaggioTruststoreSsl truststoreSsl) {
    this.truststoreSsl = truststoreSsl;
  }

  public ErogazioneModIRestRichiestaSicurezzaMessaggio truststoreSsl(OneOfErogazioneModIRestRichiestaSicurezzaMessaggioTruststoreSsl truststoreSsl) {
    this.truststoreSsl = truststoreSsl;
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

  public ErogazioneModIRestRichiestaSicurezzaMessaggio timeToLive(Integer timeToLive) {
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

  public ErogazioneModIRestRichiestaSicurezzaMessaggio audience(String audience) {
    this.audience = audience;
    return this;
  }

 /**
   * Get contemporaneita
   * @return contemporaneita
  **/
  @JsonProperty("contemporaneita")
  @Valid
  public ErogazioneModIRestRichiestaSicurezzaMessaggioContemporaneita getContemporaneita() {
    return this.contemporaneita;
  }

  public void setContemporaneita(ErogazioneModIRestRichiestaSicurezzaMessaggioContemporaneita contemporaneita) {
    this.contemporaneita = contemporaneita;
  }

  public ErogazioneModIRestRichiestaSicurezzaMessaggio contemporaneita(ErogazioneModIRestRichiestaSicurezzaMessaggioContemporaneita contemporaneita) {
    this.contemporaneita = contemporaneita;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModIRestRichiestaSicurezzaMessaggio {\n");
    
    sb.append("    riferimentoX509: ").append(ErogazioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.riferimentoX509)).append("\n");
    sb.append("    truststore: ").append(ErogazioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.truststore)).append("\n");
    sb.append("    truststoreSsl: ").append(ErogazioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.truststoreSsl)).append("\n");
    sb.append("    timeToLive: ").append(ErogazioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.timeToLive)).append("\n");
    sb.append("    audience: ").append(ErogazioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.audience)).append("\n");
    sb.append("    contemporaneita: ").append(ErogazioneModIRestRichiestaSicurezzaMessaggio.toIndentedString(this.contemporaneita)).append("\n");
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

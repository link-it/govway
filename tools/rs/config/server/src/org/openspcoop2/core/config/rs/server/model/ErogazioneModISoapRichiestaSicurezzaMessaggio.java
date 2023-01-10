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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ErogazioneModISoapRichiestaSicurezzaMessaggio  {
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModITrustStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModITrustStoreRidefinito.class, name = "ridefinito")  })
  private OneOfErogazioneModISoapRichiestaSicurezzaMessaggioTruststore truststore = null;
  
  @Schema(description = "")
  private Integer timeToLive = null;
  
  @Schema(description = "")
  private String wsaTo = null;
 /**
   * Get truststore
   * @return truststore
  **/
  @JsonProperty("truststore")
  @NotNull
  @Valid
  public OneOfErogazioneModISoapRichiestaSicurezzaMessaggioTruststore getTruststore() {
    return this.truststore;
  }

  public void setTruststore(OneOfErogazioneModISoapRichiestaSicurezzaMessaggioTruststore truststore) {
    this.truststore = truststore;
  }

  public ErogazioneModISoapRichiestaSicurezzaMessaggio truststore(OneOfErogazioneModISoapRichiestaSicurezzaMessaggioTruststore truststore) {
    this.truststore = truststore;
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

  public ErogazioneModISoapRichiestaSicurezzaMessaggio timeToLive(Integer timeToLive) {
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

  public ErogazioneModISoapRichiestaSicurezzaMessaggio wsaTo(String wsaTo) {
    this.wsaTo = wsaTo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModISoapRichiestaSicurezzaMessaggio {\n");
    
    sb.append("    truststore: ").append(ErogazioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.truststore)).append("\n");
    sb.append("    timeToLive: ").append(ErogazioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.timeToLive)).append("\n");
    sb.append("    wsaTo: ").append(ErogazioneModISoapRichiestaSicurezzaMessaggio.toIndentedString(this.wsaTo)).append("\n");
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

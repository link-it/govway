/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.config.rs.server.model.ApiImplConfigurazioneStato;
import org.openspcoop2.core.config.rs.server.model.StatoDefaultRidefinitoEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class CachingRisposta extends ApiImplConfigurazioneStato {
  
  @Schema(required = true, description = "")
  private StatoDefaultRidefinitoEnum stato = null;
  
  @Schema(description = "")
  private Boolean abilitato = null;
  
  @Schema(description = "")
  private Integer cacheTimeoutSeconds = null;
  
  @Schema(description = "")
  private Boolean maxResponseSize = null;
  
  @Schema(description = "")
  private Long maxResponseSizeKb = null;
  
  @Schema(description = "")
  private Boolean hashRequestUri = true;
  
  @Schema(description = "")
  private Boolean hashHeaders = true;
  
  @Schema(description = "")
  private Boolean hashPayload = true;
 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @NotNull
  @Valid
  public StatoDefaultRidefinitoEnum getStato() {
    return this.stato;
  }

  public void setStato(StatoDefaultRidefinitoEnum stato) {
    this.stato = stato;
  }

  public CachingRisposta stato(StatoDefaultRidefinitoEnum stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get abilitato
   * @return abilitato
  **/
  @JsonProperty("abilitato")
  @Valid
  public Boolean isAbilitato() {
    return this.abilitato;
  }

  public void setAbilitato(Boolean abilitato) {
    this.abilitato = abilitato;
  }

  public CachingRisposta abilitato(Boolean abilitato) {
    this.abilitato = abilitato;
    return this;
  }

 /**
   * Get cacheTimeoutSeconds
   * @return cacheTimeoutSeconds
  **/
  @JsonProperty("cache_timeout_seconds")
  @Valid
  public Integer getCacheTimeoutSeconds() {
    return this.cacheTimeoutSeconds;
  }

  public void setCacheTimeoutSeconds(Integer cacheTimeoutSeconds) {
    this.cacheTimeoutSeconds = cacheTimeoutSeconds;
  }

  public CachingRisposta cacheTimeoutSeconds(Integer cacheTimeoutSeconds) {
    this.cacheTimeoutSeconds = cacheTimeoutSeconds;
    return this;
  }

 /**
   * Get maxResponseSize
   * @return maxResponseSize
  **/
  @JsonProperty("max_response_size")
  @Valid
  public Boolean isMaxResponseSize() {
    return this.maxResponseSize;
  }

  public void setMaxResponseSize(Boolean maxResponseSize) {
    this.maxResponseSize = maxResponseSize;
  }

  public CachingRisposta maxResponseSize(Boolean maxResponseSize) {
    this.maxResponseSize = maxResponseSize;
    return this;
  }

 /**
   * Get maxResponseSizeKb
   * @return maxResponseSizeKb
  **/
  @JsonProperty("max_response_size_kb")
  @Valid
  public Long getMaxResponseSizeKb() {
    return this.maxResponseSizeKb;
  }

  public void setMaxResponseSizeKb(Long maxResponseSizeKb) {
    this.maxResponseSizeKb = maxResponseSizeKb;
  }

  public CachingRisposta maxResponseSizeKb(Long maxResponseSizeKb) {
    this.maxResponseSizeKb = maxResponseSizeKb;
    return this;
  }

 /**
   * Get hashRequestUri
   * @return hashRequestUri
  **/
  @JsonProperty("hash_request_uri")
  @Valid
  public Boolean isHashRequestUri() {
    return this.hashRequestUri;
  }

  public void setHashRequestUri(Boolean hashRequestUri) {
    this.hashRequestUri = hashRequestUri;
  }

  public CachingRisposta hashRequestUri(Boolean hashRequestUri) {
    this.hashRequestUri = hashRequestUri;
    return this;
  }

 /**
   * Get hashHeaders
   * @return hashHeaders
  **/
  @JsonProperty("hash_headers")
  @Valid
  public Boolean isHashHeaders() {
    return this.hashHeaders;
  }

  public void setHashHeaders(Boolean hashHeaders) {
    this.hashHeaders = hashHeaders;
  }

  public CachingRisposta hashHeaders(Boolean hashHeaders) {
    this.hashHeaders = hashHeaders;
    return this;
  }

 /**
   * Get hashPayload
   * @return hashPayload
  **/
  @JsonProperty("hash_payload")
  @Valid
  public Boolean isHashPayload() {
    return this.hashPayload;
  }

  public void setHashPayload(Boolean hashPayload) {
    this.hashPayload = hashPayload;
  }

  public CachingRisposta hashPayload(Boolean hashPayload) {
    this.hashPayload = hashPayload;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CachingRisposta {\n");
    sb.append("    ").append(CachingRisposta.toIndentedString(super.toString())).append("\n");
    sb.append("    stato: ").append(CachingRisposta.toIndentedString(this.stato)).append("\n");
    sb.append("    abilitato: ").append(CachingRisposta.toIndentedString(this.abilitato)).append("\n");
    sb.append("    cacheTimeoutSeconds: ").append(CachingRisposta.toIndentedString(this.cacheTimeoutSeconds)).append("\n");
    sb.append("    maxResponseSize: ").append(CachingRisposta.toIndentedString(this.maxResponseSize)).append("\n");
    sb.append("    maxResponseSizeKb: ").append(CachingRisposta.toIndentedString(this.maxResponseSizeKb)).append("\n");
    sb.append("    hashRequestUri: ").append(CachingRisposta.toIndentedString(this.hashRequestUri)).append("\n");
    sb.append("    hashHeaders: ").append(CachingRisposta.toIndentedString(this.hashHeaders)).append("\n");
    sb.append("    hashPayload: ").append(CachingRisposta.toIndentedString(this.hashPayload)).append("\n");
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

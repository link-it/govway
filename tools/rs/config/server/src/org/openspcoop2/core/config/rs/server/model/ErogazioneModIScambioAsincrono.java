/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

public class ErogazioneModIScambioAsincrono  {
  
  @Schema(description = "verifica della URL di callback rispetto alle fruizioni dell'API di callback (solo API con ruolo 'erogazione_dati')")
 /**
   * verifica della URL di callback rispetto alle fruizioni dell'API di callback (solo API con ruolo 'erogazione_dati')  
  **/
  private Boolean verificaUrlCallback = false;
  
  @Schema(description = "")
  private ModIScambioAsincronoCodificaEnum codificaHeaderUrlCallback = null;
  
  @Schema(example = "200-299", description = "codici HTTP (singoli o intervalli separati da virgola) che indicano il completamento della fase; se non indicati viene utilizzata la configurazione di default")
 /**
   * codici HTTP (singoli o intervalli separati da virgola) che indicano il completamento della fase; se non indicati viene utilizzata la configurazione di default  
  **/
  private String codiciHttpEsitoPositivo = null;
 /**
   * verifica della URL di callback rispetto alle fruizioni dell'API di callback (solo API con ruolo 'erogazione_dati')
   * @return verificaUrlCallback
  **/
  @JsonProperty("verifica_url_callback")
  @Valid
  public Boolean isVerificaUrlCallback() {
    return this.verificaUrlCallback;
  }

  public void setVerificaUrlCallback(Boolean verificaUrlCallback) {
    this.verificaUrlCallback = verificaUrlCallback;
  }

  public ErogazioneModIScambioAsincrono verificaUrlCallback(Boolean verificaUrlCallback) {
    this.verificaUrlCallback = verificaUrlCallback;
    return this;
  }

 /**
   * Get codificaHeaderUrlCallback
   * @return codificaHeaderUrlCallback
  **/
  @JsonProperty("codifica_header_url_callback")
  @Valid
  public ModIScambioAsincronoCodificaEnum getCodificaHeaderUrlCallback() {
    return this.codificaHeaderUrlCallback;
  }

  public void setCodificaHeaderUrlCallback(ModIScambioAsincronoCodificaEnum codificaHeaderUrlCallback) {
    this.codificaHeaderUrlCallback = codificaHeaderUrlCallback;
  }

  public ErogazioneModIScambioAsincrono codificaHeaderUrlCallback(ModIScambioAsincronoCodificaEnum codificaHeaderUrlCallback) {
    this.codificaHeaderUrlCallback = codificaHeaderUrlCallback;
    return this;
  }

 /**
   * codici HTTP (singoli o intervalli separati da virgola) che indicano il completamento della fase; se non indicati viene utilizzata la configurazione di default
   * @return codiciHttpEsitoPositivo
  **/
  @JsonProperty("codici_http_esito_positivo")
  @Valid
 @Size(max=4000)  public String getCodiciHttpEsitoPositivo() {
    return this.codiciHttpEsitoPositivo;
  }

  public void setCodiciHttpEsitoPositivo(String codiciHttpEsitoPositivo) {
    this.codiciHttpEsitoPositivo = codiciHttpEsitoPositivo;
  }

  public ErogazioneModIScambioAsincrono codiciHttpEsitoPositivo(String codiciHttpEsitoPositivo) {
    this.codiciHttpEsitoPositivo = codiciHttpEsitoPositivo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModIScambioAsincrono {\n");
    
    sb.append("    verificaUrlCallback: ").append(ErogazioneModIScambioAsincrono.toIndentedString(this.verificaUrlCallback)).append("\n");
    sb.append("    codificaHeaderUrlCallback: ").append(ErogazioneModIScambioAsincrono.toIndentedString(this.codificaHeaderUrlCallback)).append("\n");
    sb.append("    codiciHttpEsitoPositivo: ").append(ErogazioneModIScambioAsincrono.toIndentedString(this.codiciHttpEsitoPositivo)).append("\n");
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

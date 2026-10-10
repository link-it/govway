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

public class FruizioneModIScambioAsincrono  {
  
  @Schema(description = "modalità con cui viene determinata la URL di callback (solo API con ruolo 'erogazione_dati')")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "sorgente", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FruizioneModIScambioAsincronoUrlCallbackErogazione.class, name = "erogazione"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FruizioneModIScambioAsincronoUrlCallbackClient.class, name = "client")  })
 /**
   * modalità con cui viene determinata la URL di callback (solo API con ruolo 'erogazione_dati')  
  **/
  private OneOfFruizioneModIScambioAsincronoUrlCallback urlCallback = null;
  
  @Schema(description = "invio del purposeId nelle fasi get_resource e confirmation (solo API con ruolo 'erogazione_dati'); se non indicato viene utilizzata la configurazione di default")
 /**
   * invio del purposeId nelle fasi get_resource e confirmation (solo API con ruolo 'erogazione_dati'); se non indicato viene utilizzata la configurazione di default  
  **/
  private Boolean invioPurposeId = null;
  
  @Schema(description = "")
  private ModIScambioAsincronoParametro entityNumber = null;
  
  @Schema(example = "200-299", description = "codici HTTP (singoli o intervalli separati da virgola) che indicano il completamento della fase; se non indicati viene utilizzata la configurazione di default")
 /**
   * codici HTTP (singoli o intervalli separati da virgola) che indicano il completamento della fase; se non indicati viene utilizzata la configurazione di default  
  **/
  private String codiciHttpEsitoPositivo = null;
 /**
   * modalità con cui viene determinata la URL di callback (solo API con ruolo 'erogazione_dati')
   * @return urlCallback
  **/
  @JsonProperty("url_callback")
  @Valid
  public OneOfFruizioneModIScambioAsincronoUrlCallback getUrlCallback() {
    return this.urlCallback;
  }

  public void setUrlCallback(OneOfFruizioneModIScambioAsincronoUrlCallback urlCallback) {
    this.urlCallback = urlCallback;
  }

  public FruizioneModIScambioAsincrono urlCallback(OneOfFruizioneModIScambioAsincronoUrlCallback urlCallback) {
    this.urlCallback = urlCallback;
    return this;
  }

 /**
   * invio del purposeId nelle fasi get_resource e confirmation (solo API con ruolo 'erogazione_dati'); se non indicato viene utilizzata la configurazione di default
   * @return invioPurposeId
  **/
  @JsonProperty("invio_purpose_id")
  @Valid
  public Boolean isInvioPurposeId() {
    return this.invioPurposeId;
  }

  public void setInvioPurposeId(Boolean invioPurposeId) {
    this.invioPurposeId = invioPurposeId;
  }

  public FruizioneModIScambioAsincrono invioPurposeId(Boolean invioPurposeId) {
    this.invioPurposeId = invioPurposeId;
    return this;
  }

 /**
   * Get entityNumber
   * @return entityNumber
  **/
  @JsonProperty("entity_number")
  @Valid
  public ModIScambioAsincronoParametro getEntityNumber() {
    return this.entityNumber;
  }

  public void setEntityNumber(ModIScambioAsincronoParametro entityNumber) {
    this.entityNumber = entityNumber;
  }

  public FruizioneModIScambioAsincrono entityNumber(ModIScambioAsincronoParametro entityNumber) {
    this.entityNumber = entityNumber;
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

  public FruizioneModIScambioAsincrono codiciHttpEsitoPositivo(String codiciHttpEsitoPositivo) {
    this.codiciHttpEsitoPositivo = codiciHttpEsitoPositivo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIScambioAsincrono {\n");
    
    sb.append("    urlCallback: ").append(FruizioneModIScambioAsincrono.toIndentedString(this.urlCallback)).append("\n");
    sb.append("    invioPurposeId: ").append(FruizioneModIScambioAsincrono.toIndentedString(this.invioPurposeId)).append("\n");
    sb.append("    entityNumber: ").append(FruizioneModIScambioAsincrono.toIndentedString(this.entityNumber)).append("\n");
    sb.append("    codiciHttpEsitoPositivo: ").append(FruizioneModIScambioAsincrono.toIndentedString(this.codiciHttpEsitoPositivo)).append("\n");
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

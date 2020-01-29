/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 *
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import org.openspcoop2.core.config.rs.server.model.RateLimitingChiaveEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RateLimitingPolicyFiltro  {
  
  @Schema(description = "")
  private List<String> azione = null;
  
  @Schema(description = "")
  private String ruoloRichiedente = null;
  
  @Schema(description = "")
  private String applicativoFruitore = null;
  
  @Schema(description = "")
  private RateLimitingChiaveEnum chiaveTipo = null;
  
  @Schema(description = "La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: indirizzo ip del socket   * indirizzo-ip-forwarded: indirizzo ip inoltrato via header http   * plugin-based: tipo del plugin")
 /**
   * La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: indirizzo ip del socket   * indirizzo-ip-forwarded: indirizzo ip inoltrato via header http   * plugin-based: tipo del plugin  
  **/
  private String chiaveNome = null;
  
  @Schema(description = "indica il valore che deve assumere la chiave")
 /**
   * indica il valore che deve assumere la chiave  
  **/
  private String filtroChiaveValore = null;
 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  @Valid
  public List<String> getAzione() {
    return this.azione;
  }

  public void setAzione(List<String> azione) {
    this.azione = azione;
  }

  public RateLimitingPolicyFiltro azione(List<String> azione) {
    this.azione = azione;
    return this;
  }

  public RateLimitingPolicyFiltro addAzioneItem(String azioneItem) {
    this.azione.add(azioneItem);
    return this;
  }

 /**
   * Get ruoloRichiedente
   * @return ruoloRichiedente
  **/
  @JsonProperty("ruolo_richiedente")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getRuoloRichiedente() {
    return this.ruoloRichiedente;
  }

  public void setRuoloRichiedente(String ruoloRichiedente) {
    this.ruoloRichiedente = ruoloRichiedente;
  }

  public RateLimitingPolicyFiltro ruoloRichiedente(String ruoloRichiedente) {
    this.ruoloRichiedente = ruoloRichiedente;
    return this;
  }

 /**
   * Get applicativoFruitore
   * @return applicativoFruitore
  **/
  @JsonProperty("applicativo_fruitore")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getApplicativoFruitore() {
    return this.applicativoFruitore;
  }

  public void setApplicativoFruitore(String applicativoFruitore) {
    this.applicativoFruitore = applicativoFruitore;
  }

  public RateLimitingPolicyFiltro applicativoFruitore(String applicativoFruitore) {
    this.applicativoFruitore = applicativoFruitore;
    return this;
  }

 /**
   * Get chiaveTipo
   * @return chiaveTipo
  **/
  @JsonProperty("chiave_tipo")
  @Valid
  public RateLimitingChiaveEnum getChiaveTipo() {
    return this.chiaveTipo;
  }

  public void setChiaveTipo(RateLimitingChiaveEnum chiaveTipo) {
    this.chiaveTipo = chiaveTipo;
  }

  public RateLimitingPolicyFiltro chiaveTipo(RateLimitingChiaveEnum chiaveTipo) {
    this.chiaveTipo = chiaveTipo;
    return this;
  }

 /**
   * La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: indirizzo ip del socket   * indirizzo-ip-forwarded: indirizzo ip inoltrato via header http   * plugin-based: tipo del plugin
   * @return chiaveNome
  **/
  @JsonProperty("chiave_nome")
  @Valid
 @Size(max=255)  public String getChiaveNome() {
    return this.chiaveNome;
  }

  public void setChiaveNome(String chiaveNome) {
    this.chiaveNome = chiaveNome;
  }

  public RateLimitingPolicyFiltro chiaveNome(String chiaveNome) {
    this.chiaveNome = chiaveNome;
    return this;
  }

 /**
   * indica il valore che deve assumere la chiave
   * @return filtroChiaveValore
  **/
  @JsonProperty("filtro_chiave_valore")
  @Valid
 @Size(max=255)  public String getFiltroChiaveValore() {
    return this.filtroChiaveValore;
  }

  public void setFiltroChiaveValore(String filtroChiaveValore) {
    this.filtroChiaveValore = filtroChiaveValore;
  }

  public RateLimitingPolicyFiltro filtroChiaveValore(String filtroChiaveValore) {
    this.filtroChiaveValore = filtroChiaveValore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyFiltro {\n");
    
    sb.append("    azione: ").append(RateLimitingPolicyFiltro.toIndentedString(this.azione)).append("\n");
    sb.append("    ruoloRichiedente: ").append(RateLimitingPolicyFiltro.toIndentedString(this.ruoloRichiedente)).append("\n");
    sb.append("    applicativoFruitore: ").append(RateLimitingPolicyFiltro.toIndentedString(this.applicativoFruitore)).append("\n");
    sb.append("    chiaveTipo: ").append(RateLimitingPolicyFiltro.toIndentedString(this.chiaveTipo)).append("\n");
    sb.append("    chiaveNome: ").append(RateLimitingPolicyFiltro.toIndentedString(this.chiaveNome)).append("\n");
    sb.append("    filtroChiaveValore: ").append(RateLimitingPolicyFiltro.toIndentedString(this.filtroChiaveValore)).append("\n");
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

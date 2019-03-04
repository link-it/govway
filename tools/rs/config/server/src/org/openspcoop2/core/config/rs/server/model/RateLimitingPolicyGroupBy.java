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

import org.openspcoop2.core.config.rs.server.model.RateLimitingChiaveEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RateLimitingPolicyGroupBy  {
  
  @Schema(description = "")
  private Boolean azione = false;
  
  @Schema(description = "")
  private Boolean applicativoFruitore = false;
  
  @Schema(description = "")
  private RateLimitingChiaveEnum chiaveTipo = null;
  
  @Schema(description = "La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * plugin-based: tipo del plugin")
 /**
   * La semantica cambia in funzione del tipo:   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * plugin-based: tipo del plugin  
  **/
  private String chiaveNome = null;
 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  @Valid
  public Boolean isAzione() {
    return this.azione;
  }

  public void setAzione(Boolean azione) {
    this.azione = azione;
  }

  public RateLimitingPolicyGroupBy azione(Boolean azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get applicativoFruitore
   * @return applicativoFruitore
  **/
  @JsonProperty("applicativo_fruitore")
  @Valid
  public Boolean isApplicativoFruitore() {
    return this.applicativoFruitore;
  }

  public void setApplicativoFruitore(Boolean applicativoFruitore) {
    this.applicativoFruitore = applicativoFruitore;
  }

  public RateLimitingPolicyGroupBy applicativoFruitore(Boolean applicativoFruitore) {
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

  public RateLimitingPolicyGroupBy chiaveTipo(RateLimitingChiaveEnum chiaveTipo) {
    this.chiaveTipo = chiaveTipo;
    return this;
  }

 /**
   * La semantica cambia in funzione del tipo:   * header-based: nome dell&#x27;header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * soap-action-based: in questa modalità questo parametro non viene utilizzato   * content-based: xpath o json path da applicare sul contenuto   * plugin-based: tipo del plugin
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

  public RateLimitingPolicyGroupBy chiaveNome(String chiaveNome) {
    this.chiaveNome = chiaveNome;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyGroupBy {\n");
    
    sb.append("    azione: ").append(RateLimitingPolicyGroupBy.toIndentedString(this.azione)).append("\n");
    sb.append("    applicativoFruitore: ").append(RateLimitingPolicyGroupBy.toIndentedString(this.applicativoFruitore)).append("\n");
    sb.append("    chiaveTipo: ").append(RateLimitingPolicyGroupBy.toIndentedString(this.chiaveTipo)).append("\n");
    sb.append("    chiaveNome: ").append(RateLimitingPolicyGroupBy.toIndentedString(this.chiaveNome)).append("\n");
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

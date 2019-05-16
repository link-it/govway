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
import org.openspcoop2.core.config.rs.server.model.StatoFunzionalitaConWarningEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RateLimitingPolicyBase extends ApiImplConfigurazioneStato {
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(required = true, description = "")
  private StatoFunzionalitaConWarningEnum stato = null;
  
  @Schema(description = "")
  private Boolean sogliaRidefinita = false;
  
  @Schema(description = "")
  private Integer sogliaValore = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
 @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public RateLimitingPolicyBase nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @NotNull
  @Valid
  public StatoFunzionalitaConWarningEnum getStato() {
    return this.stato;
  }

  public void setStato(StatoFunzionalitaConWarningEnum stato) {
    this.stato = stato;
  }

  public RateLimitingPolicyBase stato(StatoFunzionalitaConWarningEnum stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get sogliaRidefinita
   * @return sogliaRidefinita
  **/
  @JsonProperty("soglia_ridefinita")
  @Valid
  public Boolean isSogliaRidefinita() {
    return this.sogliaRidefinita;
  }

  public void setSogliaRidefinita(Boolean sogliaRidefinita) {
    this.sogliaRidefinita = sogliaRidefinita;
  }

  public RateLimitingPolicyBase sogliaRidefinita(Boolean sogliaRidefinita) {
    this.sogliaRidefinita = sogliaRidefinita;
    return this;
  }

 /**
   * Get sogliaValore
   * @return sogliaValore
  **/
  @JsonProperty("soglia_valore")
  @Valid
  public Integer getSogliaValore() {
    return this.sogliaValore;
  }

  public void setSogliaValore(Integer sogliaValore) {
    this.sogliaValore = sogliaValore;
  }

  public RateLimitingPolicyBase sogliaValore(Integer sogliaValore) {
    this.sogliaValore = sogliaValore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateLimitingPolicyBase {\n");
    sb.append("    ").append(RateLimitingPolicyBase.toIndentedString(super.toString())).append("\n");
    sb.append("    nome: ").append(RateLimitingPolicyBase.toIndentedString(this.nome)).append("\n");
    sb.append("    stato: ").append(RateLimitingPolicyBase.toIndentedString(this.stato)).append("\n");
    sb.append("    sogliaRidefinita: ").append(RateLimitingPolicyBase.toIndentedString(this.sogliaRidefinita)).append("\n");
    sb.append("    sogliaValore: ").append(RateLimitingPolicyBase.toIndentedString(this.sogliaValore)).append("\n");
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

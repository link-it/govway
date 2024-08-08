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

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class RegistrazioneTransazioniConfigurazioneBase  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private TracciamentoTransazioniStato stato = null;
  
  @Schema(description = "")
  private RegistrazioneTransazioniConfigurazioneFasi fasi = null;
  
  @Schema(description = "")
  private TracciamentoTransazioniStatoFase filtroEsiti = null;
 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @NotNull
  @Valid
  public TracciamentoTransazioniStato getStato() {
    return this.stato;
  }

  public void setStato(TracciamentoTransazioniStato stato) {
    this.stato = stato;
  }

  public RegistrazioneTransazioniConfigurazioneBase stato(TracciamentoTransazioniStato stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get fasi
   * @return fasi
  **/
  @JsonProperty("fasi")
  @Valid
  public RegistrazioneTransazioniConfigurazioneFasi getFasi() {
    return this.fasi;
  }

  public void setFasi(RegistrazioneTransazioniConfigurazioneFasi fasi) {
    this.fasi = fasi;
  }

  public RegistrazioneTransazioniConfigurazioneBase fasi(RegistrazioneTransazioniConfigurazioneFasi fasi) {
    this.fasi = fasi;
    return this;
  }

 /**
   * Get filtroEsiti
   * @return filtroEsiti
  **/
  @JsonProperty("filtro_esiti")
  @Valid
  public TracciamentoTransazioniStatoFase getFiltroEsiti() {
    return this.filtroEsiti;
  }

  public void setFiltroEsiti(TracciamentoTransazioniStatoFase filtroEsiti) {
    this.filtroEsiti = filtroEsiti;
  }

  public RegistrazioneTransazioniConfigurazioneBase filtroEsiti(TracciamentoTransazioniStatoFase filtroEsiti) {
    this.filtroEsiti = filtroEsiti;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneTransazioniConfigurazioneBase {\n");
    
    sb.append("    stato: ").append(RegistrazioneTransazioniConfigurazioneBase.toIndentedString(this.stato)).append("\n");
    sb.append("    fasi: ").append(RegistrazioneTransazioniConfigurazioneBase.toIndentedString(this.fasi)).append("\n");
    sb.append("    filtroEsiti: ").append(RegistrazioneTransazioniConfigurazioneBase.toIndentedString(this.filtroEsiti)).append("\n");
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

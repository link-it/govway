/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class RegistrazioneTransazioniConfigurazioneFiltroEsiti  {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "Indicare i codici degli esiti richiesti")
 /**
   * Indicare i codici degli esiti richiesti  
  **/
  private List<Integer> esiti = new ArrayList<>();
 /**
   * Indicare i codici degli esiti richiesti
   * @return esiti
  **/
  @JsonProperty("esiti")
  @NotNull
  @Valid
  public List<Integer> getEsiti() {
    return this.esiti;
  }

  public void setEsiti(List<Integer> esiti) {
    this.esiti = esiti;
  }

  public RegistrazioneTransazioniConfigurazioneFiltroEsiti esiti(List<Integer> esiti) {
    this.esiti = esiti;
    return this;
  }

  public RegistrazioneTransazioniConfigurazioneFiltroEsiti addEsitiItem(Integer esitiItem) {
    this.esiti.add(esitiItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneTransazioniConfigurazioneFiltroEsiti {\n");
    
    sb.append("    esiti: ").append(RegistrazioneTransazioniConfigurazioneFiltroEsiti.toIndentedString(this.esiti)).append("\n");
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

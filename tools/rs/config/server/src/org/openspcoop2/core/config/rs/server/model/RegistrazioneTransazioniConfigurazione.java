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

public class RegistrazioneTransazioniConfigurazione extends ApiImplConfigurazioneStato {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private StatoDefaultRidefinitoEnum stato = null;
  
  @Schema(description = "")
  private RegistrazioneTransazioniConfigurazioneBase database = null;
  
  @Schema(description = "")
  private RegistrazioneTransazioniConfigurazioneFiletrace filetrace = null;
  
  @Schema(description = "")
  private RegistrazioneTransazioniConfigurazioneFiltroEsiti filtro = null;
  
  @Schema(description = "")
  private RegistrazioneTransazioniConfigurazioneInformazioniRegistrate informazioni = null;
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

  public RegistrazioneTransazioniConfigurazione stato(StatoDefaultRidefinitoEnum stato) {
    this.stato = stato;
    return this;
  }

 /**
   * Get database
   * @return database
  **/
  @JsonProperty("database")
  @Valid
  public RegistrazioneTransazioniConfigurazioneBase getDatabase() {
    return this.database;
  }

  public void setDatabase(RegistrazioneTransazioniConfigurazioneBase database) {
    this.database = database;
  }

  public RegistrazioneTransazioniConfigurazione database(RegistrazioneTransazioniConfigurazioneBase database) {
    this.database = database;
    return this;
  }

 /**
   * Get filetrace
   * @return filetrace
  **/
  @JsonProperty("filetrace")
  @Valid
  public RegistrazioneTransazioniConfigurazioneFiletrace getFiletrace() {
    return this.filetrace;
  }

  public void setFiletrace(RegistrazioneTransazioniConfigurazioneFiletrace filetrace) {
    this.filetrace = filetrace;
  }

  public RegistrazioneTransazioniConfigurazione filetrace(RegistrazioneTransazioniConfigurazioneFiletrace filetrace) {
    this.filetrace = filetrace;
    return this;
  }

 /**
   * Get filtro
   * @return filtro
  **/
  @JsonProperty("filtro")
  @Valid
  public RegistrazioneTransazioniConfigurazioneFiltroEsiti getFiltro() {
    return this.filtro;
  }

  public void setFiltro(RegistrazioneTransazioniConfigurazioneFiltroEsiti filtro) {
    this.filtro = filtro;
  }

  public RegistrazioneTransazioniConfigurazione filtro(RegistrazioneTransazioniConfigurazioneFiltroEsiti filtro) {
    this.filtro = filtro;
    return this;
  }

 /**
   * Get informazioni
   * @return informazioni
  **/
  @JsonProperty("informazioni")
  @Valid
  public RegistrazioneTransazioniConfigurazioneInformazioniRegistrate getInformazioni() {
    return this.informazioni;
  }

  public void setInformazioni(RegistrazioneTransazioniConfigurazioneInformazioniRegistrate informazioni) {
    this.informazioni = informazioni;
  }

  public RegistrazioneTransazioniConfigurazione informazioni(RegistrazioneTransazioniConfigurazioneInformazioniRegistrate informazioni) {
    this.informazioni = informazioni;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneTransazioniConfigurazione {\n");
    sb.append("    ").append(RegistrazioneTransazioniConfigurazione.toIndentedString(super.toString())).append("\n");
    sb.append("    stato: ").append(RegistrazioneTransazioniConfigurazione.toIndentedString(this.stato)).append("\n");
    sb.append("    database: ").append(RegistrazioneTransazioniConfigurazione.toIndentedString(this.database)).append("\n");
    sb.append("    filetrace: ").append(RegistrazioneTransazioniConfigurazione.toIndentedString(this.filetrace)).append("\n");
    sb.append("    filtro: ").append(RegistrazioneTransazioniConfigurazione.toIndentedString(this.filtro)).append("\n");
    sb.append("    informazioni: ").append(RegistrazioneTransazioniConfigurazione.toIndentedString(this.informazioni)).append("\n");
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

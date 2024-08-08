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
package org.openspcoop2.core.monitor.rs.server.model;

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class RicercaStatisticaDistribuzioneErrori extends RicercaBaseStatisticaSoggetti {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private OpzioniGenerazioneReportDimensioni report = null;
  
  @Schema(description = "")
  private String azione = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "identificazione", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteErogazioneSoggetto.class, name = "erogazione_soggetto"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteFruizioneApplicativo.class, name = "fruizione_applicativo"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteErogazioneApplicativo.class, name = "erogazione_applicativo"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteIdAutenticato.class, name = "identificativo_autenticato"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteTokenClaimSoggetto.class, name = "erogazione_token_info"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteTokenClaim.class, name = "token_info"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteIndirizzoIP.class, name = "indirizzo_ip")  })
  private OneOfRicercaStatisticaDistribuzioneErroriMittente mittente = null;
  
  @Schema(description = "")
  private FiltroEsito esito = null;
 /**
   * Get report
   * @return report
  **/
  @JsonProperty("report")
  @NotNull
  @Valid
  public OpzioniGenerazioneReportDimensioni getReport() {
    return this.report;
  }

  public void setReport(OpzioniGenerazioneReportDimensioni report) {
    this.report = report;
  }

  public RicercaStatisticaDistribuzioneErrori report(OpzioniGenerazioneReportDimensioni report) {
    this.report = report;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public RicercaStatisticaDistribuzioneErrori azione(String azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get mittente
   * @return mittente
  **/
  @JsonProperty("mittente")
  @Valid
  public OneOfRicercaStatisticaDistribuzioneErroriMittente getMittente() {
    return this.mittente;
  }

  public void setMittente(OneOfRicercaStatisticaDistribuzioneErroriMittente mittente) {
    this.mittente = mittente;
  }

  public RicercaStatisticaDistribuzioneErrori mittente(OneOfRicercaStatisticaDistribuzioneErroriMittente mittente) {
    this.mittente = mittente;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  @Valid
  public FiltroEsito getEsito() {
    return this.esito;
  }

  public void setEsito(FiltroEsito esito) {
    this.esito = esito;
  }

  public RicercaStatisticaDistribuzioneErrori esito(FiltroEsito esito) {
    this.esito = esito;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaStatisticaDistribuzioneErrori {\n");
    sb.append("    ").append(RicercaStatisticaDistribuzioneErrori.toIndentedString(super.toString())).append("\n");
    sb.append("    report: ").append(RicercaStatisticaDistribuzioneErrori.toIndentedString(this.report)).append("\n");
    sb.append("    azione: ").append(RicercaStatisticaDistribuzioneErrori.toIndentedString(this.azione)).append("\n");
    sb.append("    mittente: ").append(RicercaStatisticaDistribuzioneErrori.toIndentedString(this.mittente)).append("\n");
    sb.append("    esito: ").append(RicercaStatisticaDistribuzioneErrori.toIndentedString(this.esito)).append("\n");
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

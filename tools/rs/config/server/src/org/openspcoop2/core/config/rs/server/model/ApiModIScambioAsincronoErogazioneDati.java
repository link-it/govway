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

public class ApiModIScambioAsincronoErogazioneDati  implements OneOfApiModIScambioAsincrono {
  
  @Schema(required = true, description = "")
  private ModIScambioAsincronoRuoloEnum ruolo = null;
  
  @Schema(example = "3600", required = true, description = "tempo massimo entro cui è attesa la callback, espresso in secondi")
 /**
   * tempo massimo entro cui è attesa la callback, espresso in secondi  
  **/
  private Long tempoMassimoRisposta = null;
  
  @Schema(example = "86400", required = true, description = "durata della disponibilità del dato dopo la callback, espressa in secondi")
 /**
   * durata della disponibilità del dato dopo la callback, espressa in secondi  
  **/
  private Long durataDisponibilita = null;
  
  @Schema(description = "richiesta al fruitore della conferma di recupero della risposta (fase confirmation)")
 /**
   * richiesta al fruitore della conferma di recupero della risposta (fase confirmation)  
  **/
  private Boolean confermaRecupero = false;
  
  @Schema(example = "100", required = true, description = "")
  private Integer numeroMassimoRisultati = null;
 /**
   * Get ruolo
   * @return ruolo
  **/
  @Override
@JsonProperty("ruolo")
  @NotNull
  @Valid
  public ModIScambioAsincronoRuoloEnum getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(ModIScambioAsincronoRuoloEnum ruolo) {
    this.ruolo = ruolo;
  }

  public ApiModIScambioAsincronoErogazioneDati ruolo(ModIScambioAsincronoRuoloEnum ruolo) {
    this.ruolo = ruolo;
    return this;
  }

 /**
   * tempo massimo entro cui è attesa la callback, espresso in secondi
   * minimum: 1
   * @return tempoMassimoRisposta
  **/
  @JsonProperty("tempo_massimo_risposta")
  @NotNull
  @Valid
 @Min(1L)  public Long getTempoMassimoRisposta() {
    return this.tempoMassimoRisposta;
  }

  public void setTempoMassimoRisposta(Long tempoMassimoRisposta) {
    this.tempoMassimoRisposta = tempoMassimoRisposta;
  }

  public ApiModIScambioAsincronoErogazioneDati tempoMassimoRisposta(Long tempoMassimoRisposta) {
    this.tempoMassimoRisposta = tempoMassimoRisposta;
    return this;
  }

 /**
   * durata della disponibilità del dato dopo la callback, espressa in secondi
   * minimum: 1
   * @return durataDisponibilita
  **/
  @JsonProperty("durata_disponibilita")
  @NotNull
  @Valid
 @Min(1L)  public Long getDurataDisponibilita() {
    return this.durataDisponibilita;
  }

  public void setDurataDisponibilita(Long durataDisponibilita) {
    this.durataDisponibilita = durataDisponibilita;
  }

  public ApiModIScambioAsincronoErogazioneDati durataDisponibilita(Long durataDisponibilita) {
    this.durataDisponibilita = durataDisponibilita;
    return this;
  }

 /**
   * richiesta al fruitore della conferma di recupero della risposta (fase confirmation)
   * @return confermaRecupero
  **/
  @JsonProperty("conferma_recupero")
  @Valid
  public Boolean isConfermaRecupero() {
    return this.confermaRecupero;
  }

  public void setConfermaRecupero(Boolean confermaRecupero) {
    this.confermaRecupero = confermaRecupero;
  }

  public ApiModIScambioAsincronoErogazioneDati confermaRecupero(Boolean confermaRecupero) {
    this.confermaRecupero = confermaRecupero;
    return this;
  }

 /**
   * Get numeroMassimoRisultati
   * minimum: 1
   * @return numeroMassimoRisultati
  **/
  @JsonProperty("numero_massimo_risultati")
  @NotNull
  @Valid
 @Min(1)  public Integer getNumeroMassimoRisultati() {
    return this.numeroMassimoRisultati;
  }

  public void setNumeroMassimoRisultati(Integer numeroMassimoRisultati) {
    this.numeroMassimoRisultati = numeroMassimoRisultati;
  }

  public ApiModIScambioAsincronoErogazioneDati numeroMassimoRisultati(Integer numeroMassimoRisultati) {
    this.numeroMassimoRisultati = numeroMassimoRisultati;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiModIScambioAsincronoErogazioneDati {\n");
    
    sb.append("    ruolo: ").append(ApiModIScambioAsincronoErogazioneDati.toIndentedString(this.ruolo)).append("\n");
    sb.append("    tempoMassimoRisposta: ").append(ApiModIScambioAsincronoErogazioneDati.toIndentedString(this.tempoMassimoRisposta)).append("\n");
    sb.append("    durataDisponibilita: ").append(ApiModIScambioAsincronoErogazioneDati.toIndentedString(this.durataDisponibilita)).append("\n");
    sb.append("    confermaRecupero: ").append(ApiModIScambioAsincronoErogazioneDati.toIndentedString(this.confermaRecupero)).append("\n");
    sb.append("    numeroMassimoRisultati: ").append(ApiModIScambioAsincronoErogazioneDati.toIndentedString(this.numeroMassimoRisultati)).append("\n");
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

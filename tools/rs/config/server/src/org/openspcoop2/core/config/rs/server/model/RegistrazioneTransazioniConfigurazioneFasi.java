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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class RegistrazioneTransazioniConfigurazioneFasi  {
  
  @Schema(required = true, description = "")
  private TracciamentoTransazioniStatoFaseBloccante richiestaIngresso = null;
  
  @Schema(required = true, description = "")
  private TracciamentoTransazioniStatoFaseBloccante richiestaUscita = null;
  
  @Schema(required = true, description = "")
  private TracciamentoTransazioniStatoFaseBloccante rispostaUscita = null;
  
  @Schema(required = true, description = "")
  private TracciamentoTransazioniStatoFase rispostaConsegnata = null;
 /**
   * Get richiestaIngresso
   * @return richiestaIngresso
  **/
  @JsonProperty("richiesta_ingresso")
  @NotNull
  @Valid
  public TracciamentoTransazioniStatoFaseBloccante getRichiestaIngresso() {
    return this.richiestaIngresso;
  }

  public void setRichiestaIngresso(TracciamentoTransazioniStatoFaseBloccante richiestaIngresso) {
    this.richiestaIngresso = richiestaIngresso;
  }

  public RegistrazioneTransazioniConfigurazioneFasi richiestaIngresso(TracciamentoTransazioniStatoFaseBloccante richiestaIngresso) {
    this.richiestaIngresso = richiestaIngresso;
    return this;
  }

 /**
   * Get richiestaUscita
   * @return richiestaUscita
  **/
  @JsonProperty("richiesta_uscita")
  @NotNull
  @Valid
  public TracciamentoTransazioniStatoFaseBloccante getRichiestaUscita() {
    return this.richiestaUscita;
  }

  public void setRichiestaUscita(TracciamentoTransazioniStatoFaseBloccante richiestaUscita) {
    this.richiestaUscita = richiestaUscita;
  }

  public RegistrazioneTransazioniConfigurazioneFasi richiestaUscita(TracciamentoTransazioniStatoFaseBloccante richiestaUscita) {
    this.richiestaUscita = richiestaUscita;
    return this;
  }

 /**
   * Get rispostaUscita
   * @return rispostaUscita
  **/
  @JsonProperty("risposta_uscita")
  @NotNull
  @Valid
  public TracciamentoTransazioniStatoFaseBloccante getRispostaUscita() {
    return this.rispostaUscita;
  }

  public void setRispostaUscita(TracciamentoTransazioniStatoFaseBloccante rispostaUscita) {
    this.rispostaUscita = rispostaUscita;
  }

  public RegistrazioneTransazioniConfigurazioneFasi rispostaUscita(TracciamentoTransazioniStatoFaseBloccante rispostaUscita) {
    this.rispostaUscita = rispostaUscita;
    return this;
  }

 /**
   * Get rispostaConsegnata
   * @return rispostaConsegnata
  **/
  @JsonProperty("risposta_consegnata")
  @NotNull
  @Valid
  public TracciamentoTransazioniStatoFase getRispostaConsegnata() {
    return this.rispostaConsegnata;
  }

  public void setRispostaConsegnata(TracciamentoTransazioniStatoFase rispostaConsegnata) {
    this.rispostaConsegnata = rispostaConsegnata;
  }

  public RegistrazioneTransazioniConfigurazioneFasi rispostaConsegnata(TracciamentoTransazioniStatoFase rispostaConsegnata) {
    this.rispostaConsegnata = rispostaConsegnata;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrazioneTransazioniConfigurazioneFasi {\n");
    
    sb.append("    richiestaIngresso: ").append(RegistrazioneTransazioniConfigurazioneFasi.toIndentedString(this.richiestaIngresso)).append("\n");
    sb.append("    richiestaUscita: ").append(RegistrazioneTransazioniConfigurazioneFasi.toIndentedString(this.richiestaUscita)).append("\n");
    sb.append("    rispostaUscita: ").append(RegistrazioneTransazioniConfigurazioneFasi.toIndentedString(this.rispostaUscita)).append("\n");
    sb.append("    rispostaConsegnata: ").append(RegistrazioneTransazioniConfigurazioneFasi.toIndentedString(this.rispostaConsegnata)).append("\n");
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

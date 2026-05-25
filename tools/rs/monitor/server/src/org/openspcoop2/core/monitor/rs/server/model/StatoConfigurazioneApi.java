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
package org.openspcoop2.core.monitor.rs.server.model;

import java.util.List;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class StatoConfigurazioneApi  {
  
  @Schema(required = true, description = "")
  private TransazioneRuoloEnum tipo = null;
  
  @Schema(required = true, description = "Indica se l'erogazione o fruizione debba essere abilitata (true) o disabilitata (false)")
 /**
   * Indica se l'erogazione o fruizione debba essere abilitata (true) o disabilitata (false)  
  **/
  private Boolean abilitato = null;
  
  @Schema(required = true, description = "")
  private FiltroApiStato api = null;
  
  @Schema(description = "Lista dei nomi dei gruppi (di azioni o risorse) su cui applicare l'operazione. Se non fornita, l'operazione si applica a tutti i gruppi configurati per l'API.")
 /**
   * Lista dei nomi dei gruppi (di azioni o risorse) su cui applicare l'operazione. Se non fornita, l'operazione si applica a tutti i gruppi configurati per l'API.  
  **/
  private List<String> gruppi = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TransazioneRuoloEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TransazioneRuoloEnum tipo) {
    this.tipo = tipo;
  }

  public StatoConfigurazioneApi tipo(TransazioneRuoloEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Indica se l'erogazione o fruizione debba essere abilitata (true) o disabilitata (false)
   * @return abilitato
  **/
  @JsonProperty("abilitato")
  @NotNull
  @Valid
  public Boolean isAbilitato() {
    return this.abilitato;
  }

  public void setAbilitato(Boolean abilitato) {
    this.abilitato = abilitato;
  }

  public StatoConfigurazioneApi abilitato(Boolean abilitato) {
    this.abilitato = abilitato;
    return this;
  }

 /**
   * Get api
   * @return api
  **/
  @JsonProperty("api")
  @NotNull
  @Valid
  public FiltroApiStato getApi() {
    return this.api;
  }

  public void setApi(FiltroApiStato api) {
    this.api = api;
  }

  public StatoConfigurazioneApi api(FiltroApiStato api) {
    this.api = api;
    return this;
  }

 /**
   * Lista dei nomi dei gruppi (di azioni o risorse) su cui applicare l'operazione. Se non fornita, l'operazione si applica a tutti i gruppi configurati per l'API.
   * @return gruppi
  **/
  @JsonProperty("gruppi")
  @Valid
  public List<String> getGruppi() {
    return this.gruppi;
  }

  public void setGruppi(List<String> gruppi) {
    this.gruppi = gruppi;
  }

  public StatoConfigurazioneApi gruppi(List<String> gruppi) {
    this.gruppi = gruppi;
    return this;
  }

  public StatoConfigurazioneApi addGruppiItem(String gruppiItem) {
    this.gruppi.add(gruppiItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StatoConfigurazioneApi {\n");
    
    sb.append("    tipo: ").append(StatoConfigurazioneApi.toIndentedString(this.tipo)).append("\n");
    sb.append("    abilitato: ").append(StatoConfigurazioneApi.toIndentedString(this.abilitato)).append("\n");
    sb.append("    api: ").append(StatoConfigurazioneApi.toIndentedString(this.api)).append("\n");
    sb.append("    gruppi: ").append(StatoConfigurazioneApi.toIndentedString(this.gruppi)).append("\n");
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

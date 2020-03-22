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
package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openspcoop2.core.monitor.rs.server.model.TipoFiltroMittenteErogazioneSoggettoRemotoEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

/**
  * Descrive le informazioni di filtraggio relative al mittente per la ricerca di transazioni relative ad erogazioni di servizio
 **/
@Schema(description="Descrive le informazioni di filtraggio relative al mittente per la ricerca di transazioni relative ad erogazioni di servizio")
public class FiltroMittenteErogazioneDistribuzioneSoggettoRemoto  implements OneOfRicercaStatisticaDistribuzioneSoggettoRemotoMittente {
  
  @Schema(description = "")
  private TipoFiltroMittenteErogazioneSoggettoRemotoEnum tipo = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteIdAutenticato.class, name = "identificativo_autenticato"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteErogazioneTokenClaim.class, name = "token_info"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = FiltroMittenteIndirizzoIP.class, name = "indirizzo_ip")  })
  private OneOfFiltroMittenteErogazioneDistribuzioneSoggettoRemotoId id = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @Valid
  public TipoFiltroMittenteErogazioneSoggettoRemotoEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoFiltroMittenteErogazioneSoggettoRemotoEnum tipo) {
    this.tipo = tipo;
  }

  public FiltroMittenteErogazioneDistribuzioneSoggettoRemoto tipo(TipoFiltroMittenteErogazioneSoggettoRemotoEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get id
   * @return id
  **/
  @JsonProperty("id")
  @Valid
  public OneOfFiltroMittenteErogazioneDistribuzioneSoggettoRemotoId getId() {
    return this.id;
  }

  public void setId(OneOfFiltroMittenteErogazioneDistribuzioneSoggettoRemotoId id) {
    this.id = id;
  }

  public FiltroMittenteErogazioneDistribuzioneSoggettoRemoto id(OneOfFiltroMittenteErogazioneDistribuzioneSoggettoRemotoId id) {
    this.id = id;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroMittenteErogazioneDistribuzioneSoggettoRemoto {\n");
    
    sb.append("    tipo: ").append(FiltroMittenteErogazioneDistribuzioneSoggettoRemoto.toIndentedString(this.tipo)).append("\n");
    sb.append("    id: ").append(FiltroMittenteErogazioneDistribuzioneSoggettoRemoto.toIndentedString(this.id)).append("\n");
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

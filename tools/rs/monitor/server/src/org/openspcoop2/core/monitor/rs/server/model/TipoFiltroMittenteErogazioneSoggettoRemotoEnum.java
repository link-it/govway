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
package org.openspcoop2.core.monitor.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoFiltroMittenteErogazioneSoggettoRemotoEnum
 */
public enum TipoFiltroMittenteErogazioneSoggettoRemotoEnum {
IDENTIFICATIVO_AUTENTICATO("identificativo_autenticato"),
  TOKEN_INFO("token_info"),
  INDIRIZZO_IP("indirizzo_ip");

  private String value;

  TipoFiltroMittenteErogazioneSoggettoRemotoEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoFiltroMittenteErogazioneSoggettoRemotoEnum fromValue(String text) {
    for (TipoFiltroMittenteErogazioneSoggettoRemotoEnum b : TipoFiltroMittenteErogazioneSoggettoRemotoEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

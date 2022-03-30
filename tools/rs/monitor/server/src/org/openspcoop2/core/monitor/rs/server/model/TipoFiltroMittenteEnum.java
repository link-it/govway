/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
 * Gets or Sets TipoFiltroMittenteEnum
 */
public enum TipoFiltroMittenteEnum {
EROGAZIONE_SOGGETTO("erogazione_soggetto"),
  FRUIZIONE_APPLICATIVO("fruizione_applicativo"),
  EROGAZIONE_APPLICATIVO("erogazione_applicativo"),
  IDENTIFICATIVO_AUTENTICATO("identificativo_autenticato"),
  EROGAZIONE_TOKEN_INFO("erogazione_token_info"),
  TOKEN_INFO("token_info"),
  INDIRIZZO_IP("indirizzo_ip");

  private String value;

  TipoFiltroMittenteEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoFiltroMittenteEnum fromValue(String text) {
    for (TipoFiltroMittenteEnum b : TipoFiltroMittenteEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

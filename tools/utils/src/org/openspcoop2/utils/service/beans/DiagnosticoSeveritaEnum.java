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
package org.openspcoop2.utils.service.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * Gets or Sets DiagnosticoSeveritaEnum
 */
@XmlType(name="DiagnosticoSeveritaEnum")
@XmlEnum(String.class)
public enum DiagnosticoSeveritaEnum {
  @XmlEnumValue("fatal")
FATAL("fatal"),
    @XmlEnumValue("error")
ERROR("error"),
    @XmlEnumValue("warning")
WARNING("warning"),
    @XmlEnumValue("info")
INFO("info"),
    @XmlEnumValue("debug")
DEBUG("debug"),
    @XmlEnumValue("trace")
TRACE("trace");

  private String value;

  DiagnosticoSeveritaEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static DiagnosticoSeveritaEnum fromValue(String text) {
    for (DiagnosticoSeveritaEnum b : DiagnosticoSeveritaEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

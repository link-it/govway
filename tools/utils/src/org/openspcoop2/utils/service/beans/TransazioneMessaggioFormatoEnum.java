/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Gets or Sets TransazioneMessaggioFormatoEnum
 */
@XmlType(name="TransazioneMessaggioFormatoEnum")
@XmlEnum(String.class)
public enum TransazioneMessaggioFormatoEnum {
  @XmlEnumValue("soap11")
SOAP11("soap11"),
    @XmlEnumValue("soap12")
SOAP12("soap12"),
    @XmlEnumValue("xml")
XML("xml"),
    @XmlEnumValue("json")
JSON("json"),
    @XmlEnumValue("multipart")
MULTIPART("multipart"),
    @XmlEnumValue("binary")
BINARY("binary");

  private String value;

  TransazioneMessaggioFormatoEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TransazioneMessaggioFormatoEnum fromValue(String text) {
    for (TransazioneMessaggioFormatoEnum b : TransazioneMessaggioFormatoEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

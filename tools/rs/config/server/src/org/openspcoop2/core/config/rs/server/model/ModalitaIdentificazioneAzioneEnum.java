package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ModalitaIdentificazioneAzioneEnum
 */
public enum ModalitaIdentificazioneAzioneEnum {
CONTENT_BASED("content-based"),
  HEADER_BASED("header-based"),
  INPUT_BASED("input-based"),
  INTERFACE_BASED("interface-based"),
  SOAP_ACTION_BASED("soap-action-based"),
  URL_BASED("url-based");

  private String value;

  ModalitaIdentificazioneAzioneEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static ModalitaIdentificazioneAzioneEnum fromValue(String text) {
    for (ModalitaIdentificazioneAzioneEnum b : ModalitaIdentificazioneAzioneEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ModalitaAccessoEnum
 */
public enum ModalitaAccessoEnum {
HTTP_BASIC("http-basic"),
  HTTPS("https"),
  PRINCIPAL("principal");

  private String value;

  ModalitaAccessoEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static ModalitaAccessoEnum fromValue(String text) {
    for (ModalitaAccessoEnum b : ModalitaAccessoEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

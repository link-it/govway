package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoAutenticazioneNewEnum
 */
public enum TipoAutenticazioneNewEnum {
DISABILITATO("disabilitato"),
  HTTP_BASIC("http-basic"),
  HTTPS("https"),
  PRINCIPAL("principal");

  private String value;

  TipoAutenticazioneNewEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoAutenticazioneNewEnum fromValue(String text) {
    for (TipoAutenticazioneNewEnum b : TipoAutenticazioneNewEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

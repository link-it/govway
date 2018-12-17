package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoAutenticazioneEnum
 */
public enum TipoAutenticazioneEnum {
DISABILITATO("disabilitato"),
  HTTP_BASIC("http-basic"),
  HTTPS("https"),
  PRINCIPAL("principal"),
  CUSTOM("custom");

  private String value;

  TipoAutenticazioneEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoAutenticazioneEnum fromValue(String text) {
    for (TipoAutenticazioneEnum b : TipoAutenticazioneEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}

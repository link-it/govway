package org.openspcoop2.core.monitor.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

public class TransazioneSearchFilter  {
  public enum TipoEnum {
    INTERVALLOTEMPORALE("intervalloTemporale"),
    IDENTIFICATIVOAPPLICATIVO("identificativoApplicativo"),
    IDENTIFICATIVOMESSAGGIO("identificativoMessaggio"),
    IDENTIFICATIVOTRANSAZIONE("identificativoTransazione");

    private String value;

    TipoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static TipoEnum fromValue(String text) {
      for (TipoEnum b : TipoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "intervalloTemporale", required = true, description = "")
  private TipoEnum tipo = null;
  
  @Schema(description = "")
  private Object filtro = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public String getTipo() {
    if (this.tipo == null) {
      return null;
    }
    return this.tipo.getValue();
  }

  public void setTipo(TipoEnum tipo) {
    this.tipo = tipo;
  }

  public TransazioneSearchFilter tipo(TipoEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get filtro
   * @return filtro
  **/
  @JsonProperty("filtro")
  @Valid
  public Object getFiltro() {
    return this.filtro;
  }

  public void setFiltro(Object filtro) {
    this.filtro = filtro;
  }

  public TransazioneSearchFilter filtro(Object filtro) {
    this.filtro = filtro;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneSearchFilter {\n");
    
    sb.append("    tipo: ").append(TransazioneSearchFilter.toIndentedString(this.tipo)).append("\n");
    sb.append("    filtro: ").append(TransazioneSearchFilter.toIndentedString(this.filtro)).append("\n");
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

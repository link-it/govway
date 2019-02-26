package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

public class FiltroIdMessaggio  {
  public enum TipoMessaggioEnum {
    RICHIESTA("richiesta"),
    RISPOSTA("risposta");

    private String value;

    TipoMessaggioEnum(String value) {
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
    public static TipoMessaggioEnum fromValue(String text) {
      for (TipoMessaggioEnum b : TipoMessaggioEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "richiesta", description = "")
  private TipoMessaggioEnum tipoMessaggio = null;
  
  @Schema(example = "abc456", description = "")
  private String idMessaggio = null;
 /**
   * Get tipoMessaggio
   * @return tipoMessaggio
  **/
  @JsonProperty("tipoMessaggio")
  @Valid
  public String getTipoMessaggio() {
    if (this.tipoMessaggio == null) {
      return null;
    }
    return this.tipoMessaggio.getValue();
  }

  public void setTipoMessaggio(TipoMessaggioEnum tipoMessaggio) {
    this.tipoMessaggio = tipoMessaggio;
  }

  public FiltroIdMessaggio tipoMessaggio(TipoMessaggioEnum tipoMessaggio) {
    this.tipoMessaggio = tipoMessaggio;
    return this;
  }

 /**
   * Get idMessaggio
   * @return idMessaggio
  **/
  @JsonProperty("idMessaggio")
  @Valid
  public String getIdMessaggio() {
    return this.idMessaggio;
  }

  public void setIdMessaggio(String idMessaggio) {
    this.idMessaggio = idMessaggio;
  }

  public FiltroIdMessaggio idMessaggio(String idMessaggio) {
    this.idMessaggio = idMessaggio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroIdMessaggio {\n");
    
    sb.append("    tipoMessaggio: ").append(FiltroIdMessaggio.toIndentedString(this.tipoMessaggio)).append("\n");
    sb.append("    idMessaggio: ").append(FiltroIdMessaggio.toIndentedString(this.idMessaggio)).append("\n");
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

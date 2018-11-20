package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FiltroIdMessaggio  {
@XmlType(name="TipoMessaggioEnum")
@XmlEnum(String.class)
public enum TipoMessaggioEnum {

@XmlEnumValue("richiesta") RICHIESTA(String.valueOf("richiesta")), @XmlEnumValue("risposta") RISPOSTA(String.valueOf("risposta"));


    private String value;

    TipoMessaggioEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static TipoMessaggioEnum fromValue(String v) {
        for (TipoMessaggioEnum b : TipoMessaggioEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
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
  public String getTipoMessaggio() {
    if (this.tipoMessaggio == null) {
      return null;
    }
    return this.tipoMessaggio.value();
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

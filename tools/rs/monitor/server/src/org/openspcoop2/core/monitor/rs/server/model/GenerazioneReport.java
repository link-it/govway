package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerazioneReport  {
@XmlType(name="TipoReportEnum")
@XmlEnum(String.class)
public enum TipoReportEnum {

@XmlEnumValue("Distribuzione Temporale") TEMPORALE(String.valueOf("Distribuzione Temporale")), @XmlEnumValue("Distribuzione per Esiti") PER_ESITI(String.valueOf("Distribuzione per Esiti")), @XmlEnumValue("Distribuzione per Soggetto Remoto") PER_SOGGETTO_REMOTO(String.valueOf("Distribuzione per Soggetto Remoto")), @XmlEnumValue("Distribuzione per Soggetto Locale") PER_SOGGETTO_LOCALE(String.valueOf("Distribuzione per Soggetto Locale")), @XmlEnumValue("Distribuzione per Servizio") PER_SERVIZIO(String.valueOf("Distribuzione per Servizio")), @XmlEnumValue("Distribuzione per Azione") PER_AZIONE(String.valueOf("Distribuzione per Azione")), @XmlEnumValue("Distribuzione per Servizio Applicativo") PER_SERVIZIO_APPLICATIVO(String.valueOf("Distribuzione per Servizio Applicativo")), @XmlEnumValue("Distribuzione Personalizzata") PERSONALIZZATA(String.valueOf("Distribuzione Personalizzata"));


    private String value;

    TipoReportEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static TipoReportEnum fromValue(String v) {
        for (TipoReportEnum b : TipoReportEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "Distribuzione Temporale", description = "")
  private TipoReportEnum tipoReport = null;
  
  @Schema(description = "")
  private LocalDate dataDa = null;
  
  @Schema(description = "")
  private LocalDate dataA = null;
@XmlType(name="ProtocolloEnum")
@XmlEnum(String.class)
public enum ProtocolloEnum {

@XmlEnumValue("spcoop") SPCOOP(String.valueOf("spcoop")), @XmlEnumValue("trasparente") TRASPARENTE(String.valueOf("trasparente")), @XmlEnumValue("sdi") SDI(String.valueOf("sdi")), @XmlEnumValue("edelivery") EDELIVERY(String.valueOf("edelivery"));


    private String value;

    ProtocolloEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static ProtocolloEnum fromValue(String v) {
        for (ProtocolloEnum b : ProtocolloEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "trasparente", description = "")
  private ProtocolloEnum protocollo = null;
  
  @Schema(example = "EnteInterno", description = "")
  private String soggettoLocale = null;
  
  @Schema(example = "EnteEsterno", description = "")
  private String soggettoRemoto = null;
  
  @Schema(example = "servizio1", description = "")
  private String servizio = null;
  
  @Schema(example = "azione1", description = "")
  private String azione = null;
@XmlType(name="TipologiaEnum")
@XmlEnum(String.class)
public enum TipologiaEnum {

@XmlEnumValue("fruzione") FRUZIONE(String.valueOf("fruzione")), @XmlEnumValue("erogazione") EROGAZIONE(String.valueOf("erogazione"));


    private String value;

    TipologiaEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static TipologiaEnum fromValue(String v) {
        for (TipologiaEnum b : TipologiaEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "fruizione", description = "")
  private TipologiaEnum tipologia = null;
@XmlType(name="EsitoEnum")
@XmlEnum(String.class)
public enum EsitoEnum {

@XmlEnumValue("fallite") FALLITE(String.valueOf("fallite")), @XmlEnumValue("fault applicativo") FAULT_APPLICATIVO(String.valueOf("fault applicativo")), @XmlEnumValue("completate con successo") COMPLETATE_CON_SUCCESSO(String.valueOf("completate con successo"));


    private String value;

    EsitoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static EsitoEnum fromValue(String v) {
        for (EsitoEnum b : EsitoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "fallite", description = "")
  private EsitoEnum esito = null;
@XmlType(name="DettaglioEsitoEnum")
@XmlEnum(String.class)
public enum DettaglioEsitoEnum {

@XmlEnumValue("ok") OK(String.valueOf("ok")), @XmlEnumValue("ok (presenza anomalie") OK_PRESENZA_ANOMALIE(String.valueOf("ok (presenza anomalie")), @XmlEnumValue("fault applicativo") FAULT_APPLICATIVO(String.valueOf("fault applicativo")), @XmlEnumValue("autenticazione fallita") AUTENTICAZIONE_FALLITA(String.valueOf("autenticazione fallita")), @XmlEnumValue("autorizzazione negata") AUTORIZZAZIONE_NEGATA(String.valueOf("autorizzazione negata")), @XmlEnumValue("richiesta client rifiutata") RICHIESTA_CLIENT_RIFIUTATA(String.valueOf("richiesta client rifiutata")), @XmlEnumValue("errore di connessione") ERRORE_DI_CONNESSIONE(String.valueOf("errore di connessione")), @XmlEnumValue("errore di protocollo") ERRORE_DI_PROTOCOLLO(String.valueOf("errore di protocollo")), @XmlEnumValue("violazione rate limiting") VIOLAZIONE_RATE_LIMITING(String.valueOf("violazione rate limiting")), @XmlEnumValue("violazione rate limiting warningOnly") VIOLAZIONE_RATE_LIMITING_WARNINGONLY(String.valueOf("violazione rate limiting warningOnly")), @XmlEnumValue("superamento limite richieste") SUPERAMENTO_LIMITE_RICHIESTE(String.valueOf("superamento limite richieste")), @XmlEnumValue("superamento limite richieste warningOnly") SUPERAMENTO_LIMITE_RICHIESTE_WARNINGONLY(String.valueOf("superamento limite richieste warningOnly")), @XmlEnumValue("fault pdd esterna") FAULT_PDD_ESTERNA(String.valueOf("fault pdd esterna")), @XmlEnumValue("contenuto richiesta non riconosciuto") CONTENUTO_RICHIESTA_NON_RICONOSCIUTO(String.valueOf("contenuto richiesta non riconosciuto")), @XmlEnumValue("contenuto risposta non riconosciuto") CONTENUTO_RISPOSTA_NON_RICONOSCIUTO(String.valueOf("contenuto risposta non riconosciuto")), @XmlEnumValue("connessione client interrotta") CONNESSIONE_CLIENT_INTERROTTA(String.valueOf("connessione client interrotta")), @XmlEnumValue("fault generato dalla pdd") FAULT_GENERATO_DALLA_PDD(String.valueOf("fault generato dalla pdd")), @XmlEnumValue("errore interno pdd") ERRORE_INTERNO_PDD(String.valueOf("errore interno pdd"));


    private String value;

    DettaglioEsitoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static DettaglioEsitoEnum fromValue(String v) {
        for (DettaglioEsitoEnum b : DettaglioEsitoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "ok", description = "")
  private DettaglioEsitoEnum dettaglioEsito = null;
@XmlType(name="ContestoEnum")
@XmlEnum(String.class)
public enum ContestoEnum {

@XmlEnumValue("applicativo") APPLICATIVO(String.valueOf("applicativo")), @XmlEnumValue("sistema") SISTEMA(String.valueOf("sistema"));


    private String value;

    ContestoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static ContestoEnum fromValue(String v) {
        for (ContestoEnum b : ContestoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "applicativo", description = "")
  private ContestoEnum contesto = null;
@XmlType(name="TipoGraficoEnum")
@XmlEnum(String.class)
public enum TipoGraficoEnum {

@XmlEnumValue("Line") LINE(String.valueOf("Line")), @XmlEnumValue("Pie") PIE(String.valueOf("Pie")), @XmlEnumValue("Bar") BAR(String.valueOf("Bar")), @XmlEnumValue("Table") TABLE(String.valueOf("Table"));


    private String value;

    TipoGraficoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static TipoGraficoEnum fromValue(String v) {
        for (TipoGraficoEnum b : TipoGraficoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "Line", description = "")
  private TipoGraficoEnum tipoGrafico = null;
@XmlType(name="UnitaTempoEnum")
@XmlEnum(String.class)
public enum UnitaTempoEnum {

@XmlEnumValue("orario") ORARIO(String.valueOf("orario")), @XmlEnumValue("giornaliero") GIORNALIERO(String.valueOf("giornaliero")), @XmlEnumValue("settimanale") SETTIMANALE(String.valueOf("settimanale")), @XmlEnumValue("mensile") MENSILE(String.valueOf("mensile"));


    private String value;

    UnitaTempoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static UnitaTempoEnum fromValue(String v) {
        for (UnitaTempoEnum b : UnitaTempoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "giornaliero", description = "")
  private UnitaTempoEnum unitaTempo = null;
@XmlType(name="VisualizzaEnum")
@XmlEnum(String.class)
public enum VisualizzaEnum {

@XmlEnumValue("numeroTransazioni") NUMEROTRANSAZIONI(String.valueOf("numeroTransazioni")), @XmlEnumValue("occupazioneBanda") OCCUPAZIONEBANDA(String.valueOf("occupazioneBanda")), @XmlEnumValue("TempoMedioRisposta") TEMPOMEDIORISPOSTA(String.valueOf("TempoMedioRisposta"));


    private String value;

    VisualizzaEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static VisualizzaEnum fromValue(String v) {
        for (VisualizzaEnum b : VisualizzaEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "numeroTransazioni", description = "")
  private VisualizzaEnum visualizza = null;
 /**
   * Get tipoReport
   * @return tipoReport
  **/
  @JsonProperty("tipoReport")
  public String getTipoReport() {
    if (this.tipoReport == null) {
      return null;
    }
    return this.tipoReport.value();
  }

  public void setTipoReport(TipoReportEnum tipoReport) {
    this.tipoReport = tipoReport;
  }

  public GenerazioneReport tipoReport(TipoReportEnum tipoReport) {
    this.tipoReport = tipoReport;
    return this;
  }

 /**
   * Get dataDa
   * @return dataDa
  **/
  @JsonProperty("dataDa")
  public LocalDate getDataDa() {
    return this.dataDa;
  }

  public void setDataDa(LocalDate dataDa) {
    this.dataDa = dataDa;
  }

  public GenerazioneReport dataDa(LocalDate dataDa) {
    this.dataDa = dataDa;
    return this;
  }

 /**
   * Get dataA
   * @return dataA
  **/
  @JsonProperty("dataA")
  public LocalDate getDataA() {
    return this.dataA;
  }

  public void setDataA(LocalDate dataA) {
    this.dataA = dataA;
  }

  public GenerazioneReport dataA(LocalDate dataA) {
    this.dataA = dataA;
    return this;
  }

 /**
   * Get protocollo
   * @return protocollo
  **/
  @JsonProperty("protocollo")
  public String getProtocollo() {
    if (this.protocollo == null) {
      return null;
    }
    return this.protocollo.value();
  }

  public void setProtocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
  }

  public GenerazioneReport protocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get soggettoLocale
   * @return soggettoLocale
  **/
  @JsonProperty("soggettoLocale")
  public String getSoggettoLocale() {
    return this.soggettoLocale;
  }

  public void setSoggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
  }

  public GenerazioneReport soggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
    return this;
  }

 /**
   * Get soggettoRemoto
   * @return soggettoRemoto
  **/
  @JsonProperty("soggettoRemoto")
  public String getSoggettoRemoto() {
    return this.soggettoRemoto;
  }

  public void setSoggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
  }

  public GenerazioneReport soggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
    return this;
  }

 /**
   * Get servizio
   * @return servizio
  **/
  @JsonProperty("servizio")
  public String getServizio() {
    return this.servizio;
  }

  public void setServizio(String servizio) {
    this.servizio = servizio;
  }

  public GenerazioneReport servizio(String servizio) {
    this.servizio = servizio;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public GenerazioneReport azione(String azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get tipologia
   * @return tipologia
  **/
  @JsonProperty("tipologia")
  public String getTipologia() {
    if (this.tipologia == null) {
      return null;
    }
    return this.tipologia.value();
  }

  public void setTipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
  }

  public GenerazioneReport tipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  public String getEsito() {
    if (this.esito == null) {
      return null;
    }
    return this.esito.value();
  }

  public void setEsito(EsitoEnum esito) {
    this.esito = esito;
  }

  public GenerazioneReport esito(EsitoEnum esito) {
    this.esito = esito;
    return this;
  }

 /**
   * Get dettaglioEsito
   * @return dettaglioEsito
  **/
  @JsonProperty("dettaglioEsito")
  public String getDettaglioEsito() {
    if (this.dettaglioEsito == null) {
      return null;
    }
    return this.dettaglioEsito.value();
  }

  public void setDettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
  }

  public GenerazioneReport dettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
    return this;
  }

 /**
   * Get contesto
   * @return contesto
  **/
  @JsonProperty("contesto")
  public String getContesto() {
    if (this.contesto == null) {
      return null;
    }
    return this.contesto.value();
  }

  public void setContesto(ContestoEnum contesto) {
    this.contesto = contesto;
  }

  public GenerazioneReport contesto(ContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }

 /**
   * Get tipoGrafico
   * @return tipoGrafico
  **/
  @JsonProperty("tipoGrafico")
  public String getTipoGrafico() {
    if (this.tipoGrafico == null) {
      return null;
    }
    return this.tipoGrafico.value();
  }

  public void setTipoGrafico(TipoGraficoEnum tipoGrafico) {
    this.tipoGrafico = tipoGrafico;
  }

  public GenerazioneReport tipoGrafico(TipoGraficoEnum tipoGrafico) {
    this.tipoGrafico = tipoGrafico;
    return this;
  }

 /**
   * Get unitaTempo
   * @return unitaTempo
  **/
  @JsonProperty("unitaTempo")
  public String getUnitaTempo() {
    if (this.unitaTempo == null) {
      return null;
    }
    return this.unitaTempo.value();
  }

  public void setUnitaTempo(UnitaTempoEnum unitaTempo) {
    this.unitaTempo = unitaTempo;
  }

  public GenerazioneReport unitaTempo(UnitaTempoEnum unitaTempo) {
    this.unitaTempo = unitaTempo;
    return this;
  }

 /**
   * Get visualizza
   * @return visualizza
  **/
  @JsonProperty("visualizza")
  public String getVisualizza() {
    if (this.visualizza == null) {
      return null;
    }
    return this.visualizza.value();
  }

  public void setVisualizza(VisualizzaEnum visualizza) {
    this.visualizza = visualizza;
  }

  public GenerazioneReport visualizza(VisualizzaEnum visualizza) {
    this.visualizza = visualizza;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerazioneReport {\n");
    
    sb.append("    tipoReport: ").append(GenerazioneReport.toIndentedString(this.tipoReport)).append("\n");
    sb.append("    dataDa: ").append(GenerazioneReport.toIndentedString(this.dataDa)).append("\n");
    sb.append("    dataA: ").append(GenerazioneReport.toIndentedString(this.dataA)).append("\n");
    sb.append("    protocollo: ").append(GenerazioneReport.toIndentedString(this.protocollo)).append("\n");
    sb.append("    soggettoLocale: ").append(GenerazioneReport.toIndentedString(this.soggettoLocale)).append("\n");
    sb.append("    soggettoRemoto: ").append(GenerazioneReport.toIndentedString(this.soggettoRemoto)).append("\n");
    sb.append("    servizio: ").append(GenerazioneReport.toIndentedString(this.servizio)).append("\n");
    sb.append("    azione: ").append(GenerazioneReport.toIndentedString(this.azione)).append("\n");
    sb.append("    tipologia: ").append(GenerazioneReport.toIndentedString(this.tipologia)).append("\n");
    sb.append("    esito: ").append(GenerazioneReport.toIndentedString(this.esito)).append("\n");
    sb.append("    dettaglioEsito: ").append(GenerazioneReport.toIndentedString(this.dettaglioEsito)).append("\n");
    sb.append("    contesto: ").append(GenerazioneReport.toIndentedString(this.contesto)).append("\n");
    sb.append("    tipoGrafico: ").append(GenerazioneReport.toIndentedString(this.tipoGrafico)).append("\n");
    sb.append("    unitaTempo: ").append(GenerazioneReport.toIndentedString(this.unitaTempo)).append("\n");
    sb.append("    visualizza: ").append(GenerazioneReport.toIndentedString(this.visualizza)).append("\n");
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

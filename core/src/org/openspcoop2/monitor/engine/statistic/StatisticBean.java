package org.openspcoop2.monitor.engine.statistic;

import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.Utilities;

/**
 * StatisticBean
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticBean {

	private long id;
	private Date dateIntervalLeft;
	private Date dateIntervalRight;
	private Date data;
	private String idPorta;
	private TipoPdD tipoPorta;
	private IDSoggetto mittente;
	private IDSoggetto destinatario;
	private String tipoServizio;
	private String servizio;
	private Integer versioneServizio;
	private String azione;
	private String servizioApplicativo;
	private Integer esito;
	private String esitoContesto;
	private long richieste;
	private long bytesBandaTotale;
	private long bytesBandaInterna;
	private long bytesBandaEsterna;
	private long latenzaTotale;
	private long latenzaServizio;
	private long latenzaPorta;
	private String idStatistica;
	private String pluginClassname;

	public String getPluginClassname() {
		return this.pluginClassname;
	}
	public void setPluginClassname(String pluginClassname) {
		this.pluginClassname = pluginClassname;
	}
	public String getIdStatistica() {
		return this.idStatistica;
	}
	public void setIdStatistica(String idStatistica) {
		this.idStatistica = idStatistica;
	}
 	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getData() {
		return this.data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Date getDateIntervalLeft() {
		return this.dateIntervalLeft;
	}
	public void setDateIntervalLeft(Date dateIntervalLeft) {
		this.dateIntervalLeft = dateIntervalLeft;
	}
	public Date getDateIntervalRight() {
		return this.dateIntervalRight;
	}
	public void setDateIntervalRight(Date dateIntervalRight) {
		this.dateIntervalRight = dateIntervalRight;
	}
	public String getIdPorta() {
		return this.idPorta;
	}
	public void setIdPorta(String id_porta) {
		this.idPorta = id_porta;
	}
	public TipoPdD getTipoPorta() {
		return this.tipoPorta;
	}
	public void setTipoPorta(TipoPdD tipo_porta) {
		this.tipoPorta = tipo_porta;
	}
	public IDSoggetto getMittente() {
		return this.mittente;
	}
	public void setMittente(IDSoggetto mittente) {
		this.mittente = mittente;
	}
	public IDSoggetto getDestinatario() {
		return this.destinatario;
	}
	public void setDestinatario(IDSoggetto destinatario) {
		this.destinatario = destinatario;
	}
	public String getTipoServizio() {
		return this.tipoServizio;
	}
	public void setTipoServizio(String tipo_servizio) {
		this.tipoServizio = tipo_servizio;
	}
	public String getServizio() {
		return this.servizio;
	}
	public void setServizio(String servizio) {
		this.servizio = servizio;
	}
	public Integer getVersioneServizio() {
		return this.versioneServizio;
	}
	public void setVersioneServizio(Integer versioneServizio) {
		this.versioneServizio = versioneServizio;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public void setServizioApplicativo(String servizio_applicativo) {
		this.servizioApplicativo = servizio_applicativo;
	}
	public Integer getEsito() {
		return this.esito;
	}
	public void setEsito(Integer esito) {
		this.esito = esito;
	}
	public String getEsitoContesto() {
		return this.esitoContesto;
	}
	public void setEsitoContesto(String esitoContesto) {
		this.esitoContesto = esitoContesto;
	}
	public long getRichieste() {
		return this.richieste;
	}
	public void setRichieste(long richieste) {
		this.richieste = richieste;
	}
	
	public long getBytesBandaTotale() {
		return this.bytesBandaTotale;
	}
	public void setBytesBandaTotale(long bytesBandaTotale) {
		this.bytesBandaTotale = bytesBandaTotale;
	}
	public long getBytesBandaInterna() {
		return this.bytesBandaInterna;
	}
	public void setBytesBandaInterna(long bytesBandaInterna) {
		this.bytesBandaInterna = bytesBandaInterna;
	}
	public long getBytesBandaEsterna() {
		return this.bytesBandaEsterna;
	}
	public void setBytesBandaEsterna(long bytesBandaEsterna) {
		this.bytesBandaEsterna = bytesBandaEsterna;
	}
	
	public long getLatenzaTotale() {
		return this.latenzaTotale;
	}
	public void setLatenzaTotale(long latenzaTotale) {
		this.latenzaTotale = latenzaTotale;
	}
	public long getLatenzaServizio() {
		return this.latenzaServizio;
	}
	public void setLatenzaServizio(long latenzaServizio) {
		this.latenzaServizio = latenzaServizio;
	}
	public long getLatenzaPorta() {
		return this.latenzaPorta;
	}
	public void setLatenzaPorta(long latenzaPorta) {
		this.latenzaPorta = latenzaPorta;
	}
	
	@Override
	public String toString(){
		
		StringBuffer bf = new StringBuffer();
		
		bf.append("data[");
		bf.append(this.data.toString());
		bf.append("] id-porta[");
		bf.append(this.idPorta);
		bf.append("] tipo-porta[");
		bf.append(this.tipoPorta.getTipo());
		bf.append("] mittente[");
		bf.append(this.mittente.toString());
		bf.append("] destinatario[");
		bf.append(this.destinatario.toString());
		bf.append("] servizio[");
		bf.append(this.tipoServizio);
		bf.append("/");
		bf.append(this.servizio);
		bf.append(":");
		bf.append(this.versioneServizio);
		bf.append("] azione[");
		bf.append(this.azione);
		bf.append("] servizio-applicativo[");
		bf.append(this.servizioApplicativo);
		bf.append("] esito[");
		bf.append(this.esito);
		bf.append("] esito-contesto[");
		bf.append(this.esitoContesto);
		bf.append("] richieste[");
		bf.append(this.richieste);
		bf.append("] bytes-banda-totale[");
		bf.append(Utilities.convertBytesToFormatString(this.bytesBandaTotale));
		bf.append("] bytes-banda-interna[");
		bf.append(Utilities.convertBytesToFormatString(this.bytesBandaInterna));
		bf.append("] bytes-banda-esterna[");
		bf.append(Utilities.convertBytesToFormatString(this.bytesBandaEsterna));
		bf.append("] latenza-totale[");
		if(this.latenzaTotale>=0){
			bf.append(Utilities.convertSystemTimeIntoString_millisecondi(this.latenzaTotale,true));
		}
		else{
			bf.append("N.D.");
		}
		bf.append("] latenza-servizio[");
		if(this.latenzaServizio>=0){
			bf.append(Utilities.convertSystemTimeIntoString_millisecondi(this.latenzaServizio,true));
		}
		else{
			bf.append("N.D.");
		}
		bf.append("] latenza-porta[");
		if(this.latenzaPorta>=0){
			bf.append(Utilities.convertSystemTimeIntoString_millisecondi(this.latenzaPorta,true));
		}
		else{
			bf.append("N.D.");
		}
		bf.append("]");

		return bf.toString();
	}

	
}

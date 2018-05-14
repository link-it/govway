package org.openspcoop2.web.monitor.statistiche.export;

import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULL;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho;
import org.slf4j.Logger;

import it.link.pdd.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.web.monitor.core.report.Templates;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA.DettaglioSA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPD;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.utils.ConfigurazioniUtils;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperCsvExporterBuilder;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;

public class ConfigurazioniCsvExporter {

	private Logger log = null;
	private List<String> chiaviColonne = null;
	private List<String> labelColonne = null;
	private PddRuolo ruolo = null;

	public ConfigurazioniCsvExporter(Logger log, PddRuolo ruolo) {
		this.log = log;
		this.ruolo = ruolo;
		this.chiaviColonne = new ArrayList<>();
		this.labelColonne = new ArrayList<>();
		this.init();
	}

	public void init(){
		if(this.ruolo.equals(PddRuolo.DELEGATA)) {
			// init colonne delegata

			/*
		Label Generali

    azioni (dove viene indicata la singola azione, o le azioni o la scritta unica azione...)
    url di invocazione
    Identificazione Azione: ....
    Espressione: xpath o regolare

	    Autenticazione (stato)
	    Autenticazione (opzionale
	    Autorizzazione (stato)
	    Applicativi Autorizzati la colonna contiene l'elenco degli applicativi separati da '\n').
	    Ruoli (separati da '\n')
	    Ruoli Richiesti (all/any)
	  
	    Servizio Applicativo
	    MessageBox
	    Sbustamento SOAP
	    Sbustamento Protocollo
	    Connettore (Tipo)
	    Connettore (EndPoint)
	    Connettore (Debug)
	    Connettore (Username)
	    Una singola colonna per ogni altri valore possibile per i connettori http e https denominandola come Connettore (Proxy Endpoint) , Connettore (SSLType) ...
	    Una restante colonna con Connettore (Altre configurazioni) dove si elencano le proprietà rimanenti e le proprietà custom separate da '\n'

			 * */

			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORTA_DELEGATA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ASPC);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORT_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_FRUITORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_EROGATORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SERVIZIO);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_IDENTIFICAZIONE_AZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ESPRESSIONE);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_APPLICATIVI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RUOLI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RUOLI_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TIPO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_ENDPOINT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_DEBUG);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_PROXY_ENDPOINT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_PROXY_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_SSL_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HOSTNAME_VERIFIER);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_KEY_STORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TRUST_STORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_CLIENT_CERTIFICATE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_ALTRE_CONFIGURAZIONI);

		} else {
			// init colonne applicativa   
			/*
		Label Generali 		 
	    Autenticazione (stato)
	    Autenticazione (opzionale
	    Autorizzazione (stato)
	    Soggetti Autorizzati la colonna contiene l'elenco dei soggetti  separati da '\n').
	    Ruoli (separati da '\n')
	    Ruoli Richiesti (all/any)
	    Servizio Applicativo
	    MessageBox
	    Sbustamento SOAP
	    Sbustamento Protocollo
	    Connettore (Tipo)
	    Connettore (EndPoint)
	    Connettore (Debug)
	    Connettore (Username)
	    Una singola colonna per ogni altri valore possibile per i connettori http e https denominandola come Connettore (Proxy Endpoint) , Connettore (SSLType) ...
	    Una restante colonna con Connettore (Altre configurazioni) dove si elencano le proprietà rimanenti e le proprietà custom separate da '\n'

			 * */


			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORTA_APPLICATIVA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ASPC);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORT_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_EROGATORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SERVIZIO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AZIONE);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SOGGETTI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RUOLI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RUOLI_RICHIESTI);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_SERVIZIO_APPLICATIVO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_MESSAGE_BOX);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SBUSTAMENTO_SOAP);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SBUSTAMENTO_PROTOCOLLO);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TIPO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_ENDPOINT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_DEBUG);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_PROXY_ENDPOINT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_PROXY_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_SSL_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HOSTNAME_VERIFIER);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_KEY_STORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_TRUST_STORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_CLIENT_CERTIFICATE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_CONNETTORE_ALTRE_CONFIGURAZIONI);

		}

		for (int i = 0; i < this.labelColonne.size(); i++) {
			this.chiaviColonne.add(CostantiConfigurazioni.PREFIX_COLONNA + i);
		}
	}

	public String exportConfigurazioni(List<ConfigurazioneGenerale> listaConfigurazioni, java.io.OutputStream out) throws Exception {
		String errMsg = null;

		DRDataSource dataSource = creaDatasourceConfigurazioni(this.chiaviColonne, this.log);

		popolaDataSourceExport(dataSource,listaConfigurazioni);

		JasperReportBuilder reportBuilder = creaReportBuilder(dataSource, this.log);

		this.esportaCsv(out, reportBuilder, this.chiaviColonne, this.labelColonne);

		return errMsg;
	}


	public void esportaCsv(OutputStream outputStream, JasperReportBuilder report,List<String> chiaviColonne, List<String> labelColonne) throws Exception{
		List<ColumnBuilder<?,?>> colonne = new ArrayList<ColumnBuilder<?,?>>();

		// generazione delle label delle colonne
		for (int i = 0; i < labelColonne.size(); i++) {
			String label = labelColonne.get(i);
			String keyColonna = chiaviColonne.get(i);
			TextColumnBuilder<String> nomeColumn = col.column(label, keyColonna, type.stringType());
			colonne.add(nomeColumn);
		}

		report
		.setColumnTitleStyle(Templates.columnTitleStyle)
		.addProperty("net.sf.jasperreports.export.csv.exclude.origin.keep.first.band.1", "columnHeader")
		.ignorePageWidth()
		.ignorePagination()
		.columns(colonne.toArray(new ColumnBuilder[colonne.size()]));

		JasperCsvExporterBuilder builder = export.csvExporter(outputStream);
		report.toCsv(builder); 
	}

	public JasperReportBuilder creaReportBuilder(JRDataSource dataSource,Logger log) throws Exception{
		JasperReportBuilder builder = report();
		builder.setDataSource(dataSource);
		return builder;
	}

	private DRDataSource creaDatasourceConfigurazioni(List<String> colonneSelezionate,Logger log) throws Exception {
		// Scittura Intestazione sono le chiavi delle colonne scelte
		List<String> header = new ArrayList<String>();
		header.addAll(colonneSelezionate);

		DRDataSource dataSource = new DRDataSource(header.toArray(new String[header.size()])); 
		return dataSource;
	}

	private void popolaDataSourceExport(DRDataSource dataSource, List<ConfigurazioneGenerale> lstConfigurazioni) throws DriverRegistroServiziException  {

		for(ConfigurazioneGenerale configurazione: lstConfigurazioni){
			if(this.ruolo.equals(PddRuolo.DELEGATA)) {
				this.addLinePD(dataSource,configurazione);
			} else {
				List<DettaglioSA> listaSA = configurazione.getPa().getListaSA();
				// se ho piu' di un servizio applicativo aggiungo un linea per servizio
				if(listaSA == null || listaSA.size() <= 1){
					DettaglioSA dettaglioSA = (listaSA != null && listaSA.size() > 0) ? listaSA.get(0): null;
					this.addLinePA(dataSource,configurazione, dettaglioSA);
				} else {
					for (DettaglioSA dettaglioSA : listaSA) {
						this.addLinePA(dataSource,configurazione, dettaglioSA);
					}
				}
			}
		}//chiudo for configurazioni
	}

	private void addLinePA(DRDataSource dataSource, ConfigurazioneGenerale configurazione, DettaglioSA dettaglioSA) throws DriverRegistroServiziException  {
		List<Object> oneLine = new ArrayList<Object>();
		DettaglioPA dettaglioPA = configurazione.getPa();
		PortaApplicativa paOp2 = dettaglioPA.getPortaApplicativaOp2(); 
		org.openspcoop2.core.commons.search.PortaApplicativa portaApplicativa = dettaglioPA.getPortaApplicativa();

		// NOME PA
		if(StringUtils.isNotEmpty(configurazione.getLabel()))
			oneLine.add(configurazione.getLabel());
		else 
			oneLine.add("");
		
		// STATO
		if(StringUtils.isNotEmpty(configurazione.getStato()))
			oneLine.add(configurazione.getStato());
		else 
			oneLine.add("");

		// ASPC
		if(dettaglioPA.getIdAccordoServizioParteComune() != null) {
			IdAccordoServizioParteComune aspc = dettaglioPA.getIdAccordoServizioParteComune();
			String nomeAspc = aspc.getNome();

			String versioneAspc = aspc.getVersione();

			String nomeReferenteAspc = (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getNome() : null;

			String tipoReferenteAspc= (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getTipo() : null;

			oneLine.add(IDAccordoFactory.getInstance().getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc));
		} else 
			oneLine.add("");

		// PORTTYPE
		if(StringUtils.isNotEmpty(dettaglioPA.getPortType()))
			oneLine.add(dettaglioPA.getPortType());
		else 
			oneLine.add("");

		// EROGATORE
		if(StringUtils.isNotEmpty(configurazione.getErogatore()))
			oneLine.add(configurazione.getErogatore());
		else 
			oneLine.add("");

		// SERVIZIO
		if(StringUtils.isNotEmpty(configurazione.getServizio()))
			oneLine.add(configurazione.getServizio());
		else 
			oneLine.add("");

		// AZIONE
		if(portaApplicativa.getNomeAzione()!=null){
			// Azione: _XXX
			oneLine.add(portaApplicativa.getNomeAzione());
		}
		else{
			PortaApplicativaAzione paAzione = paOp2.getAzione();
			List<String> azioni = dettaglioPA.getAzioni(); 
			if(paAzione==null && (azioni == null || azioni.size() == 0)){
				oneLine.add(CostantiConfigurazioni.LABEL_UTILIZZO_DEL_SERVIZIO_SENZA_AZIONE);
			}
			else{
				StringBuffer sb = new StringBuffer();
				// Azioni: XXXs
				if(azioni != null && azioni.size() > 0){
					for (String azione : azioni) {
						if(sb.length()>0) sb.append("\n");
						sb.append(azione);
					}
				}
				oneLine.add(sb.toString());
			}
		}

		// Autenticazione (stato)
		// Autenticazione (opzionale
		if(dettaglioPA.isSupportatoAutenticazione()){
			if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(paOp2.getAutenticazione())){
				oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
			}
			else{
				oneLine.add(paOp2.getAutenticazione());
			}

			if(CostantiConfigurazione.ABILITATO.equals(paOp2.getAutenticazioneOpzionale())){
				oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
			}
			else{
				oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
			}
		} else {
			// due colonne vuote
			oneLine.add("");
			oneLine.add("");
		}


		// Soggetti Autorizzati la colonna contiene l'elenco dei soggetti  separati da '\n').
		// Ruoli (separati da '\n')
		// Ruoli Richiesti (all/any)

		// Autorizzazione (stato): disabilitato/abilitato/xacmlPolicy/NomeCustom
		String autorizzazione = paOp2.getAutorizzazione();
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazione)){
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.XACML_POLICY.getValue().toLowerCase())){
			oneLine.add(TipoAutorizzazione.XACML_POLICY.getValue());
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
				||
				autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			oneLine.add(autorizzazione);
		}

		// Se abilitato:
		// Servizi Applicativi Autorizzati: sa1 (user:xxx)
		//                                  sa2 (user:xxx)
		//                                  sa3 (user:xxx)
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			List<String> fruitori = dettaglioPA.getFruitori();

			if(fruitori != null && fruitori.size() > 0){
				StringBuffer sb = new StringBuffer();
				for (String fruitore : fruitori) {
					if(sb.length()>0) sb.append("\n");
					sb.append(fruitore);
				}
				oneLine.add(sb.toString());
			} else 
				oneLine.add("");
		}else 
			oneLine.add("");

		// Ruoli: tutti/almenoUno
		// Ruoli Autorizzati: ruolo1 (fonte esterna)
		//                    ruolo2 (fonte interna)
		//                    ruolo3 (fonte qualsiasi)
		//
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())){
			List<String> ruoli = dettaglioPA.getRuoli();
			String match = dettaglioPA.getMatchRuoli();
			if(paOp2.getRuoli()!=null){
				StringBuffer sb = new StringBuffer();

				for (String ruolo : ruoli) {
					if(sb.length()>0) sb.append("\n");
					sb.append(ruolo);
				}

				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
			oneLine.add(match);
		} else {
			oneLine.add("");
			oneLine.add("");
		}

		// colonne servizio applicativo e relativo connettore
		if(dettaglioSA != null) {
			ServizioApplicativo saOp2 = dettaglioSA.getSaOp2();
			// colonne del servizio applicativo NOME | MESSAGE BOX | SBUSTAMENTO SOAP | SBUSTAMENTO PROTOCOLLO
			if(StringUtils.isNotEmpty(saOp2.getNome()))
				oneLine.add(saOp2.getNome());
			else 
				oneLine.add("");

			if(saOp2.getInvocazioneServizio()!=null){
				//  MESSAGE BOX 
				if(StringUtils.isNotEmpty(saOp2.getInvocazioneServizio().getGetMessage().getValue()))
					oneLine.add(saOp2.getInvocazioneServizio().getGetMessage().getValue());
				else 
					oneLine.add("");
				// SBUSTAMENTO SOAP
				if(StringUtils.isNotEmpty(saOp2.getInvocazioneServizio().getSbustamentoSoap().getValue()))
					oneLine.add(saOp2.getInvocazioneServizio().getSbustamentoSoap().getValue());
				else 
					oneLine.add("");
				// SBUSTAMENTO PROTOCOLLO
				if(StringUtils.isNotEmpty(saOp2.getInvocazioneServizio().getSbustamentoInformazioniProtocollo().getValue()))
					oneLine.add(saOp2.getInvocazioneServizio().getSbustamentoInformazioniProtocollo().getValue());
				else 
					oneLine.add("");

				if(saOp2.getInvocazioneServizio().getConnettore()!=null){
					Connettore connettore = saOp2.getInvocazioneServizio().getConnettore();
					oneLine.addAll(this.printConnettore(connettore, CostantiConfigurazioni.LABEL_TIPO, saOp2.getInvocazioneServizio().getCredenziali()));
				} else {
					// 14 colonne vuote
					for (int i = 0; i < 14; i++) {
						oneLine.add("");
					}
				}
			} else {
				// 17 colonne vuote
				for (int i = 0; i < 17; i++) {
					oneLine.add("");
				}
			}

		} else {
			// 18 colonne vuote
			for (int i = 0; i < 18; i++) {
				oneLine.add("");
			}
		}

		dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 
	}

	private void addLinePD(DRDataSource dataSource, ConfigurazioneGenerale configurazione) throws DriverRegistroServiziException  {
		List<Object> oneLine = new ArrayList<Object>();
		DettaglioPD dettaglioPD = configurazione.getPd();
		PortaDelegata pdOp2 = dettaglioPD.getPortaDelegataOp2();

		if(StringUtils.isNotEmpty(configurazione.getLabel()))
			oneLine.add(configurazione.getLabel());
		else 
			oneLine.add("");
		
		// STATO
		if(StringUtils.isNotEmpty(configurazione.getStato()))
			oneLine.add(configurazione.getStato());
		else 
			oneLine.add("");

		if(dettaglioPD.getIdAccordoServizioParteComune() != null) {
			IdAccordoServizioParteComune aspc = dettaglioPD.getIdAccordoServizioParteComune();
			String nomeAspc = aspc.getNome();

			String versioneAspc = aspc.getVersione();

			String nomeReferenteAspc = (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getNome() : null;

			String tipoReferenteAspc= (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getTipo() : null;

			oneLine.add(IDAccordoFactory.getInstance().getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc));
		} else 
			oneLine.add("");

		if(StringUtils.isNotEmpty(dettaglioPD.getPortType()))
			oneLine.add(dettaglioPD.getPortType());
		else 
			oneLine.add("");

		if(StringUtils.isNotEmpty(configurazione.getFruitore()))
			oneLine.add(configurazione.getFruitore());
		else 
			oneLine.add("");

		if(StringUtils.isNotEmpty(configurazione.getErogatore()))
			oneLine.add(configurazione.getErogatore());
		else 
			oneLine.add("");

		if(StringUtils.isNotEmpty(configurazione.getServizio()))
			oneLine.add(configurazione.getServizio());
		else 
			oneLine.add("");

		// azioni (dove viene indicata la singola azione, o le azioni o la scritta unica azione...)
		// url di invocazione
		// Identificazione Azione: ....
		// Espressione: xpath o regolare
		String endpointApplicativoPD = dettaglioPD.getEndpointApplicativoPD();
		String contesto = dettaglioPD.getContesto();
		PortaDelegataAzione pdAzione = dettaglioPD.getPortaDelegataOp2().getAzione();
		
		if(StringUtils.isNotEmpty(contesto) && !contesto.endsWith("/"))
			contesto += "/";

		if(dettaglioPD.getPortaDelegata().getNomeAzione()!=null &&
				pdAzione!=null &&
				(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_STATIC.equals(pdAzione.getIdentificazione()))){
			// Azione: _XXX
			oneLine.add(dettaglioPD.getPortaDelegata().getNomeAzione());
			// URL di Invocazione: (Endpoint Applicativo PD)/PD/SPCEnte/SPCMinistero/SPCAnagrafica
			oneLine.add(endpointApplicativoPD+"/"+contesto+"PD/"+dettaglioPD.getPortaDelegata().getNome());
			oneLine.add("");
			oneLine.add("");
		}
		else{
			List<String> azioni = dettaglioPD.getAzioni();
			
			if(pdAzione==null && (azioni == null || azioni.size() == 0)){
				oneLine.add(CostantiConfigurazioni.LABEL_UTILIZZO_DEL_SERVIZIO_SENZA_AZIONE);
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
			}
			else{
				// Azioni: XXXs
				StringBuffer sb = new StringBuffer();
				// Azioni: XXXs
				if(azioni != null && azioni.size() > 0){
					for (String azione : azioni) {
						if(sb.length()>0) sb.append("\n");
						sb.append(azione);
					}
				}
				oneLine.add(sb.toString());
				// URL di Base: (Endpoint Applicativo PD)/PD/SPCEnte/SPCMinistero/SPCAnagrafica
				oneLine.add(endpointApplicativoPD+"/"+contesto+"PD/"+dettaglioPD.getPortaDelegata().getNome());

				// Identificazione Azione:  urlBased/wsdlBased
				String suffix = "";
				if(pdAzione!= null && CostantiConfigurazione.ABILITATO.equals(pdAzione.getForceWsdlBased())){
					suffix = "/"+CostantiConfigurazione.PORTA_DELEGATA_AZIONE_WSDL_BASED.getValue();
				}
				if(pdAzione!= null){
					oneLine.add(pdAzione.getIdentificazione().getValue()+suffix);
				} else 
					oneLine.add("");

				if(pdAzione!= null && CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED.equals(pdAzione.getIdentificazione())){
					// Expressione XPath: _XXX
					oneLine.add(pdAzione.getPattern());
				}
				else {
					if(pdAzione!= null && CostantiConfigurazione.PORTA_DELEGATA_AZIONE_URL_BASED.equals(pdAzione.getIdentificazione())){
						String exprDefault = ".*"+dettaglioPD.getPortaDelegata().getNome()+"/([^/|^?]*).*";
						if(exprDefault.equals(pdAzione.getPattern())==false){
							// Expressione Regolare: _XXX
							oneLine.add(pdAzione.getPattern());
						} else 
							oneLine.add("");
					} else 
						oneLine.add("");
				}
			}
		}

		// Autenticazione (stato)
		// Autenticazione (opzionale
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(pdOp2.getAutenticazione())){
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		else{
			oneLine.add(pdOp2.getAutenticazione());
		}

		if(CostantiConfigurazione.ABILITATO.equals(pdOp2.getAutenticazioneOpzionale())){
			oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}

		// Autorizzazione (stato) Tipo: disabilitato/abilitato/xacmlPolicy/NomeCustom
		// Applicativi Autorizzati la colonna contiene l'elenco degli applicativi separati da '\n').
		// Ruoli (separati da '\n')
		// Ruoli Richiesti (all/any)
		String autorizzazione = pdOp2.getAutorizzazione();
		if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazione)){
			oneLine.add(CostantiConfigurazione.DISABILITATO.getValue());
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.XACML_POLICY.getValue().toLowerCase())){
			oneLine.add(TipoAutorizzazione.XACML_POLICY.getValue());
		}
		else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
				||
				autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			oneLine.add(CostantiConfigurazione.ABILITATO.getValue());
		}
		else{
			oneLine.add(autorizzazione);
		}

		// Se abilitato:
		// Servizi Applicativi Autorizzati: sa1 (user:xxx)
		//                                  sa2 (user:xxx)
		//                                  sa3 (user:xxx)
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			List<String> sa = dettaglioPD.getSa();
			StringBuffer sb = new StringBuffer();
			for (String servApp : sa) {
				if(sb.length()>0) sb.append("\n");
				sb.append(servApp);
			}
			oneLine.add(sb.toString());
		} else 
			oneLine.add("");

		// Ruoli: tutti/almenoUno
		// Ruoli Autorizzati: ruolo1 (fonte esterna)
		//                    ruolo2 (fonte interna)
		//                    ruolo3 (fonte qualsiasi)
		//
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())){
			List<String> ruoli = dettaglioPD.getRuoli();
			String match = dettaglioPD.getMatchRuoli();
			if(pdOp2.getRuoli()!=null){
				StringBuffer sb = new StringBuffer();

				for (String ruolo : ruoli) {
					if(sb.length()>0) sb.append("\n");
					sb.append(ruolo);
				}

				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
			oneLine.add(match);
		} else {
			oneLine.add("");
			oneLine.add("");
		}
		
		// connettore
		if(dettaglioPD.getConnettore() !=null){
			Connettore connettore = dettaglioPD.getConnettore();
			oneLine.addAll(this.printConnettore(connettore, CostantiConfigurazioni.LABEL_MODALITA_INOLTRO, null));
		} else {
			// 14 colonne vuote
			for (int i = 0; i < 14; i++) {
				oneLine.add("");
			}
		}
		

		dataSource.add(oneLine.toArray(new Object[oneLine.size()])); 
	}

	/*
	 	1 Connettore (Tipo)
	    2 Connettore (EndPoint)
	    3 Connettore (Debug)
	    4 Connettore (Username)
    	5 CONNETTORE_PROXY_ENDPOINT
		6 CONNETTORE_PROXY_USERNAME
		7 CONNETTORE_SSL_TYPE
		8 CONNETTORE_HOSTNAME_VERIFIER
		9 CONNETTORE_KEY_STORE
		10 CONNETTORE_TRUST_STORE
		11 CONNETTORE_HTTPS_KEY_STORE_LOCATION
		12 CONNETTORE_HTTPS_TRUST_STORE_LOCATION
		13 CONNETTORE_CLIENT_CERTIFICATE
		14 CONNETTORE_ALTRE_CONFIGURAZIONI
	 * */
	public  List<Object> printConnettore(Connettore connettore,String labelTipoConnettore ,InvocazioneCredenziali invCredenziali){
		List<Object> oneLine = new ArrayList<Object>();
		Map<Integer, String> mapProperties = new HashMap<Integer, String>();

		mapProperties.put(1, connettore.getTipo());

		if(TipiConnettore.HTTP.getNome().equals(connettore.getTipo()) || TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			mapProperties.put(2, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList()));

			if(invCredenziali!=null){
				mapProperties.put(4, invCredenziali.getUser());
			}
			else{
				String username = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
				if(username!=null){
					mapProperties.put(4, username);
				}
			}

			//	7 CONNETTORE_SSL_TYPE
			//	8 CONNETTORE_HOSTNAME_VERIFIER
			//	9 CONNETTORE_KEY_STORE
			//	10 CONNETTORE_TRUST_STORE
			//	11 CONNETTORE_HTTPS_KEY_STORE_LOCATION
			//	12 CONNETTORE_HTTPS_TRUST_STORE_LOCATION
			//	13 CONNETTORE_CLIENT_CERTIFICATE
			if(TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
				mapProperties.put(7, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE, connettore.getPropertyList()));
				mapProperties.put(8, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER, connettore.getPropertyList()));
				mapProperties.put(10, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE, connettore.getPropertyList()));
				mapProperties.put(12, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION, connettore.getPropertyList()));


				boolean invioCertificatoClient = false;
				String cert = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION, connettore.getPropertyList());
				if(cert!=null){
					mapProperties.put(11, cert); // 11 CONNETTORE_HTTPS_KEY_STORE_LOCATION
					invioCertificatoClient = true;
				}
				if(invioCertificatoClient){ //	9 CONNETTORE_KEY_STORE
					mapProperties.put(9, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_TYPE, connettore.getPropertyList()));
				}
				mapProperties.put(13, invioCertificatoClient +""); // 13 CONNETTORE_CLIENT_CERTIFICATE
			}

			String proxy = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE, connettore.getPropertyList());
			// 5 CONNETTORE_PROXY_ENDPOINT
			// 6 CONNETTORE_PROXY_USERNAME
			if(proxy!=null){
				mapProperties.put(5, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME, connettore.getPropertyList())+CostantiConfigurazioni.LABEL_DOTS+
						ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT, connettore.getPropertyList()));

				String username = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME, connettore.getPropertyList());
				if(username!=null){
					mapProperties.put(6, username);
				}
			}
		}
		else if(TipiConnettore.JMS.getNome().equals(connettore.getTipo())){
			mapProperties.put(2, ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList()));

			StringBuffer sb = new StringBuffer();
			sb.append(CostantiConfigurazioni.LABEL_TIPO_CODA_JMS).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_JMS_TIPO, connettore.getPropertyList())).append("\n");
			sb.append(CostantiConfigurazioni.LABEL_CONNECTION_FACTORY).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_JMS_CONNECTION_FACTORY, connettore.getPropertyList())).append("\n");
			sb.append(CostantiConfigurazioni.LABEL_SEND_AS).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_JMS_SEND_AS, connettore.getPropertyList()));
			mapProperties.put(14, sb.toString()); //14 CONNETTORE_ALTRE_CONFIGURAZIONI

			if(invCredenziali!=null){
				mapProperties.put(4, invCredenziali.getUser());
			}
			else{
				String username = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
				if(username!=null){
					mapProperties.put(4, username);
				}
			}
		}
		else if(TipiConnettore.FILE.getNome().equals(connettore.getTipo())){
			StringBuffer sb = new StringBuffer();
			sb.append(CostantiConfigurazioni.LABEL_OUTPUT_FILE).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, connettore.getPropertyList())).append("\n");
			sb.append(CostantiConfigurazioni.LABEL_OUTPUT_FILE_HEADER).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS, connettore.getPropertyList())).append("\n");
			String risposta = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_MODE, connettore.getPropertyList());
			if(risposta!=null){
				sb.append(CostantiConfigurazioni.LABEL_INPUT_FILE).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE, connettore.getPropertyList())).append("\n");
				sb.append(CostantiConfigurazioni.LABEL_INPUT_FILE_HEADER).append(": ").append(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS, connettore.getPropertyList()));
			}

			mapProperties.put(14, sb.toString()); //14 CONNETTORE_ALTRE_CONFIGURAZIONI
		}
		else if(TipiConnettore.NULL.getNome().equals(connettore.getTipo())){
			mapProperties.put(2, ConnettoreNULL.LOCATION);
		}
		else if(TipiConnettore.NULLECHO.getNome().equals(connettore.getTipo())){
			mapProperties.put(2, ConnettoreNULLEcho.LOCATION);
		}
		else{

			List<Property> list = connettore.getPropertyList();
			if(list!=null && list.size()>0){
				StringBuffer sb = new StringBuffer();
				for (Property property : list) {
					if(sb.length() > 0)
						sb.append("\n");

					sb.append(property.getNome()).append(": ").append(property.getValore());
				}

				mapProperties.put(14, sb.toString()); //14 CONNETTORE_ALTRE_CONFIGURAZIONI
			}
		}
		String debug = "false";
		if(ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_DEBUG, connettore.getPropertyList())!=null){
			debug = ConfigurazioniUtils.getProperty(CostantiConnettori.CONNETTORE_DEBUG, connettore.getPropertyList());
		}
		mapProperties.put(3, debug);

		// aggiungo le 14 proprieta previste
		for (int i = 1; i < 15; i++) {
			String valoreProprieta = mapProperties.get(i);

			if(StringUtils.isNotEmpty(valoreProprieta))
				oneLine.add(valoreProprieta);
			else 
				oneLine.add("");
		}

		return oneLine;
	}
}

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
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULL;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.IProtocolIntegrationConfiguration;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.web.monitor.core.report.Templates;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA.DettaglioSA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPD;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.utils.ConfigurazioniUtils;
import org.slf4j.Logger;

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
  		Pattern
  		force interface based

		Token (stato)
		Token (policy)
		Token (validazione input)
		Token (introspection)
		Token (user info)
		Token (forward) 
	    Autenticazione (stato)
	    Autenticazione (opzionale)
		Autenticazione Token (issuer)
		Autenticazione Token (client_id)
		Autenticazione Token (subject)
		Autenticazione Token (username)
		Autenticazione Token (email)
	    
	    Autorizzazione (stato)
	    Autorizzazione (Soggetti autorizzati)
	    Soggetti Autorizzati la colonna contiene l'elenco dei soggetti  separati da '\n').
	    Autorizzazione (Ruoli)
	    Ruoli Richiesti (all/any)
	    Ruoli (separati da '\n')
	    Autorizzazione (Scope)
	    Scope Richiesti (all/any)
	    Scope (separati da '\n')
	    Autorizzazione (Token claims)
	  
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

			this.labelColonne.add(CostantiConfigurazioni.LABEL_MODALITA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ASPC);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORT_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_FRUITORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_EROGATORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SERVIZIO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AZIONE_RISORSA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_STATO);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_IDENTIFICAZIONE_AZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PATTERN);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_FORCE_INTERFACE_BASED);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_POLICY);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_VALIDAZIONE_INPUT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_INTROSPECTION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_USER_INFO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_FORWARD);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_ISSUER);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_SUBJECT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_EMAIL); 
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_APPLICATIVI_AUTORIZZATI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_APPLICATIVI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_RUOLI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RUOLI_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RUOLI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_SCOPE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SCOPE_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SCOPE_FORNITI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_CLAIMS);
			
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
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORTA_DELEGATA);

		} else {
			// init colonne applicativa   
			/*
		Label Generali 	
		
		url di invocazione
		Identificazione Azione: ....
  		Pattern
  		force interface based
		Token (stato)
		Token (policy)
		Token (validazione input)
		Token (introspection)
		Token (user info)
		Token (forward) 
	    Autenticazione (stato)
	    Autenticazione (opzionale)
		Autenticazione Token (issuer)
		Autenticazione Token (client_id)
		Autenticazione Token (subject)
		Autenticazione Token (username)
		Autenticazione Token (email)
	    
	    Autorizzazione (stato)
	    Autorizzazione (Soggetti autorizzati)
	    Soggetti Autorizzati la colonna contiene l'elenco dei soggetti  separati da '\n').
	    Autorizzazione (Ruoli)
	    Ruoli Richiesti (all/any)
	    Ruoli (separati da '\n')
	    Autorizzazione (Scope)
	    Scope Richiesti (all/any)
	    Scope (separati da '\n')
	    Autorizzazione (Token claims)
	    
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


			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_MODALITA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_ASPC);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORT_TYPE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_EROGATORE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SERVIZIO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AZIONE_RISORSA);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_STATO);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_URL_DI_INVOCAZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_IDENTIFICAZIONE_AZIONE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PATTERN);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_FORCE_INTERFACE_BASED);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_POLICY);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_VALIDAZIONE_INPUT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_INTROSPECTION);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_USER_INFO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_TOKEN_FORWARD);

			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_OPZIONALE);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_ISSUER);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_SUBJECT);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_USERNAME);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTENTICAZIONE_TOKEN_EMAIL);
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_SOGGETTI_AUTORIZZATI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SOGGETTI_AUTORIZZATI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_RUOLI_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RUOLI_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_RUOLI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_SCOPE_STATO);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SCOPE_RICHIESTI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_SCOPE_FORNITI);
			this.labelColonne.add(CostantiConfigurazioni.LABEL_AUTORIZZAZIONE_TOKEN_CLAIMS);

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
			
			this.labelColonne.add(CostantiConfigurazioni.LABEL_PORTA_APPLICATIVA);

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

	private void popolaDataSourceExport(DRDataSource dataSource, List<ConfigurazioneGenerale> lstConfigurazioni) throws Exception  {

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

	private void addLinePA(DRDataSource dataSource, ConfigurazioneGenerale configurazione, DettaglioSA dettaglioSA) throws Exception  {
		List<Object> oneLine = new ArrayList<Object>();
		DettaglioPA dettaglioPA = configurazione.getPa();
		PortaApplicativa paOp2 = dettaglioPA.getPortaApplicativaOp2(); 
		org.openspcoop2.core.commons.search.PortaApplicativa portaApplicativa = dettaglioPA.getPortaApplicativa();
		PortaApplicativaAzione paAzione = paOp2.getAzione();
		
		// Modalita
		ServiceBinding serviceBinding= null;
		String protocollo = configurazione.getProtocollo();
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		IProtocolIntegrationConfiguration createProtocolIntegrationConfiguration = protocolFactory.createProtocolIntegrationConfiguration();
		PorteNamingUtils n = new PorteNamingUtils(protocolFactory);
		if(StringUtils.isNotEmpty(protocollo))
			oneLine.add(NamingUtils.getLabelProtocollo(protocollo));
		else 
			oneLine.add("");
		
		// ASPC
		if(dettaglioPA.getIdAccordoServizioParteComune() != null) {
			IdAccordoServizioParteComune aspc = dettaglioPA.getIdAccordoServizioParteComune();
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding2 = org.openspcoop2.core.registry.constants.ServiceBinding.toEnumConstant(aspc.getServiceBinding());
			serviceBinding = ServiceBinding.valueOf(serviceBinding2.name());
			String nomeAspc = aspc.getNome();

			Integer versioneAspc = aspc.getVersione();

			String nomeReferenteAspc = (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getNome() : null;

			String tipoReferenteAspc= (aspc.getIdSoggetto() != null) ? aspc.getIdSoggetto().getTipo() : null;
			
			oneLine.add(NamingUtils.getLabelAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc)));
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
		if(StringUtils.isNotEmpty(portaApplicativa.getNomeAzione())){  // TODO controllare lista azioni
			// Azione: _XXX
			oneLine.add(portaApplicativa.getNomeAzione());
		}
		else{
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
		
		// STATO
		if(StringUtils.isNotEmpty(configurazione.getStato()))
			oneLine.add(configurazione.getStato());
		else 
			oneLine.add("");
		
		// URL INVOCAZIONE
		String urlInvocazione = null;
		
		try {
			boolean useInterfaceNameInInvocationURL = createProtocolIntegrationConfiguration.useInterfaceNameInSubscriptionInvocationURL(serviceBinding); 
			
			String prefix = dettaglioPA.getEndpointApplicativoPA();
			prefix = prefix.trim();
			if(useInterfaceNameInInvocationURL) {
				if(prefix.endsWith("/")==false) {
					prefix = prefix + "/";
				}
			}
			
			urlInvocazione = prefix;
			if(useInterfaceNameInInvocationURL) {
				// se delegated by ci metto il nome della porta padre trattato dalle namingutils
				if(StringUtils.isNotEmpty(portaApplicativa.getNomePortaDeleganteAzione())) {
					urlInvocazione = urlInvocazione + n.enrichPA(portaApplicativa.getNomePortaDeleganteAzione());
				} else {
					urlInvocazione = urlInvocazione + configurazione.getLabel();
				}
			}
		}catch(Exception e) {
			 urlInvocazione = null;
		}
		
		if(StringUtils.isNotEmpty(urlInvocazione))
			oneLine.add(urlInvocazione);
		else 
			oneLine.add("");
		
		
		if(paAzione != null) {
			// Modalita identificazione azione
			if(paAzione.getIdentificazione() != null){
				oneLine.add(paAzione.getIdentificazione().toString());
			} else {
				oneLine.add("");
			}
			
			//Pattern
			if(StringUtils.isNotEmpty(paAzione.getPattern()))
				oneLine.add(paAzione.getPattern());
			else 
				oneLine.add("");
			
			// force interface based
			if(paAzione.getForceInterfaceBased() != null){
				oneLine.add(paAzione.getForceInterfaceBased().getValue());
			} else {
				oneLine.add("");
			}
			
		} else {
			// Modalita identificazione azione
			oneLine.add("");
			
			//Pattern
			oneLine.add("");
			
			// force interface based
			oneLine.add("");
		}
		
		// Token (stato)
		// Token (opzionale)
		// Token (policy)
		// Token (validazione input)
		// Token (introspection)
		// Token (user info)
		// Token (forward)
		GestioneToken gestioneToken = paOp2.getGestioneToken();
		GestioneTokenAutenticazione gestioneTokenAutenticazione = null;
		if(gestioneToken != null) {
			gestioneTokenAutenticazione = gestioneToken.getAutenticazione(); 
			String policy = gestioneToken.getPolicy();
			if(policy != null) {
				oneLine.add(StatoFunzionalita.ABILITATO.getValue());
				oneLine.add(gestioneToken.getTokenOpzionale().getValue());
				oneLine.add(policy);
				oneLine.add(gestioneToken.getValidazione() != null ? gestioneToken.getValidazione().getValue() : ""); 
				oneLine.add(gestioneToken.getIntrospection() != null ? gestioneToken.getIntrospection().getValue() : "");
				oneLine.add(gestioneToken.getUserInfo() != null ? gestioneToken.getUserInfo().getValue() : "");
				oneLine.add(gestioneToken.getForward() != null ? gestioneToken.getForward().getValue() : "");
				
			} else {
				// sei colonne vuote
				oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
				oneLine.add(gestioneToken.getTokenOpzionale().getValue());
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
				oneLine.add("");
			}
		} else {
			// sei colonne vuote
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
			oneLine.add("");
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
		
		// Autenticazione Token (issuer)
		// Autenticazione Token (client_id)
		// Autenticazione Token (subject)
		// Autenticazione Token (username)
		// Autenticazione Token (email)
		if(gestioneTokenAutenticazione != null) {
			oneLine.add(gestioneTokenAutenticazione.getIssuer().getValue());
			oneLine.add(gestioneTokenAutenticazione.getClientId().getValue());
			oneLine.add(gestioneTokenAutenticazione.getSubject().getValue());
			oneLine.add(gestioneTokenAutenticazione.getUsername().getValue());
			oneLine.add(gestioneTokenAutenticazione.getEmail().getValue());
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
		}

		// Autorizzazione (Stato)
		// Autorizzazione (Soggetti Autorizzati)
		// Soggetti Autorizzati
		// Autorizzazione (Ruoli Autorizzati)
		// Ruoli Richiesti
		// Ruoli Forniti
		// Autorizzazione (Scope)
		// Scope Richiesti
		// Scope Forniti
		// Autorizzazione (Token Claims)
		
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

		// Autorizzazione (Soggetti Autorizzati)
		// Se abilitato:
		// Servizi Applicativi Autorizzati: sa1 (user:xxx)
		//                                  sa2 (user:xxx)
		//                                  sa3 (user:xxx)
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
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
		}else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
		}

		// Autorizzazione (Ruoli Autorizzati)
		// Ruoli Richiesti: tutti/almenoUno
		// Ruoli Autorizzati: ruolo1 (fonte esterna)
		//                    ruolo2 (fonte interna)
		//                    ruolo3 (fonte qualsiasi)
		//
		if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())){
			oneLine.add(StatoFunzionalita.ABILITATO.getValue());
			
			List<String> ruoli = dettaglioPA.getRuoli();
			String match = dettaglioPA.getMatchRuoli();
			oneLine.add(match);
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
			
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}
		
		// Autorizzazione (Scope)
		// Scope Richiesti
		// Scope Forniti
		AutorizzazioneScope scope = paOp2.getScope();
		if(scope != null) {
			oneLine.add(scope.getStato().getValue());
			oneLine.add(scope.getMatch().getValue());
			
			if(scope.getScopeList()!=null){
				StringBuffer sb = new StringBuffer();

				for (Scope scopeS : scope.getScopeList()) {
					if(sb.length()>0) sb.append("\n");
					sb.append(scopeS.getNome());
				}

				oneLine.add(sb.toString());
			} else {
				oneLine.add("");
			}
			
		} else {
			oneLine.add(StatoFunzionalita.DISABILITATO.getValue());
			oneLine.add("");
			oneLine.add("");
		}

		// Autorizzazione (Token Claims)
		
		if(gestioneToken != null) {
			if(StringUtils.isNotEmpty(gestioneToken.getOptions()))
				oneLine.add(gestioneToken.getOptions());
			else 
				oneLine.add("");
		}else {
			oneLine.add("");
		}
		

		// colonne servizio applicativo e relativo connettore
		if(dettaglioSA != null) {
			ServizioApplicativo saOp2 = dettaglioSA.getSaOp2();
			// colonne del servizio applicativo NOME | MESSAGE BOX | SBUSTAMENTO SOAP | SBUSTAMENTO PROTOCOLLO
//			if(StringUtils.isNotEmpty(saOp2.getNome()))
//				oneLine.add(saOp2.getNome());
//			else 
//				oneLine.add("");

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
		
		// NOME PA
		if(StringUtils.isNotEmpty(portaApplicativa.getNome()))
			oneLine.add(portaApplicativa.getNome());
		else 
			oneLine.add("");

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

			Integer versioneAspc = aspc.getVersione();

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
				if(pdAzione!= null && CostantiConfigurazione.ABILITATO.equals(pdAzione.getForceInterfaceBased())){
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

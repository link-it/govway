/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.TrasformazioneSoap;
import org.openspcoop2.core.config.TrasformazioneSoapRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.driver.AccordoServizioUtils;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XSDSchemaCollection;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.PageData;
import org.slf4j.Logger;

/**
 * Questa servlet si occupa di esportare le tracce diagnostiche in formato xml
 * zippandole
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DocumentoExporter extends HttpServlet {

	private static final long serialVersionUID = -7341279067126334095L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	/**
	 * Processa la richiesta pervenuta e si occupa di fornire i dati richiesti
	 * in formato zip
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		ArchiviHelper archiviHelper = null;
		try {
			archiviHelper = new ArchiviHelper(request, pd, session);

			ControlStationCore.logDebug("Ricevuta Richiesta di esportazione...");
			Enumeration<?> en = archiviHelper.getParameterNames();
			ControlStationCore.logDebug("Parametri (nome = valore):\n-----------------");
			while (en.hasMoreElements()) {
				String param = (String) en.nextElement();
				String value = archiviHelper.getParameter(param);
				ControlStationCore.logDebug(param + " = " + value);
			}
			ControlStationCore.logDebug("-----------------");
		}catch(Exception e){
			throw new ServletException(e.getMessage(),e);
		}

		byte[] docBytes = null;
		String fileName = null;

		try {
			String idAllegato = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ALLEGATO);
			String idAccordo = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO);
			String tipoDocumento = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO);
			String tipoDocumentoDaScaricare = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO);
			if(tipoDocumentoDaScaricare==null || "".equals(tipoDocumentoDaScaricare)){
				tipoDocumentoDaScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_DOCUMENTO;
			}

			String tipoSoggettoFruitore = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_TIPO_SOGGETTO_FRUITORE);
			String nomeSoggettoFruitore = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_NOME_SOGGETTO_FRUITORE);

			int idAccordoInt = 0 ;
			try{ idAccordoInt = Integer.parseInt(idAccordo); }catch(Exception e){ idAccordoInt = 0 ; }

			ArchiviCore archiviCore = new ArchiviCore();
			ProtocolPropertiesCore ppCore = new ProtocolPropertiesCore(archiviCore);
			PorteDelegateCore pdCore = new PorteDelegateCore(archiviCore);
			PorteApplicativeCore paCore = new PorteApplicativeCore(archiviCore);

			if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_DOCUMENTO.equals(tipoDocumentoDaScaricare) ){

				int idAllegatoInt = Integer.parseInt(idAllegato);

				if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoDocumento)){
					Documento doc = archiviCore.getDocumento(idAllegatoInt,true);
					fileName = doc.getFile();
					docBytes = doc.getByteContenuto();
				}
				else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA.equals(tipoDocumento)){
					Documento doc = archiviCore.getDocumento(idAllegatoInt,true);
					fileName = doc.getFile();
					docBytes = doc.getByteContenuto();
				}
				else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_COOPERAZIONE.equals(tipoDocumento)){
					Documento doc = archiviCore.getDocumento(idAllegatoInt,true);
					fileName = doc.getFile();
					docBytes = doc.getByteContenuto();
				}
				else{
					throw new ServletException("Tipo archivio ["+tipoDocumento+"] non gestito (tipo documento: "+tipoDocumentoDaScaricare+")");
				}

			}
			else {

				if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoDocumento)){

					AccordiServizioParteComuneCore asCore = new AccordiServizioParteComuneCore(archiviCore);
					AccordoServizioParteComune as = asCore.getAccordoServizioFull(idAccordoInt);

					if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_DEFINITORIO.equals(tipoDocumentoDaScaricare) ){
						fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_INTERFACCIA_DEFINITORIA;
						docBytes = as.getByteWsdlDefinitorio();
					}
					else if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_CONCETTUALE.equals(tipoDocumentoDaScaricare) ){
						docBytes = as.getByteWsdlConcettuale();
						if(as.getFormatoSpecifica()!=null) {
							switch (as.getFormatoSpecifica()) {
							case WSDL_11:
								fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_CONCETTUALE_WSDL;
								break;
							case OPEN_API_3:
								YAMLUtils yamlUtils = YAMLUtils.getInstance();
								if(yamlUtils.isYaml(docBytes)) {
									fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_OPENAPI_3_0_YAML;
								}
								else {	
									fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_OPENAPI_3_0_JSON;
								}
								break;
							case SWAGGER_2:
								yamlUtils = YAMLUtils.getInstance();
								if(yamlUtils.isYaml(docBytes)) {
									fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SWAGGER_2_0_YAML;
								}
								else {
									fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SWAGGER_2_0_JSON;
								}
								break;
							case WADL:
								fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WADL;
								break;
							}
						}
						else {
							fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_CONCETTUALE_WSDL;
						}
					}
					else if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_EROGATORE.equals(tipoDocumentoDaScaricare) ){
						fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_EROGATORE_WSDL;
						docBytes = as.getByteWsdlLogicoErogatore();
					}
					else if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_FRUITORE.equals(tipoDocumentoDaScaricare) ){
						fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_FRUITORE_WSDL;
						docBytes = as.getByteWsdlLogicoFruitore();
					}
					else if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_CONCETTUALE.equals(tipoDocumentoDaScaricare) ){
						fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_CONCETTUALE;
						docBytes = as.getByteSpecificaConversazioneConcettuale();
					}
					else if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_EROGATORE.equals(tipoDocumentoDaScaricare) ){
						fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_EROGATORE;
						docBytes = as.getByteSpecificaConversazioneErogatore();
					}
					else if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_FRUITORE.equals(tipoDocumentoDaScaricare) ){
						fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_FRUITORE;
						docBytes = as.getByteSpecificaConversazioneFruitore();
					}
					else if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_XSD_SCHEMA_COLLECTION.equals(tipoDocumentoDaScaricare) ){

						AccordoServizioParteComune asConAllegati = asCore.getAccordoServizioFull(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), true);
						try{
							AccordoServizioUtils asUtils = new AccordoServizioUtils(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), ControlStationCore.getLog());
							XSDSchemaCollection schemaCollection = asUtils.buildSchemaCollection(asConAllegati, true);
							fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_XSD_SCHEMA_COLLECTION;
							//docBytes = schemaCollection.serialize(ControlStationCore.getLog());
							docBytes = this.serializeWsdl(ControlStationCore.getLog(), schemaCollection, asConAllegati);
						}
						catch(Throwable e){
							ControlStationCore.getLog().error("XSDSchemaCollection build error: "+e.getMessage(),e);
							fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_XSD_SCHEMA_COLLECTION_ERROR;
							String msg = e.getMessage();
							if(msg==null || msg.equals("")){
								if(e instanceof NullPointerException){
									msg = "Internal Error (NP)";
								}
								else{
									msg = e.toString();
									if(msg==null || msg.equals("")){
										msg = "Internal Error";
									}
								}
							}
							docBytes = msg.getBytes();
						}

					}
					else{
						throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non gestito per il tipo archivio ["+tipoDocumento+"]");
					}

				}
				else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA.equals(tipoDocumento)){

					AccordiServizioParteSpecificaCore asCore = new AccordiServizioParteSpecificaCore(archiviCore);
					AccordoServizioParteSpecifica as = asCore.getAccordoServizioParteSpecifica(idAccordoInt);

					if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_EROGATORE.equals(tipoDocumentoDaScaricare) ){
						fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_EROGATORE_WSDL;
						if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
								nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)){
							for (int i = 0; i < as.sizeFruitoreList(); i++) {
								Fruitore f = as.getFruitore(i);
								if(tipoSoggettoFruitore.equals(f.getTipo()) && nomeSoggettoFruitore.equals(f.getNome())){
									docBytes = f.getByteWsdlImplementativoErogatore();
									break;
								}
							}
						}else{
							docBytes = as.getByteWsdlImplementativoErogatore();
						}
					}
					else if( ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_FRUITORE.equals(tipoDocumentoDaScaricare) ){
						fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_FRUITORE_WSDL;
						if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
								nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)){
							for (int i = 0; i < as.sizeFruitoreList(); i++) {
								Fruitore f = as.getFruitore(i);
								if(tipoSoggettoFruitore.equals(f.getTipo()) && nomeSoggettoFruitore.equals(f.getNome())){
									docBytes = f.getByteWsdlImplementativoFruitore();
									break;
								}
							}
						}else{
							docBytes = as.getByteWsdlImplementativoFruitore();
						}
					}
					else{
						throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non gestito per il tipo archivio ["+tipoDocumento+"]");
					}

				}
				else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PROTOCOL_PROPERTY.equals(tipoDocumento)){
					if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PROTOCOL_PROPERTY_BINARY.equals(tipoDocumentoDaScaricare)){
						int idAllegatoInt = Integer.parseInt(idAllegato);
						ProtocolProperty bp = ppCore.getProtocolPropertyBinaria(idAllegatoInt);
						fileName = bp.getFile();
						docBytes = bp.getByteFile();
					}else{
						throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non gestito per il tipo archivio ["+tipoDocumento+"]");
					}
				}
				else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PORTA_APPLICATIVA.equals(tipoDocumento)){
					if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_XACML_POLICY.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_XACML_POLICY_FILENAME;
						
						String policy = portaApplicativa.getXacmlPolicy();

						if(StringUtils.isEmpty(policy)) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						docBytes = policy.getBytes();
					}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE_FILENAME;
						
						String idTrasformazioneRegola = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID);
						long idTrasformazione = Long.parseLong(idTrasformazioneRegola);
						
						Trasformazioni trasformazioni = portaApplicativa.getTrasformazioni();
						TrasformazioneRegola oldRegola = null;
						for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
							if(reg.getId().longValue() == idTrasformazione) {
								oldRegola = reg;
								break;
							}
						}

						if(oldRegola == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						TrasformazioneRegolaRichiesta richiesta = oldRegola.getRichiesta();
					
						if(richiesta == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						if(richiesta.getConversioneTemplate() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione tipo = StringUtils.isNotEmpty(richiesta.getConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(richiesta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;

						switch(tipo) {
						case FREEMARKER_TEMPLATE:
						case CONTEXT_FREEMARKER_TEMPLATE:
						case FREEMARKER_TEMPLATE_ZIP:
						case VELOCITY_TEMPLATE:
						case CONTEXT_VELOCITY_TEMPLATE:
						case VELOCITY_TEMPLATE_ZIP:
						case TEMPLATE:
						case XSLT:
						case ZIP:
						case TGZ:
						case TAR:
							fileName += tipo.getExt();
							break;
						case EMPTY:
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] '"+tipo+"' non disponibile per il tipo archivio ["+tipoDocumento+"]: tipo non supportato");
						}
						
//						if(fileName==null) {
//							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] '"+tipo+"' non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
//						}

						docBytes = richiesta.getConversioneTemplate();
					}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE_FILENAME;
						
						String idTrasformazioneRegola = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID);
						long idTrasformazione = Long.parseLong(idTrasformazioneRegola);
						
						Trasformazioni trasformazioni = portaApplicativa.getTrasformazioni();
						TrasformazioneRegola oldRegola = null;
						for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
							if(reg.getId().longValue() == idTrasformazione) {
								oldRegola = reg;
								break;
							}
						}

						if(oldRegola == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						TrasformazioneRegolaRichiesta richiesta = oldRegola.getRichiesta();
					
						if(richiesta == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						if(richiesta.getTrasformazioneSoap() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						TrasformazioneSoap trasformazioneSoap = richiesta.getTrasformazioneSoap();
						
						if( trasformazioneSoap.getEnvelopeBodyConversioneTemplate() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione tipo = StringUtils.isNotEmpty(trasformazioneSoap.getEnvelopeBodyConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneSoap.getEnvelopeBodyConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;

						switch(tipo) {
						case FREEMARKER_TEMPLATE:
						case CONTEXT_FREEMARKER_TEMPLATE:
						case FREEMARKER_TEMPLATE_ZIP:
						case VELOCITY_TEMPLATE:
						case CONTEXT_VELOCITY_TEMPLATE:
						case VELOCITY_TEMPLATE_ZIP:
						case TEMPLATE:
						case XSLT:
						case ZIP:
						case TGZ:
						case TAR:
							fileName += tipo.getExt();
							break;
						case EMPTY:
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						docBytes = trasformazioneSoap.getEnvelopeBodyConversioneTemplate();
					}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE_FILENAME;

						String idTrasformazioneRegola = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID);
						long idTrasformazione = Long.parseLong(idTrasformazioneRegola);
						
						String idTrasformazioneRegolaRes = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RES_ID);
						long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRegolaRes);
						
						Trasformazioni trasformazioni = portaApplicativa.getTrasformazioni();
						TrasformazioneRegola oldRegola = null;
						for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
							if(reg.getId().longValue() == idTrasformazione) {
								oldRegola = reg;
								break;
							}
						}

						if(oldRegola == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						TrasformazioneRegolaRisposta oldRisposta = null;
						for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
							TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
							if (risposta.getId().longValue() == idTrasformazioneRisposta) {
								oldRisposta = risposta;
								break;
							}
						}
						
						if(oldRisposta == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						if(oldRisposta.getConversioneTemplate() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione tipo = StringUtils.isNotEmpty(oldRisposta.getConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(oldRisposta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;

						switch(tipo) {
						case FREEMARKER_TEMPLATE:
						case CONTEXT_FREEMARKER_TEMPLATE:
						case FREEMARKER_TEMPLATE_ZIP:
						case VELOCITY_TEMPLATE:
						case CONTEXT_VELOCITY_TEMPLATE:
						case VELOCITY_TEMPLATE_ZIP:
						case TEMPLATE:
						case XSLT:
						case ZIP:
						case TGZ:
						case TAR:
							fileName += tipo.getExt();
							break;
						case EMPTY:
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
								
						docBytes = oldRisposta.getConversioneTemplate();
					}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE_FILENAME;
						
						String idTrasformazioneRegola = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID);
						long idTrasformazione = Long.parseLong(idTrasformazioneRegola);
						
						String idTrasformazioneRegolaRes = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RES_ID);
						long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRegolaRes);

						Trasformazioni trasformazioni = portaApplicativa.getTrasformazioni();
						TrasformazioneRegola oldRegola = null;
						for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
							if(reg.getId().longValue() == idTrasformazione) {
								oldRegola = reg;
								break;
							}
						}

						if(oldRegola == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						TrasformazioneRegolaRisposta oldRisposta = null;
						for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
							TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
							if (risposta.getId().longValue() == idTrasformazioneRisposta) {
								oldRisposta = risposta;
								break;
							}
						}
						
						if(oldRisposta == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						if(oldRisposta.getTrasformazioneSoap() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						TrasformazioneSoapRisposta trasformazioneSoap = oldRisposta.getTrasformazioneSoap();
						
						if( trasformazioneSoap.getEnvelopeBodyConversioneTemplate() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione tipo = StringUtils.isNotEmpty(trasformazioneSoap.getEnvelopeBodyConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneSoap.getEnvelopeBodyConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;

						switch(tipo) {
						case FREEMARKER_TEMPLATE:
						case CONTEXT_FREEMARKER_TEMPLATE:
						case FREEMARKER_TEMPLATE_ZIP:
						case VELOCITY_TEMPLATE:
						case CONTEXT_VELOCITY_TEMPLATE:
						case VELOCITY_TEMPLATE_ZIP:
						case TEMPLATE:
						case XSLT:
						case ZIP:
						case TGZ:
						case TAR:
							fileName += tipo.getExt();
							break;
						case EMPTY:
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						docBytes = trasformazioneSoap.getEnvelopeBodyConversioneTemplate();
					}else {
						throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non gestito per il tipo archivio ["+tipoDocumento+"]");
					}
				}
				else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_PORTA_DELEGATA.equals(tipoDocumento)){
					if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_XACML_POLICY.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_XACML_POLICY_FILENAME;

						String policy = portaDelegata.getXacmlPolicy();

						if(StringUtils.isEmpty(policy)) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						docBytes = policy.getBytes();
					}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE_FILENAME;

						String idTrasformazioneRegola = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID);
						long idTrasformazione = Long.parseLong(idTrasformazioneRegola);
						
						Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
						TrasformazioneRegola oldRegola = null;
						for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
							if(reg.getId().longValue() == idTrasformazione) {
								oldRegola = reg;
								break;
							}
						}

						if(oldRegola == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						TrasformazioneRegolaRichiesta richiesta = oldRegola.getRichiesta();
					
						if(richiesta == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						if(richiesta.getConversioneTemplate() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione tipo = StringUtils.isNotEmpty(richiesta.getConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(richiesta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;

						switch(tipo) {
						case FREEMARKER_TEMPLATE:
						case CONTEXT_FREEMARKER_TEMPLATE:
						case FREEMARKER_TEMPLATE_ZIP:
						case VELOCITY_TEMPLATE:
						case CONTEXT_VELOCITY_TEMPLATE:
						case VELOCITY_TEMPLATE_ZIP:
						case TEMPLATE:
						case XSLT:
						case ZIP:
						case TGZ:
						case TAR:
							fileName += tipo.getExt();
							break;
						case EMPTY:
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
								
						docBytes = richiesta.getConversioneTemplate();
					}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE_FILENAME;
						
						String idTrasformazioneRegola = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID);
						long idTrasformazione = Long.parseLong(idTrasformazioneRegola);

						Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
						TrasformazioneRegola oldRegola = null;
						for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
							if(reg.getId().longValue() == idTrasformazione) {
								oldRegola = reg;
								break;
							}
						}

						if(oldRegola == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						TrasformazioneRegolaRichiesta richiesta = oldRegola.getRichiesta();
					
						if(richiesta == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						if(richiesta.getTrasformazioneSoap() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						TrasformazioneSoap trasformazioneSoap = richiesta.getTrasformazioneSoap();
						
						if( trasformazioneSoap.getEnvelopeBodyConversioneTemplate() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione tipo = StringUtils.isNotEmpty(trasformazioneSoap.getEnvelopeBodyConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneSoap.getEnvelopeBodyConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;

						switch(tipo) {
						case FREEMARKER_TEMPLATE:
						case CONTEXT_FREEMARKER_TEMPLATE:
						case FREEMARKER_TEMPLATE_ZIP:
						case VELOCITY_TEMPLATE:
						case CONTEXT_VELOCITY_TEMPLATE:
						case VELOCITY_TEMPLATE_ZIP:
						case TEMPLATE:
						case XSLT:
						case ZIP:
						case TGZ:
						case TAR:
							fileName += tipo.getExt();
							break;
						case EMPTY:
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						docBytes = trasformazioneSoap.getEnvelopeBodyConversioneTemplate();
					}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE_FILENAME;

						String idTrasformazioneRegola = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID);
						long idTrasformazione = Long.parseLong(idTrasformazioneRegola);
						
						String idTrasformazioneRegolaRes = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RES_ID);
						long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRegolaRes);
						
						Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
						TrasformazioneRegola oldRegola = null;
						for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
							if(reg.getId().longValue() == idTrasformazione) {
								oldRegola = reg;
								break;
							}
						}

						if(oldRegola == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						TrasformazioneRegolaRisposta oldRisposta = null;
						for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
							TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
							if (risposta.getId().longValue() == idTrasformazioneRisposta) {
								oldRisposta = risposta;
								break;
							}
						}
						
						if(oldRisposta == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						if(oldRisposta.getConversioneTemplate() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione tipo = StringUtils.isNotEmpty(oldRisposta.getConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(oldRisposta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;

						switch(tipo) {
						case FREEMARKER_TEMPLATE:
						case CONTEXT_FREEMARKER_TEMPLATE:
						case FREEMARKER_TEMPLATE_ZIP:
						case VELOCITY_TEMPLATE:
						case CONTEXT_VELOCITY_TEMPLATE:
						case VELOCITY_TEMPLATE_ZIP:
						case TEMPLATE:
						case XSLT:
						case ZIP:
						case TGZ:
						case TAR:
							fileName += tipo.getExt();
							break;
						case EMPTY:
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
								
						docBytes = oldRisposta.getConversioneTemplate();
					}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE.equals(tipoDocumentoDaScaricare)){
						Long idPorta = Long.parseLong(idAccordo);
						PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPorta);
						fileName = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE_FILENAME;
						
						String idTrasformazioneRegola = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_ID);
						long idTrasformazione = Long.parseLong(idTrasformazioneRegola);
						
						String idTrasformazioneRegolaRes = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RES_ID);
						long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRegolaRes);

						Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
						TrasformazioneRegola oldRegola = null;
						for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
							if(reg.getId().longValue() == idTrasformazione) {
								oldRegola = reg;
								break;
							}
						}

						if(oldRegola == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						TrasformazioneRegolaRisposta oldRisposta = null;
						for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
							TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
							if (risposta.getId().longValue() == idTrasformazioneRisposta) {
								oldRisposta = risposta;
								break;
							}
						}
						
						if(oldRisposta == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						if(oldRisposta.getTrasformazioneSoap() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						TrasformazioneSoapRisposta trasformazioneSoap = oldRisposta.getTrasformazioneSoap();
						
						if( trasformazioneSoap.getEnvelopeBodyConversioneTemplate() == null) {
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}
						
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione tipo = StringUtils.isNotEmpty(trasformazioneSoap.getEnvelopeBodyConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneSoap.getEnvelopeBodyConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;

						switch(tipo) {
						case FREEMARKER_TEMPLATE:
						case CONTEXT_FREEMARKER_TEMPLATE:
						case FREEMARKER_TEMPLATE_ZIP:
						case VELOCITY_TEMPLATE:
						case CONTEXT_VELOCITY_TEMPLATE:
						case VELOCITY_TEMPLATE_ZIP:
						case TEMPLATE:
						case XSLT:
						case ZIP:
						case TGZ:
						case TAR:
							fileName += tipo.getExt();
							break;
						case EMPTY:
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non disponibile per il tipo archivio ["+tipoDocumento+"]: contenuto vuoto o non presente");
						}

						docBytes = trasformazioneSoap.getEnvelopeBodyConversioneTemplate();
					}else {
						throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non gestito per il tipo archivio ["+tipoDocumento+"]");
					}
				}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_SOGGETTO.equals(tipoDocumento)){
					if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_CERTIFICATO_SSL.equals(tipoDocumentoDaScaricare)){
						Long idSogg = Long.parseLong(idAccordo);
						SoggettiCore soggettiCore = new SoggettiCore(archiviCore);

						String oldnomeprov = null;
						String oldtipoprov = null;
						if(soggettiCore.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto soggettoRegistry = soggettiCore.getSoggettoRegistro(idSogg); 
							oldnomeprov = soggettoRegistry.getNome();
							oldtipoprov = soggettoRegistry.getTipo();
						}
						else{
							Soggetto soggettoConfig = soggettiCore.getSoggetto(idSogg); 
							oldnomeprov = soggettoConfig.getNome();
							oldtipoprov = soggettoConfig.getTipo();
						}

						IDSoggetto idSoggetto = new IDSoggetto(oldtipoprov,oldnomeprov);
						String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(oldtipoprov);
						IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
						IRegistryReader registryReader = soggettiCore.getRegistryReader(protocolFactory);

						CredenzialiSoggetto credenziali = null;
						org.openspcoop2.core.registry.Soggetto soggetto = null;
						try{
							soggetto =  registryReader.getSoggetto(idSoggetto);
							credenziali = soggetto.getCredenziali();
						}catch(RegistryNotFound r){
							throw r;
						}
						
						fileName = soggetto.getNome() + ".crt";
						
						if(credenziali != null && credenziali.getTipo() != null && credenziali.getTipo().equals(CredenzialeTipo.SSL)) {
							if(credenziali.getCertificate() != null && credenziali.getCertificate().length > 0) {
								docBytes = credenziali.getCertificate();
							} else {
								throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non definito per il tipo archivio ["+tipoDocumento+"]");
							}
						} else 
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non definito per il tipo archivio ["+tipoDocumento+"]");
					}else{
						throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non gestito per il tipo archivio ["+tipoDocumento+"]");
					}
				}else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_SERVIZIO_APPLICATIVO.equals(tipoDocumento)){
					if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_CERTIFICATO_SSL.equals(tipoDocumentoDaScaricare)){
						Long idSA = Long.parseLong(idAccordo);
						ServiziApplicativiCore saCore = new ServiziApplicativiCore(archiviCore);
						ServizioApplicativo sa = saCore.getServizioApplicativo(idSA);
						
						fileName = sa.getNome() + ".crt";
						
						InvocazionePorta ip = sa.getInvocazionePorta();
						Credenziali credenziali = null;
						if (ip != null) {
							if(ip.sizeCredenzialiList()>0) {
								credenziali = ip.getCredenziali(0);
							}
						}
						
						if(credenziali != null && credenziali.getTipo() != null && credenziali.getTipo().equals(org.openspcoop2.core.config.constants.CredenzialeTipo.SSL)) {
							if(credenziali.getCertificate() != null && credenziali.getCertificate().length > 0) {
								docBytes = credenziali.getCertificate();
							} else {
								throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non definito per il tipo archivio ["+tipoDocumento+"]");
							}
						} else 
							throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non definito per il tipo archivio ["+tipoDocumento+"]");
					}else{
						throw new ServletException("Tipo documento ["+tipoDocumentoDaScaricare+"] non gestito per il tipo archivio ["+tipoDocumento+"]");
					}
				}
				else{
					throw new ServletException("Tipo archivio ["+tipoDocumento+"] non gestito (tipo documento: "+tipoDocumentoDaScaricare+")");
				}

			} 

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);

			OutputStream out = response.getOutputStream();	
			out.write(docBytes);
			out.flush();
			out.close();

		} catch (Exception e) {
			ControlStationCore.logError("Errore durante il download dei documenti "+e.getMessage(), e);
			throw new ServletException(e);
		} 
	}


	private byte[] serializeWsdl(Logger log,XSDSchemaCollection schemaCollection, AccordoServizioParteComune asConAllegati) throws XMLException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			serializeWsdl(log,bout,schemaCollection,asConAllegati);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	private void serializeWsdl(Logger log,OutputStream out,XSDSchemaCollection schemaCollection, AccordoServizioParteComune asConAllegati) throws XMLException{

		ZipOutputStream zipOut = null;
		try{
			zipOut = new ZipOutputStream(out);

			schemaCollection.zipSerialize(log, zipOut);

			String rootPackageDir = "wsdl"+File.separatorChar;

			if(asConAllegati.getByteWsdlLogicoErogatore()!=null){
				String nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_EROGATORE_WSDL;
				this.writeWsdl(log, rootPackageDir+nomeFile, zipOut, true, asConAllegati);
			}

			if(asConAllegati.getByteWsdlLogicoFruitore()!=null){
				String nomeFile = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_FRUITORE_WSDL;
				this.writeWsdl(log, rootPackageDir+nomeFile, zipOut, false, asConAllegati);
			}

			zipOut.flush();

		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){}
		}
	}
	private void writeWsdl(Logger log,String nomeFile, ZipOutputStream zipOut, boolean erogatore, AccordoServizioParteComune asConAllegati) throws IOException{
		try{
			AccordoServizioWrapperUtilities wsdlWrapperUtilities = new AccordoServizioWrapperUtilities(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log);
			wsdlWrapperUtilities.setAccordoServizio(new AccordoServizioWrapper());
			wsdlWrapperUtilities.getAccordoServizioWrapper().setAccordoServizio((AccordoServizioParteComune)asConAllegati.clone());
			WSDLUtilities wsdlUtilities = new WSDLUtilities(XMLUtils.DEFAULT);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			javax.wsdl.Definition wsdl = null;
			if(erogatore){
				wsdl = wsdlWrapperUtilities.buildWsdlErogatoreFromBytes();
			}
			else{
				wsdl = wsdlWrapperUtilities.buildWsdlFruitoreFromBytes();
			}
			wsdlUtilities.writeWsdlTo(wsdl, bout);
			bout.flush();
			bout.close();

			zipOut.putNextEntry(new ZipEntry(nomeFile));
			zipOut.write(bout.toByteArray());
		}catch(Throwable e){
			String tipo = "Erogatore";
			if(!erogatore){
				tipo = "Fruitore";
			}
			log.error("Costruzione WSDL "+tipo+" fallita: "+e.getMessage(),e);
			nomeFile = nomeFile + ".buildFailed.txt";
			zipOut.putNextEntry(new ZipEntry(nomeFile));
			String msg = e.getMessage();
			if(msg==null || msg.equals("")){
				if(e instanceof NullPointerException){
					msg = "Internal Error (NP)";
				}
				else{
					msg = e.toString();
					if(msg==null || msg.equals("")){
						msg = "Internal Error";
					}
				}
			}
			zipOut.write(msg.getBytes());
		}
	}

}

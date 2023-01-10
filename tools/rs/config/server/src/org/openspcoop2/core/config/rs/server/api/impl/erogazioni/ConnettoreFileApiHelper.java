/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreFile;
import org.openspcoop2.core.config.rs.server.model.ConnettoreFileRichiesta;
import org.openspcoop2.core.config.rs.server.model.ConnettoreFileRisposta;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConnettoreFileApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConnettoreFileApiHelper extends AbstractConnettoreApiHelper<ConnettoreFile> {

	@Override
	protected ConnettoreFile getConnettore(OneOfConnettoreErogazioneConnettore conn) throws Exception {
		return (ConnettoreFile) conn;
	}

	@Override
	protected ConnettoreFile getConnettore(OneOfConnettoreFruizioneConnettore conn) throws Exception {
		return (ConnettoreFile) conn;
	}

	@Override
	protected ConnettoreFile getConnettore(OneOfApplicativoServerConnettore conn) throws Exception {
		return (ConnettoreFile) conn;
	}

	@Override
	public boolean connettoreCheckData(ConnettoreFile conn, ErogazioniEnv env, boolean erogazione) throws Exception {


	    final String endpointtype = TipiConnettore.FILE.getNome();
	    
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
						null, false, endpointtype);

		String responseInputMode = conn.getRisposta() != null ? StatoFunzionalita.ABILITATO.toString() : StatoFunzionalita.DISABILITATO.toString();
		
		String responseInputFileName = null;
		String responseInputFileNameHeaders = null;
		boolean responseInputDeleteAfterRead = false;
		String responseInputWaitTime = null;
		
		if(conn.getRisposta() != null) {
			responseInputFileName = conn.getRisposta().getFile();
			responseInputFileNameHeaders = conn.getRisposta().getFileHeaders();
			responseInputDeleteAfterRead = conn.getRisposta().isDeleteAfterRead();
			responseInputWaitTime = conn.getRisposta().getWaitIfNotExistsMs() != null ? conn.getRisposta().getWaitIfNotExistsMs() + "" : null;
		}
		
		String createParentDir = conn.getRichiesta().isCreateParentDir() != null ? ServletUtils.boolToCheckBoxStatus(conn.getRichiesta().isCreateParentDir()) : ServletUtils.boolToCheckBoxStatus(false);
		String overwriteIfExists = conn.getRichiesta().isOverwriteIfExists() != null ? ServletUtils.boolToCheckBoxStatus(conn.getRichiesta().isOverwriteIfExists()) : ServletUtils.boolToCheckBoxStatus(false);
		
		return env.saHelper.endPointCheckData(
				env.tipo_protocollo,
				erogazione,
				endpointtype,
				null,
				null,	// nome
				null,	// tipo
				null,	// httpConf.getUsername()
				null,	// httpConf.getPassword()
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null,	// provurl jms,
				null, 	// connfact, 
				null,	// sendas, 
				null, 													// this.httpsurl,
				null,	// httpsConf.getTipologia().toString(), 
				false,		// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,	// this.httpspath
				null,	// this.httpstipo,
				null,	// this.httpspwd,
				null,	// this.httpsalgoritmo
				false, 
				null,
				"",																		// httpspwdprivatekeytrust, 
				null,	// pathkey
				null,	// this.httpstipokey
				null,	// this.httpspwdkey 
				null,	// this.httpspwdprivatekey,  
				null,	// this.httpsalgoritmokey, 
				null,	// httpsKeyAlias
				null,	// httpsTrustStoreCRLs
				null,																//	tipoconn (personalizzato)
				ServletUtils.boolToCheckBoxStatus(false),										 	//autenticazioneHttp,
				ServletUtils.boolToCheckBoxStatus(false),	
				null,	// evalnull( () -> proxy.getHostname() ),
				null,	// evalnull( () -> proxy.getPorta().toString() ),
				null,	// evalnull( () -> proxy.getUsername() ),
				null,	// evalnull( () -> proxy.getPassword() ),
				ServletUtils.boolToCheckBoxStatus(false), //ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				null,	// evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				null,	// evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				null,	// evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				conn.getRichiesta().getFile(),	// this.requestOutputFileName,
				conn.getRichiesta().getFilePermissions(),   // this.requestOutputFileName_permissions
				conn.getRichiesta().getFileHeaders(),	// this.requestOutputFileNameHeaders,
				conn.getRichiesta().getFileHeadersPermissions(),   // this.requestOutputFileNameHeaders_permissions
				createParentDir,
				overwriteIfExists,
				responseInputMode,	// this.responseInputMode, 
				responseInputFileName,	// this.responseInputFileName, 
				responseInputFileNameHeaders,	// this.responseInputFileNameHeaders, 
				ServletUtils.boolToCheckBoxStatus(responseInputDeleteAfterRead),	// this.responseInputDeleteAfterRead, 
				responseInputWaitTime,	// this.responseInputWaitTime,
				false,
				null,
				listExtendedConnettore,
        		false, // erogazioneServizioApplicativoServerEnabled, TODO quando si aggiunge applicativo server
    			null // rogazioneServizioApplicativoServer
			);

	}

	@Override
	public Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,
			ErogazioniEnv env,
			ConnettoreFile conn,
			String oldConnT) throws Exception {
	    final String endpointtype = TipiConnettore.FILE.getNome();
	    
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
						null, false, endpointtype);

		String responseInputMode = conn.getRisposta() != null ? StatoFunzionalita.ABILITATO.toString() : StatoFunzionalita.DISABILITATO.toString();
		
		String responseInputFileName = null;
		String responseInputFileNameHeaders = null;
		boolean responseInputDeleteAfterRead = false;
		String responseInputWaitTime = null;
		
		if(conn.getRisposta() != null) {
			responseInputFileName = conn.getRisposta().getFile();
			responseInputFileNameHeaders = conn.getRisposta().getFileHeaders();
			responseInputDeleteAfterRead = conn.getRisposta().isDeleteAfterRead();
			responseInputWaitTime = conn.getRisposta().getWaitIfNotExistsMs() != null ? conn.getRisposta().getWaitIfNotExistsMs() + "" : null;
		}
		
		
		String createParentDir = conn.getRichiesta().isCreateParentDir() != null ? ServletUtils.boolToCheckBoxStatus(conn.getRichiesta().isCreateParentDir()) : ServletUtils.boolToCheckBoxStatus(false);
		String overwriteIfExists = conn.getRichiesta().isOverwriteIfExists() != null ? ServletUtils.boolToCheckBoxStatus(conn.getRichiesta().isOverwriteIfExists()) : ServletUtils.boolToCheckBoxStatus(false);

		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnT,			// oldConnT
				"",						// tipoConn Personalizzato
				null, // this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				null, 
				null, 
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null,   // this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				null,												// this.httpsurl, 
				null,  //this.httpstipologia
				false,	// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,  // this.httpspath
				null,  //this.httpstipo,
				null,  //this.httpspwd,
				null,  // this.httpsalgoritmo
				false,
				null, // this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				null,  //pathkey
				null,  //this.httpstipokey
				null,  //this.httpspwdkey 
				null,  //this.httpspwdprivatekey,  
				null,  //this.httpsalgoritmokey,
				null,  //httpsKeyAlias
				null,  //httpsTrustStoreCRLs
			
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				null,
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				conn.getRichiesta().getFile(),	// this.requestOutputFileName,
				conn.getRichiesta().getFilePermissions(),   // this.requestOutputFileName_permissions
				conn.getRichiesta().getFileHeaders(),	// this.requestOutputFileNameHeaders,
				conn.getRichiesta().getFileHeadersPermissions(),   // this.requestOutputFileNameHeaders_permissions
				createParentDir,	// this.requestOutputParentDirCreateIfNotExists,
				overwriteIfExists,	// this.requestOutputOverwriteIfExists,
				responseInputMode,	// this.responseInputMode, 
				responseInputFileName,	// this.responseInputFileName, 
				responseInputFileNameHeaders,	// this.responseInputFileNameHeaders, 
				ServletUtils.boolToCheckBoxStatus(responseInputDeleteAfterRead),	// this.responseInputDeleteAfterRead, 
				responseInputWaitTime,	// this.responseInputWaitTime,
				null,
				listExtendedConnettore);	
		return regConnettore;
	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, ConnettoreFile conn,
			String oldConnType) throws Exception {

	    final String endpointtype = TipiConnettore.FILE.getNome();
	    
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
						null, false, endpointtype);

		String responseInputMode = conn.getRisposta() != null ? StatoFunzionalita.ABILITATO.toString() : StatoFunzionalita.DISABILITATO.toString();
		
		String responseInputFileName = null;
		String responseInputFileNameHeaders = null;
		boolean responseInputDeleteAfterRead = false;
		String responseInputWaitTime = null;
		
		if(conn.getRisposta() != null) {
			responseInputFileName = conn.getRisposta().getFile();
			responseInputFileNameHeaders = conn.getRisposta().getFileHeaders();
			responseInputDeleteAfterRead = conn.getRisposta().isDeleteAfterRead();
			responseInputWaitTime = conn.getRisposta().getWaitIfNotExistsMs() != null ? conn.getRisposta().getWaitIfNotExistsMs() + "" : null;
		}
		
		
		String createParentDir = conn.getRichiesta().isCreateParentDir() != null ? ServletUtils.boolToCheckBoxStatus(conn.getRichiesta().isCreateParentDir()) : ServletUtils.boolToCheckBoxStatus(false);
		String overwriteIfExists = conn.getRichiesta().isOverwriteIfExists() != null ? ServletUtils.boolToCheckBoxStatus(conn.getRichiesta().isOverwriteIfExists()) : ServletUtils.boolToCheckBoxStatus(false);

		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnType,			// oldConnT
				"",						// tipoConn Personalizzato
				null, // this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				null, 
				null, 
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null,   // this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				null,												// this.httpsurl, 
				null,  //this.httpstipologia
				false,	// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,  // this.httpspath
				null,  //this.httpstipo,
				null,  //this.httpspwd,
				null,  // this.httpsalgoritmo
				false,
				null, // this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				null,  //pathkey
				null,  //this.httpstipokey
				null,  //this.httpspwdkey 
				null,  //this.httpspwdprivatekey,  
				null,  //this.httpsalgoritmokey,
				null,  //httpsKeyAlias
				null,  //httpsTrustStoreCRLs
			
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				null,
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				conn.getRichiesta().getFile(),	// this.requestOutputFileName,
				conn.getRichiesta().getFilePermissions(),   // this.requestOutputFileName_permissions
				conn.getRichiesta().getFileHeaders(),	// this.requestOutputFileNameHeaders,
				conn.getRichiesta().getFileHeadersPermissions(),   // this.requestOutputFileNameHeaders_permissions
				createParentDir,	// this.requestOutputParentDirCreateIfNotExists,
				overwriteIfExists,	// this.requestOutputOverwriteIfExists,
				responseInputMode,	// this.responseInputMode, 
				responseInputFileName,	// this.responseInputFileName, 
				responseInputFileNameHeaders,	// this.responseInputFileNameHeaders, 
				ServletUtils.boolToCheckBoxStatus(responseInputDeleteAfterRead),	// this.responseInputDeleteAfterRead, 
				responseInputWaitTime,	// this.responseInputWaitTime,
				null,
				listExtendedConnettore);	
		return regConnettore;
	}

	@Override
	public ConnettoreFile buildConnettore(Map<String, String> props, String tipo) {
		ConnettoreFile c = new ConnettoreFile();

		c.setTipo(ConnettoreEnum.FILE);
		c.setDebug(Boolean.parseBoolean(props.get(CostantiDB.CONNETTORE_DEBUG)));

		ConnettoreFileRichiesta richiesta = new ConnettoreFileRichiesta();

		richiesta.setFile(props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE));
		richiesta.setFileHeaders(props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS));
		String requestOutputFileName = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);	
		String requestOutputFileName_permissions = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS);	
		String requestOutputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);	
		String requestOutputFileNameHeaders_permissions = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS);	
		String v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
		boolean requestOutputParentDirCreateIfNotExists = false;		
		if(v!=null && !"".equals(v)){
			if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
				requestOutputParentDirCreateIfNotExists = true;
			}
		}					
		v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
		boolean requestOutputOverwriteIfExists = false;
		if(v!=null && !"".equals(v)){
			if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
				requestOutputOverwriteIfExists = true;
			}
		}	

		v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
		boolean responseInputMode = false;
		if(v!=null && !"".equals(v)){
			if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
				responseInputMode = true;
			}
		}

		richiesta.setFile(requestOutputFileName);
		richiesta.setFilePermissions(requestOutputFileName_permissions);
		richiesta.setFileHeaders(requestOutputFileNameHeaders);
		richiesta.setFileHeadersPermissions(requestOutputFileNameHeaders_permissions);
		richiesta.setCreateParentDir(requestOutputParentDirCreateIfNotExists);
		richiesta.setOverwriteIfExists(requestOutputOverwriteIfExists);

		c.setRichiesta(richiesta);


		if(responseInputMode){
			boolean responseInputDeleteAfterRead = false;
			String responseInputFileName = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
			String responseInputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
			v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
			if(v!=null && !"".equals(v)){
				if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
					responseInputDeleteAfterRead = true;
				}
			}						
			String responseInputWaitTime = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);

			ConnettoreFileRisposta risposta = new ConnettoreFileRisposta();

			risposta.setFile(responseInputFileName);
			risposta.setFileHeaders(responseInputFileNameHeaders);

			if(responseInputWaitTime != null && !responseInputWaitTime.isEmpty()) {
				risposta.setWaitIfNotExistsMs(Integer.parseInt(responseInputWaitTime));
			}
			risposta.setDeleteAfterRead(responseInputDeleteAfterRead);

			c.setRisposta(risposta);
		}

		return c;
	}

	@Override
	public String getUrlConnettore(Map<String, String> properties, String tipoConnettore) throws Exception {
		return "[file] " + properties.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);
	}

}

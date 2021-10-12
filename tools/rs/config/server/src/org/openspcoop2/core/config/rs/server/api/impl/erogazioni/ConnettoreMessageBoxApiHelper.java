package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.Map;

import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpBasic;
import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreMessageBox;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;

public class ConnettoreMessageBoxApiHelper extends AbstractConnettoreApiHelper<ConnettoreMessageBox> {

	@Override
	public boolean connettoreCheckData(ConnettoreMessageBox conn, ErogazioniEnv env, boolean erogazione) throws Exception {
		return true;
	}

	@Override
	protected org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(ServizioApplicativo sa, ErogazioniEnv env, ConnettoreMessageBox connettore, String oldConnT) throws Exception {
		
		Credenziali credenziali = new Credenziali();
		credenziali.setTipo(CredenzialeTipo.BASIC);
		credenziali.setUser(connettore.getAutenticazioneHttp().getUsername());
		credenziali.setPassword(connettore.getAutenticazioneHttp().getPassword());
		if(sa.getInvocazionePorta() == null) {
			sa.setInvocazionePorta(new InvocazionePorta());
		}
		sa.getInvocazionePorta().addCredenziali(credenziali);
		sa.getInvocazioneServizio().setGetMessage(StatoFunzionalita.ABILITATO);
		
		String protocollo = env.soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());

		env.requestWrapper.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX, CostantiConfigurazione.ABILITATO.toString());
		env.requestWrapper.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME, connettore.getAutenticazioneHttp().getUsername());
		env.requestWrapper.overrideParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD, connettore.getAutenticazioneHttp().getPassword());
		env.requestWrapper.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP, CostantiConfigurazione.DISABILITATO.toString());
		env.requestWrapper.overrideParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA, CostantiConfigurazione.DISABILITATO.toString());
		env.requestWrapper.overrideParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE, TipiConnettore.DISABILITATO.getNome());

		boolean ok = env.saHelper.servizioApplicativoEndPointCheckData(protocollo, null, sa);
		
		if(!ok) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.saHelper.getPd().getMessage());
		}
		return super.buildConnettoreConfigurazione(sa, env, connettore, oldConnT);
	}
	
	@Override
	public Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,
			ErogazioniEnv env,
			ConnettoreMessageBox conn,
			String oldConnT) throws Exception {
		env.apsHelper.fillConnettore(
				regConnettore, 
				"false",				// this.connettoreDebug,
				TipiConnettore.DISABILITATO.getNome(), 			// endpointtype
				oldConnT,						// oldConnT
				"",						// tipoConn Personalizzato
				null, // this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				null,
				null,
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null, // this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				null, // this.httpsurl, 
				null,				// this.httpstipologia
				false,	// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,				// this.httpspath
				null,	// this.httpstipo,
				null,			// this.httpspwd,
				null,					// this.httpsalgoritmo
				false,
				null,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				null,				// pathkey
				null, 		// this.httpstipokey
				null,			// this.httpspwdkey 
				null,				// this.httpspwdprivatekey,  
				null,				// this.httpsalgoritmokey,
				null,					// httpsKeyAlias
        		null,					// httpsTrustStoreCRLs
			
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				null,
				
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,	// this.tempiRisposta_connectionTimeout, 
				null, //null,	// this.tempiRisposta_readTimeout, 
				null,	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,	// this.requestOutputFileName,
				null,	// this.requestOutputFileNameHeaders,
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				null,
				null);			
		return regConnettore;
	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, ConnettoreMessageBox conn,
			String oldConnType) throws Exception {

		env.apsHelper.fillConnettore(
				regConnettore, 
				"false",				// this.connettoreDebug,
				TipiConnettore.DISABILITATO.getNome(), 			// endpointtype
				oldConnType,						// oldConnT
				"",						// tipoConn Personalizzato
				null, // this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				null,
				null,
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null, // this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				null, // this.httpsurl, 
				null,				// this.httpstipologia
				false,	// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,				// this.httpspath
				null,	// this.httpstipo,
				null,			// this.httpspwd,
				null,					// this.httpsalgoritmo
				false,
				null,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				null,				// pathkey
				null, 		// this.httpstipokey
				null,			// this.httpspwdkey 
				null,				// this.httpspwdprivatekey,  
				null,				// this.httpsalgoritmokey,
				null,					// httpsKeyAlias
        		null,					// httpsTrustStoreCRLs
			
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				null,
				
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,	// this.tempiRisposta_connectionTimeout, 
				null, //null,	// this.tempiRisposta_readTimeout, 
				null,	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,	// this.requestOutputFileName,
				null,	// this.requestOutputFileNameHeaders,
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				null,
				null);			
		return regConnettore;
	}

	@Override
	protected ConnettoreMessageBox buildConnettore(ServizioApplicativo sa) {
		ConnettoreMessageBox c = new ConnettoreMessageBox();
		
		c.setTipo(ConnettoreEnum.MESSAGE_BOX);
		
		ConnettoreConfigurazioneHttpBasic autenticazioneHttp = new ConnettoreConfigurazioneHttpBasic();
		
		if(sa.getInvocazionePorta().getCredenzialiList().size() > 0) {
			autenticazioneHttp.setUsername(sa.getInvocazionePorta().getCredenziali(0).getUser());
			autenticazioneHttp.setPassword(sa.getInvocazionePorta().getCredenziali(0).getPassword());
		}
		
		c.setAutenticazioneHttp(autenticazioneHttp);
		return c;
	}

	@Override
	public ConnettoreMessageBox buildConnettore(Map<String, String> props, String tipo) {
		//non utilizzabile
		return null; //TODO eccezione
	}

	@Override
	public String getUrlConnettore(Map<String, String> props, String tipoConnettore) throws Exception {
		return "disabilitato [MessageBox]";
	}

	@Override
	protected ConnettoreMessageBox getConnettore(OneOfConnettoreErogazioneConnettore conn) throws Exception {
		return (ConnettoreMessageBox) conn;
	}

	@Override
	protected ConnettoreMessageBox getConnettore(OneOfConnettoreFruizioneConnettore conn) throws Exception {
		return (ConnettoreMessageBox) conn;
	}

	@Override
	protected ConnettoreMessageBox getConnettore(OneOfApplicativoServerConnettore conn) throws Exception {
		return (ConnettoreMessageBox) conn;
	}

}

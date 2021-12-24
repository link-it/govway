/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.transport.http;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

/**
 * SSLX509ManagerChooseClientAlias
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLX509ManagerForcedClientAlias implements X509KeyManager {

	/*
	 * Il default KeyManager spedisce il primo certificato che trova che ha un match con le condizioni richieste dal server. 
	 * Questa classe permette di forzare l'utilizzo di uno specifico certificato indirizzato da un alias
	 * */
	
	private String alias;
	private X509KeyManager wrapped_x509KeyManager;
	
	public SSLX509ManagerForcedClientAlias(String alias, X509KeyManager x509KeyManager) {
		this.alias = alias;
		this.wrapped_x509KeyManager = x509KeyManager;
	}
	
	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		// force
		return this.alias;
	}
	
	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return this.wrapped_x509KeyManager.getClientAliases(keyType, issuers);
	}
	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return this.wrapped_x509KeyManager.getServerAliases(keyType, issuers);
	}
	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		return this.wrapped_x509KeyManager.chooseServerAlias(keyType, issuers, socket);
	}
	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return this.wrapped_x509KeyManager.getCertificateChain(alias);
	}
	@Override
	public PrivateKey getPrivateKey(String alias) {
		return this.wrapped_x509KeyManager.getPrivateKey(alias);
	}
	
}

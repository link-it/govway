/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger.transazioni;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.slf4j.Logger;

/**
 * ConfigurazioneTracciamentoUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneTracciamentoUtils {

	private ConfigurazioneTracciamentoUtils() {}
	
	
	
	public static boolean isConsegnaMultipla(Context context) {
		int connettoriMultipli = getNumeroConnettoriMultipli(context);
		return isConsegnaMultipla(connettoriMultipli);
	}
	public static boolean isConsegnaMultipla(int connettoriMultipli) {
		boolean consegnaMultipla = false;
		if(connettoriMultipli>0) {
			consegnaMultipla = true;
		}
		return consegnaMultipla;
	}
	public static int getNumeroConnettoriMultipli(Context context) {
		int connettoriMultipli = -1;
		if(context.containsKey(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_CONNETTORI)) {
			Object oConnettori = context.getObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_CONNETTORI );
			if (oConnettori instanceof Integer){
				connettoriMultipli = (Integer) oConnettori;
			}
		}
		return connettoriMultipli;
	}
	public static String getConnettoriMultipli(Context context) {
		return ConnettoreUtils.getNomeConnettori(context);
	}
	
	
	
	public static boolean isEsitoDaRegistrare(Logger log, IProtocolFactory<?> protocolFactory, Context context, 
			List<String> esitiDaRegistrare, EsitoTransazione esitoTransazione) throws ProtocolException {
		// EsitiProperties
		EsitiProperties esitiProperties = EsitiProperties.getInstance(log, protocolFactory);
				
		int code = esitoTransazione.getCode();
		
		// ** Consegna Multipla **
		// NOTA: l'esito deve essere compreso solo dopo aver capito se le notifiche devono essere consegna o meno poichè le notifiche stesse si basano sullo stato di come è terminata la transazione sincrona
		boolean consegnaMultipla = isConsegnaMultipla(context);
		if(consegnaMultipla) {
			code = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
		}
		
		String codeAsString = code+"";
		return esitiDaRegistrare.contains(codeAsString);
	}
	public static String getEsitoTransazionDetail(Logger log, IProtocolFactory<?> protocolFactory,
			EsitoTransazione esitoTransazione) throws ProtocolException {
		EsitiProperties esitiProperties = EsitiProperties.getInstance(log, protocolFactory);
		int code = esitoTransazione.getCode();
		String codeAsString = code+"";
		return "esito [name:"+esitiProperties.getEsitoName(esitoTransazione.getCode())+" code:"+codeAsString+"]";
	}
	public static boolean isEsitoOk(Logger log, IProtocolFactory<?> protocolFactory,
			EsitoTransazione esitoTransazione) throws ProtocolException {
		List<String> esitiOk = getEsitiOk(log, protocolFactory);
		int code = esitoTransazione.getCode();
		String codeAsString = code+"";
		return esitiOk.contains(codeAsString);
	}
	private static List<String> getEsitiOk(Logger log, IProtocolFactory<?> protocolFactory) throws ProtocolException{
		EsitiProperties esitiProperties = EsitiProperties.getInstance(log, protocolFactory);
		List<Integer> tmpEsitiOk = esitiProperties.getEsitiCodeOk();
		List<String> esitiOk = new ArrayList<>();
		if(tmpEsitiOk!=null && !tmpEsitiOk.isEmpty()){
			for (Integer esito : tmpEsitiOk) {
				esitiOk.add(esito+"");
			}
		}
		return esitiOk;
	}
	
	public static String getPrefixFile(File f, boolean expectedDir) {
		return (expectedDir ? "Dir ":"")+"["+f.getAbsolutePath()+"] ";
	}
	public static CoreException newCoreExceptionNotFile(File f, boolean expectedDir){
		return new CoreException(getPrefixFile(f, expectedDir)+"isn't file");
	}
	public static CoreException newCoreExceptionCannotRead(File f, boolean expectedDir){
		return new CoreException(getPrefixFile(f, expectedDir)+"cannot read");
	}
	public static CoreException newCoreExceptionNotExists(File f, boolean expectedDir){
		return new CoreException(getPrefixFile(f, expectedDir)+"not exists");
	}
}

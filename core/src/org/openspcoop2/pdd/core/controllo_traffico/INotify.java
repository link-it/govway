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

package org.openspcoop2.pdd.core.controllo_traffico;

import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.RisultatoStato;
import org.openspcoop2.core.eventi.constants.CodiceEventoControlloTraffico;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.protocol.sdk.state.IState;
import org.slf4j.Logger;

/**     
 * INotify
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface INotify {

	public boolean isNotifichePassiveAttive();
	
	public RisultatoStato getStato(Logger logCore, DatiTransazione datiTransazione, IState state, String nomeAllarme) throws Exception;
	
	public void notificaGatewayRiavviato(Logger logCore, boolean debug);
	
	public void updateStatoRilevamentoCongestione(Logger logCore, boolean debug, TipoEvento tipoEvento, CodiceEventoControlloTraffico codiceEvento, String descrizione);
	
	public void updateStatoRilevamentoViolazionePolicy(Logger logCore, boolean debug, TipoEvento tipoEvento, CodiceEventoControlloTraffico codiceEvento, String idConfigurazionePolicy, String configurazionePolicy);
	
	public default void updateStatoRilevamentoRequestReadTimeout(Logger logCore, boolean debug, TipoEvento tipoEvento, CodiceEventoControlloTraffico codiceEvento, String idConfigurazionePolicy, String configurazionePolicy) {}
	
	public default void updateStatoRilevamentoReadTimeout(Logger logCore, boolean debug, TipoEvento tipoEvento, CodiceEventoControlloTraffico codiceEvento, String idConfigurazionePolicy, String configurazionePolicy) {}
	
}

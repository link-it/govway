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
package org.openspcoop2.pdd.core.behaviour;

import java.io.Serializable;

/**
 * BehaviourForwardToConfiguration
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BehaviourForwardToConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private StatoFunzionalita sbustamentoSoap = StatoFunzionalita.CONFIGURAZIONE_ORIGINALE;
	private StatoFunzionalita sbustamentoInformazioniProtocollo = StatoFunzionalita.CONFIGURAZIONE_ORIGINALE;
	
	public StatoFunzionalita getSbustamentoSoap() {
		return this.sbustamentoSoap;
	}
	public void setSbustamentoSoap(StatoFunzionalita sbustamentoSoap) {
		this.sbustamentoSoap = sbustamentoSoap;
	}
	public StatoFunzionalita getSbustamentoInformazioniProtocollo() {
		return this.sbustamentoInformazioniProtocollo;
	}
	public void setSbustamentoInformazioniProtocollo(
			StatoFunzionalita sbustamentoInformazioniProtocollo) {
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}
}

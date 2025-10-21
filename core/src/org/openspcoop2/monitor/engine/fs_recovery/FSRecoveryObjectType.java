/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.fs_recovery;

import java.util.ArrayList;
import java.util.List;

/**
 * FSRecoveryObjectType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum FSRecoveryObjectType {

	EVENTI_GESTIONE_COMPLETA,
	EVENTI,
	
	TRANSAZIONI_GESTIONE_COMPLETA,
	TRANSAZIONI,
	TRANSAZIONI_SERVER,
	TRANSAZIONI_SERVER_CONSEGNA_TERMINATA,
	TRANSAZIONI_TRACCE,
	TRANSAZIONI_DIAGNOSTICI,
	TRANSAZIONI_DUMP;
	
	public boolean isGestioneEventi() {
		return FSRecoveryObjectType.EVENTI_GESTIONE_COMPLETA.equals(this) ||
				FSRecoveryObjectType.EVENTI.equals(this);
	}
	public boolean isGestioneTransazionii() {
		return FSRecoveryObjectType.TRANSAZIONI_GESTIONE_COMPLETA.equals(this) ||
				FSRecoveryObjectType.TRANSAZIONI.equals(this) ||
				FSRecoveryObjectType.TRANSAZIONI_SERVER.equals(this) ||
				FSRecoveryObjectType.TRANSAZIONI_SERVER_CONSEGNA_TERMINATA.equals(this) ||
				FSRecoveryObjectType.TRANSAZIONI_TRACCE.equals(this) ||
				FSRecoveryObjectType.TRANSAZIONI_DIAGNOSTICI.equals(this) ||
				FSRecoveryObjectType.TRANSAZIONI_DUMP.equals(this) ;
	}
	
	public static List<FSRecoveryObjectType> getOperazioniEventi(){
		List<FSRecoveryObjectType> l = new ArrayList<>();
		for (FSRecoveryObjectType fsRecoveryObjectType : FSRecoveryObjectType.values()) {
			if(fsRecoveryObjectType.isGestioneEventi() && !EVENTI_GESTIONE_COMPLETA.equals(fsRecoveryObjectType)) {
				l.add(fsRecoveryObjectType);		
			}
		}
		return l;
	}
	
	public static List<FSRecoveryObjectType> getOperazioniTransazioni(){
		List<FSRecoveryObjectType> l = new ArrayList<>();
		for (FSRecoveryObjectType fsRecoveryObjectType : FSRecoveryObjectType.values()) {
			if(fsRecoveryObjectType.isGestioneTransazionii() && !TRANSAZIONI_GESTIONE_COMPLETA.equals(fsRecoveryObjectType)) {
				l.add(fsRecoveryObjectType);		
			}
		}
		return l;
	}
}

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



package org.openspcoop2.pdd.timers;

/**	
 * TimerLock
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class TimerLock {

	public static TimerLock newInstance(TipoLock tipoLock) throws TimerException {
		return new TimerLock(tipoLock);
	}
	
	public TimerLock(TipoLock tipoLock) throws TimerException {
		this(tipoLock,tipoLock.getTipo());
		check(tipoLock);
	}
	private static void check(TipoLock tipoLock) throws TimerException {
		if(TipoLock.CUSTOM.equals(tipoLock) || TipoLock.CONSEGNA_NOTIFICHE.equals(tipoLock)) {
			throw new TimerException("Tipo lock '"+tipoLock+"' non utilizzabile senza fornire l'id del lock");
		}
	}
	public TimerLock(TipoLock tipoLock, String idLock) throws TimerException {
		if(tipoLock==null) {
			throw new TimerException("Tipo lock non definito");
		}
		if( (TipoLock.CUSTOM.equals(tipoLock) || TipoLock.CONSEGNA_NOTIFICHE.equals(tipoLock)) 
				&& 
				(idLock==null || "".equals(idLock)) ) {
			throw new TimerException("Tipo lock '"+tipoLock+"' non utilizzabile senza fornire l'id del lock");
		}
		this.tipoLock = tipoLock;
		if(idLock==null || "".equals(idLock)) {
			this.idLock = tipoLock.getTipo();
		}
		else if(TipoLock.CONSEGNA_NOTIFICHE.equals(tipoLock)) {
			this.idLock = getIdLockConsegnaNotifica(idLock);
		}
		else {
			this.idLock = idLock;
		}
	}
	
	public static String getIdLockConsegnaNotifica(String queue) throws TimerException {
		if(queue==null) {
			throw new TimerException("Coda non definita");
		}
		return TipoLock.CONSEGNA_NOTIFICHE.getTipo()+"-"+queue;
	}	
	
	private TipoLock tipoLock;
	private String idLock;

	public TipoLock getTipoLock() {
		return this.tipoLock;
	}
	public void setTipoLock(TipoLock tipoLock) {
		this.tipoLock = tipoLock;
	}
	public String getIdLock() {
		return this.idLock;
	}
	public void setIdLock(String idLock) {
		this.idLock = idLock;
	}
}

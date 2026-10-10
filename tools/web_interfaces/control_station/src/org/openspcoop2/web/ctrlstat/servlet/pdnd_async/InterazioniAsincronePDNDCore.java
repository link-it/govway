/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pdnd_async;

import java.util.List;

import org.openspcoop2.pdd.core.pdnd.async.InterazioneAsincronaPDND;
import org.openspcoop2.pdd.core.pdnd.async.InterazioniAsincronePDNDFiltro;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.monitor.MonitorUtilities;

/**
 * Accesso alle interazioni relative agli scambi di dati asincroni PDND, registrate sulla base dati runtime;
 * vengono utilizzate le sorgenti dati runtime configurate per la consultazione della coda messaggi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioniAsincronePDNDCore extends ControlStationCore {

	public InterazioniAsincronePDNDCore() throws DriverControlStationException {
		super();
	}
	public InterazioniAsincronePDNDCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
	}
	
	public List<String> getSorgentiDati() throws DriverControlStationException {
		try {
			return MonitorUtilities.getSorgentiDati();
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	public List<String> getLabelSorgentiDati() throws DriverControlStationException {
		try {
			return MonitorUtilities.getLabelSorgentiDati();
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	
	public long count(InterazioniAsincronePDNDFiltro filtro, String sorgente) throws DriverControlStationException {
		try {
			return MonitorUtilities.countInterazioniAsincronePDND(filtro, sorgente);
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	public List<InterazioneAsincronaPDND> list(InterazioniAsincronePDNDFiltro filtro, String sorgente) throws DriverControlStationException {
		try {
			return MonitorUtilities.getInterazioniAsincronePDND(filtro, sorgente);
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	public InterazioneAsincronaPDND get(long id, String sorgente) throws DriverControlStationException {
		try {
			return MonitorUtilities.getInterazioneAsincronaPDND(id, sorgente);
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	public int delete(long id, String sorgente) throws DriverControlStationException {
		try {
			return MonitorUtilities.deleteInterazioneAsincronaPDND(id, sorgente);
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
}

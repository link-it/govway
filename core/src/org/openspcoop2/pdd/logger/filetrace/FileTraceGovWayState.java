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
package org.openspcoop2.pdd.logger.filetrace;

import java.io.File;
import java.util.Date;
import java.util.Scanner;

import org.openspcoop2.utils.date.DateUtils;

/**
 * FileTraceGovWayState
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceGovWayState {

	private boolean enabled = false;
	private String path;
	private String lastModified;
	private boolean enabledInErogazione = false;
	private boolean enabledOutErogazione = false;
	private boolean enabledInFruizione = false;
	private boolean enabledOutFruizione = false;
	
	public boolean isEnabled() {
		return this.enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getPath() {
		return this.path;
	}
	public String getLastModified() {
		return this.lastModified;
	}
	public void setPath(String path) {
		this.path = path;
		try {
			Date d = new Date(new File(this.path).lastModified());
			this.lastModified = DateUtils.getSimpleDateFormatMs().format(d);
		}catch(Exception e) {
			this.lastModified = "N.D.";
		}
	}
	public boolean isEnabledInErogazione() {
		return this.enabledInErogazione;
	}
	public void setEnabledInErogazione(boolean enabledInErogazione) {
		this.enabledInErogazione = enabledInErogazione;
	}
	public boolean isEnabledOutErogazione() {
		return this.enabledOutErogazione;
	}
	public void setEnabledOutErogazione(boolean enabledOutErogazione) {
		this.enabledOutErogazione = enabledOutErogazione;
	}
	public boolean isEnabledInFruizione() {
		return this.enabledInFruizione;
	}
	public void setEnabledInFruizione(boolean enabledInFruizione) {
		this.enabledInFruizione = enabledInFruizione;
	}
	public boolean isEnabledOutFruizione() {
		return this.enabledOutFruizione;
	}
	public void setEnabledOutFruizione(boolean enabledOutFruizione) {
		this.enabledOutFruizione = enabledOutFruizione;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Enabled: ").append(this.enabled);
		if(this.enabled) {
			sb.append("\nPath: ").append(this.path);
			sb.append("\nLastModified: ").append(this.lastModified);
			sb.append("\nInErogazione: ").append(this.enabledInErogazione);
			sb.append("\nOutErogazione: ").append(this.enabledOutErogazione);
			sb.append("\nInFruizione: ").append(this.enabledInFruizione);
			sb.append("\nOutFruizione: ").append(this.enabledOutFruizione);
		}
		return sb.toString();
	}
	
	public static FileTraceGovWayState toConfig(String s) {
		FileTraceGovWayState state = new FileTraceGovWayState();
		state.enabled = s!=null && s.startsWith("Enabled: true");
		if(state.enabled) {
			try(Scanner scanner = new Scanner(s);){
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line.contains(": ")) {
						String [] tmp = line.split(": ");
						if(tmp!=null && tmp.length==2) {
							String nome = tmp[0];
							String valore = tmp[1];
							if("Path".equals(nome)) {
								state.path = valore;
							}
							else if("LastModified".equals(nome)) {
								state.lastModified = valore;
							}
							else if("InErogazione".equals(nome)) {
								state.enabledInErogazione = Boolean.valueOf(valore);
							}
							else if("OutErogazione".equals(nome)) {
								state.enabledOutErogazione = Boolean.valueOf(valore);
							}
							else if("InFruizione".equals(nome)) {
								state.enabledInFruizione = Boolean.valueOf(valore);
							}
							else if("OutFruizione".equals(nome)) {
								state.enabledOutFruizione = Boolean.valueOf(valore);
							}
						}
					}
				}
			}
		}
		return state;
	}
}

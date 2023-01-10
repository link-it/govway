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
	private boolean enabledInErogazione_headers = false;
	private boolean enabledInErogazione_payload = false;
	
	private boolean enabledOutErogazione = false;
	private boolean enabledOutErogazione_headers = false;
	private boolean enabledOutErogazione_payload = false;
	
	private boolean enabledInFruizione = false;
	private boolean enabledInFruizione_headers = false;
	private boolean enabledInFruizione_payload = false;
	
	private boolean enabledOutFruizione = false;
	private boolean enabledOutFruizione_headers = false;
	private boolean enabledOutFruizione_payload = false;
	
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
	
	public boolean isEnabledInErogazione_headers() {
		return this.enabledInErogazione_headers;
	}
	public void setEnabledInErogazione_headers(boolean enabledInErogazione_headers) {
		this.enabledInErogazione_headers = enabledInErogazione_headers;
	}
	public boolean isEnabledInErogazione_payload() {
		return this.enabledInErogazione_payload;
	}
	public void setEnabledInErogazione_payload(boolean enabledInErogazione_payload) {
		this.enabledInErogazione_payload = enabledInErogazione_payload;
	}
	public boolean isEnabledOutErogazione_headers() {
		return this.enabledOutErogazione_headers;
	}
	public void setEnabledOutErogazione_headers(boolean enabledOutErogazione_headers) {
		this.enabledOutErogazione_headers = enabledOutErogazione_headers;
	}
	public boolean isEnabledOutErogazione_payload() {
		return this.enabledOutErogazione_payload;
	}
	public void setEnabledOutErogazione_payload(boolean enabledOutErogazione_payload) {
		this.enabledOutErogazione_payload = enabledOutErogazione_payload;
	}
	public boolean isEnabledInFruizione_headers() {
		return this.enabledInFruizione_headers;
	}
	public void setEnabledInFruizione_headers(boolean enabledInFruizione_headers) {
		this.enabledInFruizione_headers = enabledInFruizione_headers;
	}
	public boolean isEnabledInFruizione_payload() {
		return this.enabledInFruizione_payload;
	}
	public void setEnabledInFruizione_payload(boolean enabledInFruizione_payload) {
		this.enabledInFruizione_payload = enabledInFruizione_payload;
	}
	public boolean isEnabledOutFruizione_headers() {
		return this.enabledOutFruizione_headers;
	}
	public void setEnabledOutFruizione_headers(boolean enabledOutFruizione_headers) {
		this.enabledOutFruizione_headers = enabledOutFruizione_headers;
	}
	public boolean isEnabledOutFruizione_payload() {
		return this.enabledOutFruizione_payload;
	}
	public void setEnabledOutFruizione_payload(boolean enabledOutFruizione_payload) {
		this.enabledOutFruizione_payload = enabledOutFruizione_payload;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Enabled: ").append(this.enabled);
		if(this.enabled) {
			sb.append("\nPath: ").append(this.path);
			sb.append("\nLastModified: ").append(this.lastModified);
			sb.append("\nInErogazione: ").append(this.enabledInErogazione);
			if(this.enabledInErogazione) {
				sb.append("\nInErogazione (headers): ").append(this.enabledInErogazione_headers);
				sb.append("\nInErogazione (payload): ").append(this.enabledInErogazione_payload);
			}
			sb.append("\nOutErogazione: ").append(this.enabledOutErogazione);
			if(this.enabledOutErogazione) {
				sb.append("\nOutErogazione (headers): ").append(this.enabledOutErogazione_headers);
				sb.append("\nOutErogazione (payload): ").append(this.enabledOutErogazione_payload);
			}
			sb.append("\nInFruizione: ").append(this.enabledInFruizione);
			if(this.enabledInFruizione) {
				sb.append("\nInFruizione (headers): ").append(this.enabledInFruizione_headers);
				sb.append("\nInFruizione (payload): ").append(this.enabledInFruizione_payload);
			}
			sb.append("\nOutFruizione: ").append(this.enabledOutFruizione);
			if(this.enabledOutFruizione) {
				sb.append("\nOutFruizione (headers): ").append(this.enabledOutFruizione_headers);
				sb.append("\nOutFruizione (payload): ").append(this.enabledOutFruizione_payload);
			}
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
							else if("InErogazione (headers)".equals(nome)) {
								state.enabledInErogazione_headers = Boolean.valueOf(valore);
							}
							else if("InErogazione (payload)".equals(nome)) {
								state.enabledInErogazione_payload = Boolean.valueOf(valore);
							}
							
							else if("OutErogazione".equals(nome)) {
								state.enabledOutErogazione = Boolean.valueOf(valore);
							}
							else if("OutErogazione (headers)".equals(nome)) {
								state.enabledOutErogazione_headers = Boolean.valueOf(valore);
							}
							else if("OutErogazione (payload)".equals(nome)) {
								state.enabledOutErogazione_payload = Boolean.valueOf(valore);
							}
							
							else if("InFruizione".equals(nome)) {
								state.enabledInFruizione = Boolean.valueOf(valore);
							}
							else if("InFruizione (headers)".equals(nome)) {
								state.enabledInFruizione_headers = Boolean.valueOf(valore);
							}
							else if("InFruizione (payload)".equals(nome)) {
								state.enabledInFruizione_payload = Boolean.valueOf(valore);
							}
							
							else if("OutFruizione".equals(nome)) {
								state.enabledOutFruizione = Boolean.valueOf(valore);
							}
							else if("OutFruizione (headers)".equals(nome)) {
								state.enabledOutFruizione_headers = Boolean.valueOf(valore);
							}
							else if("OutFruizione (payload)".equals(nome)) {
								state.enabledOutFruizione_payload = Boolean.valueOf(valore);
							}
						}
					}
				}
			}
		}
		return state;
	}
}

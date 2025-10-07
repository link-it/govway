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
package org.openspcoop2.web.lib.mvc.login;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * Bean per gestire i tentativi falliti di login
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class FailedAttempts {
	
	private ConcurrentMap<String, LoginAttempt> failedAttemptsMap;
	
	private final List<Duration> failedDelays;
	
	private static FailedAttempts instance = null;
	
	public static final FailedAttempts getInstance() {
		return instance;
	}
	
	public static synchronized void createInstance(String loginRetryDelays) throws UtilsException {
		if (instance == null) {
			instance = new FailedAttempts(loginRetryDelays);
		}
	}
	
	public FailedAttempts(String loginRetryDelays) throws UtilsException {
		this.failedAttemptsMap = new ConcurrentHashMap<>();
		List<Duration> loginRertyDelays = this.getFailedAttemptDelay(loginRetryDelays);
		this.failedDelays = new ArrayList<>(loginRertyDelays);
	}

	public LoginAttempt get(String username) {
		return this.failedAttemptsMap.get(username);
	}
	
	public boolean bloccaUtente(Logger log, String username) {
		LoginAttempt attempt = this.get(username);
		if (attempt != null && attempt.getExpiring().isAfter(Instant.now())) {
			log.error("Accesso fallito utente: {} utente bloccato causa troppi tentativi fino a: {}", username, attempt.getExpiring());
			return true;
		}
		
		return false;
	}
	
	public List<Duration> getFailedAttemptDelay(String loginRetryDelays) throws UtilsException{
		List<String> prop = List.of(loginRetryDelays.split(","));
		List<Duration> parsedProp = new ArrayList<>();
		
		Integer prev = 0;
		for (String p : prop) {
			Integer curr = Integer.parseInt(p.trim());
			if (prev > curr)
				throw new UtilsException("I tempi di attesa nel caso di credenziali errate devono essere incrementali");
			parsedProp.add(Duration.ofSeconds(curr));
		}
		
		return parsedProp;
	}

	public void aggiungiTentativoFallitoUtente(Logger log, String username) {
		this.failedAttemptsMap.compute(username, (key, value) -> {
			int retries = ((value == null) ? 0 : value.getRetries()) + 1;
			Instant expires = Instant.now().plus(computeUserLockAmount(retries));
			log.error("utente {} password errata, tentativo: {}, utente sbloccato dopo: {}", username, retries, expires);
			return new LoginAttempt(expires, retries);
		});

	}
	
	private TemporalAmount computeUserLockAmount(int retries) {
		return this.failedDelays.get(Math.min(retries - 1, this.failedDelays.size() - 1));
	}

	public void resetTentativiUtente(Logger log,String username) {
		log.debug("utente {} password corrretta", username);
		this.failedAttemptsMap.remove(username);
	}
}

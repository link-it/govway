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

package org.openspcoop2.pdd.core.token.parser.test;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Properties;

import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.InformazioniJWTClientAssertion;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken_DatiRichiesta;
import org.openspcoop2.pdd.core.token.parser.BasicNegoziazioneTokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaimsNegoziazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.slf4j.Logger;

/**
 * TestBasicNegoziazioneTokenParser
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class BasicNegoziazioneTokenParserTest {

	public static void main(String [] args) throws Exception{
		test();
	}
	public static void test() throws Exception{
		
		Logger log = LoggerWrapperFactory.getLogger(BasicNegoziazioneTokenParserTest.class);
		String idTransazione = "66aa1676-1f9e-34e2-7777-0cfca111a9999";
		InformazioniNegoziazioneToken_DatiRichiesta datiRichiesta = null;
		
		DecimalFormat decimalFormat9 = new DecimalFormat("0.#########E0");
		
		// Test 1
		{
			System.out.println("Test BasicNegoziazioneTokenParser, access token regolare ...");
			String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			String refreshToken = "33ref1676-1f9e-34e2-8515-0cfca111a188";
			String tokenType = "Bearer";
			String rawResponse = "{\"access_token\":\""+accessToken+"\",\"refresh_token\":\""+refreshToken+"\",\"scope\":\"s1 s2\",\"token_type\":\""+tokenType+"\",\"expires_in\":3700,\"refresh_expires_in\":4000}";
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=6) {
				throw new Exception("Claims attesi 6, trovati '"+info.getClaims().size()+"'");	
			}
			if(!info.getClaims().containsKey("access_token")){
				throw new Exception("Atteso access_token non presente tra i claims");
			}
			if(!info.getClaims().containsKey("refresh_token")){
				throw new Exception("Atteso refresh_token non presente tra i claims");
			}
			if(!info.getClaims().containsKey("scope")){
				throw new Exception("Atteso scope non presente tra i claims");
			}
			if(!info.getClaims().containsKey("token_type")){
				throw new Exception("Atteso token_type non presente tra i claims");
			}
			if(!info.getClaims().containsKey("expires_in")){
				throw new Exception("Atteso expires_in non presente tra i claims");
			}
			if(!info.getClaims().containsKey("refresh_expires_in")){
				throw new Exception("Atteso refresh_expires_in non presente tra i claims");
			}
			
			if(!accessToken.equals(info.getAccessToken())){
				throw new Exception("Atteso access_token '"+accessToken+"' trovato '"+info.getAccessToken()+"'");
			}
			if(!refreshToken.equals(info.getRefreshToken())){
				throw new Exception("Atteso refresh_token '"+refreshToken+"' trovato '"+info.getRefreshToken()+"'");
			}
			
			if(info.getScopes()==null) {
				throw new Exception("Scope attesi non trovato");
			}
			if(info.getScopes().size()!=2) {
				throw new Exception("Scope attesi 2, trovati '"+info.getScopes().size()+"'");
			}
			boolean find = false;
			for (String s : info.getScopes()) {
				if("s1".equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso 's1' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if("s2".equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso 's2' non trovato");
			}
			
			if(!tokenType.equals(info.getTokenType())){
				throw new Exception("Atteso token_type '"+tokenType+"' trovato '"+info.getTokenType()+"'");
			}
			
			Date d = info.getExpiresIn();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			Date now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*2));
			if(now.before(d)){
				throw new Exception("(3) Atteso token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getRefreshExpiresIn();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) RefreshToken scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) RefreshToken scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*2));
			if(now.before(d)){
				throw new Exception("(3) Atteso refresh token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test BasicNegoziazioneTokenParser, access token regolare ok");
		}
		
		// Test 2, senza alcuni campi
		{
			System.out.println("Test BasicNegoziazioneTokenParser, access token senza alcuni campi ...");
			String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			String tokenType = "Bearer";
			String rawResponse = "{\"access_token\":\""+accessToken+"\",\"token_type\":\""+tokenType+"\"}";
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=2) {
				throw new Exception("Claims attesi 2, trovati '"+info.getClaims().size()+"'");	
			}
			if(!info.getClaims().containsKey("access_token")){
				throw new Exception("Atteso access_token non presente tra i claims");
			}
			if(info.getClaims().containsKey("refresh_token")){
				throw new Exception("refresh_token non atteso tra i claims");
			}
			if(info.getClaims().containsKey("scope")){
				throw new Exception("scope non atteso tra i claims");
			}
			if(!info.getClaims().containsKey("token_type")){
				throw new Exception("Atteso token_type non presente tra i claims");
			}
			if(info.getClaims().containsKey("expires_in")){
				throw new Exception("expires_in non atteso tra i claims");
			}
			
			if(!accessToken.equals(info.getAccessToken())){
				throw new Exception("Atteso access_token '"+accessToken+"' trovato '"+info.getAccessToken()+"'");
			}
			if(info.getRefreshToken()!=null) {
				throw new Exception("refresh_token non atteso");
			}
			
			if(info.getScopes()!=null) {
				throw new Exception("Scope non attesi");
			}
		
			if(!tokenType.equals(info.getTokenType())){
				throw new Exception("Atteso token_type '"+tokenType+"' trovato '"+info.getTokenType()+"'");
			}
			
			Date d = info.getExpiresIn();
			if(d!=null) {
				throw new Exception("Data di scadenza non attesa");
			}

			System.out.println("Test BasicNegoziazioneTokenParser, access token senza alcuni campi ok");
		}

		
		for (int i = 0; i < 2; i++) {
			String tipoTestDateSuffix = (i==0) ? " [formato: number]" : " [formato: exponential E9]";
		
		
			// Test 3
			{
				System.out.println("Test BasicNegoziazioneTokenParser, access token con expires_in con max long value"+tipoTestDateSuffix+" e un solo scope ...");
				String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
				String tokenType = "Bearer";
				Long dataL = Long.MAX_VALUE/1000;
				String dataS = (i==0) ? (dataL+"") : (decimalFormat9.format(dataL));
				String rawResponse = "{\"access_token\":\""+accessToken+"\",\"scope\":\"s1\",\"token_type\":\""+tokenType+"\",\"expires_in\":"+dataS+"}";
				BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
				InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
				
				if(info.getClaims()==null || info.getClaims().isEmpty()) {
					throw new Exception("Parse non riuscito?");
				}
				if(info.getClaims().size()!=4) {
					throw new Exception("Claims attesi 4, trovati '"+info.getClaims().size()+"'");	
				}
				if(!info.getClaims().containsKey("access_token")){
					throw new Exception("Atteso access_token non presente tra i claims");
				}
				if(info.getClaims().containsKey("refresh_token")){
					throw new Exception("refresh_token non atteso tra i claims");
				}
				if(!info.getClaims().containsKey("scope")){
					throw new Exception("Atteso scope non presente tra i claims");
				}
				if(!info.getClaims().containsKey("token_type")){
					throw new Exception("Atteso token_type non presente tra i claims");
				}
				if(!info.getClaims().containsKey("expires_in")){
					throw new Exception("Atteso expires_in non presente tra i claims");
				}
				
				if(!accessToken.equals(info.getAccessToken())){
					throw new Exception("Atteso access_token '"+accessToken+"' trovato '"+info.getAccessToken()+"'");
				}
				if(info.getRefreshToken()!=null) {
					throw new Exception("refresh_token non atteso");
				}
				
				if(info.getScopes()==null) {
					throw new Exception("Scope attesi non trovato");
				}
				if(info.getScopes().size()!=1) {
					throw new Exception("Scope attesi 1, trovati '"+info.getScopes().size()+"'");
				}
				boolean find = false;
				for (String s : info.getScopes()) {
					if("s1".equals(s)) {
						find = true;
						break;
					}
				}
				if(!find) {
					throw new Exception("Scope atteso 's1' non trovato");
				}
				
				if(!tokenType.equals(info.getTokenType())){
					throw new Exception("Atteso token_type '"+tokenType+"' trovato '"+info.getTokenType()+"'");
				}
				
				Date d = info.getExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				Date now = DateManager.getDate();
				if(!now.before(d)){
					throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
				}
				now = new Date(now.getTime() + (1000*60*60*1));
				if(!now.before(d)){
					throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
				}
				now = new Date(Long.MAX_VALUE-1);
				if(!now.before(d)){
					throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
				}
				long atteso = Long.MAX_VALUE;
				if(d.getTime() != atteso) {
					throw new Exception("(4) Attesa data associata la long max value '"+atteso+"', trovata con long value '"+d.getTime()+"' data: "+DateUtils.getOldSimpleDateFormatMs().format(d));
				}
				System.out.println("Test BasicNegoziazioneTokenParser, access token con expires_in con max long value"+tipoTestDateSuffix+" e un solo scope ok");
			}
	
			
			// Test 4
			{
				System.out.println("Test BasicNegoziazioneTokenParser, access token con expires_in con un valore maggiore del max long"+tipoTestDateSuffix+" ...");
				String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
				String tokenType = "Bearer";
				Long dataL = Long.MAX_VALUE/1000;
				String dataS = (i==0) ? (dataL+"") : (decimalFormat9.format(dataL));
				String rawResponse = "{\"access_token\":\""+accessToken+"\",\"token_type\":\""+tokenType+"\",\"expires_in\":1111"+dataS+"}";
				BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
				InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
				
				if(info.getClaims()==null || info.getClaims().isEmpty()) {
					throw new Exception("Parse non riuscito?");
				}
				if(info.getClaims().size()!=3) {
					throw new Exception("Claims attesi 3, trovati '"+info.getClaims().size()+"'");	
				}
				if(!info.getClaims().containsKey("access_token")){
					throw new Exception("Atteso access_token non presente tra i claims");
				}
				if(info.getClaims().containsKey("refresh_token")){
					throw new Exception("refresh_token non atteso tra i claims");
				}
				if(info.getClaims().containsKey("scope")){
					throw new Exception("scope non atteso tra i claims");
				}
				if(!info.getClaims().containsKey("token_type")){
					throw new Exception("Atteso token_type non presente tra i claims");
				}
				if(!info.getClaims().containsKey("expires_in")){
					throw new Exception("Atteso expires_in non presente tra i claims");
				}
				
				if(!accessToken.equals(info.getAccessToken())){
					throw new Exception("Atteso access_token '"+accessToken+"' trovato '"+info.getAccessToken()+"'");
				}
				if(info.getRefreshToken()!=null) {
					throw new Exception("refresh_token non atteso");
				}
				
				if(info.getScopes()!=null) {
					throw new Exception("Scope non attesi");
				}
				
				if(!tokenType.equals(info.getTokenType())){
					throw new Exception("Atteso token_type '"+tokenType+"' trovato '"+info.getTokenType()+"'");
				}
				
				Date d = info.getExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				Date now = DateManager.getDate();
				if(!now.before(d)){
					throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
				}
				now = new Date(now.getTime() + (1000*60*60*1));
				if(!now.before(d)){
					throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
				}
				now = new Date(Long.MAX_VALUE-1);
				if(!now.before(d)){
					throw new Exception("(3 MaxValue) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
				}
				if(d.getTime() != Long.MAX_VALUE) {
					throw new Exception("(4) Attesa data associata la long max value '"+Long.MAX_VALUE+"', trovata con long value '"+d.getTime()+"' data: "+DateUtils.getOldSimpleDateFormatMs().format(d));
				}
				System.out.println("Test BasicNegoziazioneTokenParser, access token con expires_in con un valore maggiore del max long"+tipoTestDateSuffix+" ok");
			}
			
		}
		
		
		// Test 5
		{
			System.out.println("Test ClientAssertionJWT ...");
		
			String hdr = "ewogICJhbGciOiAiUlMyNTYiLAogICJ0eXAiOiAiSldUIiwKICAia2lkIjogInByb3ZhIiwKICAieDVjIjogWwogICAgIk1JSS4uLi4uZjZ1UXhjbGhVaDBOaXZqNmhJSitDZDNSMS9GdncxejY5RXllT3ROd3FZU2JzdzJnamlkbzhHUGdEVWtxZFVaSThyYnRjbDIyK2x0S2VXbURhUXZOUUZnTlUreUJObTBBPSIKICBdCn0=";
			String payload = "ewogICJpYXQiOiAxNjUwMDMyOTAzLAogICJuYmYiOiAxNjUwMDMyOTAzLAogICJleHAiOiAxNjUwMDMzMDAzLAogICJqdGkiOiAiNGU2MWRjMTQtYmNjOC0xMWVjLTllOTktMDA1MDU2YWUwMzA3IiwKICAiYXVkIjogInRlc3QiLAogICJjbGllbnRfaWQiOiAiY1Rlc3QiLAogICJpc3MiOiAiaXNzVGVzdCIsCiAgInN1YiI6ICJzdWJUZXN0Igp9";
			String testBase64 = hdr+"."+payload+".PDdXpT5htzB6JI0TdYsfsIBjH8tSV0IkIiKAI0S1IYkqcS6pOs84MsfVk3wnd1_dSiR-2KSpGzZU9s8TuGoXcdR-4oa6EN0RNJJsF8zC1KHVx1IBl4jcZGRY5vAgtKwBC87bPz7EaYXtesS3Go-fl5HTFWvZ4OR3yxvsrCfTy_ehQwVJwJy9yKrIpQFq_dSQr_xQbRBL495D9Fp4p54vNdP3IRtoDq16NUhwkH_dbQJGUJdYZ2M31bBZUvgu9RRZz_ftjI78Swwq5FIwIG7r5trwgmVebZtdLF2Ni5Vc2rL7ZNuBpH7Y_knRgRYbH4HxnMoHOU6nU8yM_ZPZyhHneA";
			
			InformazioniJWTClientAssertion jwtClientAssertion = new InformazioniJWTClientAssertion(log, testBase64, false);
			
			if(jwtClientAssertion.getToken()==null) {
				throw new Exception("Parse non riuscito?");
			}
			
			if(jwtClientAssertion.getJsonHeader()!=null) {
				throw new Exception("JsonHeader non atteso");
			}
			
			if(jwtClientAssertion.getJsonPayload()!=null) {
				throw new Exception("JsonPayload non atteso");
			}
			
			if(jwtClientAssertion.getHeader()!=null) {
				throw new Exception("Header non atteso");
			}
			
			if(jwtClientAssertion.getPayload()!=null) {
				throw new Exception("Payload non atteso");
			}
			
			
			String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			String tokenType = "Bearer";
			String rawResponse = "{\"access_token\":\""+accessToken+"\",\"token_type\":\""+tokenType+"\",\"expires_in\":11119223372036854775}";
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
			
			jwtClientAssertion = new InformazioniJWTClientAssertion(log, testBase64, true); // effettuo analisi negoziazione
			
			datiRichiesta = new InformazioniNegoziazioneToken_DatiRichiesta();
			datiRichiesta.setPolicy("TEST");
			datiRichiesta.setTransactionId(idTransazione);
			datiRichiesta.setJwtClientAssertion(jwtClientAssertion);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
		
			if(info.getRequest()==null) {
				throw new Exception("Request not set");
			}
			
			if(info.getRequest().getJwtClientAssertion()==null) {
				throw new Exception("JWT client assertion not set");
			}
			
			if(info.getRequest().getJwtClientAssertion().getToken()==null) {
				throw new Exception("Parse non riuscito?");
			}
			
			if(info.getRequest().getJwtClientAssertion().getJsonHeader()==null) {
				throw new Exception("JsonHeader atteso non presente");
			}
			String base64DecodeHdr = new String(Base64Utilities.decode(hdr.getBytes()));
			if(!base64DecodeHdr.equals(info.getRequest().getJwtClientAssertion().getJsonHeader())) {
				throw new Exception("JsonHeader '"+info.getRequest().getJwtClientAssertion().getJsonHeader()+"' differente da quello atteso '"+base64DecodeHdr+"'");
			}
			
			if(info.getRequest().getJwtClientAssertion().getJsonPayload()==null) {
				throw new Exception("JsonPayload atteso non presente");
			}
			String base64DecodePayload = new String(Base64Utilities.decode(payload.getBytes()));
			if(!base64DecodePayload.equals(info.getRequest().getJwtClientAssertion().getJsonPayload())) {
				throw new Exception("JsonPayload '"+info.getRequest().getJwtClientAssertion().getJsonPayload()+"' differente da quello atteso '"+base64DecodePayload+"'");
			}
			
			if(jwtClientAssertion.getHeader()==null) {
				throw new Exception("Header atteso non presente");
			}
			if(jwtClientAssertion.getHeader().size()!=4) {
				throw new Exception("Claims attesi nell'header 4, trovati '"+jwtClientAssertion.getHeader().size()+"'");	
			}
			if(!jwtClientAssertion.getHeader().containsKey("alg")){
				throw new Exception("Atteso alg non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getHeader().containsKey("typ")){
				throw new Exception("Atteso typ non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getHeader().containsKey("x5c")){
				throw new Exception("Atteso x5c non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getHeader().containsKey("kid")){
				throw new Exception("Atteso kid non presente tra i claims dell'header");
			}

			if(jwtClientAssertion.getPayload()==null) {
				throw new Exception("Payload atteso non presente");
			}
			if(jwtClientAssertion.getPayload().size()!=8) {
				throw new Exception("Claims attesi nel paylod 8, trovati '"+jwtClientAssertion.getPayload().size()+"'");	
			}
			if(!jwtClientAssertion.getPayload().containsKey("iat")){
				throw new Exception("Atteso iat non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getPayload().containsKey("nbf")){
				throw new Exception("Atteso nbf non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getPayload().containsKey("exp")){
				throw new Exception("Atteso exp non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getPayload().containsKey("jti")){
				throw new Exception("Atteso jti non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getPayload().containsKey("aud")){
				throw new Exception("Atteso aud non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getPayload().containsKey("client_id")){
				throw new Exception("Atteso client_id non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getPayload().containsKey("iss")){
				throw new Exception("Atteso iss non presente tra i claims dell'header");
			}
			if(!jwtClientAssertion.getPayload().containsKey("sub")){
				throw new Exception("Atteso sub non presente tra i claims dell'header");
			}

			System.out.println("Test ClientAssertionJWT ok");
		}
		
		// Test 6
		{
			System.out.println("Test MappingNegoziazioneTokenParser ...");
			
			Properties pMapping = new Properties();
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_TOKEN_TYPE, "TESTtoken_type");
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_ACCESS_TOKEN, "TESTaccess_token,Altro");
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_REFRESH_TOKEN, "TESTrefresh_token");
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_SCOPE, "TESTscope,AltroDiverso");
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_EXPIRES_IN, "TESTexpires_in");
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_EXPIRES_ON, "TESTexpires_on");
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_IN, "TESTrefresh_expires_in");
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_ON, "TESTrefresh_expires_on");
			
			long dateAccess = DateManager.getTimeMillis();
			dateAccess = dateAccess + ((1000l)*(120l)); // aggiungo 120 secondi
			dateAccess = dateAccess / 1000;
			
			Utilities.sleep(1234);
			
			long refreshDateAccess = DateManager.getTimeMillis();
			refreshDateAccess = refreshDateAccess + ((1000l)*(120l)); // aggiungo 120 secondi
			refreshDateAccess = refreshDateAccess / 1000;
						
			String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			String refreshToken = "33ref1676-1f9e-34e2-8515-0cfca111a188";
			String tokenType = "Bearer";
			String rawResponse = "{\"TESTaccess_token\":\""+accessToken+"\",\"TESTrefresh_token\":\""+refreshToken+"\",\"TESTscope\":\"s1 s2\",\"TESTtoken_type\":\""+tokenType+
					"\",\"TESTexpires_in\":3700,\"TESTrefresh_expires_in\":4000,\"TESTexpires_on\":"+dateAccess+",\"TESTrefresh_expires_on\":"+refreshDateAccess+"}";
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.MAPPING, pMapping);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=8) {
				throw new Exception("Claims attesi 8, trovati '"+info.getClaims().size()+"'");	
			}
			if(!info.getClaims().containsKey("TESTaccess_token")){
				throw new Exception("Atteso TESTaccess_token non presente tra i claims");
			}
			if(!info.getClaims().containsKey("TESTrefresh_token")){
				throw new Exception("Atteso TESTrefresh_token non presente tra i claims");
			}
			if(!info.getClaims().containsKey("TESTscope")){
				throw new Exception("Atteso TESTscope non presente tra i claims");
			}
			if(!info.getClaims().containsKey("TESTtoken_type")){
				throw new Exception("Atteso TESTtoken_type non presente tra i claims");
			}
			if(!info.getClaims().containsKey("TESTexpires_in")){
				throw new Exception("Atteso TESTexpires_in non presente tra i claims");
			}
			if(!info.getClaims().containsKey("TESTexpires_on")){
				throw new Exception("Atteso TESTexpires_on non presente tra i claims");
			}
			if(!info.getClaims().containsKey("TESTrefresh_expires_in")){
				throw new Exception("Atteso TESTrefresh_expires_in non presente tra i claims");
			}
			if(!info.getClaims().containsKey("TESTrefresh_expires_on")){
				throw new Exception("Atteso TESTrefresh_expires_on non presente tra i claims");
			}
			
			if(!accessToken.equals(info.getAccessToken())){
				throw new Exception("Atteso access_token '"+accessToken+"' trovato '"+info.getAccessToken()+"'");
			}
			if(!refreshToken.equals(info.getRefreshToken())){
				throw new Exception("Atteso refresh_token '"+refreshToken+"' trovato '"+info.getRefreshToken()+"'");
			}
			
			if(info.getScopes()==null) {
				throw new Exception("Scope attesi non trovato");
			}
			if(info.getScopes().size()!=2) {
				throw new Exception("Scope attesi 2, trovati '"+info.getScopes().size()+"'");
			}
			boolean find = false;
			for (String s : info.getScopes()) {
				if("s1".equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso 's1' non trovato");
			}
			find = false;
			for (String s : info.getScopes()) {
				if("s2".equals(s)) {
					find = true;
					break;
				}
			}
			if(!find) {
				throw new Exception("Scope atteso 's2' non trovato");
			}
			
			if(!tokenType.equals(info.getTokenType())){
				throw new Exception("Atteso token_type '"+tokenType+"' trovato '"+info.getTokenType()+"'");
			}
			
			Date d = info.getExpiresIn();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			Date now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*2));
			if(now.before(d)){
				throw new Exception("(3) Atteso token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			d = info.getRefreshExpiresIn();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) RefreshToken scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) RefreshToken scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*2));
			if(now.before(d)){
				throw new Exception("(3) Atteso refresh token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			
			System.out.println("Test MappingNegoziazioneTokenParser, access token regolare ok");
			
			for (int i = 0; i < 2; i++) {
				
				String tipoTestDateSuffix = (i==0) ? " [formato: number]" : " [formato: exponential E9]";
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on"+tipoTestDateSuffix+" ...");
				
				pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_EXPIRES_IN, "ALTRO_DIVERSO_TESTexpires_in");
				pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_REFRESH_EXPIRES_IN, "ALTRO_DIVERSO_TESTrefresh_expires_in");
				rawResponse = "{\"TESTaccess_token\":\""+accessToken+"\",\"TESTrefresh_token\":\""+refreshToken+"\",\"TESTscope\":\"s1 s2\",\"TESTtoken_type\":\""+tokenType+"\",\"TESTexpires_in\":3700,\"TESTrefresh_expires_in\":4000,\"TESTexpires_on\":"+dateAccess+",\"TESTrefresh_expires_on\":"+refreshDateAccess+"}";
				tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.MAPPING, pMapping);
				info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
				
				if(info.getClaims()==null || info.getClaims().isEmpty()) {
					throw new Exception("Parse non riuscito?");
				}
				if(info.getClaims().size()!=8) {
					throw new Exception("Claims attesi 8, trovati '"+info.getClaims().size()+"'");	
				}
				
				long dataAccessAttesa = dateAccess * 1000;
				d = info.getExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != dataAccessAttesa){
					throw new Exception("Token con scadenza attesa differente, rilevata="+d.getTime()+" attesa="+dataAccessAttesa);
				}
				
				long refreshDateAccessAttesa = refreshDateAccess * 1000;
				d = info.getRefreshExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != refreshDateAccessAttesa){
					throw new Exception("Token con scadenza refresh attesa differente, rilevata="+d.getTime()+" attesa="+refreshDateAccessAttesa);
				}
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on"+tipoTestDateSuffix+" ok");
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on con max long value"+tipoTestDateSuffix+" ...");
				
				Long dataL = Long.MAX_VALUE/1000;
				String dataS = (i==0) ? (dataL+"") : (decimalFormat9.format(dataL));
				rawResponse = "{\"TESTaccess_token\":\""+accessToken+"\",\"TESTrefresh_token\":\""+refreshToken+"\",\"TESTscope\":\"s1 s2\",\"TESTtoken_type\":\""+tokenType+
						"\",\"TESTexpires_in\":3700,\"TESTrefresh_expires_in\":4000,\"TESTexpires_on\":"+dataS+",\"TESTrefresh_expires_on\":"+refreshDateAccess+"}";
				tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.MAPPING, pMapping);
				info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
				
				if(info.getClaims()==null || info.getClaims().isEmpty()) {
					throw new Exception("Parse non riuscito?");
				}
				if(info.getClaims().size()!=8) {
					throw new Exception("Claims attesi 8, trovati '"+info.getClaims().size()+"'");	
				}
				
				dataAccessAttesa = (i==0) ?
						((Long.MAX_VALUE / 1000) * 1000) // nel json ci sono i secondi, quindi si perdono i ms
						:
						Long.MAX_VALUE; // con la serializzazione con esponente non si perde nulla
				d = info.getExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != dataAccessAttesa){
					throw new Exception("Token con scadenza attesa differente, rilevata="+d.getTime()+" attesa="+dataAccessAttesa);
				}
				
				refreshDateAccessAttesa = refreshDateAccess * 1000;
				d = info.getRefreshExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != refreshDateAccessAttesa){
					throw new Exception("Token con scadenza refresh attesa differente, rilevata="+d.getTime()+" attesa="+refreshDateAccessAttesa);
				}
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on con max long value"+tipoTestDateSuffix+" ok");
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on con overflow long value"+tipoTestDateSuffix+" ...");
				
				dataL = Long.MAX_VALUE/1000;
				dataS = (i==0) ? (dataL+"") : (decimalFormat9.format(dataL));
				rawResponse = "{\"TESTaccess_token\":\""+accessToken+"\",\"TESTrefresh_token\":\""+refreshToken+"\",\"TESTscope\":\"s1 s2\",\"TESTtoken_type\":\""+tokenType+
						"\",\"TESTexpires_in\":3700,\"TESTrefresh_expires_in\":4000,\"TESTexpires_on\":11111"+dataS+",\"TESTrefresh_expires_on\":"+refreshDateAccess+"}";
				tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.MAPPING, pMapping);
				info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
				
				if(info.getClaims()==null || info.getClaims().isEmpty()) {
					throw new Exception("Parse non riuscito?");
				}
				if(info.getClaims().size()!=8) {
					throw new Exception("Claims attesi 8, trovati '"+info.getClaims().size()+"'");	
				}
				
				dataAccessAttesa = Long.MAX_VALUE; // overflow
				d = info.getExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != dataAccessAttesa){
					throw new Exception("Token con scadenza attesa differente, rilevata="+d.getTime()+" attesa="+dataAccessAttesa);
				}
				
				refreshDateAccessAttesa = refreshDateAccess * 1000;
				d = info.getRefreshExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != refreshDateAccessAttesa){
					throw new Exception("Token con scadenza refresh attesa differente, rilevata="+d.getTime()+" attesa="+refreshDateAccessAttesa);
				}
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on con overflow long value"+tipoTestDateSuffix+" ok");
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on con max long value in refresh_expires_on"+tipoTestDateSuffix+" ...");
				
				dataL = Long.MAX_VALUE/1000;
				dataS = (i==0) ? (dataL+"") : (decimalFormat9.format(dataL));
				rawResponse = "{\"TESTaccess_token\":\""+accessToken+"\",\"TESTrefresh_token\":\""+refreshToken+"\",\"TESTscope\":\"s1 s2\",\"TESTtoken_type\":\""+tokenType+
						"\",\"TESTexpires_in\":3700,\"TESTrefresh_expires_in\":4000,\"TESTexpires_on\":"+dateAccess+",\"TESTrefresh_expires_on\":"+dataS+"}";
				tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.MAPPING, pMapping);
				info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
				
				if(info.getClaims()==null || info.getClaims().isEmpty()) {
					throw new Exception("Parse non riuscito?");
				}
				if(info.getClaims().size()!=8) {
					throw new Exception("Claims attesi 8, trovati '"+info.getClaims().size()+"'");	
				}
				
				dataAccessAttesa = dateAccess * 1000;
				d = info.getExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != dataAccessAttesa){
					throw new Exception("Token con scadenza attesa differente, rilevata="+d.getTime()+" attesa="+dataAccessAttesa);
				}
				
				refreshDateAccessAttesa = (i==0) ?
						((Long.MAX_VALUE / 1000) * 1000) // nel json ci sono i secondi, quindi si perdono i ms
						:
						Long.MAX_VALUE; // con la serializzazione con esponente non si perde nulla
				d = info.getRefreshExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != refreshDateAccessAttesa){
					throw new Exception("Token con scadenza refresh attesa differente, rilevata="+d.getTime()+" attesa="+refreshDateAccessAttesa);
				}
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on con max long value in refresh_expires_on"+tipoTestDateSuffix+" ok");
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on con overflow long value in refresh_expires_on"+tipoTestDateSuffix+" ...");
				
				dataL = Long.MAX_VALUE/1000;
				dataS = (i==0) ? (dataL+"") : (decimalFormat9.format(dataL));
				rawResponse = "{\"TESTaccess_token\":\""+accessToken+"\",\"TESTrefresh_token\":\""+refreshToken+"\",\"TESTscope\":\"s1 s2\",\"TESTtoken_type\":\""+tokenType+
						"\",\"TESTexpires_in\":3700,\"TESTrefresh_expires_in\":4000,\"TESTexpires_on\":"+dateAccess+",\"TESTrefresh_expires_on\":11111111"+dataS+"}";
				tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.MAPPING, pMapping);
				info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
				
				if(info.getClaims()==null || info.getClaims().isEmpty()) {
					throw new Exception("Parse non riuscito?");
				}
				if(info.getClaims().size()!=8) {
					throw new Exception("Claims attesi 8, trovati '"+info.getClaims().size()+"'");	
				}
				
				dataAccessAttesa = dateAccess * 1000;
				d = info.getExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != dataAccessAttesa){
					throw new Exception("Token con scadenza attesa differente, rilevata="+d.getTime()+" attesa="+dataAccessAttesa);
				}
				
				refreshDateAccessAttesa = Long.MAX_VALUE; 
				d = info.getRefreshExpiresIn();
				if(d==null) {
					throw new Exception("Data di scadenza non trovata");
				}
				if(d.getTime() != refreshDateAccessAttesa){
					throw new Exception("Token con scadenza refresh attesa differente, rilevata="+d.getTime()+" attesa="+refreshDateAccessAttesa);
				}
				
				System.out.println("Test MappingNegoziazioneTokenParser, date on con overflow long value in refresh_expires_on"+tipoTestDateSuffix+" ok");
			}
			
		}
		
		
		
		
		// Test 7
		{
			System.out.println("Test RawNegoziazioneTokenParser ...");
			
			Properties pMapping = new Properties();
			pMapping.put(Costanti.RETRIEVE_TOKEN_PARSER_EXPIRES_IN_SECONDS, "3700");
			
			String rawResponse = "XXXX.YYYY.ZZZZ"; // access token raw
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.RAW, pMapping);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(datiRichiesta, 200, rawResponse, tokenParser);
						
			if(!rawResponse.equals(info.getAccessToken())){
				throw new Exception("Atteso access_token '"+rawResponse+"' trovato '"+info.getAccessToken()+"'");
			}
			
			Date d = info.getExpiresIn();
			if(d==null) {
				throw new Exception("Data di scadenza non trovata");
			}
			Date now = DateManager.getDate();
			if(!now.before(d)){
				throw new Exception("(1) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*1));
			if(!now.before(d)){
				throw new Exception("(2) Token scaduto non atteso, now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}
			now = new Date(now.getTime() + (1000*60*60*2));
			if(now.before(d)){
				throw new Exception("(3) Atteso token scaduto, invece sembre valido? now="+DateUtils.getOldSimpleDateFormatMs().format(now)+" expires_in="+DateUtils.getOldSimpleDateFormatMs().format(d));
			}

			System.out.println("Test RawNegoziazioneTokenParser, access token regolare ok");
		}
	}

}

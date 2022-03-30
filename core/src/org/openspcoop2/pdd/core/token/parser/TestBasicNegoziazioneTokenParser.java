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

package org.openspcoop2.pdd.core.token.parser;

import java.util.Date;

import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;

/**
 * TestBasicNegoziazioneTokenParser
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TestBasicNegoziazioneTokenParser {

	public static void main(String[] args) throws Exception {
		
		
		// Test 1
		{
			System.out.println("Test BasicNegoziazioneTokenParser, access token regolare ...");
			String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			String refreshToken = "33ref1676-1f9e-34e2-8515-0cfca111a188";
			String tokenType = "Bearer";
			String rawResponse = "{\"access_token\":\""+accessToken+"\",\"refresh_token\":\""+refreshToken+"\",\"scope\":\"s1 s2\",\"token_type\":\""+tokenType+"\",\"expires_in\":3700}";
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(200, rawResponse, tokenParser);
			
			if(info.getClaims()==null || info.getClaims().isEmpty()) {
				throw new Exception("Parse non riuscito?");
			}
			if(info.getClaims().size()!=5) {
				throw new Exception("Claims attesi 5, trovati '"+info.getClaims().size()+"'");	
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
			
			System.out.println("Test BasicNegoziazioneTokenParser, access token regolare ok");
		}
		
		// Test 2, senza alcuni campi
		{
			System.out.println("Test BasicNegoziazioneTokenParser, access token senza alcuni campi ...");
			String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			String tokenType = "Bearer";
			String rawResponse = "{\"access_token\":\""+accessToken+"\",\"token_type\":\""+tokenType+"\"}";
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(200, rawResponse, tokenParser);
			
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

		
		// Test 3
		{
			System.out.println("Test BasicNegoziazioneTokenParser, access token con expires_in con max long value e un solo scope ...");
			String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			String tokenType = "Bearer";
			String rawResponse = "{\"access_token\":\""+accessToken+"\",\"scope\":\"s1\",\"token_type\":\""+tokenType+"\",\"expires_in\":9223372036854775}";
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(200, rawResponse, tokenParser);
			
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
			System.out.println("Test BasicNegoziazioneTokenParser, access token con expires_in con max long value e un solo scope ok");
		}

		
		// Test 4
		{
			System.out.println("Test BasicNegoziazioneTokenParser, access token con expires_in con un valore maggiore del max long ...");
			String accessToken = "33aa1676-1f9e-34e2-8515-0cfca111a188";
			String tokenType = "Bearer";
			String rawResponse = "{\"access_token\":\""+accessToken+"\",\"token_type\":\""+tokenType+"\",\"expires_in\":11119223372036854775}";
			BasicNegoziazioneTokenParser tokenParser = new BasicNegoziazioneTokenParser(TipologiaClaimsNegoziazione.OAUTH2_RFC_6749);
			InformazioniNegoziazioneToken info = new InformazioniNegoziazioneToken(200, rawResponse, tokenParser);
			
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
			System.out.println("Test BasicNegoziazioneTokenParser, access token con expires_in con un valore maggiore del max long ok");
		}
	}

}

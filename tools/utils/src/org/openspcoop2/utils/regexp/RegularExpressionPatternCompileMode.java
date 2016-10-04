/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.utils.regexp;


import java.util.regex.Pattern;

/** Enumeration contenente le modalita' di compilazione delle espressioni regolari
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum RegularExpressionPatternCompileMode {

	/** 
	 * Enables canonical equivalence.
	 *
	 * When this flag is specified then two characters will be considered to match if, and only if, 
	 * their full canonical decompositions match. 
	 * The expression "a\u030A", for example, will match the string "?" when this flag is specified. 
	 * By default, matching does not take canonical equivalence into account.
	 *
	 * There is no embedded flag character for enabling canonical equivalence.	
	 *
	 * Specifying this flag may impose a performance penalty. 
	 **/
	CANON_EQ(Pattern.CANON_EQ),
	
	/** 
	 * Enables case-insensitive matching.
	 * 
	 * By default, case-insensitive matching assumes that only characters in the US-ASCII charset are being matched. 
	 * Unicode-aware case-insensitive matching can be enabled by specifying the UNICODE_CASE flag in conjunction (or) 
	 * with this flag.
	 * 
	 * Case-insensitive matching can also be enabled via the embedded flag expression (?i).
	 * 
	 * Specifying this flag may impose a slight performance penalty. 
	 **/
	CASE_INSENSITIVE(Pattern.CASE_INSENSITIVE),
	
	/**
	 * Permits whitespace and comments in pattern.
	 *
	 * In this mode, whitespace is ignored, and embedded comments starting with # are ignored until the end of a line.
	 *
	 * Comments mode can also be enabled via the embedded flag expression (?x). 
	 **/
	COMMENTS(Pattern.COMMENTS),
	
	/**
	 * Enables dotall mode.
	 *
	 * In dotall mode, the expression . matches any character, including a line terminator. 
	 * By default this expression does not match line terminators.
	 * 
	 * Dotall mode can also be enabled via the embedded flag expression (?s). 
	 * (The s is a mnemonic for "single-line" mode, which is what this is called in Perl.) 
	 **/
	DOTALL(Pattern.DOTALL),
	
	/**
	 * Enables literal parsing of the pattern.
	 *
	 * When this flag is specified then the input string that specifies the pattern is treated as a sequence of literal characters. 
	 * Metacharacters or escape sequences in the input sequence will be given no special meaning.
	 *
	 * The flags CASE_INSENSITIVE and UNICODE_CASE retain their impact on matching when used in conjunction with this flag. 
	 * The other flags become superfluous.
	 *
	 * There is no embedded flag character for enabling literal parsing. 
	 **/
	LITERAL(Pattern.LITERAL),
	
	/**
	 * Enables multiline mode.
	 * 
	 * In multiline mode the expressions ^ and $ match just after or just before, respectively, 
	 * a line terminator or the end of the input sequence. 
	 * By default these expressions only match at the beginning and the end of the entire input sequence.
	 *
	 * Multiline mode can also be enabled via the embedded flag expression (?m).
	 **/
	MULTILINE(Pattern.MULTILINE),
	
	/**
	 * Enables Unicode-aware case folding.
	 *
	 * When this flag is specified then case-insensitive matching, when enabled by the CASE_INSENSITIVE flag, 
	 * is done in a manner consistent with the Unicode Standard. 
	 * By default, case-insensitive matching assumes that only characters in the US-ASCII charset are being matched.
	 *
	 * Unicode-aware case folding can also be enabled via the embedded flag expression (?u).
	 *
	 * Specifying this flag may impose a performance penalty. 
	 **/
	UNICODE_CASE(Pattern.UNICODE_CASE),
	
	/** 
	 * Enables Unix lines mode.
	 * 
	 * In this mode, only the '\n' line terminator is recognized in the behavior of ., ^, and $.
	 * 
	 * Unix lines mode can also be enabled via the embedded flag expression (?d).
	 **/
	UNIX_LINES(Pattern.UNIX_LINES);
	
	private int value;

	RegularExpressionPatternCompileMode(int value)
	{
		this.value = value;
	}

	public int getPatternCompileMode()
	{
		return this.value;
	}
	
	@Override
	public String toString(){
		return this.name();
	}
	
	
}

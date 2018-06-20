/**
 * SyntaxHighlighter
 * http://alexgorbatchev.com/SyntaxHighlighter
 *
 * SyntaxHighlighter is donationware. If you are using it, please donate.
 * http://alexgorbatchev.com/SyntaxHighlighter/donate.html
 *
 * @version
 * 3.0.83 (July 02 2010)
 * 
 * @copyright
 * Copyright (C) 2004-2010 Alex Gorbatchev.
 *
 * @license
 * Dual licensed under the MIT and GPL licenses.
 */
;(function()
{
	// CommonJS
	typeof(require) != 'undefined' ? SyntaxHighlighter = require('shCore').SyntaxHighlighter : null;

	function Brush()
	{
		var keywords =	'null';
		
	    var valoriBoolean = 'true false';

		var r = SyntaxHighlighter.regexLib;
		
		this.regexList = [
			{ regex: /"\w+"(?=(\s*\:))/gm,						   		   css: 'keyword' },			// chiavi Json
			{ regex: /(?!("\w+"(\s*\:)\s+))"(\w+\D\s*|\d\D?|\w|(https?\:\/\/.)|\{\\n.*\}*)*"/gm,	   css: 'string' },				// valori Json
			{ regex: /\d+/gm,                                			   css: 'color1' },   			// integers
			{ regex: /(?:0|[1-9][0-9]*)\.[0-9]+/gm,            			   css: 'color1' },   			// decimali
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		   css: 'variable' },		// keywords
			{ regex: new RegExp(this.getKeywords(valoriBoolean), 'gm'),    css: 'color1' }      		// boolean
			];
	
		this.forHtmlScript(r.scriptScriptTags);
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['json'];

	SyntaxHighlighter.brushes.JScript = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();

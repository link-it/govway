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
			{ regex: r.multiLineDoubleQuotedString,						   css: 'string' },				// double quoted strings
			{ regex: r.multiLineSingleQuotedString,						   css: 'string' },				// single quoted strings
			{ regex: /\d+/gm,                                			   css: 'color3' },   			// integers
			{ regex: /(?:0|[1-9][0-9]*)\.[0-9]+/gm,            			   css: 'color3' },   			// decimali
			{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		   css: 'preprocessor' },		// keywords
			{ regex: new RegExp(this.getKeywords(valoriBoolean), 'gm'),    css: 'functions' }      		// constants
			];
	
		this.forHtmlScript(r.scriptScriptTags);
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['json'];

	SyntaxHighlighter.brushes.JScript = Brush;

	// CommonJS
	typeof(exports) != 'undefined' ? exports.Brush = Brush : null;
})();

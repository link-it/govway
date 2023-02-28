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
	SyntaxHighlighter = SyntaxHighlighter || (typeof require !== 'undefined'? require('shCore').SyntaxHighlighter : null);

	function Brush()
	{
		this.regexList = [
			// chiavi  
			{ 
				regex: /"([^\\"\n]|\\.)*" *(?=: *)/g, 
				css: 'keyword' 
			},   			
			// timestamp YYYY-MM-DDTHH:MM:SS.mmm+hh:ss
			{ 
				regex: /"\d{4}\-\d{2}\-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}\+\d{2}:\d{2}"/g,
				css: 'color3' 
			},			
			// email
			{ 
				regex: /"\w+@.+\.\w{1,3}"/g,
				css: 'script' 
			},	 				 
			// valori
			{ 
				regex: /"([^@\\"\n]|\\.)*"/g, 
				css: 'string' 
			},      						
			// int/dec/exp
			{ 
				regex: /-?(0|[1-9]\d*)(\.\d+)?([eE][+-]?\d+)?/g, 
				css: 'color1' 
			},    	
			// chiavi private
			{ 
				regex: /false|true|null|undefined/g, 
				css: 'variable' 
			}
			
		];
		
		this.forHtmlScript(SyntaxHighlighter.regexLib.aspScriptTags);
	};

	Brush.prototype	= new SyntaxHighlighter.Highlighter();
	Brush.aliases	= ['json'];

	SyntaxHighlighter.brushes.Json = Brush;

	// CommonJS
	if(typeof(exports) != 'undefined'){
		exports.Brush = Brush;
	}
})();

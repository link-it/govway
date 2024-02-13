

function createHeatMapChart(chartId, dataString, type, w, h, barwidth) {

    if(dataString.length == 0 || !type) {
        return;
    }

    var chart = document.getElementById(chartId);
    while (chart.firstChild) {
        chart.removeChild(chart.firstChild);
    }

    var dataJson = JSON.parse(dataString);
    var size = { 'w': w, 'h': h };
    generateHeatMapChart(chartId, dataJson, type, size, barwidth);
}

/**
 * Creazione grafico
 * @param id
 * @param _dataJson
 * @param _type
 * @param _size
 * @param _barwidth
 */
function generateHeatMapChart(id, _dataJson, _type, _size, _barwidth) {
	
	this.setPolyfill();

    var dp = chartMapping(_dataJson, _type, _size);
    
    var isInset = false;
    if(dp.noData != 0) {
        //Multi columns legend
        isInset = (dp.rows[0].length>dp.limiteColonneLegenda);
    }

	// disegnamo il grafico con le primitive d3 e non con c3
	let cId = '#' + id;
	let svgId = id + "_svg";
	
	// calcolo margini 
	var margin = {top: 130, right: 160, bottom: 30, left: 70.5};
  	var width = dp.size.w;
  	var	height = dp.size.h;
  	
	// appendo oggetto svg al div scelto
	var svg = d3.select(cId).append("svg")
//  	.attr("width", width + margin.left + margin.right)
//  	.attr("height", height + margin.top + margin.bottom)
	.attr("id", svgId)
  	.attr("width", width)
  	.attr("height", height)
  	.style("overflow", "hidden")
  	.style("font-family", "Roboto")
  	.append("g")
  	.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
  	
  	// Label righe e colonne
	var xLabels = dp.xSeries;
	var yLabels = dp.ySeries;
  	
  	
  	// calcolo eventuale shift verso destra per label asse y troppo lunghe
  	if(dp.noData != 0){
		
		var d = document.getElementById(id);
    	var svgTmp = d.querySelector('svg');
    	
    	var maxw = 0;
		// Calcolo la lunghezza massima delle label dell'asse y
		for (var i = 0; i < yLabels.length; i++) {
			var labelI = yLabels[i];
            
            var _label = document.createElementNS("http://www.w3.org/2000/svg", "text");
	       	_label.setAttributeNS(null,"opacity", 0);
	       	_label.setAttributeNS(null,"font-family", 'Roboto');
	    	_label.setAttributeNS(null,"font-size", 14);
	    	_label.setAttributeNS(null,"text-anchor", 'end');
		     
	     	_label.textContent =labelI;
	        svgTmp.appendChild(_label);
	        
	        var _yAxisWidth = svgTmp.querySelector('text').getBBox().width;
	        
	        if (_yAxisWidth > maxw) maxw = _yAxisWidth;
	        
	         svgTmp.removeChild(_label);
        }
	
		 // shift del grafico verso destra se le label non stanno nello spazio di default
		if(maxw > margin.left) {
			// aggiorno altezza grafico
			let svgIdSharp = "#" + svgId;        
			var g = d3.select(svgIdSharp).select("g");
			g.attr("transform", "translate(" + (maxw + 10) + "," + margin.top + ")");
		}
	}
	
	let asseXValues = xLabels.map(function(item) {
		  	return item.valore;
			});
	
	// Creazione asse X scales and axis:
	var x = d3.scaleBand()
  	.range([ 0, (width - 2 * margin.left)])
  	.domain(asseXValues);
  	
  	// Creo un selettore per l'asse x
	const xAxis = svg.append("g")
		.attr("class", "c3-axis c3-axis-x") // Aggiungo le classi CSS della libreria c3 per riutilizzarne i selettori
	    .attr("transform", "translate(0," + (height - 1.5 * margin.top) + ")")
	    .call(d3.axisBottom(x)
	    	.tickValues(xLabels.map(function(item) { return item.valore; })) // Imposta i valori dei tick sull'asse X
		    .tickFormat(function(valore) {
		        // Trova la label corrispondente al valore x
		        var label = xLabels.find(function(item) {
		            return item.valore === valore;
		        }).label;
		        return label; // Restituisci la label
		    })
	    
	    );
	
	// Controllo se è impostata la visualizzazione delle label non orizzontali
	if (dp.rotation != 0) {
	    xAxis.selectAll("text")
	        .attr("transform", "translate(-10,10)rotate(" + dp.rotation + ")")
	        .style("text-anchor", "end");
	}
	
	// Imposto la dimensione del font
	xAxis.selectAll("text")
	    .style("font-size", 14);
	    
	let asseYValues = yLabels.map(function(item) {
		  	return item.valore;
			});
	    
	// Creazione asse Y scales and axis:
	var y = d3.scaleBand()
	  .range([ (height - 1.5 * margin.top), 0 ])
	  .domain(asseYValues)
//	  .padding(0.01)
	  ;
	  
	svg.append("g")
	  .attr("class", "c3-axis c3-axis-y") // Aggiungo le classi CSS della libreria c3 per riutilizzarne i selettori
	  .call(d3.axisLeft(y))
	  .selectAll("text")
	  .style("font-size", 14)
	  .style("text-anchor", "end")
	  ; 
	    
	if(dp.noData != 0) {
		// larghezza dei singoli rettangoli 
		var _firstTick = svgTmp.querySelector('.c3-axis.c3-axis-x path').getBoundingClientRect().width / xLabels.length;
		// Spazio disponibile in caso di label orizzontali
		var spazioDisponibile = _firstTick - 10; // Sottrai 10 per lasciare spazio per i puntini di sospensione
		
		// se la label e' rotata lo spazio a disposizione si dimezza, ma sfrutto lo spazio sotto l'asse y 
        if(dp.rotation != 0){
			spazioDisponibile = _firstTick /2 + _yAxisWidth - 10;
		}
		
		// larghezza asse y comprese label
		var _yAxisWidth = svgTmp.querySelector('.c3-axis.c3-axis-y').getBBox().width;
//		console.log('Dimensione disponibile per la categoria x: ' + _firstTick);
		
		// visualizzazione orizzontale e obliqua, devo controllare che le label restino all'interno della lunghezza prevista per il rettangolo'
		// Calcolo la lunghezza massima delle label dell'asse x
		var _allXLabels = svgTmp.querySelectorAll('.c3-axis.c3-axis-x text');
		
		_allXLabels.forEach(function(elemento) {
			var testo = elemento.textContent;
		    var lunghezza = elemento.getBoundingClientRect().width;
			    
//		    console.log(testo + ":" + lunghezza);
			    
		    if(lunghezza > spazioDisponibile){
				//console.log(testo + " da accorciare");
		        
		        // Calcola la nuova lunghezza del testo accorciato
		        var testoAccorciato = testo;
		        while (elemento.getBoundingClientRect().width > spazioDisponibile && testoAccorciato.length > 0) {
		            testoAccorciato = testoAccorciato.slice(0, -1); // Rimuovi l'ultimo carattere
		            elemento.textContent = testoAccorciato + "..."; // Aggiungi i puntini di sospensione
		        }
			        
		        // stampa risultato
		        testo = elemento.textContent;
		    	lunghezza = elemento.getBoundingClientRect().width;
//		        console.log(testo + ":" + lunghezza);
				}
			});
		
    	// rotazione delle label dell'asse X
     	if(dp.rotation != 0) {
			var d = document.getElementById(id);
	    	var svgTmp = d.querySelector('svg');
	    	
	    	var _allXLabels = svgTmp.querySelectorAll('.c3-axis.c3-axis-x text');
			
			var _max_cat_height = 0;
			_allXLabels.forEach(function(elemento) {
				var testo = elemento.textContent;
			    var lunghezza = elemento.getBoundingClientRect().width;
			    var altezza = elemento.getBoundingClientRect().height;
			    
//			    console.log(testo + ":" + lunghezza  + ":" + altezza );
			    if(altezza > _max_cat_height) {
					_max_cat_height = altezza;
				}
		    });
	 
	        var _firstText = svgTmp.querySelector('.c3-axis.c3-axis-x text').getBoundingClientRect().width;
	        
	        var _pad = _firstText - (_firstTick/2);
	
	        if(_pad > _yAxisWidth){
	            let padding_left = _pad + _max_cat_height;
	        }
	        
	        let axis_x_height = Math.abs(_max_cat_height*Math.sin(dp.rotation/180*Math.PI));
	        let size_height = dp.size.h + axis_x_height + 50;
	
			// aggiorno altezza grafico
			let svgIdSharp = "#" + svgId;        
			d3.select(svgIdSharp).attr('height', size_height);
		}
	  
	  	// Build color scale
		var myColor = d3.scaleLinear()
	  		.range([dp.heatMapColorRange[0], dp.heatMapColorRange[1]])
	  		.domain([dp.heatMapColorDomain[0],dp.heatMapColorDomain[1]]);
	
		// create a tooltip
	  	var tooltip = d3.select(cId)
		    .append("div")
		    .style("opacity", 0)
		    .attr("class", "c3-tooltip-container")
		    .style("background-color", "white")
		    .style("pointer-events", "none")
		    .style("position", "absolute");

		// Three function that change the tooltip when user hover / move / leave a cell
		var mouseover = function(d) {
			let tooltipHtml = generaTooltip(d, this);
			// imposto l'html cosi da usarlo per calcolare l'eventuale sforamento vers dx
			tooltip.html(tooltipHtml);
	
			// posizione di riferimento del tooltip e' al centro del quadrato
			let boundingBox = this.getBoundingClientRect();
	
			let posX = boundingBox.x + (boundingBox.width/2);
			let posY = boundingBox.y + (boundingBox.height/2);
	
			// sposto il tooltip verso sinistra in caso scavalchi la larghezza massima del grafico
			let maxX = this.parentElement.parentElement.attributes.width.value;
			let tooltipWidth = parseFloat(tooltip.style('width'));
			let diffX = (posX + tooltipWidth) - maxX;
			if(diffX > 0){
				posX -= diffX; 
			}
	
			// visualizzo tooltip e imposto posizione
	    	tooltip.style("opacity", 1)
    			.style("left", posX + "px")
			    .style("top", posY + "px")
    		;
    
    		// imposto bordo nero nel rettangolo selezionato
    		d3.select(this)
    			.style("stroke", "black")
    			.style("opacity", 1)
    		;
  		}

		var mousemove = function(d) {
			// gestione posizione tooltip spostata nel mouse over.
			//tool tip scorre in verticale come negli altri tipi di grafico
//    		tooltip
			//      .style("left", (d3.mouse(this)[0]+70) + "px")
//      		.style("top", (d3.mouse(this)[1]) + "px");
  		}
  
		var mouseleave = function(d) {
			// nascondo il tooltip
    		tooltip.style("opacity", 0);
    		
    		// elimino bordo dal rettangolo selezionato
    		d3.select(this)
      			.style("stroke", "none")
      			.style("opacity", 0.8);
		}
		
		var mouseoverText = function(d) {
			let tooltipHtml = generaTooltip(d, this);
			// imposto l'html cosi da usarlo per calcolare l'eventuale sforamento vers dx
			tooltip.html(tooltipHtml);
	
			// posizione di riferimento del tooltip e' al centro del quadrato
			let boundingBox = this.getBoundingClientRect();
			
			let posX = boundingBox.x + (boundingBox.width/2);
			let posY = boundingBox.y + (boundingBox.height/2);
	
			// sposto il tooltip verso sinistra in caso scavalchi la larghezza massima del grafico
			let maxX = this.parentElement.parentElement.attributes.width.value;
			let tooltipWidth = parseFloat(tooltip.style('width'));
			let diffX = (posX + tooltipWidth) - maxX;
			if(diffX > 0){
				posX -= diffX; 
			}
			
			// visualizzo tooltip e imposto posizione
	    	tooltip.style("opacity", 1)
    			.style("left", posX + "px")
			    .style("top", posY + "px")
    		;
    		
    		// Recupera i dati del rettangolo corrispondente a questo testo
		    const rectData = _dataJson.dati.find(function(rect) {
		        return rect.x === d.x && rect.y === d.y;
		    });
		
			// imposto bordo nero nel rettangolo corrispondente
    		d3.select('rect[data-x="' + decodeHTML(rectData.x) + '"][data-y="' + decodeHTML(rectData.y) + '"]')
    			.style("stroke", "black")
    			.style("opacity", 1);
			}
			
		var mousemoveText = function(d) {
			// gestione posizione tooltip spostata nel mouse over.
			//tool tip scorre in verticale come negli altri tipi di grafico
//    		tooltip
			//      .style("left", (d3.mouse(this)[0]+70) + "px")
//      		.style("top", (d3.mouse(this)[1]) + "px");
  		}
  
		var mouseleaveText = function(d) {
			// nascondo il tooltip
    		tooltip.style("opacity", 0);
    		
    		// Recupera i dati del rettangolo corrispondente a questo testo
		    const rectData = _dataJson.dati.find(function(rect) {
		        return rect.x === d.x && rect.y === d.y;
		    });
    		
    		// elimino bordo dal rettangolo corrispondente
    		d3.select('rect[data-x="' + decodeHTML(rectData.x) + '"][data-y="' + decodeHTML(rectData.y) + '"]')
      			.style("stroke", "none")
      			.style("opacity", 0.8);
		}

		//Read the data		
		//console.log(_dataJson.dati);
		
		// disegno grafico
		svg.selectAll()
      		.data(_dataJson.dati, function(d) {
				return d.xLabel+':'+d.yLabel;
			})
      		.enter()
      		.append("rect")
      		.attr("x", function(d) { 
				return x(decodeHTML(d.x)); 
			})
      		.attr("y", function(d) { 
				return y(decodeHTML(d.y)); 
			})
			.attr("data-x", function(d) { // Aggiungi un attributo data-x
		        return decodeHTML(d.x);
		    })
		    .attr("data-y", function(d) { // Aggiungi un attributo data-y
		        return decodeHTML(d.y);
		    })
			.attr("class", "c3-rect")
      		.attr("width", x.bandwidth() )
      		.attr("height", y.bandwidth() )
      		.style("fill", function(d) { 
				return myColor(d.totale)} 
			)
			.on("mouseover", mouseover)
    		.on("mousemove", mousemove)
    		.on("mouseleave", mouseleave)
			;
			
		// disegno i valori nelle celle se previsto
		// text-anchor="middle" dominant-baseline="middle"
		if(dp.heatMapVisualizzaValori){
			svg.selectAll()
      		.data(_dataJson.dati)
      		.enter()
      		.append("text")
      		.text(function(d){
				if(dp.heatMapVisualizzaValoreZero){
					return d.totale_label;
				} else {
					if(d.totale_label && parseInt(d.totale_label) != 0) { 
						return d.totale_label;
						}
					else {
						return '';
					}	
				}
				})
      		.style("font-family", "Roboto")
      		.style("font-size", 14)
			.attr("text-anchor", "middle" )
			.attr("dominant-baseline", "middle" )
	   		.attr("x", function(d) {
	            // Calcola la coordinata x del centro del rettangolo
	            return x(decodeHTML(d.x)) + (x.bandwidth() / 2);
	        })
	        .attr("y", function(d) {
	            // Calcola la coordinata y del centro del rettangolo
	            return y(decodeHTML(d.y)) + (y.bandwidth() / 2) + 3;
	        })
	        .on("mouseover", mouseoverText)
	        .on("mousemove", mousemoveText)
    		.on("mouseleave", mouseleaveText)
			;
		}	
			
		// creazione della legenda
  		let linearGradient = svg.append("linearGradient").attr("id", "linear-gradient");
  
  		// Gradiente orizzontale
  		linearGradient.attr("x1", "0%").attr("y1", "0%").attr("x2", "100%").attr("y2", "0%");
    		
  		// creazione del gradiente a partire dalle informazioni su minimo e massimo
		linearGradient
	    	.selectAll("stop")
		    .data([{ offset: "0%", color: dp.heatMapColorRange[0] },{ offset: "100%", color: dp.heatMapColorRange[1] }])
		    .enter()
		    .append("stop")
		    .attr("offset", function (d) {
		    	return d.offset;
		    })
		    .attr("stop-color", function (d) {
		      return d.color;
		    });
		    
  		let minLegend = dp.heatMapColorDomain[0];
  		let maxLegend = dp.heatMapColorDomain[1];
  		let legendWidth = width * 0.3;
    	let legendHeight = 8;
		
		// Aggingo il contenitore della legenda
		let svgIdSharp = "#" + svgId;        
		let legendsvg = d3.select(svgIdSharp).append("g")
			.attr("id", "legend")
			.style("font-family", "Roboto")
  		  	.attr("transform", "translate(" + ((dp.size.w /2)) + "," + (margin.top /2) + ")")
		;
  
	  	// disegno il rettangolo della legenda
	  	legendsvg
		    .append("rect")
		    .attr("class", "legendRect")
		    .attr("x", -legendWidth / 2 + 0.5)
		    .attr("y", 10)
		    .attr("width", legendWidth)
		    .attr("height", legendHeight)
		    .style("fill", "url(#linear-gradient)")
		    .style("stroke", "black")
		    .style("stroke-width", "1px");
	  
		  // Scala dei valori per l'asse X della legenda
		  let xScaleLegenda = d3
		    .scaleLinear()
		    .range([0, legendWidth])
		    .domain([minLegend,maxLegend]);
		  
		  let tickValues = dp.heatMapLegendValues.map(function(item) {
		  	return { valore: item.valore, label: item.label };
			});
		  
		  // assegno i valori per le label dell'asse X della legenda		  
		  legendsvg.append("g")
		    .call(d3.axisBottom(xScaleLegenda)
		    		.tickValues(tickValues.map(function(item) { return item.valore; })) // Utilizza i valori x come tick values
				    .tickFormat(function(valore) {
				        // Trova la label corrispondente al valore x
				        var label = tickValues.find(function(item) {
				            return item.valore === valore;
				        }).label;
				        return label; // Restituisci la label
				    })
		    	)
		    	.attr("id", "asseXLegenda")
		    	.attr("transform","translate(" + -legendWidth / 2 + "," + (10 + legendHeight) + ")");
	}
		
	var d = document.getElementById(id);
    var svg = d.querySelector('svg');
    if(svg.style) {
        svg.style.fontFamily = 'Roboto';
    }
    
	var title = document.createElementNS("http://www.w3.org/2000/svg", "text");
    title.setAttributeNS(null,"x",0);
    title.setAttributeNS(null,"y",20);
    title.setAttributeNS(null,"id","title");
    title.setAttributeNS(null,"width", dp.size.w+'px');
    title.setAttributeNS(null,"class","c3-chart-title");

    title.appendChild(document.createTextNode(dp.title));

    svg.appendChild(title);
    var s = title.getBBox();
    title.setAttributeNS(null,"x", (dp.size.w-s.width)/2);

    var subtitle = document.createElementNS("http://www.w3.org/2000/svg", "text");
    subtitle.setAttributeNS(null,"x",0);
    subtitle.setAttributeNS(null,"y",55);
    subtitle.setAttributeNS(null,"id","subtitle");
    subtitle.setAttributeNS(null,"width", dp.size.w+'px');
    subtitle.setAttributeNS(null,"class","c3-chart-subtitle");

    subtitle.appendChild(document.createTextNode(dp.subtitle));

    svg.appendChild(title);
    svg.appendChild(subtitle);
    var s = title.getBBox();
    var st = subtitle.getBBox();
    title.setAttributeNS(null,"x", (dp.size.w-s.width)/2);
    subtitle.setAttributeNS(null,"x", (dp.size.w-st.width)/2);
		
		//No data
    if(dp.noData == 0) {
        var warning = document.createElementNS("http://www.w3.org/2000/svg", "text");
        warning.setAttributeNS(null,"x",0);
        warning.setAttributeNS(null,"y", dp.size.h/2);
        warning.setAttributeNS(null,"id","warning");
        warning.setAttributeNS(null,"width", dp.size.w+'px');
        warning.setAttributeNS(null,"class","c3-chart-nodata");

        warning.appendChild(document.createTextNode(dp.noDataLabel));
        svg.appendChild(warning);
        var wrn = warning.getBBox();
        warning.setAttributeNS(null,"x", (dp.size.w-wrn.width)/2);
    }
    
    
    this.embedFonts(svg);
		
}

function generaTooltip(d, currentCell, defaultTitleFormat, defaultValueFormat, color) {
    var nameFormat = function (name) {
                return name;
            },
        text, title, value, name, bgcolor;
        let tooltipClassName='c3-tooltip';
        let tooltipRowClassName = 'c3-tooltip-name-totale';

        if (!text) {
            title = d.xLabel;
            text = "<table class='" + tooltipClassName + "' style='word-wrap: break-word !important; max-width: 250px;'>" + (title || title === 0 ? "<tr><th colspan='2'>" + title + "</th></tr>" : "");
        }

        name = nameFormat(d.yLabel);
        value =  d.totale_tooltip; 
        bgcolor = currentCell.style.fill;

        text += "<tr class='" + tooltipRowClassName  + "'>";
        text += "<td class='name'><span style='background-color:" + bgcolor + "'></span>" + name + "</td>";
        text += "<td class='value'>" + value + "</td>";
        text += "</tr>";
    return text + "</table>";
}



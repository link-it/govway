function autocomplete(inp, suggestionList) {
  /*the autocomplete function takes two arguments,
  the text field element and an array of possible autocompleted values:*/
  var currentFocus;
  /*execute a function when someone writes in the text field:*/
  inp.addEventListener("input", function(e) {
      var a, b, i, val = this.value;
      /*close any already open lists of autocompleted values*/
      closeAllLists();
      if (!val) { return false;}
      currentFocus = -1;
      /*create a DIV element that will contain the items (values):*/
      a = document.createElement("DIV");
      a.setAttribute("id", this.id + "autocomplete-list");
      a.setAttribute("class", "autocomplete-items");
      /*append the DIV element as a child of the autocomplete container:*/
      this.parentNode.appendChild(a);
      /*for each item in the array...*/
      for (i = 0; i < suggestionList.length; i++) {
    	  // Controllo StartsWith eliminato e sostituito con contains
        /*check if the item starts with the same letters as the text field value:*/
//        if (suggestionList[i].label.substr(0, val.length).toUpperCase() == val.toUpperCase()) {
    	  
    	  // controllo contains
    	if (suggestionList[i].label.toUpperCase().indexOf(val.toUpperCase()) > -1) {
          /*create a DIV element for each matching element:*/
          b = document.createElement("DIV");
          
          // per ora l'highlight e' sospeso
          /*make the matching letters bold:*/
          // b.innerHTML = "<strong>" + suggestionList[i].label.substr(0, val.length) + "</strong>";
          // b.innerHTML += suggestionList[i].label.substr(val.length);
          
          // inserisco il valore che coincide
//          b.innerHTML += suggestionList[i].label;
          b.innerHTML += createHighlightItem(suggestionList[i].label, val);
          
          /*insert a input field that will hold the current array item's value:*/
          b.innerHTML += "<input type='hidden' value='" + suggestionList[i].label + "' name='" + suggestionList[i].value + "'>";
          /*execute a function when someone clicks on the item value (DIV element):*/
          b.addEventListener("click", function(e) {
        	  
        	  /* Navigazione verso la pagina di selezione soggetto*/
        	  visualizzaAjaxStatus();
        	  													        	  
        	  window.location = this.getElementsByTagName("input")[0].name;
        	  
              /*insert the value for the autocomplete text field:*/
              inp.value = this.getElementsByTagName("input")[0].value;
              /*close the list of autocompleted values,
              (or any other open lists of autocompleted values:*/
              closeAllLists();
          });
          a.appendChild(b);
        }
      }
  });
  /*execute a function presses a key on the keyboard:*/
  inp.addEventListener("keydown", function(e) {
      var x = document.getElementById(this.id + "autocomplete-list");
      if (x) x = x.getElementsByTagName("div");
      if (e.keyCode == 40) {
        /*If the arrow DOWN key is pressed,
        increase the currentFocus variable:*/
        currentFocus++;
        /*and and make the current item more visible:*/
        addActive(x);
      } else if (e.keyCode == 38) { //up
        /*If the arrow UP key is pressed,
        decrease the currentFocus variable:*/
        currentFocus--;
        /*and and make the current item more visible:*/
        addActive(x);
      } else if (e.keyCode == 13) {
        /*If the ENTER key is pressed, prevent the form from being submitted,*/
        e.preventDefault();
        if (currentFocus > -1) {
          /*and simulate a click on the "active" item:*/
          if (x) x[currentFocus].click();
        }
      }
  });
  function addActive(x) {
    /*a function to classify an item as "active":*/
    if (!x) return false;
    /*start by removing the "active" class on all items:*/
    removeActive(x);
    if (currentFocus >= x.length) currentFocus = 0;
    if (currentFocus < 0) currentFocus = (x.length - 1);
    /*add class "autocomplete-active":*/
    x[currentFocus].classList.add("autocomplete-active");
  }
  function removeActive(x) {
    /*a function to remove the "active" class from all autocomplete items:*/
    for (var i = 0; i < x.length; i++) {
      x[i].classList.remove("autocomplete-active");
    }
  }
  function closeAllLists(elmnt) {
    /*close all autocomplete lists in the document,
    except the one passed as an argument:*/
    var x = document.getElementsByClassName("autocomplete-items");
    for (var i = 0; i < x.length; i++) {
      if (elmnt != x[i] && elmnt != inp) {
        x[i].parentNode.removeChild(x[i]);
      }
    }
  }
  /*execute a function when someone clicks in the document:*/
  document.addEventListener("click", function (e) {
      closeAllLists(e.target);
  });
  
}
  
function createHighlightItem(test, subString){
  var tokens = occurrences(test,test.toUpperCase(), subString.toUpperCase());
  
  var html = '';
  for(var i=0;i<tokens.length;i++){
	  var token = tokens[i];
	  if(token.highlight) { 
		  html += "<strong>";
	  }
	  html += token.originText;
	  if(token.highlight) {
		  html += "</strong>";
		  }
	  }
	  
	  return html;
  }
  
  /*
    Restituisce una lista di token che corrispondono ai match trovati.
    originTest la stringa originale da dividere
    test la stringa da confrontare
    subString la stringa da cercare
   * */
function occurrences(originTest, test, subString) {
	  var split = [];
	  test += "";
  subString += "";
  if (subString.length <= 0) return split;

  var pos = 0, nuovaPos = 0, step = subString.length;

  while (true) {
//		  console.log('Pos: ' + pos);
	  nuovaPos = test.indexOf(subString, pos);
//		  console.log('NuovaPos: ' + nuovaPos);
	  var tokenString = '';
	  var originTokenString = '';
	  var highlight = false;
	  if (nuovaPos >= 0) {
		  if(nuovaPos == pos) { // metch del token highlight true
			  tokenString = test.substr(pos, step);
			  originTokenString = originTest.substr(pos, step);
			  pos += step;
			  highlight = true;
		  } else {
			  tokenString = test.substr(pos, (nuovaPos - pos));
			  originTokenString = originTest.substr(pos, (nuovaPos - pos));
			  pos = nuovaPos;
		  }
		  if(tokenString) {
//				  console.log('Token: ' + tokenString);
//				  console.log('Highlight: ' + highlight);
			  var item = {};
			  item.text = tokenString;
			  item.highlight = highlight;
			  item.originText = originTokenString;
			  split.push(item);
		  }
	  } else {
		  tokenString = test.substr(pos);
		  originTokenString = originTest.substr(pos);
		  if(tokenString) {
//				  console.log('Ultimo Token: ' + tokenString);
//				  console.log('Highlight: ' + highlight);
			  var item = {};
			  item.text = tokenString;
			  item.highlight = highlight;
			  item.originText = originTokenString;
			  split.push(item);
		  }
		  break;
	  }
  }
   
  return split;
}

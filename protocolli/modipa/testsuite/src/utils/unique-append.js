function uniqueAppend(array, elements) {
  elements.forEach(function(element) {
    // Verifica se l'array contiene già un elemento con lo stesso nome
    if (!array.some(e => e.name === element.name)) {
      // Aggiungi l'elemento all'array se non è già presente
      array.push(element);
    }
  });
  return array;
}


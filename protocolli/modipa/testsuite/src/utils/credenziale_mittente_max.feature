Feature:

Background:
* configure callSingleCache = { enabled: false }
* def calcolaValore =
"""
function() {
  var result = karate.callSingle('classpath:utils/credenziale_mittente.js', 'get_max_id_credenziale');
  karate.log('Risultato calcolato MAX:', result);
  return result;
}
"""

Scenario:
* def max_id_credenziale = calcolaValore()

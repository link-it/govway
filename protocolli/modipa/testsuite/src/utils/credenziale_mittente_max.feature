Feature:

Background:
* configure callSingleCache = { enabled: false }
* def calcolaValore =
* def get_max_id_credenziale = read('classpath:utils/get_max_id_credenziale.js')

Scenario:
* def max_id_credenziale = get_max_id_credenziale()

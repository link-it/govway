from mitmproxy import http
import base64

# Sostituisci con il nome utente e la password che desideri
USERNAME = "UTENTEGOVWAY"
PASSWORD = "PASSWORDGOVWAY"
AUTH_HEADER = f"Basic {base64.b64encode(f'{USERNAME}:{PASSWORD}'.encode()).decode()}"

# Determina quale classe usare per creare la risposta (dipende dalla versione di mimproxy installata)
try:
    # Tentativo di utilizzare HTTPResponse (per versioni più recenti)
    from mitmproxy.http import HTTPResponse
    ResponseClass = HTTPResponse
except ImportError:
    # Fallback a Response (per versioni più vecchie)
    from mitmproxy.http import Response
    ResponseClass = Response

def request(flow: http.HTTPFlow) -> None:
    # Controlla se l'header Authorization è presente e se corrisponde alle credenziali
    auth_header = flow.request.headers.get("Proxy-Authorization")
    
    if auth_header != AUTH_HEADER:
        # Se non corrisponde, restituisci una risposta di errore
        flow.response = ResponseClass.make(
            407,  # Codice di errore per autenticazione richiesta
            b"",
            {
                "Proxy-Authenticate": 'Basic realm="Access to proxy"'
            }
        )

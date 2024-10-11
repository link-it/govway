from mitmproxy import http
import base64

# Sostituisci con il nome utente e la password che desideri
USERNAME = "UTENTEGOVWAY"
PASSWORD = "PASSWORDGOVWAY"
AUTH_HEADER = f"Basic {base64.b64encode(f'{USERNAME}:{PASSWORD}'.encode()).decode()}"

def request(flow: http.HTTPFlow) -> None:
    # Controlla se l'header Authorization è presente e se corrisponde alle credenziali
    auth_header = flow.request.headers.get("Proxy-Authorization")
    
    if auth_header != AUTH_HEADER:
        # Se non corrisponde, restituisci una risposta di errore
        flow.response = http.Response.make(
            407,  # Codice di errore per autenticazione richiesta
            b"",
            {
                "Proxy-Authenticate": 'Basic realm="Access to proxy"'
            }
        )

<% 

String scheme = request.getScheme();             // http
String serverName = request.getServerName();     // hostname.com
int serverPort = request.getServerPort();        // 80
String contextPath = request.getContextPath();   // /mywebapp

// Reconstruct original requesting URL
StringBuilder url = new StringBuilder();
url.append(scheme).append("://").append(serverName);

if (serverPort != 80 && serverPort != 443) {
    url.append(":").append(serverPort);
}

url.append(contextPath).append("/public/timeoutPage.jsf");

// response.sendRedirect(request.getContextPath()+"/pages/welcome.jsf");
response.sendRedirect(url.toString());

%>
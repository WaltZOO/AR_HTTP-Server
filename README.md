# AR_HTTP-Server
[**Maxime BOSSANT**](https://github.com/WaltZOO/) - [**Maxence MAURY**](https://github.com/MixiMaxiMouse)

## Project Overview

This project implements a multi-threaded HTTP server in Java, capable of serving both static HTML pages and dynamic content via "ricmlets" (lightweight Java servlets). The server follows the HTTP protocol (GET requests only), manages cookies, and supports user sessions.

## Steps and Features

### 1. Serving Static Pages

- **Description:**  
  The server handles GET requests for static resources (e.g., HTML, images), responding with appropriate HTTP headers.
- **Key Classes:**  
  - `httpserver.itf.HttpRequest`  
  - `httpserver.itf.HttpResponse`  
  - `httpserver.itf.impl.HttpServer`  
  - `httpserver.itf.impl.HttpResponseImpl`  
  - `httpserver.itf.impl.HttpStaticRequest`
- **Files Location:**  
  - `src/httpserver/itf/`  
  - `src/httpserver/itf/impl/`
- **How to test:**  
  Start the server and use a browser or `wget` to access static files (e.g., `http://localhost:<port>/FILES/hello.html`).

### 2. Serving Dynamic Pages (Ricmlets)

- **Description:**  
  The server dispatches requests for `/ricmlets/...` to Java classes implementing dynamic responses.
- **Key Classes:**  
  - `httpserver.itf.impl.HttpRicmletRequestImpl`  
  - `httpserver.itf.impl.HttpRicmletResponseImpl`  
  - Example ricmlets: `examples.HelloRicmlet`, `examples.CountRicmlet`
- **Files Location:**  
  - Ricmlet classes: `src/examples/`  
  - Server logic: `src/httpserver/itf/impl/`
- **How to test:**  
  Access URLs like `http://localhost:<port>/ricmlets/examples/HelloRicmlet`.

### 3. Cookies Support

- **Description:**  
  The server can read and set cookies, enabling persistent client-side data between requests.
- **Key Classes:**  
  - `HttpRicmletRequestImpl#getCookie(String name)`  
  - `HttpRicmletResponseImpl#setCookie(String name, String value)`
- **Files Location:**  
  - `src/httpserver/itf/impl/`
- **How to test:**  
  Try the `MyFirstCookieRicmlet` in `src/examples/`.

### 4. Session Management

- **Description:**  
  Implements sessions using cookies to associate data with each client, destroyed after inactivity.
- **Key Classes:**  
  - `Session` (implements `HttpSession`)
- **Files Location:**  
  - `src/httpserver/itf/impl/`
- **How to test:**  
  Use `CountBySessionRicmlet`:  
  `http://localhost:<port>/ricmlets/examples/CountBySessionRicmlet`  
  Test with different browsers to ensure session isolation.

## How to Run

1. **Compile:**  
   Compile all Java files in `src/`.
2. **Start Server:**  
   Run the server main class, specifying the base directory if needed.
3. **Test:**  
   Use a web browser or `wget` to access static and dynamic URLs as described above.

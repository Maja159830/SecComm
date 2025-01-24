# SecureComm

SecureComm to aplikacja napisana w Javie przy użyciu Spring Boot. Daj͏e moż͏liwość szyfrowania i odszyfro͏wywania wiadomoś͏ci przez algoryt͏m AES w trybie CBC a także sprawdzania integralności ͏danych za pomocą͏ HMAC.

## Funkcjonalności
- Szyfrowanie wiadomości tekstowych z użyciem AES-256 w trybie CBC.
- Generowanie i weryfikacja HMAC w celu ochrony przed manipulacją danych.
- Deszyfrowanie zaszyfrowanych wiadomości z dbałością o b͏ezpieczeństwo.

## Technologie
- Java 21
- Spring Boot 3.x
- AES-256 (tryb CBC z dopełnieniem PKCS5Padding)
- HMAC-SHA256

## Wymagania
- Java 21
- Maven
- Narzędzie do testowania API (ja używałam Postman)

## Korzystanie z aplikacji
Aplikacja udostępnia dwa endpointy:

### 1. Szyfrowanie wiadomości
- **Endpoint:** `POST /api/encrypt`
- **Body (JSON):**
  ```json
  {
    "message": "Wiadomośc do zaszyfrowania"
  }
  ```
- **Przykładowa odpowiedź:**
  ```json
  {
    "encrypted": "<zaszyfrowana wiadomość>",
    "hmac": "<wygenerowany HMAC>"
  }
  ```

### 2. Deszyfrowanie wiadomości
- **Endpoint:** `POST /api/decrypt`
- **Body (JSON):**
  ```json
  {
    "encrypted": "<zaszyfrowana wiadomość>",
    "hmac": "<wygenerowany HMAC>"
  }
  ```
- **Przykładowa odpowiedź:**
  ```json
  {
    "decrypted": "oryginalna wiadomość"
  }
  ```

### Przykłady użycia (Postman)
1. W Postmanie stwórz nowy request typu **POST**.
2. Ustaw URL na `http://localhost:8080/api/encrypt` (szyfrowanie) lub `http://localhost:8080/api/decrypt` (odszyfrowywanie).
3. Przejdź do sekcji **Body**, wybierz opcję **raw** i ustaw format na **JSON**.
4. Wprowadź odpowiednie dane wejściowe i wyślij żądanie.

## Podatność na atak i zabezpieczenie
Aplikacja była podatna na atak typu **Padding Oracle**, który polega na zmianie szyfrogramu w celu uz͏yskania orygi͏nal͏nej wiadomości.

### 1. Prezentacja ataku
1. Atakujący przechwytuje szyfrogram.
2. Manipuluje jego zawartością i wysyła zmodyfikowane żądania do endpointu `/api/decrypt`.
3. Na podstawie odpowiedzi serwera odtwarza oryginalną wiadomość.

### 2. Zabezpieczenie przed atakiem
- **Ujednolicenie komunikatów o błędach**: Serwer zwraca ogólny komunikat "Niepoprawne dane wejściowe", nie wskazuje na rodzaj błędu.
- **Weryfikacja HMAC**: Każda wiadomość jest zabezpieczona HMAC, który uniemożliwia manipulację szyfrogramem. Jeśli HMAC jest nieprawidłowy, deszyfrowanie nie jest wykonywane.

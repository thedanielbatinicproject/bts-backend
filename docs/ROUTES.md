# BTS Backend API – Spec (v1)

Datum: 6.3.2026.
Verzija: v1  
Baza: Spring Boot + PostgreSQL  
Auth: Admin (Spring Session), Game submit (HMAC signature)  
Chapteri: fiksno 7 (1–7) → **ne postoji `chapters` resurs u bazi**, ali postoji “view” ruta `GET /chapters/{chapterNumber}/puzzles`.

---

## 0) Konvencije

### Base URL
Sve rute su pod:
- `/api/v1`

### Paginacija (Spring default)
- `page`: **0-based** (Spring `Pageable` default)
- `size`: broj elemenata po stranici
    - default: `50`
    - max: `200` 

Primjer:
- `GET /api/v1/news?page=0&size=50`

### Standard error format
Svi errori vraćaju JSON u formatu:

```json
{
  "code": "ERROR_CODE",
  "message": "Human readable message",
  "details": {
    "field": "timeMs"
  }
}
```

Preporučeni statusi:
- `400` invalid input
- `401` nije autentificiran (admin)
- `403` nema prava (admin)
- `404` resurs ne postoji
- `409` konflikt (npr. duplicate puzzleCode na create)
- `500` internal error

### Validacije
- `chapterNumber` mora biti u rasponu `1..7`
- `timeMs` mora biti integer u rasponu:
    - `1 <= timeMs <= 86 400 000` (24h)

---

## 1) Public/Game API (bez autentikacije)

### 1.1 Puzzles (content)

#### GET `/api/v1/puzzles`
**Opis:** Paginirani popis svih puzzleova.  
**Query (opcionalno):**
- `chapterNumber` (int, 1..7)
- `page`, `size`

**Primjena:** web i igra mogu fetchati sve puzzleove ili filter po chapteru.

---

#### GET `/api/v1/puzzles/{puzzleCode}`
**Opis:** Dohvat jednog puzzla po globalno jedinstvenom `puzzleCode`.

---

#### GET `/api/v1/chapters/{chapterNumber}/puzzles`
**Opis:** Dohvat svih puzzleova za odabrani chapter (chapter je broj, 1–7).  
**Napomena:** Chapter nije entitet u bazi; ovo je “view” endpoint filtriran po `chapterNumber`.

---

### 1.2 Leaderboards

#### GET `/api/v1/puzzles/{puzzleCode}/leaderboard`
**Opis:** Paginirani leaderboard za puzzle.  
**Query:** `page`, `size`  
**Sort (server-side):** `timeMs ASC`, tie-breaker `updatedAt ASC` (ili `createdAt`).

**Response entry sadrži:**
- `steamId`
- `username`
- `timeMs`
- `updatedAt`
- `rank`

---

#### GET `/api/v1/puzzles/{puzzleCode}/leaderboard/top?limit=100` *(opcionalno)*
**Opis:** Brzi dohvat top N rezultata bez paginacije  
**Query:**
- `limit` (default npr. 100, max 200)

---

#### GET `/api/v1/puzzles/{puzzleCode}/leaderboard/around/{steamId}?radius=5` *(opcionalno)*
**Opis:** Vraća igrača i određeni broj rezultata iznad/ispod njega (UX “my rank”).  
**Query:**
- `radius` (default npr. 5, max npr. 50)

---

### 1.3 Players & ostvarena vremena (javni pristup)

#### GET `/api/v1/players/{steamId}`
**Opis:** Dohvat minimalnih podataka o playeru.  
**Player identitet:** `steamId` je primarni identifikator.  
**Napomena:** `username` se može updateati kroz submit.

---

#### GET `/api/v1/players/{steamId}/times`
**Opis:** Paginirani dohvat svih vremena tog playera za sve zagonetke koje ima u zapisu.  
**Query:** `page`, `size`

**Tipično vraća:**
- `puzzleCode`
- `chapterNumber`
- `timeMs`
- `updatedAt`

---

#### GET `/api/v1/players/{steamId}/times/{puzzleCode}` *(opcionalno)*
**Opis:** Dohvat vremena playera za točno jedan puzzle (ako postoji).

---

### 1.4 Submit vremena iz igre (jedna primarna ruta)

#### PUT `/api/v1/puzzles/{puzzleCode}/time`
**Opis:** Upsert vremena za kombinaciju `(steamId, puzzleCode)`:
- Ako entry postoji → overwrite (uvijek, bez obzira je li bolje/lošije)
- Ako entry ne postoji → create

**Body (primjer):**
```json
{
  "steamId": "76561198000000000",
  "username": "MySteamName",
  "timeMs": 123456,
  "clientTimestamp": 1760000000000,
  "signature": "BASE64_OR_HEX_SIGNATURE"
}
```

**Server-side pravila:**
- Validira da puzzle postoji po `puzzleCode`
- Validira `timeMs` (1..24h)
- Verificira `signature` (HMAC, vidi poglavlje 4)
- Upsert leaderboard entry (jedan zapis po playeru po puzzleu)
- Kreira playera ako ne postoji
- Updatea `player.username` ako se promijenio

**Response:**
- `200 OK` kad je update
- `201 Created` kad je create

---

#### POST `/api/v1/times:batch` *(opcionalno, preporučeno za offline sync)*
**Opis:** Batch submit više zapisa odjednom (offline mode).  
**Body (primjer):**
```json
{
  "entries": [
    {
      "puzzleCode": "3-PZ-01",
      "steamId": "76561198000000000",
      "username": "MySteamName",
      "timeMs": 123456,
      "clientTimestamp": 1760000000000,
      "signature": "..."
    }
  ]
}
```

**Napomena:** Potpis je po entryju.

---

## 2) Javni API za novosti (read-only)

#### GET `/api/v1/news`
**Opis:** Paginirani popis vijesti.  
**Query:** `page`, `size`

---

#### GET `/api/v1/news/{newsId}`
**Opis:** Dohvat jedne vijesti po ID-u.

---

## 3) Admin API (Spring Session)

Base: `/api/v1/admin`  
Svi endpoints ispod zahtijevaju aktivnu session cookie.

### 3.1 Admin session (login/logout)

#### POST `/api/v1/admin/session`
**Opis:** Login admina ili superadmina.  
**Body (primjer):**
```json
{
  "username": "admin1",
  "password": "secret"
}
```

**Response:** `204 No Content` ili `200 OK` (po preferenciji) + postavlja session cookie.

---

#### GET `/api/v1/admin/session`
**Opis:** Vraća podatke o trenutno ulogiranom useru (npr. username + role).  
**Response (primjer):**
```json
{
  "username": "admin1",
  "role": "ADMIN"
}
```

---

#### DELETE `/api/v1/admin/session`
**Opis:** Logout (invalidate session).

---

### 3.2 Admin user management (samo SUPERADMIN)

> Pravilo: postoji “superadmin” user koji se ne može brisati.  
> Superadmin može kreirati admin usere. Admini ne mogu kreirati/brisati druge admine.

#### POST `/api/v1/admin/admin-users`
**Opis:** Kreira novog admin usera.  
**Body (primjer):**
```json
{
  "username": "admin2",
  "password": "secret"
}
```

---

#### GET `/api/v1/admin/admin-users`
**Opis:** Listanje admin usera (paginirano).  
**Query:** `page`, `size`

---

#### PATCH `/api/v1/admin/admin-users/{adminUserId}`
**Opis:** Update admin usera (npr. promjena passworda ili enable/disable).

---

#### DELETE `/api/v1/admin/admin-users/{adminUserId}`
**Opis:** Briše admin usera (nije dozvoljeno brisati superadmina).

---

### 3.3 Puzzle management (ADMIN + SUPERADMIN)

#### POST `/api/v1/admin/puzzles`
**Opis:** Kreira puzzle.  
**Body (primjer):**
```json
{
  "puzzleCode": "3-PZ-01",
  "chapterNumber": 3,
  "title": "Puzzle title (optional)",
  "description": "Optional"
}
```

**Validacije:**
- `chapterNumber` u `1..7`
- `puzzleCode` globalno jedinstven

---

#### GET `/api/v1/admin/puzzles`
**Opis:** Admin listanje puzzleova (paginirano, s filterima po želji).  
**Query (opcionalno):**
- `chapterNumber`
- `page`, `size`

---

#### GET `/api/v1/admin/puzzles/{puzzleCode}`
**Opis:** Admin dohvat puzzla.

---

#### PATCH `/api/v1/admin/puzzles/{puzzleCode}`
**Opis:** Edit puzzla (chapterNumber/title/description/active/order, itd.).  
**Preporuka:** Ne mijenjati `puzzleCode` nakon objave u igri.

---

#### DELETE `/api/v1/admin/puzzles/{puzzleCode}`
**Opis:** Briše puzzle.  
**Napomena:** zabrani brisanje ako postoje entryji.

---

### 3.4 Leaderboard moderation (ADMIN + SUPERADMIN)

#### DELETE `/api/v1/admin/puzzles/{puzzleCode}/times/{steamId}`
**Opis:** Briše jedan leaderboard entry (vrijeme jednog playera za jedan puzzle).

---

#### GET `/api/v1/admin/puzzles/{puzzleCode}/times` *(opcionalno)*
**Opis:** Admin pregled svih entryja za puzzle (paginirano).  
**Query:** `page`, `size`

---

### 3.5 Player moderation (ADMIN + SUPERADMIN)

#### GET `/api/v1/admin/players` *(opcionalno)*
**Opis:** Admin listanje playera (paginirano).  
**Query:** `page`, `size`

---

#### GET `/api/v1/admin/players/{steamId}`
**Opis:** Admin dohvat playera + sažetak (npr. broj zapisa vremena).

---

#### DELETE `/api/v1/admin/players/{steamId}`
**Opis:** Briše playera i sva njegova vremena (cascade).

---

### 3.6 News management (ADMIN + SUPERADMIN)

#### POST `/api/v1/admin/news`
**Opis:** Kreira vijest. Automatski je “published”.  
**Body (primjer):**
```json
{
  "title": "Patch 1.0",
  "description": "Opis vijesti...",
  "imageUrl": "https://example.com/image.png"
}
```

**Server-side:**
- sprema `createdByAdmin`
- timestamps: `createdAt`, `updatedAt`

---

#### GET `/api/v1/admin/news`
**Opis:** Admin listanje vijesti (paginirano).  
**Query:** `page`, `size`

---

#### GET `/api/v1/admin/news/{newsId}`
**Opis:** Admin dohvat jedne vijesti.

---

#### PATCH `/api/v1/admin/news/{newsId}`
**Opis:** Edit vijesti (title/description/imageUrl).

---

#### DELETE `/api/v1/admin/news/{newsId}`
**Opis:** Brisanje vijesti.

---

## 4) Game submit signature (HMAC)

### Cilj
Onemogućiti “klasične korisnike” da ručno šalju vremena (Postman/curl) bez znanja tajne.

> Napomena: Ovo nije savršena anti-cheat zaštita (reverse engineering je moguć), ali je dovoljno da spriječi trivijalno spoofanje.

### Algoritam
- HMAC-SHA256
- Tajna: `GAME_SECRET` (konfiguracija samo na backendu + u igri)
- Output encoding:
    - `hex`

### Canonical string (preporuka)
Potpisivati sve ove vrijednosti (redoslijed je bitan):

```
steamId:puzzleCode:timeMs:clientTimestamp
```

Primjer:
```
76561198000000000:3-PZ-01:123456:1760000000000
```

### Validacije na serveru (preporuka)
- `clientTimestamp`:
    - ne smije biti previše u budućnosti (npr. > +5 min)
    - može biti stariji (offline), ali postavi razumnu granicu (npr. max 30 dana unazad) po preferenciji
- opcionalno: dodati `requestId` i čuvati ga kratko (replay prevention)

---
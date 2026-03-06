# BTS Backend

Backend servis za **web stranicu igre** i **spremanje leaderboard vremena** za svaku zagonetku (puzzle).

Ovaj projekt služi za:
- **Landing / web sadržaj** (npr. vijesti/patch notes koje frontend prikazuje)
- **Javni leaderboard** po svakoj zagonetki (puzzle)
- **Spremanje vremena iz igre** (igra šalje `steamId`, `puzzleCode`, `timeMs` + signature)
- **Admin panel API** (session login) za upravljanje puzzleovima, brisanje vremena/playera i uređivanje vijesti

## Ukratko o igri

Igra je **story linearna** (cutscene + walking simulator) sa **zagonetkama**.  
Za svaku zagonetku igra mjeri vrijeme rješavanja i šalje ga backendu, gdje se taj rezultat sprema u leaderboard.

- Playeri se **ne logiraju** na web
- Player se identificira preko **Steam ID-ja**
- Svaki player može imati **samo jedno vrijeme po puzzleu** (novo vrijeme uvijek overwritea staro)

## Dokumentacija ruta

Detaljan popis REST ruta nalazi se ovdje:
- [ROUTES.md](./ROUTES.md)

## Tehnologije
- Java (Spring Boot)
- PostgreSQL

---
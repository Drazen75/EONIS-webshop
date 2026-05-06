# FurnitureStore - Veb Prodavnica Nameštaja

## Tech Stack
- **Backend**: Java 17 + Spring Boot 3.2 + Spring Security (JWT) + JPA (Code First)
- **Frontend**: Angular 17 + Angular Material
- **Baza**: PostgreSQL 16
- **Plaćanje**: Stripe Checkout + Webhook
- **Deploy**: Docker Compose

## Podešavanje i pokretanje

### 1. Klonirajte .env fajl
```bash
cp .env.example .env
```

### 2. Podesite Stripe ključeve u `.env`
Nabavite test ključeve na: https://dashboard.stripe.com/test/apikeys

```env
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
```

### 3. Pokrenite Docker Compose
```bash
docker-compose up --build
```

Aplikacija je dostupna na:
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080/api
- **PostgreSQL**: localhost:5432

### 4. Stripe Webhook (lokalno testiranje)

Instalirajte Stripe CLI: https://stripe.com/docs/stripe-cli

```bash
stripe listen --forward-to http://localhost:8080/api/webhook/stripe
```

Kopirajte `whsec_...` iz izlaza i stavite u `.env` kao `STRIPE_WEBHOOK_SECRET`.

---

## Demo nalozi (kreiraju se automatski pri startu)

| Email                  | Lozinka        | Uloga    |
|------------------------|----------------|----------|
| admin@furniture.com    | Admin123!      | ADMIN    |
| customer@test.com      | Customer123!   | CUSTOMER |

---

## Stripe test kartice

| Kartica            | Broj                |
|--------------------|---------------------|
| Uspešno plaćanje   | 4242 4242 4242 4242 |
| Odbijeno plaćanje  | 4000 0000 0000 0002 |

Datum: bilo koji u budućnosti, CVC: bilo koja 3 cifre.

---

## API Endpoints

### Auth
| Method | URL                  | Opis                  | Auth     |
|--------|----------------------|-----------------------|----------|
| POST   | /api/auth/register   | Registracija          | Public   |
| POST   | /api/auth/login      | Prijava (JWT)         | Public   |
| GET    | /api/auth/me         | Trenutni korisnik     | JWT      |

### Proizvodi
| Method | URL                        | Opis                  | Auth     |
|--------|----------------------------|-----------------------|----------|
| GET    | /api/products              | Lista (paginacija)    | Public   |
| GET    | /api/products/{id}         | Detalji               | Public   |
| POST   | /api/products              | Kreiraj               | ADMIN    |
| PUT    | /api/products/{id}         | Izmeni                | ADMIN    |
| DELETE | /api/products/{id}         | Deaktiviraj           | ADMIN    |

### Korpa
| Method | URL                        | Opis                  | Auth     |
|--------|----------------------------|-----------------------|----------|
| GET    | /api/cart                  | Prikaz korpe          | JWT      |
| POST   | /api/cart/items            | Dodaj u korpu         | JWT      |
| PUT    | /api/cart/items/{pid}      | Izmeni količinu       | JWT      |
| DELETE | /api/cart/items/{pid}      | Ukloni iz korpe       | JWT      |

### Porudžbine
| Method | URL                        | Opis                  | Auth     |
|--------|----------------------------|-----------------------|----------|
| POST   | /api/orders/checkout       | Stripe Checkout       | JWT      |
| GET    | /api/orders                | Moje porudžbine       | JWT      |
| GET    | /api/orders/{id}           | Detalji porudžbine    | JWT      |

### Admin
| Method | URL                             | Opis               |
|--------|---------------------------------|--------------------|
| GET    | /api/admin/users                | Lista korisnika    |
| GET    | /api/admin/transactions         | Transakcije        |
| GET    | /api/orders/admin/all           | Sve porudžbine     |
| PUT    | /api/orders/admin/{id}/status   | Izmeni status      |

### Webhook
| Method | URL                    | Opis                    |
|--------|------------------------|-------------------------|
| POST   | /api/webhook/stripe    | Stripe Checkout event   |

---

## Business logika
- Nije moguće dodati u korpu više od raspoloživih zaliha
- Zalihe se smanjuju automatski nakon uspešnog plaćanja (Stripe webhook)
- Admin može menjati status porudžbine
- Soft-delete za proizvode (deaktivacija, ne brisanje)
- Korisnici se deaktiviraju, ne brišu

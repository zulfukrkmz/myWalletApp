Not: Api olusturma, h2 database, log yapısı, transaction yapısı, dto-entity-repository sınıflarını tamamlayabildim. Hafta sonu özel işlerimden dolayı müsaitlik durumu olmadığından kod geliştirmesinde bulunamadım. 1.5 günde tamamladığım kısmını paylaşıyorum.
Ek olarak yapılması gerekenler Authorization yapısı, Security yapısı(bunu mevcut şirketimde devopscu arkadaslar yönetiyor.), Unit testler. Bunlar tamamlanmadan Test ortamına çıkılması mümkün değil ama dev ortamına çıkıp dev test yapılabilir haline yakın birşey yapmış olsam da
tamamladığım kadar kısmını göndermek istedim. Değerlendirirken kullandığınız zaman için teşekkür ederim.

# Wallet Management API

Bu proje, kullanıcıların birden fazla cüzdan oluşturabildiği ve cüzdanlar arası para yatırma / çekme gibi işlemleri gerçekleştirebildiği bir RESTful servistir. Tüm işlemler transaction bazlıdır ve işlem geçmişi tutulur.

## Kullanılan Teknolojiler

- Java 21  
- Spring Boot  
- Spring Data JPA  
- Hibernate  
- Lombok  
- H2 (dev ve test ortamları için)  
- SLF4J + Logback (loglama)

## Proje Yapısı

- `WalletService`: Cüzdan işlemleri için tanımlanmış servis arayüzü.
- `WalletServiceImpl`: Cüzdan oluşturma, listeleme, para yatırma, çekme ve işlem geçmişini dönen metotlar burada.
- `Customer`, `Wallet`, `Transaction`: Temel JPA Entity'leri.
- `WalletRepository`, `CustomerRepository`, `TransactionRepository`: Spring Data arayüzleri.
- DTO'lar: Request ve response modellemeleri ayrı tutuldu (örneğin `CreateWalletRequest`, `WalletResponse`).

## API Endpointleri ve Örnekler

### 1. Yeni Cüzdan Oluştur
**POST** `/api/wallets`

**Request Body:**
```json
{
  "customerId": 1,
  "walletName": "Main Wallet",
  "currency": "TRY",
  "activeForShopping": true,
  "activeForWithdraw": true
}
```

**Response:**
```json
{
  "id": 1,
  "walletName": "Main Wallet",
  "currency": "TRY",
  "balance": 0,
  "usableBalance": 0,
  "activeForShopping": true,
  "activeForWithdraw": true
}
```

---

### 2. Yeni İşlem (Deposit) Yap
**POST** `/api/wallets/{walletId}/deposit`

**Request Body:**
```json
{
  "amount": 1000,
  "walletId": 1,
  "source": "IBAN123456",
  "sourceType": "IBAN"
}
```

**Response:**
```json
{
  "id": 1,
  "amount": 1000,
  "type": "DEPOSIT",
  "oppositePartyType": "IBAN",
  "oppositeParty": "IBAN123456",
  "status": "APPROVED"
}
```

---

### 3. Yeni İşlem (Withdraw) Yap
**POST** `/api/wallets/{walletId}/withdraw`

**Request Body:**
```json
{
  "amount": 500,
  "walletId": 1,
  "destination": "IBAN987654",
  "destinationType": "IBAN"
}
```

**Response:**
```json
{
  "id": 1,
  "amount": 500,
  "type": "WITHDRAW",
  "oppositePartyType": "IBAN",
  "oppositeParty": "IBAN987654",
  "status": "APPROVED"
}
```

---

### 4. İşlem Onayla veya Reddet
**POST** `/api/transactions/approveOrDeny`

**Request Body:**
```json
{
  "transactionId": 1,
  "status": "APPROVED"
}
```

**Response:**
```json
{
  "id": 1,
  "amount": 500,
  "type": "WITHDRAW",
  "oppositePartyType": "IBAN",
  "oppositeParty": "IBAN987654",
  "status": "APPROVED"
}
```

---

### 5. Cüzdanları Listele
**GET** `/api/wallets`

**Request Params:**
- `currency`: (Opsiyonel) `TRY`, `USD`, `EUR` vb. (Filtreleme yapmak için)

**Response:**
```json
[
  {
    "id": 1,
    "walletName": "Main Wallet",
    "currency": "TRY",
    "balance": 1000,
    "usableBalance": 800,
    "activeForShopping": true,
    "activeForWithdraw": true
  },
  {
    "id": 2,
    "walletName": "Savings Wallet",
    "currency": "USD",
    "balance": 500,
    "usableBalance": 500,
    "activeForShopping": false,
    "activeForWithdraw": true
  }
]
```


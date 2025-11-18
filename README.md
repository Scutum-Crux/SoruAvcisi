# Soru Avcısı - Periyodik Tekrar ve Edebiyat Öğrenme Uygulaması

Soru Avcısı, Türkiye'deki YKS, LGS, KPSS, DGS, ALES, YDS, DUS/TUS gibi sınavlara hazırlanan öğrenciler için geliştirilmiş modern bir Android uygulamasıdır.

## 🎯 Özellikler

### 1. Periyodik Tekrar Sistemi
- Fotoğraf yükleyerek veya çekerek soru/içerik ekleme
- Ders etiketleme, motivasyon sebebi seçimi ve kısa not ekleme
- Kaydedilen sorulara arşiv ekranından erişim
- Bilimsel aralıklarla otomatik tekrar planlaması (1, 2, 5, 7, 14, 30, 60, 90 gün)
- Manuel tarih/saat seçeneği
- Akıllı bildirim sistemi

### 2. Edebiyat İçerik Modülü
- Her konu için özet ve açıklamalar
- Her konu için 5 adet 20 soruluk deneme testi
- 50 adet 24 soruluk tam sınav seti
- Yazar-Eser kodlama sistemi
- AI destekli mnemonic (ezber destekçisi) üretimi
- Otomatik "önemli eser" seçim sistemi

### 3. Abonelik Sistemi
- Ücretsiz deneme
- Sadece Tekrar Sistemi aboneliği
- Sadece Edebiyat aboneliği
- Premium (Tüm özellikler)

## 🏗️ Mimari

Proje **Clean Architecture** ve **MVVM** prensiplerine göre yapılandırılmıştır.

```
app/
├── presentation/        # UI Layer (Compose)
│   ├── splash/
│   ├── auth/
│   ├── home/
│   ├── repeat/
│   ├── literature/
│   ├── settings/
│   ├── components/     # Reusable UI components
│   └── theme/          # Design system
├── domain/             # Business Logic
│   ├── model/
│   ├── repository/
│   └── usecase/
├── data/               # Data Layer
│   ├── local/          # Room Database
│   ├── remote/         # API & Firebase
│   └── repository/
├── di/                 # Dependency Injection (Hilt)
└── core/               # Utilities & Base Classes
    ├── navigation/
    ├── constants/
    └── util/
```

## 🛠️ Teknoloji Stack

### UI & Framework
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Design system
- **Navigation Compose** - Navigation

### Dependency Injection
- **Hilt** - DI framework

### Database & Storage
- **Room** - Local database
- **DataStore** - Preferences
- **Firebase Firestore** - Cloud database

### Authentication
- **Firebase Auth** - Authentication
- **Google Sign-In** - OAuth

### ML & AI
- **OpenAI/Claude API** - AI features

### Background Tasks
- **WorkManager** - Scheduled tasks
- **Firebase Cloud Messaging** - Push notifications

### Networking
- **Retrofit** - HTTP client
- **OkHttp** - Network layer
- **Kotlinx Serialization** - JSON parsing

### Image Processing
- **Coil** - Image loading

### Payment
- **Google Play Billing** - In-app purchases

### Analytics & Monitoring
- **Firebase Analytics** - Usage analytics
- **Firebase Crashlytics** - Crash reporting

## 🎨 Design System

### Colors
- **Primary**: `#0B66FF` (Blue)
- **Accent**: `#FF6B6B` (Red-Pink)
- **Background Light**: `#F7F9FB`
- **Background Dark**: `#0B1220`

### Typography
- **Font Family**: Inter
- **Heading (H1)**: 20-24sp / SemiBold
- **Body**: 14-16sp / Regular
- **Small**: 12sp / Regular

### Shapes
- **Card Radius**: 12dp
- **Button Radius**: 12dp

## 📋 Geliştirme Görevleri

- [x] TASK-001: Project init + repo + dependencies
- [x] TASK-002: Authentication: Email + Google Sign-In
- [x] TASK-003: UI: Splash + Onboarding + Home
- [x] TASK-004: Upload flow: Camera + Gallery + preview
- [x] TASK-005: Photo upload metadata (lesson, reason, note) + archive view
- [x] TASK-006: Flashcard model + save to Room + cloud sync
- [x] TASK-007: Scheduler: period options + manual date picker
- [x] TASK-008: Notification worker (WorkManager) + reminders
- [ ] TASK-009: Study session UI: Image viewer + recall input
- [ ] TASK-010: Topic content API / local content loader
- [ ] TASK-011: Test runner: 20q test flow + scoring
- [ ] TASK-012: 50×24 exam pack importer + exam runner
- [ ] TASK-013: Author-Work DB model + curator UI
- [ ] TASK-014: AI integration: mnemonic generator
- [ ] TASK-015: Billing: Google Play Billing integration
- [ ] TASK-016: Settings: Dark mode, notifications, language
- [ ] TASK-017: Analytics + crash reporting
- [ ] TASK-018: QA + unit tests + UI tests
- [ ] TASK-019: Google Play release preparation

## 🚀 Kurulum

### Gereksinimler
- Android Studio Hedgehog | 2023.1.1 veya üzeri
- JDK 17
- Android SDK 35
- Min SDK 26 (Android 8.0)

### Adımlar
1. Projeyi klonlayın
2. Android Studio'da açın
3. Firebase projenizi oluşturun ve `google-services.json` dosyasını `app/` dizinine ekleyin
4. API anahtarlarını `local.properties` dosyasına ekleyin:
   ```properties
   OPENAI_API_KEY=your_openai_api_key
   ```
5. Sync ve Build yapın

## 📱 Ekranlar

### Auth Flow
- Splash Screen
- Onboarding
- Login / Register

### Main Flow
- Home Dashboard
- Repeat System (Upload, OCR, Schedule, Study)
- Literature (Topics, Tests, Exams, Author-Works)
- Settings & Profile
- Subscription

## 🔐 Güvenlik

- Veri anonimleştirme
- GDPR uyumlu veri saklama
- Güvenli API iletişimi (HTTPS)
- Firebase Security Rules

## 📄 Lisans

Bu proje özel mülkiyettir. Tüm hakları saklıdır.

## 👥 İletişim

Sorularınız için: support@examaid.app


# BEER VISUALS (Minecraft 1.16.5, Fabric)

Чистий візуальний клієнт **без чітів**.

- `mod/` — Fabric-мод **beer_visuals**
  - Меню: **Right Shift**
  - Тільки візуальні модулі: HUD, Crosshair, HitMarkers, Fullbright, BetterOutline, NoHurtCam
  - Сучасний ClickGUI + оновлений HUD
- `launcher/` — Electron-лаунчер **BEER VISUALS**
  - Офлайн / Microsoft акаунт
  - Автозавантаження Fabric + Fabric API

## Що всередині (тільки візуали)

| Модуль         | Опис                                      |
|----------------|-------------------------------------------|
| HUD            | Watermark, координати, FPS, HP, броня, зілля |
| Crosshair      | Кастомний хрестик (стиль, колір, розмір)  |
| HitMarkers     | Візуальні маркери попадань                |
| Fullbright     | Повне освітлення (клієнтський gamma)      |
| BetterOutline  | Кращий outline блоків                     |
| NoHurtCam      | Прибирає тряску камери при ударі          |

**Немає** KillAura, Fly, Speed, XRay, ESP, Scaffold та будь-яких інших чітів.

## Збірка

### Мод
```bash
cd mod
# потрібна Java 8–17 і Gradle
gradle build
# jar → build/libs/beer_visuals-1.2.0.jar
```

### Лаунчер
```bash
cd launcher
npm install
npm start          # dev
npm run dist       # → dist/BeerVisuals.exe
```

Поклади `beer_visuals-*.jar` у `launcher/resources/mods/` перед збіркою exe.

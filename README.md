# Moderation Helper GUI

Клиентский Minecraft-мод для Java Edition на Fabric `1.21.11`.

Мод помогает модератору быстро открыть GUI по нику игрока из чата, сделать скрин до открытия меню, выдать наказание, вести статистику текущей сессии, хранить последних игроков и управлять записью OBS через `obs-websocket`.

## Возможности

- СКМ по строке чата открывает меню наказаний по найденному нику.
- Скриншот делается до открытия GUI.
- Скриншот не делается, если строка содержит:
  - `Tick Speed`
  - `Reach`
  - `Fighting suspiciously`
  - `Block Interaction`
- H открывает только панель статистики и недавних игроков.
- G останавливает запись OBS, но не срабатывает, когда открыт чат.
- Клавиши H и G настраиваются через стандартные настройки управления Minecraft.
- Кнопка `Вызвать на проверку` отправляет:
  - `/tpp {nick}`
  - `/tp {nick}`
  - `/check {nick}`
  - `/tell {nick} Здравствуйте, проверка на читы...`
- При вызове на проверку запускается OBS-запись и появляется таймер над хотбаром: `Идёт запись: 00:00`.
- При IPBan запись OBS останавливается автоматически, кроме причин `бот` и `3.8`.
- Скриншоты автоматически сортируются по папкам.
- Старые скриншоты удаляются или архивируются по конфигу.

## Структура скриншотов

По умолчанию папка создаётся в `.minecraft/moderation_screenshots/`:

```text
moderation_screenshots/
  temp/
  warn/
  mute/
  ban/
  ipban/
  archive/
```

Сначала файл сохраняется во временную папку:

```text
moderation_screenshots/temp/{nick}_{datetime}.png
```

После выдачи наказания переносится в нужную категорию:

```text
{nick}_{punishment}_{duration}_{reason}_{datetime}.png
```

Запрещённые символы в имени файла заменяются на `_`.

## Причины наказаний

### Warn

- `2.1` — предупреждение, выдаётся сразу

### Mute

- `2.2`
- `2.3`
- `2.4`
- `2.5`
- `2.6`
- `2.7`
- `2.8`
- `2.9`
- `2.10`
- `2.11`
- `2.12`
- `2.13`
- `2.14`
- `2.15`

### Ban

- `2.2`
- `2.3`
- `2.6`
- `2.7`
- `3.1` — `perm`
- `4.1`

### IPBan

- `бот` — `perm`
- `уход от проверки` — `30d`
- `время вышло` — `30d`
- `неадекватное поведение во время проверки` — `30d`
- `признание` — `20d`
- `3.3` — `30d`
- `3.6` — `1d/3d/7d/15d`
- `3.7` — `30d/20d/15d/7d`
- `3.8` — `30d`
- `3.9` — `3d`
- `3.10` — `15d`

## Команды

Мод отправляет команды от имени игрока:

```text
/warn {nick} {reason}
/mute {nick} {duration} {reason}
/ban {nick} {duration} {reason}
/ipban {nick} {duration} {reason}
```

Если на твоём сервере команды принимают другой формат, поменяй сборку в `PunishmentExecutor.java`.

## Конфиг

После первого запуска появится файл:

```text
.minecraft/config/moderation_helper_gui.json
```

Пример:

```json
{
  "obsEnabled": true,
  "obsHost": "localhost",
  "obsPort": 4455,
  "obsPassword": "",
  "recentPlayersLimit": 15,
  "screenshotCleanupMode": "ARCHIVE",
  "screenshotRetentionDays": 30,
  "screenshotFolder": "moderation_screenshots",
  "checkCommandTemplate": "/check {nick}",
  "tppCommandTemplate": "/tpp {nick}",
  "tpCommandTemplate": "/tp {nick}",
  "checkTellTemplate": "/tell {nick} Здравствуйте, проверка на читы. В течении 5 минут жду ваш Anydesk (наилучший вариант, скачать можно в любом браузере)/Discord. Также сообщаю, что в случае признания на наличие чит-клиентов срок бана составит 20 дней, вместо 30.",
  "quickCustomReasons": ["2.2", "2.3", "3.7", "3.8", "бот", "уход от проверки", "признание"]
}
```

`screenshotCleanupMode`:

- `DELETE` — удалять старые скриншоты
- `ARCHIVE` — переносить в `archive`
- `OFF` — отключить очистку

## OBS

В OBS нужно включить WebSocket:

1. Открой OBS.
2. `Tools / Инструменты` → `WebSocket Server Settings`.
3. Включи WebSocket server.
4. Порт оставь `4455` или поменяй в конфиге мода.
5. Если включил пароль, пропиши его в `obsPassword`.

Если OBS недоступен, мод не должен крашить игру — он просто напишет ошибку в чат/лог.

## Сборка

Нужен JDK 21.

```bash
gradle build --stacktrace
```

Готовый `.jar` будет в:

```text
build/libs/
```

## Установка

1. Собери проект через Gradle.
2. Возьми `.jar` из `build/libs/`.
3. Положи его в `.minecraft/mods/`.
4. Установи Fabric Loader для Minecraft `1.21.11`.
5. Положи Fabric API под `1.21.11` в `.minecraft/mods/`.

## Важное замечание

Клик по нику в чате сделан через Mixin на `ChatScreen.mouseClicked` и кэш последних сообщений из `ChatHud`. Minecraft/Fabric `1.21.11` меняли часть GUI/keybind API, поэтому если GitHub Actions покажет ошибку по конкретной строке, надо править именно эту сигнатуру. Основная проблема прошлого варианта с `String cannot be converted to Category` исправлена: для биндов используется `KeyBinding.Category.create(Identifier.of(...))`, а не строка категории.

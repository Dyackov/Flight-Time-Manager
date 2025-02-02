# ✈️ Flight Time Manager

![Java](https://img.shields.io/badge/Java-21-blue) ![Maven](https://img.shields.io/badge/Maven-Build-brightgreen) ![JUnit](https://img.shields.io/badge/Tests-Passing-success)

**Flight Time Manager** – это консольное Java-приложение для анализа и учета рабочего времени лётных специалистов авиакомпании. 📊 Оно загружает данные в JSON формате о выполненных рейсах и формирует отчет в JSON формате с расчетом полётного времени и проверкой на превышение допустимых норм. 🕒

---

## 🚀 Возможности

✔️ Загрузка данных о перелетах из входного файла 📂  
✔️ Подсчет рабочего времени летных специалистов по месяцам 📆  
✔️ Генерация отчёта в формате JSON 📜  
✔️ Проверка на превышение нормативов рабочего времени ⚠️  
✔️ Покрытие unit-тестами ✅  
✔️ Сборка через Maven 🏗️  

---

## 📁 Структура проекта

```
Flight-Time-Manager/
│── src/
│   ├── main/
│   │   ├── java/ru/example/
│   │   │   ├── config/         # Конфигурации приложения
│   │   │   ├── dto/            # Модели данных для передачи
│   │   │   ├── model/          # Модели данных (пилоты, перелеты и т. д.)
│   │   │   ├── runner/         # Запуск обработки данных
│   │   │   ├── service/        # Логика обработки данных
│   │   │   ├── validator/      # Валидация данных
│   │   │   ├── FlightTimeManager.java  # Главный класс приложения
│   │   ├── resources/          # Входные файлы (например, JSON)
│   ├── test/                   # Unit-тесты
│── pom.xml                     # Конфигурация Maven
│── README.md                   # Документация проекта
```

---

## 🔧 Установка и запуск

### 📌 Требования
- **Java 21** или выше ☕
- **Maven** для сборки проекта ⚙️

### 🏗️ Сборка проекта

```sh
mvn clean install
```

### ▶️ Запуск

```sh
java -jar target/Flight-Time-Manager-1.0-SNAPSHOT.jar input.json output.json
```

📌 **Примечание:** Формат входного файла должен соответствовать JSON-структуре ниже.

---

## 📜 Формат входного файла

Пример `input.json`:
```json
{
  "pilots": [
    { "idPilot": 1, "fullName": "Иван Смирнов" }
  ],
  "flights": [
    {
      "id": 1,
      "aircraftType": "Boeing 737",
      "aircraftNumber": "B737-800",
      "departureTime": "2025-01-30T01:00:00",
      "arrivalTime": "2025-02-01T13:00:00",
      "departureAirport": "JFK",
      "arrivalAirport": "LAX",
      "idPilots": [1,2]
    }
  ]
}

```

---

## 📊 Формат выходного файла

Пример `output.json`:
```json
{
  "specialists" : [ {
    "idPilot" : 1,
    "fullName" : "Иван Смирнов",
    "timeMonthList" : [ {
      "date" : "2024 ноябрь",
      "totalFlightHours" : 12,
      "totalFlightsInMonth" : 1,
      "exceedsMonthlyLimit" : false,
      "exceedsWeeklyLimit" : false,
      "exceedsDailyLimit" : true
      } ]
  } ]
}

```

---

## 🧪 Тестирование

Проект покрыт unit-тестами. Для запуска тестов используй:

```sh
mvn test
```

---

## 📌 Возможные улучшения

🚀 Расширение поддерживаемых форматов входных файлов (CSV, XML)  
🔍 Оптимизация алгоритма расчета рабочего времени  
📊 Визуализация данных через веб-интерфейс  

---

👨‍💻 **Автор:** [Dyackov](https://github.com/Dyackov)  
🔗 **Репозиторий:** [Flight-Time-Manager](https://github.com/Dyackov/Flight-Time-Manager)


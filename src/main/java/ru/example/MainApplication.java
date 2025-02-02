package ru.example;

import ru.example.runner.PilotServiceRunner;

/**
 * Главный класс приложения, который инициализирует выполнение сервиса для пилотов.
 * Этот класс содержит метод {@link #main}, который запускает приложение, создавая экземпляр
 * {@link PilotServiceRunner} и вызывая его метод {@link PilotServiceRunner#run}.
 *
 * <p>Основное назначение этого класса — запуск сервиса, связанного с пилотами, при старте приложения.</p>
 */
public class MainApplication {

    /**
     * Точка входа в приложение. Этот метод создаёт экземпляр {@link PilotServiceRunner} и
     * вызывает его метод {@link PilotServiceRunner#run}, инициируя выполнение сервиса.
     *
     * @param args Параметры командной строки (не используются в данном классе).
     */
    public static void main(String[] args) {
        PilotServiceRunner pilot = new PilotServiceRunner();
        pilot.run();
    }
}

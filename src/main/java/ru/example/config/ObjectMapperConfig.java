package ru.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Конфигурация для настройки {@link ObjectMapper}.
 * <p>
 * Этот класс предоставляет метод для создания и конфигурации экземпляра {@link ObjectMapper},
 * который поддерживает сериализацию и десериализацию объектов, содержащих поля с типами времени
 * (например, {@link java.time.LocalDateTime}). Также включается возможность отступов при выводе JSON.
 * </p>
 */
public class ObjectMapperConfig {

    /**
     * Создает и настраивает {@link ObjectMapper} для работы с типами времени и отступами в JSON.
     * <p>
     * Метод регистрирует {@link JavaTimeModule}, что позволяет {@link ObjectMapper} корректно
     * работать с типами времени, такими как {@link java.time.LocalDateTime}. Также включается опция
     * {@link SerializationFeature#INDENT_OUTPUT} для форматирования JSON с отступами.
     * </p>
     *
     * @return Конфигурированный экземпляр {@link ObjectMapper}.
     */
    public static ObjectMapper createConfigObjectMapperTime() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }
}

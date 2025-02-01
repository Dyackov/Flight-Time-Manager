package ru.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Реализация сервиса для работы с файловым хранилищем данных.
 * <p>
 * Этот класс реализует интерфейс {@link DataPersistenceService}, предоставляя функциональность
 * для чтения и записи данных в файлы с использованием {@link ObjectMapper} для сериализации и десериализации.
 * </p>
 */
public class DataPersistenceServiceImpl implements DataPersistenceService {

    /**
     * Объект {@link ObjectMapper}, используемый для сериализации и десериализации данных.
     */
    private final ObjectMapper objectMapper;

    /**
     * Конструктор, который инициализирует {@link ObjectMapper}.
     *
     * @param objectMapper {@link ObjectMapper}, который будет использоваться для чтения и записи данных.
     */
    public DataPersistenceServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Читает данные из файла и преобразует их в объект заданного типа.
     * <p>
     * Метод использует {@link ObjectMapper} для десериализации данных из файла в объект указанного типа.
     * В случае ошибок при чтении или десериализации будет выброшено исключение {@link IOException}.
     * </p>
     *
     * @param file Файл, из которого будут прочитаны данные.
     * @param valueType Класс, в объект которого будут преобразованы данные.
     * @param <T> Тип объекта, в который будут преобразованы данные.
     * @return Объект типа {@code T}, полученный из данных в файле.
     * @throws IOException Если произошла ошибка при чтении файла или десериализации данных.
     */
    @Override
    public <T> T readFile(File file, Class<T> valueType) throws IOException {
        return objectMapper.readValue(file, valueType);
    }

    /**
     * Записывает данные в файл.
     * <p>
     * Метод использует {@link ObjectMapper} для сериализации переданных данных и записи их в указанный файл.
     * В случае ошибок при записи будет выброшено исключение {@link IOException}.
     * </p>
     *
     * @param file Файл, в который будут записаны данные.
     * @param data Данные, которые необходимо записать в файл.
     * @param <T> Тип данных, которые будут записаны в файл.
     * @throws IOException Если произошла ошибка при записи данных в файл.
     */
    @Override
    public <T> void writeFile(File file, T data) throws IOException {
        objectMapper.writeValue(file, data);
    }
}

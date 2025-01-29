package ru.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class DataPersistenceServiceImpl implements DataPersistenceService {
    private final ObjectMapper objectMapper;

    public DataPersistenceServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T readFile(File file, Class<T> valueType) throws IOException {
        return objectMapper.readValue(file, valueType);
    }

    @Override
    public <T> void writeFile(File file, T data) throws IOException {
        objectMapper.writeValue(file, data);
    }
}
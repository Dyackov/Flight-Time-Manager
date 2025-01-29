package ru.example.service;

import java.io.File;
import java.io.IOException;

public interface DataPersistenceService {
    <T> T readFile(File file, Class<T> valueType) throws IOException;

    <T> void writeFile(File file, T data) throws IOException;
}
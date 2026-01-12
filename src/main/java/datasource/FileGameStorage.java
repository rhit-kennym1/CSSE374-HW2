package datasource;

import java.io.*;
import java.nio.file.*;

public class FileGameStorage implements GameStorage {
    private final String filePath;

    public FileGameStorage(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void backup() {
        String data;
        try {
            data = Files.readString(Paths.get("gamesave.txt"));
            String backupFilePath = "gamesave_" + System.currentTimeMillis() + ".txt";
            Files.writeString(Paths.get(backupFilePath), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(String data) {
        try {
            Files.writeString(Paths.get(filePath), data);
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    @Override
    public String load() {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return "";
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(Paths.get(filePath));
    }
}

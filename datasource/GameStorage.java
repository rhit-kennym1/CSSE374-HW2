package datasource;

public interface GameStorage {
    void save(String data);

    String load();

    boolean exists();

    void backup();
}

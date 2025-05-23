package practicum.blog.utils;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLTestContainer extends PostgreSQLContainer<PostgreSQLTestContainer> {

    private static final String IMAGE_VERSION = "postgres:15";
    private static PostgreSQLTestContainer container;

    private PostgreSQLTestContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgreSQLTestContainer getInstance() {
        if (container == null) {
            container = new PostgreSQLTestContainer();
            container.start();
        }
        return container;
    }
}

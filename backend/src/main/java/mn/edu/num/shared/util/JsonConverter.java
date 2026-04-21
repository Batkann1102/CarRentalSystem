package mn.edu.num.shared.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public class JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverter() {
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    public <T> T fromJson(InputStream inputStream, Class<T> type) throws IOException {
        return objectMapper.readValue(inputStream, type);
    }

    public String toJson(Object value) throws IOException {
        return objectMapper.writeValueAsString(value);
    }
}


package dev.duque.api.parkingcontrol.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class DateConfig {
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final LocalDateTimeSerializer LOCAL_DATE_TIME_SERIALIZER = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN));

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LOCAL_DATE_TIME_SERIALIZER);
        return new ObjectMapper().registerModule(module);
    }

}

package ru.practicum.statsdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO класс для получения данных о запросе.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDto {
    private String app;

    private String uri;

    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

}

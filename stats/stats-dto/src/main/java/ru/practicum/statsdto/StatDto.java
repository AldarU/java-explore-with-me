package ru.practicum.statsdto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDto {
    private String app;
    private String uri;
    private Long hits;

}

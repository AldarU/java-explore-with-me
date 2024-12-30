package ru.practicum.statsdto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDto {
    @NonNull
    private String app;

    @NonNull
    private String uri;

    @NonNull
    private Long hits;
}

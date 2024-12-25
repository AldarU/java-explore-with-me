package ru.practicum.statservice.note.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statservice.note.model.Note;

@Component
public class MapperNoteDto {
    public NoteDto toNoteDto(final Note note) {
        return NoteDto.builder()
                .app(note.getApp())
                .ip(note.getIp())
                .uri(note.getUri())
                .timestamp(note.getTimestamp())
                .build();
    }

    public Note toNote(final NoteDto noteDto) {
        return Note.builder()
                .app(noteDto.getApp())
                .ip(noteDto.getIp())
                .uri(noteDto.getUri())
                .timestamp(noteDto.getTimestamp())
                .build();
    }

}

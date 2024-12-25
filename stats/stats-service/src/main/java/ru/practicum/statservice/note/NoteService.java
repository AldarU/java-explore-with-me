package ru.practicum.statservice.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statsdto.StatDto;
import ru.practicum.statservice.exception.BadRequestException;
import ru.practicum.statservice.note.dao.NoteRepository;
import ru.practicum.statservice.note.model.Note;
import ru.practicum.statservice.note.model.mapper.MapperNoteDto;
import ru.practicum.statservice.note.model.mapper.MapperStatDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;

    private final MapperNoteDto noteMapper;

    private final MapperStatDto statMapper;

    public void hit(final NoteDto noteDto) {
        Note note = noteMapper.toNote(noteDto);
        Note saved = noteRepository.save(note);
        log.info("HIT: {}", saved);
    }

    public List<StatDto> getStats(final LocalDateTime start,
                                  final LocalDateTime end,
                                  final List<String> uris,
                                  final boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Incorrect time was passed.", "Start date must not be after end date.");
        }
        List<StatDto> stats;
        if (unique) stats = noteRepository.findByParamsUniqueIsTrue(start, end, uris).stream()
                .map(statMapper::toStatDto).toList();
        else stats = noteRepository.findByParamsUniqueIsFalse(start, end, uris).stream()
                .map(statMapper::toStatDto).toList();
        log.info("GET: {}", stats);
        return stats;
    }
}

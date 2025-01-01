package ru.practicum.mainservice.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.commentlikes.dao.CommentLikeRepository;
import ru.practicum.mainservice.commentlikes.dto.CommentLikeDto;
import ru.practicum.mainservice.commentlikes.dto.CommentLikesMapper;
import ru.practicum.mainservice.commentlikes.model.CommentLike;
import ru.practicum.mainservice.comments.dao.CommentRepository;
import ru.practicum.mainservice.comments.dto.FullCommentDto;
import ru.practicum.mainservice.comments.model.Comment;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.UpdateEventRequest;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.replies.dao.ReplyRepository;
import ru.practicum.mainservice.replies.dto.FullReplyDto;
import ru.practicum.mainservice.replies.dto.ReplyMapper;
import ru.practicum.mainservice.replies.dto.ShortReplyDto;
import ru.practicum.mainservice.replies.model.Reply;
import ru.practicum.mainservice.replylikes.dao.ReplyLikeRepository;
import ru.practicum.mainservice.replylikes.dto.ReplyLikeDto;
import ru.practicum.mainservice.replylikes.dto.ReplyLikeMapper;
import ru.practicum.mainservice.replylikes.model.ReplyLike;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ServiceGeneralFunctionality {

    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final CommentLikesMapper commentLikesMapper;
    private final ReplyMapper replyMapper;
    private final ReplyLikeMapper replyLikeMapper;

    public void updateEvent(Event event, UpdateEventRequest eventUpdate) {
        if (eventUpdate.getAnnotation() != null) event.setAnnotation(eventUpdate.getAnnotation());
        if (eventUpdate.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdate.getCategory()).orElseThrow(() ->
                    new NotFoundException("There is no such category.",
                            "Category with id = " + eventUpdate.getCategory() + " does not exist."));
            event.setCategory(category);
        }
        if (eventUpdate.getDescription() != null) event.setDescription(eventUpdate.getDescription());
        if (eventUpdate.getLocation() != null) {
            event.setLat(eventUpdate.getLocation().getLat());
            event.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) event.setPaid(eventUpdate.getPaid());
        if (eventUpdate.getParticipantLimit() != null) event.setParticipantLimit(eventUpdate.getParticipantLimit());
        if (eventUpdate.getRequestModeration() != null) event.setRequestModeration(eventUpdate.getRequestModeration());
        if (eventUpdate.getTitle() != null) event.setTitle(eventUpdate.getTitle());
    }

    public Long getConfirmedRequests(Long eventId) {
        return eventRepository.countOfParticipants(eventId);
    }

    public Long getCountOfComments(Long eventId) {
        return eventRepository.getCountOfComments(eventId);
    }

    public void fillFullCommentDto(FullCommentDto fullCommentDto) {
        List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentId(fullCommentDto.getId());
        List<CommentLikeDto> commentLikeDtos = commentLikes.stream()
                .map(commentLikesMapper::toCommentLikeDto)
                .toList();
        fullCommentDto.setLikes(commentLikeDtos);
        List<ShortReplyDto> shortReplyDtos = replyRepository.findAllByCommentId(fullCommentDto.getId());
        fullCommentDto.setReplies(shortReplyDtos);
    }

    public void fillFullReplyDto(FullReplyDto fullReplyDto) {
        List<ReplyLike> replyLikes = replyLikeRepository.findAllByReplyId(fullReplyDto.getId());
        List<ReplyLikeDto> replyLikeDtos = replyLikes.stream().map(replyLikeMapper::toReplyLikeDto).toList();
        fullReplyDto.setLikes(replyLikeDtos);
    }

    public Comment commentToEventCheck(Long eventId, Long commentId) {
        return validateEntityRelationship(
                eventId,
                commentId,
                commentRepository,
                comment -> comment.getEvent().getId(),
                "Event with id = " + eventId + " does not exist.",
                "The event with id = " + eventId + " does not contain a comment with id = " + commentId + "."
        );
    }

    public Reply replyToCommentCheck(Long commentId, Long replyId) {
        return validateEntityRelationship(
                commentId,
                replyId,
                replyRepository,
                reply -> reply.getComment().getId(),
                "Comment with id = " + commentId + " does not exist.",
                "The comment with id = " + commentId + " does not contain a reply with id = " + replyId + "."
        );
    }

    public <T, R> R validateEntityRelationship(
            Long entityId,
            Long relatedEntityId,
            JpaRepository<R, Long> relatedRepo,
            Function<R, Long> relatedEntityIdExtractor,
            String notFoundMessage,
            String badRequestMessage) {

        if (!relatedRepo.existsById(entityId)) {
            throw new NotFoundException(notFoundMessage, entityId + " does not exist.");
        }

        R relatedEntity = relatedRepo.findById(relatedEntityId).orElseThrow(() ->
                new NotFoundException("There is no entity with this ID.",
                        "Entity with id = " + relatedEntityId + " does not exist."));

        if (!relatedEntityIdExtractor.apply(relatedEntity).equals(entityId)) {
            throw new BadRequestException(badRequestMessage,
                    "Entity with id = " + entityId + " does not contain related entity with id = " + relatedEntityId + ".");
        }

        return relatedEntity;
    }
}

package com.sec.movietalk.review.service;

import com.sec.movietalk.common.domain.comment.Comment;
import com.sec.movietalk.common.domain.comment.CommentReactions;
import com.sec.movietalk.common.domain.comment.CommentReports;
import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.CommentRequest;
import com.sec.movietalk.review.dto.CommentResponse;
import com.sec.movietalk.review.dto.ReactionRequest;
import com.sec.movietalk.review.dto.ReportRequest;
import com.sec.movietalk.review.repository.CommentReactionsRepository;
import com.sec.movietalk.review.repository.CommentReportsRepository;
import com.sec.movietalk.review.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;
    private final CommentReactionsRepository reactRepo;
    private final CommentReportsRepository reportRepo;

    /** 1. 특정 리뷰의 최상위 댓글(부모 없는) 모두 가져오기 **/
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long reviewId) {
        return commentRepo
                .findByReview_ReviewIdAndParentIsNullOrderByCreatedAt(reviewId)
                .stream()
                .map(this::toDtoWithReplies)
                .toList();
    }

    /** 2. 새 댓글 작성 **/
    @Transactional
    public CommentResponse addComment(CommentRequest req, User currentUser) {
        Comment c = new Comment();
        // Review 객체는 ID만 필요하므로 builder 이용
        c.setReview(Review.builder()
                .reviewId(req.reviewId())
                .build());
        c.setParent(null);
        c.setUser(currentUser);
        c.setContent(req.content());
        commentRepo.save(c);
        return toDtoWithReplies(c);
    }

    /** 3. 대댓글 작성 **/
    @Transactional
    public CommentResponse reply(CommentRequest req, User currentUser) {
        Comment parent = commentRepo.findById(req.parentId())
                .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));
        Comment c = new Comment();
        c.setReview(parent.getReview());
        c.setParent(parent);
        c.setUser(currentUser);
        c.setContent(req.content());
        commentRepo.save(c);
        return toDtoWithReplies(c);
    }

    /** 4. 좋아요/싫어요 반응 처리 **/
    @Transactional
    public void react(Long commentId, String reactionType, User currentUser) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 중복된 반응 삭제
        reactRepo.findAll().stream()
                .filter(r -> r.getComment().equals(comment) && r.getUser().equals(currentUser))
                .findFirst()
                .ifPresent(reactRepo::delete);

        // 새 반응 저장
        CommentReactions r = new CommentReactions();
        r.setComment(comment);
        r.setUser(currentUser);
        r.setReaction(CommentReactions.ReactionType.valueOf(reactionType));
        reactRepo.save(r);

        // 댓글 엔티티에 좋아요/싫어요 카운트 동기화
        long likes = reactRepo.findAll().stream()
                .filter(x -> x.getComment().equals(comment)
                        && x.getReaction() == CommentReactions.ReactionType.like)
                .count();
        long dislikes = reactRepo.findAll().stream()
                .filter(x -> x.getComment().equals(comment)
                        && x.getReaction() == CommentReactions.ReactionType.dislike)
                .count();
        comment.setLikeCnt((int) likes);
        comment.setDislikeCnt((int) dislikes);
    }

    /** 5. 댓글 신고 처리 **/
    @Transactional
    public void report(Long commentId, String reason, User currentUser) {
        Comment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        CommentReports rpt = CommentReports.builder()
                .comment(c)
                .user(currentUser)
                .reason(reason)
                .build();
        reportRepo.save(rpt);
    }

    /** 6. 댓글 채택 처리 (리뷰 작성자만) **/
    @Transactional
    public void adopt(Long commentId, User currentUser) {
        Comment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        if (!c.getReview().getUser().equals(currentUser)) {
            throw new AccessDeniedException("리뷰 작성자만 채택할 수 있습니다.");
        }
        c.setAccepted(true);
        c.setAcceptedAt(LocalDateTime.now());
    }

    /** 댓글 → DTO 변환 + 재귀적으로 대댓글 포함 **/
    private CommentResponse toDtoWithReplies(Comment c) {
        List<CommentResponse> replies = commentRepo
                .findByParent_CommentIdOrderByCreatedAt(c.getCommentId())
                .stream()
                .map(this::toDtoWithReplies)
                .toList();

        // 레코드 CommentResponse 의 기본 생성자 호출
        return new CommentResponse(
                c.getCommentId(),
                c.getUser().getNickname(),
                c.getContent(),
                c.getCreatedAt(),
                c.getLikeCnt(),
                c.getDislikeCnt(),
                c.getAccepted(),
                replies
        );
    }
}

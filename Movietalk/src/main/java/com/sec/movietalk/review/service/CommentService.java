package com.sec.movietalk.review.service;

import com.sec.movietalk.common.domain.comment.Comment;
import com.sec.movietalk.common.domain.comment.CommentReactions;
import com.sec.movietalk.common.domain.comment.CommentReports;
import com.sec.movietalk.common.domain.review.Review;
import com.sec.movietalk.common.domain.user.User;
import com.sec.movietalk.review.dto.CommentRequest;
import com.sec.movietalk.review.dto.CommentResponse;
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

    /** 1. 특정 리뷰의 최상위 댓글 모두 가져오기 **/
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
        Comment c = Comment.builder()
                .review(Review.builder().reviewId(req.reviewId()).build())
                .parent(null)
                .user(currentUser)
                .content(req.content())
                .build();
        commentRepo.save(c);
        return toDtoWithReplies(c);
    }

    /** 3. 대댓글 작성 **/
    @Transactional
    public CommentResponse reply(CommentRequest req, User currentUser) {
        Comment parent = commentRepo.findById(req.parentId())
                .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));
        Comment c = Comment.builder()
                .review(parent.getReview())
                .parent(parent)
                .user(currentUser)
                .content(req.content())
                .build();
        commentRepo.save(c);
        return toDtoWithReplies(c);
    }

    /** 4. 좋아요/싫어요 반응 처리 **/
    @Transactional
    public void react(Long commentId, CommentReactions.ReactionType reactionType, User currentUser) {
        // 1) 댓글 조회
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 2) 기존 반응이 있으면 삭제
        reactRepo.findByComment_CommentIdAndUser_UserId(commentId, currentUser.getUserId())
                .ifPresent(reactRepo::delete);

        // 3) 새 반응 저장
        CommentReactions r = new CommentReactions();
        r.setComment(comment);
        r.setUser(currentUser);
        r.setReaction(reactionType);
        reactRepo.save(r);

        // 4) 카운트 동기화
        int likes = (int) reactRepo.countByComment_CommentIdAndReaction(commentId, CommentReactions.ReactionType.like);
        int dislikes = (int) reactRepo.countByComment_CommentIdAndReaction(commentId, CommentReactions.ReactionType.dislike);
        comment.setLikeCnt(likes);
        comment.setDislikeCnt(dislikes);
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
        c.markAsAccepted();
    }

    /** 7. 댓글 수정 (작성자만) **/
    @Transactional
    public CommentResponse updateComment(Long reviewId, Long commentId, String newContent, Long userId) {
        Comment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        if (!c.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }
        c.setContent(newContent);
        return toDtoWithReplies(c);
    }

    /** 8. 댓글 삭제 (작성자 또는 리뷰 작성자) **/
    @Transactional
    public void deleteComment(Long reviewId, Long commentId, Long userId) {
        Comment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        boolean isCommentAuthor = c.getUser().getUserId().equals(userId);
        boolean isReviewAuthor  = c.getReview().getUser().getUserId().equals(userId);
        if (!(isCommentAuthor || isReviewAuthor)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        commentRepo.delete(c);
    }

    /** 댓글 → DTO 변환 + 재귀적으로 대댓글 포함 **/
    private CommentResponse toDtoWithReplies(Comment c) {
        List<CommentResponse> replies = commentRepo
                .findByParent_CommentIdOrderByCreatedAt(c.getCommentId())
                .stream()
                .map(this::toDtoWithReplies)
                .toList();

        // ↙ 여기에 authorId 추가
        return new CommentResponse(
                c.getCommentId(),              // id
                c.getUser().getUserId(),       // authorId  ← 추가
                c.getUser().getNickname(),     // authorName
                c.getContent(),
                c.getCreatedAt(),
                c.getLikeCnt(),
                c.getDislikeCnt(),
                c.getAccepted(),
                replies
        );
    }

}

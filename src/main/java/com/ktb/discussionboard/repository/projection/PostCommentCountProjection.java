package com.ktb.discussionboard.repository.projection;

public interface PostCommentCountProjection {
    Long getPostId();

    long getCommentCount();
}

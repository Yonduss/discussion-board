INSERT INTO users (
    id,
    email,
    password,
    nickname,
    profile_image_url,
    deleted,
    created_at,
    profile_updated_at,
    password_updated_at,
    deleted_at
) VALUES (
    1,
    'denzi@chainsawman.com',
    'strongman1',
    'Pochita',
    NULL,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO users (
    id,
    email,
    password,
    nickname,
    profile_image_url,
    deleted,
    created_at,
    profile_updated_at,
    password_updated_at,
    deleted_at
) VALUES (
     2,
     'reze@janedoe.com',
     '161616',
     'Bomb',
     NULL,
     false,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP,
     NULL
);

INSERT INTO posts (
    id,
    user_id,
    title,
    content,
    like_count,
    view_count,
    reported_count,
    deleted,
    edited,
    hidden,
    created_at,
    updated_at,
    deleted_at,
    hidden_at
) VALUES (
    1,
    1,
    'I''m chainsaw man',
    'Do you know where the Futamichi Cafe is?',
    0,
    0,
    0,
    false,
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL,
    NULL
);

INSERT INTO post_images (
    id,
    post_id,
    image_url,
    sort_order
) VALUES (
    1,
    1,
    'https://pixabay.com/ko/photos/hard-rock-cafe-%EC%8B%9D%EB%8B%B9-3726209/',
    0
);

INSERT INTO comments (
    id,
    post_id,
    user_id,
    parent_comment_id,
    content,
    deleted,
    edited,
    created_at,
    updated_at,
    deleted_at
) VALUES (
    1,
    1,
    2,
    NULL,
    'Do you mean the cafe close to phone booth?',
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO comments (
    id,
    post_id,
    user_id,
    parent_comment_id,
    content,
    deleted,
    edited,
    created_at,
    updated_at,
    deleted_at
) VALUES (
    2,
    1,
    1,
    1,
    'Yes, that''s exactly the one I''m looking for!',
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL
);
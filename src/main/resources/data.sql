INSERT INTO users (
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
    post_id,
    image_url,
    sort_order
) VALUES (
    1,
    'https://cdn.pixabay.com/photo/2018/10/05/15/45/hard-rock-cafe-3726209_1280.jpg',
    0
);

INSERT INTO comments (
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
    1,
    'Yes, that''s exactly the one I''m looking for!',
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL
);
INSERT INTO users (
    id,
    email,
    password,
    nickname,
    profile_image_url,
    deleted
) VALUES (
    1,
    'denzi@chainsawman.com',
    'strongman1',
    'Pochita',
    NULL,
    false
);

INSERT INTO users (
    id,
    email,
    password,
    nickname,
    profile_image_url,
    deleted
) VALUES (
     2,
     'reze@janedoe.com',
     '161616',
     'Bomb',
     NULL,
     false
);

INSERT INTO posts (
    id,
    user_id,
    title,
    content,
    post_image_url,
    like_count,
    view_count,
    reported_count,
    deleted,
    edited,
    hidden,
    created_at,
    updated_at
) VALUES (
    1,
    1,
    'I''m chainsaw man',
    'Do you know where the Futamichi Cafe is?',
    NULL,
    0,
    0,
    0,
    false,
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
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
    updated_at
) VALUES (
    1,
    1,
    2,
    NULL,
    'Do you mean the cafe close to phone booth?',
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
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
    updated_at
) VALUES (
    2,
    1,
    1,
    1,
    'Yes, that''s exactly the one I''m looking for!',
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
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
    'test@test.com',
    '$2a$10$Ipk7Xms7kvXN/TgqqO0b7OJNHhbmCrRBSheHDd7CQGC2HlnrzdWJ.', --12345678
    'test',
    'https://i.namu.wiki/i/1jdhfuJxvI-RtKFumZD0wggFTdQDOANKk8HVx0Gn57jhUhb-sLhxfUG7cx978fOizT0Lfk6tl15KjbqRKJzoyHZcFsUIT2LXpn0PtnuzHbN38iLVT_ImI2hDQeNf-2BflYeDv7ri2E-YvAZYUdIWSA.svg',
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
     'dummy@dummy.com',
     '$2a$10$XW3uqYz62k1H5ux6n5bdk.317aawij1WtgcZy1b4zxpTYjyx7UZk.', --87654321
     'dummy',
     'https://i.namu.wiki/i/0u4CFwTcXGIiHYDFAtKQ_NJZhx7SlcfYpnNWLOOmyVlsxihHNL8kTM1z6DVRy4SSliMrmjBZo0TxVF1tnrYYgVleQFWohaMINce2MQkQe_XsVJoRnMze5suwKt5STaApqs8xrQzshZVHItMqvwdWRg.svg',
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
    'Norway v England | Quarter-final',
    'There was a match between Norway & England played at Miami Stadium on Sunday 12, July at 06:00 KST.',
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
     2,
     'Argentina v Switzerland | Quarter-final',
     'There was a match between Argentina & Switzerland played at Kansas Stadium on Sunday 12, July at 10:00 KST.',
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
    'Because maybe you''re gonna be the one that saves me.',
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
    'And after all, you''re my wonderwall.',
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
    2,
    1,
    NULL,
    'VAMOS!!! MESSI!!!',
    false,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO post_images (
    post_id,
    image_url,
    sort_order
) VALUES (
    1,
    'https://cdn-media.theathletic.com/cdn-cgi/image/width=1440%2cquality=70%2cformat=auto/https://cdn-media.theathletic.com/FV7FjolqeFqF_qlbnRfjVdCBt_1440x960.jpg',
    0
);

INSERT INTO post_images (
    post_id,
    image_url,
    sort_order
) VALUES (
    2,
    'https://cdn-media.theathletic.com/cdn-cgi/image/width=1440%2cquality=70%2cformat=auto/https://cdn-media.theathletic.com/2tI5kiV1ItAO_RCOEDgnyIS25_1440x960.jpg',
    0
);
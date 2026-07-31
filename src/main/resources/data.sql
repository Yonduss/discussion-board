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
    'https://i.namu.wiki/i/O88iZiOsgmeag6nqOlfUC36JpLvV4utcUEwk03sqWOmJG6cut9O9WzychNVyqlSfcUDByx42tM61YyUhITzrnjBmQiqfSluC7R61rLEOv1RJUyE_ATaaNbQu_DgyZV9i58B-P2tEhhldpY1tjK8n0Q.svg',
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
     '/src/images/TEX_logo.svg',
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
    'jimothy@jimothy.com',
    '$2a$10$XW3uqYz62k1H5ux6n5bdk.317aawij1WtgcZy1b4zxpTYjyx7UZk.', --87654321
    'jimothy',
    'https://www.dndbeyond.com/avatars/thumbnails/57907/11/1000/1000/639202523315904128.png',
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
    'hoo@hoo.com',
    '$2a$10$Ipk7Xms7kvXN/TgqqO0b7OJNHhbmCrRBSheHDd7CQGC2HlnrzdWJ.', --12345678
    'hoo',
    '/src/images/SF_logo.svg',
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
    'Latest Dodgers injuries & transactions',
    'LATEST INJURIES TWP Shohei Ohtani Injury: Left knee inflammation Expected return (as pitcher): TBD, but in 2026 Status: Has continued to play catch and could throw off the mound by the end of the week of July 20, though his timeline to return to the rotation remains undetermined. (updated July 20)',
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
     'With trade deadline looming, Texas Rangers need wins, health',
     'President of baseball operations Chris Young still believes the Texas Rangers can get better before the trade deadline, but they need to play winning baseball and get Corey Seager healthy. ARLINGTON — Even after dropping 2 of 3 weekend games in Atlanta, the Texas Rangers entered play Monday still atop the American League West. It’s still mind-boggling that the Rangers are playoff contenders, but it’s true.',
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
    3,
    'Jimothy, the internet''s most famous raccoon, is getting a Minor League promo night',
    'The Rocket City Trash Pandas, based outside of Huntsville, Ala., derived their team name from a slang term for a raccoon. It follows, then, that the Double-A Angels affiliate is paying tribute to a raccoon folk hero whose mere existence has captivated a nation.The Trash Pandas will stage Jimothy Night at Toyota Field on Aug. 4, saluting a unique Seattle-area raccoon who survives and thrives despite being born with a spinal deformity. The team, after being inundated by requests for a Jimothy Night on social media, announced on Tuesday evening, "You asked, we listened!"',
    0,
    0,
    4,
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
    4,
    'Lee''s first homer in a month, Devers'' 22nd jump start win for Giants',
    'SAN FRANCISCO – There’s value in a well-timed ambush. Just ask the Giants’ Jung Hoo Lee. Mired in a monthlong slump that saw his average dip from .331 on June 23 to .299 entering play Saturday, the right fielder has gone through a rough stretch after a torrid start to his 2026 campaign. But with Angels starter Ryan Johnson running into trouble in the third inning, eliciting a mound visit after four singles in six batters, Lee forced the issue with a first-pitch home run toward Levi’s Landing in right field. Just like that, the Giants had blitzed the Angels for five two-out runs to spark a 9-2 win and a series victory. It was Lee’s first home run in exactly 99 plate appearances, dating back to June 23 – the date he was hitting a robust .331. Between those two homers, Lee hit just .207/.232/.283.',
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
    'No way! I can''t believe it.',
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
    'Not sure he has a serious injury.',
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
    'Rangers!!',
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
    'https://s.yimg.com/ny/api/res/1.2/Ax0wlX0COtQTx97iIDO79g--/YXBwaWQ9aGlnaGxhbmRlcjt3PTk2MDtoPTU0MDtjZj13ZWJw/https://media.zenfs.com/en/heavy_214/46d74ead5984142fc6fba1f4af26d4af',
    0
);

INSERT INTO post_images (
    post_id,
    image_url,
    sort_order
) VALUES (
    2,
    'https://cdn.allcitynetwork.com/wp-content/uploads/sites/14/2026/07/20183953/Seager7-20-1024x683.jpg',
    0
);

INSERT INTO post_images (
    post_id,
    image_url,
    sort_order
) VALUES (
    3,
    'https://img.mlbstatic.com/mlb-images/image/upload/t_16x9/t_w1536/mlb/kkcqzqrrrlahht0v03ni.jpg',
    0
);

INSERT INTO post_images (
    post_id,
    image_url,
    sort_order
) VALUES (
    4,
    'https://image.mediapen.com/news/202607/news_1112393_1785117523_m.jpg',
    0
);
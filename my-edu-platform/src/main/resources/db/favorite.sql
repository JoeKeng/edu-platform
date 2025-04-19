create table favorite
(
    id         bigint auto_increment comment '收藏ID'
        primary key,
    user_id    bigint                                          not null comment '用户ID',
    type       enum ('course', 'question', 'resource')         not null comment '收藏类型（课程、题目、资源）',
    category   enum ('video', 'document', 'exercise', 'other') null comment '收藏分类（如视频、文档、习题等，适用于资源）',
    target_id  bigint                                          not null comment '收藏对象ID（如课程ID、题目ID等）',
    created_at timestamp default CURRENT_TIMESTAMP             not null comment '收藏时间',
    updated_at timestamp default CURRENT_TIMESTAMP             not null on update CURRENT_TIMESTAMP comment '最后更新时间',
    is_deleted tinyint   default 0                             not null comment '是否逻辑删除',
    constraint uq_favorite
        unique (user_id, type, target_id),
    constraint favorite_ibfk_1
        foreign key (user_id) references user (user_id)
            on delete cascade
)
    comment '收藏表';

create index idx_user_favorite
    on favorite (user_id, type);
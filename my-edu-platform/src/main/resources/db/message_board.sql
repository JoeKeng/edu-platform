create table message_board
(
    id          bigint auto_increment comment '留言ID'
        primary key,
    user_id     bigint                               not null comment '用户ID，关联用户表',
    content     text                                 not null comment '留言内容',
    created_at  timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    parent_id   bigint                               null comment '父留言ID，支持留言回复',
    status      tinyint    default 0                 null comment '状态（1=正常, 0=待审核, -1=审核未通过）',
    module_type varchar(50)                          not null comment '模块类型（如 course, assignment, discussion）',
    module_id   bigint                               not null comment '对应模块的ID（如课程ID、作业ID等）',
    is_deleted  tinyint(1) default 0                 not null comment '是否删除（0=未删除, 1=已删除）',
    constraint fk_message_parent
        foreign key (parent_id) references message_board (id)
            on delete cascade,
    constraint fk_message_user
        foreign key (user_id) references user (user_id)
            on delete cascade
)
    comment '模块化留言板表';

create index idx_created_at
    on message_board (created_at);

create index idx_module
    on message_board (module_type, module_id, status);

create index idx_parent_id
    on message_board (parent_id);

create index idx_user_id
    on message_board (user_id);



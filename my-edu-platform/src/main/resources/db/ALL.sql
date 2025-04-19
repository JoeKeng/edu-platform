create table course_category
(
    category_id   int auto_increment comment '分类ID'
        primary key,
    category_name varchar(100)                        not null comment '分类名称',
    description   text                                null comment '分类描述',
    created_at    timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at    timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint category_name
        unique (category_name)
);

create table department
(
    department_id   int auto_increment comment '学院id'
        primary key,
    department_name varchar(100)                         not null comment '学院名称',
    description     text                                 null comment '学院概述',
    created_at      timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at      timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted      tinyint(1) default 0                 not null comment '是否逻辑删除',
    constraint department_name
        unique (department_name)
);

create table course
(
    course_id        int auto_increment comment '课程id'
        primary key,
    course_name      varchar(200)                         not null comment '课程名称',
    category_id      int                                  not null comment '课程分类ID',
    course_code      varchar(50)                          null comment '课程代码',
    department_id    int                                  null comment '开设学院id',
    description      text                                 null comment '课程描述',
    credits          decimal(3, 1)                        null comment '学分',
    syllabus         text                                 null comment '教学大纲(JSON格式)',
    created_at       timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at       timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted       tinyint(1) default 0                 not null comment '是否逻辑删除',
    cover            varchar(255)                         null comment '课程封面URL',
    start_date       date                                 null comment '开课日期',
    end_date         date                                 null comment '结课日期',
    enrollment_limit int                                  null comment '选课人数上限',
    enrollment_count int                                  null comment '选课人数',
    constraint course_code
        unique (course_code),
    constraint course_ibfk_2
        foreign key (department_id) references department (department_id),
    constraint fk_course_category
        foreign key (category_id) references course_category (category_id)
);

create index department_id
    on course (department_id);

create definer = root@localhost trigger before_insert_course_limit
    before insert
    on course
    for each row
BEGIN
    IF NEW.enrollment_limit IS NOT NULL AND NEW.enrollment_limit < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：选课人数上限不能小于 0';
    END IF;
END;

create table course_chapter
(
    chapter_id     int auto_increment comment '章节ID'
        primary key,
    course_id      int                                 not null comment '所属课程ID',
    parent_id      int                                 null comment '父章节ID，NULL 表示是主章节',
    chapter_number int                                 not null comment '章节序号',
    chapter_title  varchar(255)                        not null comment '章节标题',
    description    text                                null comment '章节描述',
    created_at     timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at     timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint course_id
        unique (course_id, parent_id, chapter_number),
    constraint course_chapter_ibfk_1
        foreign key (course_id) references course (course_id)
            on delete cascade,
    constraint course_chapter_ibfk_2
        foreign key (parent_id) references course_chapter (chapter_id)
            on delete cascade
);

create index parent_id
    on course_chapter (parent_id);

create table exam
(
    id          bigint auto_increment
        primary key,
    course_id   int                                   not null comment '关联课程ID',
    chapter_id  int                                   null comment '关联章节ID',
    teacher_id  bigint                                not null comment '出题教师ID',
    title       varchar(255)                          not null comment '考试标题',
    description text                                  null comment '考试说明',
    total_score int                                   not null comment '总分',
    duration    int                                   not null comment '考试时长(分钟)',
    start_time  datetime                              not null comment '开始时间',
    end_time    datetime                              not null comment '结束时间',
    status      varchar(20) default 'DRAFT'           not null comment '考试状态(DRAFT-草稿/PUBLISHED-已发布/ONGOING-进行中/ENDED-已结束)',
    created_at  datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at  datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint exam_ibfk_1
        foreign key (course_id) references course (course_id),
    constraint exam_ibfk_2
        foreign key (chapter_id) references course_chapter (chapter_id)
)
    comment '考试信息表';

create index idx_chapter_id
    on exam (chapter_id);

create index idx_course_id
    on exam (course_id);

create index idx_teacher_id
    on exam (teacher_id);

create table file_storage
(
    id              bigint auto_increment comment '文件ID'
        primary key,
    owner_id        bigint                              not null comment '归属ID（题目ID 或 学生作答ID）',
    file_owner_type enum ('QUESTION', 'ANSWER')         not null comment '文件归属类型（题目附件 / 学生作答附件）',
    file_url        text                                not null comment 'OSS存储地址',
    file_type       varchar(50)                         null comment '文件类型（image/pdf/doc/zip等）',
    uploaded_at     timestamp default CURRENT_TIMESTAMP null comment '上传时间'
)
    comment '文件存储表';

create table knowledge_point
(
    id          bigint auto_increment comment '知识点ID'
        primary key,
    name        varchar(100)                        not null comment '知识点名称',
    course_id   int                                 not null comment '所属课程ID',
    chapter_id  int                                 null comment '所属章节ID（可选）',
    parent_id   bigint                              null comment '父知识点ID（用于构建知识点体系）',
    description text                                null comment '知识点描述',
    created_at  timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at  timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint knowledge_point_ibfk_1
        foreign key (course_id) references course (course_id),
    constraint knowledge_point_ibfk_2
        foreign key (chapter_id) references course_chapter (chapter_id),
    constraint knowledge_point_ibfk_3
        foreign key (parent_id) references knowledge_point (id)
)
    comment '知识点表';

create index idx_course_chapter
    on knowledge_point (course_id, chapter_id);

create index idx_parent
    on knowledge_point (parent_id);

create table major
(
    major_id      int auto_increment comment '专业id'
        primary key,
    major_name    varchar(100)                         not null comment '专业名称',
    department_id int                                  not null comment '所属学院id',
    description   varchar(255)                         null comment '专业描述',
    created_at    timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at    timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted    tinyint(1) default 0                 not null comment '是否逻辑删除',
    constraint unique_major
        unique (major_name, department_id),
    constraint major_ibfk_1
        foreign key (department_id) references department (department_id)
            on delete cascade
);

create table notification
(
    id            bigint auto_increment comment '通知ID'
        primary key,
    type          int               not null comment '通知类型：1-课程通知，2-作业通知，3-互动通知，4-系统公告',
    title         varchar(100)      not null comment '通知标题',
    content       text              not null comment '通知内容',
    sender_id     bigint            null comment '发送者ID（系统通知为null）',
    receiver_id   bigint            null comment '接收者ID（群发通知为null）',
    receiver_type int               not null comment '接收者类型：1-个人，2-班级，3-全体用户',
    related_id    bigint            null comment '相关ID（如课程ID、作业ID等）',
    read_status   tinyint default 0 not null comment '是否已读：0-未读，1-已读',
    create_time   datetime          not null comment '创建时间',
    update_time   datetime          not null comment '更新时间',
    is_deleted    tinyint default 0 not null comment '是否删除：0-未删除，1-已删除'
)
    comment '通知表';

create index idx_create_time
    on notification (create_time);

create index idx_read_status
    on notification (read_status);

create index idx_receiver_id
    on notification (receiver_id);

create index idx_sender_id
    on notification (sender_id);

create table user
(
    user_id       bigint auto_increment comment '用户唯一标识'
        primary key,
    username      varchar(50)                          not null comment '用户名',
    email         varchar(100)                         null comment '邮箱地址',
    phone         varchar(20)                          null comment '手机号',
    password_hash varchar(255)                         not null comment '加密存储的密码',
    role          enum ('teacher', 'student')          not null comment '用户角色（教师或学生）',
    last_login    timestamp                            null comment '最近登录时间',
    status        tinyint    default 1                 null comment '状态：1 正常，0 禁用',
    created_at    timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at    timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted    tinyint(1) default 0                 not null comment '是否逻辑删除',
    image         varchar(255)                         null,
    constraint email
        unique (email),
    constraint username
        unique (username)
);

create table course_resource
(
    resource_id    int auto_increment comment '资源ID'
        primary key,
    course_id      int                                  not null comment '课程ID',
    chapter_id     int                                  null comment '章节ID（NULL表示课程级资源）',
    resource_name  varchar(255)                         not null comment '资源名称',
    resource_type  enum ('视频', 'PPT', 'PDF', '其他')  not null comment '资源类型',
    resource_url   varchar(500)                         not null comment '资源链接（存储文件URL）',
    upload_user_id bigint                               null comment '上传用户ID，允许 NULL',
    upload_time    timestamp  default CURRENT_TIMESTAMP null comment '上传时间',
    file_size      int                                  null comment '文件大小（字节）',
    description    text                                 null comment '资源描述',
    is_public      tinyint(1) default 1                 null comment '是否公开（1:公开，0:私有）',
    download_count int        default 0                 null comment '下载次数',
    created_at     timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at     timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint course_resource_ibfk_1
        foreign key (course_id) references course (course_id)
            on delete cascade,
    constraint course_resource_ibfk_2
        foreign key (chapter_id) references course_chapter (chapter_id)
            on delete set null,
    constraint course_resource_ibfk_3
        foreign key (upload_user_id) references user (user_id)
            on delete set null
);

create index idx_chapter_id
    on course_resource (chapter_id);

create index idx_course_id
    on course_resource (course_id);

create index idx_upload_user
    on course_resource (upload_user_id);

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

create table file_record
(
    id          bigint unsigned auto_increment comment '文件记录ID'
        primary key,
    file_name   varchar(255)                                                                       not null comment '文件名（带后缀）',
    file_path   varchar(512)                                                                       not null comment 'OSS上的存储路径',
    file_url    varchar(1024)                                                                      not null comment 'OSS访问URL',
    file_type   enum ('avatars', 'course-covers', 'resources', 'others') default 'others'          not null comment '文件类型',
    uploader_id bigint                                                                             not null comment '上传者ID（关联用户表）',
    upload_time datetime                                                 default CURRENT_TIMESTAMP not null comment '上传时间',
    constraint file_record_ibfk_1
        foreign key (uploader_id) references user (user_id)
            on update cascade
)
    comment '文件记录表';

create index idx_file_type
    on file_record (file_type);

create index idx_upload_time
    on file_record (upload_time);

create index idx_uploader_id
    on file_record (uploader_id);

create table student
(
    student_id    bigint auto_increment comment '学生id'
        primary key,
    user_id       bigint                               not null comment '对应 users(user_id)',
    student_no    varchar(50)                          not null comment '学号',
    student_name  varchar(100)                         not null comment '学生姓名',
    gender        int                                  null comment '1:男 , 2:女 , 3:保密',
    age           int                                  null comment '年龄',
    department_id int                                  null comment '所属学院id',
    major_id      int                                  null comment '专业id',
    class_id      int                                  null comment '班级id',
    student_photo varchar(255)                         null comment '学生照片',
    created_at    timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at    timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted    tinyint(1) default 0                 not null comment '是否逻辑删除',
    constraint student_no
        unique (student_no),
    constraint user_id
        unique (user_id),
    constraint student_ibfk_1
        foreign key (user_id) references user (user_id)
            on delete cascade
);

create table exam_record
(
    id          bigint auto_increment
        primary key,
    exam_id     bigint                                not null comment '考试ID',
    student_id  bigint                                not null comment '学生ID',
    start_time  datetime                              null comment '开始答题时间',
    submit_time datetime                              null comment '提交时间',
    total_score decimal(5, 2)                         null comment '总得分',
    status      varchar(20) default 'NOT_STARTED'     not null comment '状态(NOT_STARTED-未开始/IN_PROGRESS-进行中/SUBMITTED-已提交/GRADED-已批改)',
    created_at  datetime    default CURRENT_TIMESTAMP null,
    updated_at  datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_exam_student
        unique (exam_id, student_id),
    constraint exam_record_ibfk_1
        foreign key (exam_id) references exam (id),
    constraint exam_record_ibfk_2
        foreign key (student_id) references student (student_id)
)
    comment '学生考试记录表';

create index idx_student_id
    on exam_record (student_id);

create index department_id
    on student (department_id);

create index student_ibfk_4
    on student (major_id);

create definer = root@localhost trigger before_insert_student
    before insert
    on student
    for each row
BEGIN
    DECLARE major_dept_id INT;
    DECLARE class_major_id INT;
    DECLARE class_dept_id INT;

    -- 获取专业的学院 ID
    SELECT department_id INTO major_dept_id FROM major WHERE major_id = NEW.major_id;

    -- 获取班级的专业 ID 和学院 ID
    SELECT major_id, department_id INTO class_major_id, class_dept_id FROM class WHERE class_id = NEW.class_id;

    -- 校验学生的专业是否属于正确的学院
    IF NEW.department_id != major_dept_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：学生的专业与学院不匹配';
    END IF;

    -- 校验学生的班级是否属于正确的专业和学院
    IF NEW.major_id != class_major_id OR NEW.department_id != class_dept_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：学生的班级、专业和学院信息不匹配';
    END IF;
END;

create definer = root@localhost trigger before_update_student
    before update
    on student
    for each row
BEGIN
    DECLARE major_dept_id INT;
    DECLARE class_major_id INT;
    DECLARE class_dept_id INT;

    -- 获取新专业的学院 ID
    SELECT department_id INTO major_dept_id FROM major WHERE major_id = NEW.major_id;

    -- 获取新班级的专业 ID 和学院 ID
    SELECT major_id, department_id INTO class_major_id, class_dept_id FROM class WHERE class_id = NEW.class_id;

    -- 校验新的专业是否属于正确的学院
    IF NEW.department_id != major_dept_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：学生的专业与学院不匹配';
    END IF;

    -- 校验新的班级是否属于正确的专业和学院
    IF NEW.major_id != class_major_id OR NEW.department_id != class_dept_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：学生的班级、专业和学院信息不匹配';
    END IF;
END;

create table student_course
(
    student_id  bigint                                                        not null comment '学生ID',
    course_id   int                                                           not null comment '课程ID',
    enroll_date timestamp                           default CURRENT_TIMESTAMP not null comment '选课时间',
    status      enum ('进行中', '已完成', '未通过') default '进行中'          null comment '课程状态',
    primary key (student_id, course_id),
    constraint student_course_ibfk_1
        foreign key (student_id) references student (student_id)
            on delete cascade,
    constraint student_course_ibfk_2
        foreign key (course_id) references course (course_id)
            on delete cascade
);

create index course_id
    on student_course (course_id);

create table student_knowledge_mastery
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    student_id         bigint               not null comment '学生ID',
    knowledge_point_id bigint               not null comment '知识点ID',
    mastery_level      decimal(5, 2)        not null comment '掌握程度：0-100',
    question_count     int        default 0 not null comment '题目数量',
    correct_count      int        default 0 not null comment '正确数量',
    last_practice_time datetime             null comment '最后练习时间',
    created_at         datetime             not null comment '创建时间',
    updated_at         datetime             not null comment '更新时间',
    is_deleted         tinyint(1) default 0 not null comment '是否删除：0-未删除，1-已删除',
    constraint uk_student_knowledge
        unique (student_id, knowledge_point_id),
    constraint student_knowledge_mastery_ibfk_1
        foreign key (student_id) references student (student_id)
            on delete cascade
)
    comment '学生知识点掌握情况表';

create index idx_knowledge_point_id
    on student_knowledge_mastery (knowledge_point_id);

create index idx_student_id
    on student_knowledge_mastery (student_id);

create table student_learning_analysis
(
    id                     bigint auto_increment comment '主键ID'
        primary key,
    student_id             bigint               not null comment '学生ID',
    analysis_type          varchar(30)          not null comment '分析类型：HABIT, PROGRESS, STRENGTH_WEAKNESS, RECOMMENDATION, COMPREHENSIVE',
    learning_habit_data    json                 null comment '学习习惯分析数据',
    learning_progress_data json                 null comment '学习进度分析数据',
    strength_weakness_data json                 null comment '优势劣势分析数据',
    recommendations        json                 null comment '学习建议',
    analysis_time          datetime             not null comment '分析时间',
    created_at             datetime             not null comment '创建时间',
    updated_at             datetime             not null comment '更新时间',
    is_deleted             tinyint(1) default 0 not null comment '是否删除：0-未删除，1-已删除',
    constraint student_learning_analysis_ibfk_1
        foreign key (student_id) references student (student_id)
            on delete cascade
)
    comment '学生学习分析结果表';

create index idx_analysis_time
    on student_learning_analysis (analysis_time);

create index idx_analysis_type
    on student_learning_analysis (analysis_type);

create index idx_student_id
    on student_learning_analysis (student_id);

create table student_learning_behavior
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    student_id    bigint               not null comment '学生ID',
    behavior_type varchar(20)          not null comment '行为类型：LOGIN, STUDY, ASSIGNMENT, EXAM, RESOURCE',
    resource_type varchar(20)          null comment '资源类型：COURSE, VIDEO, DOCUMENT, QUESTION',
    resource_id   bigint               null comment '资源ID',
    start_time    datetime             not null comment '开始时间',
    end_time      datetime             not null comment '结束时间',
    duration      int                  null comment '持续时间（秒）',
    device_info   varchar(255)         null comment '设备信息',
    ip_address    varchar(50)          null comment 'IP地址',
    created_at    datetime             not null comment '创建时间',
    is_deleted    tinyint(1) default 0 not null comment '是否删除：0-未删除，1-已删除',
    constraint student_learning_behavior_ibfk_1
        foreign key (student_id) references student (student_id)
            on delete cascade
)
    comment '学生学习行为记录表';

create index idx_behavior_type
    on student_learning_behavior (behavior_type);

create index idx_start_time
    on student_learning_behavior (start_time);

create index idx_student_id
    on student_learning_behavior (student_id);

create table student_learning_goal
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    student_id    bigint               not null comment '学生ID',
    goal_type     varchar(20)          not null comment '目标类型：DAILY, WEEKLY, MONTHLY, COURSE',
    goal_content  varchar(255)         not null comment '目标内容',
    target_value  int                  not null comment '目标值',
    current_value int        default 0 not null comment '当前值',
    start_date    date                 not null comment '开始日期',
    end_date      date                 not null comment '结束日期',
    status        varchar(20)          not null comment '状态：NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED',
    created_at    datetime             not null comment '创建时间',
    updated_at    datetime             not null comment '更新时间',
    is_deleted    tinyint(1) default 0 not null comment '是否删除：0-未删除，1-已删除',
    constraint student_learning_goal_ibfk_1
        foreign key (student_id) references student (student_id)
            on delete cascade
)
    comment '学生学习目标表';

create index idx_goal_type
    on student_learning_goal (goal_type);

create index idx_status
    on student_learning_goal (status);

create index idx_student_id
    on student_learning_goal (student_id);

create table teacher
(
    teacher_id    bigint auto_increment comment '教师id'
        primary key,
    user_id       bigint                               not null comment '对应 users(user_id)',
    teacher_name  varchar(100)                         not null comment '教师姓名',
    department_id int                                  null comment '所属学院/部门id',
    title         varchar(50)                          null comment '职称',
    office        varchar(100)                         null comment '办公室地址',
    created_at    timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at    timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted    tinyint(1) default 0                 not null comment '是否逻辑删除',
    constraint user_id
        unique (user_id),
    constraint teacher_ibfk_1
        foreign key (user_id) references user (user_id)
            on delete cascade,
    constraint teacher_ibfk_2
        foreign key (department_id) references department (department_id)
);

create table assignment
(
    id           int auto_increment comment '作业ID'
        primary key,
    teacher_id   bigint                              not null comment '出题教师ID',
    course_id    int                                 not null comment '所属课程ID',
    chapter_id   int                                 null comment '章节ID',
    title        varchar(255)                        not null comment '作业名称',
    description  text                                null comment '作业描述',
    max_attempts int       default 1                 not null comment '学生最大提交次数',
    start_time   timestamp                           not null comment '作业开始时间',
    end_time     timestamp                           not null comment '作业截止时间',
    created_at   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at   timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint assignment_ibfk_1
        foreign key (teacher_id) references teacher (teacher_id),
    constraint assignment_ibfk_2
        foreign key (course_id) references course (course_id)
            on delete cascade,
    constraint assignment_idfk_3
        foreign key (chapter_id) references course_chapter (chapter_id)
            on delete cascade
);

create index course_id
    on assignment (course_id);

create index teacher_id
    on assignment (teacher_id);

create table class
(
    class_id      int auto_increment comment '班级id'
        primary key,
    class_name    varchar(100)                         not null comment '班级名称',
    department_id int                                  null comment '所属学院id',
    major_id      int                                  null comment '所属专业id',
    grade         varchar(20)                          null comment '年级',
    class_size    int        default 0                 null comment '班级人数',
    created_at    timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at    timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted    tinyint(1) default 0                 not null comment '是否逻辑删除',
    advisor_id    bigint                               null comment '辅导员或班主任（关联 teachers.teacher_id）',
    constraint idx_class_major_department
        unique (class_id, major_id, department_id),
    constraint unique_class_name
        unique (class_name, grade, major_id),
    constraint class_ibfk_1
        foreign key (department_id) references department (department_id),
    constraint class_ibfk_2
        foreign key (advisor_id) references teacher (teacher_id),
    constraint class_ibfk_3
        foreign key (major_id) references major (major_id)
            on delete set null
);

create index advisor_id
    on class (advisor_id);

create index department_id
    on class (department_id);

create definer = root@localhost trigger before_insert_class
    before insert
    on class
    for each row
BEGIN
    DECLARE major_dept_id INT;

    -- 获取专业所属的学院 ID
    SELECT department_id INTO major_dept_id FROM major WHERE major_id = NEW.major_id;

    -- 如果班级的学院 ID 和专业的学院 ID 不匹配，则报错
    IF NEW.department_id != major_dept_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：班级的学院与专业的学院不匹配';
    END IF;
END;

create definer = root@localhost trigger before_update_class
    before update
    on class
    for each row
BEGIN
    DECLARE major_dept_id INT;

    -- 获取新专业所属的学院 ID
    SELECT department_id INTO major_dept_id FROM major WHERE major_id = NEW.major_id;

    -- 如果新的学院 ID 和专业的学院 ID 不匹配，则报错
    IF NEW.department_id != major_dept_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：班级的学院与专业的学院不匹配';
    END IF;
END;

create table course_class
(
    course_id  int                                  not null,
    class_id   int                                  not null,
    created_at timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted tinyint(1) default 0                 not null comment '是否逻辑删除',
    primary key (course_id, class_id),
    constraint course_class_ibfk_1
        foreign key (course_id) references course (course_id)
            on delete cascade,
    constraint course_class_ibfk_2
        foreign key (class_id) references class (class_id)
            on delete cascade
);

create index class_id
    on course_class (class_id);

create definer = root@localhost trigger before_insert_course_class
    before insert
    on course_class
    for each row
BEGIN
    DECLARE course_dept INT;
    DECLARE class_dept INT;

    -- 获取课程的学院 ID
    SELECT department_id INTO course_dept FROM course WHERE course_id = NEW.course_id;

    -- 获取班级的学院 ID
    SELECT department_id INTO class_dept FROM class WHERE class_id = NEW.class_id;

    -- 如果班级和课程的学院不匹配，则报错
    IF course_dept IS NULL OR class_dept IS NULL OR course_dept != class_dept THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '错误：班级的学院与课程的学院不匹配，无法选课';
    END IF;
END;

create table course_teacher
(
    course_id  int                                             not null comment '课程ID',
    teacher_id bigint                                          not null comment '教师ID',
    role       enum ('主讲', '助教') default '主讲'            not null comment '教师角色',
    created_at timestamp             default CURRENT_TIMESTAMP not null comment '创建时间',
    primary key (course_id, teacher_id),
    constraint course_teacher_course_fk
        foreign key (course_id) references course (course_id)
            on delete cascade,
    constraint course_teacher_teacher_fk
        foreign key (teacher_id) references teacher (teacher_id)
            on delete cascade
);

create table exam_class
(
    id         bigint auto_increment
        primary key,
    exam_id    bigint                             not null comment '考试ID',
    class_id   int                                not null comment '班级ID',
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_exam_class
        unique (exam_id, class_id),
    constraint exam_class_ibfk_1
        foreign key (exam_id) references exam (id),
    constraint exam_class_ibfk_2
        foreign key (class_id) references class (class_id)
)
    comment '考试-班级关联表';

create index idx_class_id
    on exam_class (class_id);

create table question_bank
(
    id             bigint auto_increment comment '题目ID'
        primary key,
    course_id      int                                                       not null comment '所属课程ID',
    chapter_id     int                                                       not null comment '所属章节ID',
    teacher_id     bigint                                                    not null comment '出题教师ID',
    type           enum ('单选题', '多选题', '填空题', '简答题', '编程题')   not null comment '题目类型',
    question_text  text                                                      not null comment '题目内容',
    options        json                                                      null comment '选项（仅选择题需要）',
    correct_answer text                                                      not null comment '标准答案',
    analysis       text                                                      null comment '解析（错误反馈）',
    difficulty     enum ('easy', 'medium', 'hard') default 'medium'          null comment '题目难度',
    created_at     timestamp                       default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at     timestamp                       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint question_bank_ibfk_1
        foreign key (course_id) references course (course_id),
    constraint question_bank_ibfk_2
        foreign key (teacher_id) references teacher (teacher_id),
    constraint question_bank_idfk_3
        foreign key (chapter_id) references course_chapter (chapter_id)
)
    comment '题库表';

create table answer_analysis
(
    id                    bigint auto_increment comment '分析ID'
        primary key,
    student_id            bigint                              not null comment '学生ID',
    question_id           bigint                              not null comment '题目ID',
    total_attempts        int       default 1                 null comment '总答题次数',
    correct_attempts      int       default 0                 null comment '正确次数',
    first_attempt_score   decimal(4, 1)                       null comment '首次答题得分',
    last_score            decimal(5, 2)                       null comment '最近一次得分',
    recommended_materials text                                null comment '推荐学习材料',
    updated_at            timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint answer_analysis_ibfk_1
        foreign key (student_id) references student (student_id),
    constraint answer_analysis_ibfk_2
        foreign key (question_id) references question_bank (id)
)
    comment '作答分析表';

create index question_id
    on answer_analysis (question_id);

create index student_id
    on answer_analysis (student_id);

create table assignment_question
(
    assignment_id  int    not null comment '作业ID',
    question_id    bigint not null comment '题目ID',
    question_order int    not null comment '题目顺序',
    primary key (assignment_id, question_id),
    constraint assignment_question_ibfk_1
        foreign key (assignment_id) references assignment (id)
            on delete cascade,
    constraint assignment_question_ibfk_2
        foreign key (question_id) references question_bank (id)
            on delete cascade
);

create index question_id
    on assignment_question (question_id);

create table exam_answer
(
    id               bigint auto_increment
        primary key,
    exam_id          bigint                              not null comment '考试ID',
    student_id       bigint                              not null comment '学生ID',
    question_id      bigint                              not null comment '题目ID',
    answer           text                                null comment '学生答案',
    is_correct       tinyint  default 0                  null comment '是否正确',
    score            decimal(5, 2)                       null comment '得分',
    ai_feedback      text                                null comment 'AI评语',
    explanation      text                                null comment 'AI解析',
    teacher_feedback text                                null comment '教师评语',
    submit_time      datetime default CURRENT_TIMESTAMP  null comment '提交时间',
    created_at       datetime default CURRENT_TIMESTAMP  null,
    updated_at       datetime default CURRENT_TIMESTAMP  null on update CURRENT_TIMESTAMP,
    status           enum ('未提交', '已提交', '已批改') null comment '状态：0-未提交，1-已提交，2-已批改',
    constraint exam_answer_ibfk_1
        foreign key (exam_id) references exam (id),
    constraint exam_answer_ibfk_2
        foreign key (student_id) references student (student_id),
    constraint exam_answer_ibfk_3
        foreign key (question_id) references question_bank (id)
)
    comment '考试答题记录表';

create index idx_exam_student
    on exam_answer (exam_id, student_id);

create index idx_question_id
    on exam_answer (question_id);

create index student_id
    on exam_answer (student_id);

create table exam_question
(
    id            bigint auto_increment
        primary key,
    exam_id       bigint                             not null comment '考试ID',
    question_id   bigint                             not null comment '题目ID',
    score         int                                not null comment '题目分值',
    order_num     int                                not null comment '题目顺序',
    question_type varchar(50)                        not null comment '题目类型(单选/多选/判断/填空/主观题等)',
    section       varchar(50)                        null comment '所属试卷部分(选择题部分/主观题部分等)',
    created_at    datetime default CURRENT_TIMESTAMP null,
    updated_at    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint exam_question_ibfk_1
        foreign key (exam_id) references exam (id),
    constraint exam_question_ibfk_2
        foreign key (question_id) references question_bank (id)
)
    comment '考试题目关联表';

create index idx_exam_id
    on exam_question (exam_id);

create index idx_question_id
    on exam_question (question_id);

create index course_id
    on question_bank (course_id);

create index teacher_id
    on question_bank (teacher_id);

create table question_knowledge_point
(
    id                 bigint auto_increment comment '关联ID'
        primary key,
    question_id        bigint                                  not null comment '题目ID',
    knowledge_point_id bigint                                  not null comment '知识点ID',
    weight             decimal(3, 2) default 1.00              null comment '权重（用于表示该题目对知识点的考察程度）',
    created_at         timestamp     default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_question_point
        unique (question_id, knowledge_point_id),
    constraint question_knowledge_point_ibfk_1
        foreign key (question_id) references question_bank (id),
    constraint question_knowledge_point_ibfk_2
        foreign key (knowledge_point_id) references knowledge_point (id)
)
    comment '题目-知识点关联表';

create index idx_knowledge_point
    on question_knowledge_point (knowledge_point_id);

create table student_answer
(
    id               bigint auto_increment comment '作答ID'
        primary key,
    student_id       bigint                              not null comment '学生ID',
    question_id      bigint                              not null comment '题目ID',
    assignment_id    int                                 null comment '作业ID',
    answer           text                                not null comment '学生答案',
    ai_score         decimal(4, 1)                       null comment '得分',
    teacher_score    double                              null,
    ai_feedback      text                                null comment 'AI 反馈',
    teacher_feedback text                                null,
    is_correct       tinyint(1)                          null comment '是否正确',
    submitted_at     timestamp default CURRENT_TIMESTAMP null comment '提交时间',
    explanation      text                                null comment 'AI 题目解析',
    constraint student_answer_ibfk_1
        foreign key (student_id) references student (student_id),
    constraint student_answer_ibfk_2
        foreign key (question_id) references question_bank (id),
    constraint student_answer_idfk_3
        foreign key (assignment_id) references assignment (id)
            on delete cascade
)
    comment '学生作答表';

create index question_id
    on student_answer (question_id);

create index student_id
    on student_answer (student_id);

create index department_id
    on teacher (department_id);

create table user_login_log
(
    log_id      bigint auto_increment comment '日志id'
        primary key,
    user_id     bigint                               not null comment '用户id',
    login_time  datetime   default CURRENT_TIMESTAMP null comment '登录时间',
    ip_address  varchar(45)                          null comment '登录ip地址',
    device_info varchar(255)                         null comment '设备信息',
    created_at  timestamp  default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_deleted  tinyint(1) default 0                 not null comment '是否逻辑删除',
    constraint user_login_log_ibfk_1
        foreign key (user_id) references user (user_id)
);

create index user_id
    on user_login_log (user_id);

create table wrong_question
(
    id                  bigint auto_increment comment '错题记录ID'
        primary key,
    user_id             bigint                                              not null comment '用户ID',
    question_id         bigint                                              not null comment '题目ID',
    assignment_id       bigint                                              null comment '所属作业ID（可选）',
    wrong_answer        text                                                not null comment '学生错误答案',
    correct_answer      text                                                not null comment '正确答案',
    analysis            text                                                null comment '解析',
    wrong_type          enum ('concept', 'calculation', 'misread', 'other') not null comment '错误类型（概念错误、计算错误、审题错误等）',
    attempt_count       int       default 1                                 not null comment '答题尝试次数',
    first_attempt_score decimal(5, 2)                                       null comment '首次得分',
    last_attempt_score  decimal(5, 2)                                       null comment '最近一次得分',
    created_at          timestamp default CURRENT_TIMESTAMP                 not null comment '记录时间',
    updated_at          timestamp default CURRENT_TIMESTAMP                 not null on update CURRENT_TIMESTAMP comment '最后更新时间',
    is_deleted          tinyint   default 0                                 not null comment '是否逻辑删除',
    constraint uq_wrong_question
        unique (user_id, question_id),
    constraint wrong_question_ibfk_1
        foreign key (user_id) references user (user_id)
            on delete cascade
)
    comment '错题本表';

create index idx_user_wrong
    on wrong_question (user_id);



-- liquibase formatted sql

-- changeset markusk:1609000655972-1
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--rollback DROP EXTENSION IF EXISTS "uuid-ossp";

-- changeset markusk:1609000655972-2
create table if not exists targets
(
    target_snowflake uuid    default uuid_generate_v4() not null,
    name             varchar(50)                        not null,
    shortname        varchar(3)                         not null,
    fetch_url        text                               not null,
    tor              boolean default false              not null,
    wait_time        integer default 5                  not null,
    active           boolean default true               not null,
    constraint targets_pk
    primary key (target_snowflake)
    );

comment on column targets.wait_time is 'time in minutes';

create unique index if not exists targets_shortname_uindex
    on targets (shortname);

create unique index if not exists targets_target_snowflake_uindex
    on targets (target_snowflake);
--rollback DROP TABLE targets

-- changeset markusk:1609000655972-3
create table if not exists articles
(
    article_snowflake uuid      default uuid_generate_v4() not null,
    article_id        text                                 not null,
    target_snowflake  uuid                                 not null,
    title             text                                 not null,
    url               text                                 not null,
    release_time      timestamp                            not null,
    fetch_time        timestamp default CURRENT_TIMESTAMP  not null,
    constraint articles_pk
    primary key (article_snowflake),
    constraint articles_targets_target_snowflake_fk
    foreign key (target_snowflake) references targets
    on update cascade on delete cascade
    );

create unique index if not exists articles_article_snowflake_uindex
    on articles (article_snowflake);

create unique index if not exists articles_article_id_uindex
    on articles (article_id);
--rollback DROP TABLE articles

-- changeset markusk:1609000655972-4
create table if not exists fetch_offsets
(
    offset_snowflake uuid default uuid_generate_v4() not null,
    target_snowflake uuid                            not null,
    "offset"         varchar(5)                      not null,
    constraint fetch_offsets_pk
    primary key (offset_snowflake),
    constraint fetch_offsets_targets_target_snowflake_fk
    foreign key (target_snowflake) references targets
    on update cascade on delete cascade
    );

create unique index if not exists fetch_offsets_offset_snowflake_uindex
    on fetch_offsets (offset_snowflake);
--rollback DROP TABLE fetch_offsets

-- changeset markusk:1609000655972-5
create table if not exists tags
(
    tag_snowflake uuid default uuid_generate_v4() not null,
    tag           text                            not null,
    constraint tags_pk
    primary key (tag_snowflake)
    );

create unique index if not exists tags_tag_snowflake_uindex
    on tags (tag_snowflake);

create unique index if not exists tags_tag_uindex
    on tags (tag);
--rollback DROP TABLE tags

-- changeset markusk:1609000655972-6
create table if not exists article_tags
(
    link_snowflake    uuid default uuid_generate_v4() not null,
    article_snowflake uuid                            not null,
    tag_snowflake     uuid                            not null,
    constraint article_tags_pk
    primary key (link_snowflake),
    constraint article_tags_articles_article_snowflake_fk
    foreign key (article_snowflake) references articles
    on update cascade on delete cascade,
    constraint article_tags_tags_tag_snowflake_fk
    foreign key (tag_snowflake) references tags
    on update cascade on delete cascade
    );

create unique index if not exists article_tags_link_snowflake_uindex
    on article_tags (link_snowflake);
--rollback DROP TABLE article_tags

-- changeset markusk:1609000655972-7
create table if not exists versions
(
    version_snowflake uuid default uuid_generate_v4() not null,
    article_snowflake uuid                            not null,
    version_id        varchar(55)                     not null,
    version           integer                         not null,
    update_time       timestamp                       not null,
    auto_offset       varchar(5),
    constraint versions_pk
    primary key (version_snowflake),
    constraint versions_articles_article_snowflake_fk
    foreign key (article_snowflake) references articles
    on update cascade on delete cascade
    );

create unique index if not exists versions_version_id_uindex
    on versions (version_id);

create unique index if not exists versions_version_snowflake_uindex
    on versions (version_snowflake);
--rollback DROP TABLE versions

-- changeset markusk:1609000655972-8
create table if not exists status
(
    article_snowflake uuid      not null,
    status            text      not null,
    update_time       timestamp not null,
    constraint status_pk
    primary key (article_snowflake),
    constraint status_articles_article_snowflake_fk
    foreign key (article_snowflake) references articles
    on update cascade on delete cascade
    );

create unique index if not exists status_article_snowflake_uindex
    on status (article_snowflake);
--rollback DROP TABLE status

-- changeset markusk:1609000655972-9
create table if not exists user_agents
(
    user_agent_snowflake uuid default uuid_generate_v4() not null,
    user_agent           text                            not null,
    constraint user_agents_pk
    primary key (user_agent_snowflake)
    );

create unique index if not exists user_agents_user_agent_snowflake_uindex
    on user_agents (user_agent_snowflake);
--rollback DROP TABLE user_agents
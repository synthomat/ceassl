create table targets
(
    id          varchar(50),
    host        varchar(255)            not null,
    created_at  TIMESTAMP(0) WITH TIME ZONE default now() not null,
    updated_at  TIMESTAMP(0) WITH TIME ZONE default now(),
    last_check  TIMESTAMP(0) WITH TIME ZONE,
    valid_until TIMESTAMP(0) WITH TIME ZONE,

    constraint targets_pk
        primary key (id)
);
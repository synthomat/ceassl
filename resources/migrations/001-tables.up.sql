create table targets
(
    id          varchar(50),
    host        varchar(255)            not null,
    created_at  timestamp default now() not null,
    updated_at  timestamp default now(),
    last_check  timestamp,
    valid_until timestamp,

    constraint targets_pk
        primary key (id)
);
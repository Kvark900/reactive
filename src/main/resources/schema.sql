CREATE SCHEMA reactive;
SET search_path = reactive;


CREATE TABLE product (
    id         bigserial
        CONSTRAINT product_pkey
            PRIMARY KEY,
    product_id integer          NOT NULL,
    price      double precision NOT NULL
);

ALTER TABLE product
    OWNER TO postgres;


CREATE TABLE request (
    request_id          bigserial NOT NULL
        CONSTRAINT request_pkey
            PRIMARY KEY,
    url                 varchar   NOT NULL,
    response_time       bigint    NOT NULL,
    request_executed_at timestamp
);

ALTER TABLE request
    OWNER TO postgres;


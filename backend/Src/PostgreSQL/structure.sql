-- This script was generated by the ERD tool in pgAdmin 4.
-- Please log an issue at https://redmine.postgresql.org/projects/pgadmin4/issues/new if you find any bugs, including reproduction steps.
BEGIN;


CREATE TABLE IF NOT EXISTS public.message
(
    id integer NOT NULL DEFAULT nextval('message_id_seq'::regclass),
    user_id integer,
    content character varying(500) COLLATE pg_catalog."default",
    time_created timestamp without time zone,
    image_type numeric,
    CONSTRAINT message_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.message_reaction
(
    id integer NOT NULL DEFAULT nextval('message_reaction_id_seq'::regclass),
    type numeric,
    user_id integer,
    "time" timestamp without time zone,
    message_id integer,
    CONSTRAINT message_reaction_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.message_received
(
    id integer NOT NULL DEFAULT nextval('message_received_id_seq'::regclass),
    user_id integer,
    message_id integer,
    CONSTRAINT message_received_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public."user"
(
    id integer NOT NULL DEFAULT nextval('user_id_seq'::regclass),
    username text COLLATE pg_catalog."default" NOT NULL,
    passwd text COLLATE pg_catalog."default",
    email text COLLATE pg_catalog."default",
    bt_mac text COLLATE pg_catalog."default",
    token text COLLATE pg_catalog."default",
    token_exp_date date,
    token_exp_time time without time zone,
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT unique_username UNIQUE (username)
        INCLUDE(username)
);

ALTER TABLE IF EXISTS public.message
    ADD CONSTRAINT message_user_id_fkey FOREIGN KEY (user_id)
    REFERENCES public."user" (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.message_reaction
    ADD CONSTRAINT fk_message FOREIGN KEY (message_id)
    REFERENCES public.message (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.message_reaction
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id)
    REFERENCES public."user" (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.message_received
    ADD CONSTRAINT fk_message FOREIGN KEY (message_id)
    REFERENCES public.message (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public.message_received
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id)
    REFERENCES public."user" (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

END;
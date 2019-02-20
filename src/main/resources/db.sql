--
-- PostgreSQL database dump
--

-- Dumped from database version 11.1
-- Dumped by pg_dump version 11.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: machines; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.machines (
    id bigint NOT NULL,
    name character varying(255),
    created_at timestamp without time zone,
    user_id bigint,
    inserted_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    api_salt character varying(255),
    active boolean DEFAULT true NOT NULL
);


ALTER TABLE public.machines OWNER TO postgres;

--
-- Name: machines_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.machines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.machines_id_seq OWNER TO postgres;

--
-- Name: machines_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.machines_id_seq OWNED BY public.machines.id;


--
-- Name: pulses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pulses (
    id bigint NOT NULL,
    sent_at timestamp without time zone,
    user_id bigint,
    inserted_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    machine_id bigint,
    sent_at_local timestamp without time zone,
    tz_offset smallint,
    pulse_id character varying NOT NULL,
    lang_name character varying NOT NULL,
    xp_amount integer NOT NULL
);


ALTER TABLE public.pulses OWNER TO postgres;

--
-- Name: pulses_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pulses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pulses_id_seq OWNER TO postgres;

--
-- Name: pulses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pulses_id_seq OWNED BY public.pulses.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    username character varying(255) NOT NULL,
    email character varying(255),
    password character varying(255) NOT NULL,
    inserted_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    last_cached timestamp without time zone,
    private_profile boolean DEFAULT false NOT NULL,
    cache character varying(500)
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: machines id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.machines ALTER COLUMN id SET DEFAULT nextval('public.machines_id_seq'::regclass);


--
-- Name: pulses id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pulses ALTER COLUMN id SET DEFAULT nextval('public.pulses_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: machines; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.machines (id, name, created_at, user_id, inserted_at, updated_at, api_salt, active) FROM stdin;
4	is	2019-02-17 23:00:03.729	11	2019-02-17 23:00:03.729	2019-02-17 23:00:03.729	p61vengusb5v08gqh8a227vf7rarvisp	t
\.


--
-- Data for Name: pulses; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pulses (id, sent_at, user_id, inserted_at, updated_at, machine_id, sent_at_local, tz_offset, pulse_id, lang_name, xp_amount) FROM stdin;
2	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:14:48.786	4	2019-02-18 01:14:48.786	3	sgif9a7a0o49fu86ci9o0hq2	Java	15
3	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:15:09.619	4	2019-02-18 01:15:09.619	3	38ego8qcac8mgfrc28t2mvsv	Java	15
4	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:18:26.658	4	2019-02-18 01:18:26.658	3	3fqekuk9dun0kbmjnifkobl6	Java	15
5	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:18:26.659	4	2019-02-18 01:18:26.659	3	3fqekuk9dun0kbmjnifkobl6	scala	15
6	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:28:03.865	4	2019-02-18 01:28:03.865	3	ibddjfds5g8s2f9i15bteg2v	Java	15
7	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:28:03.866	4	2019-02-18 01:28:03.866	3	ibddjfds5g8s2f9i15bteg2v	scala	15
8	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:30:02.995	4	2019-02-18 01:30:02.995	3	eu2prgvtg5ajme0e2eekhdv1	Java	15
9	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:30:02.996	4	2019-02-18 01:30:02.996	3	eu2prgvtg5ajme0e2eekhdv1	scala	15
10	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:31:03.041	4	2019-02-18 01:31:03.041	3	u3qoardutb1nu48v6lhkr1k4	Java	15
11	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:31:03.042	4	2019-02-18 01:31:03.042	3	u3qoardutb1nu48v6lhkr1k4	scala	15
12	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:31:48.095	4	2019-02-18 01:31:48.095	3	43uvmfj230p9d8efpgu9v7g6	Java	15
13	2019-02-17 23:00:03	11	2019-02-17 23:00:03	2019-02-18 01:31:48.095	4	2019-02-18 01:31:48.095	3	43uvmfj230p9d8efpgu9v7g6	scala	15
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, username, email, password, inserted_at, updated_at, last_cached, private_profile, cache) FROM stdin;
11	alikemal	ali@alikemal.org	123456	2019-02-17 19:08:34.743	2019-02-17 19:08:34.743	2019-02-17 19:08:34.743	f	\N
\.


--
-- Name: machines_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.machines_id_seq', 1, false);


--
-- Name: pulses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pulses_id_seq', 13, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 1, false);


--
-- Name: machines machines_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.machines
    ADD CONSTRAINT machines_pkey PRIMARY KEY (id);


--
-- Name: pulses pulses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pulses
    ADD CONSTRAINT pulses_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: machines_name_user_id_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX machines_name_user_id_index ON public.machines USING btree (name, user_id);


--
-- Name: machines_user_id_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX machines_user_id_index ON public.machines USING btree (user_id);


--
-- Name: pulses_sent_at_DESC_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX "pulses_sent_at_DESC_index" ON public.pulses USING btree (sent_at DESC);


--
-- Name: pulses_user_id_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX pulses_user_id_index ON public.pulses USING btree (user_id);


--
-- Name: users_lower_username_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX users_lower_username_index ON public.users USING btree (lower((username)::text));


--
-- Name: users_username_index; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX users_username_index ON public.users USING btree (username);


--
-- Name: machines machines_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.machines
    ADD CONSTRAINT machines_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: pulses pulses_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pulses
    ADD CONSTRAINT pulses_machine_id_fkey FOREIGN KEY (machine_id) REFERENCES public.machines(id) ON DELETE CASCADE;


--
-- Name: pulses pulses_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pulses
    ADD CONSTRAINT pulses_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--
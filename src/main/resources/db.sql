--
-- PostgreSQL database dump
--

-- Dumped from database version 10.4
-- Dumped by pg_dump version 10.5

-- Started on 2018-09-25 22:51:52 +03

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
-- TOC entry 200 (class 1259 OID 17463)
-- Name: languages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.languages (
    id bigint NOT NULL,
    name character varying(255),
    inserted_at timestamp without time zone default CURRENT_TIMESTAMP,
    updated_at timestamp without time zone NOT NULL,
    alias_of_id bigint
);


--
-- TOC entry 199 (class 1259 OID 17461)
-- Name: languages_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.languages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3216 (class 0 OID 0)
-- Dependencies: 199
-- Name: languages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.languages_id_seq OWNED BY public.languages.id;


--
-- TOC entry 206 (class 1259 OID 17531)
-- Name: machines; Type: TABLE; Schema: public; Owner: -
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


--
-- TOC entry 205 (class 1259 OID 17529)
-- Name: machines_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.machines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3217 (class 0 OID 0)
-- Dependencies: 205
-- Name: machines_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.machines_id_seq OWNED BY public.machines.id;


--
-- TOC entry 208 (class 1259 OID 17614)
-- Name: password_resets; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.password_resets (
    id bigint NOT NULL,
    token uuid,
    user_id bigint,
    inserted_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- TOC entry 207 (class 1259 OID 17612)
-- Name: password_resets_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.password_resets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3218 (class 0 OID 0)
-- Dependencies: 207
-- Name: password_resets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.password_resets_id_seq OWNED BY public.password_resets.id;


--
-- TOC entry 202 (class 1259 OID 17471)
-- Name: pulses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.pulses (
    id bigint NOT NULL,
    sent_at timestamp without time zone,
    user_id bigint,
    inserted_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    machine_id bigint,
    sent_at_local timestamp without time zone,
    tz_offset smallint
);


--
-- TOC entry 201 (class 1259 OID 17469)
-- Name: pulses_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.pulses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3219 (class 0 OID 0)
-- Dependencies: 201
-- Name: pulses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.pulses_id_seq OWNED BY public.pulses.id;


--
-- TOC entry 196 (class 1259 OID 17445)
-- Name: schema_migrations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schema_migrations (
    version bigint NOT NULL,
    inserted_at timestamp without time zone
);


--
-- TOC entry 198 (class 1259 OID 17452)
-- Name: users; Type: TABLE; Schema: public; Owner: -
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
    cache jsonb
);


--
-- TOC entry 197 (class 1259 OID 17450)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3220 (class 0 OID 0)
-- Dependencies: 197
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 204 (class 1259 OID 17485)
-- Name: xps; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.xps (
    id bigint NOT NULL,
    amount integer,
    pulse_id bigint,
    language_id bigint,
    inserted_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    original_language_id bigint
);


--
-- TOC entry 203 (class 1259 OID 17483)
-- Name: xps_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.xps_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3221 (class 0 OID 0)
-- Dependencies: 203
-- Name: xps_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.xps_id_seq OWNED BY public.xps.id;


--
-- TOC entry 3036 (class 2604 OID 17466)
-- Name: languages id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.languages ALTER COLUMN id SET DEFAULT nextval('public.languages_id_seq'::regclass);


--
-- TOC entry 3039 (class 2604 OID 17534)
-- Name: machines id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.machines ALTER COLUMN id SET DEFAULT nextval('public.machines_id_seq'::regclass);


--
-- TOC entry 3041 (class 2604 OID 17617)
-- Name: password_resets id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.password_resets ALTER COLUMN id SET DEFAULT nextval('public.password_resets_id_seq'::regclass);


--
-- TOC entry 3037 (class 2604 OID 17603)
-- Name: pulses id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pulses ALTER COLUMN id SET DEFAULT nextval('public.pulses_id_seq'::regclass);


--
-- TOC entry 3034 (class 2604 OID 17455)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3038 (class 2604 OID 17594)
-- Name: xps id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.xps ALTER COLUMN id SET DEFAULT nextval('public.xps_id_seq'::regclass);


--
-- TOC entry 3201 (class 0 OID 17463)
-- Dependencies: 200
-- Data for Name: languages; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.languages (id, name, inserted_at, updated_at, alias_of_id) FROM stdin;
\.


--
-- TOC entry 3207 (class 0 OID 17531)
-- Dependencies: 206
-- Data for Name: machines; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.machines (id, name, created_at, user_id, inserted_at, updated_at, api_salt, active) FROM stdin;
\.


--
-- TOC entry 3209 (class 0 OID 17614)
-- Dependencies: 208
-- Data for Name: password_resets; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.password_resets (id, token, user_id, inserted_at, updated_at) FROM stdin;
\.


--
-- TOC entry 3203 (class 0 OID 17471)
-- Dependencies: 202
-- Data for Name: pulses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.pulses (id, sent_at, user_id, inserted_at, updated_at, machine_id, sent_at_local, tz_offset) FROM stdin;
\.


--
-- TOC entry 3197 (class 0 OID 17445)
-- Dependencies: 196
-- Data for Name: schema_migrations; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.schema_migrations (version, inserted_at) FROM stdin;
20160330182048	2018-09-25 18:57:06.238902
20160330183130	2018-09-25 18:57:06.28035
20160330183920	2018-09-25 18:57:06.3298
20160330184112	2018-09-25 18:57:06.359198
20160409113405	2018-09-25 18:57:06.391816
20160409165848	2018-09-25 18:57:06.418177
20160411182338	2018-09-25 18:57:06.454497
20160411183300	2018-09-25 18:57:06.489616
20160411183703	2018-09-25 18:57:06.506995
20160411194736	2018-09-25 18:57:06.546332
20160413173856	2018-09-25 18:57:06.587174
20160413185340	2018-09-25 18:57:06.633938
20160413192028	2018-09-25 18:57:06.657782
20160418181234	2018-09-25 18:57:06.689651
20160424144837	2018-09-25 18:57:06.732236
20160424150909	2018-09-25 18:57:06.75396
20160528111628	2018-09-25 18:57:06.783445
20160604203814	2018-09-25 18:57:06.813491
20160608192828	2018-09-25 18:57:06.830375
20160608193609	2018-09-25 18:57:06.868433
20160716121928	2018-09-25 18:57:06.917523
20160717133814	2018-09-25 18:57:06.94083
20160717194423	2018-09-25 18:57:06.963526
20160725180410	2018-09-25 18:57:07.004227
20160912155303	2018-09-25 18:57:07.038549
20160919194553	2018-09-25 18:57:07.070136
20161229212230	2018-09-25 18:57:07.095847
20170401171439	2018-09-25 18:57:07.123549
20171017175938	2018-09-25 18:57:07.158497
\.


--
-- TOC entry 3199 (class 0 OID 17452)
-- Dependencies: 198
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, username, email, password, inserted_at, updated_at, last_cached, private_profile, cache) FROM stdin;
\.


--
-- TOC entry 3205 (class 0 OID 17485)
-- Dependencies: 204
-- Data for Name: xps; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.xps (id, amount, pulse_id, language_id, inserted_at, updated_at, original_language_id) FROM stdin;
\.


--
-- TOC entry 3222 (class 0 OID 0)
-- Dependencies: 199
-- Name: languages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.languages_id_seq', 1, false);


--
-- TOC entry 3223 (class 0 OID 0)
-- Dependencies: 205
-- Name: machines_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.machines_id_seq', 1, false);


--
-- TOC entry 3224 (class 0 OID 0)
-- Dependencies: 207
-- Name: password_resets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.password_resets_id_seq', 1, false);


--
-- TOC entry 3225 (class 0 OID 0)
-- Dependencies: 201
-- Name: pulses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.pulses_id_seq', 1, false);


--
-- TOC entry 3226 (class 0 OID 0)
-- Dependencies: 197
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 1, false);


--
-- TOC entry 3227 (class 0 OID 0)
-- Dependencies: 203
-- Name: xps_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.xps_id_seq', 1, false);


--
-- TOC entry 3051 (class 2606 OID 17468)
-- Name: languages languages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.languages
    ADD CONSTRAINT languages_pkey PRIMARY KEY (id,'name');


--
-- TOC entry 3062 (class 2606 OID 17536)
-- Name: machines machines_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.machines
    ADD CONSTRAINT machines_pkey PRIMARY KEY (id);


--
-- TOC entry 3065 (class 2606 OID 17619)
-- Name: password_resets password_resets_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.password_resets
    ADD CONSTRAINT password_resets_pkey PRIMARY KEY (id);


--
-- TOC entry 3053 (class 2606 OID 17605)
-- Name: pulses pulses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pulses
    ADD CONSTRAINT pulses_pkey PRIMARY KEY (id);


--
-- TOC entry 3043 (class 2606 OID 17449)
-- Name: schema_migrations schema_migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schema_migrations
    ADD CONSTRAINT schema_migrations_pkey PRIMARY KEY (version);


--
-- TOC entry 3046 (class 2606 OID 17460)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3058 (class 2606 OID 17596)
-- Name: xps xps_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.xps
    ADD CONSTRAINT xps_pkey PRIMARY KEY (id);


--
-- TOC entry 3048 (class 1259 OID 17592)
-- Name: languages_lower_name_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX languages_lower_name_index ON public.languages USING btree (lower((name)::text));


--
-- TOC entry 3049 (class 1259 OID 17591)
-- Name: languages_name_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX languages_name_index ON public.languages USING btree (name);


--
-- TOC entry 3060 (class 1259 OID 17627)
-- Name: machines_name_user_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX machines_name_user_id_index ON public.machines USING btree (name, user_id);


--
-- TOC entry 3063 (class 1259 OID 17542)
-- Name: machines_user_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX machines_user_id_index ON public.machines USING btree (user_id);


--
-- TOC entry 3066 (class 1259 OID 17626)
-- Name: password_resets_token_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX password_resets_token_index ON public.password_resets USING btree (token);


--
-- TOC entry 3067 (class 1259 OID 17625)
-- Name: password_resets_user_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX password_resets_user_id_index ON public.password_resets USING btree (user_id);


--
-- TOC entry 3054 (class 1259 OID 17611)
-- Name: pulses_sent_at_DESC_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX "pulses_sent_at_DESC_index" ON public.pulses USING btree (sent_at DESC);


--
-- TOC entry 3055 (class 1259 OID 17543)
-- Name: pulses_user_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX pulses_user_id_index ON public.pulses USING btree (user_id);


--
-- TOC entry 3044 (class 1259 OID 17638)
-- Name: users_lower_username_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX users_lower_username_index ON public.users USING btree (lower((username)::text));


--
-- TOC entry 3047 (class 1259 OID 17593)
-- Name: users_username_index; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX users_username_index ON public.users USING btree (username);


--
-- TOC entry 3056 (class 1259 OID 17502)
-- Name: xps_language_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX xps_language_id_index ON public.xps USING btree (language_id);


--
-- TOC entry 3059 (class 1259 OID 17597)
-- Name: xps_pulse_id_index; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX xps_pulse_id_index ON public.xps USING btree (pulse_id);


--
-- TOC entry 3068 (class 2606 OID 17628)
-- Name: languages languages_alias_of_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.languages
    ADD CONSTRAINT languages_alias_of_id_fkey FOREIGN KEY (alias_of_id) REFERENCES public.languages(id) ON DELETE SET NULL;


--
-- TOC entry 3074 (class 2606 OID 17537)
-- Name: machines machines_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.machines
    ADD CONSTRAINT machines_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3075 (class 2606 OID 17620)
-- Name: password_resets password_resets_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.password_resets
    ADD CONSTRAINT password_resets_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3069 (class 2606 OID 17544)
-- Name: pulses pulses_machine_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pulses
    ADD CONSTRAINT pulses_machine_id_fkey FOREIGN KEY (machine_id) REFERENCES public.machines(id) ON DELETE CASCADE;


--
-- TOC entry 3070 (class 2606 OID 17549)
-- Name: pulses pulses_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.pulses
    ADD CONSTRAINT pulses_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3071 (class 2606 OID 17496)
-- Name: xps xps_language_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.xps
    ADD CONSTRAINT xps_language_id_fkey FOREIGN KEY (language_id) REFERENCES public.languages(id);


--
-- TOC entry 3073 (class 2606 OID 17633)
-- Name: xps xps_original_language_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.xps
    ADD CONSTRAINT xps_original_language_id_fkey FOREIGN KEY (original_language_id) REFERENCES public.languages(id);


--
-- TOC entry 3072 (class 2606 OID 17606)
-- Name: xps xps_pulse_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.xps
    ADD CONSTRAINT xps_pulse_id_fkey FOREIGN KEY (pulse_id) REFERENCES public.pulses(id) ON DELETE CASCADE;


-- Completed on 2018-09-25 22:51:53 +03

--
-- PostgreSQL database dump complete
--
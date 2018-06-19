CREATE TABLE public."TOKEN" (
  "TOKENKEY" varchar NOT NULL,
  "USERID" int4 NOT NULL,
  "ID"       serial  NOT NULL,
  CONSTRAINT "TOKEN_pkey" PRIMARY KEY ("ID")
)
    WITH (
      OIDS = FALSE
    );

CREATE TABLE public."USER" (
  "NAME" varchar NOT NULL,
  "ID"   serial  NOT NULL,
  CONSTRAINT "USER_pkey" PRIMARY KEY ("ID")
)
    WITH (
      OIDS = FALSE
    );

CREATE TABLE public."language" (
  id int8 NOT NULL,
  "name"      varchar(255) NOT NULL,
  inserted_at timestamp    NOT NULL,
  updated_at  timestamp    NOT NULL,
  alias_of_id int4 NULL,
  CONSTRAINT pk_language PRIMARY KEY (id)
)
    WITH (
      OIDS = FALSE
    );

CREATE TABLE public.machine (
  id int8 NOT NULL,
  "name"      varchar(255) NOT NULL,
  created_at  timestamp    NOT NULL,
  inserted_at timestamp    NOT NULL,
  updated_at  timestamp    NOT NULL,
  api_salt    varchar(255) NULL,
  active      bool         NULL,
  user_id int8 NOT NULL,
  CONSTRAINT pk_machine PRIMARY KEY (id),
  CONSTRAINT fk_machine_user_id FOREIGN KEY (user_id) REFERENCES jhi_user (id)
)
    WITH (
      OIDS = FALSE
    );

CREATE TABLE public.pulse (
  id int8 NOT NULL,
  send_at       timestamp NOT NULL,
  inserted_at   timestamp NOT NULL,
  updated_at    timestamp NOT NULL,
  send_at_local timestamp NULL,
  tz_offset int4 NULL,
  user_id int8 NOT NULL,
  machine_id int8 NULL,
  CONSTRAINT pk_pulse PRIMARY KEY (id),
  CONSTRAINT fk_pulse_machine_id FOREIGN KEY (machine_id) REFERENCES machine (id),
  CONSTRAINT fk_pulse_user_id FOREIGN KEY (user_id) REFERENCES jhi_user (id)
)
    WITH (
      OIDS = FALSE
    );

CREATE TABLE public.xp (
  id int8 NOT NULL,
  amount int4 NOT NULL,
  inserted_at timestamp NOT NULL,
  updated_at  timestamp NOT NULL,
  orginal_lang_id int4 NULL,
  language_id int8 NOT NULL,
  pulse_id int8 NULL,
  CONSTRAINT pk_xp PRIMARY KEY (id),
  CONSTRAINT xp_language_id_key UNIQUE (language_id),
  CONSTRAINT fk_xp_language_id FOREIGN KEY (language_id) REFERENCES language (id),
  CONSTRAINT fk_xp_pulse_id FOREIGN KEY (pulse_id) REFERENCES pulse (id)
)
    WITH (
      OIDS = FALSE
    );

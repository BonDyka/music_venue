CREATE TABLE IF NOT EXISTS addresses (
  id      SERIAL PRIMARY KEY NOT NULL,
  country VARCHAR(50)        NOT NULL,
  city    VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
  id    SERIAL PRIMARY KEY NOT NULL,
  title VARCHAR(10) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS music_types (
  id    SERIAL PRIMARY KEY NOT NULL,
  genre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  id         SERIAL PRIMARY KEY                                         NOT NULL,
  login      VARCHAR(30) UNIQUE                                         NOT NULL,
  password   VARCHAR(16)                                                NOT NULL,
  address_id INTEGER REFERENCES addresses (id) ON DELETE CASCADE UNIQUE NOT NULL,
  role_id    INTEGER REFERENCES roles (id)                              NOT NULL
);

CREATE TABLE IF NOT EXISTS user_musics (
  user_id       INTEGER REFERENCES users (id) ON DELETE CASCADE       NOT NULL,
  music_type_id INTEGER REFERENCES music_types (id) ON DELETE CASCADE NOT NULL
);

/* fill tables */

INSERT INTO addresses (country, city) VALUES
('Russia', 'Moscow'),
('Russia', 'Saratov'),
('Russia', 'SPB');

INSERT INTO roles (title) VALUES ('ADMIN'), ('MANDATOR'), ('USER');

INSERT INTO music_types (genre) VALUES ('Blues'), ('Rock'), ('Jazz'), ('Heavy Metal'), ('Club');

INSERT INTO users (login, password, address_id, role_id) VALUES
('admin', 'admin', 1, 1),
('guest', 'guest', 2, 3),
('mandator', 'mandator', 3, 2);

INSERT INTO user_musics (user_id, music_type_id) VALUES
(1, 2), (2, 1), (1, 4), (2, 3), (3, 2), (3, 3);
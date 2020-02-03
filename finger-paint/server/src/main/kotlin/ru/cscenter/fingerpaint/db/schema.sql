DROP TABLE IF EXISTS DrawTasks;
DROP TABLE IF EXISTS ChooseTasks;
DROP TABLE IF EXISTS Images;
DROP TABLE IF EXISTS Statistics;
DROP TABLE IF EXISTS GameTypes;
DROP TABLE IF EXISTS Patients;
DROP TABLE IF EXISTS Users;


CREATE TABLE Users
(
    id       SERIAL PRIMARY KEY,
    login    TEXT UNIQUE NOT NULL,
    password TEXT        NOT NULL
);

CREATE TABLE Patients
(
    id      SERIAL PRIMARY KEY,
    user_id INT  NOT NULL REFERENCES Users ON DELETE CASCADE,
    name    TEXT NOT NULL,
    UNIQUE (user_id, name)
);

CREATE TABLE GameTypes
(
    id   INT PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE Statistics
(
    patient_id INT  NOT NULL REFERENCES Patients ON DELETE CASCADE,
    date       DATE NOT NULL,
    type       INT  NOT NULL REFERENCES GameTypes,
    total      INT  NOT NULL CHECK ( total >= 0 ),
    success    INT  NOT NULL CHECK ( 0 <= success AND success <= total),
    PRIMARY KEY (patient_id, date, type)
);

CREATE TABLE Images
(
    id   SERIAL PRIMARY KEY,
    path TEXT UNIQUE NOT NULL
);

CREATE TABLE ChooseTasks
(
    id           SERIAL PRIMARY KEY,
    text         TEXT NOT NUll,
    correct_id   INT  NOT NULL REFERENCES Images,
    incorrect_id INT  NOT NULL REFERENCES Images
);

CREATE TABLE DrawTasks
(
    id       SERIAL PRIMARY KEY,
    text     TEXT NOT NUll,
    image_id INT  NOT NULL REFERENCES Images
);

INSERT INTO GameTypes(id, name)
VALUES (1, 'choose figure'),
       (2, 'choose letter'),
       (3, 'choose figure color'),
       (4, 'choose letter color'),
       (5, 'choose special'),
       (6, 'draw figure'),
       (7, 'draw color'),
       (8, 'draw special');

create database library_1;
use library_1;

-- GRANT ALL PRIVILEGES ON library.* TO 'user45'@'localhost';

create table student
(
    student_roll_number VARCHAR(100) NOT NULL,
    student_full_name VARCHAR(100) NOT NULL,
    book_id CHAR(100),
    CONSTRAINT pk_book PRIMARY KEY (student_roll_number)
);

create table book
(
    book_id VARCHAR(100) NOT NULL,
    book_name VARCHAR(100) NOT NULL,
    book_author CHAR(100) NOT NULL,
    student_rno CHAR(100),
    admin_roll_number INT UNSIGNED,
    CONSTRAINT pk_book PRIMARY KEY (book_id)
);

create table library_manager
(
    admin_number INT UNSIGNED AUTO_INCREMENT,
    admin_name VARCHAR(100) NOT NULL,
    password_admin VARCHAR(100) NOT NULL,
    CONSTRAINT pk_table_dependent PRIMARY KEY (admin_number)
);
alter table student
    ADD CONSTRAINT fk_book_id FOREIGN KEY (book_id)
REFERENCES book(book_id);

alter table book
    ADD CONSTRAINT fk_student_rno FOREIGN KEY (student_rno)
REFERENCES student(student_roll_number);

alter table book
    ADD CONSTRAINT fk_admin_roll_number FOREIGN KEY (admin_roll_number)
REFERENCES library_manager(admin_number);
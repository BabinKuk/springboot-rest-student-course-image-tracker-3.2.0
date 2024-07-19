/*
INSERT INTO course(id,title,instructor_id) values(1,'test course',null)
INSERT INTO review(id,comment,course_id) values(1,'test review',1)
INSERT INTO instructor_detail(id,youtube_channel,hobby) values(1,'ytb test','test hobby')
INSERT INTO "user"(id,first_name,last_name,email,status) values(1,'firstNameInstr','lastNameInstr','firstNameInstr@babinkuk.com','ACTIVE')
INSERT INTO instructor(id,salary,instructor_detail_id) values(1,1000,1)
INSERT INTO image(id,user_id,file_name,data) values(1,1,'file1.jpg',X'746573742066696c652031')
UPDATE course set instructor_id='1' where id=1
INSERT INTO "user"(id,first_name,last_name,email,status) values(2,'firstNameStudent','lastNameStudent','firstNameStudent@babinkuk.com','ACTIVE')
INSERT INTO student(id,street,city,zip_code) values(2,'Street','City','ZipCode')
INSERT INTO image(id,user_id,file_name,data) values(2,2,'file2.jpg',X'746573742066696c652032')
INSERT INTO course(course_id,student_id) values(1,2)
*/

INSERT INTO `student-course-file-tracker`.log_module(lm_id,lm_description,lm_entity_name) values(1,'STUDENT','org.babinkuk.entity.Student');
INSERT INTO `student-course-file-tracker`.log_module(lm_id,lm_description,lm_entity_name) values(2,'INSTRUCTOR','org.babinkuk.entity.Instructor');
INSERT INTO `student-course-file-tracker`.log_module(lm_id,lm_description,lm_entity_name) values(3,'COURSE','org.babinkuk.entity.Course');
INSERT INTO `student-course-file-tracker`.log_module(lm_id,lm_description,lm_entity_name) values(4,'REVIEW','org.babinkuk.entity.Review');
INSERT INTO `student-course-file-tracker`.log_module(lm_id,lm_description,lm_entity_name) values(5,'IMAGE','org.babinkuk.entity.Image');

/*
INSERT INTO change_log(chlo_id,chlo_timestamp,chlo_user_id,chlo_table_id,chlo_rm_id) values(1,curdate(),'user',1,1);
INSERT INTO change_log_item(chli_id,chli_field_name,chli_old_value,chli_old_value_id,chli_new_value,chli_new_value_id,chlo_id) values(1,'test field','old',1,'new',1,1);
*/
commit;

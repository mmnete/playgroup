-- SQLite
SELECT 
    name
FROM 
    sqlite_master 
WHERE 
    type ='table' AND 
    name NOT LIKE 'sqlite_%';


SELECT * from posts;
SELECT * from likes;
SELECT * from users;
SELECT * from userrelations;

INSERT INTO posts (caption, url, userid, filetype) 
VALUES ("Hello people !", "userUploads/17/funny_n.mp4", 15, "video");

INSERT INTO posts (caption, url, userid, filetype) 
VALUES ("Hello folks !", "userUploads/18/2020-03-20 (1).png", 14, "image"); 

INSERT INTO userrelations (followerid, followedid) 
VALUES ( 18, 20 );

UPDATE posts
SET filetype = "video"
WHERE userid = 17;

SELECT * FROM posts WHERE userid IN (
    SELECT followedid from userrelations
    WHERE followerid = 18
)

DELETE FROM posts WHERE id = 1;

SELECT posts.id, posts.caption, posts.url, posts.filetype, users.fullname
FROM (SELECT * FROM posts WHERE userid IN ( SELECT followedid from userrelations WHERE followerid = 18)) AS posts
INNER JOIN users 
ON users.id = posts.userid;
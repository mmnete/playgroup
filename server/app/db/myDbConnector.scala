package db;

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.PreparedStatement
import model._
import scala.collection.mutable.ListBuffer
import java.util._
import java.text._

class myDbConnector {

  // Name of the database (password, port, etc. is not required)
  val dbName: String = "jdbc:postgresql://localhost:5432/mmnete?user=mmnete&password=0811494";
  var dbConnection: Connection = null;

  def connectToDatabase(): Boolean = {

    if(this.dbConnection != null) {
      return false;
    }

    try {
      this.dbConnection = DriverManager.getConnection(this.dbName);
    }catch {
      case e: SQLException => { e.printStackTrace;
      return false; }
    }


    if(this.dbConnection == null) {
      return false;
    }else{
      return true;
    }
  }

  def update(statement: String): Unit = {
    var stmnt: Statement = this.dbConnection.createStatement();
    stmnt.setQueryTimeout(30);
    stmnt.executeUpdate(statement);
    stmnt.close();
  }

  def getNumberOfUsers(): Int = {

    // does not have to be prepared statement, we do not use any user input
    var stmnt: Statement = this.dbConnection.createStatement();
    val res: ResultSet = stmnt.executeQuery("SELECT count(*) FROM users");
    var result = 0
    if(res.next()) {
      result = res.getInt(0);
    }
    // do not close statement until you finish collecting results
    stmnt.close();

    return result;
  }

  def doesAccountExist(email: String): Boolean = {

      // using prepared statement for security of our queries
      var selectQuery = "SELECT * FROM users WHERE email = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setString(1, email);
      val res: ResultSet = pstmt.executeQuery();
      var result = res.next()
      // do not close statement until you finish collecting results
      pstmt.close();

      return result;
  }

  def validateLogIn(email: String, password: String): Boolean = {

    // using prepared statement for security of our queries
    var selectQuery = "SELECT * FROM users WHERE email = ? AND password = ?";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
    pstmt.setString(1, email);
    pstmt.setString(2, password);
    val res: ResultSet = pstmt.executeQuery();
    var result = res.next()
    // do not close statement until you finish collecting results
    pstmt.close();

    return result;
  }

  def createAccount(email: String, password: String, fullname: String): Boolean = {


    if(doesAccountExist(email)) {
      return false;
    }

    // using prepared statement for security of our queries
    var insertQuery = "INSERT INTO users (email, password, fullname) VALUES (?, ?, ?)";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
    pstmt.setString(1, email);
    pstmt.setString(2, password);
    pstmt.setString(3, fullname);
    pstmt.executeUpdate();

    pstmt.close();

    // check if update works
    if(doesAccountExist(email)) {
      return true;
    }else{
      return false;
    }
  }

  def fetchUser (email: String): user = {
       if(connectToDatabase()){
         println("Connected")
       } else {
         println("Not connected")
       }
   // using prepared statement for security of our queries
      var selectQuery = "SELECT * FROM users WHERE email = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setString(1, email);
      val res: ResultSet = pstmt.executeQuery();
      var userList = new ListBuffer[user]()
      while (res.next()) {
            var id = res.getInt("id");
            var email = res.getString("email");
            var password = res.getString("password");
            var fullname = res.getString("fullname");
            var pictureurl = res.getString("pictureurl");
            userList += user(id.toString, email, password, fullname, pictureurl)
        }
      // do not close statement until you finish collecting results
      pstmt.close();

      if(userList.length == 0)
      {
         return null;
      }

      return userList(0);
  }

  def searchForUser (email: String, fullname: String): ListBuffer[user] = {
        // search limit
        var searchLimit = 10

        // search query
        var searchEmail = email + "%"
        var searchFullname = fullname + "%"
        var selectQuery = "SELECT * FROM users WHERE email LIKE ? OR fullname LIKE ?";
        var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
        pstmt.setString(1, searchEmail);
        pstmt.setString(2, searchFullname);
        val res: ResultSet = pstmt.executeQuery();
        var userList = new ListBuffer[user]()
        while (res.next() && searchLimit > 0) {
              var id = res.getInt("id");
              var email = res.getString("email");
              var password = "";
              var fullname = res.getString("fullname");
              var pictureurl = res.getString("pictureurl");
              userList += user(id.toString, email, password, fullname, pictureurl)
              searchLimit = searchLimit - 1
          }
        // do not close statement until you finish collecting results
        pstmt.close();

        return userList;
  }

  def fetchAllUsers (): ListBuffer[user] = {
        // fetch limit
        var fetchLimit = 10

        var selectQuery = "SELECT * FROM users";
        var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
        val res: ResultSet = pstmt.executeQuery();
        var userList = new ListBuffer[user]()
        while (res.next() && fetchLimit > 0) {
              var id = res.getInt("id");
              var email = res.getString("email");
              var password = "";
              var fullname = res.getString("fullname");
              var pictureurl = res.getString("pictureurl");
              userList += user(id.toString, email, password, fullname, pictureurl)
              fetchLimit = fetchLimit - 1
          }
        // do not close statement until you finish collecting results
        pstmt.close();

        return userList;
  }

  def isFollower(me: user, otherUser: user): Boolean = {
      // using prepared statement for security of our queries
      var found = false
      var selectQuery = "SELECT * FROM userrelations WHERE followerid = ? AND followedid = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setInt(1, otherUser.id.toInt);
      pstmt.setInt(2, me.id.toInt);
      val res: ResultSet = pstmt.executeQuery();
      while (res.next()) {
            found = true
        }
      // do not close statement until you finish collecting results
      pstmt.close();

      return found;
  }

  def iFollow(me: user, otherUser: user): Boolean = {
      // using prepared statement for security of our queries
      var found = false
      var selectQuery = "SELECT * FROM userrelations WHERE followerid = ? AND followedid = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setInt(1, me.id.toInt);
      pstmt.setInt(2, otherUser.id.toInt);
      val res: ResultSet = pstmt.executeQuery();
      while (res.next()) {
            found = true
        }
      // do not close statement until you finish collecting results
      pstmt.close();

      return found;
  }

  def followUser(me: user, otherUser: user): Unit = {
      // using prepared statement for security of our queries
      var insertQuery = "INSERT INTO userrelations (followerid, followedid) VALUES (?, ?)";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
      pstmt.setInt(1, me.id.toInt);
      pstmt.setInt(2, otherUser.id.toInt);
      pstmt.executeUpdate();
      pstmt.close();
  }

  def removeFollower(me: user, otherUser: user): Unit = {
      // using prepared statement for security of our queries
      var insertQuery = "DELETE FROM userrelations WHERE  followerid = ? AND followedid = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
      pstmt.setInt(1, otherUser.id.toInt);
      pstmt.setInt(2, me.id.toInt);
      pstmt.executeUpdate();
      pstmt.close();
  }

  def unFollowUser(me: user, otherUser: user): Unit = {
      // using prepared statement for security of our queries
      var insertQuery = "DELETE FROM userrelations WHERE  followerid = ? AND followedid = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
      pstmt.setInt(1, me.id.toInt);
      pstmt.setInt(2, otherUser.id.toInt);
      pstmt.executeUpdate();
      pstmt.close();
  }

  // get all the people i follow...
  def getFollowList(me: user): ListBuffer[user] = {
      // search query
      var selectQuery = "SELECT * FROM users WHERE id IN (SELECT followedid FROM userrelations WHERE followerid = ?)";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setInt(1, me.id.toInt);
      val res: ResultSet = pstmt.executeQuery();
      var userList = new ListBuffer[user]()
      while (res.next()) {
            var id = res.getInt("id");
            var email = res.getString("email");
            var password = "";
            var fullname = res.getString("fullname");
            var pictureurl = res.getString("pictureurl");
            userList += user(id.toString, email, password, fullname, pictureurl)
        }
      // do not close statement until you finish collecting results
      pstmt.close();

      return userList;
  }

  def getFollowMeList(me: user): ListBuffer[user] = {
      // search query
      var selectQuery = "SELECT * FROM users WHERE id IN (SELECT followerid FROM userrelations WHERE followedid = ?)";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setInt(1, me.id.toInt);
      val res: ResultSet = pstmt.executeQuery();
      var userList = new ListBuffer[user]()
      while (res.next()) {
            var id = res.getInt("id");
            var email = res.getString("email");
            var password = "";
            var fullname = res.getString("fullname");
            var pictureurl = res.getString("pictureurl");
            userList += user(id.toString, email, password, fullname, pictureurl)
        }
      // do not close statement until you finish collecting results
      pstmt.close();

      return userList;
  }

  def deletePost(me: user): Unit = {
      // using prepared statement for security of our queries
      var insertQuery = "DELETE FROM posts WHERE userid = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
      pstmt.setInt(1, me.id.toInt);
      pstmt.executeUpdate();
      pstmt.close();
  }

  def newPostUpload(me: user, caption: String, url: String, filetype: String): Unit = {
      // using prepared statement for security of our queries
      // delete previous post
      deletePost(me);
      var insertQuery = "INSERT INTO posts (caption, url, userid, filetype, posttime) VALUES (?, ?, ?, ?, ?)";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
      pstmt.setString(1, caption);
      pstmt.setString(2, url);
      pstmt.setInt(3, me.id.toInt);
      pstmt.setString(4, filetype);
      pstmt.setString(5, Calendar.getInstance().getTime().toString());
      pstmt.executeUpdate();
      pstmt.close();
  }

  def searchForPosts (filter: String): ListBuffer[post] = {
        // search limit
        var searchLimit = 10

        // search query
        var selectQuery = "SELECT * FROM posts WHERE caption LIKE ?";
        var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
        pstmt.setString(1, filter);
        val res: ResultSet = pstmt.executeQuery();
        var postsList = new ListBuffer[post]()
        while (res.next() && searchLimit > 0) {
              var id = res.getInt("id");
              var caption = res.getString("caption");
              var url = res.getString("url");
              var userid = res.getInt("userid").toString;
              var filetype = res.getString("filetype");
              postsList += post(id.toString, caption, url, userid, filetype)
              searchLimit = searchLimit - 1
          }
        // do not close statement until you finish collecting results
        pstmt.close();

        return postsList;
  }

  def getPost(me: user): post = {
      // using prepared statement for security of our queries
      var selectQuery = "SELECT * FROM posts WHERE userid = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setInt(1, me.id.toInt);
      val res: ResultSet = pstmt.executeQuery();
      var postList = new ListBuffer[post]()
      while (res.next()) {
          var sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
          var currTime =  sdf.parse(Calendar.getInstance().getTime().toString()) //Calendar.getInstance().getTime();
          var postTime = sdf.parse(res.getString("posttime"));

          var diffInSecs = Math.abs(currTime.getTime() - postTime.getTime())/1000.0;

          println(diffInSecs)

          if(diffInSecs <= 86400)
          {
              var id = res.getInt("id");
              var caption = res.getString("caption");
              var url = res.getString("url");
              var userid = res.getString("userid");
              var filetype = res.getString("filetype");
              postList += post(id.toString, caption, url, userid.toString, filetype)
          }
        }
      // do not close statement until you finish collecting results
      pstmt.close();

      if(postList.length == 0)
      {
      return null;
      }

      return postList(0);
  }

  // the function below is only for testing
  def getAllPosts(): Seq[post] = {
     println("Getting...")
     // using prepared statement for security of our queries
     var selectQuery = """
     SELECT * FROM posts
     """;
     var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
     val res: ResultSet = pstmt.executeQuery();
     var postList = new ListBuffer[post]()
     while (res.next()) {


             var id = res.getInt("id");
             var caption = res.getString("caption");
             var url = res.getString("url");
             var userid = "Mohamed";
             var filetype = res.getString("filetype");
             postList += post(id.toString, caption, url, userid.toString, filetype)

       }
      // do not close statement until you finish collecting results
      pstmt.close()
      println(postList)
      return postList;
  }

  def getPosts(me: user): Seq[post] = {
  /*   
      var peopleIFollow = getFollowList(me)
      var postList = new ListBuffer[post]()

      for(currUser <- peopleIFollow)
      {
         postList += getPost(currUser)

      }

      return postList;
*/

   // using prepared statement for security of our queries
     var selectQuery = """
     SELECT posts.id AS postid, caption, url, filetype, posttime, fullname
     FROM (SELECT * FROM posts WHERE userid IN ( SELECT followedid from userrelations WHERE followerid = ?)) AS posts
     INNER JOIN users
     ON users.id = posts.userid;
     """;
     var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
     pstmt.setInt(1, me.id.toInt);
     val res: ResultSet = pstmt.executeQuery();
     var postList = new ListBuffer[post]()
     while (res.next()) {

           var sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
           var currTime =  sdf.parse(Calendar.getInstance().getTime().toString()) //Calendar.getInstance().getTime();
           var postTime = sdf.parse(res.getString("posttime"));

           var diffInSecs = Math.abs(currTime.getTime() - postTime.getTime())/1000.0;

           println(diffInSecs)

           if(diffInSecs <= 86400)
           {
             var id = res.getInt("postid");
             var caption = res.getString("caption");
             var url = res.getString("url");
             var userid = res.getString("fullname");
             var filetype = res.getString("filetype");
             postList += post(id.toString, caption, url, userid.toString, filetype)
           }
       }
      // do not close statement until you finish collecting results
      pstmt.close()
      println(postList)
      return postList;
     
  }

  def unlikePost(postId: String, me: user): Unit = {
    var insertQuery = "DELETE FROM likes WHERE postid = ? AND userid = ?";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
    pstmt.setInt(1, postId.toInt);
    pstmt.setInt(2, me.id.toInt);
    pstmt.executeUpdate();
    pstmt.close();
  }

  def unlikePost(currPost: post, me: user): Unit = {
    var insertQuery = "DELETE FROM likes WHERE postid = ? AND userid = ?";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
    pstmt.setInt(1, currPost.id.toInt);
    pstmt.setInt(2, me.id.toInt);
    pstmt.executeUpdate();
    pstmt.close();
  }

  def likePost (postId: String, me: user): Unit = {
    var insertQuery = "INSERT INTO likes (postid, userid) VALUES (?, ?)";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
    pstmt.setInt(1, postId.toInt);
    pstmt.setInt(2, me.id.toInt);
    pstmt.executeUpdate();
    pstmt.close();
  }

  def likePost (currPost: post, me: user): Unit = {
    var insertQuery = "INSERT INTO likes (postid, userid) VALUES (?, ?)";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
    pstmt.setInt(1, currPost.id.toInt);
    pstmt.setInt(2, me.id.toInt);
    pstmt.executeUpdate();
    pstmt.close();
  }

  def isPostLiked (postId: String, me: user): Boolean = {
    var isLiked = false;
    var insertQuery = "SELECT * FROM likes WHERE postid = ? AND userid = ?";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
    pstmt.setInt(1, postId.toInt);
    pstmt.setInt(2, me.id.toInt);
    val res: ResultSet = pstmt.executeQuery();
    var postList = new ListBuffer[post]()
    while (res.next()) {

          isLiked = true;

      }
     // do not close statement until you finish collecting results
     pstmt.close();
     return isLiked;
  }

  // when given a whole post
  def getListOfUserLikes (currPost: post): ListBuffer[user] = {
      // search query
      var selectQuery = "SELECT * FROM users WHERE id IN (SELECT userid FROM likes WHERE postid = ?)";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setInt(1, currPost.id.toInt);
      val res: ResultSet = pstmt.executeQuery();
      var userList = new ListBuffer[user]()
      while (res.next()) {
            var id = res.getInt("id");
            var email = res.getString("email");
            var password = "";
            var fullname = res.getString("fullname");
            var pictureurl = res.getString("pictureurl");
            userList += user(id.toString, email, password, fullname, pictureurl)
        }
      // do not close statement until you finish collecting results
      pstmt.close();

      return userList;
  }

  // when only given post id
  def getListOfUserLikes (postId: String): ListBuffer[user] = {
      // search query
      var selectQuery = "SELECT * FROM users WHERE id IN (SELECT userid FROM likes WHERE postid = ?)";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setInt(1, postId.toInt);
      val res: ResultSet = pstmt.executeQuery();
      var userList = new ListBuffer[user]()
      while (res.next()) {
            var id = res.getInt("id");
            var email = res.getString("email");
            var password = "";
            var fullname = res.getString("fullname");
            var pictureurl = res.getString("pictureurl");
            userList += user(id.toString, email, password, fullname, pictureurl)
        }
      // do not close statement until you finish collecting results
      pstmt.close();

      return userList;
  }

  def changeProfilePhoto(me: user, url: String): Unit = {
      var selectQuery = "UPDATE users SET pictureurl = ? WHERE id = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setString(1, url);
      pstmt.setInt(2, me.id.toInt);
      pstmt.executeUpdate();
      pstmt.close();
  }

  def newFullName(me: user, newName: String): Unit = {
      var selectQuery = "UPDATE users SET fullname = ? WHERE id = ?";
      var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
      pstmt.setString(1, newName);
      pstmt.setInt(2, me.id.toInt);
      pstmt.executeUpdate();
      pstmt.close();
  }

  def updatePassword(me: user, newPassword: String): Unit = {
    var selectQuery = "UPDATE users SET password = ? WHERE id = ?";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(selectQuery)
    pstmt.setString(1, newPassword);
    pstmt.setInt(2, me.id.toInt);
    pstmt.executeUpdate();
    pstmt.close();
  }

  def deleteAccount(me: user): Unit = {
    var insertQuery = "DELETE FROM users WHERE id = ?";
    var pstmt: PreparedStatement = this.dbConnection.prepareStatement(insertQuery)
    pstmt.setInt(1, me.id.toInt);
    pstmt.executeUpdate();
    pstmt.close();
  }




}

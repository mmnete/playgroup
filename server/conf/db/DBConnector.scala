package db;

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class DBConnector {

  // Name of the database (password, port, etc. is not required)
  val dbName: String = "jdbc:sqlite:playgroup.db";
  var dbConnection: Connection = null;

  def connectToDatabase(): Int = {

    if(this.dbConnection != null) {
      // we were already connected
       println("Connected")
      return 0;
    }

    try {
       println("Connected")
      this.dbConnection = DriverManager.getConnection(this.dbName);
    }catch {
    case e: ClassNotFoundException => {
      println("Not connected here 1")
      return 2;
      }
    case e: Exception => {
    e.printStackTrace
    return 2;
      }
    }

    if(this.dbConnection == null) {
      return 2;
    }else{
      // we made it
      return 1;
    }
  }

  def update(statement: String): Unit = {
    var stmnt: Statement = this.dbConnection.createStatement();
    stmnt.setQueryTimeout(30);
    stmnt.executeUpdate(statement);
    stmnt.close();
  }

  def query(q: String): ResultSet = {
    var stmnt: Statement = this.dbConnection.createStatement();
    val res: ResultSet = stmnt.executeQuery(q);
    stmnt.close();
    return res;
  }

  def getNumberOfUsers(): Int = {
    val res = query("select count(*) from users");

    if(res.next()) {
      return res.getInt(0);
    }else{
      return 0;
    }
  }

  def doesAccountExist(email: String): Boolean = {
    val res = query("select count(*) from users where email = '" + email + "'");

    return res.next();
  }

  def validateLogIn (email: String, password: String): Boolean = {
      val res = query("select count(*) from users where email = '" + email + "' and password = '" + password + "'");

      return res.next();
  }

  def createAccount(email: String, password: String, fullName: String): Boolean = {
    // check if account is already made just to prevent database errors
    if(doesAccountExist(email)) {
      return false;
    }

    query("INSERT INTO users (email, password, fullName) VALUES ('" + email + "', '" + password + "', '" + fullName + "')");

    // check if update works
    if(doesAccountExist(email)) {
      return true;
    }else{
      return false;
    }
  }

}

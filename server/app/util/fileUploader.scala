package util

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

class fileUploader {

  def upload (file2Upload: File, fileName: String) : Boolean = {

    var server = "files.000webhost.com";
    var port = 21;
    var user = "playgroup12345";
    var pass = "123abacus";

    var ftpClient = new FTPClient();
    try {

        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode();

        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        // APPROACH #1: uploads first file using an InputStream
        var firstLocalFile = file2Upload

        var firstRemoteFile = "public_html/" + fileName;
        var inputStream = new FileInputStream(firstLocalFile);




        println("Start uploading first file");
        var done = ftpClient.storeFile(firstRemoteFile, inputStream);
        inputStream.close();
        if (done) {
            println("The first file is uploaded successfully.");
            return true;
        } else {
            // could not upload
            return false;
        }

    }catch {
      case e: IOException => { e.printStackTrace;
      return false; }
    }


  }


}

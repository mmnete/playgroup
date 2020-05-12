

function changePassword() {
  var currPassword = document.getElementById("prevPassword").value;
  var newPassword = document.getElementById("newPassword").value;
  var confPassword = document.getElementById("confNewPassword").value;

  if(currPassword.trim().length == 0 || newPassword.trim().length == 0 || confPassword.trim().length == 0)
  {
    document.getElementById("passwordChangeStatusSpan").innerHTML = "Enter all fields";
    return;
  }

  if(newPassword != confPassword)
  {
    document.getElementById("passwordChangeStatusSpan").innerHTML = "You retyped your password incorrectly";
    return;
  }

  document.getElementById("passwordChangeStatusSpan").innerHTML = "Changing Password ... ";
  // now make changes to password
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "SUCCESS"){
       document.getElementById("passwordChangeStatusSpan").innerHTML = "Password changed :)";
     } else if (this.responseText == "!PASSWORD")
     {
       document.getElementById("passwordChangeStatusSpan").innerHTML = "Error. Your current password is incorrect :(";
     } else {
       document.getElementById("passwordChangeStatusSpan").innerHTML = "Error. Please try again.";
     }
    //  alert(this.responseText);
    }
  };
  xhttp.open("POST", "/updatePassword", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("currPassword="+currPassword+"&newPassword="+newPassword+"&confPassword="+confPassword);


}

function deleteAcccount() {
  document.getElementById("passwordChangeStatusSpan").innerHTML = "Deleting account ...";
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "SUCCESS"){
        document.getElementById("passwordChangeStatusSpan").innerHTML = "Account deleted.";
        location.assign("/login");
       window.location.href = "/login";
     }
    //  alert(this.responseText);
    }
  };
  xhttp.open("POST", "/deleteAccount", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("");
}



// do this when window loads..
window.addEventListener("load", function(){

      document.getElementById("changePasswordBtn").addEventListener("click",
        function(){ changePassword(); }
      );

      document.getElementById("deleteAccountBtn").addEventListener("click",
        function(){ deleteAcccount(); }
      );

});

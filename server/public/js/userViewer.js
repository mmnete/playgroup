

function unFollowUser(email)
{
  if(document.getElementById("unfollowBtn").innerHTML == "UNFOLLOW")
  {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        if(this.responseText == "SUCCESS"){
          document.getElementById("unfollowBtn").innerHTML = "FOLLOW";
        }
      //  alert(this.responseText);
      }
    };
    xhttp.open("POST", "/unfollowUser", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("email="+email);
  } else if(document.getElementById("unfollowBtn").innerHTML == "FOLLOW")
  {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        if(this.responseText == "SUCCESS"){
          document.getElementById("unfollowBtn").innerHTML = "UNFOLLOW";
        }
      //  alert(this.responseText);
      }
    };
    xhttp.open("POST", "/followUser", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("email="+email);
  }
}


function followUser(email){
  if(document.getElementById("followBtn").innerHTML == "FOLLOW")
  {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
      //  alert(this.responseText);
        if(this.responseText == "SUCCESS"){
          document.getElementById("followBtn").innerHTML = "UNFOLLOW";
        }
      //  alert(this.responseText);
      }
    };
    xhttp.open("POST", "/followUser", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("email="+email);
  } else if(document.getElementById("followBtn").innerHTML == "UNFOLLOW")
  {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        if(this.responseText == "SUCCESS"){
          document.getElementById("followBtn").innerHTML = "FOLLOW";
        }
      //  alert(this.responseText);
      }
    };
    xhttp.open("POST", "/unfollowUser", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("email="+email);
  }

}

function removeFollower(email){

    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        if(this.responseText == "SUCCESS"){
          var removeFollowerBtn = document.getElementById("removeFollowerBtn");
          removeFollowerBtn.parentNode.removeChild(removeFollowerBtn);
        }
      //  alert(this.responseText);
      }
    };
    xhttp.open("POST", "/removeFollower", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("email="+email);

}


window.addEventListener("load", function(){

     if(document.getElementById("followBtn") != null)
     {
       document.getElementById("followBtn").addEventListener("click",
         function(){ followUser(document.getElementById("userEmail").innerHTML); }
       );
     }


    if(document.getElementById("unfollowBtn") != null)
    {
      document.getElementById("unfollowBtn").addEventListener("click",
        function(){ unFollowUser(document.getElementById("userEmail").innerHTML); }
      );
    }


    if(document.getElementById("removeFollowerBtn") != null)
    {
      document.getElementById("removeFollowerBtn").addEventListener("click",
        function(){ removeFollower(document.getElementById("userEmail").innerHTML); }
      );
    }

    document.getElementById("profileViewerBackBtn").addEventListener("click",
      function () { window.history.back(); }
   );



});

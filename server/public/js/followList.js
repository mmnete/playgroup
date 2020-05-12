function generateFollowList(){



   document.getElementById("iFollowList").innerHTML = "<center><h3>I follow (<span id='iFollowListNumber'>0</span>):</h3></center>";

    // now we do a post request
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        if(this.responseText == "")
        {
          // no result found
          document.getElementById("iFollowList").innerHTML += "<br>Lonely? Follow People :(";
        } else {
          //alert(this.responseText);
          var userJsonArray = JSON.parse(this.responseText);
          document.getElementById("iFollowList").innerHTML = "<center><h3>I follow (<span id='iFollowListNumber'>"+ userJsonArray.length + "</span>):</h3>";
          var i;
          for (i = 0; i < userJsonArray.length; i++) {
            var newResSpan = "<center><a href='/viewUser/"+userJsonArray[i].email+"'><h3 id='friendFullName' class='friendFullName'>"
            newResSpan += userJsonArray[i].fullname;
            newResSpan += "</h3>";
            if(userJsonArray[i].pictureurl == "default"){
              newResSpan += "<img src='/versionedAssets/images/test-profile.png' id='profileDivImage' class='profileDivImage'/>";
            } else {
              newResSpan += "<img src='"+userJsonArray[i].pictureurl+"' id='profileDivImage' class='profileDivImage'/>";
            }
            newResSpan += "<br>";
            // <button class="removeBtn" id="removeBtn">Remove User</button>
            if(i + 1 < userJsonArray.length)
            {
               newResSpan += "<hr>";
            }
            newResSpan += "</a></center>";
            document.getElementById("iFollowList").innerHTML += newResSpan;
          }
          document.getElementById("iFollowList").innerHTML += "</center>";
        }
      //  alert(this.responseText);
      }
    };
    xhttp.open("POST", "/getFollowList", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("");




}

function generateFollowersList(){

  document.getElementById("followingMeList").innerHTML = "<center><h3>Who Follow Me (<span id='followingMeListNumber'>0</span>):</h3></center>";

   // now we do a post request
   var xhttp = new XMLHttpRequest();
   xhttp.onreadystatechange = function() {
     if (this.readyState == 4 && this.status == 200) {
       if(this.responseText == "")
       {
         // no result found
         document.getElementById("followingMeList").innerHTML += "<br>Lonely? Follow People so they follow back :(";
       } else {
         var userJsonArray = JSON.parse(this.responseText);
         document.getElementById("followingMeList").innerHTML = "<center><h3>Who Follow Me (<span id='followingMeListNumber'>"+ userJsonArray.length + "</span>):</h3>";
         var i;
         for (i = 0; i < userJsonArray.length; i++) {
           var newResSpan = "<center><a href='/viewUser/"+userJsonArray[i].email+"'><h3 id='friendFullName' class='friendFullName'>"
           newResSpan += userJsonArray[i].fullname;
           newResSpan += "</h3>";
           if(userJsonArray[i].pictureurl == "default"){
             newResSpan += "<img src='/versionedAssets/images/test-profile.png' id='profileDivImage' class='profileDivImage'/>";
           } else {
             newResSpan += "<img src='"+userJsonArray[i].pictureurl+"' id='profileDivImage' class='profileDivImage'/>";
           }
           newResSpan += "<br>";
           // <button class="removeBtn" id="removeBtn">Remove User</button>
           if(i + 1 < userJsonArray.length)
           {
              newResSpan += "<hr>";
           }
           newResSpan += "</a></center>";
           document.getElementById("followingMeList").innerHTML += newResSpan;
         }
         document.getElementById("followingMeList").innerHTML += "</center>";
       }
     //  alert(this.responseText);
     }
   };
   xhttp.open("POST", "/getFollowMeList", true);
   xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
   xhttp.send("");
}

window.addEventListener("load", function(){
   generateFollowersList();
   generateFollowList();
});

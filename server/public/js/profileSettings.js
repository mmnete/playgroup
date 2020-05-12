

function newProfilePhoto(newSrc)
{
   // this uploads the new photo
   if(newSrc == "images/test-profile.png")
   {
     document.getElementById("profileDivImage").src = "versionedAssets/"+newSrc;
   } else
   {
     document.getElementById("profileDivImage").src = newSrc;
   }
}

function updateFullName()
{
  var newName = document.getElementById("changeFullNameInput").value;
  if(newName.trim().length == 0)
  {
    document.getElementById("profilePhotoError").innerHTML = "Please enter a name!";
    return;
  }

  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "SUCCESS"){
       document.getElementById("profilePhotoError").innerHTML = "Name Changed :)";
       document.getElementById("changeFullNameInput").value = newName;
      }
    //  alert(this.responseText);
    }
  };
  xhttp.open("POST", "/newFullName", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("name="+newName);
}

function removeProfilePhoto()
{
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "SUCCESS"){
       newProfilePhoto("images/test-profile.png");
       document.getElementById("profilePhotoError").innerHTML = "Photo Removed";
      }
    //  alert(this.responseText);
    }
  };
  xhttp.open("POST", "/deleteProfilePhoto", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("");
}

function changeProfilePhoto()
{

  if(document.getElementById("newProfilePhotoFileChooser").value == "")
  {
    document.getElementById("profilePhotoError").innerHTML = "Please choose a profile photo";
    return;
  }

  // check if user has chosen photo
  var control = document.getElementById("newProfilePhotoFileChooser");
  var files = control.files;


  if(files[0].size > 9052374)
  {
    document.getElementById("profilePhotoError").innerHTML = "Profile Photo too large";
    document.getElementById("newProfilePhotoFileChooser").value = "";
    return;
  }


  if (files[0].type != "image/jpeg")
  {
    // chose image
    document.getElementById("profilePhotoError").innerHTML = "Please choose an image";
    document.getElementById("newProfilePhotoFileChooser").value = "";
    return;
  }






  // upload file data
  var data = new FormData();

// file selected by the user
// in case of multiple files append each of them
data.append('fileUpload', document.getElementById("newProfilePhotoFileChooser").files[0]);

var request = new XMLHttpRequest();
request.open('post', '/changeProfilePhoto');

// upload progress event
request.upload.addEventListener('progress', function(e) {
  var percent_complete = (e.loaded / e.total)*100;

  // Percentage of upload completed
  if(percent_complete < 100.0)
  {
    document.getElementById("profilePhotoError").innerHTML = "We are uploading your photo: "+ parseFloat(percent_complete).toFixed(2) + "% Complete";
  } else {
    document.getElementById("profilePhotoError").innerHTML = "Photo Uploaded";
  }
});

// AJAX request finished event
request.addEventListener('load', function(e) {
  // HTTP status message
  console.log(request.status);
  // request.response will hold the response from the server
  console.log(request.response);

  //
  if(request.response != "FAIL")
  {
    newProfilePhoto(request.response);
  } else {
     document.getElementById("profilePhotoError").innerHTML = "Could not change photo!";
  }

});

// send POST request to server side script
request.send(data);
}

function viewMyStory() {
   window.location.href = "/mystory";
   // sometimes firefox is messy so
   window.location.assign = "/mystory";
}



// do this when window loads..
window.addEventListener("load", function(){

      document.getElementById("removeProfilePhotoBtn").addEventListener("click",
        function(){ removeProfilePhoto(); }
      );

      document.getElementById("changeProfilePhotoBtn").addEventListener("click",
        function(){ changeProfilePhoto(); }
      );

      document.getElementById("changeFullNameBtn").addEventListener("click",
        function(){ updateFullName(); }
      );

      document.getElementById("viewMyStoryBtn").addEventListener("click",
        function(){ viewMyStory(); }
      );

});

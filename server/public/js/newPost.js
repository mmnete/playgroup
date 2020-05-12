
function validateMediaFile(){
   var control = document.getElementById("fileUpload");
   var files = control.files;


   if(files[0].size > 9052374)
   {
     document.getElementById("postProgressSpan").innerHTML = "Stories are better kept short :) Choose a smaller file please.";
     document.getElementById("fileUpload").value = "";
     return;
   }

   const acceptedImageTypes = ['image/gif', 'image/jpeg', 'image/png'];

   if(files[0].type == "audio/mpeg"){
     // chose audio file
     document.getElementById("postProgressSpan").innerHTML = "Your Good to Share with the world. Click Upload to share";
     return "audio";
   } else if(acceptedImageTypes.includes(files[0].type))
   {
     // chose image
     document.getElementById("postProgressSpan").innerHTML = "Your Good to Share with the world. Click Upload to share";
     return "image";
   } else if(files[0].type == "video/mp4")
   {
     // chose video
     document.getElementById("postProgressSpan").innerHTML = "Your Good to Share with the world. Click Upload to share";
     return "video";
   } else {
     alert("Please choose an audio/image/video file :(");
     document.getElementById("fileUpload").value = "";
   }
}

function uploadCaption(caption, filename, filetype){
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "SUCCESS"){
      //  document.getElementById("postProgressSpan").innerHTML = "POST SENT";
      }
    //  alert(this.responseText);
    }
  };
  xhttp.open("POST", "/uploadCaption", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("caption="+caption+"&filename="+filename+"&filetype="+filetype);
}

function initiateUpload(filetype){

  var caption = document.getElementById("newPostTextArea").value;
  var filename = "";
  console.log(`MID1 ${filetype}`);
  if(caption.length == 0)
  {
    document.getElementById("postProgressSpan").innerHTML = "Please Enter Caption :(";
    return;
  }

  if(document.getElementById("fileUpload").value == "")
  {
    document.getElementById("postProgressSpan").innerHTML = "A story is not complete without media :(";
    return;
  }

  // upload file data
  var data = new FormData();

// file selected by the user
// in case of multiple files append each of them
data.append('fileUpload', document.getElementById("fileUpload").files[0]);

var request = new XMLHttpRequest();
request.open('post', '/newPostFileUpload');

// upload progress event
request.upload.addEventListener('progress', function(e) {
	var percent_complete = (e.loaded / e.total)*100;

	// Percentage of upload completed
  if(percent_complete < 100.0)
  {
    document.getElementById("postProgressSpan").innerHTML = "We are uploading your post: "+ parseFloat(percent_complete).toFixed(2) + "% Complete";
  } else {
    document.getElementById("postProgressSpan").innerHTML = "Your story is live :) You can close this window. ";
  }
});

// AJAX request finished event
request.addEventListener('load', function(e) {
	// HTTP status message
	console.log(request.status);
	// request.response will hold the response from the server
	console.log(request.response);

  filename = document.getElementById("fileUpload").files[0].name;
  console.log(`MID ${filetype}`);
  // filetype = document.getElementById("fileUpload").files[0].type;
// upload caption
   uploadCaption(caption, filename, filetype);
});

// send POST request to server side script
request.send(data);


}



window.addEventListener("load", function(){
  var filetype = "";
  // check if user has selected a file to upload
  document.getElementById("fileUpload").addEventListener("change",
    function(){
      filetype = validateMediaFile();
     }
  );

  document.getElementById("newPostDivPostBtn").addEventListener("click",
  function(){
    console.log(`Click event filetype: ${filetype}`);
    initiateUpload(filetype);
   }
);


});

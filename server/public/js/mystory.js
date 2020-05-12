
function generateMyCurrentPost(){
  document.getElementById("myPostContainer").innerHTML = "Loading Your Current Story ...";
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "")
      {
        // no result found
        document.getElementById("myPostContainer").innerHTML = "<br><br><center>You do not have a story.</center>";
      } else {
        var postJson = JSON.parse(this.responseText);
        console.log(postJson);
        var postHtml = "<center><h4>This is your current Post</h4></center>";
        if(postJson.filetype == "audio"){
          // chose audio file
            postHtml += "<center><audio id='myAudioPost' controls><source src='"+postJson.url+"' type='audio/mpeg'></audio></center>";
        } else if(postJson.filetype == "image")
        {
          // chose image
          postHtml += "<img id='myImagePost' style='width:350px; height:350px;' class='postMediaContent' src='"+postJson.url+"' />";
        } else if(postJson.filetype == "video")
        {
          postHtml += "<video id='myVideoPost' style='width:350px; height:350px;' class='postMediaContent' controls><source src='"+postJson.url+"' type='video/mp4'></video>";
        }
        postHtml += "<center><br><span id='myPostLikeNumber'>2.1k</span> Likes<br></center>";
        postHtml += "<span id='postCaption' class='postCaption'>"+postJson.caption+"</span>";
        postHtml += "<br><center><button id='deletePostBtn' class='deletePostBtn' onclick='deleteStory()'>Delete Post</button></center>";
        document.getElementById("myPostContainer").innerHTML = postHtml;
        getListOfUserLikesForMyPost();
      }
    //  alert(this.responseText);
    }
  };
  xhttp.open("POST", "/getMyPost", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("");
}

function deleteStory(){
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
       // the post has been deleted
    //  alert(this.responseText);
       generateMyCurrentPost();
    }
  };
  xhttp.open("POST", "/deleteMyPost", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("");
}

function getListOfUserLikesForMyPost(){
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
       // the post has been deleted
    //  alert(this.responseText);
      if(this.responseText == "")
      {
        document.getElementById("myPostLikeNumber").innerHTML = 0;
      } else {
        var postJson = JSON.parse(this.responseText);
        document.getElementById("myPostLikeNumber").innerHTML = postJson.length;
      }
    }
  };
  xhttp.open("POST", "/getListOfUserLikes", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("");
}

window.addEventListener("load", function(){
    // ....
    generateMyCurrentPost();

    document.getElementById("storyPageBackBtn").addEventListener("click",
      function () { window.history.back(); }
    );

});

/*function storySearchResults() {
  var searchStr =  document.getElementById("userStorySearchBar").value;
  if(searchStr.trim().length == 0)
  {
     // if the user is not searching for anything we show every post
     // this still does not work well. It still needs more work
     document.getElementById("postContainer").style.display = 'block';
    return;
  }

  // now we filter
       document.getElementById("postContainer").style.display = 'hide';
        var  postDivs = document.getElementsByClassName("postContainer");
          for (var j = 0, divsLen = postDivs.length; j < divsLen; j++) {
              if (postDivs[j].textContent.search('\\b\\w*' + searchStr + '\\w*\\b') > 0) {
                  postDivs[j].style.display = "block";
              }
          }


}


window.addEventListener("load", function() {

  document.getElementById("userStorySearchBar").addEventListener("keyup",
    function(){ storySearchResults(); }
  );


});
*/

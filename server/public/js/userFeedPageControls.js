


function cleanProfilePage(){
  document.getElementById("profilePhotoError").innerHTML = "";
  document.getElementById("newProfilePhotoFileChooser").value = "";
}

function clearSettingsPage(){
  document.getElementById("passwordChangeStatusSpan").innerHTML = "";
}


function populateSearchResults()
{
  console.log("Hello");
  var query =  document.getElementById("searchBar").value;
  document.getElementById("searchResultsDiv").innerHTML = "";
  if(query.trim().length == 0)
  {
    hideSearchResultDiv();
    return;
  }
  // now we do a post request
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "")
      {
        // no result found
        document.getElementById("searchResultsDiv").innerHTML = "No user found";
      } else {
        var userJsonArray = JSON.parse(this.responseText);
        var i;
        for (i = 0; i < userJsonArray.length; i++) {
          var newResSpan = "<a href='/viewUser/"+userJsonArray[i].email+"'><h3 id='friendFullName' class='friendFullName'>"
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
          newResSpan += "</a>";
          document.getElementById("searchResultsDiv").innerHTML += newResSpan;
        }
      }
    //  alert(this.responseText);
    }
  };
  xhttp.open("POST", "/searchUsers", true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("query="+query);
}

function showSearchResultDiv()
{
    populateSearchResults();
    var searchResultDiv = document.getElementById("searchResultsDiv");
    searchResultDiv.style.display = "block";
}

function hideSearchResultDiv()
{
  var searchResultDiv = document.getElementById("searchResultsDiv");
  searchResultDiv.style.display = "none";
}

function searchOperation()
{
    showSearchResultDiv();
}




function openSettingsProfileConnectionsDiv(div) {
  // fade the body
  var settingsDiv = document.getElementById("settingsDiv");
  var profileDiv = document.getElementById("profileDiv");
  var connectionsDiv = document.getElementById("connectionsDiv");
  if(div == "settings")
  {
    // close other menu
    profileDiv.style.display = "none";
    connectionsDiv.style.display  = "none";
    settingsDiv.style.display = "block";
    clearSettingsPage();
  } else if(div == "profile")
  {
    profileDiv.style.display = "block";
    connectionsDiv.style.display  = "none";
    settingsDiv.style.display = "none";
    cleanProfilePage();
  } else if(div == "connections") {
    profileDiv.style.display = "none";
    connectionsDiv.style.display  = "block";
    settingsDiv.style.display = "none";
    generateFollowList();
    generateFollowersList();
  }
  return;
}

function closeSettingsProfileDiv()
{
  var settingsDiv = document.getElementById("settingsDiv");
  var profileDiv = document.getElementById("profileDiv");
  var connectionsDiv = document.getElementById("connectionsDiv");
  profileDiv.style.display = "none";
  settingsDiv.style.display = "none";
  connectionsDiv.style.display = "none";
  return;
}

function openNewPostDiv()
{
  var blurDiv = document.getElementById("newPostDivBlur");
  var newPostDiv = document.getElementById("newPostDiv");
  blurDiv.style.display = "block";
  newPostDiv.style.display = "block";
  blurDiv.style.zIndex  = "12";
  newPostDiv.style.zIndex  = "13";
  document.getElementById("newPostTextArea").value = "";
  document.getElementById("fileUpload").value = "";
  document.getElementById("postProgressSpan").innerHTML = "";
  return;
}

function closeNewPostDiv()
{
  var blurDiv = document.getElementById("newPostDivBlur");
  var newPostDiv = document.getElementById("newPostDiv");
  blurDiv.style.display = "none";
  newPostDiv.style.display = "none";
  blurDiv.style.zIndex  = "0";
  newPostDiv.style.zIndex  = "0";
  return;
}


function logOut()
{
  location.href = '/logout';
}


function deleteAccount()
{

}














// do this when window loads..
window.addEventListener("load", function(){
    // ....
    document.getElementById("bottomNavSettingsBtn").addEventListener("click",
     function(){ openSettingsProfileConnectionsDiv("settings") }
     );
    document.getElementById("bottomNavProfileBtn").addEventListener("click",
      function(){ openSettingsProfileConnectionsDiv("profile") }
  );
  document.getElementById("bottomNavConnectionsBtn").addEventListener("click",
    function () { openSettingsProfileConnectionsDiv("connections") }
  );
  document.getElementById("bottomNavHomeBtn").addEventListener("click",
    function(){ closeSettingsProfileDiv() }
    );
    document.getElementById("newPostDivCloseBtn").addEventListener("click",
      function(){ closeNewPostDiv() }
      );
      document.getElementById("bottomNavNewPostBtn").addEventListener("click",
        function(){ openNewPostDiv() }
        );



    // setting div buttons
    document.getElementById("logoutBtn").addEventListener("click",
      function(){ logOut() }
    );

    document.getElementById("deleteAccountBtn").addEventListener("click",
      function(){ deleteAccount() }
    );




      // if outside body is clicked the hide
      window.addEventListener('click', function(e){
          if (document.getElementById('searchResultsDiv').contains(e.target)){
            // Clicked in box
          } else
          {
            hideSearchResultDiv();
          }
      });

      document.getElementById("searchBar").addEventListener("keyup",
        function(){ showSearchResultDiv(); }
      );

    closeSettingsProfileDiv();
});

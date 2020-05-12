const { createElement, useState, useEffect } = React;
const html = htm.bind(createElement);

const Feed = () => {
  const [posts, setPosts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [playingMediaPostId, setPlayingMediaPostId] = useState(null);
  const [isSearching, setIsSearching] = useState(false);
  const [currentSearchFilter, setCurrentSearchFilter] = useState(null)

  const onMediaClick = (e, postId) => {
    e.preventDefault();
    const media = e.target.closest(".mediaContainer").querySelector(".media");

    if(playingMediaPostId == postId) {
      setPlayingMediaPostId(null);
    }
    else {
      setPlayingMediaPostId(postId);
    }
  };

  const onUserSearch = (e) => {
    console.log("test123");
    const input = e.target.value.toLowerCase();

    if(input.length > 0) {
      setCurrentSearchFilter(input);
      setIsSearching(true);
    }else{
      setCurrentSearchFilter(null);
      setIsSearching(false);
    }
  }


  useEffect(() => {
    getData(document.getElementById("getPosts").value, (data) => setPosts(data));
    setIsLoading(false);
  }, []);

  console.log(posts);
  //if(!isSearching) {
  if(isLoading) {
    return html`<center><img src=${document.getElementById("spinner").value} /></center>`;
  }else{

    if(!isSearching) {
      if(posts.length == 0) {
        return html `
          <center><input className="searchBar" id="userFeedSearchBar" type="text" placeholder="Discover stories from your followers..." onChange=${(e) => onUserSearch(e)}/></center>
          <center><h3>The users you follow have not posted any stories :(</h3></center>
        `;
      }else{
        return html `
          <center><input className="searchBar" id="userFeedSearchBar" type="text" placeholder="Discover stories from your followers..." onChange=${(e) => onUserSearch(e)}/></center>
          ${posts.map((post) => {
            let isPlaying = false;
            if(post.id == playingMediaPostId) {
              isPlaying = true;
            }
            return html `
              <${Post} id=${post.id} url=${post.url} filetype=${post.filetype} caption=${post.caption} userid=${post.userid} key=${post.id} isPlaying=${isPlaying} onMediaClick=${onMediaClick}/>
              `;
            })
          }
        `;
      }
    }else{
      return html `
        <center><input className="searchBar" id="userFeedSearchBar" type="text" placeholder="Discover stories from your followers..." onChange=${(e) => onUserSearch(e)}/></center>
        ${posts.map((post) => {
            if(post.caption.toLowerCase().includes(currentSearchFilter) || post.userid.toLowerCase().includes(currentSearchFilter)) {
              let isPlaying = false;
              if(post.id == playingMediaPostId) {
                isPlaying = true;
              }
              return html `
                <${Post} id=${post.id} url=${post.url} filetype=${post.filetype} caption=${post.caption} userid=${post.userid} key=${post.id} isPlaying=${isPlaying} onMediaClick=${onMediaClick}/>
                `;
            }
          })
        }
      `;
    }
  }
}




const getPostLikeUserList = (postId) => {
  const [noLikes, setNoLikes] = useState("0");
  const [postLikeUserList, setPostLikeUserList] = useState([]);

  async function fetchData() {
    const res = await fetch("/getPostLikesUserList/"+postId);
    res
      .json()
      .then(res => setPostLikeUserList(res))
      .catch(err => setNoLikes(err));
  }

  useEffect(() => {
    fetchData();
  }, []);

  return html`
      <span>${JSON.stringify(postLikeUserList.length)}</span>
  `;
};

const likeSectionObject = (postId) => {

  const [didNotLike, setDidNotLike] = useState("Like");
  const [likeResponse, setLikeResponse] = useState("Like");

  const [noLikes, setNoLikes] = useState("0");
  const [postLikeUserList, setPostLikeUserList] = useState([]);

  const [prevLikedError, setisPostLikedResponseError] = useState("NO");
  const [prevLiked, setisPostLikedResponse] = useState("YES");

  async function likePost() {
    const res = await fetch("/likePost/"+postId);
    res
    .json()
    .then(res => {
      setLikeResponse("Unlike");
      fetchNumberOfLikes();
    })
    .catch(err => {
      alert(err);
    });
  }

  async function fetchNumberOfLikes() {
    const res = await fetch("/getPostLikesUserList/"+postId);
    res
      .json()
      .then(res => {
         setPostLikeUserList(res);
         console.log(postLikeUserList);
      })
      .catch(err => {
        alert(err);
      });
  }

  async function unlikePost() {
    const res = await fetch("/unlikePost/"+postId);
    res
    .json()
    .then(res => {
      setLikeResponse("Like");
      fetchNumberOfLikes();
    })
    .catch(err => setDidNotLike(err));
  }

  async function isPostLiked() {
    const res = await fetch("/isPostLiked/"+postId);
    res
    .json()
    .then(res => {
     if(res == "YES")
      {
        setLikeResponse("Unlike");
      } else {
        setLikeResponse("Like");
      }
    })
    .catch(err => {
      alert(err);
    });
  }


  function clickLike() {
    if(likeResponse == "Like")
    {
    //  alert("Liked");
      likePost();

    } else
    {
    //  alert("Unliked");
      unlikePost();
    }
  }

  useEffect(() => {
    fetchNumberOfLikes();
    isPostLiked();
  }, []);

  return html`
    <li id="likeCount" className="postControl" >Liked by <span>${JSON.stringify(postLikeUserList.length)}</span></li>
    <li className="postControl"><button className="likeBtn" id="likeBtn_${postId}" onClick=${clickLike}>${likeResponse}</button></li>
  `;
};

const isMediaType = type => type == "video" || type == "audio";

const Post = (props) => {
    PropTypes.checkPropTypes(Post.propTypes, props, 'post', 'Post');
    const {id, caption, url, filetype, userid, isPlaying, onMediaClick} = props;
    const likeSection = likeSectionObject(id);
    console.log("post render");

    useEffect(() => {
      if(isMediaType(filetype)) {
        const media = document.getElementById(`mediaContainer_${id}`).querySelector(".media");
        if(isPlaying) {
          if(media.paused) media.play();
        } else if(!media.paused) {
          media.pause();
        }
      }
    });

    const getMediaContent = () => {
      switch(filetype) {
        case "audio":
          return html`
            <div className="mediaContainer" id=${`mediaContainer_${id}`} onClick=${(e) => onMediaClick(e, id)} style=${{position: 'relative'}}>
              <audio className="media media_audio" controls><source src="${url}" type="audio/mpeg"/></audio>
              <div
                className="playButtonCover"
                style=${{
                    position: 'absolute',
                    width: '40px',
                    height: '50px',
                    top: '0px',
                    cursor: 'pointer'
                }}
              > </div>
            </div>
          `;
        case "image":
          return html`
            <div className="mediaContainer">
              <img className="postMediaContent media media_image" src="${url}" onClick=${(e) => console.log(e)} />
            </div>
          `;
        case "video":
          return html`
          <div className="mediaContainer" id=${`mediaContainer_${id}`} style=${{position: 'relative'}} onClick=${(e) => onMediaClick(e, id)}>
            <video className="media media_video" controls><source src="${url}" type="video/mp4"/></video>
            <div className="playButtonCover" style=${{
              width: '50px',
              height: '40px',
              position: 'absolute',
              bottom: '35px',
              cursor: 'pointer'
            }}></div>
          </div>
          `;
      }
    };

    return html`
    <center>
      <div id="postContainer" className="postContainer">
        <h4 className="userFullName">${userid}</h4>
        ${getMediaContent()}
        <ul id="postFooterControls" className="postFooterControls">
          ${likeSection}
        </ul>
        <span id="postCaption" className="postCaption">
          ${caption}
        </span>
      </div>
     </center>
    `;
}


Post.propTypes = {
  id: PropTypes.string.isRequired,
  caption: PropTypes.string.isRequired,
  url: PropTypes.string.isRequired,
  filetype: PropTypes.string.isRequired,
  userid: PropTypes.string.isRequired,
  isPlaying: PropTypes.bool.isRequired,
  onMediaClick: PropTypes.func.isRequired
};

ReactDOM.render(html`<${Feed}/>`, document.getElementById("feedContainer"));

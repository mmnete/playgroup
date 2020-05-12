// const postData = (url, obj) => {
//     fetch(url, {
//         method: 'POST',
//         headers: {'Content-Type': 'application/json', 'Csrf-Token': document.querySelector('input[name=csrfToken]').value},
//         body: JSON.stringify(obj)
//     })
//         .then((response) => response.json())
//         .then((msg) => {
//             if(msg.level != "danger") {
//                 this.props.updateLogin(name, msg);
//             } else {
//                 this.props.changeView(views.LOGIN, msg)
//             }
//     })
//     .catch((err) => console.error(err));
// };

const getData = (url, callback) => {
    fetch(url)
    .then((response) => response.json())
    .then((data) => {
        callback(data);
    })
    .catch((err) => console.error(err));
};
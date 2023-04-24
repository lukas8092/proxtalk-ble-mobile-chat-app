$(document).ready(function () {
  let socket = new WebSocket("wss://proxtalk.live:85");
  var token = "p6ozp18jl26Wbk47ozq0s5kFj2Y45a"

  socket.onopen = function (e) {
    socket.send('{"subscribe_to_messages": ""}');
    addMessage("Connected to live messages")
  };

  socket.onmessage = async function (event) {
    console.log(event.data);
    try {
      var data = JSON.parse(event.data);
      if (data["id"] == undefined) {
        return;
      }
      var el = $("#item-template").clone();
      el.attr("id", "m" + data["id"]);
      el.find(".item-username").text(data["username"]);
      el.find(".item-content").text(data["content"]);
      el.find(".item-img").attr("src", "https://devlukas.tk:81/message/profile_image?token="+token+"&id=" + data["id"]);
      if (data["image"] != null) {
        await sleep(5000);
        var img = $('<img class="content-img" src="https://devlukas.tk:81/message/image?token=' + token + '&id=' + data["id"] + '" alt="">');
        el.find(".item-content-img").attr("id", data["id"]);
        el.find(".item-content-img").css("display", "block");
        el.find("#" + data["id"]).append(img);
      }
      el.css("display", "block");
      el.addClass('animation-fadeInDown');
      $(".items").prepend(el);
    }
    catch (err) {

    }

  };

  socket.onclose = function (event) {
    if (event.wasClean) {
      addMessage("Disconected from server");
    } else {
      addMessage("Disconected from server, connection lost");
    }
  };

  socket.onerror = function (error) {
    addMessage("Cant reach a server");
  };

  function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  function addMessage(text) {
    var el = $("#item-template").clone();
    el.attr("id", "start");
    el.find(".item-username").text("ProxTalk");
    el.find(".item-content").text(text);
    el.find(".item-img").attr("src", "logo.jpg");
    el.css("display", "block");
    el.addClass('animation-fadeInDown');
    $(".items").append(el);
  }
});
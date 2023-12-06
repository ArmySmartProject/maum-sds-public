"use strict";
// created by babel

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var SocketClient = function SocketClient(userType, userId) {
  var _this = this;

  _classCallCheck(this, SocketClient);

  this.userType = userType;
  this.userId = userId;
  this.connected = false;
  this.myRooms = []; // todo: should 'roomId', 'bot' be multi value?

  this.roomId = '';
  this.bot = false; // {roomId: roomId, unreadMsgCnt: unreadMsgCnt}

  this.unreadMsgs = [];
  var socket = io.connect(serverURL, {
    transports: ['websocket']
  }); // var socket = io.connect(serverURL + '/connectors');

  this.socket = socket;
  socket.once('connection', function (data) {
    if (data.type === 'connected') {
      _this.connected = true;
    }
  });
};

var socketClient = SocketClient.prototype; // websocket 서버에서 event 응답 시 UI action을 정의

socketClient.setEventListeners = function (writeMessage, createLoadingMsg, deleteLoadingMsg) {
  var _this2 = this;

  var writeChatList = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : undefined;
  var handleChatEndUI = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : undefined;
  var writePreviousMessages = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : undefined;
  var chatbotFirstMsg = arguments.length > 6 && arguments[6] !== undefined ? arguments[6] : undefined;
  this.socket.on('system', function (data) {
    writeMessage(data.roomId, 'system', data.talkObj);
  });
  this.socket.on('message', function (data) {
    // console.log('message 도착');
    // console.log(data);
    window.parent.postMessage({
      data: "chatbot_msg",
      contents: data.roomId,
      jsonDebug: data.talkObj.adapterResponse
    }, '*');
    var talker = 'other';

    if (data.userId === _this2.userId) {
      talker = 'me';
    } else if (data.userType === 'supporter') {
      talker = 'supporter'
    } else if (data.userType === 'alarm') {
      talker = 'alarm'
    }

    writeMessage(data.roomId, talker, data.talkObj);
  });
  this.socket.on('getLastMsg', function (data) {
    console.log('getLastMsg 도착');
    var lastMsg = data.lastMsg;
    var unreadMsgCnt = data.unreadMsgCnt;

    var foundRoom = _this2.unreadMsgs.find(function (room) {
      return room.roomId === data.roomId;
    });

    if (foundRoom) {
      foundRoom.unreadMsgCnt = unreadMsgCnt;
    } else {
      _this2.unreadMsgs.push({
        roomId: data.roomId,
        unreadMsgCnt: unreadMsgCnt
      });
    }

    writeMessage(data.roomId, '', lastMsg);
  });
  this.socket.on('getPreviousMsgs', function (data) {
    console.log('getPreviousMsgs 도착');
    _this2.roomId = data.roomId;
    var previousMsgs = data.previousMsg;

    if (writePreviousMessages) {
      writePreviousMessages(data.roomId, previousMsgs);
    }
  }); // userType: supporter

  if (this.userType === 'supporter') {
    this.socket.on('getAvailableRooms', function (data) {
      console.log('getAvailableRoom 도착');

      _this2.socket.emit('getMyRooms', {
        userId: _this2.userId
      });

      writeChatList(data.rooms, 'av_rooms');
    });
    this.socket.on('getMyRooms', function (data) {
      console.log('getRoom 도착');
      _this2.myRooms = data.rooms;
      writeChatList(data.rooms, 'my_rooms');
      data.rooms.forEach(function (room) {
        _this2.joinRoom(room.roomId);
      });
    });
  } // userType: user


  if (this.userType === 'user') {
    this.socket.on('leaveRoom', function (data) {
      console.log('leaveRoom 도착'); // 상담 종료 UI action

      if (handleChatEndUI) {
        handleChatEndUI();
      }
    }); // 'end Conversation' event By Server

    this.socket.on('endConversation', function (data) {
      window.parent.postMessage({
        data: "chatbot_counsel_end",
        contents: data.roomId
      }, '*');
      console.log('endConversation 도착');

      _this2.endChat(data.roomId);
      chatbotFirstMsg(data.roomId); // fast 상담종료 후 처음으로 및 채팅 활성화
    });
  }

  this.socket.on('err', function (data) {
    alert(data.message);
  });

  this.socket.on('enteringEvent', function(data) {
    console.log('상담사 입력중');
    createLoadingMsg();
  });

  this.socket.on('bluringEvent', function(data) {
    console.log('상담사 입력 취소');
    deleteLoadingMsg();
  });
};

socketClient.send2Server = function (roomId, msg, csService) {
  var meta = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
  // console.log('send msg to server:', msg);
  this.socket.emit('message', {
    roomId: roomId,
    userType: this.userType,
    userId: this.userId,
    message: msg,
    csService: csService,
    meta: meta
  });
};

socketClient.getAvailableRooms = function () {
  this.socket.emit('getAvailableRooms', {
    userId: this.userId
  });
};

socketClient.transferToAgent = function () {
  this.socket.emit('transferToAgent', {
    userType: this.userType,
    userId: this.userId,
    roomId: this.roomId
  });
  this.bot = false;
};

socketClient.startChat = function (roomId) {
  this.socket.emit('startConversation', {
    userType: this.userType,
    userId: this.userId,
    roomId: roomId
  });
};

socketClient.endChat = function (roomId) {
  this.socket.emit('leaveRoom', {
    userType: this.userType,
    userId: this.userId,
    roomId: roomId
  });
}; // supporter 용


socketClient.endUserChat = function (roomId) {
  this.socket.emit('endConversation', {
    userType: 'supporter',
    userId: this.userId,
    roomId: roomId
  });
};

socketClient.createRoom = function (csService, csCategory) {
  var _this3 = this;

  // console.log('createRoom');
  this.socket.emit('createRoom', {
    userType: this.userType,
    userId: this.userId,
    csService: csService,
    csCategory: csCategory
  });
  return new Promise(function (resolve, reject) {
    _this3.socket.once('createRoom', function (data) {
      console.log('createRoom 도착');
      resolve(data.roomId);
    });
  });
};

socketClient.joinRoom = function (roomId) {
  var _this4 = this;

  console.log('joinRoom');
  this.socket.emit('joinRoom', {
    roomId: roomId,
    userType: this.userType,
    userId: this.userId
  });
  return new Promise(function (resolve, reject) {
    _this4.socket.once('joinRoom', function (data) {
      console.log('joinRoom 도착');
      _this4.roomId = data.roomId;
      resolve(data);
    });
  });
};

socketClient.createNJoinRoom = function () {
  var _this5 = this;

  var bot = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : false;
  var csService = arguments.length > 1 ? arguments[1] : undefined;
  var csCategory = arguments.length > 2 ? arguments[2] : undefined;
  console.log('createNJoinRoom');
  this.socket.emit('createNJoinRoom', {
    userType: this.userType,
    userId: this.userId,
    bot: bot,
    csService: csService,
    csCategory: csCategory
  });
  return new Promise(function (resolve, reject) {
    _this5.socket.once('createNJoinRoom', function (data) {
      console.log('createNJoinRoom 도착:' + data.roomId);
      _this5.roomId = data.roomId;
      _this5.bot = bot;
      resolve(data);
      if (previousSess === 'y') {
        socketRoomId = data.roomId;
        data.previousMsg.forEach(function (talkObj, index) {
          // index 0:'처음으로', 1:'공지사항'
          if (talkObj.userType === 'user' && index !== 0 && talkObj.message !== undefined && talkObj.message !== '챗봇공지사항') {
            previousYn = 'y';
            createUsrMsg(talkObj.message, talkObj.timeDetail);
          } else if (index !== 0 && talkObj.adapterResponse !== undefined && talkObj.message !== undefined) {
            if (talkObj.adapterResponse.intent !== '선톡') {
              if (talkObj.lang !== undefined && talkObj.lang !== "") {
                lang = talkObj.lang;
              }
              previousTime = 'y';
              previousYn = 'y';
              writeMessage(data.roomId, 'other', talkObj);
            }
          } else if (talkObj.userType === 'system') {
            writeMessage(data.roomId, 'system', talkObj);
          } else if (talkObj.message !== undefined && talkObj.userType === 'supporter') {
            previousTime = 'y';
            writeMessage(data.roomId, 'supporter', talkObj);
          }
          });
      }
    });
  });
};

socketClient.getLastMsg = function (roomId) {
  console.log('getLastMsg, ' + roomId);
  this.socket.emit('getLastMsg', {
    roomId: roomId
  });
};

socketClient.getPreviousMsgs = function (roomId) {
  console.log('getPreviousMsgs: ' + roomId);
  this.socket.emit('getPreviousMsgs', {
    roomId: roomId
  });
};

socketClient.getMyRooms = function () {
  return this.myRooms;
}; // 일단 user쪽만 처리


socketClient.readAllMsgs = function (roomId) {
  if (this.userType !== 'user') {
    return;
  }

  console.log('readAllMsgs: ' + roomId);
  this.socket.emit('readAllMsgs', {
    roomId: roomId,
    userType: this.userType
  });
};

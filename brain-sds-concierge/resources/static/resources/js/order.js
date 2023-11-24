module.exports = {
    newMsg: function(name, cusReqValue, cusMsgValue){
        return {
            "text": "새로운 요청이 들어왔습니다.",
            "attachments": [
                {
                    mrkdwn_in: ["text"],
                    color: "#ff0000",
                    callback_id: "cusReq",
                    fields: [
                        {
                            title: "요청 고객",
                            value: name,
                            short: false
                        },
                        {
                            title: "요청 물품",
                            value: cusReqValue,
                            short: false
                        },
                        {
                            title: "메시지",
                            value: cusMsgValue,
                            short: false
                        }
                    ],
                    actions: [
                        {
                            "type": "button",
                            "name": "check",
                            "text": "check",
                            "value": "check"
                        },
                        {
                            "type": "button",
                            "name": "done",
                            "text": "done",
                            "value": "done"
                        }
                    ]
                },
                
            ]
        }
    },
    updateMsg: function(originMsg){
        return {
            text: originMsg.text,
            attachments: [{
                    mrkdwn_in: originMsg.attachments.mrkdwn_in,
                    callback_id: "cusReq",
                    fields: originMsg.attachments[0].fields,
            }]
        }
    }
}
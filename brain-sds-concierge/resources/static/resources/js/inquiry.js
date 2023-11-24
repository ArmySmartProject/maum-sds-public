module.exports = {
    newMsg: function(name, cusGenderValue, cusNumValue, cusMsgValue){
        return {
            "text": "새로운 문의사항이 들어왔습니다.",
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
                            title: "성별",
                            value: cusGenderValue,
                            short: false
                        },
                        {
                            title: "연락처",
                            value: cusNumValue,
                            short: false
                        },
                        {
                            title: "문의사항",
                            value: cusMsgValue,
                            short: false
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

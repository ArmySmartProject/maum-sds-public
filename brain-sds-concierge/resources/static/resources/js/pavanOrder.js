module.exports = {
    newMsg: function(cusReqValue, data){
        var common_fields = [
            {
                title: "주문 고객",
                value: data.name,
                short: false
            },
            {
                title: "연락처",
                value: data.phone,
                short: false
            },
            {
                title: "이메일",
                value: data.email,
                short: false
            },
            {
                title: "주문 내역",
                value: cusReqValue,
                short: false
            },
            {
                title: "픽업 시간",
                value: data.pickupTime,
                short: false
            },
            {
                title: "메시지",
                value: data.msg,
                short: false
            },
            {
                title: "결제방법",
                value: data.payment,
                short: false
            },
            {
                title: "결제금액",
                value: data.totalPrice,
                short: false
            }
        ];

        if (data.host === '71') {
            var takeField = {
                title: "수령방법",
                value: data.take,
                short: false
            };
            common_fields.splice(5, 0, takeField)
        }

        return {
            "text": "새로운 주문이 들어왔습니다.",
            "attachments": [
                {
                    mrkdwn_in: ["text"],
                    color: "#ff0000",
                    callback_id: "pavanReq",
                    fields: common_fields,
                    pavanAction: [
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
                    callback_id: "pavanReq",
                    fields: originMsg.attachments[0].fields,
            }]
        }
    }
}

require('dotenv').config();

const express = require('express');
const app = express();
const fs = require('fs');
const bodyParser = require('body-parser');
const http = require('http');
const https = require('https');
const request = require('request');

const order = require('./resources/static/resources/js/order');
const pavanOrder = require('./resources/static/resources/js/pavanOrder');
const inquiry = require('./resources/static/resources/js/inquiry');
const inquiryMail = require('./resources/static/resources/js/inquiryMail');

const sql = require('mssql');

//Slack
const { IncomingWebhook } = require('@slack/webhook');
let webhook;

const httpServer = http.createServer(app);
let option = {

}
const httpsServer = http.createServer(option, app);

// DB 연결정보
const connectionConfig = {
	user: 'sa',
	password: 'Mindslab!1',
	server: '182.162.19.19',
	port:1433,
	options: {
		database: 'Chatbot',
		instanceName    : '',
		connectTimeout  : 15000,
		requestTimeout  : 15000,
		useUTC          : true
	}
};

app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());

app.get('/auth', (req,res)=>{
    res.sendFile(__dirname + "/install.html");
});

app.get('/auth/redirect', (req, res) =>{
    console.log(req.query.code);
    var options = {
        uri: 'https://slack.com/api/oauth.access?code='
            +req.query.code+
            '&client_id='+process.env.SLACK_CLIENT_ID+
            '&client_secret='+process.env.SLACK_CLIENT_SECRET
    }
    request.get(options, (error, response, body) => {
        var JSONresponse = JSON.parse(body)
        if (!JSONresponse.ok){
            console.log(JSONresponse)
            res.send("Error encountered: \n"+JSON.stringify(JSONresponse)).status(200).end()
        }else{
            path = './db/userinfo.json';
            fs.exists(path, (exists)=>{
                if(exists){
                    let data = fs.readFileSync(path);
                    let json = JSON.parse(data);
                    json.data.push(JSON.stringify(JSONresponse));
                    fs.writeFile(path,JSON.stringify(json),(err)=>{
                        if(err) console.log(err);
                    });
                }else{
                    console.log('b');
                    let db = {
                        data:[]
                    }

                    fs.appendFile(path, JSON.stringify(db), 'utf8', (err)=>{
                        if(err) console.log(err);
                    });

                    let data = fs.readFileSync(path);
                    let json = JSON.parse(data);
                    json.data.push(JSON.stringify(JSONresponse));
                    fs.writeFile(path,JSON.stringify(json),(err)=>{
                        if(err) console.log(err);
                    });
                }
            });
            console.log(JSONresponse)
            res.send("Success!")
        }
    })
})

app.post('/cusReq',(req, res)=>{
    let data = req.body.data;
    let name = data.name;

    let cusReq = data.reqList;
    let cusReqValue = "";
    cusReq.forEach((element, index)=>{
        if(index == 0) { cusReqValue += element; }
        else { cusReqValue += ",  " + element; }

    });

    let cusMsg = data.msg;
    let cusMsgValue = "";
    cusMsg.forEach((element, index)=>{
        cusMsgValue += (index+1) + ". " +element + "\n";
    });

    let url = "https://hooks.slack.com/services/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";

    if (data.host === "demo") {
        url = process.env.REDTIE_WEBHOOK_URL;
    }
    webhook = new IncomingWebhook(url);
    webhook.send(order.newMsg(name, cusReqValue, cusMsgValue));

    let resResult = {
        message: {
            "message": "Success",
            "status": 0
        },
        data: {
            name: name,
            cusReq: cusReqValue,
            cusMsg: cusMsgValue
        }
    }
    res.send(resResult);
});

app.post('/pavanReq',(req, res)=>{
  let data = req.body;

  let cusReqValue = "";
  if (data.hasOwnProperty('reqList')) {
      let pavanReq = data.reqList;
      pavanReq.forEach((element, index)=>{
          cusReqValue += (index+1) + ". " + element[0] + " " + element[1] + "\n";
      });
  }

  let url = '';
  if (data.host === '71') { // PAVAN
      url = "https://hooks.slack.com/services/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";
  } else if (data.host === '72') { // DELIGHT
      url = "https://hooks.slack.com/services/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";
  }

  webhook = new IncomingWebhook(url);
  webhook.send(pavanOrder.newMsg(cusReqValue, data));

  let resResult = {
    message: {
        "message": "Success",
        "status": 0
    },
    data: {
        pavanReq: cusReqValue,
        resource: data
    }
  }
  res.send(resResult);
});

app.post('/inquiryReq',(req, res)=>{

    let data = req.body.data;
    let host = data.host;
    // default is gt_hud (2)
    let url = process.env.GT_HUD_WEBHOOK_URL;

    // inquiry information
    let name = data.name;
    let gender = data.gender;
    let phone = data.phone;
    let inquiryMsg = data.inquiry;

    // get host's slack url
    if (host === "4") {url = process.env.LIFE_DENTAL_WEBHOOK_URL;}
    else if (host === "5") {url = process.env.MAYFIELD_WEBHOOK_URL;}
    else if (host === "8") {url = process.env.VALUE_BUSAN_WEBHOOK_URL;}
    else if (host === "9") {url = process.env.ACTIVE_LIFE_WEBHOOK_URL;}
    else if (host === "10") {url = process.env.MERCURE_WEBHOOK_URL;}
    else if (host === "11") {url = process.env.RAMADA_WEBHOOK_URL;}

    webhook = new IncomingWebhook(url);
    webhook.send(inquiry.newMsg(name, gender, phone, inquiryMsg));

    let resResult = {
        message: {
            "message": "Success",
            "status": 0
        },
        data: {
            host: host,
            name: name,
            gender: gender,
            phone: phone,
            inquiryMsg: inquiryMsg
        }
    }
    res.send(resResult);
});

app.post('/inquiryMailReq',(req, res)=>{

    let data = req.body;
    let host = data.host;
    let chatbot_email = process.env.CHATBOT_EMAIL;
    let chatbot_pwd = process.env.CHATBOT_PWD;
    let host_email = process.env.LIFE_DENTAL_EMAIL;
    console.log(data);

    // default is gt_hud (2)
    let cus_email = '';

    var connection = new sql.connect(connectionConfig).then(pool => {
        let strQuery = "SELECT Name, Email "
            + "FROM Chatbot.dbo.Account "
            + "WHERE No='" + host + "'";

        return (pool.request().query(strQuery));
    }).then(function(result) {
        let cus_name = result.recordset[0].Name;
        cus_email = result.recordset[0].Email;
        var email_list = cus_email.split(',');
        if (cus_email !== '' && cus_email !== null && cus_email !== undefined) {
            for (var i in email_list) {
                var email = email_list[i].trim();
                if (email !== '' && email !== null && email !== undefined) {
                    inquiryMail.sendMail(data, cus_name, email);
                }
            }
        } else {
            console.log('failed get email address');
            res.status(500).send('failed get email address!');
        }
    });
});

app.post('/action',(req,res)=>{
    let payload = req.body.payload;
    payloadObj = JSON.parse(payload)
    originMsg = payloadObj.original_message;

    let msg = order.updateMsg(originMsg);
    let options = {
        uri: 'https://slack.com/api/chat.postMessage',
        form: {
            token: process.env.SLACK_AUTH_TOKEN,
            channel: payloadObj.channel.id
        }
    };
    request.post(options, function (error, response, body) {
        switch(payloadObj.actions[0].value)
        {
            case "check":
                msg.attachments[0].color = "#ffff00";
                msg.attachments[0].actions = [
                    {
                        "type": "button",
                        "name": "cancel",
                        "text": "cancel",
                        "value": "cancel"
                    },
                    {
                        "type": "button",
                        "name": "done",
                        "text": "done",
                        "value": "done"
                    }
                ];
                msg.attachments[0].footer = "<@" + payloadObj.user.id + ">가 체크했습니다.";

                res.json(msg);
                break;
            case "cancel":
                msg.attachments[0].color = "#ff0000";
                msg.attachments[0].actions = [
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
                ];
                msg.attachments[0].footer = "<@" + payloadObj.user.id + ">가 취소했습니다.";

                res.json(msg);
                break;
            case "done":
                msg.attachments[0].color = "#00ff00";
                msg.attachments[0].actions = [
                    {
                        "type": "button",
                        "name": "cancel",
                        "text": "cancel",
                        "value": "cancel"
                    }
                ];
                msg.attachments[0].footer = "<@" + payloadObj.user.id + ">가 완료했습니다.";

                res.json(msg)
                break;
            default:
                break;
        }
    });
})

app.post('/commonAction', (req, res) => {
    let payload = req.body.payload;
    payloadObj = JSON.parse(payload);
    originMsg = payloadObj.original_message;

    let msg = pavanOrder.updateMsg(originMsg);
    let options = {
        uri: 'https://slack.com/api/chat.postMessage',
        form: {
            token: 'xoxb-948916336931-1057962416707-3p2dixoqCeR1TsbeWxM4QGnV',
            channel: payloadObj.channel.id,
        },
    };
    request.post(options, function(error, response, body) {
    switch (payloadObj.actions[0].value) {
        case 'check':
            msg.attachments[0].color = '#ffff00';
            msg.attachments[0].actions = [
                {
                    'type': 'button',
                    'name': 'cancel',
                    'text': 'cancel',
                    'value': 'cancel',
                },
                {
                    'type': 'button',
                    'name': 'done',
                    'text': 'done',
                    'value': 'done',
                },
            ];
            msg.attachments[0].footer = '<@' + payloadObj.user.id +
                '>가 체크했습니다.';

            res.json(msg);
            break;
        case 'cancel':
            msg.attachments[0].color = '#ff0000';
            msg.attachments[0].actions = [
                {
                    'type': 'button',
                    'name': 'check',
                    'text': 'check',
                    'value': 'check',
                },
                {
                    'type': 'button',
                    'name': 'done',
                    'text': 'done',
                    'value': 'done',
                },
            ];
            msg.attachments[0].footer = '<@' + payloadObj.user.id +
                '>가 취소했습니다.';

            res.json(msg);
            break;
        case 'done':
            msg.attachments[0].color = '#00ff00';
            msg.attachments[0].actions = [
                {
                    'type': 'button',
                    'name': 'cancel',
                    'text': 'cancel',
                    'value': 'cancel',
                },
            ];
            msg.attachments[0].footer = '<@' + payloadObj.user.id +
                '>가 완료했습니다.';

            res.json(msg);
            break;
        default:
            break;
    }
    });
})

httpServer.listen(5005, ()=>{
    console.log('5005 is running')
});
httpsServer.listen(5006, ()=>{
    console.log('5006 is running')
});

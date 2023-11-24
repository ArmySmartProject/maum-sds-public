const nodemailer = require('nodemailer');
const handlebars = require('handlebars');
const fs = require('fs');

module.exports = {
    sendMail : function (param, cus_name, cus_email) {

        var transport = nodemailer.createTransport({
            service: 'gmail',
            host: 'smtp.gmail.com',
            port: 587,
            secure: false,
            auth: {
                user: 'saas_chatbot@mindslab.ai',
                pass: 'msl1234~'
            }
        });

        var htmlPath = __dirname + '/../../../template/mail_format.html';
        const source = fs.readFileSync(htmlPath, 'utf-8').toString();
        var template = handlebars.compile(source);

        // 문의하기, 예약하기에 따라 타이틀 변경
        let irTitle = '';
        let resTitle = '';
        let msg = '';
        if (param.intent === "문의하기") {
            irTitle = '문의내용';
            msg = param.inquiryMsg;
        } else {
            irTitle = '예약내용';
            resTitle = '예약일자';
            msg = param.add;
        }

       // 현재 시간 yyyy-MM-DD 형식으로 구하기
       var now = new Date();
       var year = now.getFullYear();
       var month = now.getMonth() + 1;
       if(month<10) month = '0' + month;
       var date = now.getDate();
       if(date<10) date = '0' + date;

       // html 파일에 {{}}로 선언된 변수들에 데이터 채워주기
       var replacements = {
           ir_title: irTitle,
           host: cus_name,
           date: year + "-" + month + "-" + date,
           user_name: param.name,
           user_gender: param.gender,
           user_phone: param.phone,
           user_email: param.email,
           res_date: param.datetime,
           person_cnt: param.personCnt,
           event_type: param.eventType,
           table_style: param.tableStyle,
           user_inquiry: msg,
           res_title: resTitle
       };

       var htmlToSend = template(replacements);
       var mailOptions = {
           from: 'hello@redtie.ai',
           to: cus_email,
           subject: param.intent,
           html: htmlToSend
       };

        transport.sendMail(mailOptions, function (err, res2) {
            if (err) {
                console.log(err)
            } else {
                res.status(200).send(res2);
            }
        });
    }
}

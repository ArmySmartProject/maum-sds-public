from flask import Flask, render_template, send_from_directory

app = Flask(__name__, static_folder='./static')

@app.route('/')
def hello_world():
    return render_template("dev.html")

@app.route('/redtie/<hostname>/qrLocation')
def return_qr_page(hostname):
    return render_template("host_dev_qr.html", sub=hostname)

@app.route('/test/<hostname>')
def return_test_host_page(hostname):
    return render_template("host_dev.html", sub=hostname)

@app.route('/redtie/<hostname>')
def return_redtie_host_page(hostname):
    return render_template("host_dev.html", sub=hostname)

@app.route('/<hostname>')
def return_host_page(hostname):
    return render_template("host_dev.html", sub=hostname)

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5110)

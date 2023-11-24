from flask import Flask, render_template, send_from_directory

app = Flask(__name__, static_folder='./static')


@app.route('/')
def hello_world():
    return render_template("dev.html")

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5020)

from Context import Context
from flask import Flask, make_response, jsonify
import json
import os
from dotenv import load_dotenv
load_dotenv("../Config/.env")
from BluePrints.UserAPI import app_user
from BluePrints.MessagesAPI import app_message

        
app = Flask(__name__)
"""
Initializing connetion to websocket comunication
"""


context = Context()


@app.route('/app/state', methods=['GET'])
def app_state():
    with open("../Config/state.json") as file:
        data = json.load(file)
        return make_response(jsonify(data),200)


app.register_blueprint(app_user)
app.register_blueprint(app_message)


if __name__ == "__main__":
    #Debug only
    app.run(host=os.getenv("ADDRESS_API"), port=int(os.getenv("PORT_API")),debug=True)
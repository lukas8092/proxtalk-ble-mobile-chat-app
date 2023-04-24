from Context import Context
from flask import Blueprint, request, make_response, jsonify, send_file
import sys
import datetime
import os
import psycopg2
sys.path.append("./")
sys.path.append("./Entities")
from Entities.User import User
from Entities.MessageReceived import MessageReceived
sys.path.append("./Db")
from Db.UserRepository import UserRepository
from Db.MessageRepository import MessageRepository
from Db.MessageReceivedRepository import MessageRecivedRepository
from Db.DbConnection import Conn
sys.path.append("./BluePrints")
from Auth import hash_password, check_password_hash, validate_token, generate_token
from DataCheck import check_for,check_for_number,EMAIL_REGEX,PASSWORD_REGEX,TOKEN_REGEX,USERNAME_REGEX
from ImageHandler import save_image,get_profile_image_file


app_user = Blueprint('app_user', __name__)
context = Context()

if os.name == "nt":
    UPLOAD_FOLDER = 'D:\profile'
else:
    UPLOAD_FOLDER = "/home/ubuntu/profile"

@app_user.route('/user/register', methods=['POST'])
def register():
    """
    Endpoint to create user in database
    """
    form = request.json
    try:
        username = check_for(form["username"])
        passwd = check_for(form["passwd"],PASSWORD_REGEX)
        email = check_for(form["email"],EMAIL_REGEX)
        bt_mac = check_for(form["bt_mac"])
    except Exception as e:
        print(e)
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        user = User()
        user.username = username
        user.email = email
        user.passwd = hash_password(passwd)
        user.bt_mac = bt_mac
        try:
            repo.insert(user)
        except psycopg2.errors.UniqueViolation:
            conn.rollback()
            return ("",409)
    return ("",200)

@app_user.route('/user/login', methods=['GET'])
def login():
    """
    Method to verificate user with login credentials
    Returns:
        new generated token of expiration 1 day
    """
    try:
        username = check_for(request.authorization.username)
        passwd =  check_for(request.authorization.password)
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        user = repo.find_by_username(username)
        if user is None or not check_password_hash(passwd,user.passwd):
            return ("",401)
        token = generate_token()
        token_exp = datetime.datetime.now() + datetime.timedelta(days=1)
        user.token = token
        user.token_exp_date = token_exp
        user.token_exp_time = token_exp
        repo.update(user)
        json = {
            "token": user.token
        }
    return make_response(jsonify(json),200)

@app_user.route('/user/validate_token', methods=['GET'])
def validate_token_endpoint():
    """
    Endpoint to check id token is still valid
    """
    try:
        token = check_for(request.args.get("token"),TOKEN_REGEX)
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        user = validate_token(repo,token)
        if user is None:
            return ("",401)
    return ("",200)


@app_user.route('/user/upload_profile_image', methods=['POST'])
def upload_profile_image():
    """
    Method to upload image to internal storage of server
    """
    try:
        token = check_for(request.headers["token"],TOKEN_REGEX)
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        user = validate_token(repo,token)
        if user is None:
            return ("",401)
    try:
        file = request.files['file']
        ext = save_image(file,str(user.id),UPLOAD_FOLDER)
        if ext is not None:
            return ("",200)
        else:
            return ("",406) 
    except Exception as e:
        print(e)
        return ("",500)

@app_user.route('/user/profile_image', methods=['GET'])
def get_profile_image():
    """
    Endpoint to get user profile image from internal storage
    """
    try:
        token = check_for(request.args.get("token"),TOKEN_REGEX)
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo_user = UserRepository(conn)
        user = validate_token(repo_user,token)
        if user is None:
            return ("",401)
    try:
        img = get_profile_image_file(UPLOAD_FOLDER,user.id)
        return send_file(img[0], mimetype=img[1])
    except:
        img = get_profile_image_file("Img/","default_profile_picture")
        return send_file(img[0], mimetype=img[1])
    

@app_user.route('/user/received', methods=['POST'])
def user_received():
    """
    Method to add row to db to keep track of received messages
    """
    form = request.json
    try:
        token = check_for(form["token"],TOKEN_REGEX)
        message_id = check_for_number(form["message_id"])
    except Exception as e:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        msg_repo = MessageRepository(conn)
        user = validate_token(repo,token)
        msg = msg_repo.find_by_id(message_id)
        if user is None or msg is None:
            return ("",401)
        msg_received_repo = MessageRecivedRepository(conn)
        received = MessageReceived()
        received.user_id = user.id
        received.message_id = msg.id
        msg_received_repo.insert(received)
        return ("",200)
        

@app_user.route('/user/stats', methods=['GET'])
def user_stats():
    """
    Method to get user statistic about how many messages sent and received
    """
    try:
        token = check_for(request.args.get("token"),TOKEN_REGEX)
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo_user = UserRepository(conn)
        user = validate_token(repo_user,token)
        if user is None:
            return ("",401)
        msg_repo = MessageRepository(conn)
        value1 = msg_repo.get_count_of_messages(user.id)
        msg_received_repo = MessageRecivedRepository(conn)
        value2 = msg_received_repo.get_recived_count_by_user_id(user.id)
        json = {
            "sended": value1,
            "received": value2
        }
        return make_response(jsonify(json),200)
    
@app_user.route('/user/change_username', methods=['POST'])
def change_username():
    """
    Endpoint to change username
    """
    form = request.json
    try:
        token = check_for(form["token"],TOKEN_REGEX)
        new_username = check_for(form["username"],USERNAME_REGEX)
    except Exception as e:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        user = validate_token(repo,token)
        if user is None:
            return ("",401)   
        user.username = new_username
        try:
            repo.update(user)
        except psycopg2.errors.UniqueViolation:
            conn.rollback()
            return ("",409)
        return ("",200)

@app_user.route('/user/change_password', methods=['POST'])
def change_password():
    """
    Method to change password
    It will generate new token
    """
    form = request.json
    try:
        token = check_for(form["token"],TOKEN_REGEX)
        new_password = check_for(form["password"],PASSWORD_REGEX)
    except Exception as e:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        user = validate_token(repo,token)
        if user is None:
            return ("",401) 
        user.passwd = hash_password(new_password)
        token = generate_token()
        token_exp = datetime.datetime.now() + datetime.timedelta(days=1)
        user.token = token
        user.token_exp_date = token_exp
        user.token_exp_time = token_exp
        repo.update(user)
        json = {
            "token": user.token
        }
        return make_response(jsonify(json),200)
        

if __name__ == "__main__":
    pass



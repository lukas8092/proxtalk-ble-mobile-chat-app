from Context import Context
from flask import Blueprint, request, make_response, jsonify, send_file
import sys
import datetime
import os
sys.path.append("./")
sys.path.append("./Entities")
from Entities.Message import Message
from Entities.MessageReaction import MessageReaction
from Entities.MessageComment import MessageComment
sys.path.append("./Db")
from Db.UserRepository import UserRepository
from Db.MessageRepository import MessageRepository
from Db.MessageReactionRepository import MessageReactionRepository
from Db.MessageCommentRepository import MessageCommentRepository
from Db.DbConnection import Conn
sys.path.append("./BluePrints")
from Auth import validate_token
from DataCheck import check_for,check_for_number,TOKEN_REGEX,CONTENT_REGEX
from ImageHandler import save_image,get_image_file, get_profile_image_file


app_message = Blueprint('app_message', __name__)
context = Context()

if os.name == "nt":
    UPLOAD_FOLDER = 'D:\images'
    UPLOAD_FOLDER_PROFILES = 'D:\profile'
else:
    UPLOAD_FOLDER = "/home/ubuntu/images"
    UPLOAD_FOLDER_PROFILES = "/home/ubuntu/profile"
    # UPLOAD_FOLDER = os.getenv("IMAGES_PATH")
    # UPLOAD_FOLDER_PROFILES = os.getenv("PROFILE_IMAGES_PATH")


@app_message.route('/message/create', methods=['POST'])
def create():
    """
    Endpoint to create message in db
    """
    form = request.json
    try:
        token = check_for(form["token"],TOKEN_REGEX)
        content = check_for(form["content"],CONTENT_REGEX)
        image = form["image"]
    except Exception as e:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        user = validate_token(repo,token)
        if user is None:
            return ("",401)
        msg = Message()
        msg.user_id = user.id
        msg.content = content
        msg.time_created = datetime.datetime.now()
        if image:
            msg.image_type = 1
        message_repo = MessageRepository(conn)
        msg_id = message_repo.insert(msg)
    json = {
        "id": msg_id
    }
    return make_response(jsonify(json),200)

@app_message.route('/message/postImage', methods=['POST'])
def post_image():
    """
    Method to upload message image to internal storage of server
    """
    try:
        token = check_for(request.headers["token"],TOKEN_REGEX)
        id_of_message = check_for_number(request.headers["id"])
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo_user = UserRepository(conn)
        user = validate_token(repo_user,token)
        if user is None:
            return ("",401)
        repo = MessageRepository(conn)
        message = repo.find_by_id(int(id_of_message))
        if message is None:
            return ("",401)
        try:
            file = request.files['file']
            ext = save_image(file,id_of_message,UPLOAD_FOLDER)
            if ext is not None:
                message.image_type = ext
                repo.update(message)
                return ("",200)
            else:
                return ("",406) 
        except:
            return ("",500)

@app_message.route('/message/image',methods=['GET'])
def get_image():
    """
    Endpoint to get user message image from internal storage
    """
    try:
        token = check_for(request.args.get("token"),TOKEN_REGEX)
        id_of_message = check_for_number(request.args.get("id"))
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo_user = UserRepository(conn)
        user = validate_token(repo_user,token)
        if user is None:
            return ("",401)
        repo = MessageRepository(conn)
        message = repo.find_by_id(int(id_of_message))
        if message is None or message.image_type is None:
            return ("",402)
    try:
        img = get_image_file(UPLOAD_FOLDER,id_of_message,message.image_type)
        return send_file(img[0], mimetype=img[1])
    except Exception as e:
        return ("",500)


@app_message.route('/message/profile_image', methods=['GET'])
def get_profile_image():
    """
    Endpoint to get user message image from internal storage
    based in message author
    """
    try:
        token = check_for(request.args.get("token"),TOKEN_REGEX)
        id_of_message = check_for_number(request.args.get("id"))
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo_user = UserRepository(conn)
        user = validate_token(repo_user,token)
        if user is None:
            return ("",401)
        repo = MessageRepository(conn)
        message = repo.find_by_id(int(id_of_message))
        if message is None:
            return ("",401)
        message_user = repo_user.find_by_id(message.user_id)
    if(message_user is None):
        return ("",401)
    try:
        img = get_profile_image_file(UPLOAD_FOLDER_PROFILES,message_user.id)
        return send_file(img[0], mimetype=img[1])
    except:
        img = get_profile_image_file("Img/","default_profile_picture")
        return send_file(img[0], mimetype=img[1])

@app_message.route('/message/reaction', methods=['POST'])
def reaction():
    """
    Endpoint to add/remove reaction from message
    First call wil add record reaction to db
    Second call will remove reaction from db
    And it will every time sent message to websocket to update clients with actual state
    """
    form = request.json
    try:
        token = check_for(form["token"],TOKEN_REGEX)
        message_id = check_for_number(form["message_id"])
        reaction_type = check_for_number(form["reaction_type"])
    except Exception as e:
        print(e)
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo = UserRepository(conn)
        user = validate_token(repo,token)
        msg_repo = MessageRepository(conn)
        msg = msg_repo.find_by_id(int(message_id))
        if user is None or msg is None:
            return ("",401)
        msg_reaction_repo = MessageReactionRepository(conn)
        existing_reaction = msg_reaction_repo.find_by_message_user_id(msg.id,user.id)
        if existing_reaction is None:
            reaction = MessageReaction()
            reaction.user_id = user.id
            reaction.message_id = msg.id
            reaction.type = int(reaction_type)
            reaction.time = datetime.datetime.now()
            msg_reaction_repo.insert(reaction)
        else:
            msg_reaction_repo.delete(existing_reaction)
    return_data = {
        "state": existing_reaction is None
    }
    return make_response(jsonify(return_data),200)

@app_message.route('/message/history', methods=['GET'])
def get_history():
    """
    Endpoint to get array of message history of user
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
        messages = msg_repo.find_by_user_id(user.id)
        msg_reactions_repo = MessageReactionRepository(conn)
        data = []
        for msg in messages:
            count = msg_reactions_repo.get_reaction_count_by_message_id(msg.id)
            if count == {}:
                count = {1:0}
            item = {
                "id": msg.id,
                "content": msg.content,
                "reactions": count,
                "image": msg.image_type
            }
            data.append(item)          
        return make_response(jsonify(data),200)

@app_message.route('/message/random_message', methods=['GET'])
def get_random_message():
    """
    Endpoint to get random message
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
        r_msg = msg_repo.get_random_message()
        msg_username = repo_user.find_by_id(r_msg.user_id).username
        data = {
            "id": r_msg.id,
            "content" : r_msg.content,
            "username": msg_username,
            "image": r_msg.image_type
        }
    return make_response(jsonify(data),200)


@app_message.route('/message/comment', methods=['POST'])
def create_message_comment():
    try:
        token = check_for(request.json["token"],TOKEN_REGEX)
        id_of_message = check_for_number(request.json["message_id"])
        text = check_for(request.json["comment"],CONTENT_REGEX)
    except:
        return ("",400)
    with Conn(context.connection_pool) as conn:
        repo_user = UserRepository(conn)
        user = validate_token(repo_user,token)
        if user is None:
            return ("",401)
        repo = MessageRepository(conn)
        message = repo.find_by_id(int(id_of_message))
        if message is None:
            return ("",401)
        comment_repo = MessageCommentRepository(conn)
        comment = MessageComment()
        comment.user_id = user.id
        comment.message_id = message.id
        comment.comment = text
        comment_repo.insert(comment)
        return ("",200)


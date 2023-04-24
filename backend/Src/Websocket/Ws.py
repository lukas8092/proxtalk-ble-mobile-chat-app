from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
import asyncio
import websockets
import json
import sys
import ssl
import os
import time
sys.path.append("./Db")
from Db.UserRepository import UserRepository
from Db.MessageRepository import MessageRepository
from Db.MessageReactionRepository import MessageReactionRepository
from Db.MessageCommentRepository import MessageCommentRepository
from Db.Selects import select_table_columns
from Db.DbConnection import ConnectionPool, Conn
from Entities.Message import Message
from Entities.User import User
from Entities.MessageReaction import MessageReaction
from Entities.MessageComment import MessageComment
from MessageRoom import MessageRoom
sys.path.append("./")
sys.path.append("./Websocket")
sys.path.append("./Entities")

try:
    db_name = os.getenv("DB_NAME")
    db_username = os.getenv("DB_USERNAME")
    db_passwd = os.getenv("DB_PASSWORD")
    db_host = os.getenv("DB_HOST")
    db_port = os.getenv("DB_POST")
    ws_connection_count = int(os.getenv("WEBSOCKET_CONNECTION_COUNT"))
except:
    raise Exception("Missing .env config")
connection_pool = ConnectionPool(
    ws_connection_count, db_name, db_username, db_passwd, db_host, db_port)
with Conn(connection_pool) as conn:
    MessageReaction.attrs = select_table_columns(conn, "message_reaction")
    MessageReaction.table_name = "public.message_reaction"
    User.attrs = select_table_columns(conn, "user")
    User.table_name = "public.user"
    Message.attrs = select_table_columns(conn, "message")
    Message.table_name = "public.message"
    MessageComment.attrs = select_table_columns(conn,"message_comment") 
    MessageComment.table_name = "public.message_comment"

class Ws():
    """
    Websocket class
    """

    def __init__(self) -> None:
        self.rooms = {}
        self.messages_clients = set()

    async def message_subscription(self, data, ws):
        """
        Method where user will be subsribed to message room
        If room does not existed it will be created
        If existed user will join the room
        It will send actual data
        """
        if data["subscribe_to_message"] != None:
            id = data["subscribe_to_message"]["id"]
            try:
                room: MessageRoom = self.rooms[id]
                room.join(ws)
                print(f"Subscribed to {id} room")
                return await ws.send(room.output_data(room.output_reactions()))
            except Exception as e:
                print(e)
                print(f"New {id} room created and subsribed")
                self.rooms[id] = MessageRoom(id)
                self.rooms[id].join(ws)
                with Conn(connection_pool) as conn:
                    self.rooms[id].reactions = self.get_reactions_count(id, conn)
                    await self.rooms[id].send_to_room(self.rooms[id].output_reactions())
                    await ws.send(self.return_response(200))
                return
        else:
            return await ws.send(self.return_response(404))

    async def messsages_insert_subscription(self, ws, data):
        """
        Method to subscribe user to new inserted messages
        """
        self.messages_clients.add(ws)
        await ws.send(self.return_response(200))
        print("SUbscribed to messages insert")

    async def disconected(self, ws):
        """
        Method when user end connection, it remove client from room
        If room is empty it will be removed
        It will remove user from all subscribtions
        """
        temp = self.rooms.copy()
        for room in temp:
            if ws in self.rooms[room].clients:
                self.rooms[room].left(ws)
                if len(self.rooms[room].clients) == 0:
                    try:
                        del self.rooms[room]
                    except:
                        pass
                    print(f"Room deleted {len(self.rooms)}")
        try:
            self.messages_clients.remove(ws)
        except:
            pass

    def return_response(self, code: int):
        data = {
            "status": code
        }
        return json.dumps(data)

    async def response(self, websocket, path):
        """
        Main method that handles incoming clients
        Contains loop that waits to client messages and processes it
        """
        print("New Connection")
        try:
            async for message in websocket:
                print(f"Recv:{message}")
                try:
                    json_data = json.loads(message)
                except:
                    return await websocket.send(self.return_response(404))
                if "subscribe_to_message" in json_data:
                    await self.message_subscription(json_data, websocket)
                elif "subscribe_to_messages" in json_data:
                    await self.messsages_insert_subscription(websocket, json_data)
                else:
                    return await websocket.send(self.return_response(404))
        except websockets.exceptions.ConnectionClosed:
            await self.disconected(websocket)
            print("Disconected")
        except Exception as e:
            print("Err")
            print(e)
        finally:
            pass

    async def lisen_to_commands(self):
        """
        Method where is lisning to notification from database that are triggers by triggers
        And then it will notify users based on notification channel
        """
        with Conn(connection_pool) as conn:
            cursor = conn.cursor()
            conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
            cursor.execute("LISTEN new_message;")
            cursor.execute("LISTEN update_reactions;")
            cursor.execute("LISTEN new_comment;")
            print("Lisening")
            while True:
                conn.poll()
                await asyncio.sleep(0.1)
                while conn.notifies:
                    notify = conn.notifies.pop(0)
                    print(f"Got NOTIFY: {notify.channel} - {notify.payload}")
                    if notify.channel == "new_message":
                        if len(self.messages_clients) != 0:
                            await self.handle_incoming_message_insert(notify.payload, conn)
                    if notify.channel == "update_reactions":
                        if len(self.rooms) != 0:
                            await self.handle_incoming_reactions_update(notify.payload, conn)
                    if notify.channel == "new_comment":
                        if len(self.rooms) != 0:
                            await self.handle_incoming_comment(notify.payload, conn)
                    

    async def handle_incoming_message_insert(self, id, conn):
        """
        Method to handle incoming notification
        It will fetch data about message from db from given id from notification
        and send it to all subscribed clients
        """
        user_repo = UserRepository(conn)
        msg_repo = MessageRepository(conn)
        message = msg_repo.find_by_id(id)
        user = user_repo.find_by_id(message.user_id)
        img = None
        if message.image_type != None:
            img = int(message.image_type)
        data = {
            "id": message.id,
            "username": user.username,
            "content": message.content,
            "image": img
        }
        print(json.dumps(data))
        await self.send_to_all_messages_clients(json.dumps(data))

    async def send_to_all_messages_clients(self, data):
        """
        Method to send message to all clients
        """
        for c in self.messages_clients:
            try:
                await c.send(data)
            except:
                pass

    async def handle_incoming_reactions_update(self, id, conn):
        """
        Method to handle incoming notification
        It will fetch notification count on given message from db from given id from notification
        and send it to all subscribed clients
        """
        id = int(id)
        print(f"Reaction: {id}")
        if id in self.rooms:
            room = self.rooms[id]
            data = self.get_reactions_count(id, conn)
            room.reactions = data
            await room.send_to_room(room.output_reactions())

    def get_reactions_count(self, id, conn):
        msg_reaction_repo = MessageReactionRepository(conn)
        reactions_count = msg_reaction_repo.get_reaction_count_by_message_id(
            id)
        if (reactions_count == {}):
            reactions_count = {
                1: 0
            }
        return reactions_count
    
    async def handle_incoming_comment(self,id,conn):
        print(id)
        try:
            comment_repo = MessageCommentRepository(conn)
            comment = comment_repo.find_by_id(id)
            user_repo = UserRepository(conn)
            user = user_repo.find_by_id(comment.user_id)
            msg_id = comment.message_id
            print(msg_id)
            await self.rooms[msg_id].send_to_room(self.rooms[msg_id].output_comment(user.username,comment.comment))
        except Exception as e:
            print(e)
            pass

    def between_callback(self):
        """
        Method to start websocket cycle
        And set SSL keys
        """
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        ssl_cert = "/etc/letsencrypt/live/proxtalk.live/fullchain.pem"
        ssl_key = "/etc/letsencrypt/live/proxtalk.live/privkey.pem"
        ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        ssl_context.load_cert_chain(ssl_cert, keyfile=ssl_key)
        ws_server = websockets.serve(self.response, os.getenv(
            "ADDRESS_WS"), int(os.getenv("PORT_WS")),ssl=ssl_context)
        loop.run_until_complete(ws_server)
        loop.run_forever()
        loop.close()

    def connection(self):
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        loop.run_until_complete(self.lisen_to_commands())
        loop.close()

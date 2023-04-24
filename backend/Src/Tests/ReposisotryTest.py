import unittest
import sys
import os
from dotenv import load_dotenv
load_dotenv("../Config/.env")
sys.path.append("./")
sys.path.append("./Db")
sys.path.append("./Entities")
from Db.DbConnection import ConnectionPool,Conn
from Db.Selects import select_table_columns
from Entities.Message import Message
from Entities.User import User
from Entities.MessageReaction import MessageReaction
from Entities.MessageReceived import MessageReceived
from Entities.MessageComment import MessageComment
from Db.UserRepository import UserRepository
from Db.MessageRepository import MessageRepository
from Db.MessageReactionRepository import MessageReactionRepository
from Db.MessageReceivedRepository import MessageRecivedRepository
from Db.MessageCommentRepository import MessageCommentRepository
try:
    db_name = os.getenv("DB_NAME")
    db_username = os.getenv("DB_USERNAME")
    db_passwd = os.getenv("DB_PASSWORD")
    db_host = os.getenv("DB_HOST")
    db_port = os.getenv("DB_POST")
except:
    raise Exception("Missing .env config")
connection_pool = ConnectionPool(
    1, db_name, db_username, db_passwd, db_host, db_port)
with Conn(connection_pool) as conn:
    User.attrs = select_table_columns(conn,"user") 
    User.table_name = "public.user"
    Message.attrs = select_table_columns(conn,"message") 
    Message.table_name = "public.message"
    MessageReaction.attrs = select_table_columns(conn,"message_reaction")
    MessageReaction.table_name = "public.message_reaction"
    MessageReceived.attrs = select_table_columns(conn,"message_received") 
    MessageReceived.table_name = "public.message_received"
    MessageComment.attrs = select_table_columns(conn,"message_comment") 
    MessageComment.table_name = "public.message_comment"

USER_ID = 78

class RepositoryTest(unittest.TestCase):
    def _repository_test(self,repo,entity_type,entity,entity_changed):
        # Insert and find by id
        id_of_row = repo.insert(entity)
        row = repo.find_by_id(id_of_row)
        self.assertEqual(type(row),entity_type)
        entity.id = id_of_row
        
        self.assertEqual(self.compare_objects(row,entity),True)
        # Update and find_all
        entity_changed.id = id_of_row
        repo.update(entity_changed)
        new_row = None
        for r in repo.find_all():
            if r.id == id_of_row:
                new_row = r
        self.assertEqual(type(new_row),entity_type)
        self.assertEqual(self.compare_objects(new_row,entity_changed),True)
        # Delete and find by id
        repo.delete(new_row)
        none_row = repo.find_by_id(new_row.id)
        self.assertEqual(none_row,None)
    
    def compare_objects(self,obj1,obj2):
        return vars(obj1) == vars(obj2)
    
    def test_user_repository(self):
        entity = User([None,"tester7","1234","lukas2@gmail.com","..","dwgf4w5r654",None,None])
        entity_changed = User([None,"tester2","1234975","lukas@gmail.com","..","dwgf4w5r65",None,None])
        with Conn(connection_pool) as conn:
            repo = UserRepository(conn)
            self._repository_test(repo,User,entity,entity_changed)
    
    def test_message_repository(self):
        entity = Message([None,USER_ID,"Hello World",None,1])
        entity_changed = Message([None,USER_ID,"Hello World!",None,2])
        with Conn(connection_pool) as conn:
            repo = MessageRepository(conn)
            self._repository_test(repo,Message,entity,entity_changed)
    
    def test_message_reaction_repository(self):
        entity = MessageReaction([None,1,USER_ID,None,None])
        entity_changed = MessageReaction([None,2,USER_ID,None,None])
        with Conn(connection_pool) as conn:
            repo = MessageReactionRepository(conn)
            self._repository_test(repo,MessageReaction,entity,entity_changed)

    def test_message_received_repository(self):
        entity = MessageReceived([None,USER_ID,None])
        entity_changed = MessageReceived([None,USER_ID,902])
        with Conn(connection_pool) as conn:
            repo = MessageRecivedRepository(conn)
            self._repository_test(repo,MessageReceived,entity,entity_changed)
    
    def test_message_comment_repository(self):
        entity = MessageComment([None,USER_ID,902,"AHoj"])
        entity_changed = MessageComment([None,USER_ID,902,"Hey"])
        with Conn(connection_pool) as conn:
            repo = MessageCommentRepository(conn)
            self._repository_test(repo,MessageComment,entity,entity_changed)


if __name__ == "__main__":
    unittest.main()
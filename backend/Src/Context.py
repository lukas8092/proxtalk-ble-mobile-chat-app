from Db.DbConnection import ConnectionPool, Conn
from Db.Selects import select_table_columns
from Entities.User import User
from Entities.Message import Message
from Entities.MessageReaction import MessageReaction
from Entities.MessageReceived import MessageReceived
from Entities.MessageComment import MessageComment
import os


class Context(object):
    """
    Class with database context
    It contains singleton with connection pool
    """
    def __new__(self):
        if not hasattr(self, 'instance'):
            self.instance = super(Context, self).__new__(self)
            self.init(self)
        return self.instance


    def init(self):
        """
        Method to initiate singleton object
        It will create database connection based on configuration
        And initiate database entities, fetch tables structres
        """
        try:
            db_name = os.getenv("DB_NAME")
            db_username = os.getenv("DB_USERNAME")
            db_passwd = os.getenv("DB_PASSWORD")
            db_host = os.getenv("DB_HOST")
            db_port = os.getenv("DB_POST")
            api_connection_count = int(os.getenv("API_CONNECTION_COUNT"))
        except:
            raise Exception("Missing .env config")
        self.connection_pool = ConnectionPool(api_connection_count,db_name,db_username,db_passwd,db_host,db_port) # init of connection pool
        with Conn(self.connection_pool) as conn:
            User.attrs = select_table_columns(conn,"user") # init of user attributes
            User.table_name = "public.user"
            Message.attrs = select_table_columns(conn,"message") # init of message attributes
            Message.table_name = "public.message"
            MessageReaction.attrs = select_table_columns(conn,"message_reaction") # init of message reactins attributes
            MessageReaction.table_name = "public.message_reaction"
            MessageReceived.attrs = select_table_columns(conn,"message_received") 
            MessageReceived.table_name = "public.message_received"
            MessageComment.attrs = select_table_columns(conn,"message_comment") 
            MessageComment.table_name = "public.message_comment"


if __name__ == "__main__":
    c = Context()
    c2 = Context()
    print(c2.connection_pool)

   
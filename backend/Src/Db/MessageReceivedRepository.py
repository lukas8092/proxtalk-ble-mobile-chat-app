import sys
from RepositoryBase import RepositoryBase
from Entities.MessageReceived import MessageReceived
sys.path.append("./")
from Context import Context
sys.path.append("./Db")


class MessageRecivedRepository(RepositoryBase):
    def __init__(self,conn) -> None:
        self.conn = conn
        super().__init__(MessageReceived.table_name,MessageReceived.attrs,MessageReceived, conn)
    

    def get_recived_count_by_user_id(self,user_id):
            cursor = self.conn.cursor()
            sql = """
            select count(public.message_received.id) from public.message_received
            where public.message_received.user_id = %s
            """
            cursor.execute(sql,(user_id,))
            data = cursor.fetchone()
            print(data)
            return data[0]
    
    
if __name__ == "__main__":
    c = Context()
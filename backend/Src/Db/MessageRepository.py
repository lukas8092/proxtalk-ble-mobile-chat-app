import sys
from RepositoryBase import RepositoryBase
from Entities.Message import Message
sys.path.append("./")
from Context import Context
sys.path.append("./Db")


class MessageRepository(RepositoryBase):
    def __init__(self,conn) -> None:
        self.conn = conn
        super().__init__(Message.table_name,Message.attrs,Message, conn)
    

    def get_count_of_messages(self,id: int):
        cursor = self.conn.cursor()
        sql = "select count(id) from public.message where user_id = %s"
        cursor.execute(sql,(id,))
        data = cursor.fetchone()
        return data[0]
    

    def find_by_user_id(self,id):
        cursor = self.conn.cursor()
        sql = "select * from public.message where user_id = %s"
        cursor.execute(sql,(id,))
        output = []
        data = cursor.fetchall()
        for row in data:
            output.append(Message(row))
        return output
        

    def get_random_message(self) -> Message:
        cursor = self.conn.cursor()
        sql = "select * from public.message offset random() * (select count(*) from public.message) limit 1;"
        cursor.execute(sql)
        data = cursor.fetchone()
        return Message(data)
    

if __name__ == "__main__":
    c = Context()
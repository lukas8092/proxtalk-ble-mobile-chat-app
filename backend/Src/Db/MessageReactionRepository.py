import sys
from RepositoryBase import RepositoryBase
from Entities.MessageReaction import MessageReaction
sys.path.append("./")
from Context import Context
sys.path.append("./Db")


class MessageReactionRepository(RepositoryBase):
    def __init__(self,conn) -> None:
        self.conn = conn
        super().__init__(MessageReaction.table_name,MessageReaction.attrs,MessageReaction, conn)
    

    def get_reaction_count_by_message_id(self,message_id):
            cursor = self.conn.cursor()
            sql = """
            select count(public.message_reaction.id), public.message_reaction.type from public.message_reaction
            where public.message_reaction.message_id = %s group by public.message_reaction.type
            """
            cursor.execute(sql,(message_id,))
            data = cursor.fetchall()
            output = {}
            for row in data:
                 output[int(row[1])] = row[0]
            return output


    def find_by_message_user_id(self,message_id,user_id):
            cursor = self.conn.cursor()
            sql = """
            select * from public.message_reaction
            where public.message_reaction.message_id = %s and public.message_reaction.user_id = %s
            """
            cursor.execute(sql,(message_id,user_id,))
            data = cursor.fetchone()
            if data is None:
                return None
            return MessageReaction(data)
    
    

    

if __name__ == "__main__":
    c = Context()
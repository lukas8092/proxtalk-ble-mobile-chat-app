import sys
from RepositoryBase import RepositoryBase
from Entities.MessageComment import MessageComment
sys.path.append("./")
from Context import Context
sys.path.append("./Db")


class MessageCommentRepository(RepositoryBase):
    def __init__(self,conn) -> None:
        self.conn = conn
        super().__init__(MessageComment.table_name,MessageComment.attrs,MessageComment, conn)
    

    

    

if __name__ == "__main__":
    c = Context()
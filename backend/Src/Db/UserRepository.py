import sys
from RepositoryBase import RepositoryBase
from Entities.User import User
sys.path.append("./")
from Context import Context
sys.path.append("./Db")


class UserRepository(RepositoryBase):
    def __init__(self,conn) -> None:
        self.conn = conn
        super().__init__(User.table_name,User.attrs,User, conn)
    

    def find_by_username(self,username: str):
        cursor = self.conn.cursor()
        sql = "select * from public.user where username = %s"
        cursor.execute(sql,(username,))
        data = cursor.fetchone()
        if data is None:
            return None
        return User(data)
    

    def find_by_token(self,token:str):
        cursor = self.conn.cursor()
        sql = "select * from public.user where token = %s"
        cursor.execute(sql,(token,))
        data = cursor.fetchone()
        if data is None:
            return None
        return User(data)


if __name__ == "__main__":
    c = Context()
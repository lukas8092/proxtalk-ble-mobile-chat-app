from Entities.EntityBase import EntityBase
import Db.DbConnection as DbConnection


class User(EntityBase):
        def __init__(self, values: list=None) -> None:
                self.attrs = None
                self.table_name = "user"
                self.username = None
                self.passwd = None
                self.email = None
                self.bt_mac = None
                super().__init__(User.attrs, values)


if __name__ == "__main__":
        pool = DbConnection.ConnectionPool()
                    
        
        
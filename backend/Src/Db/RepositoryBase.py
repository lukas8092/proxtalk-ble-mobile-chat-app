from Selects import select_all_data
import sys
from DbConnection import Conn
sys.path.append("./")
sys.path.append("./Entities/")
from Entities.User import User
from Context import Context


class RepositoryBase():
    """
    Class that will create all basic operations for given table
    """
    def __init__(self,table_name,attrs, table_object,conn) -> None:
        self.table_name = table_name
        self.attrs = attrs
        self.table_object = table_object
        self.conn = conn
        
    def find_all(self):
        cursor = self.conn
        arr = []
        rows = select_all_data(self.conn,self.table_name)
        for row in rows:
            arr.append(self.table_object(row))
        return arr
    
    def find_by_id(self,id):
        cursor = self.conn.cursor()
        data = select_all_data(self.conn,self.table_name,id=id)
        if data is None or data == []:
            return None
        return self.table_object(data)

    def insert(self,obj):
        cursor = self.conn.cursor()
        attrs = ""
        items = ""
        for i,x in enumerate(self.attrs,start=1):
            if x[0]!= "id":
                attrs += x[0]
                items += "%s"
            else:
                continue
            if i != len(self.attrs):
                attrs += ","
                items += ","
        sql = f"insert into {self.table_name}({attrs}) values({items}) RETURNING id"
        tuple_of_values = []
        for attr in self.attrs:
            if attr[0] != "id":
                tuple_of_values.append(getattr(obj,attr[0]))
        tuple_of_values = tuple(tuple_of_values)
        cursor.execute(sql,tuple_of_values)
        self.conn.commit()
        return cursor.fetchone()[0]
    
    def update(self,obj):
        cursor = self.conn.cursor()
        params = ""
        for i,x in enumerate(self.attrs):
            if x[0] != "id":
                params += f"{x[0]} = %s"
            else:
                continue
            if i != len(self.attrs)-1:
                params += ","
        sql = f"update {self.table_name} SET {params} where id = {obj.id}"
        tuple_of_values = []
        for attr in self.attrs:
            if attr[0] != "id":
                tuple_of_values.append(getattr(obj,attr[0]))
        tuple_of_values = tuple(tuple_of_values)
        cursor.execute(sql,tuple_of_values)
        self.conn.commit()


    def delete(self,obj):
        cursor = self.conn.cursor()
        sql = f"delete from {self.table_name} where id = {obj.id}"
        cursor.execute(sql)
        self.conn.commit()

if __name__ == "__main__":
    c = Context()
    with Conn(c.connection_pool) as conn:
        rep = RepositoryBase(User.table_name,User.attrs,User,conn)

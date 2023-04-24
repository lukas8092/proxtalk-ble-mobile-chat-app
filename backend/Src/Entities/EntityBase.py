from Db.Selects import select_table_columns
import Db.DbConnection as DbConnection
from typing import List

class EntityBase():
    """
    Class to define entity class for table
    It will automatically set all atributes as class variable
    """
    def __init__(self,attrs:List[str],values:list) -> None:
        self.id = None
        if values is None:
            values = [None] * len(attrs)
        self._init_attrs(attrs,values)
    
    def _init_attrs(self,attrs,values):
        for i,attr in enumerate(attrs):
            setattr(self,attr[0],values[i])

if __name__ == "__main__":
    pool = DbConnection.ConnectionPool()
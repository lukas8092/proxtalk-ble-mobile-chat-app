from typing import List
from Entities.EntityBase import EntityBase


class Message(EntityBase):
        def __init__(self, values: list=None) -> None:
                self.attrs = None # defining it only for intelli sense purpose
                self.table_name = "message"
                self.content = None
                self.user_id = None
                super().__init__(Message.attrs, values)


if __name__ == "__main__":
        pass
                    
        
        
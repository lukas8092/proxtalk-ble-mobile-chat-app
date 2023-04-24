from typing import List
from Entities.EntityBase import EntityBase


class MessageReceived(EntityBase):
        def __init__(self, values: list=None) -> None:
                self.attrs = None
                self.table_name = "message_received"
                self.user_id = None
                self.message_id = None
                super().__init__(MessageReceived.attrs, values)


if __name__ == "__main__":
        pass
                    
        
        
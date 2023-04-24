from typing import List
from Entities.EntityBase import EntityBase


class MessageComment(EntityBase):
        def __init__(self, values: list=None) -> None:
                self.attrs = None
                self.table_name = "message_comment"
                self.user_id = None
                self.message_id = None
                self.type = None
                super().__init__(MessageComment.attrs, values)


if __name__ == "__main__":
        pass
                    
        
        
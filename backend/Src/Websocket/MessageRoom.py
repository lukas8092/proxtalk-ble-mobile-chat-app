import json

class MessageRoom():
    def __init__(self,id) -> None:
        self.clients = set()
        self.id = id
        self.reactions = {}
    
    def join(self,client):
        self.clients.add(client)
    
    def left(self,client):
        try:
            self.clients.remove(client)
        except:
            pass
    
    def output_reactions(self):
        output = {
                    "method": "updated_reactions",
                    "id": self.id,
                    "data": self.reactions
                }
        return output
    
    def output_comment(self,username,comment):
        output = {
                    "method": "add_comment",
                    "id": self.id,
                    "data": {
                        "username": username,
                        "comment": comment
                    }
                }
        return output

    def output_data(self,data):
        return json.dumps(data)

    async def send_to_room(self,data):
        """
        Method to sent data to all clients in room
        """
        output = self.output_data(data)
        for c in self.clients:
            await c.send(output)
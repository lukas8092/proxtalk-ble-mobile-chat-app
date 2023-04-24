import sys
sys.path.append("./")
sys.path.append("./Websocket")
from dotenv import load_dotenv
load_dotenv("../Config/.env")
from Ws import Ws
import threading

if __name__ == "__main__":
    ws = Ws()
    server = threading.Thread(target=ws.between_callback, daemon=True)
    server.start()
    print("Websocket started")
    clientt = threading.Thread(target=ws.connection)
    clientt.start()
    clientt.join()
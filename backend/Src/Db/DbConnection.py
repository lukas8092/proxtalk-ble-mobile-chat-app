import psycopg2
from typing import List, Type
import time

class ConnectionPool():
    def __init__(self,number_of_connection,db_name,db_username,db_passwd,db_host,db_port) -> None:
        self.pool_size = number_of_connection
        self.available_connections: List[psycopg2.connection] = []
        self.used_connections: List[psycopg2.connection] = []
        self.database = db_name
        self.username = db_username
        self.passwd = db_passwd
        self.host = db_host
        self.port = db_port
        self._init_pool()
    
    def _init_pool(self):
        """
        Initializing connetion to Postgres db
        """
        print(f"Initializing connection pool(size={self.pool_size})")
        for i in range(self.pool_size):
            conn = psycopg2.connect(database=self.database,
                        host=self.host,
                        user=self.username,
                        password=self.passwd,
                        port=self.port)
            self.available_connections.append(conn)
        print("Initializing finished")
    
    def get_connection(self):
        """
        Method to get connetion from available connections array
        If there is no connection available it will wait in cycle
        """
        while len(self.available_connections) == 0:
            time.sleep(100)
        if len(self.available_connections) > 0:
            conn =  self.available_connections.pop()
            self.used_connections.append(conn)
            return conn
        else:
            raise Exception("empty connection pool when it should not be")
    
    def release_connetion(self, connection):
        """
        Method to get back connection from used connections to available connection
        """
        self.used_connections.remove(connection)
        self.available_connections.append(connection)
    
    def test_connection(self,conn):
        """
        Method to test if connection is still alive
        If its reurn isolation level, connetion is alive
        Otherwise it will raise exception
        """
        try:
            conn.isolation_level
        except psycopg2.OperationalError as oe:
            conn = psycopg2.connect(database=self.database,
                    host=self.host,
                    user=self.username,
                    password=self.passwd,
                    port=self.port)

class Conn():
    """
    Class to store connection, that was get from connection pool
    This class is made for with statment

    """
    def __init__(self,connection_pool: ConnectionPool) -> None:
        self.connection_pool: ConnectionPool = connection_pool
    
    def __enter__(self):
        """
        On with statment created, it will take connection from pool
        """
        self.connection = self.connection_pool.get_connection()
        return self.connection

    def __exit__(self, type, value, traceback):
        """
        On exited with statment, it will get back connection to pool
        """
        self.connection_pool.release_connetion(self.connection)
    

if __name__ == "__main__":
    pool = ConnectionPool()

    

    

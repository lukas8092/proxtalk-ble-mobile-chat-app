import sys
sys.path.append("./")
sys.path.append("./Db")
import DbConnection as DbConnection

schema = "public"

def select_table_columns(conn,table_name):
    """
    Method to fetch all table atrributes
    """
    cursor = conn.cursor()
    sql = f"""
    SELECT column_name, data_type
    FROM information_schema.columns
    WHERE table_schema = '{schema}'
    AND table_name = '{table_name}';
    """
    cursor.execute(sql)
    rows = cursor.fetchall()
    return rows


def select_all_data(conn,table_name,id=None):
    """
    Method to fetch all data from table
    If id is defined it will add id to select
    """
    cursor = conn.cursor()
    sql = f"select * from {table_name}"
    if id is not None:
        sql += f" where id = {id}"
    cursor.execute(sql)
    rows = cursor.fetchall()
    if len(rows) == 1:
        return rows[0]
    return rows




if __name__ == "__main__":
    p = DbConnection.ConnectionPool()
import bcrypt
import datetime
import string
import random

def hash_password(passwd: str):
    return bcrypt.hashpw(passwd.encode("utf8"), bcrypt.gensalt()).decode("utf-8")

def check_password_hash(password, hashed_password):
    return bcrypt.checkpw(password.encode("utf8"), hashed_password.encode("utf8"))

def validate_token(repo,token:str):
    user = repo.find_by_token(token)
    if user is None:
        return None
    date = datetime.datetime.combine(user.token_exp_date,user.token_exp_time)
    now = datetime.datetime.now()
    if now <= date:
        return user
    else:
        return None

def generate_token():
    """
    Method to generate random string of letters and digits
    """
    characters = string.ascii_letters + string.digits
    return ''.join(random.choice(characters) for i in range(30))
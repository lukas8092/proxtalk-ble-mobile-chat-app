import datetime
import re


PASSWORD_REGEX = r"^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&.,-])[A-Za-z\d@$!%*#?&.,-]{8,32}$"
EMAIL_REGEX = r"^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
TOKEN_REGEX = r"^[aA-zZ\d]{30}$"
USERNAME_REGEX = r"^[aA-zZ\d.\-_\]\]\[\{\}]{2,100}$"
CONTENT_REGEX = r"^.{1,250}$"


def check_for(input,regex=None) -> str:
    """
    Method to check input from api with given regex
    If its invalid it will raise a exception
    """
    if input != "" or input != None:
        if regex is None:
            return input
        r = re.match(regex,input)
        if r is None:
            raise Exception()
        return input
    else:
        raise Exception()


def check_for_number(input) -> int:
    """
    Method to check if given input is number
    Number as string or type int are valid
    """
    if type(input) is int:
        return input
    if type(input) is str:
        try:
            int(input)
            return input
        except:
            raise Exception()
    raise Exception()


def timestamp_to_datime(timestamp:int):
    return datetime.datetime.fromtimestamp(timestamp)

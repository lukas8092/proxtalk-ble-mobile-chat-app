import unittest
import requests
import mimetypes
from Tools import *

URL = f"{HOST}user/"

URL_REGISTER = f"{URL}register"
username = "test12345679"
password = "AHoj.1234"
login_username = "unittest"
login_password = "Ahoj.1234"
register_json = {
  "username": username,
  "passwd": password,
  "email": "user@gmail.com",
  "bt_mac": ".."
}
register_json_wrong = {
  "usernam": "Karel",
  "paswd": "1234",
  "email": "user@gmail.com",
  "bt_mac": "fd:45:sd"
}
URL_LOGIN = f"{URL}login"
URL_VALIDATE_TOKEN = f"{URL}validate_token"
URL_UPLOAD_IMAGE = f"{URL}upload_profile_image"
files = {'file': open('./Tests/image.png','rb')}
files_wrong = {'file': open('./Tests/text.txt','rb')}
header = {"token": TOKEN}
header_wrong = {"toen": TOKEN}
header_exp = {"token": "1111iGggp48PyW0umgA2JYQ33PdeZx"}
ULR_PROFILE_IMAGE = f"{URL}profile_image"
URL_RECEIVED = f"{URL}received"
message_id = 905
message_recv_json = {
    "token": TOKEN,
    "message_id": message_id
}
message_recv_json_exp = {
    "token": "1111iGggp48PyW0umgA2JYQ33PdeZx",
    "message_id": message_id
}
message_recv_json_bad_format = {
    "toke": "1111iGggp48PyW0umgA2JYQ33PdeZx",
    "mesage_id": message_id
}
message_recv_json_bad_id = {
    "token": TOKEN,
    "message_id": 0
}
URL_STATS = f"{URL}stats"
URL_CHANGE_USERNAME = f"{URL}change_username"
new_username = username+"299"
change_username_json = {
    "token": TOKEN,
    "username": new_username
}
change_username_json_same = {
    "token": TOKEN,
    "username": "test"
}
change_username_json_exp = {
    "token": EXP_TOKEN,
    "username": new_username
}
change_username_json_wrong = {
    "tokn": TOKEN,
    "usrname": new_username
}
URL_CHANGE_PASSWORD = f"{URL}change_password"
change_password_json = {
    "token": TOKEN,
    "password": "Ahoj.123456"
}
change_password_json_exp = {
    "token": EXP_TOKEN,
    "password": "Ahoj.123456"
}
change_password_json_wrong = {
    "token": TOKEN,
    "password": "Ahoj123456"
}


class UserTest(unittest.TestCase):
    def test_register(self): # User in register json must be changed to get valid test
        post(self,URL_REGISTER,200,register_json)
    
    def test_register_wrong(self):
        post(self,URL_REGISTER,400,register_json_wrong)

    def test_register_empty(self):
        post(self,URL_REGISTER,400,{})
    
    def test_register_exists(self):
        post(self,URL_REGISTER,409,register_json)
    
    def test_login(self):
        r = get_with_response_auth(self,URL_LOGIN,login_username,login_password,200)
        self.assertIn("token",r)
    
    def test_login_bad_credentials(self):
        get_with_response_auth(self,URL_LOGIN,"111","111",401)
    
    def test_login_user_bad_form(self):
        get(self,URL_LOGIN,400)
    
    def test_validate_token(self):
        get(self,f"{URL_VALIDATE_TOKEN}?token={TOKEN}",200)
    
    def test_validate_token_wrong(self):
        get(self,f"{URL_VALIDATE_TOKEN}?toke=ghj23",400)
    
    def test_validate_token_wrong(self):
        get(self,f"{URL_VALIDATE_TOKEN}?token=1111iGggp48PyW0umgA2JYQ33PdeZx",401)
    
    def test_upload_profile_image(self):
        r = requests.post(URL_UPLOAD_IMAGE,files=files,headers=header)
        self.assertEqual(r.status_code,200)
    
    def test_upload_profile_image_wrong_file(self):
        r = requests.post(URL_UPLOAD_IMAGE,files=files_wrong,headers=header)
        self.assertEqual(r.status_code,406)

    def test_upload_profile_image_wrong_header(self):
        r = requests.post(URL_UPLOAD_IMAGE,files=files,headers=header_wrong)
        self.assertEqual(r.status_code,400)
    
    def test_upload_profile_image_exp(self):
        r = requests.post(URL_UPLOAD_IMAGE,files=files,headers=header_exp)
        self.assertEqual(r.status_code,401)
    
    def test_get_profile_image(self):
        r = requests.get(f"{ULR_PROFILE_IMAGE}?token={TOKEN}")
        content_type = r.headers['content-type']
        self.assertIn("image/",content_type)
    
    def test_get_profile_image_wrong(self):
        r = requests.get(f"{ULR_PROFILE_IMAGE}?ton={TOKEN}")
        self.assertEqual(r.status_code,400)
    
    def test_get_profile_image_exp(self):
        r = requests.get(f"{ULR_PROFILE_IMAGE}?token=1111iGggp48PyW0umgA2JYQ33PdeZx")
        self.assertEqual(r.status_code,401)
    
    def test_receive(self):
        post(self,URL_RECEIVED,200,message_recv_json)
    
    def test_receive_bad_format(self):
        post(self,URL_RECEIVED,400,message_recv_json_bad_format)
    
    def test_receive_exp(self):
        post(self,URL_RECEIVED,401,message_recv_json_exp)
    
    def test_receive_bad_id(self):
        post(self,URL_RECEIVED,401,message_recv_json_bad_id)
    
    def test_stats(self):
        r = get(self,f"{URL_STATS}?token={TOKEN}",200)
        self.assertIn("sended",r)
        self.assertIn("received",r)
    
    def test_stats_exp(self):
        get(self,f"{URL_STATS}?token={EXP_TOKEN}",401)
    
    def test_stats_wrong(self):
        get(self,f"{URL_STATS}?tokn={TOKEN}",400)
    
    def test_change_username(self):
        post(self,URL_CHANGE_USERNAME,200,change_username_json)
    
    def test_change_username_same(self):
        post(self,URL_CHANGE_USERNAME,409,change_username_json_same)
    
    def test_change_username_wrong(self):
        post(self,URL_CHANGE_USERNAME,400,change_username_json_wrong)
    
    def test_change_username_exp(self):
        post(self,URL_CHANGE_USERNAME,401,change_username_json_exp)

    

    

if __name__ == "__main__":
    unittest.main()


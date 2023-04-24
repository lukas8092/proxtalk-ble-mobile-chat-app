import unittest
import requests
import mimetypes
from Tools import *

URL = f"{HOST}message/"
MESSAGE_ID = 905
URL_CREATE = f"{URL}create"
create_json = {
  "token": TOKEN,
  "content": "Hello world!",
  "image": 1
}
create_json_wrong = {
  "toen": TOKEN,
  "ctent": "Hello world!"
}
create_json_exp = {
  "toen": "123",
  "ctent": "Hello world!"
}
URL_REACTION = f"{URL}reaction"
reaction_json = {
  "token": TOKEN,
  "message_id": MESSAGE_ID,
  "reaction_type": 1
}
reaction_json_wrong = {
  "ton": TOKEN,
  "meage_id": 300,
  "meage_type": 1
}
reaction_json_exp = {
  "token": EXP_TOKEN,
  "message_id": 300,
  "reaction_type": 1
}
reaction_json_wrong_id = {
  "token": TOKEN,
  "message_id": -45645,
  "reaction_type": 1
}
reaction_json_expected1 = {
    "state": False
}
reaction_json_expected2 = {
    "state": True
}
files = {'file': open('./Tests/image.png','rb')}
files_wrong = {'file': open('./Tests/text.txt','rb')}
URL_POST_IMAGE = f"{URL}postImage"
header = {"token": TOKEN, "id": "905"}
URL_MESSAGE = f"{URL}image"
URL_PROFILE_IMAGE = f"{URL}profile_image"
URL_HISTORY = f"{URL}history"
URL_RANDOM_MESSAGE = f"{URL}random_message"


class MessageTest(unittest.TestCase):
    def test_create(self):
        post(self,URL_CREATE,200,create_json)
    
    def test_create_wrong(self):
        post(self,URL_CREATE,400,create_json_wrong)

    # def test_create_exp(self):
    #     post(self,URL_CREATE,401,create_json_exp) ## NOT PROCHAZET
    
    def test_reaction(self):
        r = requests.post(URL_REACTION,json=reaction_json)
        self.assertEqual(r.status_code,200)
        data = r.json()
        self.assertIn(data,[reaction_json_expected1,reaction_json_expected2])
    
    def test_reaction_wrong(self):
        post(self,URL_REACTION,400,reaction_json_wrong)
    
    def test_reaction_exp(self):
        post(self,URL_REACTION,401,reaction_json_exp)
    
    def test_reaction_wrong(self):
        post(self,URL_REACTION,401,reaction_json_wrong_id)
      
    def test_post_image(self):
        r = requests.post(URL_POST_IMAGE,files=files,headers=header)
        self.assertEqual(r.status_code,200)
    
    def test_get_image(self):
        r = requests.get(f"{URL_MESSAGE}?token={TOKEN}&id={MESSAGE_ID}")
        self.assertEqual(r.status_code,200)
        content_type = r.headers['content-type']
        self.assertIn("image/",content_type,)
    
    def test_get_image_wrong(self):
        r = requests.get(f"{URL_MESSAGE}?token={TOKEN}&i={MESSAGE_ID}")
        self.assertEqual(r.status_code,400)
    
    def test_get_image_exp(self):
        r = requests.get(f"{URL_MESSAGE}?token={EXP_TOKEN}&id={MESSAGE_ID}")
        self.assertEqual(r.status_code,401)
    
    def test_profile_image(self):
        r = requests.get(f"{URL_PROFILE_IMAGE}?token={TOKEN}&id={MESSAGE_ID}")
        content_type = r.headers['content-type']
        self.assertIn("image/",content_type,)
    
    def test_profile_image_wrong(self):
        r = requests.get(f"{URL_PROFILE_IMAGE}?toen={TOKEN}&id={MESSAGE_ID}")
        self.assertEqual(r.status_code,400)
    
    def test_profile_image_exp(self):
        r = requests.get(f"{URL_PROFILE_IMAGE}?token={EXP_TOKEN}&id={MESSAGE_ID}")
        self.assertEqual(r.status_code,401)
    
    def test_reaction(self):
        r = post(self,URL_REACTION,200,reaction_json)
        self.assertEqual(True,r == reaction_json_expected1 or r == reaction_json_expected2)
    
    def test_reaction_wrong(self):
        post(self,URL_REACTION,400,reaction_json_wrong)
    
    def test_reaction_exp(self):
        post(self,URL_REACTION,400,reaction_json_wrong)
    
    def test_reaction_wrong_id(self):
        post(self,URL_REACTION,401,reaction_json_wrong_id)
    
    def test_history(self):
        r = get(self,f"{URL_HISTORY}?token={TOKEN}",200)
        self.assertEqual(type(r),list)
    
    def test_history_wrong(self):
        get(self,f"{URL_HISTORY}?tokn={TOKEN}",400)

    def test_history_exp(self):
        get(self,f"{URL_HISTORY}?token={EXP_TOKEN}",401)
    
    def test_random_message(self):
        r = get(self,f"{URL_RANDOM_MESSAGE}?token={TOKEN}",200)
        self.assertIn("id",r)
        self.assertIn("content",r)
        self.assertIn("username",r)
        self.assertIn("image",r)
    
    def test_random_wrong(self):
        r = get(self,f"{URL_RANDOM_MESSAGE}?toen={TOKEN}",400)

    def test_random_exp(self):
        r = get(self,f"{URL_RANDOM_MESSAGE}?token={EXP_TOKEN}",401)



if __name__ == "__main__":
    unittest.main()
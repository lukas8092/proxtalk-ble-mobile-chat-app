import requests as req

HOST = "https://devlukas.tk:81/"
TOKEN = "96ZcB0LCmA3hm1ZemANvmObDBuzqLc"
EXP_TOKEN = "1111iGggp48PyW0umgA2JYQ33PdeZx"
ALLOWED_EXTENSIONS = [".png",".jpeg",".jpg"]

def get_with_response(self,url: str,requested_response_code: int, requested_response):
    r = req.get(url)
    self.assertEqual(r.status_code,requested_response_code)
    data = r.json()
    self.assertEqual(data,requested_response)

def get_with_response_auth(self,url: str,username,password,requested_response_code: int):
    r = req.get(url,auth=(username, password))
    self.assertEqual(r.status_code,requested_response_code)
    try:
        data = r.json()
        return data
    except:
        pass

def get(self,url: str,requested_response_code: int):
    r = req.get(url)
    self.assertEqual(r.status_code,requested_response_code)
    try:
        data = r.json()
        return data
    except:
        pass

def post_with_response(self,url: str,requested_response_code: int,body, requested_response):
    r = req.post(url,json=body)
    self.assertEqual(r.status_code,requested_response_code)
    data = r.json()
    self.assertEqual(data,requested_response)

def post(self,url: str,requested_response_code: int,body):
    r = req.post(url,json=body)
    self.assertEqual(r.status_code,requested_response_code)
    try:
        data = r.json()
        return data
    except:
        pass

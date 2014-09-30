import hmac
from hashlib import sha1
from sys import argv
from time import time
from datetime import date


storeurl = flow.getVariable("Url")
expirytime = flow.getVariable("expirytime")

containerName = flow.getVariable("containerName")

date_now = str(date.today().isoformat())


url = storeurl + '/' + containerName + '/' + 'new_location/date/' + date_now + '.xml'

flow.setVariable("redirect_url",url)
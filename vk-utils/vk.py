import urllib.request
import urllib.parse
import contextlib
import json
import codecs
import time
import logging

_MIN_DELAY = 0.36
_SLEEP_TIME = 3.0
_LOGGER = logging.getLogger('vk')

class Error(BaseException):
    def __init__(self, obj):
        super().__init__('[{}] {}'.format(obj['error_code'], obj['error_msg']))
        self.code = obj['error_code']
        self.msg = obj['error_msg']
        self.obj = obj

class Session:
    def __init__(self, defparams=None):
        self.defparams = defparams or {}
        self.last_req_time = -_MIN_DELAY

    def _update_last_req_time(self):
        now = time.monotonic()
        delay = now - self.last_req_time
        if delay < _MIN_DELAY:
            time.sleep(_MIN_DELAY - delay)
        self.last_req_time = now

    def request(self, _method, _raw=False, **kwargs):
        self._update_last_req_time()
        params = {}
        params.update(self.defparams)
        params.update(kwargs)
        url = 'https://api.vk.com/method/{}?{}'.format(_method, urllib.parse.urlencode(params))
        while True:
            with contextlib.closing(urllib.request.urlopen(url)) as resp:
                obj = json.load(codecs.getreader('utf-8')(resp))
                if _raw:
                    return obj
                if 'error' in obj:
                    if obj['error']['error_code'] == 6:
                        _LOGGER.warning('we are being too fast, sleeping for %.1fs', _SLEEP_TIME)
                        time.sleep(_SLEEP_TIME)
                        continue
                    else:
                        raise Error(obj['error'])
                return obj['response']

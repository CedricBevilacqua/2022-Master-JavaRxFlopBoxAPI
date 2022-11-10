from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer

authorizer = DummyAuthorizer()
authorizer.add_user("byvoid", "pass", "path", perm="elradfmw")

handler = FTPHandler
handler.authorizer = authorizer

server = FTPServer(("localhost", 21), handler)
server.serve_forever()
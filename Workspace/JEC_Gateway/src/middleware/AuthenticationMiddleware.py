import jwt
from fastapi import HTTPException
from starlette.requests import Request
from starlette.responses import JSONResponse

from constant.AuthenticationConfig import AuthenticationConfig


class AuthenticationMiddleware:
    @staticmethod
    async def authenticate(request: Request, call_next):
        if request.url.path.startswith("/"):
            authorization = request.headers.get("Authorization")
            if not authorization:
                return JSONResponse(status_code=401, content={"detail": "Authorization header missing"})
            token = authorization.split(" ")[1]
            payload = AuthenticationMiddleware.verify_token(token)
            request.state.user = payload
        response = await call_next(request)
        return response

    @classmethod
    def verify_token(cls, token: str):
        try:
            payload = jwt.decode(token, AuthenticationConfig.read_secret_key(), algorithms=AuthenticationConfig.get_algorithms())
            return payload
        except jwt.ExpiredSignatureError:
            raise HTTPException(status_code=401, detail="Token expired")
        except jwt.PyJWTError:
            raise HTTPException(status_code=401, detail="Invalid token")
        except Exception as e:
            raise HTTPException(status_code=401, detail=f"Token verification failed: {e}")

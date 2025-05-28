FROM python:3.11-slim

WORKDIR /app

COPY . .

RUN pip install -e .

EXPOSE 5000

CMD ["python", "Server.py"]
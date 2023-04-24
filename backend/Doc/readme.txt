To run backend api and websocket on linux server(this guide works for ubuntu):
Required packages(more in requirements.txt file):
    -Flask
    -websocket
    -bcrypt
    -gunicorn
    psycopg2-binary
Configuration file:
-one configuration file is shared betwwen api and websocket script
    PORT_API=port where will flask server listen
    ADDRESS_API=address where will flask server listen
    PORT_WS=portwhere will websocket listen
    ADDRESS_WS=address where will websocket listen
    DB_NAME=name of database
    DB_USERNAME=database username
    DB_PASSWORD=database password
    DB_HOST=database hostname
    DB_PORT=database port
    API_CONNECTION_COUNT=number of connections in connection pool in flask server
    WEBSOCKET_CONNECTION_COUNT=number of connections in connection pool in websocket
    IMAGES_PATH=path to directory where images will be stored
    PROFILE_IMAGES_PATH=path to directory where profile images will be stored
Instalation:
1. Create python envirovment
    - $sudo apt install python3-venv
    - (in Src dir) $python3 -m venv venv
    - $source venv/bin/activate
2. Install all pip packages
    - $ pip install -r ./Doc/requirements.txt
3. Prepare config file in Config/.env
4. Prepare Nginx server
    - $ sudo apt install nginx
    - paste into /etc/nginx/sites-available/name this:
        server {
            listen 80;
            server_name your_domain www.your_domain;

            location / {
                include proxy_params;
                proxy_pass http://unix:/home/user/name/name.sock;
            }      
        }
    - $ sudo ln -s /etc/nginx/sites-available/name /etc/nginx/sites-enabled
    - check for syntax error: $ sudo nginx -t
    - $ sudo systemctl restart nginx
5. Create daemon
    - paste into /etc/systemd/system/api.service
        [Unit]
        Description=Flask wsgi API
        After=network.target
        [Service]
        Type=simple
        User=ubuntu
        WorkingDirectory=/home/ubuntu/source/Src
        Environment="PATH=/home/ubuntu/source/bin"
        ExecStart=sudo /home/ubuntu/source/Src/venv/bin/gunicorn --workers=3 --bind unix:name.sock wsgi:app
        [Install]
        WantedBy=multi-user.target
    - $ sudo systemctl start api
6. Create daemon for websocket
    - paste into /etc/systemd/system/websocket.service
        [Unit]
        Description=Websocket 
        After=network.target
        [Service]
        Type=simple
        User=ubuntu
        WorkingDirectory=/home/ubuntu/name/Src
        ExecStart=sudo python3 Websocket/main.py
        [Install]
        WantedBy=multi-user.target
    - $ sudo systemctl start websocket


For detail documentation of api visit my swagger
http://devlukas.tk:8080/dist/



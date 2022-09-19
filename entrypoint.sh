#!/bin/sh

openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /vsftpd.key -out /vsftpd.crt -subj "/C=FR/ST=Paris/L=Paris/O=TheUntitledCloud/OU=IT/CN=theuntitledcloud.com"

cat > /etc/pam_pgsql.conf << EOF
database = $DATABASE
table = $TABLE
host = $HOST
port = $PORT
user = $USERNAME
password = $PASSWORD
user_column = $USERNAME_COLUMN
pwd_column = $PWD_COLUMN
pw_type = $PW_TYPE
debug = 1
EOF

cat >> /etc/vsftpd/vsftpd.conf << EOF
pasv_address=$PASV_ADDRESS
EOF

echo "Starting vsftpd..."
vsftpd /etc/vsftpd/vsftpd.conf

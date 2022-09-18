#!/bin/sh

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

echo "Starting vsftpd..."
vsftpd /etc/vsftpd/vsftpd.conf

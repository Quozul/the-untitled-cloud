## Environment variables
```sh
DATABASE=<database name>
TABLE=<table name>
HOST=<database hostname or ip address>
PORT=<database port>
USERNAME=<username to login the database>
PASSWORD=<password to the database>
USERNAME_COLUMN=<column where the usernames are stored>
PWD_COLUMN=<column where the passwords are stored>
PW_TYPE=<password hash algorithm>
PASV_ADDRESS=<remote address of the ftp server>
```

### Example
```sh
DATABASE=postgres
TABLE=public.users
HOST=127.0.0.1 # Or "postgres" in the case of the "docker-compose.yml" file example
PORT=5432
USERNAME=postgres
PASSWORD=postgrespw
USERNAME_COLUMN=username
PWD_COLUMN=password
PW_TYPE=md5
PASV_ADDRESS=127.0.0.1
```

For further documentation, read the GitHub page of [pam-pgsql](https://github.com/pam-pgsql/pam-pgsql).

## Volumes
- `/data/<username>`: Where all files of the FTP users are stored
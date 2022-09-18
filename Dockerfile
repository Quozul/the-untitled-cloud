FROM ubuntu:latest

# Installing vsftpd
RUN apt update && apt install -y vsftpd libpam-pgsql

# Home directory
RUN mkdir -p /data
RUN chown -R ftp:ftp /data
RUN chmod -R ug=rwx,g+s,o= /data

# Other folders
RUN mkdir -p /var/run/vsftpd/empty

# Copy configuration files
COPY ./etc/ /etc/

# Entry point
COPY entrypoint.sh /usr/sbin/
RUN chmod +x /usr/sbin/entrypoint.sh

EXPOSE 20 21 1024-1048

VOLUME [ "/data" ]

CMD ["/usr/sbin/entrypoint.sh"]
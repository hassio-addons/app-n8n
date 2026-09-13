# The Home Assistant node in n8n talks to "http://<host>:<port>/api" with a
# long-lived access token. This server stands in for Home Assistant on the
# loopback interface: it forwards the API to Home Assistant by way of the
# Supervisor, which authenticates the app itself, so the credential this app
# creates in n8n needs no token of its own.
#
# Only processes inside this container can reach it, and those already hold
# the Supervisor token in their environment.
server {
    listen 127.0.0.1:{{ .port }};

    include /etc/nginx/includes/server_params.conf;
    include /etc/nginx/includes/proxy_params.conf;

    location /api/ {
        proxy_set_header Authorization "Bearer {{ env "SUPERVISOR_TOKEN" }}";
        proxy_set_header Host supervisor;

        proxy_pass http://supervisor/core/api/;
    }

    location / {
        return 404;
    }
}

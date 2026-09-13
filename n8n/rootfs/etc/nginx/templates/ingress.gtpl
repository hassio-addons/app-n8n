server {
    listen {{ .interface }}:{{ .port }} default_server;

    include /etc/nginx/includes/server_params.conf;
    include /etc/nginx/includes/proxy_params.conf;

    # n8n is told the Ingress path in N8N_PATH and builds every URL in the
    # editor from it, but keeps serving from its root and expects whatever is
    # in front of it to take the path off again. Ingress does exactly that
    # before a request gets here.
    location / {
        allow   172.30.32.2;
        deny    all;

        proxy_pass http://backend;
    }
}

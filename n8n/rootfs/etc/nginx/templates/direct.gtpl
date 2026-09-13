server {
    {{ if not .ssl }}
    listen {{ .port }} default_server;
    listen [::]:{{ .port }} default_server;
    {{ else }}
    listen {{ .port }} default_server ssl;
    listen [::]:{{ .port }} default_server ssl;
    http2 on;
    {{ end }}

    include /etc/nginx/includes/server_params.conf;
    include /etc/nginx/includes/proxy_params.conf;

    {{ if .ssl }}
    include /etc/nginx/includes/ssl_params.conf;

    ssl_certificate /ssl/{{ .certfile }};
    ssl_certificate_key /ssl/{{ .keyfile }};
    {{ end }}

    # The editor is built for the Ingress path, so that is where it lives on
    # this port as well, with the path taken off on the way to n8n as Ingress
    # would. Landing on the root sends a browser there.
    location = / {
        return 302 $scheme://$http_host{{ .entry }}/;
    }

    location {{ .entry }}/ {
        proxy_pass http://backend/;
    }

    # Everything else is served from the root, which is where the webhook,
    # form and OAuth callback URLs n8n hands out point to. n8n's own login
    # guards the editor and its API; webhooks are meant to be reached
    # without one.
    location / {
        proxy_pass http://backend;
    }
}

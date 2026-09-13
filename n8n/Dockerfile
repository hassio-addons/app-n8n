ARG BUILD_FROM=ghcr.io/hassio-addons/base:21.0.5
# hadolint ignore=DL3006
FROM ${BUILD_FROM}

# Copy n8n package.json
COPY package.json /opt/

# Set workdir
WORKDIR /opt

# Set shell
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# Setup base
#
# n8n is installed from npm with the Node.js that Alpine ships, rather than
# copied out of the upstream image. Its native modules (sqlite3 among them)
# are compiled here, against musl, so the build tools come and go within the
# same layer. npm stays: n8n uses it at runtime to install community nodes.
#
# The remaining packages mirror what upstream's image carries for the nodes
# that shell out: graphicsmagick for the Edit Image node, git and an SSH
# client for the Git node, and tzdata so GENERIC_TIMEZONE resolves.
RUN \
    apk add --no-cache --virtual .build-dependencies \
        build-base=0.5-r4 \
        linux-headers=7.0.0-r1 \
        python3=3.14.7-r1 \
    \
    && apk add --no-cache \
        git=2.54.0-r0 \
        graphicsmagick=1.3.47-r0 \
        nginx=1.30.4-r1 \
        nodejs=24.18.1-r0 \
        npm=11.12.1-r0 \
        openssh-client-default=10.3_p1-r1 \
        tzdata=2026c-r0 \
    \
    && npm install \
        --no-audit \
        --no-fund \
        --no-update-notifier \
        --omit=dev \
    \
    && npm cache clear --force \
    \
    && apk del --no-cache --purge .build-dependencies \
    && rm -fr \
        /etc/nginx \
        /root/.cache \
        /root/.npm \
        /tmp/*

# Without this n8n believes it is being developed on: it validates nothing
# about where a WebSocket came from, caches no editor files, and reports
# itself as a development build.
ENV NODE_ENV=production

# Copy root filesystem
COPY rootfs /

# Health check
HEALTHCHECK --start-period=10m \
    CMD curl --silent --fail http://127.0.0.1:45678/healthz || exit 1

# Build arguments
ARG BUILD_ARCH
ARG BUILD_DATE
ARG BUILD_DESCRIPTION
ARG BUILD_NAME
ARG BUILD_REF
ARG BUILD_REPOSITORY
ARG BUILD_VERSION

# Labels
LABEL \
    io.hass.name="${BUILD_NAME}" \
    io.hass.description="${BUILD_DESCRIPTION}" \
    io.hass.arch="${BUILD_ARCH}" \
    io.hass.type="addon" \
    io.hass.version=${BUILD_VERSION} \
    maintainer="Franck Nijhof <opensource@frenck.dev>" \
    org.opencontainers.image.title="${BUILD_NAME}" \
    org.opencontainers.image.description="${BUILD_DESCRIPTION}" \
    org.opencontainers.image.vendor="Home Assistant Community Apps" \
    org.opencontainers.image.authors="Franck Nijhof <opensource@frenck.dev>" \
    org.opencontainers.image.licenses="MIT" \
    org.opencontainers.image.url="https://frenck.dev/home-assistant-apps" \
    org.opencontainers.image.source="https://github.com/${BUILD_REPOSITORY}" \
    org.opencontainers.image.documentation="https://github.com/${BUILD_REPOSITORY}/blob/main/README.md" \
    org.opencontainers.image.created=${BUILD_DATE} \
    org.opencontainers.image.revision=${BUILD_REF} \
    org.opencontainers.image.version=${BUILD_VERSION}

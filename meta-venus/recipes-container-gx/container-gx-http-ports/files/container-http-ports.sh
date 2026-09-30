#!/bin/sh

# The web sites listen on ports 80 and 443; serve them on VENUS_HTTP_PORT and
# VENUS_HTTPS_PORT instead when the container is started with them. The
# services don't see the container's environment, because svscanboot clears
# it, so read it from init's. Only digits pass, as the value goes into the
# nginx config.
env_port() {
	tr '\0' '\n' < /proc/1/environ | sed -n "s/^$1=\([0-9]\{1,5\}\)$/\1/p"
}

http_port=$(env_port VENUS_HTTP_PORT)
https_port=$(env_port VENUS_HTTPS_PORT)

# usage: rewrite_site <site> <sed arguments>
rewrite_site() {
	site=$1
	shift
	link=/run/nginx/sites-enabled/$site
	[ -L "$link" ] || return 0
	# not in sites-enabled, where nginx would load a leftover as a site
	tmp=/run/nginx/$site.tmp
	sed "$@" "/etc/nginx/sites-available/$site" > "$tmp" && mv -f "$tmp" "$link"
}

if [ -n "$http_port" ] && [ "$http_port" != "80" ]; then
	for site in http.site http-explanation.site; do
		rewrite_site $site \
		    -e "s/listen 80 default_server;/listen $http_port default_server;/" \
		    -e "s/listen \[::\]:80 default_server;/listen [::]:$http_port default_server;/" \
		    -e 's|proxy_pass http://localhost/|proxy_pass http://127.0.0.1:$server_port/|'
	done
fi

if [ -n "$https_port" ] && [ "$https_port" != "443" ]; then
	rewrite_site https.site \
	    -e "s/listen 443 ssl;/listen $https_port ssl;/" \
	    -e "s/listen \[::\]:443 ssl;/listen [::]:$https_port ssl;/" \
	    -e 's|proxy_pass https://localhost/|proxy_pass https://127.0.0.1:$server_port/|'
fi

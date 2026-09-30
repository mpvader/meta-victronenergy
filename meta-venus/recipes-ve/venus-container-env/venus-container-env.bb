SUMMARY = "Serve the web sites on the ports set for the container"
DESCRIPTION = "Rewrites the nginx sites to listen on VENUS_HTTP_PORT and \
VENUS_HTTPS_PORT when the container is started with them."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "venus-container"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://container-http-ports.sh"

S = "${S_UNUSED}"

do_install() {
    # run by start-nginx.sh before it starts nginx
    install -d ${D}${sysconfdir}/venus/www.d
    install -m 0755 ${UNPACKDIR}/container-http-ports.sh ${D}${sysconfdir}/venus/www.d/
}

SUMMARY = "Empty /run and /var/volatile at boot without tmpfs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "container-gx"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://container-volatile.sh"

S = "${S_UNUSED}"

inherit update-rc.d

INITSCRIPT_NAME = "container-volatile.sh"
# Run before the other S03 scripts (mountall, overlays, test-data-partition)
# write to /run.
INITSCRIPT_PARAMS = "start 03 S ."

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/container-volatile.sh ${D}${sysconfdir}/init.d/
}

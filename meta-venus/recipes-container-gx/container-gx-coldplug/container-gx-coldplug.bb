SUMMARY = "Replay serial devices without writing to /sys"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "container-gx"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://container-coldplug.sh"

S = "${S_UNUSED}"

inherit update-rc.d

INITSCRIPT_NAME = "container-coldplug.sh"
# after udev (S04)
INITSCRIPT_PARAMS = "start 05 S ."

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/container-coldplug.sh ${D}${sysconfdir}/init.d/
}

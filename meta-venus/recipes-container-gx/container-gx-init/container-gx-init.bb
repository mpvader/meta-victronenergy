SUMMARY = "End the container at halt and reboot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "container-gx"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = " \
    file://init.container \
    file://container-stop.sh \
"

S = "${S_UNUSED}"

inherit update-alternatives update-rc.d

ALTERNATIVE:${PN} = "init"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_TARGET[init] = "${base_sbindir}/init.container"
# Above sysvinit, so its re-exec on shutdown runs init.container.
ALTERNATIVE_PRIORITY[init] = "100"

INITSCRIPT_NAME = "container-stop.sh"
# After umountfs (S40), before halt and reboot (S90).
INITSCRIPT_PARAMS = "start 89 0 6 ."

do_install() {
    install -d ${D}${base_sbindir} ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/init.container ${D}${base_sbindir}/init.container
    install -m 0755 ${UNPACKDIR}/container-stop.sh ${D}${sysconfdir}/init.d/

    # marks the system as a container
    install -d ${D}${sysconfdir}/venus
    touch ${D}${sysconfdir}/venus/container
}

RDEPENDS:${PN} = "sysvinit"

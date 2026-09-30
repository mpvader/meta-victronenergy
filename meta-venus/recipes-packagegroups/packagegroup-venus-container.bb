SUMMARY = "Venus OS for container images"
DESCRIPTION = "packagegroup-venus-base, without its hardware packages, plus the runtime support for running as a container."

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "venus-container"

inherit packagegroup

# what venus-image.inc and packagegroup-venus-machine add on a GX
RDEPENDS:${PN} += "\
    dbus-shelly \
    machine-runtime-conf \
    netmon \
    packagegroup-core-boot \
    packagegroup-ve-console-apps \
    venus-version \
"

RDEPENDS:${PN} += "\
    packagegroup-venus-base \
"

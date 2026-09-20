SUMMARY = "Container GX"
DESCRIPTION = "packagegroup-venus-base, without its hardware packages, plus the runtime support for running as a container."

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "container-gx"

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
    container-gx-sysfs \
    container-gx-volatile \
"

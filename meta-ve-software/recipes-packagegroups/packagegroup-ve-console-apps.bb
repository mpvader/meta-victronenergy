PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

DESCRIPTION = " \
    Victron Energy applications which can also be installed on a headless system. \
    As in not X / wayland depend, since not all targets have a X server \
"

RDEPENDS:${PN} += " \
    dbus-acsystem \
    dbus-generator \
    dbus-pump \
    dbus-recorder \
    dbus-systemcalc-py \
    dbus-tempsensor-relay \
    dbus-vebus-to-pvinverter \
    javascript-vnc-client \
    localsettings \
"

# only for gui-v1, which a container doesn't have
RDEPENDS:${PN}:remove:venus-container = "javascript-vnc-client"

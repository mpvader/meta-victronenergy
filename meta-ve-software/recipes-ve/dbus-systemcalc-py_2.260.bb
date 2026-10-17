DESCRIPTION = "VE system calculations"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9b0a9609befce3122afcc444da0fe825"

inherit daemontools localsettings python-compile ve_package

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\S+)"
SRC_URI = " \
    gitsm://github.com/mpvader/dbus-systemcalc-py.git;branch=mva/container;protocol=https \
    file://com.victronenergy.system.conf \
    file://localsettings \
"
SRCREV = "bff5eb60cab7073164e00c6c2adaae252ec87ae1"
S = "${WORKDIR}/git"

RDEPENDS:${PN} = " \
    localsettings \
    python3-core \
    python3-dbus \
    python3-pprint \
    python3-pygobject \
"

DAEMONTOOLS_RUN = "${@softlimit(d, data=100000000, stack=1000000, all=100000000)} ${bindir}/dbus_systemcalc.py"

do_install () {
    install -d ${D}${bindir}
    cp -r ${S}/* ${D}${bindir}
    rm -rf ${D}${bindir}/tests

    install -d ${D}/${sysconfdir}/dbus-1/system.d
    install -m 644 ${UNPACKDIR}/com.victronenergy.system.conf ${D}/${sysconfdir}/dbus-1/system.d
}

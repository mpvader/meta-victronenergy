FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://set-base-compatible.patch;patchdir=${UNPACKDIR} \
    file://do-not-mount-over-dev.patch;patchdir=${UNPACKDIR} \
"

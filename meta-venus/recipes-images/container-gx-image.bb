SUMMARY = "Container GX image"
DESCRIPTION = "Venus OS image for OCI-compatible container runtimes."

COMPATIBLE_MACHINE = "container-gx"

require venus-image.inc

# The machine names already say container-gx: name the files after the
# machine alone.
IMAGE_BASENAME = "${MACHINE}"
IMAGE_NAME = "${MACHINE}-${DATETIME}-${DISTRO_VERSION}"
IMAGE_LINK_NAME = "${MACHINE}"
VENUS_IMAGE_TYPE = "container"

IMAGE_INSTALL = "packagegroup-container-gx"
IMAGE_FEATURES:remove = "read-only-rootfs"

IMAGE_FSTYPES = "container oci"

# image-container.bbclass fails the parse on am62xx-k3r5, which sets no
# kernel; the container machines set linux-dummy.
IMAGE_CONTAINER_NO_DUMMY = "1"

# sloci-image, unlike the default umoci, can set the arch variant (v7). Set
# before the inherit, which picks the backend.
OCI_IMAGE_BACKEND = "sloci-image"
inherit image-oci

# sloci-image translates the arch itself: image-oci's "arm64" would come out
# as "arm".
OCI_IMAGE_ARCH = "${TARGET_ARCH}"

# image-oci looks for "armv7" in TUNE_FEATURES, which tune-cortexa8
# doesn't set.
OCI_IMAGE_SUBARCH:container-gx-armv7 = "v7"

# ~ isn't a legal OCI/Docker tag character; betas before v3.90 have it (v3.83~1).
OCI_IMAGE_TAG = "${@d.getVar('DISTRO_VERSION').replace('~', '-')}"
# the name docker load and podman load give the image, see sloci-image patch 0002;
# Docker's containerd image store only finds a fully qualified name
OCI_IMAGE_REF_NAME = "docker.io/library/container-gx:${OCI_IMAGE_TAG}"
export OCI_IMAGE_REF_NAME

OCI_IMAGE_ENTRYPOINT = "/sbin/init"
# sysvinit ignores SIGTERM and SIGRTMIN+3, the Docker and Podman defaults,
# and SIGINT reboots.
OCI_IMAGE_STOPSIGNAL = "SIGPWR"
export OCI_IMAGE_STOPSIGNAL
OCI_IMAGE_PORTS = "80/tcp 443/tcp 1883/tcp 8883/tcp 9001/tcp"

# When the container can't mount, overlays.sh can't mount its copy of the
# services on /service, so point /service at that copy instead.
ROOTFS_POSTPROCESS_COMMAND += "container_gx_link_service_dir"
container_gx_link_service_dir() {
    rm -rf ${IMAGE_ROOTFS}/service
    ln -s /run/overlays/service ${IMAGE_ROOTFS}/service
}

# for check-updates.sh in a container, which reads it from the feed
IMAGE_POSTPROCESS_COMMAND += "container_gx_deploy_version"
container_gx_deploy_version() {
    install -m 0644 ${IMAGE_ROOTFS}/opt/victronenergy/version ${IMGDEPLOYDIR}/container-gx-version
}

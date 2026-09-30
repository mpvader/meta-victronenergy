SUMMARY = "Containerized Venus image"
DESCRIPTION = "Venus OS image for OCI-compatible container runtimes."

COMPATIBLE_MACHINE = "venus-container"

require venus-image.inc

IMAGE_BASENAME = "venus-container"
IMAGE_INSTALL = "packagegroup-venus-container"
IMAGE_FEATURES:remove = "read-only-rootfs"

IMAGE_FSTYPES = "container oci"

# image-container.bbclass checks PREFERRED_PROVIDER_virtual/kernel for every MACHINE at parse
# time; am62xx-k3r5 (the R5 multiconfig) never sets it, so it crashes instead of warning. We do
# set it (linux-dummy).
IMAGE_CONTAINER_NO_DUMMY = "1"

# umoci (the default OCI_IMAGE_BACKEND) can't set the arch variant, so armv7 would lose its
# /v7 platform tag - sloci-image does via --arch-variant. Set before the inherit, which picks
# the backend.
OCI_IMAGE_BACKEND = "sloci-image"
inherit image-oci

# image-oci.bbclass's own OCI_IMAGE_ARCH default is already Go/OCI-translated (e.g. "arm64"),
# which is correct for umoci (direct pass-through) but wrong for sloci-image: its own oci_arch()
# does the raw->OCI translation itself and expects raw input (aarch64/x86_64/arm*) - "arm64"
# collides with its arm* catch-all and silently comes out as plain "arm". Feed it raw instead.
OCI_IMAGE_ARCH = "${TARGET_ARCH}"

# image-oci.bbclass's OCI_IMAGE_SUBARCH auto-detection checks TUNE_FEATURES for the literal
# substring "armv7", but armv7's tune (tune-cortexa8.inc, an armv7a-profile tune) only sets
# TUNE_FEATURES to "arm vfp cortexa8 neon callconvention-hard" - no such substring exists, so
# the heuristic silently resolves to empty and the OCI image loses its /v7 platform tag. Set it
# explicitly since we already know which machine needs it.
OCI_IMAGE_SUBARCH:container-armv7 = "v7"

# ~ isn't a legal OCI/Docker tag character; DISTRO_VERSION uses it for beta builds (v3.80~46).
OCI_IMAGE_TAG = "${@d.getVar('DISTRO_VERSION').replace('~', '-')}"
# the name docker load and podman load give the image, see sloci-image patch 0002;
# Docker's containerd image store only finds a fully qualified name
OCI_IMAGE_REF_NAME = "docker.io/library/container-gx:${OCI_IMAGE_TAG}"
export OCI_IMAGE_REF_NAME

OCI_IMAGE_ENTRYPOINT = "/sbin/init"
OCI_IMAGE_PORTS = "80/tcp 443/tcp 1883/tcp 8883/tcp 9001/tcp"

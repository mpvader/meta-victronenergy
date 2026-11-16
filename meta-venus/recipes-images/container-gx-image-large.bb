SUMMARY = "Large Container GX image"
DESCRIPTION = "Container GX image plus what venus-image-large adds, such as Node-RED and Signal K."

require container-gx-image.bb

IMAGE_INSTALL += "packagegroup-venus-large"
# Node-RED (https) and Signal K
OCI_IMAGE_PORTS += "1881/tcp 3000/tcp"
VENUS_IMAGE_TYPE = "large"

# container-gx-large-arm64 and the like
IMAGE_BASENAME = "${@d.getVar('MACHINE').replace('container-gx-', 'container-gx-large-')}"
OCI_IMAGE_REF_NAME = "docker.io/library/container-gx-large:${OCI_IMAGE_TAG}"

# both images have the same version; container-gx-image deploys its file
IMAGE_POSTPROCESS_COMMAND:remove = "container_gx_deploy_version"

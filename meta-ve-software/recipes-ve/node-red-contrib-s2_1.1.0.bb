SUMMARY = "Node-RED nodes for the S2 energy management protocol"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=876b44dded698736d08769eeb7c62258"

# The npm registry tarball contains the compiled dist/, the GitHub tag tarball does not.
SRC_URI = " \
    npm://registry.npmjs.org;package=${BPN};version=${PV} \
    file://npm-shrinkwrap.json;subdir=${S} \
"

SRC_URI[sha256sum] = "b063b3e1bd60cc6b1041c9777a471a156b6608450dc8615f819b55b8732d9282"

S = "${UNPACKDIR}/npm"

inherit npm-online-install

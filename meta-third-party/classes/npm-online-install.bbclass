inherit npm

# The default npm class is so slow, that it is pratically unusable.
# Just run pack + install instead, this cannot be done offline!!!
# Well plus a bit more foo, since a tarball install doesn't respect
# the npm-shrinkwrap.json, see https://github.com/npm/cli/issues/5349

# Documentation:
# https://github.com/victronenergy/venus/wiki/npm-and-nodejs-packages

do_configure() {
    :
}

# a global install (which is no longer done), will place files under the
# "name" json entry. Store its value in a seperate file so do_compile can
# use it.
python do_getname() {
    import json

    s = d.getVar("S")
    w = d.getVar("WORKDIR")
    pn = d.getVar("PN")
    with open(os.path.join(s, "package.json"), "r") as f:
        package = json.load(f)
    with open(os.path.join(w, "nodename"), "w") as f:
        f.write(package["name"] if "name" in package else pn)
}

addtask getname after do_unpack before do_compile

# No optionalDependencies get installed. List here those of the package
# itself that it works without; those of its dependencies aren't checked.
NPM_ALLOWED_OPTIONAL ?= ""

# Fail on what npm clean-install doesn't catch: a shrinkwrap of another
# version or made in a workspace, and optionalDependencies that
# --omit=optional leaves out although they are needed.
python do_npm_check() {
    import json

    s = d.getVar("S")
    pn = d.getVar("PN")
    pv = d.getVar("PV")
    update = f"run 'bitbake -c update_shrinkwrap {pn}'"
    with open(os.path.join(s, "package.json"), "r") as f:
        package = json.load(f)
    if not pv == package.get("version"):
        bb.fatal(f"PV {pv} doesn't match the package.json its version {package.get('version')}")

    shrinkwrap = os.path.join(s, "npm-shrinkwrap.json")
    if not os.path.isfile(shrinkwrap):
        bb.fatal(f"No npm-shrinkwrap.json found for {pn}, add file://npm-shrinkwrap.json;subdir=${{S}} to SRC_URI")
    with open(shrinkwrap, "r") as f:
        lock = json.load(f)
    version = lock.get("lockfileVersion", 1)
    if version < 2:
        bb.fatal(f"lockfileVersion {version} of npm-shrinkwrap.json is too old, {update}")

    root = lock["packages"][""]
    if not pv == root.get("version"):
        bb.fatal(f"PV {pv} doesn't match the shrinkwrap version {root.get('version')}, {update}")

    optional = package.get("optionalDependencies") or {}
    for key in ["dependencies", "devDependencies", "optionalDependencies", "peerDependencies"]:
        specs = package.get(key) or {}
        if key == "dependencies":
            # In the shrinkwrap, npm lists a dependency that is also optional
            # under optionalDependencies only.
            specs = {name: spec for name, spec in specs.items() if name not in optional}
        # npm leaves empty ones out of the shrinkwrap
        if not specs == root.get(key, {}):
            bb.fatal(f"{key} differs between npm-shrinkwrap.json and package.json, {update}")

    links = [entry["resolved"] for entry in lock["packages"].values() if entry.get("link")]
    if links:
        bb.fatal(f"npm-shrinkwrap.json links to {links[0]}; it was made in a workspace, {update}")

    allowed = d.getVar("NPM_ALLOWED_OPTIONAL").split()
    skipped = [name for name in optional if name not in allowed]
    if skipped:
        bb.fatal(f"optionalDependencies {' '.join(skipped)} won't be installed, "
                 "move them to dependencies with a patch or list them in NPM_ALLOWED_OPTIONAL")
}

addtask npm_check after do_patch before do_configure

B = "${WORKDIR}/npm-pack"
NPM_TMP = "${WORKDIR}/npm-tmp"

# Fail on a package or dependency that doesn't support this nodejs (engines
# field). Set to "false" for a dependency with a wrong engines field.
NPM_ENGINE_STRICT ?= "true"

DEBUG_PREFIX_MAP += " \
    -fmacro-prefix-map=${NPM_TMP}=${TARGET_DBGSRC_DIR} \
    -fdebug-prefix-map=${NPM_TMP}=${TARGET_DBGSRC_DIR} \
"

NPM_SHRINKMAP_SRC = "${@bb.utils.which(d.getVar('FILESPATH'), 'npm-shrinkwrap.json')}"

do_compile() {

    if [ "$1" = "update_shrinkwrap" ]; then
        if [ -f ${S}/npm-shrinkwrap.json ]; then
            rm ${S}/npm-shrinkwrap.json
        fi
        npm_cmd="install"
    else
        npm_cmd="clean-install"
    fi

    # remove the workdir of quilt, otherwise the patches themselves will be installed as well.
    rm -rf ${S}/.pc

    # Don't run lifecycle scripts like prepack: without the devDependencies
    # they can't build anything, and their output would end up in $tar.
    tar=$(npm pack --ignore-scripts ${S})

    # This used to install the tarball directly in the right place, but there is a bug
    # in more recent versions, ignoring the npm-shrinkwrap.json, see
    # https://github.com/npm/cli/issues/5349. So instead work around it, by extracting
    # the tarball to a temporary location, run a npm install on the dir and thereafter
    # move the files to where OE expects them (not that is in lib; they will be moved
    # again to populate D, to end up in /usr/lib eventually)
    tar -xzf $tar -C ${WORKDIR}/npm-extracted --strip-components 1 -C "${NPM_TMP}"

    if [ "$1" != "update_shrinkwrap" ]; then
        # make sure npm-shrinkwrap is there for a normal install. Not sure how this works,
        # it is in the tar for node-red, but not node-red-venus-contrib.
        # Maybe https://github.com/npm/cli/issues/6803 ?
        cp ${S}/npm-shrinkwrap.json "${NPM_TMP}"
    fi

    cd "${NPM_TMP}" && npm $npm_cmd \
        --arch=${NPM_ARCH} \
        --target_arch=${NPM_ARCH} \
        --engine-strict=${NPM_ENGINE_STRICT} \
        --omit=dev \
        --omit=optional

    # mimic a global install
    NAME="$(cat ${WORKDIR}/nodename)"
    install -d ${NPM_BUILD}/lib/node_modules/$NAME
    cp --no-preserve=ownership --recursive ${NPM_TMP}/. ${NPM_BUILD}/lib/node_modules/$NAME
}
do_compile[network] = "1"
do_compile[cleandirs] += "${NPM_BUILD} ${NPM_TMP}"

python do_binlinks() {
    import json

    s = d.getVar("S")
    b = d.getVar("NPM_BUILD")
    pn = d.getVar("PN")
    with open(os.path.join(s, "package.json"), "r") as f:
        package = json.load(f)
    if "bin" in package:
        bb.utils.mkdirhier(os.path.join(b, "bin"))
        for tg, src in package["bin"].items():
            bin = os.path.abspath(os.path.join(b, "lib", "node_modules", pn, src))
            link = os.path.join(b, "bin", tg)
            rel = os.path.relpath(bin, os.path.dirname(link))
            oe.path.symlink(rel, link, True)
}
addtask binlinks after do_compile before do_install

NPM_SHRINKWRAP_SRC = "${@bb.utils.which(d.getVar('FILESPATH'), 'npm-shrinkwrap.json')}"

do_update_shrinkwrap() {
    if [ "${NPM_SHRINKWRAP_SRC}" = "" ]; then
         bbfatal "no shrinkmap source file found"
    fi

    do_compile update_shrinkwrap
    npm shrinkwrap

    if ! diff -q ${NPM_TMP}/npm-shrinkwrap.json ${NPM_SHRINKWRAP_SRC}; then
        bbwarn updating ${NPM_SHRINKWRAP_SRC}
        cp ${NPM_TMP}/npm-shrinkwrap.json ${NPM_SHRINKWRAP_SRC}
    else
        bbwarn "shrinkwrap not changed ${S}"
        cp ${NPM_SHRINKWRAP_SRC} ${S}/npm-shrinkwrap.json
    fi
}
addtask update_shrinkwrap after do_getname do_patch do_prepare_recipe_sysroot
do_update_shrinkwrap[cleandirs] += "${NPM_BUILD} ${NPM_TMP}"
do_update_shrinkwrap[dirs] = "${NPM_TMP}"
do_update_shrinkwrap[network] = "1"
do_update_shrinkwrap[nostamp] = "1"

INSANE_SKIP:${PN} += "already-stripped"

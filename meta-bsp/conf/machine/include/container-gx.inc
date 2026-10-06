# Common to the Container GX machines. The host provides the kernel.
MACHINEOVERRIDES =. "container-gx:"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-dummy"

# no virtual terminal in a container, so no getty on tty1
USE_VT = "0"

START_GUI = ""

# The SDK can have the architecture of a container machine; keep the
# toolchain names of the image and the SDK apart. Native SDK recipes ignore
# machine overrides, so append.
SDK_VENDOR:append = "sdk"

# shut down on SIGPWR, container-gx-image's stop signal
INITTAB = "pf::powerfail:/sbin/shutdown -h now"

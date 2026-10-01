# Generic container machine, no real kernel/bootloader - the container runtime supplies it.
MACHINEOVERRIDES =. "venus-container:"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-dummy"

# no virtual terminal in a container, so no getty on tty1
USE_VT = "0"

START_GUI = ""

# The SDK can have the architecture of a container machine; keep the
# toolchain names of the image and the SDK apart. Native SDK recipes ignore
# machine overrides, so append.
SDK_VENDOR:append = "sdk"

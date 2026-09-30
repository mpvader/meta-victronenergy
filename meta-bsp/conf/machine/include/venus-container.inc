# Generic container machine, no real kernel/bootloader - the container runtime supplies it.
MACHINEOVERRIDES =. "venus-container:"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-dummy"

# no virtual terminal in a container, so no getty on tty1
USE_VT = "0"

START_GUI = ""

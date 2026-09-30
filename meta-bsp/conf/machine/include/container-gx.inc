# Common to the Container GX machines. The host provides the kernel.
MACHINEOVERRIDES =. "container-gx:"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-dummy"

# no virtual terminal in a container, so no getty on tty1
USE_VT = "0"


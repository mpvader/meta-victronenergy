#!/bin/sh

### BEGIN INIT INFO
# Provides:          container-sysfs
# Required-Start:    mountall
# Required-Stop:
# Default-Start:     S
# Default-Stop:
# Short-Description: Remount /sys read-write before udev
### END INIT INFO

# Unless the container is privileged, the runtime mounts sysfs read-only.
# Venus OS udev rules write to it to create I2C clients and export GPIOs, for
# example for the GX IO-Extender 150. With --cap-add=SYS_ADMIN, though not in
# rootless Podman, remount it read-write in this container's mount namespace
# before udev starts. The sysfs objects still belong to the host kernel.
mount -o remount,rw /sys 2>/dev/null ||
    echo "WARNING: /sys is read-only, so a GX IO-Extender 150 will not work; it needs --cap-add=SYS_ADMIN, and not rootless Podman" >&2

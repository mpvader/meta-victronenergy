#!/bin/sh

### BEGIN INIT INFO
# Provides:          container-coldplug
# Required-Start:    udev
# Required-Stop:
# Default-Start:     S
# Default-Stop:
# Short-Description: Replay serial devices without writing to /sys
### END INIT INFO

# udevadm trigger needs a writable /sys to replay the serial devices passed
# into the container. udevadm test doesn't, but skips the RUN keys: hand
# serial-starter the ttys that serial-starter.rules links.
for dev in /dev/tty*; do
    sys=/sys/class/tty/${dev#/dev/}
    [ -e "$sys/device" ] || continue

    udevadm test --action=add "$sys" >/dev/null 2>&1
    udevadm info -q symlink -p "$sys" | grep -q serial-starter/ &&
        /opt/victronenergy/serial-starter/start-tty.sh "$dev"
done

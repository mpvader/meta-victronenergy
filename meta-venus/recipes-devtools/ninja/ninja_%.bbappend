FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Compare -l with the number of runnable processes, as make 4.3 and
# newer do, instead of with the 1-minute load average.
SRC_URI += "file://0001-On-Linux-try-to-use-proc-loadavg-to-get-instaneous-l.patch"

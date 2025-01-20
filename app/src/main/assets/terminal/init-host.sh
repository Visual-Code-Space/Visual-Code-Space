prootArgs="-r $ALPINE -0 -b /dev/ -b /sys/ -b /proc/ -b /sdcard -b $PREFIX -w / --kill-on-exit --link2symlink"

exec $PROOT $prootArgs /bin/sh $PREFIX/bin/init "$@"

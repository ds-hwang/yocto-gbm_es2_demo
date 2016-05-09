# Yocto GBM ES2 Demo
Yocto recipes for embedded OpenGL ES2 demo based on DRM/KMS

# Introduction

* It's yocto recipes of [GBM ES2 Demo](https://github.com/ds-hwang/gbm_es2_demo) project.
* It shows how to package minimal OpenGL ES2 application with Yocto embedded linux.

# Checkout Yocto
* Install tools

```
  $ sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib build-essential chrpath libsdl1.2-dev xterm gyp
```

* Checkout Yocto

```
  $ git clone http://git.yoctoproject.org/git/poky
```

# Build
* I usually work on master branch.
* jump to its build environment:

```
  $ cd poky/
  $ source oe-init-build-env

```

* You had no conf/local.conf file so a configuration file has therefore been created for you with some default values, but we need still to add the `yocto-gbm_es2_demo` layers in `conf/bblayers.conf`:

  ```
BBLAYERS ?= " \
  /d/workspace/yocto/poky/meta \
  /d/workspace/yocto/poky/meta-poky \
  /d/workspace/yocto/poky/meta-yocto-bsp \
  /d/workspace/yocto/yocto-gbm_es2_demo \
  "
  ```

* then, set in conf/local.conf the genericx86-64 machine (you could try a different architecture but we haven't yet):

```
MACHINE ?= "genericx86-64"
```

* then, set chromium configuration:

```
IMAGE_INSTALL_append = " gbm-es2-demo"
```

* Now close the file and let's build whole yocto image including `gbm-es2-demo`. Use `core-image-minimal` which doesn't include X11 and window manager.
```
  $ bitbake core-image-minimal
```

* You can build only `gbm-es2-demo` package.
```
  $ bitbake gbm-es2-demo
```


* If you want to compile only, you can do it. I use it when I don't want to bake image. I usually compile executables and run it via `sshfs` on the device.
```
  $ bitbake -c compile gbm-es2-demo
```


* It will take tens minuites to download much of the dependencies, build and etc. Relax now. If everything goes fine, you will have the following directory with the images built in inside:
```
  $ ls tmp/deploy/images/genericx86-64/*.hddimg
  $ tmp/deploy/images/genericx86-64/core-image-sato-genericx86-64-20150307113028.hddimg
  $ tmp/deploy/images/genericx86-64/core-image-sato-genericx86-64.hddimg
```

# USB flash
* Make sure you have now inserted a USB flash drive, **checking the correct file descriptor** that Linux will be using with the `sudo fdisk -l` command. For example in our system it is ```/dev/sdc```, so the following is what we used to flash it:
```
  $ cd tmp/deploy/images/genericx86-64/
  $ sudo dd if=core-image-minimal-genericx86-64.hddimg of=/dev/sdc
  $ sync 
  $ sudo eject /dev/sdc
```

# Run
* You are able now to boot the flash drive in your hardware.
```
login: root
# gbm_es2_demo
```

# Tips
## ICECC
add following lines in local.conf
```
PARALLEL_MAKE = "-j 80"
ICECC_PATH = "/home/dshwang/thirdparty/icecream/install/bin/icecc"
INHERIT += "icecc"
```

Before `bitbake` you must exclude icecc toolchain wrapper path(e.g. `/usr/lib/icecc/bin`) from $PATH
* Reference
 * [icecc.bbclass](http://git.yoctoproject.org/cgit.cgi/poky/plain/meta/classes/icecc.bbclass)
 * [Using IceCC in OpenEmbedded](http://www.openembedded.org/wiki/Using_IceCC)


### Icecc trouble shooting
* icecc you builds by yourself gets along with yocto. don't worry.

* exception 23
 * this means that your machine makes wrong toolchain.
```
ICECC[24079] 13:52:14: compiler did not start - compiled on 10.237.72.78
ICECC[24079] 13:52:14: got exception 23 (10.237.72.78) 
```

 * A1: purge annoying hardening-wrapper (which wastes my 2 days) `sudo apt-get purge hardening-wrapper hardening-includes`
 * A2: purge clang and all gcc and then reinstall only minimal gcc

## Add more tools in minimal images
* you might need "bash", "ssh", "sshfs" for more convinient embedded development.
 * checkout [meta-openembedded](git://git.openembedded.org/meta-openembedded) and then add following lines to `bblayers.conf`
```
BBLAYERS ?= " \
   ...
  /d/workspace/yocto/meta-openembedded/meta-oe \
  /d/workspace/yocto/meta-openembedded/meta-filesystems \
  "
```

 * add packages you want ot `local.conf`
```
-EXTRA_IMAGE_FEATURES = "debug-tweaks"
+EXTRA_IMAGE_FEATURES = "debug-tweaks ssh-server-dropbear"

-IMAGE_INSTALL_append = " gbm-es2-demo"
+IMAGE_INSTALL_append = " gbm-es2-demo sshfs-fuse bash"

```

## How to use sshfs
* install sshfs on your machine, then add yourself to the fuse group:
 * Refer [Ubuntu SSHFS](https://help.ubuntu.com/community/SSHFS)
```
> sudo apt-get install sshfs
> sudo gpasswd -a $USER fuse
```

* ssh to device
```
> ssh root@$<YOCTOURL>
$
```

* (optional) you can use bash. Do you remember we added bash package on the image :)
```
$ bash
```

* ssh from device to your machine because it loads some kernel module, which means sshfs has a bug not-loading it.
```
$ ssh <ID>@<HOST>
> [crtl + d]
```

* sshfs mounts host chromium directory on device
```
$ mkdir remote
$ sshfs -o idmap=user <ID>@<HOST>:<chromium path> /home/root/remote/
```

* Enjoy hack

* unmount if needed
```
fusermount -u /home/root/remote
```

## my conf
* Refer to my [local.conf](reference_conf/local.conf) and [bblayers.conf](reference_conf/bblayers.conf)

# Reference
* [Yocto Chromium](https://github.com/ds-hwang/yocto-chromium)
* [Linux : How to run a command when boots up?](http://www.cyberciti.biz/tips/linux-how-to-run-a-command-when-boots-up.html)
* [Yocto Cookbook:Appliance:Startup Scripts](https://wiki.yoctoproject.org/wiki/Cookbook:Appliance:Startup_Scripts)
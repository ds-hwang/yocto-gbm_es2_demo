AUTHOR = "Dongseong Hwang <dongseong.hwang@intel.com>"
DESCRIPTION = "Example OpenGL ES2 demo using GBM and DRM(KMS) modesetting."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6399aca264cba2dc4c3a68d5904ffd33"

SRC_URI = "\
    git://github.com/ds-hwang/gbm_es2_demo.git;protocol=git \
    file://gbm_es2_demo.service \
    file://init \
"

SRCREV="beeca04ecd9e7004fecc9f07c9de0a34c0b01405"
SRC_URI[md5sum] = "d44e4db53258f3fd811050865d34e3a9"

S="${WORKDIR}/git"

# run gbm_es2_demo after boot
inherit update-rc.d systemd

INITSCRIPT_NAME = "gbm_es2_demo-service"
INITSCRIPT_PARAMS = "start 06 5 2 3 . stop 22 0 1 6 ."

SYSTEMD_SERVICE_${PN} = "gbm_es2_demo.service"

DEPENDS = "virtual/egl libdrm"

do_configure() {
	cmake .
}

do_compile() {
    make
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/run/gbm_es2_demo ${D}${bindir}/gbm_es2_demo
    install -m 0755 ${S}/run/dma_buf_mmap_demo ${D}${bindir}/dma_buf_mmap_demo

    # run gbm_es2_demo after boot
    if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/gbm_es2_demo-service
    else
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/gbm_es2_demo.service ${D}${systemd_unitdir}/system
    fi
}

AUTHOR = "Dongseong Hwang <dongseong.hwang@intel.com>"
DESCRIPTION = "Example OpenGL ES2 demo using GBM and DRM(KMS) modesetting."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6399aca264cba2dc4c3a68d5904ffd33"

SRC_URI = "git://github.com/ds-hwang/gbm_es2_demo.git;protocol=git"
SRCREV="beeca04ecd9e7004fecc9f07c9de0a34c0b01405"
SRC_URI[md5sum] = "d44e4db53258f3fd811050865d34e3a9"

S="${WORKDIR}/git"

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
}

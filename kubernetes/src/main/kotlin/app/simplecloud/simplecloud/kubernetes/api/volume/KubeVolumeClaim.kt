package app.simplecloud.simplecloud.kubernetes.api.volume

import app.simplecloud.simplecloud.api.utils.Nameable

interface KubeVolumeClaim : Nameable {

    fun delete()

    interface Factory {

        fun create(
            name: String,
            volumeSpec: KubeVolumeSpec
        ): KubeVolumeClaim

    }

}